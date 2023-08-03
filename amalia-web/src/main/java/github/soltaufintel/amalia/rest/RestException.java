package github.soltaufintel.amalia.rest;

public class RestException extends RestStatusException {
    private final ErrorMessage m;

    public RestException(ErrorMessage m, int status) {
        super(m == null ? "Status is " + status : m.toString(), status);
        this.m = m;
    }

    public ErrorMessage getErrorMessage() {
        return m;
    }
}
