package org.eclipse.babel.editor.widgets.suggestion.provider;

import java.util.ArrayList;

public class SuggestionProviderUtils {
	private static ArrayList<ISuggestionProvider> providers = new ArrayList<ISuggestionProvider>();
	private static ArrayList<ISuggestionProviderListener> listeners = new ArrayList<ISuggestionProviderListener>();

	public static void addSuggestionProvider(ISuggestionProvider provider) {
		providers.add(provider);
	}

	public static void removeSuggestionProvider(ISuggestionProvider provider) {
		providers.remove(provider);
	}
	
	public static ArrayList<ISuggestionProvider> getSuggetionProviders(){
		return providers;
	}

	public static void addSuggestionProviderUpdateListener(ISuggestionProviderListener listener){
		listeners.add(listener);
	}

	/**
	 * This method is to call after making updating a provider
	 */
	public static void fireSuggestionProviderUpdated(ISuggestionProvider provider){
		for(ISuggestionProviderListener listener : listeners){
			listener.SuggestionProviderUpdated(provider, SuggestionProviderUtils.
					getSuggetionProviders().indexOf(provider));
		}
	}
}
