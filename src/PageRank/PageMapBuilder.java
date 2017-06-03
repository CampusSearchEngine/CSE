package PageRank;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class PageMapBuilder {
	static HashMap<Integer, Page> buildPageMap(String pageListFile, String pageMapFile){
		HashMap<Integer, Page> pageMap = new HashMap<Integer, Page>();
		
		try {
			//read page list
			System.out.println("reading page List");
			
			File file = new File(pageListFile);
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader iReader = new InputStreamReader(fileInputStream,"utf-8");
			BufferedReader bReader = new BufferedReader(iReader);
			
			String line;
			int pageCnt = 0;
			while((line = bReader.readLine()) != null){
				String[] parts = line.split("-->");
				String title = parts[0];
				int id = Integer.parseInt(parts[1]);
				Page page = new Page(title, id);
				pageMap.put(id, page);
				pageCnt ++;
			}
			bReader.close();
			System.out.println("pageList read, " + pageCnt + " pages found");
			
			//read pageLink list
			file = new File(pageMapFile);
			fileInputStream = new FileInputStream(file);
			iReader = new InputStreamReader(fileInputStream,"utf-8");
			bReader = new BufferedReader(iReader);
			
			while((line = bReader.readLine()) != null){
				String[] rootChildren = line.split(":");
				if(rootChildren.length > 1){
					String[] children = rootChildren[1].split(",");
					Integer rootId = Integer.parseInt(rootChildren[0]);
					Page page = pageMap.get(rootId);
					assert(page != null);
					
					for(String str : children){
						int id = Integer.parseInt(str);
						page.outPages.addElement(id);
						page.outDegree ++;
						Page inPage = pageMap.get(id);
						if(inPage != null){
							inPage.inDegree ++;
							inPage.inPages.addElement(rootId);
						}
					}
				}
			}
			bReader.close();
			System.out.println("pageMap built");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pageMap;
	}
}
