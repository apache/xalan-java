package javax.xml.trax.stream;

import javax.xml.trax.Source;

import java.io.InputStream;
import java.io.Reader;

/**
 * Acts as an holder for a transformation Source tree.
 *
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class StreamSource implements Source
{

  /**
   * Zero-argument default constructor.
   */
  public StreamSource() {}

  /**
   * Constructor StreamSource
   *
   * @param byteStream
   */
  public StreamSource(InputStream byteStream)
  {
    setByteStream(byteStream);
  }

  /**
   * Constructor StreamSource
   *
   * @param characterStream
   */
  public StreamSource(Reader characterStream)
  {
    setCharacterStream(characterStream);
  }
  
  /**
   * Method setCharacterStream
   *
   * @param characterStream
   */
  public StreamSource(String systemId)
  {
    this.systemId = systemId;
  }


  /**
   * Method setByteStream
   *
   * @param byteStream
   */
  public void setByteStream(InputStream byteStream)
  {
    this.byteStream = byteStream;
  }

  /**
   * Method getByteStream
   *
   * @return
   */
  public InputStream getByteStream()
  {
    return byteStream;
  }

  /**
   * Method setCharacterStream
   *
   * @param characterStream
   */
  public void setCharacterStream(Reader characterStream)
  {
    this.characterStream = characterStream;
  }

  /**
   * Method getCharacterStream
   *
   * @return
   */
  public Reader getCharacterStream()
  {
    return characterStream;
  }

  /**
   * Method setPublicId
   *
   * @param publicId
   */
  public void setPublicId(String publicId)
  {
    this.publicId = publicId;
  }

  /**
   * Method getPublicId
   *
   * @return
   */
  public String getPublicId()
  {
    return publicId;
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
   * Field publicId
   */
  private String publicId;

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
  private InputStream byteStream;

  /**
   * Field characterStream
   */
  private Reader characterStream;
}
