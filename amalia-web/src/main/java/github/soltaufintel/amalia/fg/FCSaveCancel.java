package github.soltaufintel.amalia.fg;

import com.github.template72.data.DataMap;

import github.soltaufintel.amalia.base.StringService;

public class FCSaveCancel extends FCBaseItem {
    private final String cancellink;
    private String saveLabel = "Speichern";
    private String cancelLabel = "Abbruch";
    private String waitDisplay;
    
    public FCSaveCancel(String cancellink) {
        super("", "");
        this.cancellink = cancellink;
    }

    @Override
    protected void fill(DataMap model) {
        String save = getSaveLabel();
        if (!StringService.isNullOrEmpty(waitDisplay)) {
            model.put("attrs", " onclick=\"document.querySelector('#" + waitDisplay + "').style='';\"");
            save += " <i id=\"" + waitDisplay + "\" class=\"fa fa-delicious fa-spin\" style=\"display: none;\"></i>";
        } else {
            model.put("attrs", "");
            model.put("attrs2", "");
        }
        model.put("save", save);
        model.put("cancel", getCancelLabel());
        model.put("cancellink", cancellink);
    }

    public String getSaveLabel() {
        return saveLabel;
    }

    public void setSaveLabel(String saveLabel) {
        this.saveLabel = saveLabel;
    }

    public String getCancelLabel() {
        return cancelLabel;
    }

    public void setCancelLabel(String cancelLabel) {
        this.cancelLabel = cancelLabel;
    }

    public String getWaitDisplay() {
        return waitDisplay;
    }

    /**
     * @param waitDisplay ID (e.g. "s1") for a wait display for Save button
     */
    public void setWaitDisplay(String waitDisplay) {
        this.waitDisplay = waitDisplay;
    }
}
