package kr.codemakers.oracle.tool.db;

public interface Querys {

	String ALL_TABS = " SELECT ATC.TABLE_NAME "
					+ "      , ATC.COMMENTS "
					+ "   FROM ALL_TAB_COMMENTS ATC "
					+ "  INNER JOIN ALL_TABLES AT "
					+ "     ON ATC.OWNER = AT.OWNER "
					+ "    AND ATC.TABLE_NAME = AT.TABLE_NAME "
					+ "  WHERE ATC.OWNER = ? ";
	
	String ONE_TAB = " SELECT ATC.TABLE_NAME "
					+ "      , ATC.COMMENTS "
					+ "   FROM ALL_TAB_COMMENTS ATC "
					+ "  INNER JOIN ALL_TABLES AT "
					+ "     ON ATC.OWNER = AT.OWNER "
					+ "    AND ATC.TABLE_NAME = AT.TABLE_NAME "
					+ "  WHERE ATC.OWNER = ? "
					+ "    AND ATC.TABLE_NAME = ? ";
	
	String ALL_COLS = " SELECT ATC.COLUMN_NAME"
					+ "      , ATC.DATA_TYPE"
					+ "      , ACC.COMMENTS "
					+ "      , CASE "
					+ "         WHEN ATC.DATA_TYPE LIKE '%CHAR%' THEN '(' || ATC.CHAR_LENGTH || ')' "
					+ "         WHEN ATC.DATA_TYPE LIKE '%NUMBER%' THEN '(' || ATC.DATA_PRECISION || ', ' || ATC.DATA_SCALE || ')' "
					+ "         ELSE '' "
					+ "        END AS LENGTH "
					+ "   FROM ALL_TAB_COLUMNS ATC "
					+ "  INNER JOIN ALL_COL_COMMENTS ACC "
					+ "     ON ATC.OWNER = ACC.OWNER "
					+ "    AND ATC.TABLE_NAME = ACC.TABLE_NAME "
					+ "    AND ATC.COLUMN_NAME = ACC.COLUMN_NAME "
					+ "  WHERE ATC.OWNER = ? "
					+ "    AND ATC.TABLE_NAME = ? "
					+ "  ORDER BY ATC.COLUMN_ID ";
	
}

