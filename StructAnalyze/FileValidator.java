import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileValidator {
	
	static final int INVALID = -1;
	static final int HTML = 0;
	static final int DOC = 1;
	static final int PDF = 2;
	
	static final String HTML_REGEX = ".*\\.(html|htl)";
	static final String DOC_REGEX = ".*\\.(doc|docx)";
	static final String PDF_REGEX = ".*\\.(PDF|pdf)";
	
	static int validate(String filename){
		if(valiHtml(filename))
			return HTML;
		if(valiDOC(filename))
			return DOC;
		if(valiPDF(filename))
			return PDF;
		return INVALID;
	}
	
	static boolean valiHtml(String filename){
		Pattern pattern = Pattern.compile(HTML_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();
	}
	
	static boolean valiDOC(String filename){
		Pattern pattern = Pattern.compile(DOC_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();
	}
	
	static boolean valiPDF(String filename){
		Pattern pattern = Pattern.compile(PDF_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();
	}
	
}
