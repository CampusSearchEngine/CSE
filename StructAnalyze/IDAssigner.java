import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


public class IDAssigner {
	HashMap<String, Integer> IDMap;
	
	public IDAssigner() {
	}
	
	/*
	 * assign unique ID for every valid URI (html doc pdf ¡£¡£¡££©
	 * */
	public HashMap<String, Integer> assignIDForMirror(String MirrorPath) {
		IDMap = new HashMap<String, Integer>();
		int maxID = -1;
		
		DirIter iter = new DirIter(MirrorPath);
		while(iter.hasNext()){
			String URI = iter.next().replaceAll(MirrorPath+"\\\\", "");
			IDMap.put(URI, ++maxID);
		}
		return IDMap;
	}
	
	/*
	 * write every line as "URI-->ID-->type" for each URI into specified path
	 * */
	public void writeIDMap(String path) {
		try {
			File file = new File(path);
			FileOutputStream fStream = new FileOutputStream(file);
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fStream, "utf-8");
			BufferedWriter bWriter = new BufferedWriter(outputStreamWriter);
			
			Iterator<Entry<String, Integer>> iterator = IDMap.entrySet().iterator();
			int count = 0;
			while (iterator.hasNext()) {
				if(count++ % 10000 == 0)
					System.out.println(count + "/" + IDMap.size() + " Entires Written");
				Entry<String, Integer> entry = iterator.next();
				int type = FileValidator.validate(entry.getKey());
				if(type != FileValidator.INVALID && type != FileValidator.WDOC)
					bWriter.write(entry.getKey() + "-->" + entry.getValue() + "-->" + type + "\n");
			}
			bWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * usage : <mirrorPath> <output>
	 * */
	public static void main(String[] args){
		IDAssigner idAssigner = new IDAssigner();
		idAssigner.assignIDForMirror(args[0]);
		idAssigner.writeIDMap(args[1]);
	}
}
