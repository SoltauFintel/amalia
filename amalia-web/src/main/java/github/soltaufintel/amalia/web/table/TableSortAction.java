package github.soltaufintel.amalia.web.table;

import java.util.HashMap;
import java.util.Map;

import github.soltaufintel.amalia.base.IdGenerator;
import github.soltaufintel.amalia.web.action.Action;

public class TableSortAction extends Action {
	private static final String HANDLE = "HANDLE_TSA";
	public static String path = "/tablesort/";
	private static Map<String, TableComponent> tables = new HashMap<>();
	private String html;
	
	public static String register(TableComponent table) {
		synchronized (HANDLE) {
			String id = IdGenerator.createId6();
			tables.put(id, table);
			// Problem: Map wird immer voller... Man könnte die immer gegen 3 Uhr clearen? Oder nach 3 Stunden?
			return path + id + "/";
		}
	}
	
	@Override
	protected void execute() {
		synchronized (HANDLE) {
			String id = ctx.pathParam("id");
			int col = Integer.parseInt(ctx.pathParam("col"));
			
			TableComponent table = tables.get(id);
			table.sort(col);
			html = table.run();
		}
	}
	
	@Override
	protected String render() {
		return html;
	}
}