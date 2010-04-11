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
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Some useful static methods.
 *
 * @author Johan Degraeve
 *
 */
public class Utilities {
    /**
     * gets the class name without the package name
     * @param c the class
     * @return the class  name
     */
    @SuppressWarnings("unchecked")
    static public String getClassname (Class c) {
	    String FQClassName = c.getName();
	    int firstChar;
	    firstChar = FQClassName.lastIndexOf ('.') + 1;
	    if ( firstChar > 0 ) {
	      FQClassName = FQClassName.substring ( firstChar );
	      }
	    return FQClassName;
    }
    
    /**
     * calls  {@link #makeEndTag(String, int)} with indent = 0
     * @param tag
     * @return string
     */
    /*static public String makeEndTag(String tag) {
	return makeEndTag(tag, 0);
    }*/
    
    /**
     * returns the an endtag to be used for creating an XML : &lt;/ ..the tag name ..&gt;<br>
     * @param tag
     * @param indent
     * @return string
     */
    /*static private String makeEndTag(String tag, int indent) {
	StringBuilder returnvalue = new StringBuilder();
	for (int i = 0;i < indent;i++) {
	    returnvalue.append(" ");
	}
	returnvalue.append("</" + tag  + ">\n");
	return returnvalue.toString();
    }*/

    
    /**
     * calls  {@link #makeStartTag(String, int)} with indent = 0
     * @param tag
     * @return string
     */
   /* static public String makeStartTag(String tag) {
	return makeStartTag(tag, 0);
    }*/

    /**
     * make the start to be used for creating an XML : &lt; ..the tag name ..&gt;<br>
     * @param tag
     * @param indent
     * @return string
     */
    static private String makeStartTag(String tag, int indent) {
	StringBuilder returnvalue = new StringBuilder();
	for (int i = 0;i < indent;i++) {
	    returnvalue.append(" ");
	}
	returnvalue.append( "<" + tag  + ">\n");
	return returnvalue.toString();
    }

    /**
     * Used for converting simple  text elements to XML, it will generate the starttag, text and endtag.<br>
     * If the element contains attributes then this can't be used.
     * @param input the text, if null the no text is added
     * @param indent required indentation
     * @param tag the tag of the element
     * @return String
     */
    /*static private String toString(String input, int indent, String tag) {
	StringBuilder returnvalue = new StringBuilder();
	returnvalue.append(makeStartTag(tag,indent));
	if (input != null) {
	    for (int i = 0;i < indent + 3;i++)
		returnvalue.append(" ");
	    returnvalue.append("<![CDATA[");
	    returnvalue.append(input);
	    returnvalue.append("]]>");
	    returnvalue.append("\n");
	}
	returnvalue.append(makeEndTag(tag, indent));
	return returnvalue.toString();
    }*/
    
    /**
     * Build XML for Element with text and attributes.<br>
     * @param input the text, if null then no text is added
     * @param indent the indentation to be used
     * @param tag the tag name for the element
     * @param attr the attributes
     * @return XML
     */
    /*static private String toStringWithAttributes(String input, int indent, String tag, Attributes attr) {
	StringBuilder returnvalue = new StringBuilder();
	returnvalue.append(makeStartTag(tag + " " + attributesToString(attr),indent));
	if (input != null) {
	    for (int i = 0;i < indent + 3;i++)
		returnvalue.append(" ");
	    returnvalue.append("<![CDATA[");
	    returnvalue.append(input);
	    returnvalue.append("]]>");
	    returnvalue.append("\n");
	}
	returnvalue.append(makeEndTag(tag, indent));
	return returnvalue.toString();
    }*/
    
    /**
     * For each qName in attributeqNames, search in attributes if it's found and return the value.<br>
     * If qName is not found, then a SAXParseException will be thrown with Locator = null.<br>
     * @param xmlElement will be used to get the className, to be used in case an Exception needs to be generated, for the text string
     * @param attributes to be searched in
     * @param attributeqNames the list of mandatory attributes
     * @return the list of values in the same order as the names in attributeqNames, null if attributeqNames = null
     * @throws SAXException in case a name is not found in the attributes
     */
    static public String[] getMandatoryAttributeValues(XMLElement xmlElement , Attributes attributes, String[] attributeqNames)  throws SAXException {
	
	if (attributeqNames == null) return null;
	
	StringBuilder exceptionString = new StringBuilder();
	String[] returnvalue = new String[attributeqNames.length];
	
	for (int i = 0; i < attributeqNames.length; i++) {
	     if (attributes.getValue(attributeqNames[i]) == null) {
		exceptionString.append("Element " + getClassname(xmlElement.getClass()));
		exceptionString.append(" is missing attribute " + attributeqNames[i]);
		throw new SAXException(exceptionString.toString());
	    } else {
		returnvalue[i] = attributes.getValue(attributeqNames[i]);
	    }
	}
	return returnvalue;
    }
    
    /**
     * search in attributes for all names in attributeqNames, and if found gets the corresponding value in attributes and returns it.
     * @param attributes to search in
     * @param attributeqNames the list off attributenames sto search for
     * @param defaultValues the default values in the same order as in attributeqNames
     * @return if a qname is not found, returns the default value, if a qname is found, returns the corresponding value
     */
    static public String[]  getOptionalAttributeValues(Attributes attributes, String[] attributeqNames,String[] defaultValues ) {
	
	if (attributeqNames == null) return null;
	
	String[] returnvalue = defaultValues;
	
	for (int i = 0; i < attributes.getLength(); i ++) {
	    for (int j = 0; j < attributeqNames.length; j++) {
		if (attributes.getQName(i).equals(attributeqNames[j]))
		    returnvalue[j] = attributes.getValue(i);
	    }
	}
	return returnvalue;
    }
    
    /**
     * useful for building the XML for an Element with attributes.<br>
     * @param attributes
     * @return list of attributesName=&quot;attribute value&quot; separated by blanks
     */
    static public String attributesToString(Attributes attributes) {
	StringBuilder returnvalue = new StringBuilder();
	for (int i = 0; i < attributes.getLength(); i++) {
	    returnvalue.append(
		    " " + 
		    attributes.getQName(i) + 
		    "=\"" + 
		    attributes.getValue(i) +
		    "\"");
	}
	return returnvalue.toString();
    }
    
    /**
     * to verify if a certain child type is allowed, if none of the childNames is allowed, then a SAXException is thrown<br>
     * Throwing an exception is the actual return value of this method.<br>
     * @param child the child
     * @param tagPrefixes tagPrefixes + childNames form the names of allowed children for the parentName, tagPrefix can be null,
     * tagPrefixes and childNames should have the same length
     * @param childNames tagPrefix + childNames form the names of allowed children for the parentName, childNames can be null
     * tagPrefixes and childNames should have the same length
     * @param parentName the parentName
     * @throws SAXException in case none of the childnames is allowed, an exception is thrown with explanation text that lists the allowed children
     */
    public static void verifyChildType(XMLElement child, String[] tagPrefixes,String[] childNames, String parentName) throws SAXException {
	
	if (childNames != null)
	    if (childNames.length != tagPrefixes.length)
		throw new IllegalArgumentException("Method verifyChildType in Utilities class : childNames and tagPrefixes should have the same length");

	if (childNames == null) {
	    if (child != null)
		throw new SAXException("No children allowed for element of type" + parentName);
	} else
	for (int i = 0; i < tagPrefixes.length; i++) {
	    if (Utilities.getClassname(child.getClass()).equals(
		tagPrefixes[i] +
		childNames[i]))
		return;
	}
	//non of the childNames matched the child class name
	StringBuilder exceptionString = new StringBuilder();
	exceptionString.append("Invalid childname for " + parentName +
		".\n");
	if (childNames.length > 0) 
	    if (!((childNames.length == 1) && (childNames[0].equalsIgnoreCase("")))){
		exceptionString.append("Allowed children are :\n");
		for (int i =  0; i < tagPrefixes.length; i++) {
		    exceptionString.append(childNames[i] + "\n");
		}
	    }    
	throw new SAXException(exceptionString.toString());
    }
    
    /**
     * a version of {@link #verifyChildType(XMLElement, String[], String[], String)} where only one tagPrefix is supplied
     * @param child the child
     * @param tagPrefix tagPrefix + childNames form the names of allowed children for the parentName, tagPrefix can be null
     * @param childNames tagPrefix + childNames form the names of allowed children for the parentName, childNames can be null
     * @param parentName the parentName
     * @throws SAXException in case none of the childnames is allowed, an exception is thrown with explanation text that lists the allowed children
     */
    public static void verifyChildType(XMLElement child, String tagPrefix ,String[] childNames, String parentName) throws SAXException {
	String[] tagPrefixes = null;
	if (childNames != null) {
	    tagPrefixes = new String[childNames.length];
	    for (int i = 0;i < childNames.length;i++)
		tagPrefixes[i] = tagPrefix;
	}
	verifyChildType(child, tagPrefixes, childNames, parentName);
    }
    
    /**
     * create XML representation, does not include XML declaration
     * @param input
     * @return one string with the XML representation
     */
    public static String createXML(XMLElement input) {
	return createXML(input, 0);
    }
    
    /**
     * create XML representation, does not include XML declaration
     * @param input 
     * @param indent starting indentation 
     * @return one string with the XML representation
     */
    public static String createXML(XMLElement input, int indent) {
	if (input == null) return null;
	StringBuilder returnvalue = new StringBuilder();
	ArrayList<XMLElement> list;
	
	String attributes = (input.getAttributes() == null ? "": Utilities.attributesToString(input.getAttributes()));
	returnvalue.append(Utilities.makeStartTag(input.getTagName() + attributes,indent));
	if (input.getText() != null) {
	    if(input.preserveSpaces())
		//stripp off the '>' added by makeStartTag
		returnvalue.deleteCharAt(returnvalue.length() -1);
	    if(!input.preserveSpaces())
		//shifting to the right, as far as the current value of the indentation
		for (int i = 0;i < indent;i ++) 
		    returnvalue.append(" ");
	    if(!input.preserveSpaces())
		//add three blanks, shifting the text to the right by three spaces
		returnvalue.append("   ");
	    returnvalue.append("<![CDATA[" + input.getText() + "]]>");
	    if (!input.preserveSpaces())
		returnvalue.append("\n");
	}
	list = input.getChildren();
	if (list != null) {
	    for (int i = 0;i < list.size();i++)
		returnvalue.append(createXML(list.get(i),indent + 3));
	}
	if (!input.preserveSpaces())
	    for (int i = 0;i < indent;i++) {
		returnvalue.append(" ");
	    }
	returnvalue.append("</" + input.getTagName()  + ">\n");
	return returnvalue.toString();
    }
    
    /**
     * creates an ArrayList of XMLElement, with one XMLElement
     * @param newElement
     * @return the new ArrayList
     */
    static public ArrayList<XMLElement> createXMLElementList(XMLElement newElement) {
	ArrayList<XMLElement>  returnvalue = new ArrayList<XMLElement>();
	returnvalue.add(newElement);
	return returnvalue;
    }

}
