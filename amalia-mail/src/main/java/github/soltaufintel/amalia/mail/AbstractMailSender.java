package github.soltaufintel.amalia.mail;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public abstract class AbstractMailSender {
    public static boolean active = true;
    public static String to;
    public static String subject;
    public static String body;
    
    public void send(Mail mail, MailConfig config) {
        String fromMailAddress = config.getFromMailAddress();
        String forceTo = config.getForceTo();
        String sendTo = forceTo.isBlank() ? getTo(mail) : forceTo;

        if (active) {
            var host = config.getServer();
            var port = config.getPort();
            try {
                Mailer mailer = MailerBuilder
                        .withSMTPServer(host, port)
                        .withSMTPServerUsername(config.getUsername())
                        .withSMTPServerPassword(config.getPassword())
                        .buildMailer();
                mailer.sendMail(EmailBuilder.startingBlank()
                        .from(mail.getSendername(), fromMailAddress)
                        .to(sendTo)
                        .withSubject(mail.getSubject())
                        .withPlainText(mail.getBody())
                        .buildEmail());
                logSendMail(mail, sendTo, fromMailAddress);
            } catch (Exception e) {
                handleException(e, mail, host, port);
            }
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
    
    // info("Mail sent to: " + sendTo + " \"" + mail.getSubject() + "\"");
    protected abstract void logSendMail(Mail mail, String sendTo, String froMAString);
    
    private String getTo(Mail mail) {
        if (mail.getToName() == null || mail.getToName().isBlank()) {
            return mail.getToEmailaddress();
        }
        return "\"" + mail.getToName() + "\" <" + mail.getToEmailaddress() + ">";
    }
    
    protected void handleException(Exception e, Mail mail, String host, int port) {
        throw new RuntimeException("Email can not be sent.", e);
        // Otherwise "Third party error" would be displayed to the user.
    }
}
