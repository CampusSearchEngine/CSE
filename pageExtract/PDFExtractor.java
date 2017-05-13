import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.apache.pdfbox.tools.ExtractText;
import org.htmlparser.nodes.TagNode;

/*
 *	extract the content in a pdf into another html 
 *  with the most out tag is <document>
 *  current elements:
 *  <type>		: pdf
 *  <title> 	: pdf title (first line in txt file)
 *  <content> 	: pdf content (remaining lines)
 *  <URI>		: remote URI
 * */
public class PDFExtractor implements Extractor {

	@Override
	public void extract(String URI, String toPath) {
		String txtDoc = URI.substring(0, URI.length()-3) + "txt";
		try {
			ExtractText.main(new String[]{URI, txtDoc});
			
			BufferedReader bReader = IO.getReader(txtDoc);
			String titleStr = bReader.readLine();
			String conStr = new String();
			String line;
			while((line = bReader.readLine()) != null){
				conStr += line;
			}
			bReader.close();
			
			TagNode docNode = new TagNode();
			docNode.setTagName("document");
			TagNode typeNode = new TagNode();
			typeNode.setTagName("type");
			typeNode.setText("pdf");
			TagNode titleNode = new TagNode();
			titleNode.setTagName("title");
			titleNode.setText(titleStr);
			TagNode URINode = new TagNode();
			URINode.setTagName("URI");
			URINode.setText(URI);
			TagNode absNode = new TagNode();
			absNode.setTagName("content");
			absNode.setText(conStr);
			
			docNode.getChildren().add(titleNode);
			docNode.getChildren().add(typeNode);
			docNode.getChildren().add(URINode);
			docNode.getChildren().add(absNode);
			
			BufferedWriter bWriter = IO.getWriter(toPath);
			bWriter.write(docNode.toHtml());
			bWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
