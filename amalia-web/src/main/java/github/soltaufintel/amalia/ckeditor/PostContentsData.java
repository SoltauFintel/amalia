package github.soltaufintel.amalia.ckeditor;

public abstract class PostContentsData {
    private final String key;
    private final int version;
    private final String content;
    private PostContentsData previous; // einfach verkettete Liste
    
    public PostContentsData(String key, int version, String content) {
        this.key = key;
        this.version = version;
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public int getVersion() {
        return version;
    }

    public String getContent() {
        return content;
    }

    public PostContentsData getPrevious() {
        return previous;
    }

    public void setPrevious(PostContentsData previous) {
        this.previous = previous;
    }
}
