package org.eclipse.babel.editor.widgets.suggestion.model;

import org.eclipse.swt.graphics.Image;

public class Suggestion {
	
	private Image icon;
	private String text;

	public Suggestion(Image icon, String text) {
		this.icon = icon;
		this.text = text;
	}

	public Image getIcon() {
		return icon;
	}
	public void setIcon(Image icon) {
		this.icon = icon;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
