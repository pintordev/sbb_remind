package com.pintor.sbb_remind.question;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

        return "redirect:/index";
    }
}
