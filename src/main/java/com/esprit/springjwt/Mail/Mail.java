package com.esprit.springjwt.Mail;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import com.esprit.springjwt.entity.User;



@Service
public class Mail {
	@Autowired
	PasswordEncoder encoder;
	 @Autowired
     JavaMailSender javaMailSender;
	 
	 @Value("${spring.mail.username}") private String sender;
	 @Value("${site.base.url.https:http://localhost:4200}") private String siteBaseUrl;
	 private final TemplateEngine templateEngine;
		
		public Mail(TemplateEngine templateEngine) {
			this.templateEngine=templateEngine;
		}
		
	public void SendForgotPassword(User user,String Token)
			throws MessagingException, UnsupportedEncodingException {

		Context context=new Context();
		context.setVariable("token", Token);
		context.setVariable("link", siteBaseUrl + "/verifyemail");
		
		String body = templateEngine.process("ForgotPassword", context);
		String senderName = "9angraTraining";
         
		MimeMessage message = javaMailSender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(message,true);
		String toAddress = user.getUsername();
		helper.setFrom(sender, senderName);
		helper.setTo(toAddress);
		helper.setSubject("Email Forgot Password");

		helper.setText(body, true);

		javaMailSender.send(message);
	}
	
	public void sendVerificationEmail(User user)
			throws MessagingException, UnsupportedEncodingException {

		Context context=new Context();

		context.setVariable("name", user.getLastName());
		context.setVariable("password",user.getNumeroTel());
		context.setVariable("link","http://localhost:4200/login?email="+user.getUsername());
		String body=templateEngine.process("VerifyEmail", context);
		
		String senderName = "9angraTraining";
         
		MimeMessage message = javaMailSender.createMimeMessage();
		
		MimeMessageHelper helper = new MimeMessageHelper(message,true);
		String toAddress = user.getUsername();
		helper.setFrom(sender, senderName);
		helper.setTo(toAddress);
		helper.setSubject("Email address Verification");

		helper.setText(body, true);

		javaMailSender.send(message);
	}


}
