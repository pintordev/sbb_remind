package com.pintor.sbb_remind.question;

import com.google.gson.Gson;
import com.pintor.sbb_remind.exception.DataNotFoundException;
import com.pintor.sbb_remind.member.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question create(String category, String title, String content, Member author) {

        Question question = Question.builder()
                .questionCategory(QuestionCategory.getCodeByType(category))
                .title(title)
                .content(content)
                .author(author)
                .liked(0L)
                .hit(0L)
                .build();

        this.questionRepository.save(question);

        return question;
    }

    public Page<Question> getList(String kw, String sort, int page) {

        Pageable pageable = PageRequest.of(page - 1, 10);

        if (sort.equals("liked")) {
            return this.questionRepository.findAllByOrderByLiked(kw, pageable);
        } else if (sort.equals("old")) {
            return this.questionRepository.findAllByOrderByCreateDateAsc(kw, pageable);
        } else {
            return this.questionRepository.findAllByOrderByCreateDate(kw, pageable);
        }
    }

    public Question getById(Long id) {

        return this.questionRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    public Question modify(Question question, String category, String title, String content) {

        question = question.toBuilder()
                .questionCategory(QuestionCategory.getCodeByType(category))
                .title(title)
                .content(content)
                .build();

        this.questionRepository.save(question);

        return question;
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public Question like(Question question, Member member) {

        Set<Member> likedMember = question.getLikedMember();

        if (likedMember.contains(member)) {

            likedMember.remove(member);

            question = question.toBuilder()
                    .likedMember(likedMember)
                    .liked(question.getLiked() - 1)
                    .build();
        } else {

            likedMember.add(member);

            question = question.toBuilder()
                    .likedMember(likedMember)
                    .liked(question.getLiked() + 1)
                    .build();
        }

        this.questionRepository.save(question);

        return question;
    }

    public boolean hitJudge(Question question, HttpServletRequest request, HttpServletResponse response) {

        // date formatter
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        // gson initialize
        Gson gson = new Gson();
        // question key
        String qKey = "q" + question.getId();
        // hit log map
        Map<String, Object> hitLogMap = new HashMap<>();

        // check whether question cookie exits
        Cookie oldCookie = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("QuestionHit")) {
                    oldCookie = cookie;
                }
            }
        }

        // cookie check and update
        if (oldCookie != null) {

            // cookie to hit log map
            String oldValue = oldCookie.getValue().replaceAll("s", "\"").replaceAll("c", "\\,");
            hitLogMap = gson.fromJson(oldValue, Map.class);

            log.info(oldValue);
            log.info(hitLogMap.toString());

            // 이미 조회한 경우
            if (hitLogMap.containsKey(qKey)) {

                LocalDateTime oldAge = LocalDateTime.parse(hitLogMap.get(qKey).toString(), dateTimeFormatter);

                // 하루 이상 지난 경우 갱신
                if (oldAge.isBefore(LocalDateTime.now().minusHours(24))) {
                    hitLogMap.put(qKey, LocalDateTime.now().format(dateTimeFormatter));

                    // 변환 값 저장
                    String newValue = gson.toJson(hitLogMap).replaceAll("\"", "s").replaceAll("\\,", "c");
                    oldCookie.setValue(newValue);
                    oldCookie.setPath("/"); // path
                    oldCookie.setMaxAge(60 * 60 * 24); // 1 day long
                    response.addCookie(oldCookie); // save cookie to browser

                    return true;
                }

                // 그 외 갱신하지 않음
                return false;

            } else { // 아직 조회하지 않은 경우

                hitLogMap.put(qKey, LocalDateTime.now().format(dateTimeFormatter));

                // 변환 값 저장
                String newValue = gson.toJson(hitLogMap).replaceAll("\"", "s").replaceAll("\\,", "c");
                oldCookie.setValue(newValue);
                oldCookie.setPath("/"); // path
                oldCookie.setMaxAge(60 * 60 * 24); // 1 day long
                response.addCookie(oldCookie); // save cookie to browser

                return true;
            }
        } else {

            hitLogMap.put(qKey, LocalDateTime.now().format(dateTimeFormatter));
            String newValue = gson.toJson(hitLogMap).replaceAll("\"", "s");
            Cookie newCookie = new Cookie("QuestionHit", newValue);

            newCookie.setPath("/"); // path
            newCookie.setMaxAge(60 * 60 * 24); // 1 day long
            response.addCookie(newCookie); // save cookie to browser

            return true;
        }
    }

    public Question hit(Question question) {

        question = question.toBuilder()
                .hit(question.getHit() + 1)
                .build();

        this.questionRepository.save(question);

        return question;
    }
}
