package com.googlecode.icegem.cacheutils.monitor.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Service to simplify the email sending
 */
public class EmailService {

	private static EmailService instance = null;

	private Session mailSession;

	private PropertiesHelper propertiesHelper;

	public static EmailService getInstance() {
		if (instance == null) {
			try {
				instance = new EmailService();
			} catch (Exception e) {
				throw new IllegalStateException(
						"Cannot initialize EmailService", e);
			}
		}

		return instance;
	}

	/**
	 * Sends email with subject and content to receivers specified in property file after key "mail.to" 
	 * 
	 * @param subject - the subject
	 * @param content - the content
	 * @throws MessagingException
	 */
	public void send(String subject, String content) throws MessagingException {
		MimeMessage message = compose(subject, content,
				propertiesHelper.getStringProperty("mail.to"));
		transport(message);
	}

	/**
	 * Sends email with subject and content to receivers specified by argument "to"
	 * 
	 * @param subject - the subject
	 * @param content - the content
	 * @param to - the CSV list of receivers
	 * @throws MessagingException
	 */
	public void send(String subject, String content, String to)
			throws MessagingException {
		MimeMessage message = compose(subject, content, to);
		transport(message);
	}

	private EmailService() throws FileNotFoundException, IOException {
		propertiesHelper = new PropertiesHelper("/mail.properties");
		mailSession = Session.getDefaultInstance(
				propertiesHelper.getProperties(),
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(propertiesHelper
								.getStringProperty("mail.user"),
								propertiesHelper
										.getStringProperty("mail.password"));
					}
				});
	}

	private Set<String> csvToSetOfString(String csv) {
		Set<String> resultSet = new HashSet<String>();

		if ((csv != null) && (csv.trim().length() > 0)) {
			for (String s : csv.split(",")) {
				resultSet.add(s.trim());
			}
		}

		return resultSet;
	}

	private MimeMessage compose(String subject, String content, String to)
			throws MessagingException {
		MimeMessage message = new MimeMessage(mailSession);
		message.setSubject(subject);
		message.setContent(content, "text/html; charset=ISO-8859-1");

		message.setFrom(new InternetAddress(propertiesHelper
				.getStringProperty("mail.from")));
		for (String email : csvToSetOfString(to)) {
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					email));
		}

		return message;
	}

	private void transport(MimeMessage message) throws MessagingException {
		Transport.send(message);
	}
}
