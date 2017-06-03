package HotQuery;

import java.util.Vector;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Mongo;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;

import mongoDB.MongoDBs;

public class HotQuery {
	
	/**�������ݿ��ж�Ӧquery�Ĳ�ѯ�������ѯʱ�䣬û��ʱ�½�
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
	
	/**��ѯ����Ƶ����ߵ�ǰtopNum��query
	 * @param topNum
	 * @return һ��Bson��Vector��Bson�а���<query,query�ַ���>,<count,��ѯ����>,<timeStamps,������ѯ��ʱ�������>
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
