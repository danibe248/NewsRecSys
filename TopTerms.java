import java.util.ArrayList;
import java.util.Collections;

public class TopTerms{
	public int n_terms;
	public ArrayList<TermTFIDF> top;
	public TopTerms(int n_terms){
		this.n_terms = n_terms;
		this.top = new ArrayList<TermTFIDF>();
	}
	public void add(TermTFIDF t){
		if(top.size() < n_terms)
			top.add(t);
		else{
			if(t.tfidf <= top.get(n_terms -1).tfidf)
				return;
			this.top.remove(n_terms-1);
			this.top.add(t);
		}
		Collections.sort(this.top);
	}
}
