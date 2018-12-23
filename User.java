import java.util.ArrayList;
import org.apache.lucene.document.Document;

public class User {
	private int id;
	private ArrayList<Document> user_profile;
	
	public int getId() {
		return id;
	}

	public ArrayList<Document> getUser_profile() {
		return user_profile;
	}

	public ArrayList<Document> getUser_profile(String cat) throws CategoryNotFoundException {
		if (cat.equals("anime") | cat.equals("tech") | cat.equals("cars") | cat.equals("archi") | 
				cat.equals("sport") | cat.equals("weather") | cat.equals("hiphop")) {
			ArrayList<Document> filtered_profile = new ArrayList<Document>();
			for (Document d : (Document[]) user_profile.toArray()) {
				if (d.getField("Category").equals(cat)) {
					filtered_profile.add(d);
				}
			}
			return filtered_profile;
		} else if (cat.equals("allin")) {
			return user_profile;
		} else {
			throw new CategoryNotFoundException();
		}
	}
	
	public void setUser_profile(ArrayList<Document> user_profile) {
		this.user_profile = user_profile;
	}
}
