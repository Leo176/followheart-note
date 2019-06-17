package com.leo.util;

public class NoteStringUtil {
	
	//从源字符串中删去子字符串并返回修改后的字符串(同时一并删去它之前的","(如果有))
	public static String deleteSubString(String subString,String sourceString) {
		StringBuffer sb = new StringBuffer(sourceString);
			int index = sourceString.indexOf(subString);
			if(index == -1) {
				return "";
			}
			sb.delete(index, index+subString.length());
			//删去逗号
			if(index==0&&sourceString.length()>subString.length())
				sb.delete(0,1);
				
			//删去逗号
			if(index>0) {
				sb.delete(index-1, index);
			}
			sourceString = sb.toString();
			return sourceString;
		}

}
