package javax.xml.trax.sax;

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
public class SAXResult implements Result
{

  /**
   * Zero-argument default constructor.
   */
  public SAXResult() {}

  /**
   * Create a new output target with a DOM node.
   *
   *
   * @param handler
   */
  public SAXResult(ContentHandler handler)
  {
    setHandler(handler);
  }

  /**
   * Set the node that will contain the result DOM tree.
   *
   * @param handler
   */
  public void setHandler(ContentHandler handler)
  {
    this.handler = handler;
  }

  /**
   * Get the node that will contain the result tree.
   *
   * @return
   */
  public ContentHandler getHandler()
  {
    return handler;
  }

  /**
   * Set the SAX2 DeclHandler for the output.
   *
   * @param handler
   */
  void setDeclHandler(DeclHandler handler)
  {
    this.declhandler = declhandler;
  }

  /**
   * Get the SAX2 DeclHandler for the output.
   * @return A DeclHandler, or null.
   */
  DeclHandler getDeclHandler()
  {
    return declhandler;
  }

  /**
   * Set the SAX2 LexicalHandler for the output.
   *
   * @param handler
   */
  void setLexicalHandler(LexicalHandler handler)
  {
    this.lexhandler = lexhandler;
  }

  /**
   * Get a SAX2 LexicalHandler for the output.
   * @return A LexicalHandler, or null.
   */
  LexicalHandler getLexicalHandler()
  {
    return lexhandler;
  }

  //////////////////////////////////////////////////////////////////////
  // Internal state.
  //////////////////////////////////////////////////////////////////////

  /**
   * Field handler
   */
  private ContentHandler handler;

  /**
   * Field declhandler
   */
  private DeclHandler declhandler;

  /**
   * Field lexhandler
   */
  private LexicalHandler lexhandler;
}
