package github.soltaufintel.amalia.web.table;

import com.github.template72.compiler.CompiledTemplate;

public class Col {
    /** null or empty: sort by column content (but be aware if there's HTML as content!) */
    private final String sortkey;
    private final ColSort sort;
    private final String headerCSS;
    private final String headerHTML;
    private final String rowCSS;
    private final String rowHTML;
    private final ColAlign align;
    private final boolean remove;
    CompiledTemplate template;

    /**
     * Common Col constructor
     * @param headerHTML usually just the column title
     * @param rowHTML something like "{{i.FIELDNAME}}" where FIELDNAME varies. But it could be more complex HTML, e.g. a link.
     */
    public Col(String headerHTML, String rowHTML) {
        this(false, null, ColSort.NONE, "", headerHTML, "", rowHTML, ColAlign.LEFT);
    }

    /**
     * Col constructor with 2nd argument rowCSS
     * @param headerHTML usually just the column title
     * @param rowCSS CSS class for the cell in a row; can be empty
     * @param rowHTML something like "{{i.FIELDNAME}}" where FIELDNAME varies. But it could be more complex HTML, e.g. a link.
     */
    public Col(String headerHTML, String rowCSS, String rowHTML) {
        this(false, null, ColSort.NONE, "", headerHTML, rowCSS, rowHTML, ColAlign.LEFT);
    }
    
    /**
     * Col constructor with 3rd argument rowCSS and 1st argument headerCSS
     * @param headerCSS CSS class for the cell in the header row; can be empty
     * @param headerHTML usually just the column title
     * @param rowCSS CSS class for the cell in a row; can be empty
     * @param rowHTML something like "{{i.FIELDNAME}}" where FIELDNAME varies. But it could be more complex HTML, e.g. a link.
     */
    public Col(String headerCSS, String headerHTML, String rowCSS, String rowHTML) {
        this(false, null, ColSort.NONE, headerCSS, headerHTML, rowCSS, rowHTML, ColAlign.LEFT);
    }

    protected Col(boolean remove, String sortkey, ColSort sort, String headerCSS, String headerHTML, String rowCSS,
            String rowHTML, ColAlign align) {
        this.remove = remove;
        this.sortkey = sortkey;
        this.sort = sort;
        this.headerCSS = headerCSS;
        this.headerHTML = headerHTML;
        this.rowCSS = rowCSS;
        this.rowHTML = rowHTML;
        this.align = align;
    }
    
    /**
     * Convenience method for creating a Col with run var "i".
     * @param header column title (can be HTML)
     * @param fieldname just the fieldname - makes rowHTML = "{{i.FIELDNAME}}"
     * @return new Col
     */
    public static Col i(String header, String fieldname) {
        return new Col(header, "{{i." + fieldname + "}}");
    }

    /**
     * Convenience method for creating a Col with run var "i". Col is a link.
     * @param header column title (can be HTML)
     * @param fieldname just the fieldname - makes rowHTML = "<a href=LINK>{{i.FIELDNAME}}</a>"
     * @param link -
     * @return new Col
     */
    public static Col i(String header, String fieldname, String link) {
        return new Col(header, "<a href=\"" + link + "\">{{i." + fieldname + "}}</a>").sortable(fieldname);
    }

    /**
     * Convenience method for creating a sortable Col with run var "i".
     * @param header column title (can be HTML)
     * @param fieldname just the fieldname - makes rowHTML = "{{i.FIELDNAME}}"
     * @return new Col, sorted by fieldname
     */
    public static Col si(String header, String fieldname) {
        return i(header, fieldname).sortable(fieldname);
    }

    /**
     * Convenience method for creating a Col with run var "i". Col is a link and is sortable.
     * @param header column title (can be HTML)
     * @param fieldname just the fieldname - makes rowHTML = "<a href=LINK>{{i.FIELDNAME}}</a>"
     * @param link -
     * @return new Col, sorted by fieldname
     */
    public static Col si(String header, String fieldname, String link) {
        return i(header, fieldname, link).sortable(fieldname);
    }

    public Col sortable(String sortkey) {
        return new Col(remove, sortkey, ColSort.ASC_DESC, headerCSS, headerHTML, rowCSS, rowHTML, align);
    }

    public Col asc(String sortkey) {
        return new Col(remove, sortkey, ColSort.ASC, headerCSS, headerHTML, rowCSS, rowHTML, align);
    }

    public Col desc(String sortkey) {
        return new Col(remove, sortkey, ColSort.DESC, headerCSS, headerHTML, rowCSS, rowHTML, align);
    }

    public Col right() {
        return new Col(remove, sortkey, sort, headerCSS, headerHTML, rowCSS, rowHTML, ColAlign.RIGHT);
    }

    /**
     * Must be called before Col is added to Cols.
     * @param condition true: this column will be removed
     * @return change Col
     */
    public Col remove(boolean condition) {
        return new Col(condition, sortkey, sort, headerCSS, headerHTML, rowCSS, rowHTML, align);
    }

    public String getSortkey() {
        return sortkey;
    }

    public ColSort getSort() {
        return sort;
    }

    public String getHeaderCSS() {
        return headerCSS;
    }

    public String getHeaderHTML() {
        return headerHTML;
    }

    public String getRowCSS() {
        return rowCSS;
    }

    public String getRowHTML() {
        return rowHTML;
    }

    public ColAlign getAlign() {
        return align;
    }
    
    public boolean isRemove() {
        return remove;
    }
    
    public enum ColAlign {
        LEFT, // default
        // CENTER,
        RIGHT;
    }
    
    /**
     * Sort order for column
     */
    public enum ColSort {
        NONE,
        ASC,
        DESC,
        ASC_DESC;
    }
}
