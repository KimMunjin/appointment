package com.zerobase.appointment.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

  private final JavaMailSender emailSender;
  private final String MAIL_SUBJECT = "회원 가입 확인 이메일";
  private final String MAIL_TEST = "계정을 활성화하려면 다음 링크를 클릭하세요: ";
  @Value("${DOMAIN_URL}")
  private String domainUrl;

  public void sendVerificationEmail(String email, String authCode) {
    String url = domainUrl + "/confirm?email=" + email + "&authCode=" + authCode;

    String htmlContent = "<html><body>" +
        "<p>" + MAIL_TEST + "<a href='" + url + "'>이메일 확인 링크</a></p>" +
        "</body></html>";

    MimeMessage message = emailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setSubject(MAIL_SUBJECT);
      helper.setTo(email);
      helper.setText(htmlContent, true);
    } catch (MessagingException e) {
      log.error("이메일 발송 에러 발생");
    }
    emailSender.send(message);
  }

}
