package com.pintor.sbb_remind.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    public boolean send(String email, String code, String type) {

        try {
            MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String title = "", content = "";

            if (type.equals("이메일 인증")) {
                title = "SBB_Remind " + type + " 발송 메일입니다.";
                content = "아래 링크로 접속하시면 인증이 완료됩니다.<br>"
                        + "http://localhost:10001/member/authenticate/"
                        + code;
            } else if (type.equals("임시 비밀번호")) {
                title = "SBB_Remind " + type + " 발송 메일입니다.";
                content = "아래 비밀번호로 로그인 하시고 비밀번호를 변경해주세요.<br>"
                        + code;
            }

            helper.setTo(email);
            helper.setSubject(title);
            helper.setText(content, true);

            this.javaMailSender.send(mimeMessage);

            return true;

        } catch (Exception e) {

            return false;
        }
    }
}
