package github.soltaufintel.amalia.ckeditor;

import java.util.Collection;
import java.util.List;

import com.github.template72.data.DataMap;

import github.soltaufintel.amalia.web.action.Escaper;

public interface CKEditorModel {
    
    DataMap getModel();

    String getType();

    String getId();

    int getVersion();
    
    String getText();
    
    String getPostContentsPath();
    
    String getImageUploadPath();
    
    /**
     * @return "#" = no live save
     */
    default String getLiveSavePath() {
        return "#";
    }

    /**
     * Access other fields using ctx.formParam().
     * @param html -
     * @param version increment version and save it
     */
    void save(String html, int version);
    
    default void versionMismatch() {
        throw new RuntimeException("Die gespeicherte Version hat sich geändert. Bitte laden Sie den Datensatz neu und versuchen Sie es erneut.");
    }
    
    default String u(String text) {
        return Escaper.urlEncode(text, "");
    }

    /**
     * @return value for optimizing optimal editor height. CSS is: max-height: calc(100vh - {vh}px);
     */
    default int getVh() {
        return 320;
    }
    
    default String getEditorStyle() {
        return "min-height: 150px;";
    }
    
    default String getGuiLanguage() {
        return "en";
    }
    
    default String getSaveErrorMsg() {
        if ("de".equals(getGuiLanguage())) {
            return "Fehler beim Speichern der Seite! Inhalte werden im Browser gespeichert. Melden Sie sich erneut an und beginnen Sie erneut mit der Bearbeitung.";
        } else {
            return "Error saving page! Content is being saved in your browser. Please log in again and start editing.";
        }
    }
    
    default String getKeepOldInputMsg() {
        if ("de".equals(getGuiLanguage())) {
            return "Es werden ungespeicherte Eingaben angezeigt.";
        } else {
            return "Unsaved input is displayed.";
        }
    }
    
    /**
     * @return true: all styles, false: only some styles
     */
    default boolean isBigEditor() {
        return true;
    }
    
    /**
     * Also save other form fields in case of crash.
     * Language postfix (e.g. "EN") will be automatically appended if needed.
     * @return field names, not null
     */
    default Collection<String> getAdditionalFields() {
        return List.of();
    }
}
