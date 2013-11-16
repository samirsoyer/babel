package org.eclipse.babel.editor.widgets.suggestion.provider;

import org.eclipse.babel.editor.util.UIUtils;
import org.eclipse.babel.editor.widgets.suggestion.ISuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.swt.graphics.Image;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class MicrosoftTranslatorProvider implements ISuggestionProvider {

	private static final String CUSTOMER_ID = "65c20c30-6149-4601-b453-9ebe21ae58ac";
	private static final String ACCOUNT_KEY = "rcWKv9hos62Y86uc/Hqk6l5C9HCBwg4GUXXa6weNRlE";
	private Image icon;

	public MicrosoftTranslatorProvider(){
		Translate.setClientId(CUSTOMER_ID);
		Translate.setClientSecret(ACCOUNT_KEY);
		icon = UIUtils.getImageDescriptor("mt16.png").createImage();
//		icon = new Image(Display.getCurrent(),"icons/mt16.png");
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
	 * @return suggestion object
	 * 
	 * */
	@Override
	public Suggestion getSuggestion(String original, String targetLanguage) {
		
		if(original == null || targetLanguage == null ||
				original.equals("") || targetLanguage.equals("")){
			throw new IllegalArgumentException();
		}
		
		String translatedText = "";

		try {
			translatedText = Translate.execute(original, Language.AUTO_DETECT, Language.fromString(targetLanguage.toLowerCase()));
		} catch (Exception e) {
			//TODO logging 
			throw new IllegalArgumentException();
		}
		
		if(translatedText.toLowerCase().contains("exception")){
			return new Suggestion(icon,"No suggestions available");
		}

		return new Suggestion(icon,translatedText);
	}

}
