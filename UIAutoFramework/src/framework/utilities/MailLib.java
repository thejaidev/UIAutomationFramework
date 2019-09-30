/* MailLib Library contains the most commonly used methods to perform actions on email client
 * Guideline: Only reusable navigation flows should be added in this file.
 */

package framework.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This class contains all email related methods / actions
 */
public class MailLib {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private ConfigurationLib configLib = new ConfigurationLib();

	/**
	 * To send an email with report as attachment post execution
	 *
	 */
	public void sendMail(String to, String subject, String body, String attachFileNm, boolean reportMail)
			throws Exception {
		FileSystemLib fileSystem = new FileSystemLib();
		List<String> reports = null;
		String username = configLib.getSender();
		String tempScreenShots = "Screenshots.zip";
		String templogFile = configLib.getlogFileName().replace(".log", ".zip");
		String[] tempFiles = { templogFile, tempScreenShots };
		SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = new Date();
		String logFileWithPath = configLib.getlogFilePath() + configLib.getlogFileName();
		String screenshotPath = configLib.getscreenshotPath();

		CommonUtilLib utilLib = new CommonUtilLib();

		Properties props = new Properties();
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.host", "<smtp host name>");

		Session session = Session.getInstance(props, null);

		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		addRecipients(message, to);
		message.setSubject(
				subject + " | Host Machine : " + utilLib.getHostname() + " | " + simpleDateFormatter.format(date));

		BodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(body);
		messageBodyPart.setContent(body, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		if (reportMail) {
			reports = fileSystem.getFilesInFolder(configLib.getReportPath());
			for (String report : reports)
				addAttachment(multipart, messageBodyPart, configLib.getReportPath(), report);

			fileSystem.compressFileFolders(logFileWithPath, System.getProperty("user.dir"), templogFile);
			addAttachment(multipart, messageBodyPart, System.getProperty("user.dir"), templogFile);

			fileSystem.compressFileFolders(screenshotPath, System.getProperty("user.dir"), tempScreenShots);
			addAttachment(multipart, messageBodyPart, System.getProperty("user.dir"), tempScreenShots);
		}
		else
			addAttachment(multipart, messageBodyPart, configLib.getscreenshotPath(),
					attachFileNm.substring(attachFileNm.lastIndexOf("\\") + 1, attachFileNm.length()));
		message.setContent(multipart);

		Transport transport = session.getTransport("smtp");
		transport.connect("smtp host machine", username, null);
		transport.sendMessage(message, message.getAllRecipients());

		logger.info("Sent Email");
		// Cleanup Temporary Files
		for (String deleteFile : tempFiles)
			fileSystem.deleteFilesFolders(System.getProperty("user.dir") + "\\" + deleteFile);

	}

	/**
	 * To add attachments to the email
	 *
	 * @note This is called by sendMail(). Do not call this method directly
	 */
	private void addRecipients(MimeMessage message, String recipient) {
		logger.info("Adding recipients to send email");
		try {
			if (recipient.contains(";")) {
				String[] recipientList = recipient.split(";");
				InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
				int counter = 0;
				for (String to : recipientList) {
					recipientAddress[counter] = new InternetAddress(to.trim());
					counter++;
				}
				message.setRecipients(Message.RecipientType.TO, recipientAddress);
			}
			else
				message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(recipient)[0]);
		}
		catch (Exception e) {
			logger.error("Unable to add the recipients", e);
		}
	}

	/**
	 * To add attachments to the email
	 *
	 * @param attachPath
	 *            Path of the attachment
	 * @param attachName
	 *            Name of the attachment
	 * @note This is called by sendMail(). Do not call this method directly
	 */

	private void addAttachment(Multipart multipart, BodyPart messageBodyPart, String attachPath, String attachName) {
		String completeFilePath = attachPath + "\\" + attachName;
		logger.info("Adding attachement to the email");
		try {
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(completeFilePath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(attachName);
			multipart.addBodyPart(messageBodyPart);
		}
		catch (Exception e) {
			logger.error("Unable to add the attachment");
		}
	}
}