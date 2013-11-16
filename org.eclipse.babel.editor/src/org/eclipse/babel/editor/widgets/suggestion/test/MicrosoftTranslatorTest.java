package org.eclipse.babel.editor.widgets.suggestion.test;

import static org.junit.Assert.*;

import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.babel.editor.widgets.suggestion.provider.MicrosoftTranslatorProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MicrosoftTranslatorTest {
	
	MicrosoftTranslatorProvider mtp;
	
	String originalText = "Instances of this class are selectable user " +
			"interface objects that allow the user to enter and modify text." +
			" Text controls can be either single or multi-line."; 
			
	String targetLanguage = "de";
	
	String translatedText = "Instanzen dieser Klasse sind wählbar " +
			"Benutzer-Interface-Objekte, mit denen den Benutzer eingeben " +
			"und Ändern von Text. Text-Steuerelemente können entweder " +
			"ein- oder mehrzeiligen sein.";

	@Before
	public void setUp() throws Exception {
		mtp = new MicrosoftTranslatorProvider();
	}

	@After
	public void tearDown() throws Exception {
		mtp = null;
	}

	/**According to Microsoft Translator API, empty source language string should cause to detect source
	 * language automatically. If the resulting suggestion doesn't match with already translated suggestion 
	 * (by Microsoft Translator) test fails.
	 * */
	@Test
	public void testGetSuggestion() {
		
		Suggestion actual = null;
		
		try {
			actual = mtp.getSuggestion(originalText, targetLanguage);
		} catch (Exception e) {
			fail();
		}
				
		
		Suggestion expected = new Suggestion(new Image(Display.getCurrent(),"icons/mt16.png"),translatedText);
		
		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());		
	}
	
	@Test
	public void testGetSuggestionWithTargetLanguageInUpperCase() {
		Suggestion actual = null;
		
		try {
			actual =  mtp.getSuggestion(originalText, "DE");
		} catch (Exception e) {
			fail();
		}
		
		Suggestion expected = new Suggestion(new Image(Display.getCurrent(),"icons/mt16.png"),translatedText);
		
		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());		
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetSuggestionWithWrongLanguage() {
		mtp.getSuggestion(originalText, "foo");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetSuggestionWithNullParameter() {
		mtp.getSuggestion(null, null);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetSuggestionShouldThrowIllegalArgumentException() {
		mtp.getSuggestion("", "");
		
	}

}
