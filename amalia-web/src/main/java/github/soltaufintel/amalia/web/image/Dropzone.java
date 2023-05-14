package github.soltaufintel.amalia.web.image;

/**
 * HTML snippet for drop zone
 */
public class Dropzone {

	public String getHTML(String action) {
		return """
			<form action="[action]" method="post" class="dropzone" id="[formId]">
				<div class="dz-message"><br/>DROP ZONE</div>
				<div class="fallback"><input name="[inputId]" type="file" multiple></div>
			</form>
			""".replace("[action]", action)
			   .replace("[formId]", getFormId())
			   .replace("[inputId]", getInputId());
	}
	
	protected String getFormId() {
		// If you use another id you can't use dropzone-options.js!
		return "myDropzone";
	}
	
	protected String getInputId() {
		// same id as AbstractImageUpload.getId()
		return "file";
	}
}
