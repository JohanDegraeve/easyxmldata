/*  
 *  Copyright (C) 2010  Johan Degraeve
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 *    
 *  Please contact Johan Degraeve at johan.degraeve@johandegraeve.net if you need
 *  additional information or have any questions.
 */
package net.johandegraeve.easyxmldata;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * To parse an XML page<br>
 * Extends {@link org.xml.sax.helpers.DefaultHandler} and overrides it methods to allow parsing an XML document.<br>
 *
 * @author Johan Degraeve
 *
 */
public class EasyXMLDataParser  extends DefaultHandler {
    
    /**
     * the root node retrieved from the XML page
     */
    private XMLElement rootFromXML = null;
    
    /**
     * Used to maintain the nodes being read from the XML file. As soon as a valid opening tag is encountered, a node will be added.<br>
     * When an end tag is encountered, a node is removed.  
     */
    private Stack<XMLElement> XMLObjectStack;
    
    /**
     * Used to keep the characters belonging to a node.  
     */
    private Stack<StringBuilder> stringBuilderStack;
    
    /**
     * Used together with {@link #XMLObjectStack}. To remember the prefixName used to find a class 
     * When an end tag is encountered, a node is removed.  
     */
    //private Stack<String> prefixNameStack;
    
    /**
     * the names off the packages to search for a class with the same name as a tag found in an XML document.
     * The field is used together with prefixNames : class to search will be packageNames[i] + prefixNames[i]
     */
    private String[] packagesNames;
    
    /**
     * the names of the prefixes to use when searching for a class
     */
    private String[] prefixNames;
    
    /**
     * the locate an event, see {@link org.xml.sax.Locator}
     */
    private Locator locator;

    /**
     * if true then unknown tags will be treated as {@link DefaultXMLElement}, if false then when unknown tag is encountered, an exception 
     * will be thrown. (unknown tag = corresponding class could not be created)
     */
    private boolean ignoreUnknownTags;

    /**
     * prefix name used for pushing on prefixname stack when creating defaultXMLElement
     */
    //private String defaultPrefixname = "QDFI67HJDHFJ234";
    
    /**
     * constructor<br>
     * packageNames and prefixNames can both be null but if one of them is not null then both should be not null.<br>
     * if packageNames and prefixNames are both null then the value of ignoreUnknownTags will be set to true no matter the input value.
     * @param packagesNames list of packagesNames to search for classes with same name as tags found
     * @param prefixNames list of prefixnames , by which founds tags should be prefixed while searching for classes
     * @param ignoreUnknownTags if true then if tags are found in the XML for which no corresponding class is found, a DefaultXMLELement will be created.
     */
    public EasyXMLDataParser (String[] packagesNames, String[] prefixNames, boolean ignoreUnknownTags) {
	this.packagesNames = packagesNames;
	this.prefixNames = prefixNames;
	if (packagesNames == null && prefixNames != null)
	    throw new NullPointerException("packageNames and prefixNames should either be both null or both non null");
	if (packagesNames != null && prefixNames == null)
	    throw new NullPointerException("packageNames and prefixNames should either be both null or both non null");
	this.ignoreUnknownTags = ignoreUnknownTags;
	if (packagesNames == null || prefixNames == null)
	    ignoreUnknownTags = true;
    }
    
    /**
     * constructor, createas an EasyXMLDataParser with ignoreUnknownTags = true<br>
     * packageNames nad prefixNames can both be null but if one of them is not null then both should be not null.
     * @param packagesNames list of packagesNames to search for classes with same name as tags found
     * @param prefixNames list of prefixnames , by which founds tags should be prefixed while searching for classes
     */
    public EasyXMLDataParser (String[] packagesNames, String[] prefixNames) {
	this(packagesNames, prefixNames,true);
    }
    
    /**
     * Opens the InputStream
     * @param feedUrl
     * @return an InputStream based on the feedUrl
     */
    static private InputStream getInputStream(URL feedUrl) {
        try {
            return feedUrl.openConnection().getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        stringBuilderStack.peek().append(ch, start, length);
    }

    /**
     * checks if end tag corresponds to current open element, if not throws SAXParseException<br>
     * Calls  {@link XMLElement#addText}, {XMLElement#addUnTrimmedText}, {@link XMLElement#complete} and {@link XMLElement#addChild} of the corresponding 
     * open element.<br>
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String name)
            throws SAXException {
	super.endElement(uri, localName, name);
	String textToAdd;
	
	try {
	    //add text from the builder
	    textToAdd = stringBuilderStack.peek().toString();
	    if (textToAdd != null)
		if (textToAdd.length() > 0) {
		    XMLObjectStack.peek().addUnTrimmedText(textToAdd);
		    textToAdd = textToAdd.trim();
		    if (textToAdd.length() > 0)
			XMLObjectStack.peek().addText(textToAdd);
		}
	    
	    //complete the object parsing
	    XMLObjectStack.peek().complete();
	    
	    //add the child to the parent
	    if (!XMLObjectStack.empty()) {
		if (XMLObjectStack.size() > 1) {
		    XMLElement popped = XMLObjectStack.pop();
		    //prefixNameStack.pop();
		    XMLObjectStack.peek().addChild(popped);
		}    
	    }
	    
	    //remove the StringBuilder from the stack
	    stringBuilderStack.pop();
	    
	    
	} catch (SAXException e) {
	    //instance of EasyXMLData class may throw an exception because it doesn't like the text, or because complete() failed
	    //or maybe, because it doesn't like the child
	    throw new SAXParseException(e.getMessage(), locator, e);
	}
        
    }

    /**
     * parses the xml which is located at the URL and return the result in a XMLElement 
     * @param url
     * @return the XMLElement  populated with the contents of the XML page
     * @throws SAXParseException is important to catch because it contains details about why and where the parsing failed
     */
    public XMLElement parse(URL url) throws SAXParseException {
	SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            parser.parse(getInputStream(url), this);
            return rootFromXML;
        } catch (Exception e) {
            throw new SAXParseException("Exception : \n" + 
        	    e.toString() +  "\n", locator);
        } 
    }

    /**
     * parses the xml which is in the supplied String parameter and return the result in a XMLElement<br>
     * a check is made to see if the first non-whitespace character is a <, in which case it is assumed to be XML. 
     * Otherwise an attempt is made to open it as a URL
     * @param source the XML page or a string representing the URL
     * @return the XMLElement  populated with the contents of the XML page
     * @throws SAXParseException is important to catch because it contains details about why and where the parsing failed
     */
    public XMLElement parse(String source) throws SAXParseException {
	return parse(source, Charset.defaultCharset().displayName());
    }

    /**
     * parses the xml which is in the supplied String parameter and return the result in a XMLElement<br>
     * a check is made to see if the first non-whitespace character is a <, in which case it is assumed to be XML. 
     * Otherwise an attempt is made to open it as a URL
     * @param source the XML page or a string representing the URL
     * @param charsetName in case the source is XML (and not a URL), then the source will be converted to byte array using the specified charsetName
     * @return the XMLElement  populated with the contents of the XML page
     * @throws SAXParseException is important to catch because it contains details about why and where the parsing failed
     */
    public XMLElement parse(String source, String charsetName) throws SAXParseException {
	BufferedInputStream inputStream;
	int counter = 0;
	SAXParserFactory factory = SAXParserFactory.newInstance();
	

        try {
            SAXParser parser = factory.newSAXParser();
            if (source != null)
        	while (source.charAt(counter) == ' ') 
        	    counter ++;
            if (source !=null && source.charAt(counter) != '<') {
        	inputStream = new BufferedInputStream(getInputStream(new URL(source)));
            } else {
        	inputStream = new BufferedInputStream(new ByteArrayInputStream(source.getBytes(charsetName)));
            }
            parser.parse(inputStream, this);
            return rootFromXML;
        } catch (Exception e) {
            throw new SAXParseException("Exception : \n" + 
        	    e.toString() +  "\n", locator);
        } 
    }

    
    
    /** 
     * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    @Override 
    public void setDocumentLocator (Locator locator) {
	this.locator = locator;
    }

    /**
     * Start of document.
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
	XMLObjectStack = new Stack<XMLElement>();
	//prefixNameStack = new Stack<String>();
	stringBuilderStack = new Stack<StringBuilder>();
    }
    
    /**
     * Tries to create a class with the same name as the Tag found in the XML page.<br>
     * Searches through the list of packages/prefixes defined in {@link #packagesNames} and {@link #prefixNames} :
     * For example if packagesNames[i] = &quot;mypackagename&quot;, prefixNames[i] = &quot;myPrefix&quot; then
     * an attempt will be made to create a class &quot;mypackagename.myPrefix<i>name</i>&quot;<br>
     * The list of package names and prefix names will be tried until creation of a class was successful. If no class could be 
     * created with any of the combination of packagename and prefix, then the local field ignoreUnknownTags is used. If true, a
     * {@link DefaultXMLElement} will be created. If false, an exception will be thrown.<br><br>
     * Calls {@link XMLElement#addAttributes(Attributes)}<br>
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     * @throws SAXException will contain a {@link org.xml.sax.Locator}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void startElement(String uri, String localName, String name,
        Attributes attributes) throws SAXException {
        super.startElement(uri, localName, name, attributes);
        
        Class clsHandler = null;
        String tagName = name;
        if (tagName == null)
            tagName = localName;
        if (tagName.length() == 0)
            tagName = localName;
        stringBuilderStack.push(new StringBuilder());
        
        try {
            //try to create an easyxmldata instance of type packagename.prefixname+name, with packagename
            //each of the list in packagenames, prefixname the corresponding prefix. If it fails, throws exception
            //which means, if the tag found is for instance "book", then go through the list of packages and prefixes and
            //try to instantiate an EasyXMLData instance of type packagename.prefixesbook
	    for (int i = 0;i < packagesNames.length; i++) {
	         try {
	             
	    	clsHandler = Class.forName(packagesNames[i] + "." + prefixNames[i] + tagName);
	    	try {
	    	    XMLObjectStack.push((XMLElement)clsHandler.newInstance());
	    	} catch (InstantiationException e) {
	    	    e.printStackTrace();
	    	} catch (IllegalAccessException e) {
	    	    SAXParseException ex = new SAXParseException(
	    		    "Tag : " +
	    		    tagName +
	    	    	    " found but corresponding class " +
	    	    	    clsHandler.getName() +
	    	    	    " does not allow access to no-argument constructor", 
	    	    	    locator);
	    	    throw ex;
	    	}
	    	//if following code is execute then it means the class was successfully created
	    	//so no need to go through the rest of the packageNames
	    	i = packagesNames.length;
	                 
	            try {
	    	    XMLObjectStack.peek().addAttributes(attributes);
	    	} catch (SAXException e) {
	    	    //instance of EasyXMLData class may throw an exception because it doesn't like the attributes
	    	    throw new SAXParseException(e.getMessage(), locator, e);
	    	}
	        } catch (ClassNotFoundException e) {
	    	if (i == (packagesNames.length - 1)) {
	    	    if (!ignoreUnknownTags) {
	    		SAXParseException ex = new SAXParseException(
	    			"Unknown tag received : " +
	    			tagName +
	    			".", 
	    			locator);
	    		throw ex;
	    	    } else {
	    		XMLObjectStack.push(new DefaultXMLElement().setTagName(tagName)).addAttributes(attributes);
	    	    }
	    	}
	        }
	    }
	} catch (NullPointerException e) {
	    // packagenames = null, ignore unknown tags
	    XMLObjectStack.push(new DefaultXMLElement().setTagName(tagName)).addAttributes(attributes);
	}
    }
    
    /**
     * Here's where finally {@link #rootFromXML} is assigned to the XMLElement of on top of the stack
     * {@link #XMLObjectStack}
     * @see org.xml.sax.helpers.DefaultHandler#endDocument()
     */
    @Override
    public void endDocument() {
	if (XMLObjectStack.size() > 0) {
	    rootFromXML = XMLObjectStack.peek();
	}
    }
    
    /**
     * overriding this because I'm interested in whitespace characters
     * @see org.xml.sax.helpers.DefaultHandler#ignorableWhitespace(char[], int, int)
     */
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        stringBuilderStack.peek().append(ch, start, length);
    }
}
