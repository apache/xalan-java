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

public class KeyIterator extends LocPathIterator
{
  private QName m_name;
  public QName getName() { return m_name; }
  
  private Vector m_keyDeclarations;
  public Vector getKeyDeclarations() { return m_keyDeclarations; }
  
  public KeyIterator(Node doc, 
                     PrefixResolver nscontext, 
                     QName name, 
                     Vector keyDeclarations, 
                     XPathContext xctxt)
  {
    super(nscontext);
    initContext(xctxt);
    m_name = name;
    m_keyDeclarations = keyDeclarations;
    m_firstWalker = new KeyWalker(this);
    this.setLastUsedWalker(m_firstWalker);
  }
  
  public Node nextNode()
    throws DOMException
  {  
    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    Node n = super.nextNode();
    // System.out.println("--> "+((null == n) ? "null" : n.getNodeName()));
    return n;
  }

  
  public void setLookupKey(String lookupKey)
  {
    // System.out.println("setLookupKey - lookupKey: "+lookupKey);
    ((KeyWalker)m_firstWalker).m_lookupKey = lookupKey;
    m_firstWalker.setRoot(getContext());
    this.setLastUsedWalker(m_firstWalker);
    this.setNextPosition(0);
  }  
  
}
