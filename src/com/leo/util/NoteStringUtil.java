package com.leo.util;

public class NoteStringUtil {
	
	//从源字符串中删去子字符串并返回修改后的字符串
	public static String deleteSubString(String subString,String sourceString) {
		StringBuffer sb = new StringBuffer(sourceString);
			int index = sourceString.indexOf(subString);
			if(index == -1) {
				return null;
			}
			sb.delete(index, index+subString.length());
			
			sourceString = sb.toString();
			return sourceString;
		}

}
