package github.soltaufintel.amalia.mail;

public class Mail {
    private String sendername;
    private String toEmailaddress;
    private String toName;
    private String subject;
    private String body;

    public String getSendername() {
        return sendername;
    }

    public void setSendername(String sendername) {
        this.sendername = sendername;
    }

    public String getToEmailaddress() {
        return toEmailaddress;
    }

    public void setToEmailaddress(String toEmailaddress) {
        this.toEmailaddress = toEmailaddress;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
