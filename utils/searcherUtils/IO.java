package searcherUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.lucene.document.Field;

import com.sun.org.apache.bcel.internal.generic.RETURN;

public class IO {
	public static BufferedReader getReader(String filename){
		try {
			File file = new File(filename);
			FileInputStream fStream = new FileInputStream(file);
			InputStreamReader iReader = new InputStreamReader(fStream,"utf-8");
			BufferedReader bReader = new BufferedReader(iReader);
			return bReader;
		} catch (Exception e) {
			e.printStackTrace();
		}
	return null;
	}
	
	public static BufferedWriter getWriter(String filename){
		try {
			File file = new File(filename);
			if(file.getParentFile() != null && !file.getParentFile().exists())
				file.getParentFile().mkdirs();
			FileOutputStream fStream = new FileOutputStream(file);
			OutputStreamWriter iWriter = new OutputStreamWriter(fStream,"utf-8");
			BufferedWriter bWriter = new BufferedWriter(iWriter);
			return bWriter;
		} catch (Exception e) {
			e.printStackTrace();
		}
	return null;
	}
}
