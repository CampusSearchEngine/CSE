import java.io.File;

/*
 * a synthesized extractor
 * */
public class AllExtractor {
	String mirrorPath;
	
	static DOCExtractor docExtractor = new DOCExtractor();
	static HtmlExtractor htmlExtractor = new HtmlExtractor();
	static PDFExtractor pdfExtractor = new PDFExtractor();
	
	final String WDOC_SUFFIX = ".wdoc";
	
	public AllExtractor(String mirrorPath) {
		this.mirrorPath = mirrorPath;
	}
	
	/*
	 * extract files in mirrorPath using corresponding extractor
	 * */
	public void doExtract() {
		DirIter dirIter = new DirIter(mirrorPath);
		File mirrorDir = new File(mirrorPath);		// get the exact dir name of mirrorPath
		mirrorPath = mirrorDir.getName();
		
		while(dirIter.hasNext()){
			String filename = dirIter.next();
			String toPath = filename.replaceFirst(mirrorPath, "extracted");
			
			switch (FileValidator.validate(filename)) {
			case FileValidator.HTML:
				toPath = filename.replaceAll(FileValidator.HTML_SUFFIX, WDOC_SUFFIX);
				htmlExtractor.extract(filename, toPath);
				break;
			case FileValidator.DOC:
				toPath = filename.replaceAll(FileValidator.DOC_SUFFIX, WDOC_SUFFIX);
				docExtractor.extract(filename, toPath);
				break;
			case FileValidator.PDF:
				toPath = filename.replaceAll(FileValidator.PDF_SUFFIX, WDOC_SUFFIX);
				pdfExtractor.extract(filename, toPath);
				break;
			default:
				break;
			}
		}
	}
	
	/*
	 * usage : <mirrorPath>
	 * notice : the mirrorPath's directory name should not contain special character like "." "*" "+" ... 
	 * */
	static public void main(String[] args) {
		AllExtractor allExtractor = new AllExtractor(args[0]);
		allExtractor.doExtract();
	}
}
