package org.eclipse.babel.editor.widgets.suggestion;

import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;


public interface ISuggestionProvider {
	
	public Suggestion getSuggestion(String original, String targetLanguage);
}
