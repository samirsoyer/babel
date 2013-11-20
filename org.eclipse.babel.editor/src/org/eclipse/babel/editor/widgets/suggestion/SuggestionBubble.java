/*******************************************************************************
 * Copyright (c) 2013 Samir Soyer.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Samir Soyer - initial API and implementation
 ******************************************************************************/
package org.eclipse.babel.editor.widgets.suggestion;

import java.util.ArrayList;

import org.eclipse.babel.editor.widgets.suggestion.exception.SuggestionErrors;
import org.eclipse.babel.editor.widgets.suggestion.filter.SuggestionFilter;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.babel.editor.widgets.suggestion.provider.ISuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.provider.ISuggestionProviderListener;
import org.eclipse.babel.editor.widgets.suggestion.provider.SuggestionProviderUtils;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Auto complete pop-up dialog that displays translation suggestions from
 * a given text to a target language. Detecting the source language depends on
 * the implementation of {@link ISuggestionProvider}.
 * 
 * @author Samir Soyer
 */
public class SuggestionBubble implements ISuggestionProviderListener{

	// TODO documentation

	private PopupDialog dialog;
	private TableViewer tableViewer;
	private Text text;
	private Shell shell;
	private Point caret;
	private SuggestionFilter suggestionFilter;
	private Composite composite;
	private Label noSug;
	private PartialTranslationDialog partialTranslationDialog;
	private ArrayList<Suggestion> suggestions;
	//	private static ArrayList<ISuggestionProvider> suggestionProviders;
	private String targetLanguage;
	private static String defaultText;
	private String oldDefaultText = "";


	/**
	 * Constructor
	 * 
	 * @param parent is the parent {@link Text} object, to which SuggestionBubble
	 * will be added.
	 * @param targetLanguage is the language, to which the {@link SuggestionBubble.defaultText}
	 * will be translated
	 */
	public SuggestionBubble(Text parent, String targetLanguage) {
		shell = parent.getShell();
		text = parent;
		this.targetLanguage=targetLanguage;

		//		suggestionProviders = new ArrayList<ISuggestionProvider>();
		suggestionFilter = new SuggestionFilter();
		suggestions = new ArrayList<Suggestion>();

		//		MessagesEditorPlugin.getDefault().getBundle().
		//		getEntry("glossary.xml").getPath()

		//		System.out.println("install path "+MessagesEditorPlugin.getDefault().getBundle().getEntry("/").getPath()+"glossary.xml");

		SuggestionProviderUtils.addSuggestionProviderUpdateListener(this);

		init();
	}

	//	public static void addSuggestionProvider(ISuggestionProvider suggestionProvider) {
	//		if(suggestionProviders == null){
	//			suggestionProviders = new ArrayList<ISuggestionProvider>();
	//		}
	//		suggestionProviders.add(suggestionProvider);
	//	}
	//
	//	public static void removeSuggestionProvider(ISuggestionProvider suggestionProvider) {
	//		if(suggestionProviders != null){
	//			suggestionProviders.remove(suggestionProvider);
	//		}	
	//	}

	public static String getDefaultText() {
		return defaultText;
	}

	public static void setDefaultText(String defaultText) {
		SuggestionBubble.defaultText = defaultText;		
	}

	private void updateSuggestions() {		
		if(!oldDefaultText.equals(defaultText)){

			ArrayList<ISuggestionProvider> providers =
					SuggestionProviderUtils.getSuggetionProviders();


			suggestions.clear();

			for (ISuggestionProvider provider : providers) {
				suggestions
				.add(provider.getSuggestion(defaultText, targetLanguage));
			}
			oldDefaultText = defaultText;
		}
	}

	public boolean isCreated() {
		if (dialog != null && dialog.getShell() != null) {
			return true;
		} else {
			return false;
		}
	}

	public void dispose() {
		if (dialog != null)
			dialog.close();
	}

	private void init() {

		// ModifyListener
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				recalculatePosition();

				if (dialog != null && dialog.getShell() != null
						&& !tableViewer.getControl().isDisposed()) {
					suggestionFilter.setSearchText(text.getText().trim());
					tableViewer.refresh();

					if (tableViewer.getTable().getItemCount() == 0) {
						if (noSug == null || noSug.isDisposed()) {
							noSug = new Label(composite, SWT.NONE);
							noSug.setText("No suggestions available");
							noSug.moveAbove(tableViewer.getControl());
							noSug.setBackground(new Color(shell.getDisplay(),
									255, 255, 225));
							composite.layout();
						}
					} else {
						if (noSug != null && !noSug.isDisposed()) {
							tableViewer.getTable().setSelection(0);
							noSug.dispose();
							composite.layout();
						}
					}
					suggestionFilter.setSearchText("");
				}
			}

		});

		// KeyListener
		text.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.keyCode == SWT.CR || e.keyCode == SWT.LF)
						&& (dialog != null && dialog.getShell() != null)) {
					e.doit = false;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {

				// CTRL + SPACE listener
				if ((e.stateMask & SWT.CTRL) != 0 && e.character == ' ') {
					if (isCreated()) {
						if (noSug != null && !noSug.isDisposed()) {
							noSug.dispose();
							composite.layout();
						}
						suggestionFilter.setSearchText("");
						tableViewer.refresh();
						tableViewer.getTable().setSelection(0);
					} else {
						createDialog();
					}
				}

				if (dialog == null || dialog.getShell() == null) {
					return;
				}

				if (e.keyCode == SWT.ESC) {
					dialog.close();
					return;
				}

				// Changing selection with keyboard arrows and applying
				// translation with enter
				int currentSelectionIndex = tableViewer.getTable()
						.getSelectionIndex();

				if (e.keyCode == SWT.ARROW_DOWN) {
					if (currentSelectionIndex >= tableViewer.getTable()
							.getItemCount() - 1) {
						tableViewer.getTable().setSelection(0);
					} else {
						tableViewer.getTable().setSelection(
								currentSelectionIndex + 1);
					}
				}

				if (e.keyCode == SWT.ARROW_UP) {
					if (currentSelectionIndex <= 0) {
						tableViewer.getTable().setSelection(
								tableViewer.getTable().getItemCount() - 1);
					} else {
						tableViewer.getTable().setSelection(
								currentSelectionIndex - 1);
					}
				}

				if (e.keyCode == SWT.CR || e.keyCode == SWT.LF) {
					applySuggestion(text);
				}

			}

		});

		// FocusListener
		text.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {

				if (!isCreated() && text.getText().length() == 0) {
					createDialog();
				}

			}

			@Override
			public void focusLost(FocusEvent e) {
				if (dialog != null && !isCursorInsideDialog()) {
					dialog.close();
				}
			}

		});

		text.addMouseListener(new MouseListener() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// Nothing to do
			}

			@Override
			public void mouseUp(MouseEvent e) {

				if (caret != null) {
					if (dialog != null
							&& !caret.equals(text.getCaretLocation())) {
						dialog.close();
						caret = text.getCaretLocation();
					}
				} else {
					caret = text.getCaretLocation();
				}

				if (partialTranslationDialog != null
						&& !partialTranslationDialog.isCursorInsideDialog()) {
					partialTranslationDialog.setVisible(false, "");
				}
			}

		});

	}

	private void createDialog() {

		int shellStyle = PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE;
		boolean takeFocusOnOpen = false;
		boolean persistSize = false;
		boolean persistLocation = false;
		boolean showDialogMenu = false;
		boolean showPersistActions = false;
		String languageSource = "EN > DE";
		String titleText = "Suggestions (" + languageSource + ")";
		String infoText = "Ctrl+Space to display all suggestions";
		dialog = new PopupDialog(shell, shellStyle, takeFocusOnOpen,
				persistSize, persistLocation, showDialogMenu,
				showPersistActions, titleText, infoText) {

			@Override
			protected Control createDialogArea(Composite parent) {
				composite = (Composite) super.createDialogArea(parent);

				tableViewer = new TableViewer(composite, SWT.V_SCROLL);
				tableViewer.getTable().setLayoutData(
						new GridData(GridData.FILL_BOTH));

				tableViewer.setContentProvider(new ArrayContentProvider());
				tableViewer.setLabelProvider(new ITableLabelProvider() {

					@Override
					public Image getColumnImage(Object arg0, int arg1) {
						Suggestion s = (Suggestion) arg0;
						return s.getIcon();
					}

					@Override
					public String getColumnText(Object element, int index) {
						return ((Suggestion) element).getText();

					}

					@Override
					public void addListener(ILabelProviderListener listener) {
						// nothing to do
					}

					@Override
					public void dispose() {
						// nothing to do
					}

					@Override
					public boolean isLabelProperty(Object arg0, String arg1) {
						return true;
					}

					@Override
					public void removeListener(ILabelProviderListener arg0) {
						// nothing to do
					}
				});

				updateSuggestions();
				tableViewer.setInput(suggestions.toArray());

				tableViewer.addFilter(suggestionFilter);

				tableViewer.addDoubleClickListener(new DoubleClickListener() {

					@Override
					public void doubleClick(DoubleClickEvent event) {
						applySuggestion(text);
					}

				});

				tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					@Override
					public void selectionChanged(
							SelectionChangedEvent event) {
						if (tableViewer.getTable().getSelection().length > 0) {
							partialTranslationDialog.setVisible(true,
									tableViewer.getTable()
									.getSelection()[0]
											.getText());
						}
					}
				});

				// For Windows 7
				// Set background color of column line
				// tableViewer.getTable().addListener(SWT.EraseItem, new
				// Listener() {
				// @Override
				// public void handleEvent(Event event) {
				// event.gc.setBackground(new Color(shell.getDisplay(), 255,
				// 255, 225));
				// event.gc.fillRectangle(event.getBounds());
				// }
				// });

				tableViewer.getTable().setSelection(0);

				return composite;
			}

			@Override
			protected void adjustBounds() {
				super.adjustBounds();

				Point point = text.getCaretLocation();

				getShell().setLocation(text.toDisplay(1, 1).x + point.x + 5,
						text.toDisplay(1, 1).y + point.y + 20);

				getShell().setSize(450, 200);
			}

		};
		dialog.open();
		partialTranslationDialog = new PartialTranslationDialog(
				dialog.getShell(), this);
	}

	public Text getTextField() {
		return text;
	}

	public String getTargetLanguage() {
		return targetLanguage;
	}

	public void recalculatePosition() {
		caret = text.getCaretLocation();

		if (dialog != null && dialog.getShell() != null) {

			int oldCaretX = getCurrentLocation().x - (text.toDisplay(1, 1).x)
					- 5;
			int oldCaretY = getCurrentLocation().y - (text.toDisplay(1, 1).y)
					- 20;

			int newCaretX = caret.x;
			int newCaretY = caret.y;

			setLocation(getCurrentLocation().x + (newCaretX - oldCaretX),
					getCurrentLocation().y + (newCaretY - oldCaretY));

		}
	}

	public Point getCurrentLocation() {
		if (dialog != null && dialog.getShell() != null) {
			return dialog.getShell().getLocation();
			// return dialog.getShell().toDisplay(1, 1);
		}

		return new Point(0, 0);
	}

	public Point getSize() {
		return dialog.getShell().getSize();
	}

	private void setLocation(int x, int y) {
		if (dialog != null && dialog.getShell() != null)
			dialog.getShell().setLocation(new Point(x, y));
	}

	private void applySuggestion(Text text) {
		IStructuredSelection selection = (IStructuredSelection) tableViewer
				.getSelection();
		Suggestion suggestion = (Suggestion) selection.getFirstElement();

		String s = suggestion.getText();

		if(suggestion.getText().contains("% match")){
			s = suggestion.getText().replaceAll("[(].*[% match)]", "");
		}

		if(s.equals(SuggestionErrors.LANG_NOT_SUPPORT_ERR) ||
				s.equals(SuggestionErrors.CONNECTION_ERR)    ||
				s.equals(SuggestionErrors.NO_SUGESTION_ERR)|| 
				s.equals(SuggestionErrors.QUOTA_EXCEEDED) ||
				s.equals(SuggestionErrors.NO_GLOSSARY_FILE)){
			//Ignore call
			return;
		}

		text.setText(s);
		dialog.close();
	}

	private boolean isCursorInsideDialog() {

		if (dialog == null || dialog.getShell() == null) {
			return false;
		}

		Display d = Display.getCurrent();
		if (d == null) {
			d = Display.getDefault();
		}

		Point start = dialog.getShell().getLocation();
		Point size = dialog.getShell().getSize();
		Point end = new Point(size.x + start.x, size.y + start.y);

		if ((d.getCursorLocation().x > end.x || d.getCursorLocation().x < start.x)
				|| (d.getCursorLocation().y > end.y || d.getCursorLocation().y < start.y)) {
			return false;
		}
		return true;

	}

	@Override
	public void SuggestionProviderUpdated(ISuggestionProvider provider, int index) {

		if(suggestions.size() > index){
			suggestions
			.set(index,provider.getSuggestion(defaultText, targetLanguage));
		}else{
			suggestions.add(provider.getSuggestion(defaultText, targetLanguage));
		}

		if(tableViewer != null && !tableViewer.getTable().isDisposed()){			
			tableViewer.setInput(suggestions);
		}

	}
}

abstract class DoubleClickListener implements IDoubleClickListener {
}