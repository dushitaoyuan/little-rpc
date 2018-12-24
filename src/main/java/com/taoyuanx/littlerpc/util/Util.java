package com.taoyuanx.littlerpc.util;

public class Util {
	public static final boolean isEmpty(String str){
		return null==str||str.trim().length()==0;
	}
	public static final boolean isNotEmpty(String str){
		return null!=str&&str.trim().length()>0;
	}
	public static final boolean isNotEmpty(String ...str){
		for(String s:str){
			if(isEmpty(s)){
				return false;
			}
		}
		return true;
	}
	
	/**参照log4j  模板匹配

	 * xx{}xx{} 1,2

	 * @param pattern 

	 * @param objects

	 * @return

	 */
	public static String log4jFormat(String pattern,Object ...objects){
		if(isEmpty(pattern))return null;
		char[] arr = pattern.toCharArray();
		StringBuilder temp=new StringBuilder();
		int count=0,objLen=objects.length,len=arr.length;
		char left="{".charAt(0);
		char right="}".charAt(0);
		for(int i=0;i<len;i++){
			if(count<objLen){
				if(i<len-1&&left==arr[i]&&right==arr[i+1]){
					temp.append(objects[count++]);
					i++;	
				}else{
					temp.append(arr[i]);
				}
			}else{
				temp.append(arr[i]);
			}	
		}
		return temp.toString();
		
	}
	
	public static String join(String joinSymbol,Object ...strs) {
		if(strs==null||strs.length==0) {
			return null;
		}
		if(strs.length==1) {
			return strs[0].toString();
		}
		StringBuilder buf=new StringBuilder();
		for(int i=0,len=strs.length-1;i<len;i++) {
			buf.append(strs[i]).append(joinSymbol);
		}
		buf.append(strs[strs.length-1]);
		return buf.toString();
	}
}
