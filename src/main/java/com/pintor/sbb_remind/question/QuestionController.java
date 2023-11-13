package com.pintor.sbb_remind.question;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/create")
    public String create(QuestionForm questionForm) {

        return "question/create";
    }

    @PostMapping("/create")
    public String create(@Valid QuestionForm questionForm, BindingResult bindingResult) {

        log.info("title: " + questionForm.getTitle());
        log.info("content: " + questionForm.getContent());

        if (bindingResult.hasErrors()) {

            return "question/create";
        }

        Question question = this.questionService.create(questionForm.getTitle(), questionForm.getContent());
        log.info("question has created");

        return "redirect:/question";
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
    public String detail(Model model, @PathVariable("id") Long id) {

        Question question = this.questionService.getById(id);
        model.addAttribute("question", question);

        return "question/detail";
    }
}
