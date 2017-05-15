import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileValidator {
	
	static final int INVALID = -1;
	static final int HTML = 0;
	static final int DOC = 1;
	static final int PDF = 2;
	static final int WDOC = 3;
	
	static final String HTML_REGEX = ".*\\.html$";
	static final String DOC_REGEX = ".*\\.(doc|docx)$";
	static final String PDF_REGEX = ".*\\.(PDF|pdf)$";
	
	static final String HTML_SUFFIX = "\\.html";
	static final String DOC_SUFFIX = "\\.(doc|docx)";
	static final String PDF_SUFFIX = "\\.(PDF|pdf)";
	static final String WDOC_SUFFIX = "\\.wdoc";
	
	static int validate(String filename){
		if(valiHtml(filename))
			return HTML;
		if(valiDOC(filename))
			return DOC;
		if(valiPDF(filename))
			return PDF;
		if(valiWDOC(filename))
			return WDOC;
		return INVALID;
	}
	
	static boolean valiHtml(String filename){
		/*Pattern pattern = Pattern.compile(HTML_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();*/
		return filename.endsWith(HTML_SUFFIX);
	}
	
	static boolean valiDOC(String filename){
		/*Pattern pattern = Pattern.compile(DOC_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();*/
		return filename.endsWith(DOC_SUFFIX);
	}
	
	static boolean valiPDF(String filename){
		/*Pattern pattern = Pattern.compile(PDF_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();*/
		return filename.endsWith(PDF_SUFFIX);
	}
	
	static boolean valiWDOC(String filename){
		return filename.endsWith(WDOC_SUFFIX);
	}
}
