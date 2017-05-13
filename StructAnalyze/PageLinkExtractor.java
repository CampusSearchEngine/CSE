import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.LinkStringFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;


public class PageLinkExtractor {
	HashMap<String, Integer> IDMap;
	HashMap<Integer, Vector<Integer>> linkMap;

	public PageLinkExtractor() {
		IDMap = new HashMap<String, Integer>();
		linkMap = new HashMap<Integer, Vector<Integer>>();
	}

	/*
	 * read format "URI-->ID-->type" from mapFile
	 */
	public void readIDMap(String mapFile) {
		try {
			BufferedReader bReader = IO.getReader(mapFile);
			String line;

			while ((line = bReader.readLine()) != null) {
				String[] args = line.split("-->");
				String URI = args[0];
				int ID = Integer.parseInt(args[1]);
				IDMap.put(URI, ID);
			}
			bReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * scan every page in MirrorPath to get its page links
	 */
	public void scanPageLink(String MirrorPath) {
		MirrorIter iter = new MirrorIter(MirrorPath);

		while (iter.hasNext()) {
			String URI = iter.next();
			int type = FileValidator.validate(URI);
			// process only HTML files
			if(type == FileValidator.HTML){
				int rootID = IDMap.get(URI);
				Vector<Integer> childernID = new Vector<Integer>();
				
				Set<String> sublinks = extracLinks(URI, null);
				
				Iterator<String> linkIter = sublinks.iterator();
				while(linkIter.hasNext()){				//find every subURI's corresponding ID, if not null, add to childrenID
					String subURI = linkIter.next();
					Integer subID = IDMap.get(subURI);
					if(subID != null){
						childernID.addElement(subID);
					}
				}
				
				linkMap.put(rootID, childernID);
			}
		}
	}

	// 获取子链接，url为网页url，filter是链接过滤器，返回该页面子链接的HashSet
	// reference from : http://www.cnblogs.com/coding-hundredOfYears/archive/2012/12/15/2819217.html
	public static Set<String> extracLinks(String url, LinkRegexFilter filter) {

		Set<String> links = new HashSet<String>();
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("utf-8");
			// 过滤 <frame >标签的 filter，用来提取 frame 标签里的 src 属性所表示的链接
			NodeFilter frameFilter = new NodeFilter() {
				public boolean accept(Node node) {
					if (node.getText().startsWith("frame src=")) {
						return true;
					} else {
						return false;
					}
				}
			};
			// OrFilter
			// 接受<a>标签或<frame>标签，注意NodeClassFilter()可用来过滤一类标签，linkTag对应<标签>
			OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
			// 得到所有经过过滤的标签，结果为NodeList
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				Node tag = list.elementAt(i);
				if (tag instanceof LinkTag)// <a> 标签
				{
					LinkTag link = (LinkTag) tag;
					String linkUrl = link.getLink();// 调用getLink()方法得到<a>标签中的链接
					if (filter == null || filter.accept(tag))// 将符合filter过滤条件的链接加入链接表
						links.add(linkUrl);
				} else {// <frame> 标签
						// 提取 frame 里 src 属性的链接如 <frame src="test.html"/>
					String frame = tag.getText();
					int start = frame.indexOf("src=");
					frame = frame.substring(start);
					int end = frame.indexOf(" ");
					if (end == -1)
						end = frame.indexOf(">");
					String frameUrl = frame.substring(5, end - 1);
					if (filter == null || filter.accept(tag))
						links.add(frameUrl);
				}
			}
		} catch (ParserException e) {// 捕捉parser的异常
			e.printStackTrace();
		}
		return links;
	}
	
	/*
	 * save page links as "hostID:subID1,subID2,...\n" to <path> for every page in linkMap
	 * */
	public void saveLinkMap(String path){
		BufferedWriter bufferedWriter = IO.getWriter(path);

		Iterator<Entry<Integer, Vector<Integer>>> iterator = linkMap.entrySet().iterator();
		try {
			while(iterator.hasNext()){
				Entry<Integer, Vector<Integer>> entry = iterator.next();
				bufferedWriter.write(entry.getKey() + ":" );
				
				Vector<Integer> subIDs = entry.getValue();
				if (subIDs != null) {
					for(int i = 0; i < subIDs.size() - 1; i++){
						bufferedWriter.write(subIDs.elementAt(i) + "," );
					}
					bufferedWriter.write(subIDs.lastElement() + "\n" );
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
