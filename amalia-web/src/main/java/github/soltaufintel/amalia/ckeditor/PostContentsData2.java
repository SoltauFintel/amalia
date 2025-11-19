package github.soltaufintel.amalia.ckeditor;

import github.soltaufintel.amalia.spark.Context;

public class PostContentsData2 extends PostContentsData {

    public PostContentsData2(Context ctx) {
        super(ctx.queryParam("key"), Integer.parseInt(ctx.formParam("version")), ctx.formParam("content"));
    }
}
