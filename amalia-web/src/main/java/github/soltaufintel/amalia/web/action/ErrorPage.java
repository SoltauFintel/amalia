package github.soltaufintel.amalia.web.action;

public interface ErrorPage {

    void setException(Exception exception);
    
    void setMsg(String msg);
}
