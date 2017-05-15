import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;

import bsh.commands.dir;

/*
 * a synthesized extractor
 * */
public class AllExtractor {
	String mirrorPath;
	
	static DOCExtractor docExtractor = new DOCExtractor();
	static HtmlExtractor htmlExtractor = new HtmlExtractor();
	static PDFExtractor pdfExtractor = new PDFExtractor();
	
	final String WDOC_SUFFIX = ".wdoc";
	
	HashMap<String, Integer> IDMap;
	
	public AllExtractor(String mirrorPath) {
		this.mirrorPath = mirrorPath;
	}
	
	public void readIDMap(String mapFile) {
		try {
			BufferedReader bReader = IO.getReader(mapFile);
			String line;

			while ((line = bReader.readLine()) != null) {
				String[] args = line.split("-->");
				String URI = args[0];
				int ID = Integer.parseInt(args[1]);
				IDMap.put(URI, ID);
			}
			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * extract files in mirrorPath using corresponding extractor
	 * */
	public void doExtract() {
		DirIter dirIter = new DirIter(mirrorPath);
		File mirrorDir = new File(mirrorPath);		// get the exact dir name of mirrorPath
		mirrorPath = mirrorDir.getName();
		
		int count = 0;
		while(dirIter.hasNext()){
			count ++;
			if(count % 10000 == 0)
				System.out.println(count + "/" + dirIter.getCount() + " DOCs Extracted");
			String filename = dirIter.next();
			//System.out.println(filename + " " + FileValidator.validate(filename));
			String URI = filename.replaceFirst(mirrorPath, "extracted");
			int id = IDMap.get(URI);
			//System.out.println(toPath);
			switch (FileValidator.validate(filename)) {
			case FileValidator.HTML:
				URI = URI.replaceAll(FileValidator.HTML_SUFFIX, WDOC_SUFFIX);
				htmlExtractor.extract(filename, URI, "utf-8", id);
				break;
			case FileValidator.DOC:
				URI = URI.replaceAll(FileValidator.DOC_SUFFIX, WDOC_SUFFIX);
				docExtractor.extract(filename, URI, "utf-8", id);
				break;
			case FileValidator.PDF:
				URI = URI.replaceAll(FileValidator.PDF_SUFFIX, WDOC_SUFFIX);
				pdfExtractor.extract(filename, URI, "utf-8", id);
				break;
			default:
				break;
			}
		}
	}
	
	/*
	 * usage :  <mirrorPath> <IDMapFile>
	 * notice : the mirrorPath's directory name should not contain special character like "." "*" "+" ... 
	 * */
	static public void main(String[] args) {
		AllExtractor allExtractor = new AllExtractor(args[0]);
		allExtractor.readIDMap(args[1]);
		allExtractor.doExtract();
	}
}
