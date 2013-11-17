package org.eclipse.babel.editor.widgets.suggestion;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.babel.editor.plugin.MessagesEditorPlugin;
import org.eclipse.babel.editor.widgets.suggestion.filter.SuggestionFilter;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.babel.editor.widgets.suggestion.provider.GlossarySuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.provider.MicrosoftTranslatorProvider;
import org.eclipse.babel.editor.widgets.suggestion.provider.MyMemoryProvider;
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

public class SuggestionBubble {

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
	private ArrayList<ISuggestionProvider> suggestionProviders;
	private ArrayList<Suggestion> suggestions;
	// private String sourceLanguage;
	private String targetLanguage;
	public static String defaultText;

	public SuggestionBubble(Shell shell, Text text, String targetLanguage) {
		this.shell = shell;
		this.text = text;
		this.targetLanguage=targetLanguage;

		suggestionProviders = new ArrayList<ISuggestionProvider>();
		suggestionFilter = new SuggestionFilter();
		suggestions = new ArrayList<Suggestion>();

		suggestionProviders.add(new MicrosoftTranslatorProvider());
		suggestionProviders.add(new MyMemoryProvider());
		suggestionProviders.add(new GlossarySuggestionProvider(
				new File(System.getProperty("user.dir")+"/glossary.xml")));
		
//		System.out.println(System.getProperty("user.dir"));
		
//		MessagesEditorPlugin.getDefault().getBundle().
//		getEntry("glossary.xml").getPath()
		
//		C:\Users\Samir\Documents\SAMIR\WIEN\TU WIEN\INSO Bakkarbeit\TapiJI\workspace\plugins\org.eclipse.babel.editor
		
//		;
		
//		System.out.println("install path "+MessagesEditorPlugin.getDefault().getBundle().getEntry("/").getPath()+"glossary.xml");
		
		//TODO optimize performance (get translation only if default text is modified, etc)
		
		createListeners();
	}

	public void addSuggestionProvider(ISuggestionProvider suggestionProvider) {
		suggestionProviders.add(suggestionProvider);
		updateSuggestions();
	}

	public void removeSuggestionProvider(ISuggestionProvider suggestionProvider) {
		suggestionProviders.remove(suggestionProvider);
		updateSuggestions();
	}

	private void updateSuggestions() {

		suggestions.clear();

		for (ISuggestionProvider provider : suggestionProviders) {
			suggestions
					.add(provider.getSuggestion(defaultText, targetLanguage));
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

	private void createListeners() {
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
		
		//TODO filter No Suggestion
		
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
}

abstract class DoubleClickListener implements IDoubleClickListener {
}