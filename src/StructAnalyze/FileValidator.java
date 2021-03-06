package StructAnalyze;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileValidator {
	
	public static final int INVALID = -1;
	public static final int HTML = 0;
	public static final int DOC = 1;
	public static final int PDF = 2;
	public static final int WDOC = 3;
	
	public static final String HTML_REGEX = ".*\\.(html|htm|do)$";
	public static final String HTML_REGEX2 = ".*(more|category).*";
	public static final String DOC_REGEX = ".*\\.(doc|docx)$";
	public static final String PDF_REGEX = ".*\\.(PDF|pdf)$";
	public static final String WDOC_REGEX = ".*\\.wdoc$";
	
	public static final String HTML_SUFFIX = "\\.(html|htm)";
	public static final String DOC_SUFFIX = "\\.(doc|docx)";
	public static final String PDF_SUFFIX = "\\.(PDF|pdf)";
	public static final String WDOC_SUFFIX = "\\.wdoc";
	
	public static int validate(String filename){		
		if(valiDOC(filename))
			return DOC;
		if(valiPDF(filename))
			return PDF;
		if(valiWDOC(filename))
			return WDOC;
		if(valiHtml(filename))
			return HTML;
		return INVALID;
	}
	
	public static boolean valiHtml(String filename){
		Pattern pattern = Pattern.compile(HTML_REGEX);
		Matcher matcher = pattern.matcher(filename);
		Pattern pattern2 = Pattern.compile(HTML_REGEX2);
		Matcher matcher2 = pattern.matcher(filename);
		return matcher.matches() || matcher2.matches();
	}
	
	public static boolean valiDOC(String filename){
		Pattern pattern = Pattern.compile(DOC_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();
	}
	
	public static boolean valiPDF(String filename){
		Pattern pattern = Pattern.compile(PDF_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();
	}
	
	public static boolean valiWDOC(String filename){
		Pattern pattern = Pattern.compile(WDOC_REGEX);
		Matcher matcher = pattern.matcher(filename);
		return matcher.matches();
	}
}
