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

	// ��ȡ�����ӣ�urlΪ��ҳurl��filter�����ӹ����������ظ�ҳ�������ӵ�HashSet
	// reference from : http://www.cnblogs.com/coding-hundredOfYears/archive/2012/12/15/2819217.html
	public static Set<String> extracLinks(String url, LinkRegexFilter filter) {

		Set<String> links = new HashSet<String>();
		try {
			Parser parser = new Parser(url);
			parser.setEncoding("utf-8");
			// ���� <frame >��ǩ�� filter��������ȡ frame ��ǩ��� src ��������ʾ������
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
			// ����<a>��ǩ��<frame>��ǩ��ע��NodeClassFilter()����������һ���ǩ��linkTag��Ӧ<��ǩ>
			OrFilter linkFilter = new OrFilter(new NodeClassFilter(LinkTag.class), frameFilter);
			// �õ����о������˵ı�ǩ�����ΪNodeList
			NodeList list = parser.extractAllNodesThatMatch(linkFilter);
			for (int i = 0; i < list.size(); i++) {
				Node tag = list.elementAt(i);
				if (tag instanceof LinkTag)// <a> ��ǩ
				{
					LinkTag link = (LinkTag) tag;
					String linkUrl = link.getLink();// ����getLink()�����õ�<a>��ǩ�е�����
					if (filter == null || filter.accept(tag))// ������filter�������������Ӽ������ӱ�
						links.add(linkUrl);
				} else {// <frame> ��ǩ
						// ��ȡ frame �� src ���Ե������� <frame src="test.html"/>
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
		} catch (ParserException e) {// ��׽parser���쳣
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
