package org.eclipse.babel.editor.widgets.suggestion.provider;

import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;


/**
 * Interface for the suggestion providers which should implement
 *  {@link ISuggestionProvider.getSuggestion()} method to return
 *  provided suggestion
 * @author Samir Soyer
 *
 */
public interface ISuggestionProvider {
	
	/**
	 * Returns translation of the original text to a given language
	 * 
	 * @param original is the untranslated string
	 * @param targetLanguage is the language, to which the original text will
	 * be translated
	 * @return translation of original text
	 */
	public Suggestion getSuggestion(String original, String targetLanguage);
}
