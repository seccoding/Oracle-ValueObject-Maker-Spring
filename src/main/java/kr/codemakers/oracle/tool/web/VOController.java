package kr.codemakers.oracle.tool.web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.codemakers.oracle.tool.db.Querys;
import kr.codemakers.oracle.tool.db.helper.DataAccessHelper;
import kr.codemakers.oracle.tool.db.helper.SQLType;
import kr.codemakers.oracle.tool.entity.vo.Column;
import kr.codemakers.oracle.tool.entity.vo.Table;
import kr.codemakers.oracle.tool.utils.StringUtils;

@RestController
public class VOController {

	private DataAccessHelper helper;
	
	@PostMapping(value="/connect", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> connect(@RequestBody Map<String, String> param) {
		this.connectDb(param);
		
		List<Table> tableList = new ArrayList<>();
		this.helper.preparedStatement(Querys.ALL_TABS, pstmt -> {
			pstmt.setString(1, param.get("dbUsername"));
		});
		this.helper.executeQuery(SQLType.SELECT, row -> {
			String tableName = row.getString("TABLE_NAME");
			String comments = row.getString("COMMENTS");
			tableList.add(new Table(tableName, comments));
		});
		this.helper.close();
		
		Map<String, Object> result = new HashMap<>();
		result.put("tables", tableList);
		return result;
	}
	
	@PostMapping(value="/columns", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> columns(@RequestBody Map<String, String> param) {
		this.connectDb(param);
		
		List<Column> columnList = new ArrayList<>();
		this.helper.preparedStatement(Querys.ALL_COLS, pstmt -> {
			pstmt.setString(1, param.get("dbUsername"));
			pstmt.setString(2, param.get("tableName"));
		});
		this.helper.executeQuery(SQLType.SELECT, row -> {
			String columnName = row.getString("COLUMN_NAME");
			String dataType = row.getString("DATA_TYPE");
			String comments = row.getString("COMMENTS");
			String length = row.getString("LENGTH");
			columnList.add(new Column(columnName, dataType, comments, length));
		});
		
		this.helper.close();
		
		Map<String, Object> result = new HashMap<>();
		result.put("columns", columnList);
		return result;
	}
	
	@PostMapping(value="/make", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> make(@RequestBody Map<String, String> param) {
		this.connectDb(param);
		
		Table table = new Table();
		this.helper.preparedStatement(Querys.ONE_TAB, pstmt -> {
			pstmt.setString(1, param.get("dbUsername"));
			pstmt.setString(2, param.get("tableName"));
		});
		this.helper.executeQuery(SQLType.SELECT, row -> {
			String tableName = row.getString("TABLE_NAME");
			String comments = row.getString("COMMENTS");
			table.setTableName(tableName);
			table.setComments(comments);
		});
		
		List<Column> columnList = new ArrayList<>();
		this.helper.preparedStatement(Querys.ALL_COLS, pstmt -> {
			pstmt.setString(1, param.get("dbUsername"));
			pstmt.setString(2, param.get("tableName"));
		});
		this.helper.executeQuery(SQLType.SELECT, row -> {
			String columnName = row.getString("COLUMN_NAME");
			String dataType = row.getString("DATA_TYPE");
			String comments = row.getString("COMMENTS");
			String length = row.getString("LENGTH");
			columnList.add(new Column(columnName, dataType, comments, length));
		});
		this.helper.close();
		
		String path = param.get("workspace") + "/src/main/java/";
		String voPath = path + param.get("package").replace(".", "/") + "/vo";
		
		if (param.get("makePackages").equals("Y")) {
			String webPath = path + param.get("package").replace(".", "/") + "/web";
			String servicePath = path + param.get("package").replace(".", "/") + "/service/impl";
			String daoPath = path + param.get("package").replace(".", "/") + "/dao/impl";
			
			File webPackage = new File(webPath);
			File servicePackage = new File(servicePath);
			File daoPackage = new File(daoPath);
			if (!webPackage.exists()) {
				webPackage.mkdirs();
			}
			if (!servicePackage.exists()) {
				servicePackage.mkdirs();
			}
			if (!daoPackage.exists()) {
				daoPackage.mkdirs();
			}
		}
		
		String className = StringUtils.toCamelCase(true, table.getTableName());
		File voFile = new File(voPath, className + "VO.java");
		
		String voContent = getVOJavaFileContent(voFile, table, columnList);
		writeFile(voFile, voContent);
		
		Map<String, Object> result = new HashMap<>();
		result.put("result", voFile.getAbsolutePath());
		return result;
	}
	
	private void connectDb(Map<String, String> param) {
		this.helper = new DataAccessHelper(param.get("dbUrl"), Integer.parseInt(param.get("dbPort")), param.get("dbDatabase"), param.get("dbUsername"), param.get("dbPassword"));
	}
	
	private String getVOJavaFileContent(File voFile, Table table, List<Column> columns) {
		String path = voFile.getParentFile().getAbsolutePath().replace("\\", "/");
		String packagePath = path.substring(path.indexOf("/src/main/java/") + "/src/main/java/".length()).replace("/", ".");

		String className = StringUtils.toCamelCase(true, table.getTableName());

		StringBuffer java = new StringBuffer();
		java.append("package " + packagePath + ";\n");
		java.append("\n");
		java.append("/**\n");
		java.append(" * @TableName " + table.getTableName() + "\n");
		java.append(" * @TableComment " + table.getComments() + "\n");
		java.append(" */\n");
		java.append("public class " + className +  "VO {\n");
		java.append("\n");

		for (Column column : columns) {
			java.append("    /**\n");
			java.append("     * @ColumnName " + column.getColumnName() + "\n");
			java.append("     * @ColumnType " + column.getDataType() + column.getLength() + "\n");
			java.append("     * @ColumnComment " + column.getComments() + "\n");
			java.append("     */\n");
			java.append("    private " + column.getJdbcType() + " " + StringUtils.toCamelCase(false, column.getColumnName()) + ";\n");
			java.append("\n");
		}

		for (Column column : columns) {
			String jdbcType = column.getJdbcType();
			String columnName = StringUtils.toCamelCase(false, column.getColumnName());
			String setter = "set" + StringUtils.toCamelCase(true, column.getColumnName());
			String getter = "get" + StringUtils.toCamelCase(true, column.getColumnName());

			java.append("    public " + jdbcType + " " + getter + "() {\n");
			java.append("        return this." + columnName + ";\n");
			java.append("    }\n");
			java.append("    \n");
			java.append("    public void " + setter + "(" + jdbcType + " " + columnName + ") {\n");
			java.append("        this." + columnName + " = " + columnName + ";\n");
			java.append("    }\n");
			java.append("    \n");
		}
		
		java.append("    @Override\n");
		java.append("    public String toString() {\n");
		java.append("        return \"" + className + "VO(");
		for (Column column : columns) {
			String columnName = StringUtils.toCamelCase(false, column.getColumnName());
			java.append(columnName + ": \" + " + columnName + " + \", ");
		}
		java.append(")\";\n");
		java.append("    }");

		java.append("\n");
		java.append("}");
		
		return java.toString();
	}

	private void writeFile(File voFile, String content) {
		if (!voFile.getParentFile().exists()) {
			voFile.getParentFile().mkdirs();
		}
		
		FileWriter writer = null;
		try {
			writer = new FileWriter(voFile);
			writer.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
