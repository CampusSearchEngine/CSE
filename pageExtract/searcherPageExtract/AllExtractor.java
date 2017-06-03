package searcherPageExtract;

import java.io.BufferedReader;
import java.io.File;
import java.util.HashMap;

import bsh.commands.dir;
import searcherStruct.FileValidator;
import searcherUtils.DirIter;
import searcherUtils.IO;

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
		this.IDMap = new HashMap<String,Integer>();
	}
	
	public void readIDMap(String mapFile) {
		System.out.println("Reading IDMap...");
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
		System.out.println("IDMap read, with " + IDMap.size() + " items");
	}
	
	/*
	 * extract files in mirrorPath using corresponding extractor
	 * */
	public void doExtract() {
		DirIter dirIter = new DirIter(mirrorPath);
		File mirrorDir = new File(mirrorPath);		// get the exact dir name of mirrorPath
		
		int count = 0;
		while(dirIter.hasNext()){
			count ++;
			if(count % 1000 == 0)
				System.out.println(count + "/" + dirIter.getCount() + " DOCs Extracted");
			String filename = dirIter.next();
			//System.out.println(filename + " " + FileValidator.validate(filename));
			String URI = filename.substring(mirrorPath.length()+1);
			String output = "extracted\\" + URI ;
			// Get id of this page, if null(this page is unwanted type) continue
			Integer idObj = IDMap.get(URI);
			int id = 0;
			if(idObj != null)
				id = idObj.intValue();
			else
			{
				//System.out.println(filename);
				//System.out.println(URI);
				continue;
			}
				
				
			//System.out.println(toPath);
			switch (FileValidator.validate(filename)) {
			case FileValidator.HTML:
				output = output.replaceAll(FileValidator.HTML_SUFFIX, WDOC_SUFFIX);
				htmlExtractor.extract(filename, output, "utf-8", id, mirrorPath);
				break;
			case FileValidator.DOC:
				output = output.replaceAll(FileValidator.DOC_SUFFIX, WDOC_SUFFIX);
				docExtractor.extract(filename, output, "utf-8", id, mirrorPath);
				break;
			case FileValidator.PDF:
				output = output.replaceAll(FileValidator.PDF_SUFFIX, WDOC_SUFFIX);
				pdfExtractor.extract(filename, output, "utf-8", id, mirrorPath);
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
