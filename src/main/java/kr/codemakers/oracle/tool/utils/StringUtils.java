package kr.codemakers.oracle.tool.utils;

public abstract class StringUtils {

	private StringUtils() {}
	
	public static String toCamelCase(boolean isFirstUpper, String capitalCase) {
		String camelString = capitalCase.toLowerCase();
		
		while(camelString.indexOf("_") != -1) {
			int underScoreIndex = camelString.indexOf("_");
			
			String pre = camelString.substring(0, underScoreIndex);
			String character = camelString.substring(underScoreIndex + 1, underScoreIndex + 2).toUpperCase();
			String after = camelString.substring(underScoreIndex + 2);
			
			camelString = pre + character + after;
		}
		
		if (isFirstUpper) {
			camelString = camelString.substring(0, 1).toUpperCase() + camelString.substring(1);
		}
		
		return camelString;
	}
	
}
