import java.io.BufferedWriter;
import java.io.IOException;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/*
 *	extract useful content in a page(title, h1~h5 , p , a ...) into another html 
 *  with the most out tag is <document>
 *  current elements:
 *  <type>		: html
 *  <title> 	: html title
 *  <content> 	: texts in <p> <h1>~<h5>
 *  <anchor>	: anchor texts in <a>
 *  <URI>		: remote URI
 * */
public class HtmlExtractor implements Extractor{
	
	@Override
	public void extract(String URI, String toPath) {
		try {
			Parser parser = new Parser(URI);
			parser.setEncoding("utf-8");
			
			TagNameFilter titleFilter = new TagNameFilter("title");
			TagNameFilter pFilter = new TagNameFilter("p");
			TagNameFilter h1Filter = new TagNameFilter("h1");
			TagNameFilter h2Filter = new TagNameFilter("h2");
			TagNameFilter h3Filter = new TagNameFilter("h3");
			TagNameFilter h4Filter = new TagNameFilter("h4");
			TagNameFilter h5Filter = new TagNameFilter("h5");
			TagNameFilter aFilter = new TagNameFilter("a");
			
			NodeFilter[] nodeFilters = {titleFilter, pFilter,h1Filter,h2Filter,h3Filter,h4Filter,h5Filter,
										aFilter};
			OrFilter orFilter = new OrFilter(nodeFilters);
			
			NodeList nodeList = parser.parse(orFilter);
			Node[] nodeArray = nodeList.toNodeArray();
			
			TagNode docNode = new TagNode();
			docNode.setTagName("document");
			TagNode typeNode = new TagNode();
			typeNode.setTagName("type");
			typeNode.setText("html");
			TagNode titleNode = new TagNode();
			titleNode.setTagName("title");
			TagNode URINode = new TagNode();
			URINode.setTagName("URI");
			URINode.setText(URI);
			TagNode absNode = new TagNode();
			absNode.setTagName("content");
			TagNode ancNode = new TagNode();
			ancNode.setTagName("anchor");

			docNode.getChildren().add(titleNode);
			docNode.getChildren().add(typeNode);
			docNode.getChildren().add(URINode);
			docNode.getChildren().add(absNode);
			docNode.getChildren().add(ancNode);
			
			String absString = new String();
			String anchorString = new String();
			
			for(Node node : nodeArray){
				if(titleFilter.accept(node)){
					titleNode.setText(node.getText());
				}
				else if(pFilter.accept(node) || h1Filter.accept(node) || h2Filter.accept(node) ||
						h3Filter.accept(node) || h4Filter.accept(node) || h5Filter.accept(node) ){
					absString += " " + node.getText();
				}
				else if(aFilter.accept(node)){
					anchorString += " " + node.getText();
				}
			}
			
			absNode.setText(absString);
			ancNode.setText(anchorString);
			
			BufferedWriter bWriter = IO.getWriter(toPath);
			bWriter.write(docNode.toHtml());
			bWriter.flush();
			bWriter.close();
		} catch (ParserException | IOException e) {
			e.printStackTrace();
		}
		
	}

}
