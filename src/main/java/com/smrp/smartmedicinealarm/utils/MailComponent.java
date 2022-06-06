package com.smrp.smartmedicinealarm.utils;

import com.smrp.smartmedicinealarm.error.code.GlobalErrorCode;
import com.smrp.smartmedicinealarm.error.code.MailErrorCode;
import com.smrp.smartmedicinealarm.error.exception.GlobalException;
import com.smrp.smartmedicinealarm.error.exception.MailSenderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailComponent {
    private final JavaMailSender sender;
    private final MailProperties mailProperties;

    public void sendMail( String toEmail, String toName, String title, String content) {
        sendMail(mailProperties.getUsername(), "SmartMedicineAlarm", toEmail, toName, title, content);
    }

    public  void sendMail(String fromEmail, String fromName, String toEmail, String toName, String title, String content){
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(new InternetAddress(fromEmail, fromName));
            mimeMessageHelper.setTo(new InternetAddress(toEmail, toName));
            mimeMessageHelper.setSubject(title);
            mimeMessageHelper.setText(content, true);

        };
        try {
            sender.send(mimeMessagePreparator);
            log.info("sendMail - {}님에게 {} 제목에 메일이 전송되었습니다.", toEmail, title);
        } catch (MailPreparationException e) {
            log.error("sendMailError {}님에게 {}제목에 메일 전송에 실패하였습니다.", toEmail, title);
            throw new MailSenderException(MailErrorCode.WRONG_MAIL_TEXT) {
            };
        } catch (MailParseException e) {
            log.error("sendMailError {}님에게 {} 제목에 메일 전송에 실패하였습니다.", toEmail, title);
            throw new MailSenderException(MailErrorCode.WRONG_MAIL_ATTRIBUTE);
        } catch (MailSenderException e) {
            log.error("sendMailError {}님에게 {} 제목에 메일 전송에 실패하였습니다.", toEmail, title);
            throw new MailSenderException(MailErrorCode.WRONG_INPUT_MAIL);
        }catch (MailAuthenticationException e){
            log.error("sendMailError {}님에게 {} 제목에 메일 전송에 실패하였습니다.", toEmail, title);
            throw new MailSenderException(MailErrorCode.WRONG_MAIL_AUTHENTICATION);
        }catch (MailException e){
            log.error("sendMailError {}님에게 {} 제목에 메일 전송에 실패하였습니다.", toEmail, title);
            throw new GlobalException(GlobalErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
