package kr.codemakers.oracle.tool.web;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.codemakers.oracle.tool.dao.TableDao;
import kr.codemakers.oracle.tool.entity.vo.Column;
import kr.codemakers.oracle.tool.entity.vo.Table;
import kr.codemakers.oracle.tool.utils.FileUtils;
import kr.codemakers.oracle.tool.utils.StringUtils;

@RestController
public class VOController {

	private TableDao tableDao;

	private void createTableDaoInstance(Map<String, String> param) {
		if (this.tableDao == null) {
			this.tableDao = new TableDao(param.get("dbUrl"), Integer.parseInt(param.get("dbPort")),
					param.get("dbDatabase"), param.get("dbUsername"), param.get("dbPassword"));
		}
	}

	@PostMapping(value = "/connect", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> connect(@RequestBody Map<String, String> param) {
		this.createTableDaoInstance(param);

		Map<String, Object> result = new HashMap<>();

		try {
			List<Table> tableList = this.tableDao.getAllTables(param.get("dbUsername"));
			result.put("tables", tableList);
		} catch (RuntimeException e) {
			result.put("error", e.getMessage());
		}
		return result;
	}

	@PostMapping(value = "/columns", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> columns(@RequestBody Map<String, String> param) {
		Map<String, Object> result = new HashMap<>();
		result.put("columns", this.tableDao.getAllColumns(param.get("dbUsername"), param.get("tableName")));
		return result;
	}

	@PostMapping(value = "/make", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> make(@RequestBody Map<String, String> param) {
		Table table = this.tableDao.getOneTable(param.get("dbUsername"), param.get("tableName"));
		List<Column> columnList = this.tableDao.getAllColumns(param.get("dbUsername"), param.get("tableName"));

		String className = StringUtils.toCamelCase(true, table.getTableName());
		
		String path = param.get("workspace") + "/src/main/java/";
		String resourcePath = param.get("workspace") + "/src/main/resources/";
		String voPath = path + param.get("package").replace(".", "/") + "/vo";

		
		if (param.get("makePackages").equals("Y")) {
			String webPath = path + param.get("package").replace(".", "/") + "/web";
			String serviceImplPath = path + param.get("package").replace(".", "/") + "/service/impl";
			String daoImplPath = path + param.get("package").replace(".", "/") + "/dao/impl";
			String daoImplMapperPath = resourcePath + param.get("package").replace(".", "/") + "/dao/impl/mapper";
			
			String servicePath = path + param.get("package").replace(".", "/") + "/service";
			String daoPath = path + param.get("package").replace(".", "/") + "/dao";

			FileUtils.mkdirs(webPath);
			FileUtils.mkdirs(serviceImplPath);
			FileUtils.mkdirs(daoImplPath);
			FileUtils.mkdirs(daoImplMapperPath);
			
			if (param.get("classPrefix").trim().length() > 0) {
				String prefix = StringUtils.toClassName(param.get("classPrefix").trim());
				
				File controllerFile = new File(webPath, prefix + "Controller.java");
				String controllerContent = FileUtils.makeControllerContent(controllerFile, prefix);
				FileUtils.write(controllerFile, controllerContent);
				
				File serviceFile = new File(servicePath, prefix + "Service.java");
				String serviceContent = FileUtils.makeServiceContent(serviceFile, prefix);
				FileUtils.write(serviceFile, serviceContent);
				
				File serviceImplFile = new File(serviceImplPath, prefix + "ServiceImpl.java");
				String serviceImplContent = FileUtils.makeServiceImplContent(serviceImplFile, prefix);
				FileUtils.write(serviceImplFile, serviceImplContent);
				
				File daoFile = new File(daoPath, prefix + "Dao.java");
				String daoContent = FileUtils.makeDaoContent(daoFile, prefix);
				FileUtils.write(daoFile, daoContent);
				
				File daoImplFile = new File(daoImplPath, prefix + "DaoImpl.java");
				String daoImplContent = FileUtils.makeDaoImplContent(daoImplFile, prefix);
				FileUtils.write(daoImplFile, daoImplContent);
				
				File daoImplMapperFile = new File(daoImplMapperPath, prefix + "DaoImplMapper.xml");
				String daoImplMapperContent = FileUtils.makeDaoImplMapperContent(daoImplPath + "/" + prefix + "DaoImpl");
				FileUtils.write(daoImplMapperFile, daoImplMapperContent);
			}
		}

		File voFile = new File(voPath, className + "VO.java");

		String voContent = FileUtils.makeVoContent(voFile, table, columnList);
		FileUtils.write(voFile, voContent);

		Map<String, Object> result = new HashMap<>();
		result.put("result", voFile.getAbsolutePath());
		return result;
	}

}
