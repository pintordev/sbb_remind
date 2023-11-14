package com.pintor.sbb_remind.answer;

import com.pintor.sbb_remind.member.Member;
import com.pintor.sbb_remind.member.MemberService;
import com.pintor.sbb_remind.question.Question;
import com.pintor.sbb_remind.question.QuestionService;
import com.pintor.sbb_remind.util.RsData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

}
