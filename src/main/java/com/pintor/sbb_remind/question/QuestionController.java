package com.pintor.sbb_remind.question;

import com.pintor.sbb_remind.answer.Answer;
import com.pintor.sbb_remind.answer.AnswerService;
import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberService;
import com.pintor.sbb_remind.util.RsData;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

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
    public String create(Model model, QuestionForm questionForm) {

        List<String> categoryList = QuestionCategory.getList();
        model.addAttribute("categoryList", categoryList);

        return "question/create";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(Model model,
                         @Valid QuestionForm questionForm, BindingResult bindingResult,
                         Principal principal) {

        log.info("question create request");
        log.info("category: " + questionForm.getCategory());
        log.info("title: " + questionForm.getTitle());
        log.info("content: " + questionForm.getContent());

        if (bindingResult.hasErrors()) {
            List<String> categoryList = QuestionCategory.getList();
            model.addAttribute("categoryList", categoryList);

            return "question/create";
        }

        Member author = this.memberService.getByLoginId(principal.getName());
        Question question = this.questionService.create(questionForm.getCategory(),
                questionForm.getTitle(),
                questionForm.getContent(),
                author);

        log.info("question has created");

        return String.format("redirect:/question/" + question.getId());
    }

    @GetMapping("")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw,
                       @RequestParam(value = "sort", defaultValue = "new") String sort) {

        Page<Question> questionPaging = this.questionService.getList(kw, sort, page);

        // 잘못된 페이지 접근 방어
        if (questionPaging.isEmpty() && page != 1) {
            log.info("required page " + page + " does not exist");
            return "redirect:/question";
        }

        model.addAttribute("questionPaging", questionPaging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sort);

        return "question/list";
    }

    @GetMapping("/{id}")
    public String detail(Model model, @PathVariable("id") Long id,
                         @RequestParam(value = "aPage", defaultValue = "1") int aPage,
                         @RequestParam(value = "sort", defaultValue = "old") String sort,
                         HttpServletRequest request, HttpServletResponse response) {

        Question question = this.questionService.getById(id);

        // 조회수 증가 여부 판단
        if (this.questionService.hitJudge(question, request, response)) {
            question = this.questionService.hit(question);
        }
        model.addAttribute("question", question);

        log.info("aPage: " + aPage);

        // 잘못된 페이지 접근 방어
        if (aPage < 1) {
            log.info("required page " + aPage + " has invalid value");
            return String.format("redirect:/question/" + id + "#answer_start");
        }

        Page<Answer> answerPaging = this.answerService.getList(question, sort, aPage);

        log.info("question id: " + question.getId());
        log.info("total elements: " + answerPaging.getTotalElements());

        // 잘못된 페이지 접근 방어
        if (answerPaging.isEmpty() && aPage > 1) {
            log.info("required page " + aPage + " does not exist");
            return String.format("redirect:/question/" + id + "#answer_start");
        }

        model.addAttribute("answerPaging", answerPaging);
        model.addAttribute("sort", sort);

        return "question/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(Model model,
                         QuestionForm questionForm,
                         @PathVariable("id") Long id,
                         Principal principal) {

        Question question = this.questionService.getById(id);

        if (!question.getAuthor().getLoginId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 질문에 대한 수정 권한이 없습니다");
        }

        List<String> categoryList = QuestionCategory.getList();
        model.addAttribute("categoryList", categoryList);

        questionForm.setCategory(question.getCategory());
        questionForm.setTitle(question.getTitle());
        questionForm.setContent(question.getContent());

        return "question/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(Model model,
                         @Valid QuestionForm questionForm, BindingResult bindingResult,
                         @PathVariable("id") Long id,
                         Principal principal) {

        Question question = this.questionService.getById(id);

        if (!question.getAuthor().getLoginId().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 질문에 대한 수정 권한이 없습니다");
        }

        log.info("question modify request");
        log.info("category: " + questionForm.getCategory());
        log.info("title: " + questionForm.getTitle());
        log.info("content: " + questionForm.getContent());

        if (bindingResult.hasErrors()) {
            List<String> categoryList = QuestionCategory.getList();
            model.addAttribute("categoryList", categoryList);

            return "question/modify";
        }

        question = this.questionService.modify(question,
                questionForm.getCategory(),
                questionForm.getTitle(),
                questionForm.getContent());

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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like/{id}")
    @ResponseBody
    public ResponseEntity like(@PathVariable("id") Long id, Principal principal) {

        Question question = this.questionService.getById(id);

        if (question.getAuthor().getLoginId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RsData<>("F-1", "본인이 작성한 질문은 추천할 수 없습니다", ""));
        }

        Member member = this.memberService.getByLoginId(principal.getName());

        question = this.questionService.like(question, member);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("S-1", "질문 추천 변경을 완료했습니다", question.getLiked()));
    }
}
