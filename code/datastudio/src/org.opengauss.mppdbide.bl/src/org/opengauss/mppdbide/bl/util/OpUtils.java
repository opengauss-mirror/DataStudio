package org.opengauss.mppdbide.bl.util;

import java.util.HashMap;
import java.util.List;

/**
 * 
 * Title: class
 * 
 * Description: The Class OpUtils.
 * 
 * @since 3.0.0
 */
public class OpUtils {
	private static boolean from = false;
	private static HashMap<String, List<String>> map = null;
	private static String pre = null;
	
	public OpUtils() {
		
	}
	
	public static void setFrom(boolean from) {
	    OpUtils.from = from;
	}
	
	public static boolean getFrom() {
        return from;
	}
	
	public static void setPre(String pre) {
	    OpUtils.pre = pre;
	}
	
	public static String getPre() {
	    return pre;
	}
	
	public static void setMap(HashMap<String, List<String>> map) {
	    OpUtils.map = map;
	}
	
	public static HashMap<String, List<String>> getMap() {
	    return map;
	}
}
