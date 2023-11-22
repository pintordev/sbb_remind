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
    public ResponseEntity create(@RequestParam("content") String content,
                                 @RequestParam(value = "qId") Long qId,
                                 @RequestParam(value = "aId", defaultValue = "-1") Long aId,
                                 @RequestParam(value = "tId", defaultValue = "-1") Long tId,
                                 Principal principal) {

        log.info("answer create request");
        log.info("content: " + content);
        log.info("questionId: " + qId);
        log.info("answerId: " + aId);
        log.info("tagId: " + tId);

        if (content.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RsData<>("F-1", "내용은 필수로 입력해주세요", ""));
        }

        Question question = this.questionService.getById(qId);
        Member author = this.memberService.getByLoginId(principal.getName());
        Answer root = this.answerService.getById(aId);
        Answer tag = this.answerService.getById(tId);

        Answer answer = this.answerService.create(content, author, question, root, tag);

        log.info("answer has created on question #" + question.getId());

        Map<String, Object> answerAttributes = new HashMap<>();

        answerAttributes.put("id", answer.getId());
        answerAttributes.put("content", answer.getContent());
        answerAttributes.put("author", answer.getAuthor().getNickName());
        answerAttributes.put("createDate", answer.getCreateDate().format(DateTimeFormatter.ofPattern("yy.MM.dd HH:mm")));
        answerAttributes.put("count", answer.getQuestion().getAnswerList().size());
        answerAttributes.put("tag_id", answer.getTagId());
        answerAttributes.put("tag_nick_name", answer.getTagNickName());

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
    @PostMapping("/delete")
    @ResponseBody
    public ResponseEntity delete(@RequestParam("id") Long id,
                                 Principal principal) {

        Answer answer = this.answerService.getById(id);

        if (!answer.getAuthor().getLoginId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RsData<>("F-1", "해당 댓글에 대한 삭제 권한이 없습니다", ""));
        }

        log.info("answer delete request");
        log.info("id: " + id);

        answer = this.answerService.delete(answer);

        log.info("answer has deleted");

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("S-1", "댓글 삭제를 완료했습니다", answer.getId()));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like")
    @ResponseBody
    public ResponseEntity like(@RequestParam("id") Long id,
                               Principal principal) {

        Answer answer = this.answerService.getById(id);

        if (answer.getAuthor().getLoginId().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RsData<>("F-1", "본인이 작성한 댓글은 추천할 수 없습니다", ""));
        }

        log.info("answer like request");
        log.info("id: " + id);

        Member member = this.memberService.getByLoginId(principal.getName());

        answer = this.answerService.like(answer, member);

        log.info("answer liked has toggled");

        Map<String, Object> answerAttributes = new HashMap<>();

        answerAttributes.put("id", answer.getId());
        answerAttributes.put("liked", answer.getLiked());

        return ResponseEntity.status(HttpStatus.OK)
                .body(new RsData<>("S-1", "댓글 추천 변경을 완료했습니다", answerAttributes));
    }
}
