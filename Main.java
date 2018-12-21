import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		DataInputStream is;
		DataOutputStream os;
		System.out.println("Waiting for you...");
		ServerSocket socket = new ServerSocket(22399);
		while (true) {
			Socket socket2 = socket.accept();
			// String string =
			// "{\"id\":1,\"method\":\"object.deleteAll\",\"params\":[\"subscriber\"]}";
			is = new DataInputStream(socket2.getInputStream());
			os = new DataOutputStream(socket2.getOutputStream());
			PrintWriter pw = new PrintWriter(os);
			// pw.println(string);
			// pw.flush();

			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			JSONObject json;

			json = new JSONObject(in.readLine());
			System.out.println(json.toString());
			
			JSONObject obj = new JSONObject().put("ack",1).put("lista", new String[] {"a","b","c","d"});
			if (json.getString("action").equals("prova")) {
				System.out.println(obj.toString());
				pw.println(obj);
			}

//			if (!json.has("action") || !json.has("id")) {
//				System.out.println("serve action e id");
//				continue;
//			}
//			if (json.getString("action").equals("recommend")) {
//				if (!json.has("category") || !json.has("start_date"))
//					continue;
//				Recommender rec = new Recommender(json.getInt("id"), json.getString("category"),
//						json.getString("start_date"));
//				ArrayList<Event> results = rec.raccomanda();
//				// for (Event r : results) {
//				// System.out.print(r.category + " " + r.fb_id + " ");
//				// System.out.println(r.name);
//				// }
//				// System.out.println("");
//				// System.out.println(rec.utente.profilo.toString());
//				// System.out.println(rec.utente.profilo_fb_id.toString());
//
//				pw.println(new JSONArray(results));
//			} else if (json.getString("action").equals("user_info")) {
//				Recommender rec = new Recommender(json.getInt("id"), "", "");
//				Utente u = rec.utente;
//				pw.println(new JSONObject().put("bags", u.bags).put("docs", u.profilo));
//			}

			pw.flush();
			socket2.close();
		}

	}

}