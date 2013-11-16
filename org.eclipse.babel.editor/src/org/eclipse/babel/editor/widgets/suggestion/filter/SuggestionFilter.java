package org.eclipse.babel.editor.widgets.suggestion.filter;

import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SuggestionFilter extends ViewerFilter {

	private String searchString;

	public void setSearchText(String s) {

		// ensure that the value can be used for matching 	
		
		this.searchString = ".*" + s + ".*";

	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null || searchString.length() == 0) {
			return true;
		}

		Suggestion s = (Suggestion) element;		
		if (s.getText().toLowerCase().matches(searchString.toLowerCase())) {
			return true;
		}

		return false;
	}
} 