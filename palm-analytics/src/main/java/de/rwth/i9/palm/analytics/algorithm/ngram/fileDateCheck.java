package de.rwth.i9.palm.analytics.algorithm.ngram;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

public class fileDateCheck {

	public boolean checkPath(String path) throws IOException {
		File pathcheck = new File(path);
		Boolean isValid = false;
		
		if (pathcheck.isFile() || pathcheck.isDirectory())
			isValid = true;
		return isValid;
	}
	
	
	public Boolean createNewModel(String path) throws IOException{
		Boolean create = false;
		long maxTime = 40*24*3600*1000L;
		if (checkPath(path)){
			if (!((long) (System.currentTimeMillis() - Files.readAttributes((new File(path)).toPath(), BasicFileAttributes.class).lastModifiedTime().to(TimeUnit.MILLISECONDS)) < maxTime)){
				create = true;
			}
		}else{
			create = true;
		}
		return create;
	}
	
}
