package PageExtract;

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

import Utils.IO;

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
	
	/**	Ĭ�Ϲ���������ȡtitle,anchor,h1~h5
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
	
	/**	������ҳ��URI�����Ӧ�Ĺ�����
	 * @param URI	��ҳ��URI
	 * @return ��������ϸҳ���������Ź�����  ��������Ĭ�Ϲ�����
	 */
	NodeFilter getFilter(String URI){
		
		/*	����������ϸҳ����ȡ��title��anchor��article, p�е�����
		 * */
		if(URI.contains("news.tsinghua.edu.cn") && !URI.contains("index.html")){
			TagNameFilter titleFilter = new TagNameFilter("title");
			TagNameFilter anchorFilter = new TagNameFilter("a");
			TagNameFilter articleFilter = new TagNameFilter("article");
			TagNameFilter pFilter = new TagNameFilter("p");
			NodeFilter[] nodeFilters = {titleFilter, anchorFilter, articleFilter,pFilter};
			
			OrFilter orFilter = new OrFilter(nodeFilters);
			return orFilter;
		}
		
		return getDefaultFilter();
	}
	
	@Override
	public void extract(String URI, String toPath, String encoding, int ID, String mirrorPath) {
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
			
			URI = URI.substring(mirrorPath.length()+1);
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
					extract(URI, toPath, "GB2312",ID, mirrorPath);
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
		pExtractor.extract(args[0], args[1], "utf-8", 0, "");
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
