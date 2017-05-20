import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.*;

public class BM25QueryParser extends QueryParser {

	float avgLength;
	
	public BM25QueryParser(Version matchVersion, String f, Analyzer a, float avgLength2) {
		super(matchVersion, f, a);
		this.avgLength = avgLength2;
	}
	
	@Override
	public Query parse(String query) {
		BooleanQuery rtQuery = new BooleanQuery();
		StringReader stringReader = new StringReader(query);
		Analyzer analyzer = this.getAnalyzer();
		String field = this.getField();
		TokenStream tokenStream = analyzer.tokenStream(field, stringReader);		//tokenize query
		try {
			tokenStream.reset();
			tokenStream.incrementToken();
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			do{
				//System.out.println("token : " + charTermAttribute.toString());
				Term term = new Term(field, charTermAttribute.toString());			//construct queries using tokens
				BM25Query bm25Query = new BM25Query(term, this.avgLength);
				rtQuery.add(bm25Query, Occur.SHOULD);
			}
			while(tokenStream.incrementToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return rtQuery;
	}
}
