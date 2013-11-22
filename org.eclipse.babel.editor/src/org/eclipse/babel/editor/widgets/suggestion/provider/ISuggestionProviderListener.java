package org.eclipse.babel.editor.widgets.suggestion.provider;


/** Listener interface for {@link ISuggestionProvider}s. Defines the
 * method to call when an update event occurs.
 * @author Samir Soyer
 *
 */
public interface ISuggestionProviderListener {
	
	
	/**
	 * This method will be called after a {@link ISuggestionProvider} is updated.
	 * e.q if resource of a suggestion provider is updated after creating the object.
	 * @param provider is the suggestion provider which was updated
	 * @param index is the index of the updated suggestion provider in the suggestion
	 * provider list of {@link SuggestionProviderUtils}.
	 */
	public void suggestionProviderUpdated(ISuggestionProvider provider, int index);
}
