package javax.xml.trax.stream;

import javax.xml.trax.*;

import java.lang.String;

import java.io.OutputStream;
import java.io.Writer;

import org.xml.sax.ContentHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 * Acts as an holder for a transformation result tree.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class StreamResult implements Result
{
  /**
   * Zero-argument default constructor.
   */
  public StreamResult() {}

  /**
   * Constructor StreamResult
   *
   * @param byteStream
   */
  public StreamResult(OutputStream byteStream)
  {
    setByteStream(byteStream);
  }

  /**
   * Constructor StreamResult
   *
   * @param characterStream
   */
  public StreamResult(Writer characterStream)
  {
    setCharacterStream(characterStream);
  }

  /**
   * Method setByteStream
   *
   * @param byteStream
   */
  public void setByteStream(OutputStream byteStream)
  {
    this.byteStream = byteStream;
  }

  /**
   * Method getByteStream
   *
   * @return
   */
  public OutputStream getByteStream()
  {
    return byteStream;
  }

  /**
   * Method setCharacterStream
   *
   * @param characterStream
   */
  public void setCharacterStream(Writer characterStream)
  {
    this.characterStream = characterStream;
  }

  /**
   * Method getCharacterStream
   *
   * @return
   */
  public Writer getCharacterStream()
  {
    return characterStream;
  }

  /**
   * Method setSystemId
   *
   * @param systemId
   */
  public void setSystemId(String systemId)
  {
    this.systemId = systemId;
  }

  /**
   * Method getSystemId
   *
   * @return
   */
  public String getSystemId()
  {
    return systemId;
  }
  
  /**
   * Field systemId
   */
  private String systemId;

  //////////////////////////////////////////////////////////////////////
  // Internal state.
  //////////////////////////////////////////////////////////////////////

  /**
   * Field byteStream
   */
  private OutputStream byteStream;

  /**
   * Field characterStream
   */
  private Writer characterStream;
}
