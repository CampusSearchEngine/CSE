import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.archive.util.Iteratorable;

/*
 * an iterator to iter every file in the  rootpath
 * */
public class DirIter extends Iteratorable {
	
	String rootPath;
	Vector<String> files;
	int pointer;
	
	public DirIter(String rootPath) {
		super(null);
		this.rootPath = rootPath;
		this.files = new Vector<String>();
		this.pointer = -1;
		
		init();
	}
	
	String next(){
		return files.elementAt(pointer++);
	}
	
	boolean hasNext(){
		return pointer != files.size(); 
	}
	
	void reset(){
		pointer = 0;
	}
	
	void init(){
		readPath(rootPath);
		pointer = 0;
	}
	
	void readPath(String path){
		try {
			File rfile = new File(path);
			File[] fileList = rfile.listFiles();
			
			for(File file : fileList){
				if(file.isDirectory()){			//recursively read from dir
					readPath(file.getPath());
				}
				else if(file.isFile()){
					files.addElement(file.getPath());
				}
			}
			
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
