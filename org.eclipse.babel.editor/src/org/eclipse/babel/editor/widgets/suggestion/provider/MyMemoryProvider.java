package org.eclipse.babel.editor.widgets.suggestion.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.babel.editor.widgets.suggestion.ISuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyMemoryProvider implements ISuggestionProvider {

	private final String URL = "http://api.mymemory.translated.net/get?q=";
	private Image icon;


	public MyMemoryProvider() {
		this.icon = new Image(Display.getCurrent(),System.getProperty("user.dir")+"/icons/mymemo16.png");
//		this.icon = UIUtils.getImageDescriptor("mymemo16.png").createImage();
	}


	/**
	 * Connects to MyMemory Translation Memory, translates given String form
	 * English to {@code targetLanguage}, then returns translation as Suggestion object
	 * @param original is the original text that is going be translated.
	 * @param targetLanguage should be in ISO 639-1 Code, e.g "de" for GERMAN.
	 * @return suggestion object
	 */
	@Override
	public Suggestion getSuggestion(String original, String targetLanguage) {

		if(original == null || targetLanguage == null ||
				original.equals("") || targetLanguage.equals("")){
			throw new IllegalArgumentException();
		}


		//REST GET
		String langpair = "langpair=en|" + targetLanguage.toLowerCase();
		String restUrl = URL + original + "&"+ langpair;
		restUrl = restUrl.replaceAll("\\s", "%20");

		URL url;
		StringBuilder sb;

		try {
			url = new URL(restUrl);
		} catch (MalformedURLException e) {
			//TODO logging
			return new Suggestion(icon,"No suggestions available");
		}
		
		HttpURLConnection conn;

		try {
			conn = (HttpURLConnection) url.openConnection();


			if (conn.getResponseCode() != 200) {
				return new Suggestion(icon,"No suggestions available");
				//TODO logging
				//			throw new RuntimeException("Failed : HTTP error code : "
				//					+ conn.getResponseCode());
			}

			BufferedReader br;

			br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream()), "UTF-8"));

			sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			// System.out.println("apache "+StringEscapeUtils.unescapeJava(sb.toString()));

			br.close();
		} catch (IOException e) {
			//TODO logging
			return new Suggestion(icon,"No suggestions available");
		}

		conn.disconnect();

		JsonElement jelement = new JsonParser().parse(sb.toString());
		JsonObject  jobject = jelement.getAsJsonObject();
		jobject = jobject.getAsJsonObject("responseData");
		String translatedText = jobject.get("translatedText").toString().replaceAll("\"", "");

		if(translatedText.contains("IS AN INVALID TARGET LANGUAGE")){
			return new Suggestion(icon,"No suggestions available");
		}

		return new Suggestion(icon,translatedText);
	}

}
