package com.pintor.sbb_remind.member;

import com.pintor.sbb_remind.mail.MailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;

    @GetMapping("/signUp")
    public String signUp(MemberSignUpForm memberSignUpForm) {

        return "member/signUp";
    }

    @PostMapping("/signUp")
    public String signUp(@Valid MemberSignUpForm memberSignUpForm, BindingResult bindingResult) {

        log.info("member signUp request");
        log.info("loginId: " + memberSignUpForm.getLoginId());
        log.info("password: " + memberSignUpForm.getPassword());
        log.info("passwordConfirm: " + memberSignUpForm.getPasswordConfirm());
        log.info("nickName: " + memberSignUpForm.getNickName());
        log.info("userName: " + memberSignUpForm.getUserName());
        log.info("email: " + memberSignUpForm.getEmail());

        // Form Validation Check
        if (bindingResult.hasErrors()) {
            return "member/signUp";
        }

        // Extra Validation Check
        if (this.memberService.isDuplicatedLoginId(memberSignUpForm.getLoginId())) {
            bindingResult.rejectValue("loginId", "Duplicated", "입력한 아이디가 이미 존재합니다");
        }

        if (!memberSignUpForm.getPassword().equals(memberSignUpForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "PasswordNotMatch", "입력한 비밀번호가 일치하지 않습니다");
        }

        if (this.memberService.isDuplicatedNickName(memberSignUpForm.getNickName())) {
            bindingResult.rejectValue("nickName", "Duplicated", "입력한 닉네임이 이미 존재합니다");
        }

        if (this.memberService.isDuplicatedEmail(memberSignUpForm.getEmail())) {
            bindingResult.rejectValue("email", "Duplicated", "입력한 이메일이 이미 존재합니다");
        }

        if (bindingResult.hasErrors()) {
            return "member/signUp";
        }

        // Member Create
        String[] codeBits = this.memberService.genSecurityCode(memberSignUpForm.getEmail(), 16);

        Member member = this.memberService.create(codeBits[0],
                memberSignUpForm.getLoginId(),
                memberSignUpForm.getPassword(),
                memberSignUpForm.getNickName(),
                memberSignUpForm.getUserName(),
                memberSignUpForm.getEmail());

        log.info("member has created");

        // Authentication Mail Send
        this.mailService.send(member.getEmail(), codeBits[0], "이메일 인증");

        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request,
                        @RequestParam(value = "error", defaultValue = "") String error,
                        @RequestParam(value = "field", defaultValue = "") String field) {

        String refererUri = request.getHeader("Referer");
        log.info("referer: " + refererUri);

        if (refererUri != null && !refererUri.contains("/member/login")
                && !refererUri.contains("/member/signUp") && !refererUri.contains("/member/find")) {
            request.getSession().setAttribute("refererUri", refererUri);
        }

        Map<String, String> errors = new HashMap<>();
        errors.put(field, error);
        model.addAttribute("errors", errors);

        return "member/login";
    }

    @GetMapping("/authenticate/{emailAuthenticationCode}")
    public String authenticateEmail(@PathVariable("emailAuthenticationCode") String emailAuthenticationCode) {

        Member member = this.memberService.getByEmailAuthenticationCode(emailAuthenticationCode);

        if (member != null) this.memberService.authenticateEmail(member);

        return "redirect:/";
    }
}
