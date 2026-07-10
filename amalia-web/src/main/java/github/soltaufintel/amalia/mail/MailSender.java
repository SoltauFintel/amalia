package github.soltaufintel.amalia.mail;

import org.pmw.tinylog.Logger;

import github.soltaufintel.amalia.web.config.AppConfig;

public class MailSender extends AbstractMailSender {
    public static final String SMTP_SERVER = "mail.smtp-server";

    public void send(Mail mail, AppConfig app) {
        send(mail, new MailConfig() {
            @Override
            public String getUsername() {
                return app.get("mail.username");
            }

            @Override
            public String getPassword() {
                return app.get("mail.password");
            }

            @Override
            public String getServer() {
                return app.get(SMTP_SERVER);
            }

            @Override
            public int getPort() {
                return app.getInt("mail.smtp-server-port", getDefaultPort());
            }

            @Override
            public String getFromMailAddress() {
                return app.get("mail.from.mail-address");
            }

            @Override
            public String getForceTo() {
                return app.get("mail.to", "");
            }
        });
    }

    @Override
    protected void logSendMail(Mail mail, String sendTo, String froMAString) {
        Logger.info("Mail sent to: " + sendTo + " \"" + mail.getSubject() + "\"");
    }
}
