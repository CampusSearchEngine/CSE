/*
 * extract useful content from a raw web page, the output should be a json and ends with ".wdoc"
 * */
package PageExtract;
public interface Extractor {
	public void extract(String URI, String toPath, String encoding, int ID, String mirrorPath);
}
