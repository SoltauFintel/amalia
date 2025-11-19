# CKEditor

In einer amalia-web Anwendung kann die WYSIWYG Komponente CKEditor eingesetzt werden.

Features:

- Persistenz
- Mehrsprachigkeit (d.h. ein CKEditor Eingabefeld je Sprache)
- Bilder Upload/Download
- Speichern mit Strg+S
- Schutz vor versehentlichen Verlassen der Seite
- Browser-Absturz-Sicherheit
- Webapp-Absturz-Sicherheit
- Live Save ins Journal (alle 15 Sekunden den Text zum Server senden)

## Einbau

Datei editor.html aus amalia-web nach -project-/src/main/resources/templates kopieren.
Das Template, welches einen CKEditor enthalten soll, muss diese Datei am Dateiende (einmal) einbinden:

```
{{include: editor}}
```

An der Stelle an der der CKEditor kommen soll ist `{{ckeditor}}` zu schreiben. Das Formular muss die CSS Klasse "editorform",
ein verstecktes version Feld und einen Submit Button haben.

        <form class="editorform form-horizontal" method="post">
            <fieldset>
                <input type="hidden" id="version" name="version" value="{{version}}">
                <div class="form-group">
                    <label class="col-lg-1 control-label">Beschreibung</label>
                    <div class="col-lg-7">
                        {{ckeditor}}
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-lg-offset-1 col-lg-5">
                        <div class="form-group">
                            <button id="submit" type="submit" class="btn btn-primary br"
                                onclick="document.querySelector('#s1').style='';">Speichern
                                <i id="s1" class="fa fa-delicious fa-spin" style="display: none;"></i></button>
                            <a href="{{cancel}}" class="btn btn-default">Abbruch</a>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>

Das master Template muss im Kopf (bzw. das master_head Template muss) `{{ckeditorCSS}}` und `{{ckeditorJS}}` enthalten.
In der PageInitializer Implementierung muss `CKEditor.initPage(page)` aufgerufen werden.

## Page

Die Page Klasse muss mittels form() eingebunden werden (d.h. GET und POST).
In der Page Klasse muss eine CKEditor Instanz (cked) mithilfe einer CKEditorModel Implementierung erzeugt werden. Im isPOST() Teil muss `ck.post(ctx)` und im else-GET-Teil muss `ck.get()` aufgerufen werden.
Die CKEditorModel Implementierung muss die Entity kennen, da viele Methoden auf sie zugreifen werden.

            @Override
            public String getId() {
                return entity.getId();
            }


## Post Contents

    post("/post-contents/:type", PostContentsAction.class)

    public class VogelPCD extends PostContentsData2 {
        public static final String TYPE = "vogel";
        
        public VogelPCD(Context ctx) {
            super(ctx);
        }
        
        public static void init() {
            PostContentsService.pcdClasses.put(TYPE, VogelPCD.class);
        }
    }

## Images

        get("/s/master/handbuch/:id/img/:filename", ImageDownloadAction.class);
        get("/s-edit/master/handbuch/:id/img/:filename", ImageDownloadAction.class);
        post("/s-edit/master/handbuch/:id/image-upload", ImageUploadAction.class);

## ImageService

                var imageServiceDispatcher = new ImageServiceDispatcher();
                imageServiceDispatcher.add(new VogelImageService());
                ImageUploadAction.imageService = imageServiceDispatcher;

                
    public class SeiteImageService implements ImageService {
    
        @Override
        public String saveImage(Context ctx, InputStream content, String rawFilename) {
            return _saveImage(ctx.path().startsWith("/s-edit/"),
                    content,
                    rawFilename, 
                    filename -> getUploadFile(ctx.pathParam("id"), filename),
                    filename -> ctx.pathParam("id") + "/img/" + filename);
        }
    
        @Override
        public BinaryData loadImage(Context ctx) {
            var path = ctx.path();
            return _loadImage(
                    ((path.startsWith("/s/") || path.startsWith("/s-edit/")) && path.contains("/img/")),
                    () -> ctx.pathParam("id"),
                    () -> ctx.pathParam("filename"),
                    (id, filename) -> new SeiteService().imgFile(id, filename),
                    (id, filename) -> getUploadFile(id, filename));
        }
    
        private File getUploadFile(String id, String filename) {
            var dn = "upload/seite/" + id + "/" + filename;
            return new File(AmaliaDemoWebapp.workdir, dn);
        }
    }

# Mehrsprachigkeit

`{{ckeditor}}` muss in einer ckedLangs Schleife sein (bspw. mit Laufvar "L").


        if (isPOST()) {
            post(seite);
        } else {
            super.execute2(seite);
            cked.get(SeiteService.LANGS.get(0).toUpperCase(), model);
        }
    
    private void post(Seite seite) {
        int version = Integer.parseInt(ctx.formParam("version"));
        if (seite.getVersion() != version) {
            throw new RuntimeException("Die gespeicherte Version ...");
        }
        SeitePCD pcd = (SeitePCD) new PostContentsService().waitForContents(seite.getId(), version);
        for (String lang : SeiteService.LANGS) {
            seite.getTitel().setString(lang, ctx.formParam("titel" + lang.toUpperCase()));
            seite.getText().setString(lang, CKEditor.filter(pcd.getContent(lang)));
        }
        seite.setVersion(version + 1);
        new SeiteService().save(seite);
    }
    
    // get -> language loop
    @Override
    protected void add(Seite seite, String lang, DataMap map) {
        if (SeiteService.LANGS.get(0).equals(lang)) {
            cked = new CKEditor(createCKEditorModel(map, seite));
        } else {
            CKEditor.putVar(map, lang.toUpperCase(), seite.getText().getString(lang), cked.getModel());
        }
    }
    
    private CKEditorModel createCKEditorModel(DataMap map, Seite seite) { ...

    