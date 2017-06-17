package NgramsTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class fileCreationDateCheck {
	
	
	public static void main(String[] args) throws IOException {
		
		String d = "C:/Users/Administrator/Desktops";
		String f = "C:/Users/Administrator/Desktop/Test.txt";
		
		// path checker
		if (checkPath(d))
		{
			System.out.println("Path is Valid");
		}
		else
			System.out.println("Path not Valid");
		
		// get the actual date 
		System.out.println("Actual Time: " +  LocalDateTime.now());
		
		// difference of the two
		// System.out.println(System.currentTimeMillis() - Files.readAttributes((new File(d)).toPath(), BasicFileAttributes.class).lastModifiedTime().to(TimeUnit.MILLISECONDS));
		
		if (createNewModel(f))
			System.out.println("To be created");
		else
			System.out.println("Still fine");
	}

	private static boolean checkPath(String path) throws IOException {
		File pathcheck = new File(path);
		Boolean isValid = false;
		
		if (pathcheck.isFile() || pathcheck.isDirectory())
			isValid = true;
		return isValid;
	}
	
	
	static Boolean createNewModel(String path) throws IOException{
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
