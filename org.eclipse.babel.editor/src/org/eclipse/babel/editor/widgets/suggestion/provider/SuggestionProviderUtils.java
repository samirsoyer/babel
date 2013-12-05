package org.eclipse.babel.editor.widgets.suggestion.provider;

import java.util.ArrayList;

/**
 * This class contains a list of all suggestion providers.
 * {@link org.eclipse.babel.editor.widgets.suggestion.SuggestionBubble} gets its suggestion providers from this class,
 * therefore the ones that should be used in SuggestionBubble must be registered
 * in this class by calling {@link SuggestionProviderUtils.addSuggestionProvider()}
 * method
 * @author Samir Soyer
 *
 */
public class SuggestionProviderUtils {
	private static ArrayList<ISuggestionProvider> providers = new ArrayList<ISuggestionProvider>();
	private static ArrayList<ISuggestionProviderListener> listeners = new ArrayList<ISuggestionProviderListener>();

	/**
	 * Adds suggestion provider object to the list
	 * @param provider is the suggestion provider to be registered
	 */
	public static void addSuggestionProvider(ISuggestionProvider provider) {
		providers.add(provider);
	}

	public static void removeSuggestionProvider(ISuggestionProvider provider) {
		providers.remove(provider);
	}
	
	/**
	 * @return all the registered suggestion providers
	 */
	public static ArrayList<ISuggestionProvider> getSuggetionProviders(){
		return providers;
	}

	/**
	 * Adds a new suggestion provider listener, which calls 
	 * {@link ISuggestionProviderListener.suggestionProviderUpdated()} method, when a
	 * suggestion provider object is updated
	 * @param listener is the object, whose 
	 * {@link ISuggestionProviderListener.suggestionProviderUpdated()} will be called, 
	 * when the suggestion provider is updated
	 */
	public static void addSuggestionProviderUpdateListener(ISuggestionProviderListener listener){
		listeners.add(listener);
	}

	/**
	 * This method is to call after updating a provider
	 */
	public static void fireSuggestionProviderUpdated(ISuggestionProvider provider){
		for(ISuggestionProviderListener listener : listeners){
			listener.suggestionProviderUpdated(provider, SuggestionProviderUtils.
					getSuggetionProviders().indexOf(provider));
		}
	}
}
