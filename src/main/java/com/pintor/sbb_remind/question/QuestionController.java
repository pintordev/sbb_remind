package com.pintor.sbb_remind.question;

import com.pintor.sbb_remind.answer.Answer;
import com.pintor.sbb_remind.answer.AnswerForm;
import com.pintor.sbb_remind.answer.AnswerService;
import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(QuestionForm questionForm) {

        return "question/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Valid QuestionForm questionForm, BindingResult bindingResult,
                         Principal principal) {

        log.info("question create request");
        log.info("title: " + questionForm.getTitle());
        log.info("content: " + questionForm.getContent());

        if (bindingResult.hasErrors()) {

            return "question/create";
        }

        Member author = this.memberService.getByLoginId(principal.getName());
        Question question = this.questionService.create(questionForm.getTitle(), questionForm.getContent(), author);

        log.info("question has created");

        return String.format("redirect:/question/" + question.getId());
    }

    @GetMapping("")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "1") int page) {

        Page<Question> questionPaging = this.questionService.getList(page);

        // 잘못된 페이지 접근 방어
        if (questionPaging.isEmpty()) {
            log.info("required page " + page + " does not exist");
            return "redirect:/question";
        }

        model.addAttribute("questionPaging", questionPaging);

        return "question/list";
    }

    @GetMapping("/{id}")
    public String detail(Model model, @PathVariable("id") Long id,
                         AnswerForm answerForm,
                         @RequestParam(value = "aPage", defaultValue = "1") int aPage) {

        log.info("aPage: " + aPage);

        // 잘못된 페이지 접근 방어
        if (aPage < 1) {
            log.info("required page " + aPage + " has invalid value");
            return String.format("redirect:/question/" + id + "#answer_start");
        }

        Question question = this.questionService.getById(id);
        model.addAttribute("question", question);

        Page<Answer> answerPaging = this.answerService.getList(question, aPage);

        log.info("question id: " + question.getId());
        log.info("total elements: " + answerPaging.getTotalElements());

        // 잘못된 페이지 접근 방어
        if (answerPaging.isEmpty() && aPage > 1) {
            log.info("required page " + aPage + " does not exist");
            return String.format("redirect:/question/" + id + "#answer_start");
        }

        model.addAttribute("answerPaging", answerPaging);

        return "question/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(QuestionForm questionForm,
                         @PathVariable("id") Long id,
                         Principal principal) {

        Question question = this.questionService.getById(id);

        if (!question.getAuthor().getLoginId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 질문에 대한 수정 권한이 없습니다");
        }

        questionForm.setTitle(question.getTitle());
        questionForm.setContent(question.getContent());

        return "question/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                         @PathVariable("id") Long id,
                         Principal principal) {

        Question question = this.questionService.getById(id);

        if (!question.getAuthor().getLoginId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 질문에 대한 수정 권한이 없습니다");
        }

        log.info("question modify request");
        log.info("title: " + questionForm.getTitle());
        log.info("content: " + questionForm.getContent());

        if (bindingResult.hasErrors()) {

            return "question/modify";
        }

        question = this.questionService.modify(question, questionForm.getTitle(), questionForm.getContent());

        log.info("question has modified");

        return String.format("redirect:/question/" + question.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Principal principal) {

        Question question = this.questionService.getById(id);

        if (!question.getAuthor().getLoginId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 질문에 대한 삭제 권한이 없습니다");
        }

        this.questionService.delete(question);

        return "redirect:/question";
    }
}
