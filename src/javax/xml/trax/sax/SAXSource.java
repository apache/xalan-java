package javax.xml.trax.sax;

import javax.xml.trax.*;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.InputSource;

/**
 * Acts as an holder for SAX-style Source tree input.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class SAXSource implements Source
{
  /**
   * Zero-argument default constructor.
   */
  public SAXSource() 
  {
  }

  /**
   * Create a SAXSource, using an XMLReader and an InputSource.
   * The Transformer or SAXTransformerFactory will set itself 
   * to be the reader's content handler, and then will call
   * reader.parse(inputSource).
   */
  public SAXSource(XMLReader reader, InputSource inputSource) 
  {
    this.reader = reader;
    this.inputSource = inputSource;
  }
  
  /**
   * Create a SAXSource, using an InputSource.
   * The Transformer or SAXTransformerFactory will create a  
   * reader via org.xml.sax.helpers.ParserFactory
   * (if setXMLReader is not used), and will set itself
   * to be the content handler of that reader, and then will call
   * reader.parse(inputSource).
   */
  public SAXSource(InputSource inputSource) 
  {
    this.inputSource = inputSource;
  }

  /**
   * Set the InputSource to be used for the source tree input.
   *
   * @param reader A valid XMLReader or XMLFilter reference.
   */
  public void setXMLReader(XMLReader reader)
  {
    this.reader = reader;
  }
  
  /**
   * Set the XMLReader to be used for the source tree input.
   *
   * @param inputSource A valid InputSource reference.
   */
  public void setInputSource(InputSource inputSource)
  {
    this.inputSource = inputSource;
  }

  private XMLReader reader;
  private InputSource inputSource;
}
