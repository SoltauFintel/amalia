package github.soltaufintel.amalia.mail;

import org.pmw.tinylog.Logger;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import github.soltaufintel.amalia.web.config.AppConfig;

public class MailSender {
    public static final String SMTP_SERVER = "mail.smtp-server";
    public static boolean active = true;
    public static String to;
    public static String subject;
    public static String body;
    
    public void send(Mail mail, AppConfig config) {
        String fromMailAddress = config.get("mail.from.mail-address");
        String forceTo = config.get("mail.to", "");
        String sendTo = forceTo.isBlank() ? getTo(mail) : forceTo;

        if (active) {
            Mailer mailer = MailerBuilder
                    .withSMTPServer(config.get(SMTP_SERVER), config.getInt("mail.smtp-server-port", getDefaultPort()))
                    .withSMTPServerUsername(config.get("mail.username"))
                    .withSMTPServerPassword(config.get("mail.password"))
                    .buildMailer();
            mailer.sendMail(EmailBuilder.startingBlank()
                    .from(mail.getSendername(), fromMailAddress)
                    .to(sendTo)
                    .withSubject(mail.getSubject())
                    .withPlainText(mail.getBody())
                    .buildEmail());
            logSendMail(mail, sendTo, fromMailAddress);
        } else {
            to = mail.getToEmailaddress();
            subject = mail.getSubject();
            body = mail.getBody();
        }
    }
    
    /**
     * Port can also be 25 or 2525.
     * @return 587
     */
    protected int getDefaultPort() {
    	return 587;
    }
    
    protected void logSendMail(Mail mail, String sendTo, String froMAString) {
        Logger.info("Mail sent to: " + sendTo + " \"" + mail.getSubject() + "\"");
    }
    
    private String getTo(Mail mail) {
        if (mail.getToName() == null || mail.getToName().isBlank()) {
            return mail.getToEmailaddress();
        }
        return "\"" + mail.getToName() + "\" <" + mail.getToEmailaddress() + ">";
    }
}
