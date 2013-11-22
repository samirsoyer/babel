package org.eclipse.babel.editor.widgets.suggestion.model;

import org.eclipse.swt.graphics.Image;

/**
 * Encapsulates text of the suggestion and icon of the suggestion
 * provider, which provides the respective translation.
 * @author Samir Soyer
 *
 */
public class Suggestion {
	
	private Image icon;
	private String text;

	/**
	 * @param icon is the image of suggestion provider which 
	 * provides the translation of the text
	 * @param text is the translated suggestion
	 */
	public Suggestion(Image icon, String text) {
		this.icon = icon;
		this.text = text;
	}

	/**
	 * @return Image object of the suggestion provider which 
	 * provides the suggestion
	 */
	public Image getIcon() {
		return icon;
	}
	/**
	 * @param icon Image object of the suggestion provider which 
	 * provides the suggestion
	 */
	public void setIcon(Image icon) {
		this.icon = icon;
	}
	/**
	 * @return translated text, i.e suggestion
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * @param text is the translated text, i.e suggestion
	 */
	public void setText(String text) {
		this.text = text;
	}
	
}
