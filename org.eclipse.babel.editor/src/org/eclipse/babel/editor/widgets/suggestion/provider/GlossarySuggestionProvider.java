package org.eclipse.babel.editor.widgets.suggestion.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.babel.editor.widgets.suggestion.ISuggestionProvider;
import org.eclipse.babel.editor.widgets.suggestion.model.Suggestion;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class GlossarySuggestionProvider extends DefaultHandler implements ISuggestionProvider {

	private File glossaryFile;
	private Image icon;

	public GlossarySuggestionProvider(File glossaryFile) {
		super();
		this.glossaryFile = glossaryFile;
		this.icon = new Image(Display.getCurrent(),System.getProperty("user.dir")+"/icons/sample.gif");
//		this.icon = UIUtils.getImageDescriptor("sample.gif").createImage();
	}

	@Override
	public Suggestion getSuggestion(String original, String targetLanguage) {

		if(original == null || targetLanguage == null ||
				original.equals("") || targetLanguage.equals("")){
			throw new IllegalArgumentException();
		}

		XMLReader xr = null;

		try {
			xr = XMLReaderFactory.createXMLReader();
		} catch (SAXException e) {
			//			e.printStackTrace();
			return new Suggestion(icon,"No suggestions available");
		}

		XMLContentHandler handler = new XMLContentHandler(original,targetLanguage);
		xr.setContentHandler(handler);
		InputSource is = null;

		try {
			is = new InputSource(new FileInputStream(glossaryFile));
		} catch (FileNotFoundException e) {
						e.printStackTrace();
			return new Suggestion(icon,"No suggestions available");
		}

		try {
			xr.parse(is);
		} catch (IOException e) {
			//			e.printStackTrace();
			return new Suggestion(icon,"No suggestions available");
		} catch (SAXException e) {
			//			e.printStackTrace();
			return new Suggestion(icon,"No suggestions available");
		}

		String bestSuggestion = handler.getBestSuggestion();

		if(bestSuggestion.equals("")){

			return new Suggestion(icon,"No suggestions available");

		}

		return new Suggestion(icon,bestSuggestion);
	}

}
class XMLContentHandler extends DefaultHandler{

	private int percentage = 0;
	private boolean inID = false;
	private boolean inValue = false;
	private String id = "";
	private String value = "";
	private String defaultGlossaryEntry = "";
	private String original;
	private String targetLanguage;
	private String longestMatching = "";
	private String bestSuggestion = "";
	private String suggestion = "";

	public XMLContentHandler(String orginal, String targetLanguage){
		this.original=orginal;
		this.targetLanguage=targetLanguage;

	}

	public String getBestSuggestion(){
		if(!bestSuggestion.equals("")){
			return bestSuggestion+" ("+ percentage +"% match)"; 
		}
		return bestSuggestion;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if(localName.equals("id")){
			inID=true;
		}
		if(localName.equals("value")){
			inValue=true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if(localName.equals("translations")){

			if(original.toLowerCase().contains(defaultGlossaryEntry.toLowerCase())){
				if(defaultGlossaryEntry.length() > longestMatching.length()){

					longestMatching = defaultGlossaryEntry;

					bestSuggestion = suggestion;

					percentage = (int)(( ((double)longestMatching.length())/original.length() )*100);

				}
			}
		}

		if(localName.equals("translation")){
			id = "";
			value = "";
		}

		if(localName.equals("id")){
			inID=false;

		}
		if(localName.equals("value")){
			inValue=false;
		}
	}

	@Override
	public void endDocument() throws SAXException {

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if(inID){

			id = new String(ch,start,length);

			if(!value.equals("") && id.equals("Default")){
				defaultGlossaryEntry = value;
			}

			if(id.equals(targetLanguage.substring(0, 2).toLowerCase())){
				if(!value.equals("")){
					suggestion = value;
				}
			}

		}
		if(inValue){
			value = new String(ch,start,length);

			if(!id.equals("")){
				if(id.equals("Default")){
					defaultGlossaryEntry = value;
				}

				if(id.equals(targetLanguage.substring(0, 2).toLowerCase())){
					suggestion = value;
				}
			}
		}
	}
}
