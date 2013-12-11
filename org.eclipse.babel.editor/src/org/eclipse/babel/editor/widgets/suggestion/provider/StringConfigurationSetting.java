package org.eclipse.babel.editor.widgets.suggestion.provider;

/**
 * This class contains configuration setting for
 * {@link ISuggestionProvider} in string format,
 * i.e object that contains the configuration setting
 * is a string. 
 * @author Samir Soyer
 *
 */
public class StringConfigurationSetting implements
		ISuggestionProviderConfigurationSetting {
	
	private String config;
	
	/**
	 * Constructor
	 * @param config is the string that contains the
	 * configuration, e.g {@code "/home/xyz/file.xml"}
	 */
	public StringConfigurationSetting(String config) {
		super();
		this.config = config;
	}

	/**
	 * @return configuration as string
	 */
	public String getConfig() {
		return config;
	}

	/**
	 * @param config is the string that contains the
	 * configuration, e.g {@code "/home/xyz/file.xml"
	 */
	public void setConfig(String config) {
		this.config = config;
	}

	/**
	 * @see org.eclipse.babel.editor.widgets
	 * .suggestion.provider
	 * .ISuggestionProviderConfigurationSetting#getConfigurationSetting()
	 */
	@Override
	public Object getConfigurationSetting() { 
		return config;
	}

}
