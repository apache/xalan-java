package org.apache.xpath.objects;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xml.dtm.*;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathContext;

/**
 * This class overrides the XNodeSet#object() method to provide the original 
 * Node object, NodeList object, or NodeIterator.
 */
public class XNodeSetForDOM extends XNodeSet
{
  Object m_origObj;

  /**
   * Wrap an XNodeSetForDOM around another XNodeset
   *
   * @param val Value of the XNodeSet object
   */
  /*
  public XNodeSetForDOM(XNodeSet val)
  {
  	super(val);
  	if(val instanceof XNodeSetForDOM)
  	{
    	m_origObj = ((XNodeSetForDOM)val).m_origObj;
  	}
  }
  */

  /**
   * Wrap an XNodeSetForDOM around a single DOM Node.
   * 
   * (Interesting that this one takes the DTM Manager explicitly
   * while others get it via the xctxt. Should we reconcile?)
   *
   */
  public XNodeSetForDOM(Node node, DTMManager dtmMgr)
  {
    super(dtmMgr);
    m_origObj = node;
    int dtmHandle = dtmMgr.getDTMHandleFromNode(node);
    
	NodeSetDTM nsdtm = new NodeSetDTM(dtmMgr);
    nsdtm.addNode(dtmHandle);  
    m_obj=nsdtm;
  }
  
  /**
   * Wrap an XNodeSetForDOM around a DOM NodeList
   *
   */
  public XNodeSetForDOM(NodeList nodeList, XPathContext xctxt)
  {
    super(xctxt.getDTMManager());
    m_origObj = nodeList;

    // JKESS 20020514: Longer-term solution is to force
    // folks to request length through an accessor, so we can defer this
    // retrieval... but that requires an API change.
    // m_obj=new org.apache.xpath.NodeSetDTM(nodeList, xctxt);
    NodeSetDTM nsdtm=new org.apache.xpath.NodeSetDTM(nodeList, xctxt);
    m_last=nsdtm.getLength();
    m_obj = nsdtm;   
  }

  /**
   * Wrap an XNodeSetForDOM around a DOM NodeIterator
   *
   */
  public XNodeSetForDOM(NodeIterator nodeIter, XPathContext xctxt)
  {
    super(xctxt.getDTMManager());
    m_dtmMgr = xctxt.getDTMManager();
    m_origObj = nodeIter;

    // JKESS 20020514: Longer-term solution is to force
    // folks to request length through an accessor, so we can defer this
    // retrieval... but that requires an API change.
    // m_obj = new org.apache.xpath.NodeSetDTM(nodeIter, xctxt);
    NodeSetDTM nsdtm=new org.apache.xpath.NodeSetDTM(nodeIter, xctxt);
    m_last=nsdtm.getLength();
    m_obj = nsdtm;   
  }
  
  /**
   * Return the original DOM object that the user passed in.  For use primarily
   * by the extension mechanism.
   *
   * @return The object that this class wraps
   */
  public Object object()
  {
    return m_origObj;
  }
  
  /**
   * Cast result object to a nodelist. Always issues an error.
   *
   * @return null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeIterator nodeset() throws javax.xml.transform.TransformerException
  {
    return (m_origObj instanceof NodeIterator) 
                   ? (NodeIterator)m_origObj : super.nodeset();      
  }
  
  /**
   * Cast result object to a nodelist. Always issues an error.
   *
   * @return null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeList nodelist() throws javax.xml.transform.TransformerException
  {
    return (m_origObj instanceof NodeList) 
                   ? (NodeList)m_origObj : super.nodelist();      
  }



}