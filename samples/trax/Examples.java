import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

// Needed java classes
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

import java.util.Properties;

// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;

// Needed DOM classes
import org.w3c.dom.Node;

// Needed JAXP classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.BufferedInputStream;    // dml

/**
 * Some examples to show how the Simple API for Transformations
 * could be used.
 *
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class Examples
{

  /**
   * Method main
   */
  public static void main(String argv[])
          throws TransformerException, TransformerConfigurationException, IOException, SAXException,
                 ParserConfigurationException
  {

    System.out.println("==== exampleSimple ====");
    exampleSimple1("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleSAX2SAX ====");
    exampleSAX2SAX("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleXMLFilter ====");
    exampleXMLFilter("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleXMLFilterChain ====");
    exampleXMLFilterChain("foo.xml", "baz.xsl", "foo2.xsl", "baz.xsl");
    System.out.println("\n==== exampleDOM2DOM ====");
    exampleDOM2DOM("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleParam ====");
    exampleParam("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleOutputProperties ====");
    exampleOutputProperties("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleUseAssociated ====");
    exampleUseAssociated("foo.xml");
    System.out.println("\n==== exampleSAX2DOM ====");
    exampleSAX2DOM("foo.xml", "foo.xsl");
    System.out.println("\n==== exampleAsSerializer ====");
    exampleAsSerializer("foo.xml", "foo.xsl");
    System.out.println("\n==== done! ====");
  }
  
  /**
   * Show the simplest possible transformation from system id 
   * to output stream.
   */
  public static void exampleSimple1(String sourceID, String xslID)
    throws TransformerException, TransformerConfigurationException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance();
    Transformer transformer 
      = tfactory.newTransformer(new StreamSource(xslID));
    transformer.transform( new StreamSource(sourceID),
                           new StreamResult(System.out));
  }
 
  /**
   * Show the simplest possible transformation from system id to output stream.
   */
  public static void exampleSimple2(String sourceID1, 
                                    String sourceID2, 
                                    String xslID)
          throws TransformerException, TransformerConfigurationException
  {

    TransformerFactory tfactory = TransformerFactory.newInstance();
    Templates templates = tfactory.newTemplates(new StreamSource(xslID));

    Transformer transformer1 = templates.newTransformer();
    transformer1.transform(new StreamSource(sourceID1),
                          new StreamResult(System.out));
    
    Transformer transformer2 = templates.newTransformer();
    transformer2.transform(new StreamSource(sourceID2),
                          new StreamResult(System.out));
  }
  


  /**
   * Show the Transformer using SAX events in and SAX events out.
   */
  public static void exampleSAX2SAX(String sourceID, String xslID)
          throws TransformerException, TransformerConfigurationException, SAXException, IOException    // , ParserConfigurationException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance();
    XMLReader reader = XMLReaderFactory.createXMLReader();

    // Have a Templates builder handle the parse events from the SAXParser's 
    // parse of an xslt file.
    if (tfactory instanceof SAXTransformerFactory)
    {
      TransformerHandler handler 
        = ((SAXTransformerFactory) tfactory).newTransformerHandler(new StreamSource(xslID));

      // Set the result handling to be a serialization to System.out.
      Result result = new SAXResult(new ExampleContentHandler());

      handler.setResult(result);
      reader.setContentHandler(handler);
      reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                         handler);
      reader.parse(sourceID);
    }
    else
    {
      System.out.println(
        "Can't do exampleSAX2SAX because tfactory is not a SAXTransformerFactory");
    }
  }

  /**
   * Show the Transformer as a SAX2 XMLFilter/XMLReader.  In this case
   * the Transformer acts like a parser, and can in fact be polymorphicaly
   * used in places where a SAX parser would be used.
   */
  public static void exampleXMLFilter(String sourceID, String xslID)
          throws TransformerException, TransformerConfigurationException, SAXException, IOException    // , ParserConfigurationException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance();

    // The transformer will use a SAX parser as it's reader.    
    XMLReader reader = XMLReaderFactory.createXMLReader();
    reader.setContentHandler(new ExampleContentHandler());
    try
    {
      reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                        true);
      reader.setFeature("http://apache.org/xml/features/validation/dynamic",
                        true);
    }
    catch (SAXException se)
    {

      // What can we do?
      // TODO: User diagnostics.
    }

    XMLFilter filter 
      = ((SAXTransformerFactory) tfactory).newXMLFilter(new StreamSource(xslID));

    filter.setParent(reader);

    // Now, when you call transformer.parse, it will set itself as 
    // the content handler for the parser object (it's "parent"), and 
    // will then call the parse method on the parser.
    filter.parse(new InputSource(sourceID));
  }

  /**
   * This example shows how to chain events from one Transformer
   * to another transformer, using the Transformer as a
   * SAX2 XMLFilter/XMLReader.
   */
  public static void exampleXMLFilterChain(
                                           String sourceID, String xslID_1, 
                                           String xslID_2, String xslID_3)
    throws TransformerException, TransformerConfigurationException, SAXException, IOException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance();
    
    Templates stylesheet1 = tfactory.newTemplates(new StreamSource(xslID_1));
    Transformer transformer1 = stylesheet1.newTransformer();
    
     // If one success, assume all will succeed.
    if (tfactory.getFeature(Features.SAX))
    {
      SAXTransformerFactory stf = (SAXTransformerFactory)tfactory;
      XMLReader reader = XMLReaderFactory.createXMLReader();

      XMLFilter filter1 = stf.newXMLFilter(new StreamSource(xslID_1));
      XMLFilter filter2 = stf.newXMLFilter(new StreamSource(xslID_2));
      XMLFilter filter3 = stf.newXMLFilter(new StreamSource(xslID_3));

      if (null != filter1) // If one success, assume all were success.
      {
        // transformer1 will use a SAX parser as it's reader.    
        filter1.setParent(reader);

        // transformer2 will use transformer1 as it's reader.
        filter2.setParent(filter1);

        // transform3 will use transform2 as it's reader.
        filter3.setParent(filter2);

        filter3.setContentHandler(new ExampleContentHandler());

        // Now, when you call transformer3 to parse, it will set  
        // itself as the ContentHandler for transform2, and 
        // call transform2.parse, which will set itself as the 
        // content handler for transform1, and call transform1.parse, 
        // which will set itself as the content listener for the 
        // SAX parser, and call parser.parse(new InputSource("foo.xml")).
        filter3.parse(new InputSource(sourceID));
      }
      else
      {
        System.out.println(
                           "Can't do exampleXMLFilter because "+
                           "tfactory doesn't support asXMLFilter()");
      }
    }
    else
    {
      System.out.println(
                         "Can't do exampleXMLFilter because "+
                         "tfactory is not a SAXTransformerFactory");
    }
  }

  /**
   * Show how to transform a DOM tree into another DOM tree.
   * This uses the javax.xml.parsers to parse an XML file into a
   * DOM, and create an output DOM.
   */
  public static Node exampleDOM2DOM(String sourceID, String xslID)
    throws TransformerException, TransformerConfigurationException, SAXException, IOException,
    ParserConfigurationException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance();

    if (tfactory.getFeature(Features.DOM))
    {
      Templates templates;

      {
        DocumentBuilderFactory dfactory =
          DocumentBuilderFactory.newInstance();
        dfactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
        org.w3c.dom.Document outNode = docBuilder.newDocument();
        Node doc = docBuilder.parse(new InputSource(xslID));
 
        templates = tfactory.newTemplates(new DOMSource(doc));
      }

      Transformer transformer = templates.newTransformer();
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      org.w3c.dom.Document outNode = docBuilder.newDocument();
      Node doc = docBuilder.parse(new InputSource(sourceID));

      transformer.transform(new DOMSource(doc), new DOMResult(outNode));
      
      Transformer serializer = tfactory.newTransformer();
      serializer.transform(new DOMSource(outNode), new StreamResult(System.out));

      return outNode;
    }
    else
    {
      throw new org.xml.sax
        .SAXNotSupportedException("DOM node processing not supported!");
    }
  } 

  /**
   * This shows how to set a parameter for use by the templates.
   */
  public static void exampleParam(String sourceID, 
                                  String xslID)
    throws TransformerException, TransformerConfigurationException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance();
    Templates templates = tfactory.newTemplates(new StreamSource(xslID));
    Transformer transformer1 = templates.newTransformer();
    Transformer transformer2 = templates.newTransformer();

    transformer1.setParameter("a-param",
                              "hello to you!");
    transformer1.transform(new StreamSource(sourceID),
                           new StreamResult(System.out));
    
    System.out.println("\n=========");
    
    transformer2.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer2.transform(new StreamSource(sourceID),
                           new StreamResult(System.out));
  }

  /**
   * Show how to override output properties.
   */
  public static void exampleOutputProperties(String sourceID, String xslID)
    throws TransformerException, TransformerConfigurationException
  {

    TransformerFactory tfactory = TransformerFactory.newInstance();
    Templates templates = tfactory.newTemplates(new StreamSource(xslID));
    Properties oprops = templates.getOutputProperties();

    oprops.put(OutputKeys.INDENT, "yes");

    Transformer transformer = templates.newTransformer();

    transformer.setOutputProperties(oprops);
    transformer.transform(new StreamSource(sourceID),
                          new StreamResult(System.out));
  }

  /**
   * Show how to get stylesheets that are associated with a given
   * xml document via the xml-stylesheet PI (see http://www.w3.org/TR/xml-stylesheet/).
   */
  public static void exampleUseAssociated(String sourceID)
    throws TransformerException, TransformerConfigurationException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance();

    // The DOM tfactory will have it's own way, based on DOM2, 
    // of getting associated stylesheets.
    if (tfactory instanceof SAXTransformerFactory)
    {
      SAXTransformerFactory stf = ((SAXTransformerFactory) tfactory);
      Source sources =
        stf.getAssociatedStylesheet(new StreamSource(sourceID),
          null, null, null);

      if(null != sources)
      {
        Transformer transformer = tfactory.newTransformer(sources);

        transformer.transform(new StreamSource("foo.xml"),
                              new StreamResult(System.out));
      }
      else
      {
        System.out.println("Can't find the associated stylesheet!");
      }
    }
  }
  
  /**
   * Show the Transformer using SAX events in and SAX events out.
   */
  public static Node exampleSAX2DOM(String sourceID, String xslID)
          throws TransformerException, TransformerConfigurationException, SAXException, IOException, ParserConfigurationException
  {
    TransformerFactory tfactory = TransformerFactory.newInstance();
    Templates templates = tfactory.newTemplates(new StreamSource(xslID));

    if (tfactory.getFeature(Features.SAX)
        && tfactory.getFeature(Features.DOM))
    {
      // Create an Document node as the root for the output.
      DocumentBuilderFactory dfactory 
        = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      org.w3c.dom.Document outNode = docBuilder.newDocument();
      
      TransformerHandler handler 
        = ((SAXTransformerFactory) tfactory).newTransformerHandler(new StreamSource(xslID));
      handler.setResult(new DOMResult(outNode));
      
      // Create a reader and set it's content handler to be the 
      // transformer.
      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(handler);
      reader.setProperty("http://xml.org/sax/properties/lexical-handler",
                         handler);
      
      // Send the SAX events from the parser to the transformer.
      reader.parse(sourceID);
      
      // The tree should now be filled in, so return it.
      return outNode;
    }
    else
    {
      System.out.println(
        "Can't do exampleSAX2SAX because tfactory is not a SAXTransformerFactory");
    }
    return null;
  }
  
  public static void exampleAsSerializer(String sourceID, String xslID)
    throws TransformerException, TransformerConfigurationException, SAXException, IOException,
    ParserConfigurationException
  {
    DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
    org.w3c.dom.Document outNode = docBuilder.newDocument();
    Node doc = docBuilder.parse(new InputSource(sourceID));

    TransformerFactory tfactory = TransformerFactory.newInstance();    
    Transformer serializer = tfactory.newTransformer();
    Properties oprops = new Properties();
    oprops.put("method", "html");
    oprops.put("indent-amount", "2");
    serializer.setOutputProperties(oprops);
    serializer.transform(new DOMSource(doc), 
                         new StreamResult(System.out));
  } 

}
