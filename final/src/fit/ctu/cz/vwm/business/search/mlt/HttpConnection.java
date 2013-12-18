package fit.ctu.cz.vwm.business.search.mlt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpConnection {

	private final String USER_AGENT = "Mozilla/5.0";

	public void sendGet(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		int responseCode = con.getResponseCode();
		System.out.println("GET HTTP Response Code : " + responseCode);
		System.out.println("------------------------------------------");
	}

	// "http://ner.vse.cz/thd/api/v1/extraction?apid4aca40d1bab16783f3dbd381&format=xml&provenance=thd&
	// priority_entity_linking=true&entity_type=all&lang=de" -d "München"

	// HTTP POST request
	public InputStream sendPostSecure(String url, String data) throws Exception {
		URL urlObj = new URL(url);
		StringBuffer buffer = new StringBuffer();
		HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);
		OutputStream os = connection.getOutputStream();
		os.write(data.getBytes("UTF-8"));
		os.flush();
		os.close();
		System.out.println("GET HTTP Response Code : " + connection.getResponseCode());
		return connection.getInputStream();
	}

	public InputStream sendPostNotSecure(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		// Send post request
		con.setDoOutput(true);

		int responseCode = con.getResponseCode();
		System.out.println("HTTP Response Code : " + responseCode);
		System.out.println("------------------------------------------");
		return con.getInputStream();
	}

	// debugg
	public static void printinBuffer(InputStream data) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(data));

		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		System.out.println("response:");
		System.out.println(response.toString());
		System.out.println("");
	}

}