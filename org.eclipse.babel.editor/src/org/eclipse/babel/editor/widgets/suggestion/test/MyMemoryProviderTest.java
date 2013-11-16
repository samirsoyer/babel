package org.eclipse.babel.editor.widgets.suggestion.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.babel.editor.widgets.suggestion.provider.MyMemoryProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MyMemoryProviderTest {
	
	private MyMemoryProvider mp;
	private String originalText = "Instances of this class are" +
			" selectable user interface objects";
	
	private String translatedText = "Instanzen dieser Klasse werden " +
			"Objekte der Benutzeroberfl\u00e4che w\u00e4hlbar";
	
	private String targetLanguage = "de";
	
	@Before
	public void setUp() throws Exception {
		mp = new MyMemoryProvider();
	}
	
	@After
	public void tearDown() throws Exception {
		mp = null;
	}

	@Test
	public void testgetSuggestion() {
		Suggestion actual = null;
		
		try {
			actual = mp.getSuggestion(originalText, targetLanguage);
		} catch (Exception e) {
			fail();
		}
		
		Suggestion expected = new Suggestion(new Image(Display.getCurrent(),"icons/mymemo16.png"),translatedText);
		
		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());	
	}
	
	@Test
	public void testGetSuggestionWithTargetLanguageInUpperCase() {
		Suggestion actual = null;
		
		try {
			actual =  mp.getSuggestion(originalText, "DE");
		} catch (Exception e) {
			fail();
		}
		
		Suggestion expected = new Suggestion(new Image(Display.getCurrent(),"icons/mymemo16.png"),translatedText);
		
		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());		
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetSuggestionWithWrongLanguage() {
		mp.getSuggestion(originalText, "foo");
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetSuggestionWithNullParameter() {
		mp.getSuggestion(null, null);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testGetSuggestionShouldThrowIllegalArgumentException() {
		mp.getSuggestion("", "");
		
	}

}
