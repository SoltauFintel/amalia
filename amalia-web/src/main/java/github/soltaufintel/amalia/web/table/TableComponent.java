package github.soltaufintel.amalia.web.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.github.template72.compiler.CompiledTemplate;
import com.github.template72.compiler.TemplateCompiler;
import com.github.template72.compiler.TemplateCompilerBuilder;
import com.github.template72.data.DataList;
import com.github.template72.data.DataMap;
import com.github.template72.data.IDataItem;
import com.github.template72.data.IDataList;
import com.github.template72.data.IDataMap;

import github.soltaufintel.amalia.spark.Context;
import github.soltaufintel.amalia.web.action.Action;

/**
 * Using the TableComponent the use gets the ability to sort columns without reloading the entire page.
 * This is achieved using HTMX calls that replace only the table HTML.
 */
public class TableComponent extends Action {
	protected final TemplateCompiler compiler = new TemplateCompilerBuilder().build();
	protected final String tableCSS;
	protected final List<Col> cols;
	protected final DataMap model;
	protected final String listName;
	protected final String sortlink;
	protected final String sid;
	protected String runVarName = "i";
	protected int sortedColumn = -1;
	protected boolean asc = false;
	protected String html;
	protected String rowClass;
	// Sort rows by drag&drop
	protected boolean rowDragDrop = false;
	protected String rowDragDropCSS = "sortable";
	protected String rowDragDropIdFieldname = "id";

	public TableComponent(List<Col> cols, DataMap model, String listName) {
		this("", cols, model, listName);
	}

	/**
	 * @param tableCSS CSS classes for the table. "!" at begin to replace the default CSS classes
	 * @param cols -
	 * @param model -
	 * @param listName name of the list in model that contains data for all rows
	 */
	public TableComponent(String tableCSS, List<Col> cols, DataMap model, String listName) {
		this.tableCSS = tableCSS;
		this.cols = cols;
		this.model = model;
		this.listName = listName;
		sortlink = TableSortAction.register(this);
		sid = sortlink.replace("/", "");
	}
	
	@Override
	public void init(Context ctx) {
	    cols.forEach(col -> col.template = compiler.compile(col.getRowHTML()));
	    super.init(ctx);
	}

	@Override
	protected void execute() {
		for (Col col : cols) {
			if (col.isRemove()) {
				System.err.println("warning: there is a TableComponent Col \"" + col.getHeaderHTML() +
						"\" with isRemove()=true! You should use Cols and call remove() before adding it to Cols.");
			}
		}
		StringBuilder sb = new StringBuilder();
		if (tableCSS.startsWith("!")) {
            sb.append("<table class=\"" + tableCSS.substring(1) + " " + sid + "\">");
		} else if (rowDragDrop) {
            sb.append("<table class=\"table mt2 wauto " + tableCSS + " " + sid + "\">");
		} else {
		    sb.append("<table class=\"table table-striped table-hover mt2 " + tableCSS + " " + sid + "\">");
		}
		sb.append("\n<thead><tr>\n");
		sb.append(makeHeader());
        sb.append("\n</tr></thead>");
        sb.append("<tbody"
                + (rowDragDrop ? " class=\"" + rowDragDropCSS + "\" hx-post=\"" + sortlink + "-2\" hx-target=\"#sid" + sid + "\" hx-trigger=\"end\"" : "")
                + ">");
        
		makeRows(sb);
		
		sb.append("\n</tbody></table>\n");
        if (rowDragDrop) {
            html = "<form id=\"sid" + sid + "\">" + sb.toString() + "</form>";
        } else {
            html = sb.toString();
        }
	}

	protected StringBuilder makeHeader() {
		StringBuilder headers = new StringBuilder();
		for (int i = 0; i < cols.size(); i++) {
			Col col = cols.get(i);
			if (ColSort.NONE.equals(col.getSort())) {
				headers.append("\n<th class=\"" + col.getHeaderCSS() + (ColAlign.RIGHT.equals(col.getAlign()) ? " tar" : "") + "\">");
			} else {
				headers.append("\n<th" + (ColAlign.RIGHT.equals(col.getAlign()) ? " class=\"tar\"" : "")
						+ "><a href=\"#\" class=\"sortlink " + col.getHeaderCSS() + "\" hx-get=\"" + sortlink
						+ i + "\" hx-target=\"." + sid + "\" hx-swap=\"outerHTML\">");
			}
			String content = compiler.compile(col.getHeaderHTML()).render(model);
			headers.append(content);
			String sortIcon = "";
			if (!ColSort.NONE.equals(col.getSort()) && i == sortedColumn) {
				sortIcon = asc ? "fa-arrow-down" : "fa-arrow-up";
			}
			headers.append("<i class=\"fa " + (ColAlign.RIGHT.equals(col.getAlign()) ? "" : "fa-fw ") + sortIcon
					+ " sortarrow\"></i>");
			if (!ColSort.NONE.equals(col.getSort())) {
				headers.append("</a>");
			}
			headers.append("</th>");
		}
		return headers;
	}

	protected void makeRows(StringBuilder rows) {
		final String r = rowClass != null && !rowClass.isBlank() ? rowClass : null;
		Stream<IDataMap> stream = model.getList(listName).stream();
		Comparator<IDataMap> comparator = comparator();
		if (comparator != null) {
			stream = stream.sorted(comparator);
		}
		stream.forEach(map -> {
			boolean hasRowClass = false;
			if (r != null) {
				IDataItem rcdi = map.get(r);
				if (rcdi != null) {
					String rc = rcdi.toString();
					if (rc != null && !rc.isEmpty()) {
						rows.append("\n\t<tr ");
						rows.append(rc);
						rows.append(">");
						hasRowClass = true;
					}
				}
			}
			if (!hasRowClass) {
				rows.append("\n\t<tr>");
			}
			model.put(runVarName, map);
			cols.forEach(col -> {
				String content = col.template.render(model);
				rows.append("\n\t\t<td class=\"" + col.getRowCSS() + col.getHeaderCSS()
						+ (ColAlign.RIGHT.equals(col.getAlign()) ? " tar" : "") + "\">" + content + "</td>");
			});
			rows.append("\n\t</tr>");
		});
	}

    public void sortRowsByDragAndDrop(List<String> newOrder) {
        if (rowDragDropIdFieldname == null) {
            return;
        }
        IDataList list = (DataList) model.getList(listName);
        List<IDataMap> neu = new ArrayList<>();
        for (int i = 0; i < newOrder.size(); i++) {
            IDataMap reihe = find(list, newOrder.get(i));
            if (reihe == null) { // unexpected error -> abort
                return;
            }
            neu.add(reihe);
        }
        list.clear();
        list.addAll(neu);

        Map<String, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < newOrder.size(); i++) {
            indexMap.put(newOrder.get(i), i);
        }
        saveSortedRows(newOrder, indexMap);
    }

	private IDataMap find(IDataList rows, String id) {
	    for (IDataMap row : rows) {
            if (row.get(rowDragDropIdFieldname).toString().equals(id)) {
                return row;
            }
        }
        return null;
    }

    /**
     * Use this for sorting your data: <br>
     * <code>.sort(Comparator.comparingInt(i -> indexMap.getOrDefault(i.getId(), -1)));</code>
     * @param newOrder ID list with new sort order
     * @param indexMap key: ID, value: position
     */
    public void saveSortedRows(List<String> newOrder, Map<String, Integer> indexMap) {
    }
	
	public TableComponent withRowDragDrop() {
	    rowDragDrop = true;
	    cols.add(0, new Col("", "<i class=\"fa fa-fw fa-arrows-v handle\" title=\"Sortieren\"></i>"
            + "<input type=\"hidden\" name=\"" + TableSortAction.item + "\" value=\"{{i.id}}\"/>"));
	    return this;
	}
	
	public TableComponent withRowDragDrop(String css, String idFieldname, String title) {
	    if (css == null || idFieldname == null || title == null) {
	        throw new IllegalArgumentException("Parameters must not be null!");
	    }
        rowDragDrop = true;
        cols.add(0, new Col("", "<i class=\"fa fa-fw fa-arrows-v handle\" title=\"" + title + "\"></i>"
            + "<input type=\"hidden\" name=\"" + TableSortAction.item + "\" value=\"{{i." + idFieldname + "}}\"/>"));
        rowDragDropCSS = css;
        rowDragDropIdFieldname = idFieldname;
        return this;
	}
	
    @Override
	protected String render() {
		String ret = html;
		html = null;
		return ret;
	}
	
	public static class Cols extends ArrayList<Col> {
		
		public static Cols of(Col ...cols) {
			Cols ret = new Cols();
			for (Col i : cols) {
				if (!i.isRemove()) {
					ret.add(i);
				}
			}
			return ret;
		}
	}
	
	public static class Col {
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

	public TableComponent sort(int col) {
		if (col >= 0 && col < cols.size()) {
			switch (cols.get(col).getSort()) {
				case NONE:
					sortedColumn = -1;
					return this;
				case DESC:
					asc = false;
					break;
				case ASC:
					asc = true;
					break;
				case ASC_DESC:
					if (sortedColumn == col) { // ASC <-> DESC
						asc = !asc;
					} else {
						asc = true;
					}
					break;
			}
			sortedColumn = col;
		} else if (col == -1) {
			sortedColumn = col;
		}
		return this;
	}
	
	public TableComponent sortDesc(int col) {
	    return sort(col).sort(col); 
	}
	
	protected Comparator<IDataMap> comparator() {
		if (sortedColumn < 0) {
			return null;
		}
		Col col = cols.get(sortedColumn);
		String sortkey = col.getSortkey();
		if (sortkey == null || sortkey.isBlank()) {
			sortkey = "__sort";
			makeSortValues(col, sortkey);
		}
		return getComparator(sortkey, asc);
	}
	
	protected void makeSortValues(Col col, String sortkey) {
		model.getList(listName).forEach(map -> { 
			model.put(runVarName, map);
			String content = col.template.render(model);
			model.put(sortkey, umlaute(content));
		});
	}

    public static String umlaute(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase()
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue")
            .replace("ß", "ss");
    }

	protected Comparator<IDataMap> getComparator(String sortkey, boolean asc) {
		return (a, b) -> (asc ? 1 : -1) * a.get(sortkey).toString().compareToIgnoreCase(b.get(sortkey).toString());
	}

	public String getRunVarName() {
		return runVarName;
	}

	/**
	 * @param runVarName default value is "i"
	 * @return this
	 */
	public TableComponent setRunVarName(String runVarName) {
		this.runVarName = runVarName;
		return this;
	}

	public String getRowClass() {
		return rowClass;
	}

	/**
	 * @param rowClass e.g. class="highlight", null for no rowClass
	 * @return this
	 */
	public TableComponent setRowClass(String rowClass) {
		this.rowClass = rowClass;
		return this;
	}
}
