package github.soltaufintel.amalia.web.action;

import github.soltaufintel.amalia.spark.Context;

public class PageInitializer { // TODO Eigentlich könnte das doch ein Interface sein.
    
    public void initPage(Context ctx, Page page) {
        page.put("title", "Web app");
    }
}
