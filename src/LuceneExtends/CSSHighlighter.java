package LuceneExtends;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.util.Version;

public class CSSHighlighter {
	private Analyzer analyzer;
	private SimpleHTMLFormatter formatter;
	private float avgLength;
	
	public CSSHighlighter(Analyzer analyzer, float avgLength) {
		this.analyzer = analyzer;
		this.formatter = new SimpleHTMLFormatter("<span style=\"color:red\">", "</span>");
		this.avgLength = avgLength;
	}
	
	public void setFormatter(SimpleHTMLFormatter formatter) {
		this.formatter = formatter;
	}
	
	/**	使用默认CSS格式对文本进行高亮处理
	 * @param string		待处理的文本
	 * @param maxLength		最大返回长度
	 * @param avgLength 	BM25算法使用的平均文本长度
	 * @return				处理后的文本
	 */
	public String Highlight(String text, String queryText, int maxLength){
		QueryParser queryParser = new QueryParser(Version.LUCENE_35, "field", this.analyzer);
		try {
			Query query = queryParser.parse(queryText);
			TokenStream tokenStream = this.analyzer.tokenStream("field", new StringReader(text));
			QueryScorer scorer = new QueryScorer(query);
			
			Highlighter highlighter = new Highlighter(formatter, scorer);
			highlighter.setTextFragmenter(new SimpleSpanFragmenter(scorer, maxLength));
			
			return highlighter.getBestFragment(tokenStream, text);
		} catch (ParseException | IOException | InvalidTokenOffsetsException e) {
			e.printStackTrace();
		}
		return text;
	}
}
