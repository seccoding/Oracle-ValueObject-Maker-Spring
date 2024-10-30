package kr.codemakers.oracle.tool.entity.vo;

public class Table {

	private String tableName;
	private String comments;

	public Table() {}
	
	public Table(String tableName, String comments) {
		this.tableName = tableName;
		this.comments = comments;
	}

	public String getTableName() {
		return tableName;
	}

	public String getComments() {
		return comments;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return this.tableName + " (" + this.comments + ")";
	}

}
