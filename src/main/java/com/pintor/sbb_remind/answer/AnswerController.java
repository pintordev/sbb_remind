package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberService;
import com.pintor.sbb_remind.question.Question;
import com.pintor.sbb_remind.question.QuestionService;
import com.pintor.sbb_remind.util.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {

    private final AnswerService answerService;
    private final QuestionService questionService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity create(@Valid AnswerForm answerForm, BindingResult bindingResult,
                                 Principal principal) {

        log.info("answer create request");
        log.info("content: " + answerForm.getContent());
        log.info("questionId: " + answerForm.getQuestionId());

        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RsData<>("F-1", bindingResult.getFieldError("content").getDefaultMessage(), ""));
        }

        Question question = this.questionService.getById(answerForm.getQuestionId());
        Member author = this.memberService.getByLoginId(principal.getName());

        Answer answer = this.answerService.create(answerForm.getContent(), question, author);

        log.info("answer has created on question #" + question.getId());

        Map<String, Object> answerAttributes = new HashMap<>();

        answerAttributes.put("id", answer.getId());
        answerAttributes.put("content", answer.getContent());
        answerAttributes.put("author", answer.getAuthor().getNickName());
        answerAttributes.put("createDate", answer.getCreateDate().format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm")));

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("S-1", "댓글 등록을 완료했습니다", answerAttributes));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(Model model, AnswerForm answerForm,
                         @PathVariable("id") Long id,
                         Principal principal, HttpServletRequest request) {

        Answer answer = this.answerService.getById(id);

        if (!answer.getAuthor().getLoginId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 답변에 대한 수정 권한이 없습니다");
        }

        String refererUri = request.getHeader("Referer") + "#answer_" + answer.getId();
        log.info("referer: " + refererUri);
        request.getSession().setAttribute("answerModifyRefererUri", refererUri);
        model.addAttribute("cancelRefererUri", refererUri);

        answerForm.setContent(answer.getContent());

        return "answer/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@Valid AnswerForm answerForm, BindingResult bindingResult,
                         @PathVariable("id") Long id,
                         Principal principal, HttpServletRequest request) {

        // refererUri check
        String refererUri = (String) request.getSession().getAttribute("answerModifyRefererUri");
        log.info("referer: " + refererUri);
        request.getSession().removeAttribute("answerModifyRefererUri");

        Answer answer = this.answerService.getById(id);

        if (!answer.getAuthor().getLoginId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 답변에 대한 수정 권한이 없습니다");
        }

        log.info("answer modify request");
        log.info("content: " + answerForm.getContent());

        if (bindingResult.hasErrors()) {

            return "answer/modify";
        }

        answer = this.answerService.modify(answer, answerForm.getContent());

        log.info("answer has modified");

        return String.format("redirect:" + refererUri);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id,
                         Principal principal, HttpServletRequest request) {

        Answer answer = this.answerService.getById(id);

        if (!answer.getAuthor().getLoginId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 답변에 대한 삭제 권한이 없습니다");
        }

        String refererUri = request.getHeader("Referer");
        log.info("referer: " + refererUri);

        this.answerService.delete(answer);

        return String.format("redirect:" + refererUri);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/{id}")
    @ResponseBody
    public ResponseEntity like(@PathVariable("id") Long id, Principal principal) {

        Answer answer = this.answerService.getById(id);

        if (answer.getAuthor().getLoginId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RsData<>("F-1", "본인이 작성한 답변은 추천할 수 없습니다", ""));
        }

        Member member = this.memberService.getByLoginId(principal.getName());

        answer = this.answerService.like(answer, member);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("S-1", "답변 추천 변경을 완료했습니다", answer.getLiked()));
    }
}
