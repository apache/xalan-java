package org.apache.xalan.transformer;

/**
 * A content handler can get a reference 
 * to a TransformState by implementing 
 * the TransformerClient interface.  Xalan will check for 
 * that interface before it calls startDocument, and, if it 
 * is implemented, pass in a TransformState reference to the 
 * setTransformState method. 
*/
public interface TransformerClient
{
  /**
   * Pass in a reference to a TransformState object, which 
   * can be used during SAX ContentHandler events to obtain 
   * information about he state of the transformation. This 
   * method will be called  before each startDocument event.
   */
  void setTransformState(TransformState ts);
}
