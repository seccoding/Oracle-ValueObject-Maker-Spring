package kr.codemakers.oracle.tool.entity.vo;

public class Column {

	private String columnName;
	private String dataType;
	private String comments;
	private String length;
	
	private String jdbcType;
	
	public Column(String columnName, String dataType, String comments, String length) {
		this.columnName = columnName;
		this.dataType = dataType;
		this.comments = comments;
		this.length = length;
		
		if (dataType.equalsIgnoreCase("VARCHAR")
				|| dataType.equalsIgnoreCase("VARCHAR2")
				|| dataType.equalsIgnoreCase("CHAR")
				|| dataType.equalsIgnoreCase("CLOB")
				|| dataType.equalsIgnoreCase("DATE")
				|| dataType.equalsIgnoreCase("TIMESTAMP")) {
			this.jdbcType = "String";
		}
		else if (dataType.equalsIgnoreCase("NUMBER")) {
			this.jdbcType = "int";
		}
		else {
			this.jdbcType = "int";
		}
	}

	public String getColumnName() {
		return columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public String getComments() {
		return comments;
	}
	
	public String getLength() {
		if (length == null) return "";
		return length;
	}
	
	public String getJdbcType() {
		return jdbcType;
	}

}
