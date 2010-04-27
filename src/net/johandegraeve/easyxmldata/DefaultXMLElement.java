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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Will be used for unrecognized tags, ie tags for which no corresponding class can be instantiated.<br>
 *
 * @author Johan Degraeve
 *
 */
public class DefaultXMLElement implements XMLElement {
    
    /**
     * the attributes, if any
     */
    private Attributes myAttributes;
    /**
     * list of XMLElement children, if any
     */
    private ArrayList<XMLElement> XMLElementList;
    /**
     * the text if any, trimmed
     */
    private String theText;

    /**
     * tag name as read from XML
     */
    private String tagName;

    /**
     * read the tag name
     * @return tagName
     */
    public String getTagName() {
	return tagName;
    }
    
    /**
     * sets the tagName
     * @param name
     * @return itself to allow chaining methods
     */
    /*package private */ DefaultXMLElement setTagName(String name) {
	tagName = name;
	return this;
    }
    
    /**
     * get the attributes
     * @return the attributes, null if no attributes
     */
    public Attributes getAttributes() {
	return myAttributes;
    }
    
    /**
     * get XMLElement
     * @param index
     * @return the XML element
     */
    public XMLElement getXMLElement(int index) {
	return XMLElementList.get(index);
    }
    
    /**
     * get the size of the XMLElementList
     * @return the size
     */
    public int getSize() {
	return XMLElementList.size();
    }
    
    /**
     * the text trimmed !
     * @return the trimmed text, null if no text received or empty string
     */
    public String getText() {
	return (theText.equalsIgnoreCase("") ? null : theText);
    }
    
    /**
     * get the children of this element
     * @return the children, null if no children
     */
    public ArrayList<XMLElement> getChildren() {
	
	if (XMLElementList.size() > 0) return XMLElementList;
	return null;
    }
    /**
     * default constructor
     */
    public DefaultXMLElement() {
	XMLElementList = new ArrayList<XMLElement>();
    }
    
    /**
     * constructor taking tagname as argument
     * @param tagName
     */
    public DefaultXMLElement(String tagName) {
	this();
	this.tagName = tagName;
    }
    
    /**
     * constructor
     * @param newElement
     */
    public DefaultXMLElement(XMLElement newElement) {
	myAttributes = newElement.getAttributes();
	tagName = newElement.getTagName();
	theText = newElement.getText();
	XMLElementList = newElement.getChildren();
    }
    
    /**
     * stores attributes in local field
     * @see net.johandegraeve.easyxmldata.XMLElement#addAttributes(org.xml.sax.Attributes)
     */
    public void addAttributes(Attributes attributes) throws SAXException {
	this.myAttributes = attributes;
    }

    /**
     * Adds child to local XMLElementList
     * @see net.johandegraeve.easyxmldata.XMLElement#addChild(net.johandegraeve.easyxmldata.XMLElement)
     */
    public void addChild(XMLElement child) throws SAXException {
	XMLElementList.add(child);
    }

    /**
     * Does nothing, as this implementation of {@link #XMLElementList} uses {@link #preserveSpaces()} = true.
     * @see net.johandegraeve.easyxmldata.XMLElement#addText(java.lang.String)
     */
    public void addText(String text) throws SAXException {
    }

    /**
     * assigns text
     * @see net.johandegraeve.easyxmldata.XMLElement#addUnTrimmedText(java.lang.String)
     */
    public void addUnTrimmedText(String text) throws SAXException {
	theText = text;;
    }

    /**
     * doesn't do anything
     * @see net.johandegraeve.easyxmldata.XMLElement#complete()
     */
    public void complete() throws SAXException {
    }

    /** 
     * @see net.johandegraeve.easyxmldata.XMLElement#preserveSpaces()
     * This implementation returns true;
     */
    public boolean preserveSpaces() {
	return true;
    }

 }
