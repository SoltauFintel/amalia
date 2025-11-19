package github.soltaufintel.amalia.ckeditor;

import java.util.Collection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;
import com.github.template72.data.DataValue;
import com.github.template72.data.IDataMap;

import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.action.Page;

// amalia-web enthält CKEditor, .css, .js
// 2 Vars in mainhead: ckeditorCSS, ckeditorJS
// 1 Aufruf in PageInitializer
public class CKEditor {
    /** list name for CKEditor languages */
    public static final String CKEDLANGS = "ckedLangs";
    /** var name for language (language value must be uppercase) */
    public static final String LANG = "LANG";
    private final CKEditorModel model;
    
    // TODO Bei Abbruch darf keine SiAbfr kommen!?
    
    public CKEditor(CKEditorModel model) {
        this.model = model;
    }
    
    public CKEditorModel getModel() {
        return model;
    }
    
    public void get() {
        get(model.getModel(), model.getModel(), "");
    }

    // wichtig für Mehrsprachigkeit
    public void get(String idPostfix, DataMap parentModel) {
        get(model.getModel(), parentModel, idPostfix);
    }
    
    private void get(DataMap myModel, DataMap pModel, String idPostfix) {
        if (pModel.get(CKEDLANGS) == null) {
            pModel.list(CKEDLANGS).add().put(LANG, ""); // keine Mehrsprachigkeit
        }
        // ckedFields je language anlegen
        var ckedFields = pModel.list("ckedFields");
        for (IDataMap m : pModel.getList(CKEDLANGS)) {
            String lang = ((DataValue) m.get(LANG)).toString();
            configureFields(model.getAdditionalFields(), lang, ckedFields);
        }
        
        putVar(myModel, idPostfix, model.getText(), model);
        
        putParams(pModel);
    }
    
    private void configureFields(Collection<String> additionalFields, String lang, DataList ckedFields) {
        var m = ckedFields.add();
        m.put("isCKEditor", true);
        m.put("field", "editor" + lang);
        for (String field : additionalFields) {
            m = ckedFields.add();
            m.put("isCKEditor", false);
            m.put("field", field + lang);
        }
    }
    
    // wichtig für Mehrsprachigkeit
    public static void putVar(DataMap myModel, String idPostfix, String html, CKEditorModel model) {
        var style = "max-height: calc(100vh - " + model.getVh() + "px);" + model.getEditorStyle();
        myModel.put("ckeditor", "<div class=\"form-group\"><div id=\"toolbar-container" + idPostfix + "\"></div>"
                + "<div id=\"editor" + idPostfix + "\" class=\"editbox\" style=\"" + style + "\">" + //
                html /* no esc */
                + "</div></div>");
    }

    private void putParams(DataMap pModel) {
        pModel.put("ckeditorCSS", "<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/ckeditor-vite.css\">" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/ckeditor.css\">");
        pModel.put("ckeditorJS", "<script src=\"/js/ckeditor-vite.js\" type=\"module\"></script>"
                + "<script src=\"/js/image-upload.js\"></script>");
        pModel.put("id", model.getId());
        pModel.put("bigEditor", model.isBigEditor());
        pModel.put("imageuploadlink", model.getImageUploadPath());
        pModel.put("postcontentslink", model.getPostContentsPath());
        pModel.put("livesavelink", model.getLiveSavePath());
        pModel.put("errorName", model.getType());
        pModel.put("saveError", model.getSaveErrorMsg());
        pModel.put("keepOldInput", model.getKeepOldInputMsg());
        pModel.putInt("version", model.getVersion());
        pModel.put("ckedGuiLanguage", model.getGuiLanguage());
    }
    
    public void post(Context ctx) {
        int version = Integer.parseInt(ctx.formParam("version"));
        PostContentsData pcd = new PostContentsService().waitForContents(model.getId(), version);
        String content = pcd.getContent();
        model.save(filter(content), version);
    }
    
    /**
     * @param html -
     * @return html without style attributes
     */
    public static String filter(String html) {
        if (html == null) {
            return "";
        }
        boolean dirty = false;
        Document document = Jsoup.parse(html);
        Elements elementsWithStyle = document.select("[style]");
        if (!elementsWithStyle.isEmpty()) {
            for (Element element : elementsWithStyle) {
                element.removeAttr("style");
                dirty = true;
            }
        }
        for (Element element : document.select("img")) {
            if (element.hasAttr("width")) {
                element.removeAttr("width");
                dirty = true;
            }
            if (element.hasAttr("height")) {
                element.removeAttr("height");
                dirty = true;
            }
        }
        return dirty ? document.html() : html;
    }

    // call by PageInitializer
    public static void initPage(Page page) {
        page.put("ckeditorCSS", "");
        page.put("ckeditorJS", "");
    }
}
