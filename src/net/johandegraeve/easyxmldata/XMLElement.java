package net.johandegraeve.easyxmldata;
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

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Defines methods to be implemented for every possible XML Element that can be created based on tags found in the parsed XML page.<br>
 * Per allowed tag in the xml, there must be one class that implements XMLElement and that has the same name as the name of the tag<br>
 * <br>It is also possible to define the custom classes with a Prefix string followed by the tag name. 
 * <br><br>
 * <b>Any class that implements XMLElement must have a public constructor with no arguments.</b><br>
 * <br>
 * The class implementing the methods should verify itself whether the received arguments are acceptable or not, and if not throw
 * a SAXException with a text explaining what is wrong. For instance there could be an attempt to add a child of a certain type (ie with a 
 * certain tag) which is not acceptable, so the implementation can throw a SAXException saying "child with tag ...  not allowed for tag .."
 * 
 * @author Johan Degraeve
 *
 */
public interface XMLElement {
    
    	/**
    	 * process the attributes received from the parser
    	 * @param attributes the attributes
    	 * @throws SAXException 
    	 */
    	public void addAttributes(Attributes attributes) throws SAXException;
    	
    	/**
    	 * Add a child. Called when end-tag of a child XML ELement is reached. The implementation must check if that child is allowed 
    	 * or not and if not throw a SAXException.
    	 * @param child
    	 * @throws SAXException in case for instance child contains invalid data
    	 */
    	public void addChild(XMLElement child) throws SAXException;
    	
    	/**
    	 * this method is always called by  as soon as the end element is reached, with the contents of the text found, if there
    	 * was no text between the start and end element, then the method is not called.<br> 
    	 * Before calling the method, text is trimmed with String.trim()
    	 * @param text
    	 * @throws SAXException
    	 */
    	public void addText(String text) throws SAXException;
    	
    	/**
    	 * this method is always called by  as soon as the end element is reached, with the contents of the text found, if there
    	 * was no text between the start and end element, then the method is not called.<br>
    	 * The text is not trimmed.
    	 * @param text
    	 * @throws SAXException
    	 */
    	public void addUnTrimmedText(String text) throws SAXException;
    	
    	/**
    	 * Should white spaces (and carriage return) be preserved when printing ?<br>
    	 * Used in method {#Utilities.createXML(int indent)}. <br>
    	 * If true then the text of the element will be printed by the createXML function
    	 * without adding carriage returns, without trimming the text field<br>
    	 * If false, then the createXML function will trim the text, carriage return will be added after the opening and 
    	 * before the closing tag of the element. (or before printing the first child element if any). <br>
    	 * Implementations will typically be aligned with the usage of {@link #addText(String)} and {@link #addUnTrimmedText(String)}. 
    	 * In case the implementation uses {@link #addText(String)}, then the return value will usually be false. In case the 
    	 * implementation uses {@link #addUnTrimmedText(String)}, then the return value will usually be true.
    	 * 
    	 * @return should white spaces be preserved or not
    	 */
    	public boolean preserveSpaces();

    	/**
    	 * Called when parsing of the object is completed. The implementation can verify if all children/text .. are correctly
    	 * received and if not throw a SAXException
    	 * @throws SAXException 
    	 */
    	public void complete() throws SAXException;
    	
    	/**
    	 * @return a String representation.
    	 */
    	public String toString();
    	
    	/**
    	 * get children elements, should return null if there aren't any
    	 * @return the children
    	 */
    	public ArrayList<XMLElement> getChildren();
    	
    	/**
    	 * get the text
    	 * @return the text part, can be null
    	 */
    	public String getText();
    	
    	/**
    	 * get the Attributes
    	 * @return the attributes
    	 */
    	public Attributes getAttributes();
    	
    	/**
    	 * get the tagname of the element
    	 * @return the name of the tag for this element
    	 */
    	public String getTagName();
    	
}
