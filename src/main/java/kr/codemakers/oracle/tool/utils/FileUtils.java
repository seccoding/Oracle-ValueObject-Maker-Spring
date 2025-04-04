package kr.codemakers.oracle.tool.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import kr.codemakers.oracle.tool.entity.vo.Column;
import kr.codemakers.oracle.tool.entity.vo.Table;

public abstract class FileUtils {

	private FileUtils() {
	}

	public static void mkdirs(String directoryPath) {
		File dir = new File(directoryPath);
		mkdirs(dir);
	}

	public static void mkdirs(File directory) {
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public static void write(File voFile, String content) {
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

	public static String makeVoContent(File voFile, Table table, List<Column> columns) {
		String path = voFile.getParentFile().getAbsolutePath().replace("\\", "/");
		String packagePath = path.substring(path.indexOf("/src/main/java/") + "/src/main/java/".length()).replace("/",
				".");

		String className = StringUtils.toCamelCase(true, table.getTableName());

		StringBuffer java = new StringBuffer();
		java.append("package " + packagePath + ";\n");
		java.append("\n");
		java.append("/**\n");
		java.append(" * @TableName " + table.getTableName() + "\n");
		java.append(" * @TableComment " + table.getComments() + "\n");
		java.append(" */\n");
		java.append("public class " + className + "VO {\n");
		java.append("\n");

		for (Column column : columns) {
			java.append("    /**\n");
			java.append("     * @ColumnName " + column.getColumnName() + "\n");
			java.append("     * @ColumnType " + column.getDataType() + column.getLength() + "\n");
			java.append("     * @ColumnComment " + column.getComments() + "\n");
			java.append("     */\n");
			java.append("    private " + column.getJdbcType() + " "
					+ StringUtils.toCamelCase(false, column.getColumnName()) + ";\n");
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

	public static String makeControllerContent(File controllerFile, String prefix) {
		String path = controllerFile.getParentFile().getAbsolutePath().replace("\\", "/");
		String packagePath = path.substring(path.indexOf("/src/main/java/") + "/src/main/java/".length()).replace("/",
				".");

		String className = StringUtils.toClassName(prefix);
		String fieldName = StringUtils.toFieldName(prefix);

		StringBuffer java = new StringBuffer();
		java.append("package " + packagePath + ";\n");
		java.append("\n");
		java.append("import org.springframework.stereotype.Controller;\n");
		java.append("import org.springframework.beans.factory.annotation.Autowired;\n");
		java.append("\n");
		java.append("import " + packagePath.replace(".web", ".service") + "." + className + "Service;\n");
		java.append("\n");
		java.append("@Controller\n");
		java.append("public class " + className + "Controller {\n");
		java.append("\n");
		java.append("    @Autowired\n");
		java.append("    private " + className + "Service " + fieldName + "Service;\n");
		java.append("\n");
		java.append("}");

		return java.toString();
	}

	public static String makeServiceContent(File serviceFile, String prefix) {
		String path = serviceFile.getParentFile().getAbsolutePath().replace("\\", "/");
		String packagePath = path.substring(path.indexOf("/src/main/java/") + "/src/main/java/".length()).replace("/",
				".");

		String className = StringUtils.toClassName(prefix);

		StringBuffer java = new StringBuffer();
		java.append("package " + packagePath + ";\n");
		java.append("\n");
		java.append("public interface " + className + "Service {\n");
		java.append("\n");
		java.append("}");

		return java.toString();
	}

	public static String makeServiceImplContent(File serviceImplFile, String prefix) {
		String path = serviceImplFile.getParentFile().getAbsolutePath().replace("\\", "/");
		String packagePath = path.substring(path.indexOf("/src/main/java/") + "/src/main/java/".length()).replace("/",
				".");

		String className = StringUtils.toClassName(prefix);
		String fieldName = StringUtils.toFieldName(prefix);

		StringBuffer java = new StringBuffer();
		java.append("package " + packagePath + ";\n");
		java.append("\n");
		java.append("import org.springframework.stereotype.Service;\n");
		java.append("import org.springframework.beans.factory.annotation.Autowired;\n");
		java.append("\n");
		java.append("import " + packagePath.replace(".service.impl", ".dao") + "." + className + "Dao;\n");
		java.append("import " + packagePath.replace(".service.impl", ".service") + "." + className + "Service;\n");
		java.append("\n");
		java.append("@Service\n");
		java.append("public class " + className + "ServiceImpl implements " + className + "Service {\n");
		java.append("\n");
		java.append("    @Autowired\n");
		java.append("    private " + className + "Dao " + fieldName + "Dao;\n");
		java.append("\n");
		java.append("}");

		return java.toString();
	}

	public static String makeDaoContent(File daoFile, String prefix) {
		String path = daoFile.getParentFile().getAbsolutePath().replace("\\", "/");
		String packagePath = path.substring(path.indexOf("/src/main/java/") + "/src/main/java/".length()).replace("/",
				".");

		String className = StringUtils.toClassName(prefix);

		StringBuffer java = new StringBuffer();
		java.append("package " + packagePath + ";\n");
		java.append("\n");
		java.append("public interface " + className + "Dao {\n");
		java.append("\n");
		java.append("}");

		return java.toString();
	}

	public static String makeDaoImplContent(File daoImplFile, String prefix) {
		String path = daoImplFile.getParentFile().getAbsolutePath().replace("\\", "/");
		String packagePath = path.substring(path.indexOf("/src/main/java/") + "/src/main/java/".length()).replace("/",
				".");

		String className = StringUtils.toClassName(prefix);

		StringBuffer java = new StringBuffer();
		java.append("package " + packagePath + ";\n");
		java.append("\n");
		java.append("import org.mybatis.spring.SqlSessionTemplate;\n");
		java.append("import org.mybatis.spring.support.SqlSessionDaoSupport;\n");
		java.append("import org.springframework.beans.factory.annotation.Autowired;\n");
		java.append("import org.springframework.stereotype.Repository;\n");
		java.append("\n");
		java.append("import " + packagePath.replace(".dao.impl", ".dao") + "." + className + "Dao;\n");
		java.append("\n");
		java.append("@Repository\n");
		java.append("public class " + className + "DaoImpl extends SqlSessionDaoSupport implements " + className + "Dao {\n");
		java.append("\n");
		java.append("    private final String NAME_SPACE = \"" + packagePath + "." + className + "DaoImpl.\";\n");
		java.append("\n");
		java.append("    @Autowired\n");
		java.append("    @Override\n");
		java.append("    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {\n");
		java.append("        super.setSqlSessionTemplate(sqlSessionTemplate);\n");
		java.append("    }\n");
		java.append("\n");
		java.append("\n");
		java.append("}");

		return java.toString();
	}
	
	public static String makeDaoImplMapperContent(String namespace) {
		String path = namespace.replace("\\", "/");
		String packagePath = path.substring(path.indexOf("/src/main/java/") + "/src/main/java/".length()).replace("/",
				".");
		
		StringBuffer mapper = new StringBuffer();
		mapper.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		mapper.append("<!DOCTYPE mapper\n");
		mapper.append("  PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\"\n");
		mapper.append("  \"https://mybatis.org/dtd/mybatis-3-mapper.dtd\">\n");
		mapper.append("<mapper namespace=\""+packagePath+"\">\n");
		mapper.append("    \n");
		mapper.append("</mapper>");
		return mapper.toString();
	}
}
