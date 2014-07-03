package com.example.blackboarddhbwloe.tools;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mail {

	public static void sendMail(String username, String messageString, 
			String sub) {
		final String reciepient = username + "@dhbw-loerrach.de";
		final String text = messageString;
		final String subject = sub;

		
		Thread t = new Thread() {
			public void run() {
				final String usrName = "bbdhbwloe@gmail.com";
				final String password = "Black12Board";

				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", "smtp.gmail.com");
				props.put("mail.smtp.port", "587");
				props.put("mail.smtp.connectiontimeout", "10000");
				props.put("mail.smtp.timeout", "10000");

				Session session = Session.getInstance(props,
						new javax.mail.Authenticator() {
							protected PasswordAuthentication getPasswordAuthentication() {
								return new PasswordAuthentication(usrName,
										password);
							}
						});

				try {


					Message message = new MimeMessage(session);
					
					message.setFrom(new InternetAddress("bbdhbwloe@gmail.com"));
					message.setRecipients(Message.RecipientType.TO,
							InternetAddress.parse(reciepient));
					message.setSubject(subject);
					message.setText(text);

					Transport.send(message);

					System.out.println("Done");

				} catch (MessagingException e) {
					throw new RuntimeException(e);
				}
			}
		};
		t.start();
	}
}
