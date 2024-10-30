package kr.codemakers.oracle.tool.db.helper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface BindingParameter {

	public void bind(PreparedStatement pstmt) throws SQLException;
	
}
