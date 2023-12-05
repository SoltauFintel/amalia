package github.soltaufintel.amalia.web.action;

public abstract class Form extends Page {

    @Override
    protected void execute() {
        if (isPOST()) {
            post();
        } else {
            get();
        }
    }

    /**
     * display
     */
    protected abstract void get();

    /**
     * save
     */
    protected abstract void post();
}
