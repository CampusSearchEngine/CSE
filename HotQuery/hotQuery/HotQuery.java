package hotQuery;

import java.util.Vector;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import searcherDB.MongoDBs;

public class HotQuery {
	
	/**更新数据库中对应query的查询计数与查询时间，没有时新建
	 * @param query
	 */
	public static void updateHotQuery(String query){
		if(MongoDBs.queries.count(Filters.eq("query", query)) == 0){
			Document document = new Document();
			document.put("query", query);
			document.put("count", 1);
			BasicDBList timeList = new BasicDBList();
			timeList.add(System.currentTimeMillis());
			document.put("timeStamps", timeList);
			MongoDBs.queries.insertOne(document);
		}
		else{
			Document updateArg = new Document();
			updateArg.put("$inc", new BasicDBObject("count",1));
			updateArg.put("$push", new BasicDBObject("timeStamps",System.currentTimeMillis()));
			MongoDBs.queries.findOneAndUpdate(Filters.eq("query", query), updateArg);
		}
	}
	
	/**查询搜索频率最高的前topNum个query
	 * @param topNum
	 * @return 一个Bson的Vector，Bson中包含<query,query字符串>,<count,查询次数>,<timeStamps,产生查询的时间戳数组>
	 */
	public static Vector<Document> getTop(int topNum) {
		MongoCursor<Document> cursor =  MongoDBs.queries.find().sort(new BasicDBObject("count",-1)).iterator();
		Vector<Document> results = new Vector<Document>();
		
		int count = 0;
		while(cursor.hasNext() && count < topNum){
			results.addElement(cursor.next());
			count++;
		}
		
		return results;
	}
}
