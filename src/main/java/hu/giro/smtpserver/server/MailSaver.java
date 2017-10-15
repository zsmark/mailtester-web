package hu.giro.smtpserver.server;


import hu.giro.smtpserver.model.entity.EmailObject;
import hu.giro.smtpserver.model.repository.EmailObjectRepository;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Saves emails and notifies components so they can refresh their views with new data.
 *
 * @author Nilhcem
 * @since 1.0
 */
@Service
public final class MailSaver extends Observable {

    private static final Logger LOGGER = LoggerFactory.getLogger("MailSaver.class");
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    // This can be a static variable since it is Thread Safe
    private static final Pattern SUBJECT_PATTERN = Pattern.compile("^Subject: (.*)$");

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyhhmmssSSS");

    private final EmailObjectRepository repository;

    @Autowired
    public MailSaver(EmailObjectRepository repository) {
        this.repository = repository;
    }


    public void saveEmailAndNotify(String from, String to, InputStream data) {

        // We move everything that we can move outside the synchronized block to limit the impact

    }

    /**
     * Deletes all received emails from file system.
     */
    public void deleteEmails() {
    /*	for (String value : mails.values()) {
			File file = new File(value);
			if (file.exists()) {
				try {
					if (!file.delete()) {
						LOGGER.error("Impossible to delete file {}", value);
					}
				} catch (SecurityException e) {
					LOGGER.error("", e);
				}
			}
		}*/
    }

    /**
     * Returns a lock object.
     * <p>
     * This lock will be used to make the application thread-safe, and
     * avoid receiving and deleting emails in the same time.
     * </p>
     *
     * @return a lock object <i>(which is actually the current instance of the {@code MailSaver} object)</i>.
     */
    public Object getLock() {
        return this;
    }

    /**
     * Converts an {@code InputStream} into a {@code String} object.
     * <p>
     * The method will not copy the first 4 lines of the input stream.<br>
     * These 4 lines are SubEtha SMTP additional information.
     * </p>
     *
     * @param is the InputStream to be converted.
     * @return the converted string object, containing data from the InputStream passed in parameters.
     */
    private String convertStreamToString(InputStream is) {
        try {
            return IOUtils.toString(is, Charset.forName("UTF8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
/*		final long lineNbToStartCopy = 0; // Do not copy the first 4 lines (received part)
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF8")));
		StringBuilder sb = new StringBuilder();

		String line;
		long lineNb = 0;
		try {
			while ((line = reader.readLine()) != null) {
				if (++lineNb > lineNbToStartCopy) {
					sb.append(line).append(LINE_SEPARATOR);
				}
			}
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		return sb.toString();*/
    }

    /**
     * Saves the content of the email passed in parameters in a file.
     *
     * @param mailContent the content of the email to be saved.
     * @return the path of the created file.
     */
    private String saveEmailToFile(String mailContent) {

        String filePath = String.format("%s%s%s", "/tmp", File.separator,
                dateFormat.format(new Date()));

        // Create file
        int i = 0;
        File file = null;
        while (file == null || file.exists()) {
            String iStr;
            if (i++ > 0) {
                iStr = Integer.toString(i);
            } else {
                iStr = "";
            }
            file = new File(filePath + iStr + "_email");
        }

        // Copy String to file
        try {
            FileUtils.writeStringToFile(file, mailContent);
        } catch (IOException e) {
            // If we can't save file, we display the error in the SMTP logs
            Logger smtpLogger = LoggerFactory.getLogger(org.subethamail.smtp.server.Session.class);
            smtpLogger.error("Error: Can't save email: {}", e.getMessage());
        }
        return file.getAbsolutePath();
    }

    /**
     * Gets the subject from the email data passed in parameters.
     *
     * @param data a string representing the email content.
     * @return the subject of the email, or an empty subject if not found.
     */
    private String getSubjectFromStr(String data) {


        try {
            Session s = Session.getDefaultInstance(new Properties());
            InputStream is = new ByteArrayInputStream(data.getBytes());
            MimeMessage message = new MimeMessage(s, is);
            return message.getSubject();
        } catch (MessagingException e) {
            LOGGER.error("", e);

        }
       /* try {
			BufferedReader reader = new BufferedReader(new StringReader(data));

			String line;
			while ((line = reader.readLine()) != null) {
				 Matcher matcher = SUBJECT_PATTERN.matcher(line);
				 if (matcher.matches()) {
					 return matcher.group(1);
				 }
			}
		} catch (IOException e) {
		  LOGGER.error("", e);
		}*/
        return "";
    }
}
