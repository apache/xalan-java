package org.apache.xalan.transformer;

import java.util.Vector;

import org.apache.xpath.axes.LocPathIterator;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.utils.QName;

import org.apache.xalan.templates.KeyDeclaration;

import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.DescendantOrSelfWalker;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPath;

import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import org.xml.sax.SAXException;

public class KeyWalker extends DescendantOrSelfWalker
{
  /**
   * Construct a KeyWalker using a LocPathIterator.
   */
  public KeyWalker(LocPathIterator locPathIterator)
  {
    super(locPathIterator);
  }
  
  /**
   *  Set the root node of the TreeWalker.
   */
  public void setRoot(Node root)
  {
    m_attrs = null;
    m_foundAttrs = false;
    m_attrPos = 0;
    super.setRoot(root);
  }
  
  NamedNodeMap m_attrs;
  boolean m_foundAttrs;
  int m_attrPos;
  String m_lookupKey;
  
  /**
   * Get the next node in document order on the axes.
   */
  protected Node getNextNode()
  {
    if(!m_foundAttrs)
    {
      m_attrs = getCurrentNode().getAttributes();
      m_foundAttrs = true;
    }
    if(null != m_attrs)
    {
      if(m_attrPos < m_attrs.getLength())
      {
        return m_attrs.item(m_attrPos++);
      }
      else
      {
        m_attrs = null;
      }
    }
    
    Node next = super.getNextNode();
    if(null != next)
      m_foundAttrs = false;
    
    return next;
  }

  
  /**
   *  Test whether a specified node is visible in the logical view of a 
   * TreeWalker or NodeIterator. This function will be called by the 
   * implementation of TreeWalker and NodeIterator; it is not intended to 
   * be called directly from user code.
   * @param n  The node to check to see if it passes the filter or not.
   * @return  a constant to determine whether the node is accepted, 
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(Node testNode)
  {
    KeyIterator ki = (KeyIterator)m_lpi;
    Vector keys = ki.getKeyDeclarations();
    QName name = ki.getName();
    try
    {
      String lookupKey = m_lookupKey;
      // System.out.println("lookupKey: "+lookupKey);
      int nDeclarations = keys.size();

      // Walk through each of the declarations made with xsl:key
      for(int i = 0; i < nDeclarations; i++)
      {
        KeyDeclaration kd = (KeyDeclaration)keys.elementAt(i);
        
        if(!kd.getName().equals(name)) 
          continue;
        
        // See if our node matches the given key declaration according to 
        // the match attribute on xsl:key.
        double score = kd.getMatch().getMatchScore(ki.getXPathContext(), testNode);
        
        if(score == kd.getMatch().MATCH_SCORE_NONE)
          continue;

        // Query from the node, according the the select pattern in the
        // use attribute in xsl:key.
        XObject xuse = kd.getUse().execute(ki.getXPathContext(), testNode, ki.getPrefixResolver());

        if(xuse.getType() != xuse.CLASS_NODESET)
        {
          String exprResult = xuse.str();
          if(lookupKey.equals(exprResult))
            return this.FILTER_ACCEPT;
        }
        else
        {
          NodeIterator nl = xuse.nodeset();
          Node useNode;
          while(null != (useNode = nl.nextNode()))
          {
            String exprResult = m_lpi.getDOMHelper().getNodeData(useNode);
            if((null != exprResult) && lookupKey.equals(exprResult))
              return this.FILTER_ACCEPT;
          }
        }

      } // end for(int i = 0; i < nDeclarations; i++)

    }
    catch(SAXException se)
    {
      // TODO: What to do?
    }
    return this.FILTER_REJECT;
  }
}
