import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.Explanation.*;
import org.apache.lucene.util.*;

/**
 * A Query that matches documents containing a term. This may be combined with
 * other terms with a {@link BooleanQuery}.
 */
public class BM25Query extends Query {
	private Term term;
	private float avgLength;

	private class BM25Weight extends Weight {
		private float avgLength;
		private Searcher search;
		private final Similarity similarity;
		private float value;
		private float idf;
		private float queryNorm;
		private float queryWeight;
		private IDFExplanation idfExp;
		private final Set<Integer> hash;

		public BM25Weight(Searcher searcher, float avg)
				throws IOException {
			this.avgLength = avg;
			this.search = searcher;
			this.similarity = getSimilarity(searcher);
			if (searcher instanceof IndexSearcher) {
				hash = new HashSet<Integer>();
				IndexReader ir = ((IndexSearcher) searcher).getIndexReader();
				final int dfSum[] = new int[1];
				new ReaderUtil.Gather(ir) {
					@Override
					protected void add(int base, IndexReader r)
							throws IOException {
						int df = r.docFreq(term);
						dfSum[0] += df;
						if (df > 0) {
							hash.add(r.hashCode());
						}
					}
				}.run();

				idfExp = similarity.idfExplain(term, searcher, dfSum[0]);
			} else {
				idfExp = similarity.idfExplain(term, searcher);
				hash = null;
			}

			idf = idfExp.getIdf();
		}

		@Override
		public String toString() {
			return "weight(" + BM25Query.this + ")";
		}

		@Override
		public Query getQuery() {
			return BM25Query.this;
		}

		@Override
		public float getValue() {
			return value;
		}

		@Override
		public float sumOfSquaredWeights() {
			queryWeight = idf * getBoost(); // compute query weight
			return queryWeight * queryWeight; // square it
		}

		@Override
		public void normalize(float queryNorm) {
			this.queryNorm = queryNorm;
			queryWeight *= queryNorm; // normalize query weight
			value = queryWeight * idf; // idf for document
		}

		@Override
		public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder,
				boolean topScorer) throws IOException {
			// only use the early exit condition if we have an atomic reader,
			// because Lucene 3.x still supports non-atomic readers here:
			if (hash != null && reader.getSequentialSubReaders() == null
					&& !hash.contains(reader.hashCode())) {
				return null;
			}

			TermDocs termDocs = reader.termDocs(term);
			if (termDocs == null)
				return null;

			return new BM25Scorer(this, termDocs, similarity, reader
					.norms(term.field()), idf,avgLength, reader);
		}

		@Override
		public Explanation explain(IndexReader reader, int doc)
				throws IOException {

			ComplexExplanation result = new ComplexExplanation();
			result.setDescription("weight(" + getQuery() + " in " + doc
					+ "), product of:");

			Explanation expl = new Explanation(idf, idfExp.explain());

			// explain query weight
			Explanation queryExpl = new Explanation();
			queryExpl.setDescription("queryWeight(" + getQuery()
					+ "), product of:");

			Explanation boostExpl = new Explanation(getBoost(), "boost");
			if (getBoost() != 1.0f)
				queryExpl.addDetail(boostExpl);
			queryExpl.addDetail(expl);

			Explanation queryNormExpl = new Explanation(queryNorm, "queryNorm");
			queryExpl.addDetail(queryNormExpl);

			queryExpl.setValue(boostExpl.getValue() * expl.getValue()
					* queryNormExpl.getValue());

			result.addDetail(queryExpl);

			// explain field weight
			String field = term.field();
			ComplexExplanation fieldExpl = new ComplexExplanation();
			fieldExpl.setDescription("fieldWeight(" + term + " in " + doc
					+ "), product of:");

			Explanation tfExplanation = new Explanation();
			int tf = 0;
			TermDocs termDocs = reader.termDocs(term);
			if (termDocs != null) {
				try {
					if (termDocs.skipTo(doc) && termDocs.doc() == doc) {
						tf = termDocs.freq();
					}
				} finally {
					termDocs.close();
				}
				tfExplanation.setValue(similarity.tf(tf));
				tfExplanation.setDescription("tf(termFreq(" + term + ")=" + tf
						+ ")");
			} else {
				tfExplanation.setValue(0.0f);
				tfExplanation.setDescription("no matching term");
			}
			fieldExpl.addDetail(tfExplanation);
			fieldExpl.addDetail(expl);

			Explanation fieldNormExpl = new Explanation();
			byte[] fieldNorms = reader.norms(field);
			float fieldNorm = fieldNorms != null ? similarity
					.decodeNormValue(fieldNorms[doc]) : 1.0f;
			fieldNormExpl.setValue(fieldNorm);
			fieldNormExpl.setDescription("fieldNorm(field=" + field + ", doc="
					+ doc + ")");
			fieldExpl.addDetail(fieldNormExpl);

			fieldExpl.setMatch(Boolean.valueOf(tfExplanation.isMatch()));
			fieldExpl.setValue(tfExplanation.getValue() * expl.getValue()
					* fieldNormExpl.getValue());

			result.addDetail(fieldExpl);
			result.setMatch(fieldExpl.getMatch());

			// combine them
			result.setValue(queryExpl.getValue() * fieldExpl.getValue());

			if (queryExpl.getValue() == 1.0f)
				return fieldExpl;

			return result;
		}
	}

	/** Constructs a query for the term <code>t</code>. */
	public BM25Query(Term t,float avg) {
		term = t;
		avgLength=avg;
	}

	/** Returns the term of this query. */
	public Term getTerm() {
		return term;
	}

	@Override
	public Weight createWeight(Searcher searcher) throws IOException {
		return new BM25Weight(searcher,avgLength);
	}

	@Override
	public void extractTerms(Set<Term> terms) {
		terms.add(getTerm());
	}

	/** Prints a user-readable version of this query. */
	@Override
	public String toString(String field) {
		StringBuilder buffer = new StringBuilder();
		if (!term.field().equals(field)) {
			buffer.append(term.field());
			buffer.append(":");
		}
		buffer.append(term.text());
		buffer.append(ToStringUtils.boost(getBoost()));
		return buffer.toString();
	}

	/** Returns true iff <code>o</code> is equal to this. */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BM25Query))
			return false;
		BM25Query other = (BM25Query) o;
		return (this.getBoost() == other.getBoost())
				&& this.term.equals(other.term);
	}

	/** Returns a hash code value for this object. */
	@Override
	public int hashCode() {
		return Float.floatToIntBits(getBoost()) ^ term.hashCode();
	}

}
