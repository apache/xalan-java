// Transformations for XML (TRaX)
// Copyright ©2000 Lotus Development Corporation, Exoffice Technologies,
// Oracle Corporation, Michael Kay of International Computers Limited, Apache
// Software Foundation.  All rights reserved.
package trax;

import java.lang.String;
import java.io.OutputStream;
import java.io.Writer;
import org.w3c.dom.Node;

/**
 * Acts as an holder for result tree specifications.
 * <p>This class is modeled after the SAX InputSource class, except that it
 * is for the Result target, and in addition to streams, and writers,
 * it also can specify a DOM node to which nodes will be appended.</p>
 *
 * <h3>Open issues:</h3>
 * <dl>
 *    <dt><h4>Should this be an interface?</h4></dt>
 *    <dd>Should this be an interface instead of a concrete class?  The justification 
 *        for it being a class is that it is just a bag of data, and contains no 
 *        behavior of its own.</dd>
 * </dl>
 * 
 * @version Alpha
 * @author <a href="mailto:scott_boag@lotus.com">Scott Boag</a>
 */
public class Result
{
  /**
   * Zero-argument default constructor.
   */
  public Result ()
  {
  }

  /**
   * Create a new output target with a byte stream.
   *
   * @param byteStream The raw byte stream that will contain the document.
   */
  public Result (OutputStream byteStream)
  {
    setByteStream(byteStream);
  }


  /**
   * Create a new output target with a character stream.
   *
   * @param characterStream The character stream where the results will be written.
   */ 
  public Result (Writer characterStream)
  {
    setCharacterStream(characterStream);
  }

  /**
   * Create a new output target with a character stream.
   *
   * @param characterStream The character stream where the results will be written.
   */
  public Result (Node n)
  {
    setNode(n);
  }
  
  /**
   * Set the byte stream for this output target.
   *
   * @param byteStream A byte stream that will contain the result document.
   */
  public void setByteStream (OutputStream byteStream)
  {
    this.byteStream = byteStream;
  }

  /**
   * Get the byte stream for this output target.
   *
   * @return The byte stream, or null if none was supplied.
   */
  public OutputStream getByteStream ()
  {
    return byteStream;
  }

  /**
   * Set the character stream for this output target.
   *
   * @param characterStream The character stream that will contain 
   *                     the result document.
   */
  public void setCharacterStream (Writer characterStream)
  {
    this.characterStream = characterStream;
  }

  /**
   * Get the character stream for this output target.
   *
   * @return The character stream, or null if none was supplied.
   */
  public Writer getCharacterStream ()
  {
    return characterStream;
  }

  /**
   * Set the node that will contain the result nodes.
   */
  public void setNode (Node node)
  {
    this.node = node;
  }

  /**
   * Get the node that will contain the result nodes.
   */
  public Node getNode ()
  {
    return node;
  }
  
  //////////////////////////////////////////////////////////////////////
  // Internal state.
  //////////////////////////////////////////////////////////////////////

  private String fileName;
  private OutputStream byteStream;
  private String encoding;
  private Writer characterStream;
  private Node node;
}
