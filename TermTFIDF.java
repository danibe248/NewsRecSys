public class TermTFIDF implements Comparable<TermTFIDF>{
	public String term;
	public double tfidf;
	
	public TermTFIDF(double tfidf, String term){
		this.term = term;
		this.tfidf = tfidf;
	}
	
	public int compareTo(TermTFIDF second){
		if (second.tfidf > this.tfidf) {
			return 1;
		} else {
			if (second.tfidf < this.tfidf) {
				return -1;
			} else {
				return 0;
			}
		}
//		return other.tfidf > this.tfidf ? 1 :other.tfidf < this.tfidf?-1:0;  
	}
	
}