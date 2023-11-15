package com.pintor.sbb_remind.index;

import com.pintor.sbb_remind.answer.Answer;
import com.pintor.sbb_remind.answer.AnswerService;
import com.pintor.sbb_remind.question.Question;
import com.pintor.sbb_remind.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("/")
    public String root() {

        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model) {

        List<Question> recentQuestionList = this.questionService.getRecentQuestion();
        model.addAttribute("recentQuestionList", recentQuestionList);

        List<Answer> recentAnswerList = this.answerService.getRecentAnswer();
        model.addAttribute("recentAnswerList", recentAnswerList);

        return "index";
    }

}
