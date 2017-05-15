/*
 * extract useful content from a raw web page, the output should be a json and ends with ".wdoc"
 * */
public interface Extractor {
	public void extract(String URI, String toPath);
}
