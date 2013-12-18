package fit.ctu.cz.vwm.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotSecureConnection {

	private final String USER_AGENT = "Mozilla/5.0";

	// HTTP POST request
	public InputStream sendPost(String url) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());

		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		// System.out.println("Post parameters : " + postData);
		System.out.println("--------------\nHTTP Response Code : " + responseCode);
		return con.getInputStream();
	}

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