package github.soltaufintel.amalia.ckeditor; // TODO nach amalia-web

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.github.template72.data.DataMap;

import github.soltaufintel.amalia.ckeditor.image.ImageUploadAction;
import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.action.Escaper;
import github.soltaufintel.amalia.web.action.Page;

// amalia-web enthält CKEditor, .css, .js
// 2 Vars in mainhead: ckeditorCSS, ckeditorJS
// 1 Aufruf in PageInitializer
public class CKEditor {
    private final Context ctx;
    private final DataMap model;
    private final AbstractCKEditorModel text;
    private final String var;
    
    // TODO Bei Abbruch darf keine SiAbfr kommen!
    
    public CKEditor(Context ctx, DataMap model, AbstractCKEditorModel text, String var) {
        this.ctx = ctx;
        this.model = model;
        this.text = text;
        this.var = var;
    }

    protected String getEditorStyle() {
        return "min-height: 150px; ";
    }

    public void get() {
        String id = text.getId();
        
        model.put("ckeditorCSS", "<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/ckeditor-vite.css\">" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"/css/ckeditor.css\">");
        model.put("ckeditorJS", "<script src=\"/js/ckeditor-vite.js\" type=\"module\"></script>"
                + "<script src=\"/js/image-upload.js\"></script>");
        
        model.put(var,
                "<div class=\"form-group\"><div id=\"toolbar-container\"></div>"
                        + "<div id=\"editor\" class=\"editbox\" style=\"" + getEditorStyle() + "max-height: calc(100vh - " + text.vh() + "px);\">"
                        + text.getHTML() /* no esc */
                        + "</div></div>");
        
        model.put("id", id);
        model.put("imageuploadlink", ImageUploadAction.imageService.getImageUploadLink(ctx));
        model.put("bigEditor", true);
        model.put("jspackage", "moco");
        model.put("postcontentslink", postcontentslink(text, id));
        model.put("errorName", text.getType());
        model.put("postExtra", "");// "titel: document.getElementById('titel').value,\r\n");
        model.put("postFailExtra", "");
                //"localStorage.setItem('error_titel." + id + "', document.getElementById('titel').value);\r\n");
        model.put("saveError",
                "Fehler beim Speichern der Seite! Inhalte werden im Browser gespeichert. Melden Sie sich erneut an und beginnen Sie erneut mit der Bearbeitung.");
        model.put("keepOldInput", "Es werden ungespeicherte Eingaben angezeigt.");
        model.put("onloadExtra", "");//"document.getElementById('titel').value = localStorage.getItem('error_titel." + id
                //+ "');\r\n" + "localStorage.removeItem('error_titel." + id + "');\r\n");
        model.putInt("version", text.getVersion());
    }

    protected String postcontentslink(AbstractCKEditorModel ckeditorModel, String id) {
        return "/aufgabentext/" + id + "/post/" + ckeditorModel.getType() + "?key=" + Escaper.urlEncode(ckeditorModel.getId(), "");
    }
    
    public void post() {
        int version = Integer.parseInt(ctx.formParam("version"));
        PostContentsData pcd = new PostContentsService().waitForContents(text.getId(), version);
        String content = pcd.getContent();
        text.setHTML(filter(content));
    }
    
    /**
     * @param html -
     * @return html without style attributes
     */
    public static String filter(String html) {
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
