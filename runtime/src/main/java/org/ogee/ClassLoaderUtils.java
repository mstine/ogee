package org.ogee;

public class ClassLoaderUtils {
	
	public static String convertPath(String fqcn) {
		String path = fqcn.substring(0, fqcn.lastIndexOf(".")).replace('.', '/');
		String ext = fqcn.substring(fqcn.lastIndexOf("."));
		return path + ext;
	}
	
}
