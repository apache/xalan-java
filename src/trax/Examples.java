// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

// Needed java classes
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

// Needed SAX classes
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.Parser;
import org.xml.sax.helpers.ParserAdapter;

// Needed DOM classes
import org.w3c.dom.Node;

// Needed Serializer classes
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.SerializerFactory;

/**
 * Some examples to show how the Simple API for Transformations 
 * could be used.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class Examples
{

  public static void main( String argv[] )
    throws ProcessorException, ProcessorFactoryException, 
           TransformException, SAXException, IOException
  {
    exampleSimple("foo.xml", "foo.xsl");
  }
  
  /**
   * Show the simplest possible transformation from system id to output stream.
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    Templates templates = processor.process(new InputSource("t1.xsl"));
    Transformer transformer = templates.newTransformer();

    transformer.transform(new InputSource("foo.xml"), new Result(System.out));
   * </pre>
   */
  public static void exampleSimple(String sourceID, String xslID)
    throws ProcessorException, ProcessorFactoryException, 
           TransformException, SAXException, IOException
  {  
    Processor processor = Processor.newInstance("xslt");

    Templates templates = processor.process(new InputSource(xslID));
    Transformer transformer = templates.newTransformer();

    transformer.transform(new InputSource(sourceID), new Result(System.out));
  }
  
  /**
   * Show the Transformer using SAX events in and SAX events out.
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    // Use the JAXP interface to get a SAX1 parser interface.    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxparser = factory.newSAXParser();
    Parser parser = saxparser.getParser();

    // Have a Templates builder handle the parse events from the SAXParser's 
    // parse of an xslt file.
    TemplatesBuilder templatesBuilder = processor.getTemplatesBuilder();
    parser.setDocumentHandler(templatesBuilder);
    parser.parse("t1.xsl");
    Templates templates = templatesBuilder.getTemplates();
    
    // Get a transformer instance for the templates.
    Transformer transformer = templates.newTransformer();
    
    // Set the result handling to be a serialization to System.out.
    Serializer serializer = SerializerFactory.getSerializer( "xml" );
    serializer.setOutputStream(System.out);
    transformer.setContentHandler(serializer.asContentHandler());

    
    // Is it my imagination, or is there no way to set the DeclHandler and 
    // the LexicalHandler, since there seems to be no way to get an 
    // XMLReader.  This is a rather major problem.  Hopefully, that will 
    // be fixed in the next round.
    
    // Cause the transformation to occur by asking the parser to send 
    // the parse events from "foo.xsl" to the transformer.
    parser.setDocumentHandler(transformer.getInputContentHandler());
    parser.parse("foo.xml");
   * </pre>
   */
  /*
  public static void exampleSAX2SAX()
    throws SAXException, IOException // , ParserConfigurationException
  {  
    Processor processor = Processor.newInstance("xslt");

    // Use the JAXP interface to get a SAX1 parser interface.    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxparser = factory.newSAXParser();
    Parser parser = saxparser.getParser();

    // Have a Templates builder handle the parse events from the SAXParser's 
    // parse of an xslt file.
    TemplatesBuilder templatesBuilder = processor.getTemplatesBuilder();
    parser.setDocumentHandler(templatesBuilder);
    parser.parse("t1.xsl");
    Templates templates = templatesBuilder.getTemplates();
    
    // Get a transformer instance for the templates.
    Transformer transformer = templates.newTransformer();
    
    // Set the result handling to be a serialization to System.out.
    Serializer serializer = SerializerFactory.getSerializer( "xml" );
    serializer.setOutputStream(System.out);
    transformer.setContentHandler(serializer.asContentHandler());

    
    // Is it my imagination, or is there no way to set the DeclHandler and 
    // the LexicalHandler, since there seems to be no way to get an 
    // XMLReader.  This is a rather major problem.  Hopefully, that will 
    // be fixed in the next round.
    
    // Cause the transformation to occur by asking the parser to send 
    // the parse events from "foo.xsl" to the transformer.
    parser.setDocumentHandler(transformer.getInputContentHandler());
    parser.parse("foo.xml");
  }
  */
  
  /**
   * Show the Transformer using the JAXP interface for input.
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    // The transformer will use a SAX parser as it's reader.    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxparser = factory.newSAXParser();

    // Have a Templates builder handle the parse events from the SAXParser's 
    // parse of an xslt file.
    TemplatesBuilder templatesBuilder = processor.getTemplatesBuilder();
    saxparser.parse("t1.xsl", templatesBuilder);
    Templates templates = templatesBuilder.getTemplates();
    
    // Get a transformer instance for the templates.
    Transformer transformer = templates.newTransformer();
    
    // Set the result handling to be a serialization to System.out.
    Serializer serializer = SerializerFactory.getSerializer( "xml" );
    serializer.setOutputStream(System.out);
    transformer.setContentHandler(serializer.asContentHandler());

    
    // Is it my imagination, or is there no way to set the DeclHandler and 
    // the LexicalHandler, since there seems to be no way to get an 
    // XMLReader.  This is a rather major problem.  Hopefully, that will 
    // be fixed in the next round.
    
    // Cause the transformation to occur by asking the parser to send 
    // the parse events from "foo.xsl" to the transformer.
    saxparser.parse("foo.xml", transformer.getInputContentHandler());
   * </pre>
   */
  /*
  public static void exampleJAXP2SAX2()
    throws SAXException, IOException // , ParserConfigurationException
  {  
    Processor processor = Processor.newInstance("xslt");

    // The transformer will use a SAX parser as it's reader.    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser saxparser = factory.newSAXParser();

    // Have a Templates builder handle the parse events from the SAXParser's 
    // parse of an xslt file.
    TemplatesBuilder templatesBuilder = processor.getTemplatesBuilder();
    saxparser.parse("t1.xsl", templatesBuilder);
    Templates templates = templatesBuilder.getTemplates();
    
    // Get a transformer instance for the templates.
    Transformer transformer = templates.newTransformer();
    
    // Set the result handling to be a serialization to System.out.
    Serializer serializer = SerializerFactory.getSerializer( "xml" );
    serializer.setOutputStream(System.out);
    transformer.setContentHandler(serializer.asContentHandler());

    
    // Is it my imagination, or is there no way to set the DeclHandler and 
    // the LexicalHandler, since there seems to be no way to get an 
    // XMLReader.  This is a rather major problem.  Hopefully, that will 
    // be fixed in the next round.
    
    // Cause the transformation to occur by asking the parser to send 
    // the parse events from "foo.xsl" to the transformer.
    saxparser.parse("foo.xml", transformer.getInputContentHandler());
  }
  */

  
  /**
   * Show the Transformer as a SAX2 XMLFilter/XMLReader.  In this case 
   * the Transformer acts like a parser, and can in fact be polymorphicaly 
   * used in places where a SAX parser would be used.
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    Templates templates = processor.process(new InputSource("t1.xsl"));
    Transformer transformer = templates.newTransformer();

    // Set the result handling to be a serialization to System.out.
    Serializer serializer = SerializerFactory.getSerializer( "xml" );
    serializer.setOutputStream(System.out);
    transformer.setContentHandler(serializer.asContentHandler());

    // The transformer will use a SAX parser as it's reader.    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    transformer.setParent(new ParserAdapter( parser.getParser() ));
    
    // Now, when you call transformer.parse, it will set itself as 
    // the content handler for the parser object (it's "parent"), and 
    // will then call the parse method on the parser.
    transformer.parse(new InputSource("foo.xml"));
   * </pre>
   */
  /*
  public static void exampleXMLFilter()
    throws SAXException, IOException// , ParserConfigurationException
  {  
    Processor processor = Processor.newInstance("xslt");

    Templates templates = processor.process(new InputSource("t1.xsl"));
    Transformer transformer = templates.newTransformer();

    // Set the result handling to be a serialization to System.out.
    Serializer serializer = SerializerFactory.getSerializer( "xml" );
    serializer.setOutputStream(System.out);
    transformer.setContentHandler(serializer.asContentHandler());

    // The transformer will use a SAX parser as it's reader.    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    transformer.setParent(new ParserAdapter( parser.getParser() ));
    
    // Now, when you call transformer.parse, it will set itself as 
    // the content handler for the parser object (it's "parent"), and 
    // will then call the parse method on the parser.
    transformer.parse(new InputSource("foo.xml"));
  }
  */
  
  /**
   * This example shows how to chain events from one Transformer 
   * to another transformer, using the Transformer as a 
   * SAX2 XMLFilter/XMLReader.
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    Templates stylesheet1 = processor.process(new InputSource("t1.xsl"));
    Transformer transformer1 = stylesheet1.newTransformer();

    Templates stylesheet2= processor.process(new InputSource("t2.xsl"));
    Transformer transformer2 = stylesheet2.newTransformer();

    Templates  stylesheet3 = processor.process(new InputSource("t3.xsl"));
    Transformer transformer3= stylesheet3.newTransformer();
    
    // transformer1 will use a SAX parser as it's reader.    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    transformer1.setParent(new ParserAdapter( parser.getParser() ));
    
    // transformer2 will use transformer1 as it's reader.
    transformer2.setParent(transformer1);
    
    // transform3 will use transform2 as it's reader.
    transformer3.setParent(transformer2);
    
    // transform3 will output the events to the serializer.
    Serializer serializer = SerializerFactory.getSerializer( "xml" );
    serializer.setOutputStream(System.out);
    transformer3.setContentHandler(serializer.asContentHandler());

    // Now, when you call transformer3 to parse, it will set  
    // itself as the ContentHandler for transform2, and 
    // call transform2.parse, which will set itself as the 
    // content handler for transform1, and call transform1.parse, 
    // which will set itself as the content listener for the 
    // SAX parser, and call parser.parse(new InputSource("foo.xml")).
    transformer3.parse(new InputSource("foo.xml"));
   * </pre>
   */
  /*
  public static void exampleXMLFilterChain()
    throws SAXException, IOException// , ParserConfigurationException
  {  
    Processor processor = Processor.newInstance("xslt");

    Templates stylesheet1 = processor.process(new InputSource("t1.xsl"));
    Transformer transformer1 = stylesheet1.newTransformer();

    Templates stylesheet2= processor.process(new InputSource("t2.xsl"));
    Transformer transformer2 = stylesheet2.newTransformer();

    Templates  stylesheet3 = processor.process(new InputSource("t3.xsl"));
    Transformer transformer3= stylesheet3.newTransformer();
    
    // transformer1 will use a SAX parser as it's reader.    
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    transformer1.setParent(new ParserAdapter( parser.getParser() ));
    
    // transformer2 will use transformer1 as it's reader.
    transformer2.setParent(transformer1);
    
    // transform3 will use transform2 as it's reader.
    transformer3.setParent(transformer2);
    
    // transform3 will output the events to the serializer.
    Serializer serializer = SerializerFactory.getSerializer( "xml" );
    serializer.setOutputStream(System.out);
    transformer3.setContentHandler(serializer.asContentHandler());

    // Now, when you call transformer3 to parse, it will set  
    // itself as the ContentHandler for transform2, and 
    // call transform2.parse, which will set itself as the 
    // content handler for transform1, and call transform1.parse, 
    // which will set itself as the content listener for the 
    // SAX parser, and call parser.parse(new InputSource("foo.xml")).
    transformer3.parse(new InputSource("foo.xml"));
  }
  */
  
  /**
   * Show how to transform a DOM tree into another DOM tree.  
   * This uses the javax.xml.parsers to parse an XML file into a 
   * DOM, and create an output DOM.
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    if(processor.getFeature("http://xml.org/trax/features/dom/input"))
    {
      Templates templates = processor.process(new InputSource("t1.xsl"));
      Transformer transformer = templates.newTransformer();

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      org.w3c.dom.Node outNode = docBuilder.newDocument();
      Node doc = docBuilder.parse(new InputSource("foo.xml"));
      
      transformer.transformNode(doc, new Result(outNode));
    }
    else
    {
      throw new org.xml.sax.SAXNotSupportedException("DOM node processing not supported!");
    }
   * </pre>
   */
  /*
  public static void exampleDOM2DOM()
    throws SAXException, IOException// , ParserConfigurationException
  {  
    Processor processor = Processor.newInstance("xslt");

    if(processor.getFeature("http://xml.org/trax/features/dom/input"))
    {
      Templates templates = processor.process(new InputSource("t1.xsl"));
      Transformer transformer = templates.newTransformer();

      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      org.w3c.dom.Node outNode = docBuilder.newDocument();
      Node doc = docBuilder.parse(new InputSource("foo.xml"));
      
      transformer.transformNode(doc, new Result(outNode));
    }
    else
    {
      throw new org.xml.sax.SAXNotSupportedException("DOM node processing not supported!");
    }
  }
  */

  /**
   * This shows how to set a parameter for use by the templates.
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    Templates templates = processor.process(new InputSource("t1.xsl"));
    Transformer transformer = templates.newTransformer();
    transformer.setParameter("my-param", "http://foo.com", "hello");
    
    transformer.transform(new InputSource("foo.xml"), new Result(System.out));
   * </pre>   
   */
  public static void exampleParam()
    throws ProcessorException, ProcessorFactoryException, 
           TransformException, SAXException, IOException
  {  
    Processor processor = Processor.newInstance("xslt");

    Templates templates = processor.process(new InputSource("t1.xsl"));
    Transformer transformer = templates.newTransformer();
    transformer.setParameter("my-param", "http://foo.com" /* namespace */, "hello");
    
    transformer.transform(new InputSource("foo.xml"), new Result(System.out));
  }
    
  /**
   * Show how to override output properties.
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    Templates templates = processor.process(new InputSource("t1.xsl"));
    OutputFormat oprops = templates.getOutputFormat();
    oprops.setIndenting( true );
    Transformer transformer = templates.newTransformer();
    transformer.setOutputFormat(oprops);
    
    transformer.transform(new InputSource("foo.xml"), new Result(System.out));
   * </pre>   
   */
  public static void exampleOutputFormat()
    throws ProcessorException, ProcessorFactoryException, 
           TransformException, SAXException, IOException
  {  
    Processor processor = Processor.newInstance("xslt");

    Templates templates = processor.process(new InputSource("t1.xsl"));
    OutputFormat oprops = templates.getOutputFormat();
    oprops.setIndenting( true );
    Transformer transformer = templates.newTransformer();
    transformer.setOutputFormat(oprops);
    
    transformer.transform(new InputSource("foo.xml"), new Result(System.out));
  }
    
  /**
   * Show how to get stylesheets that are associated with a given 
   * xml document via the xml-stylesheet PI (see http://www.w3.org/TR/xml-stylesheet/).
   * <pre>
    Processor processor = Processor.newInstance("xslt");

    InputSource docSouce = new InputSource("foo.xml");
    InputSource[] sources 
      = processor.getAssociatedStylesheets(docSouce, "text/xslt", null, null);
    // Processor may remember it read the given document...
    
    Templates templates = (null != sources) ?
                            processor.processMultiple(sources) :
                            processor.process(new InputSource("default.xsl"));

    Transformer transformer = templates.newTransformer();
    
    transformer.transform(docSouce, new Result(System.out));
   * </pre>   
   */
  public static void exampleUseAssociated()
    throws ProcessorException, ProcessorFactoryException, 
           TransformException, SAXException, IOException
  {  
    Processor processor = Processor.newInstance("xslt");

    InputSource docSouce = new InputSource("foo.xml");
    InputSource[] sources 
      = processor.getAssociatedStylesheets(docSouce, "text/xslt", null, null);
    // Processor may remember it read the given document...
    
    Templates templates = (null != sources) ?
                            processor.processMultiple(sources) :
                            processor.process(new InputSource("default.xsl"));

    Transformer transformer = templates.newTransformer();
    
    transformer.transform(docSouce, new Result(System.out));
  }
    
}
