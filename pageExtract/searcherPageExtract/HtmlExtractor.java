package searcherPageExtract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

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
	
	/**	默认过滤器，提取title,anchor,h1~h5
	 * @return
	 */
	NodeFilter getDefaultFilter(){
		TagNameFilter titleFilter = new TagNameFilter("title");
		TagNameFilter aFilter = new TagNameFilter("a");
		TagNameFilter pFilter = new TagNameFilter("p");
		TagNameFilter h1Filter = new TagNameFilter("h1");
		TagNameFilter h2Filter = new TagNameFilter("h2");
		TagNameFilter h3Filter = new TagNameFilter("h3");
		TagNameFilter h4Filter = new TagNameFilter("h4");
		TagNameFilter h5Filter = new TagNameFilter("h5");
		
		NodeFilter[] nodeFilters = {titleFilter, pFilter,h1Filter,h2Filter,h3Filter,h4Filter,h5Filter,
									aFilter};
		OrFilter orFilter = new OrFilter(nodeFilters);
		return orFilter;
	}
	
	/**	根据网页的URI获得相应的过滤器
	 * @param URI	网页的URI
	 * @return 对新闻详细页，返回新闻过滤器  其他返回默认过滤器
	 */
	NodeFilter getFilter(String URI){
		
		/*	对于新闻详细页，提取其title，anchor，article中的内容
		 * */
		if(URI.contains("news.tsinghua.edu.cn") && !URI.contains("index.html")){
			TagNameFilter titleFilter = new TagNameFilter("title");
			TagNameFilter anchorFilter = new TagNameFilter("a");
			TagNameFilter articleFilter = new TagNameFilter("article");
			NodeFilter[] nodeFilters = {titleFilter, anchorFilter, articleFilter};
			
			OrFilter orFilter = new OrFilter(nodeFilters);
			return orFilter;
		}
		
		return getDefaultFilter();
	}
	
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
			
			TagNameFilter aFilter = new TagNameFilter("a");
			TagNameFilter titleFilter = new TagNameFilter("title");
			
			NodeFilter combinedFilter = getFilter(URI);
			
			NodeList nodeList = parser.parse(combinedFilter);
			Node[] nodeArray = nodeList.toNodeArray();
			
			JSONObject json = new JSONObject();
			json.put("title","");
					
			String absString = new String();
			String anchorString = new String();
			
			for(Node node : nodeArray){
				
				if(titleFilter.accept(node)){
					if(node.getChildren() != null)
						json.put("title",node.getChildren().elementAt(0).getText());
				}else if(aFilter.accept(node)){
					anchorString += digText(node);
				}
				else if(combinedFilter.accept(node) ){
					absString += digText(node);
				}
				
			}
			
			URI = URI.replaceFirst(".*?\\\\", "");
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
	 * get contained plain text in a html tag recursively
	 * */
	String digText(Node node){
		if(node instanceof TextNode){
			return node.getText();
		}
		String text = new String();
		if(node.getChildren() != null){
			for(int i = 0; i < node.getChildren().size(); i++){
				Node subNode = node.getChildren().elementAt(i);
				if(subNode instanceof TextNode){
					text += subNode.getText();
				}
				else{
					text += digText(subNode);
				}
			}
		}
		else{
			return text;
		}
		text = text.replaceAll("(&nbsp);", "");
		text = text.replaceAll("\" \\u002B entry\\u005B\"title\"] \\u002B \"", "");
		return text;
	}
}
