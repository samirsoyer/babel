package org.eclipse.babel.editor.widgets.suggestion.test;

import static org.junit.Assert.*;

import java.io.File;

import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.babel.editor.widgets.suggestion.provider.GlossarySuggestionProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GlossarySuggestionProviderTest {

	private GlossarySuggestionProvider gsp;
	private String originalText = "Instance of this class are selectable";
	private String translatedText = "Instanz dieser Klasse (59% match)";
	private String targetLanguage = "de";

	/**Test glossary */
	private File glossaryFile = new File("glossary.xml");

	@Before
	public void setUp() throws Exception {
		gsp = new GlossarySuggestionProvider(glossaryFile);
	}

	@After
	public void tearDown() throws Exception {
		gsp = null;
	}

	@Test
	public void testGetSuggestion() {
		Suggestion actual = null;

		try {
			actual = gsp.getSuggestion(originalText, targetLanguage);
		} catch (Exception e) {
			fail();
		}

		Suggestion expected = new Suggestion(new Image(Display.getCurrent(),
				"icons/sample.gif"), translatedText);

		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());
	}

	@Test
	public void testGetSuggestionWithTargetLanguageInUpperCase() {
		
		Suggestion actual = null;

		try {
			actual = gsp.getSuggestion(originalText, "DE");
		} catch (Exception e) {
			fail();
		}

		Suggestion expected = new Suggestion(new Image(Display.getCurrent(),
				"icons/sample.gif"),translatedText);

		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());
	}

	@Test
	public void testGetSuggestionWithWrongLanguage() {
		Suggestion actual = null;
		
		try {
			actual = gsp.getSuggestion(originalText, "foo");
		} catch (Exception e) {
			fail();
		}

		assertEquals("No suggestions available", actual.getText());

	}

	@Test
	public void testGetSuggestionCaseInsensitive() {
		Suggestion actual = null;
		
		try {
			actual = gsp.getSuggestion(originalText.toLowerCase(), targetLanguage);
		} catch (Exception e) {
			fail();
		}

		Suggestion expected = new Suggestion(new Image(Display.getCurrent(),
				"icons/sample.gif"),translatedText);

		assertNotNull(actual);
		assertEquals(expected.getText(), actual.getText());

	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetSuggestionWithNullParameter() {
		gsp.getSuggestion(null, null);
	}

	@Test (expected = IllegalArgumentException.class)
	public void testGetSuggestionShouldThrowIllegalArgumentException() {
		gsp.getSuggestion("", "");

	}

}
