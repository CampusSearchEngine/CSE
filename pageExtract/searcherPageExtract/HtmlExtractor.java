package searcherPageExtract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xpath.internal.operations.And;

import searcherUtils.IO;
import sun.util.logging.resources.logging_es;

/*
 *	extract useful content in a page(title, h1~h5 , p , a ...) into a json
 *  current elements:
 *  <type>		: html
 *  <title> 	: html title
 *  <content> 	: texts in <p> <h1>~<h5>
 *  <anchor>	: anchor texts in <a>
 *  <URI>		: remote URI
 * */
public class HtmlExtractor implements Extractor{
	
	@Override
	public void extract(String URI, String toPath, String encoding, int ID) {
		try {
			BufferedReader bReader = IO.getReader(URI);
			String html = new String(), line;
			while((line = bReader.readLine()) != null)
				html += line;
			bReader.close();
			
			Parser parser = new Parser();
			parser.setInputHTML(html);
			parser.setEncoding(encoding);
			
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
			
			JSONObject json = new JSONObject();
			json.put("title","");
					
			String absString = new String();
			String anchorString = new String();
			
			for(Node node : nodeArray){
				
				if(titleFilter.accept(node)){
					if(node.getChildren() != null)
						json.put("title",node.getChildren().elementAt(0).getText());
				}
				else if(pFilter.accept(node) || h1Filter.accept(node) || h2Filter.accept(node) ||
						h3Filter.accept(node) || h4Filter.accept(node) || h5Filter.accept(node) ){
					absString += digText(node);
				}
				else if(aFilter.accept(node)){
					anchorString += digText(node);
				}
			}
			
			json.put("type", "html");
			json.put("URI", URI);
			json.put("content", absString);
			json.put("anchor", anchorString);
			json.put("ID", ID);

			BufferedWriter bWriter = IO.getWriter(toPath);
			bWriter.write(json.toString());
			bWriter.flush();
			bWriter.close();
		} catch (ParserException | IOException | JSONException e) {
			if(e instanceof EncodingChangeException){
				if(encoding.equals("utf-8")){		// if encountering encoding problem, just switch encoding and retry
					//e.printStackTrace();
					//System.out.println("retry: " + URI);
					extract(URI, toPath, "GB2312",ID);
					//System.out.println("retry success");
					return;
				}
			}
			e.printStackTrace();
			System.out.println("-->: " + URI);
		}
		
	}
	
	static public void main(String[] args){
		HtmlExtractor pExtractor = new HtmlExtractor();
		pExtractor.extract(args[0], args[1], "utf-8", 0);
	}
	
	/*
	 * get contained plain text in a html tag
	 * assuming every first-level subNode is a linear structure, and its leaf node contains the text 
	 * */
	String digText(Node node){
		String text = new String();
		if(node.getChildren() != null){
			for(int i = 0; i < node.getChildren().size(); i++){
				Node subNode = node.getChildren().elementAt(i);
				while(subNode.getChildren() != null)
					subNode = subNode.getChildren().elementAt(0);
				if(subNode instanceof TextNode){
					String nodeText = subNode.getText();
					nodeText = nodeText.replaceAll("(&nbsp);", "");
					nodeText = nodeText.replaceAll("\" \\u002B entry\\u005B\"title\"] \\u002B \"", "");
					if(!nodeText.startsWith("a href=") && nodeText.length() > 0){
						text += " " + nodeText;
					}
				}
			}
		}
		return text;
	}
}