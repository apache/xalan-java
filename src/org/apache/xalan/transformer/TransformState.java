package org.apache.xalan.transformer;

import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.apache.trax.Transformer;

/**
 * This interface is meant to be used by a consumer of 
 * SAX2 events produced by Xalan, and enables the consumer 
 * to get information about the state of the transform.  It 
 * is primarily intended as a tooling interface.  A content 
 * handler can get a reference to a TransformState by implementing 
 * the TransformerClient interface.  Xalan will check for 
 * that interface before it calls startDocument, and, if it 
 * is implemented, pass in a TransformState reference to the 
 * setTransformState method. 
 * 
 * <p>Note that the current stylesheet and root stylesheet can 
 * be retrieved from the ElemTemplateElement obtained from 
 * either getCurrentElement() or getCurrentTemplate().</p>
 */
public interface TransformState
{
  /**
   * Retrieves the stylesheet element that produced 
   * the SAX event.
   * 
   * <p>Please note that the ElemTemplateElement returned may 
   * be in a default template, and thus may not be 
   * defined in the stylesheet.</p>
   */
  ElemTemplateElement getCurrentElement();

  /**
   * This method retrieves the current context node 
   * in the source tree.
   */
  Node getCurrentNode();
  
  /**
   * This method retrieves the xsl:template 
   * that is in effect, which may be a matched template 
   * or a named template.
   * 
   * <p>Please note that the ElemTemplate returned may 
   * be a default template, and thus may not have a template 
   * defined in the stylesheet.</p>
   */
  ElemTemplate getCurrentTemplate();

  /**
   * This method retrieves the xsl:template 
   * that was matched.  Note that this may not be 
   * the same thing as the current template (which 
   * may be from getCurrentElement()), since a named 
   * template may be in effect.
   * 
   * <p>Please note that the ElemTemplate returned may 
   * be a default template, and thus may not have a template 
   * defined in the stylesheet.</p>
   */
  ElemTemplate getMatchedTemplate();

  /**
   * Retrieves the node in the source tree that matched 
   * the template obtained via getMatchedTemplate().
   */
  Node getMatchedNode();
  
  /**
   * Get the current context node list.
   */
  NodeIterator getContextNodeList();
  
  /**
   * Get the TrAX Transformer object in effect.
   */
  Transformer getTransformer();
}
