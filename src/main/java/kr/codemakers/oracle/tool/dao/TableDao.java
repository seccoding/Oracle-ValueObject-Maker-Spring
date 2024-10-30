package kr.codemakers.oracle.tool.dao;

import java.util.ArrayList;
import java.util.List;

import kr.codemakers.oracle.tool.db.Querys;
import kr.codemakers.oracle.tool.db.helper.DataAccessHelper;
import kr.codemakers.oracle.tool.db.helper.SQLType;
import kr.codemakers.oracle.tool.entity.vo.Column;
import kr.codemakers.oracle.tool.entity.vo.Table;

public class TableDao {

	private String url;
	private int port;
	private String database;
	private String username;
	private String password;
	
	public TableDao(String url, int port, String database, String username, String password) {
		this.url = url;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	public List<Table> getAllTables(String username) {
		DataAccessHelper helper = new DataAccessHelper(this.url, this.port, this.database, this.username, this.password);
		
		List<Table> tableList = new ArrayList<>();
		helper.preparedStatement(Querys.ALL_TABS, pstmt -> {
			pstmt.setString(1, username);
		});
		helper.executeQuery(SQLType.SELECT, row -> {
			String tableName = row.getString("TABLE_NAME");
			String comments = row.getString("COMMENTS");
			tableList.add(new Table(tableName, comments));
		});
		helper.close();
		
		return tableList;
	}
	
	public Table getOneTable(String username, String tableName) {
		DataAccessHelper helper = new DataAccessHelper(this.url, this.port, this.database, this.username, this.password);
		
		Table table = new Table();
		helper.preparedStatement(Querys.ONE_TAB, pstmt -> {
			pstmt.setString(1, username);
			pstmt.setString(2, tableName);
		});
		helper.executeQuery(SQLType.SELECT, row -> {
			table.setTableName(row.getString("TABLE_NAME"));
			table.setComments(row.getString("COMMENTS"));
		});
		
		helper.close();
		
		return table;
	}
	
	public List<Column> getAllColumns(String username, String tableName) {
		DataAccessHelper helper = new DataAccessHelper(this.url, this.port, this.database, this.username, this.password);
		
		List<Column> columnList = new ArrayList<>();
		helper.preparedStatement(Querys.ALL_COLS, pstmt -> {
			pstmt.setString(1, username);
			pstmt.setString(2, tableName);
		});
		helper.executeQuery(SQLType.SELECT, row -> {
			String columnName = row.getString("COLUMN_NAME");
			String dataType = row.getString("DATA_TYPE");
			String comments = row.getString("COMMENTS");
			String length = row.getString("LENGTH");
			columnList.add(new Column(columnName, dataType, comments, length));
		});
		
		helper.close();
		return columnList;
	}
}
