# Introduction #

You want to parse an XML document and create corresponding classes that will hold the parsed XML elements. Easyxmldata allows you to create your own classes that will represent the XML elements. For each possible tag that can be foud in the XML document, you need to define a custom class with the same name. Each class needs to implement the interface XMLElement and must define a public consructor.

# JavaDoc #

[JavaDoc](http://www.johandegraeve.net/easyxmldata/doc/)

# Summary #

The methods in the interface [XMLElement](http://www.johandegraeve.net/easyxmldata/doc/net/johandegraeve/easyxmldata/XMLElement.html)  allow the custom class to define what needs to happen with

  * the attributes that are found. This method will be called even if the list of attributes is empty. It allows the custom class to populate internal fields with the values of the attributes in the XML document, or to generate a SAXException in case attributes are missing.
  * child elements that are found : the custom class decides which children are allowed. Some may not be allowed in which case the custom class can throw a SAXException.
  * text that is found. Two methods are called that contain the text, a trimmed version and an untrimmed version.

The methods also allow to

  * check if an element is complete : this method is called when the end-tag of the element in the XML document is encountered. At that moment the custom class may check whether all mandatory fields are received and if not throw a SAXException.


The package defines a EasyXMLDataParser class. This is where [DefaultHandler](http://java.sun.com/j2se/1.4.2/docs/api/org/xml/sax/helpers/DefaultHandler.html) is extended. The class overrides DefaultHandler's methods  characters, endDocument , endElement, setDocumentLocator , startDocument, startElement  and from there the methods defined in [XMLElement](http://www.johandegraeve.net/easyxmldata/doc/net/johandegraeve/easyxmldata/XMLElement.html) are called for an instance of a class with the same name as the tag found in the XML Document.

A trick is implemented to allow to define the classes corresponding to the XML elements, with a bit more clear names than just the name of the tag.
The trick is that a prefix can be added before the name of the Element in the class name. When creating a EasyXMLDataParser, then a list of package names and prefix names needs to be supplied. Whenever a tag is encountered in a parsed XML document, the parser will search for a class in each package supplied, but the class name will be prefixed with the corresponding prefix in the supplied array.
When validating a received XML Element in [addChild](http://www.johandegraeve.net/easyxmldata/doc/net/johandegraeve/easyxmldata/XMLElement.html#addChild%28net.johandegraeve.easyxmldata.XMLElement%29addChild), and if you want to validate the type of XML element received, then it is important to know that the class of the XMLElement will be prefix+XML element name.

The [Utilities](http://www.johandegraeve.net/easyxmldata/doc/net/johandegraeve/easyxmldata/Utilities.html) class defines some useful static methods.