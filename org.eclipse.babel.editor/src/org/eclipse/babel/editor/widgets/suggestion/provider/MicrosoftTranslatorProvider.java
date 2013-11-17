package org.eclipse.babel.editor.widgets.suggestion.provider;

import org.eclipse.babel.editor.widgets.suggestion.ISuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class MicrosoftTranslatorProvider implements ISuggestionProvider {

	private static final String CLIENT_ID = "tapiji_translator";
	private static final String CLIENT_SECRET = "+aQX87s1KwVOziGL3DgAdXIQu63K0nYDS7bNkh3XuyE=";
	private Image icon;

	public MicrosoftTranslatorProvider(){
		Translate.setClientId(CLIENT_ID);
		Translate.setClientSecret(CLIENT_SECRET);
//		icon = UIUtils.getImageDescriptor("mt16.png").createImage();
		icon = new Image(Display.getCurrent(),System.getProperty("user.dir")+"/icons/mt16.png");
	}


	/**
	 * Connects to Microsoft Translator, translates given String form
	 * English to {@code targetLanguage}, then returns translation as Suggestion object
	 * 
	 * Supported languages and their abbreviations are:
	 * <p><blockquote><pre>
	 * ARABIC("ar"),
	 * BULGARIAN("bg"),
	 * CATALAN("ca"),
	 * CHINESE_SIMPLIFIED("zh-CHS"),
	 * CHINESE_TRADITIONAL("zh-CHT"),
	 * CZECH("cs"),
	 * DANISH("da"),
	 * DUTCH("nl"),
	 * ENGLISH("en"),
	 * ESTONIAN("et"),
	 * FINNISH("fi"),
	 * FRENCH("fr"),
	 * GERMAN("de"),
	 * GREEK("el"),
	 * HAITIAN_CREOLE("ht"),
	 * HEBREW("he"),
	 * HINDI("hi"),
	 * HMONG_DAW("mww"),
	 * HUNGARIAN("hu"),
	 * INDONESIAN("id"),
	 * ITALIAN("it"),
	 * JAPANESE("ja"),
	 * KOREAN("ko"),
	 * LATVIAN("lv"),
	 * LITHUANIAN("lt"),
	 * MALAY("ms"),
	 * NORWEGIAN("no"),
	 * PERSIAN("fa"),
	 * POLISH("pl"),
	 * PORTUGUESE("pt"),
	 * ROMANIAN("ro"),
	 * RUSSIAN("ru"),
	 * SLOVAK("sk"),
	 * SLOVENIAN("sl"),
	 * SPANISH("es"),
	 * SWEDISH("sv"),
	 * THAI("th"),
	 * TURKISH("tr"),
	 * UKRAINIAN("uk"),
	 * URDU("ur"),
	 * VIETNAMESE("vi");
	 *  </pre></blockquote><p>
	 * 
	 * @param original is the original text that  is going be translated.
	 * @param targetLanguage should be in ISO 639-1 Code, e.g "de" for GERMAN.
	 * If not specified "zh-CHS" will be used for any variants of Chinese
	 * @return suggestion object
	 * 
	 * */
	@Override
	public Suggestion getSuggestion(String original, String targetLanguage) throws IllegalArgumentException{
		
		if(original == null || targetLanguage == null ||
				original.equals("") || targetLanguage.equals("")){
			throw new IllegalArgumentException();
		}
		
		targetLanguage=targetLanguage.toLowerCase();
		
		String translatedText = "";
		
		if(targetLanguage.contains("zh")){
			targetLanguage = "zh-CHS";
		}
		
		try {
			if(!Language.getLanguageCodesForTranslation().contains(targetLanguage)){
				return new Suggestion(icon,"Language not supported");
			}
		} catch (Exception e1) {
			return new Suggestion(icon,"Language not supported");
		}

		try {
			translatedText = Translate.execute(original, Language.AUTO_DETECT, Language.fromString(targetLanguage));
		} catch (Exception e) {
			//TODO logging 
			return new Suggestion(icon,"No suggestions available");
		}
		
		if(translatedText.toLowerCase().contains("exception")){
			return new Suggestion(icon,"No suggestions available");
		}
		
		System.out.println("translatedText "+translatedText);

		return new Suggestion(icon,translatedText);
	}

}
