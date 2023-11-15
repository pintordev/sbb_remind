package com.pintor.sbb_remind.member;

import com.pintor.sbb_remind.answer.Answer;
import com.pintor.sbb_remind.answer.AnswerService;
import com.pintor.sbb_remind.mail.MailService;
import com.pintor.sbb_remind.question.Question;
import com.pintor.sbb_remind.question.QuestionService;
import com.pintor.sbb_remind.util.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    private final MailService mailService;
    private final QuestionService questionService;
    private final AnswerService answerService;

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

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public String profile(Model model, Principal principal,
                          @RequestParam(value = "qPage", defaultValue = "1") int qPage,
                          @RequestParam(value = "aPage", defaultValue = "1") int aPage) {

        Member member = this.memberService.getByLoginId(principal.getName());
        model.addAttribute("member", member);

        Page<Question> questionPaging = this.questionService.getListByMember(member, qPage);
        model.addAttribute("questionPaging", questionPaging);

        Page<Answer> answerPaging = this.answerService.getListByMember(member, aPage);
        model.addAttribute("answerPaging", answerPaging);

        model.addAttribute("qPage", qPage);
        model.addAttribute("aPage", aPage);

        return "member/profile";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String modify(MemberPasswordForm memberPasswordForm) {
        return "member/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(@Valid MemberPasswordForm memberPasswordForm, BindingResult bindingResult,
                         Principal principal) {

        log.info("member password modify request");
        log.info("presentPassword: " + memberPasswordForm.getPresentPassword());
        log.info("newPassword: " + memberPasswordForm.getNewPassword());
        log.info("newPasswordConfirm: " + memberPasswordForm.getNewPasswordConfirm());

        // Form Validation Check
        if (bindingResult.hasErrors()) {
            return "member/modify";
        }

        Member member = this.memberService.getByLoginId(principal.getName());

        // Extra Validation Check
        if (!this.memberService.isPasswordMatched(member.getPassword(), memberPasswordForm.getPresentPassword())) {
            bindingResult.rejectValue("presentPassword", "notMatched", "현재 비밀번호가 일치하지 않습니다");
        }

        if (!memberPasswordForm.getNewPassword().equals(memberPasswordForm.getNewPasswordConfirm())) {
            bindingResult.rejectValue("newPasswordConfirm", "PasswordNotMatch", "입력한 새 비밀번호가 일치하지 않습니다");
        }

        if (bindingResult.hasErrors()) {
            return "member/modify";
        }

        member = this.memberService.modifyPassword(member, memberPasswordForm.getNewPassword());

        log.info("member password has modified");

        return "redirect:/member/logout";
    }

    @GetMapping("/find")
    public String find() {

        return "member/find";
    }

    @GetMapping("/find/loginId")
    @ResponseBody
    public ResponseEntity findLoginId(@RequestParam(value = "email") String email) {

        Member member = this.memberService.getByEmail(email);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RsData<>("F-1", "입력한 이메일로 가입한 로그인 아이디가 없습니다", ""));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("S-1", "입력한 이메일로 가입한 로그인 아이디를 반환합니다", member.getLoginId()));
    }

    @PostMapping("/find/password")
    @ResponseBody
    public ResponseEntity resetPassword(@RequestParam(value = "loginId") String loginId,
                                        @RequestParam(value = "email") String email) {

        Member member = this.memberService.getByLoginIdAndEmail(loginId, email);

        if (member == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RsData<>("F-1", "입력한 값으로 가입한 계정을 찾을 수 없습니다", ""));
        }

        // Member Create
        String[] codeBits = this.memberService.genSecurityCode(member.getEmail(), 16);

        member = this.memberService.resetPassword(member, codeBits[0]);

        // Authentication Mail Send
        this.mailService.send(member.getEmail(), codeBits[0], "임시 비밀번호");

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("S-1", "입력한 이메일로 임시 비밀번호를 발송했습니다",""));
    }

    @GetMapping("/authenticate/{emailAuthenticationCode}")
    public String authenticateEmail(@PathVariable("emailAuthenticationCode") String emailAuthenticationCode) {

        Member member = this.memberService.getByEmailAuthenticationCode(emailAuthenticationCode);

        if (member != null) this.memberService.authenticateEmail(member);

        return "redirect:/";
    }
}
