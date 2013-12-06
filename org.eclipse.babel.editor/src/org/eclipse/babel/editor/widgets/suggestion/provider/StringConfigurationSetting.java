package org.eclipse.babel.editor.widgets.suggestion.provider;

public class StringConfigurationSetting implements
		ISuggestionProviderConfigurationSetting {
	
	private String config;
	
	//TODO javadoc
	public StringConfigurationSetting(String config) {
		super();
		this.config = config;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	@Override
	public Object getConfigurationSetting() { 
		return config;
	}

}
