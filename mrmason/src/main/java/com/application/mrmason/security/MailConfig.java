package com.application.mrmason.security;

import java.util.Base64;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.application.mrmason.entity.AdminMail;
import com.application.mrmason.repository.AdminMailRepo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Configuration
public class MailConfig {

	@Autowired
	private AdminMailRepo mailRepo;
	
	
	@Bean
	public JavaMailSender getJavaMailSender() {
		AdminMail smtpConfig = mailRepo.findByEmailid("no_reply@kosuriers.com");

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		// Use default values if smtpConfig is not found
		String host = (smtpConfig != null && smtpConfig.getMailHost() != null) ? smtpConfig.getMailHost() : "smtp.test.com";
		String port = (smtpConfig != null && smtpConfig.getSmtpPort() != null) ? smtpConfig.getSmtpPort() : "587";
		String emailid = (smtpConfig != null && smtpConfig.getEmailid() != null) ? smtpConfig.getEmailid() : "no_reply@test.com";
		String pwd = (smtpConfig != null && smtpConfig.getPwd() != null) ? smtpConfig.getPwd() : "c2VjcmV0cGFzc3dvcmQ=";
		String smtpAuth = (smtpConfig != null && smtpConfig.getSmtpAuth() != null) ? smtpConfig.getSmtpAuth() : "true";
		String starttls = (smtpConfig != null && smtpConfig.getStarttlsEnable() != null) ? smtpConfig.getStarttlsEnable() : "true";

		mailSender.setHost(host);
		mailSender.setPort(Integer.parseInt(port));
		mailSender.setUsername(emailid);
		String decodedPassword = new String(Base64.getDecoder().decode(pwd));
		mailSender.setPassword(decodedPassword);

		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", smtpConfig != null ? smtpConfig.getSmtpAuth() : "true");
		props.put("spring.mail.properties.mail.smtp.ssl.enable", true);
		//props.put("mail.debug", "true");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.starttls.enable", smtpConfig != null ? smtpConfig.getStarttlsEnable() : "true");
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = null;
		try {
			helper = new MimeMessageHelper(message, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			helper.setFrom(smtpConfig != null ? smtpConfig.getEmailid() : "no_reply@test.com");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mailSender;
	}
}