package org.apache.xpath;

import java.util.Hashtable;
import java.util.Vector;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.apache.xalan.utils.NSInfo;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.StringBufferPool;
import org.apache.xalan.utils.FastStringBuffer;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;

// Imported JAVA API for XML Parsing 1.0 classes
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; 

public class DOMHelper
{
  /**
   * Used as a helper for handling DOM issues.  May be subclassed to take advantage 
   * of specific DOM implementations.
   */
  public Document createDocument()
  {
    try
    {
      // Use an implementation of the JAVA API for XML Parsing 1.0 to
      // create a DOM Document node to contain the result.
      DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
      dfactory.setNamespaceAware(true);
      dfactory.setValidating(true);
      DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
      Document outNode = docBuilder.newDocument();
      return outNode;
    }
    catch(ParserConfigurationException pce)
    {
      throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CREATEDOCUMENT_NOT_SUPPORTED, null));//"createDocument() not supported in XPathContext!");
      // return null;
    }
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Tells, through the combination of the default-space attribute 
   * on xsl:stylesheet, xsl:strip-space, xsl:preserve-space, and the
   * xml:space attribute, whether or not extra whitespace should be stripped 
   * from the node.  Literal elements from template elements should 
   * <em>not</em> be tested with this function.
   * @param textNode A text node from the source tree.
   * @return true if the text node should be stripped of extra whitespace.
   */
  public boolean shouldStripSourceNode(Node textNode)
    throws org.xml.sax.SAXException
  {
    // return (null == m_envSupport) ? false : m_envSupport.shouldStripSourceNode(textNode);
    return false;
  }
  
  public String getUniqueID(Node node)
  {
    return "N"+Integer.toHexString(node.hashCode());
  }
  
  /**
   * Figure out if node2 should be placed after node1 in 
   * document order (returns node1 &lt;= node2).
   * NOTE: Make sure this does the right thing with attribute nodes!!!
   * @return true if node2 should be placed 
   * after node1, and false if node2 should be placed 
   * before node1.
   */
  public boolean isNodeAfter(Node node1, Node node2)
  {
    if(node1 == node2)
      return true;

    boolean isNodeAfter = false; // return value.
    
    Node parent1 = getParentOfNode(node1);
    Node parent2 = getParentOfNode(node2);
    
    // Optimize for most common case
    if(parent1 == parent2) // then we know they are siblings
    {
      if (null != parent1)
        isNodeAfter = isNodeAfterSibling(
                                       parent1, node1, node2);
      else
      {
        if(node1 == node2) // Same document?
          return false;
        else
          return true;
      }
    }
    else
    {
      // General strategy: Figure out the lengths of the two 
      // ancestor chains, and walk up them looking for the 
      // first common ancestor, at which point we can do a 
      // sibling compare.  Edge condition where one is the 
      // ancestor of the other.
      
      // Count parents, so we can see if one of the chains 
      // needs to be equalized.
      int nParents1 = 2, nParents2 = 2; // count node & parent obtained above
      while(parent1 != null)
      {
        nParents1++;
        parent1 = getParentOfNode(parent1);
      }
      
      while(parent2 != null)
      {
        nParents2++;
        parent2 = getParentOfNode(parent2);
      }
      
      Node startNode1 = node1, startNode2 = node2; // adjustable starting points
      
      // Do I have to adjust the start point in one of 
      // the ancesor chains?
      if(nParents1 < nParents2)
      {
        // adjust startNode2
        int adjust = nParents2 - nParents1;
        for(int i = 0; i < adjust; i++)
        {
          startNode2 = getParentOfNode(startNode2);
        }
      }
      else if(nParents1 > nParents2)
      {
        // adjust startNode1
        int adjust = nParents1 - nParents2;
        for(int i = 0; i < adjust; i++)
        {
          startNode1 = getParentOfNode(startNode1);
        }
      }
      
      Node prevChild1 = null, prevChild2 = null; // so we can "back up"
      
      // Loop up the ancestor chain looking for common parent.
      while(null != startNode1)
      {
        if(startNode1 == startNode2) // common parent?
        {
          if(null == prevChild1) // first time in loop?
          {
            // Edge condition: one is the ancestor of the other.
            isNodeAfter = (nParents1 < nParents2) ? true : false;
            break; // from while loop
          }
          else
          {
            isNodeAfter = isNodeAfterSibling(
                                             startNode1, prevChild1, prevChild2);
            break; // from while loop
          }
        } // end if(startNode1 == startNode2)
        prevChild1 = startNode1;
        startNode1 = getParentOfNode(startNode1);
        prevChild2 = startNode2;
        startNode2 = getParentOfNode(startNode2);
      } // end while
    } // end big else
    
    /* -- please do not remove... very useful for diagnostics --
    System.out.println("node1 = "+node1.getNodeName()+"("+node1.getNodeType()+")"+
    ", node2 = "+node2.getNodeName()
    +"("+node2.getNodeType()+")"+
    ", isNodeAfter = "+isNodeAfter); */
    
    return isNodeAfter;
  } // end isNodeAfter(Node node1, Node node2)
  
  /**
   * Figure out if child2 is after child1 in document order.
   * @param parent Must be the parent of child1 and child2.
   * @param child1 Must be the child of parent and not equal to child2.
   * @param child2 Must be the child of parent and not equal to child1.
   * @returns true if child 2 is after child1 in document order.
   */
  private static boolean isNodeAfterSibling(
                                     Node parent, Node child1, Node child2)
  {
    boolean isNodeAfterSibling = false;
    int child1type = child1.getNodeType();
    int child2type = child2.getNodeType();
    if((Node.ATTRIBUTE_NODE != child1type) && (Node.ATTRIBUTE_NODE == child2type))
    {
      // always sort attributes before non-attributes.
      isNodeAfterSibling = false;
    }
    else if((Node.ATTRIBUTE_NODE == child1type) && (Node.ATTRIBUTE_NODE != child2type))
    {
      // always sort attributes before non-attributes.
      isNodeAfterSibling = true;
    }
    else if(Node.ATTRIBUTE_NODE == child1type)
    {
      NamedNodeMap children = parent.getAttributes();
      int nNodes = children.getLength();
      boolean found1 = false, found2 = false;
      for(int i = 0; i < nNodes; i++)
      {
        Node child = children.item(i);
        if(child1 == child)
        {
          if(found2)
          {
            isNodeAfterSibling = false;
            break;
          }
          found1 = true;
        }
        else if(child2 == child)
        {
          if(found1)
          {
            isNodeAfterSibling = true;
            break;
          }
          found2 = true;
        }
      }
    }
    else
    {
      // NodeList children = parent.getChildNodes();
      // int nNodes = children.getLength();
      Node child = parent.getFirstChild();
      boolean found1 = false, found2 = false;
      while(null != child)
      {
        // Node child = children.item(i);
        if(child1 == child)
        {
          if(found2)
          {
            isNodeAfterSibling = false;
            break;
          }
          found1 = true;
        }
        else if(child2 == child)
        {
          if(found1)
          {
            isNodeAfterSibling = true;
            break;
          }
          found2 = true;
        }
        child = child.getNextSibling();
      }
    }
    return isNodeAfterSibling;
  } // end isNodeAfterSibling(Node parent, Node child1, Node child2)
  
  
  //==========================================================
  // SECTION: Namespace resolution
  //==========================================================
  
  /**
   * <meta name="usage" content="internal"/>
   * Get the depth level of this node in the tree (count from 1).
   */
  public short getLevel(Node n)
  {
    short level = 1;
    while(null != (n = getParentOfNode(n)))
    {
      level++;
    }
    return level;
  }
  
  /**
   * Given a prefix and a namespace context, return the expanded namespace.
   * Default handling: 
   */
  public String getNamespaceForPrefix(String prefix, Element namespaceContext)
  {
    int type;
    Node parent = namespaceContext;
    String namespace = null;
    if(prefix.equals("xml"))
    {
      namespace = QName.S_XMLNAMESPACEURI;
    }
    else
    {
      while ((null != parent) && (null == namespace)
             && (((type = parent.getNodeType()) == Node.ELEMENT_NODE)
                 || (type == Node.ENTITY_REFERENCE_NODE))) 
      {
        if (type == Node.ELEMENT_NODE) 
        {
          NamedNodeMap nnm = parent.getAttributes();
          for (int i = 0;  i < nnm.getLength();  i ++) 
          {
            Node attr = nnm.item(i);
            String aname = attr.getNodeName();
            boolean isPrefix = aname.startsWith("xmlns:");
            if (isPrefix || aname.equals("xmlns")) 
            {
              int index = aname.indexOf(':');
              String p = isPrefix ? aname.substring(index+1) : "";
              if (p.equals(prefix)) 
              {
                namespace = attr.getNodeValue();
                break;
              }
            }
          }
        }
        parent = getParentOfNode(parent);
      }
    }
    return namespace;
  }
  
  /**
   * An experiment for the moment.
   */
  Hashtable m_NSInfos = new Hashtable();
  
  protected static final NSInfo m_NSInfoUnProcWithXMLNS = new NSInfo(false, true);
  protected static final NSInfo m_NSInfoUnProcWithoutXMLNS = new NSInfo(false, false);
  protected static final NSInfo m_NSInfoUnProcNoAncestorXMLNS = new NSInfo(false, false, NSInfo.ANCESTORNOXMLNS);
  protected static final NSInfo m_NSInfoNullWithXMLNS = new NSInfo(true, true);
  protected static final NSInfo m_NSInfoNullWithoutXMLNS = new NSInfo(true, false);
  protected static final NSInfo m_NSInfoNullNoAncestorXMLNS = new NSInfo(true, false, NSInfo.ANCESTORNOXMLNS);
  
  protected Vector m_candidateNoAncestorXMLNS =  new Vector();
  
  /**
   * Returns the namespace of the given node.
   */
  public String getNamespaceOfNode(Node n)
  {
    String namespaceOfPrefix;
    boolean hasProcessedNS;
    NSInfo nsInfo;
    int ntype = n.getNodeType();
    if(Node.ATTRIBUTE_NODE != ntype)
    {
      Object nsObj = m_NSInfos.get(n); // return value
      nsInfo = (nsObj == null) ? null : (NSInfo)nsObj;
      hasProcessedNS = (nsInfo == null) ? false : nsInfo.m_hasProcessedNS;
    }
    else
    {
      hasProcessedNS = false;
      nsInfo = null;
    }
    if(hasProcessedNS)
    {
      namespaceOfPrefix = nsInfo.m_namespace;
    }
    else
    {
      namespaceOfPrefix = null;
      String nodeName = n.getNodeName();
      int indexOfNSSep = nodeName.indexOf(':');
      String prefix;

      if(Node.ATTRIBUTE_NODE == ntype)
      {
        if(indexOfNSSep > 0)
        {
          prefix = nodeName.substring(0, indexOfNSSep);
        }
        else
        {
          // Attributes don't use the default namespace, so if 
          // there isn't a prefix, we're done.
          return namespaceOfPrefix;
        }
      }
      else
      {
        prefix = (indexOfNSSep >= 0) ? nodeName.substring(0, indexOfNSSep) : "";
      }
      boolean ancestorsHaveXMLNS = false;
      boolean nHasXMLNS = false;
      if(prefix.equals("xml"))
      {
        namespaceOfPrefix = QName.S_XMLNAMESPACEURI;
      }
      else
      {
        int parentType;
        Node parent = n;
        while ((null != parent) && (null == namespaceOfPrefix)) 
        {
          if((null != nsInfo) 
             && (nsInfo.m_ancestorHasXMLNSAttrs == nsInfo.ANCESTORNOXMLNS))
          {
            break;
          }
          parentType = parent.getNodeType();
          if((null == nsInfo) || nsInfo.m_hasXMLNSAttrs)
          {
            boolean elementHasXMLNS = false;
            if (parentType == Node.ELEMENT_NODE) 
            {
              NamedNodeMap nnm = parent.getAttributes();
              for (int i = 0;  i < nnm.getLength();  i ++) 
              {
                Node attr = nnm.item(i);
                String aname = attr.getNodeName();
                if(aname.charAt(0) == 'x')
                {
                  boolean isPrefix = aname.startsWith("xmlns:");
                  if (aname.equals("xmlns") || isPrefix) 
                  {
                    if(n == parent)
                      nHasXMLNS = true;
                    elementHasXMLNS = true;
                    ancestorsHaveXMLNS = true;
                    String p = isPrefix ? aname.substring(6) : "";
                    if (p.equals(prefix)) 
                    {
                      namespaceOfPrefix = attr.getNodeValue();
                      break;
                    }
                  }
                }
              }
            }
            if((Node.ATTRIBUTE_NODE != parentType) && (null == nsInfo) && (n != parent))
            {
              nsInfo = elementHasXMLNS ? m_NSInfoUnProcWithXMLNS : m_NSInfoUnProcWithoutXMLNS;
              m_NSInfos.put(parent, nsInfo);
            }
          }
          if(Node.ATTRIBUTE_NODE == parentType)
          {
            parent = getParentOfNode(parent);
          }
          else
          {
            m_candidateNoAncestorXMLNS.addElement(parent);
            m_candidateNoAncestorXMLNS.addElement(nsInfo);
            parent = parent.getParentNode();
          }
          if(null != parent)
          {
            Object nsObj = m_NSInfos.get(parent); // return value
            nsInfo = (nsObj == null) ? null : (NSInfo)nsObj;
          }
        }
        int nCandidates = m_candidateNoAncestorXMLNS.size();
        if(nCandidates > 0)
        {
          if((false == ancestorsHaveXMLNS) && (null == parent))
          {
            for(int i = 0; i < nCandidates; i+=2)
            {
              Object candidateInfo = m_candidateNoAncestorXMLNS.elementAt(i+1);
              if(candidateInfo == m_NSInfoUnProcWithoutXMLNS)
              {
                m_NSInfos.put(m_candidateNoAncestorXMLNS.elementAt(i), 
                              m_NSInfoUnProcNoAncestorXMLNS );
              }
              else if(candidateInfo == m_NSInfoNullWithoutXMLNS)
              {
                m_NSInfos.put(m_candidateNoAncestorXMLNS.elementAt(i), 
                              m_NSInfoNullNoAncestorXMLNS );
              }
            }
          }
          m_candidateNoAncestorXMLNS.removeAllElements();
        }
      }
      
      if(Node.ATTRIBUTE_NODE != ntype)
      {
        if(null == namespaceOfPrefix)
        {
          if(ancestorsHaveXMLNS)
          {
            if(nHasXMLNS)
              m_NSInfos.put(n, m_NSInfoNullWithXMLNS );
            else
              m_NSInfos.put(n, m_NSInfoNullWithoutXMLNS );
          }
          else
          {
            m_NSInfos.put(n, m_NSInfoNullNoAncestorXMLNS );
          }
        }
        else
        {
          m_NSInfos.put(n,  new NSInfo(namespaceOfPrefix, nHasXMLNS));
        }
      }
    }
    return namespaceOfPrefix;
  }

  /**
   * Returns the local name of the given node.
   */
  public String getLocalNameOfNode(Node n)
  {
    String qname = n.getNodeName();
    int index = qname.indexOf(':');
    return (index < 0) ? qname : qname.substring(index+1);
  }

  /**
   * Returns the element name with the namespace expanded.
   */
  public String getExpandedElementName(Element elem)
  {
    String namespace = getNamespaceOfNode(elem);
    return (null != namespace) ? namespace+":"+ getLocalNameOfNode(elem) 
                                 : getLocalNameOfNode(elem);
  }

  /**
   * Returns the attribute name with the namespace expanded.
   */
  public String getExpandedAttributeName(Attr attr)
  {
    String namespace = getNamespaceOfNode(attr);
    return (null != namespace) ? namespace+":"+ getLocalNameOfNode(attr) 
                                 : getLocalNameOfNode(attr);
  }

  //==========================================================
  // SECTION: DOM Helper Functions
  //==========================================================

  /** 
   * Tell if the node is ignorable whitespace.
   * @deprecated
   */
  public boolean isIgnorableWhitespace(Text node)
  {
    boolean isIgnorable = false; // return value
    // TODO: I can probably do something to figure out if this 
    // space is ignorable from just the information in
    // the DOM tree.
    return isIgnorable;
  }
  
  /**
   * Get the first unparented node in the ancestor chain.
   * @deprecated
   */
  public Node getRoot(Node node)
  {
    Node root = null;
    while(node != null)
    {
      root = node;
      node = getParentOfNode(node);
    }
    return root;
  }
  
  /**
   * Get the root node of the document tree, regardless of 
   * whether or not the node passed in is a document node.
   */
  public Node getRootNode(Node n)
  {
    return (Node.DOCUMENT_NODE == n.getNodeType()) ? 
           n : n.getOwnerDocument();
  }
  
  /**
   * Tell if the given node is a namespace decl node.
   */
  public boolean isNamespaceNode(Node n)
  {
    if(Node.ATTRIBUTE_NODE == n.getNodeType())
    {
      String attrName = n.getNodeName();
      return (attrName.startsWith("xmlns:") || attrName.equals("xmlns"));
    }
    return false;
  }
  
  /**
   * I have to write this silly, and expensive function, 
   * because the DOM WG decided that attributes don't 
   * have parents.  If Xalan is used with a DOM implementation
   * that reuses attribute nodes, this will not work correctly.
   */
  public Node getParentOfNode(Node node)
    throws RuntimeException
  {
    Node parent;
    int nodeType = node.getNodeType();
    if(Node.ATTRIBUTE_NODE == nodeType)
    {
      Document doc = node.getOwnerDocument();
      
      /*
      TBD:
      if(null == doc)
      {
        throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CHILD_HAS_NO_OWNER_DOCUMENT, null));//"Attribute child does not have an owner document!");
      }
      */
      
      Element rootElem = doc.getDocumentElement();
      
      if(null == rootElem)
      {
        throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT, null));//"Attribute child does not have an owner document element!");
      }
      
      parent = locateAttrParent(rootElem, node);
    }
    else
    {
      parent = node.getParentNode();
      // if((Node.DOCUMENT_NODE != nodeType) && (null == parent))
      // {
      //   throw new RuntimeException("Child does not have parent!");
      // }
    }
    return parent;
  }
  
  /**
   * Given an ID, return the element.
   */
  public Element getElementByID(String id, Document doc)
  {
    return null;
  }
  
  /**
   * The getUnparsedEntityURI function returns the URI of the unparsed 
   * entity with the specified name in the same document as the context 
   * node (see [3.3 Unparsed Entities]). It returns the empty string if 
   * there is no such entity.
   * Since it states in the DOM draft: "An XML processor may choose to 
   * completely expand entities before the structure model is passed 
   * to the DOM; in this case, there will be no EntityReferences in the DOM tree."
   * So I'm not sure how well this is going to work.
   */
  public String getUnparsedEntityURI(String name, Document doc)
  {
    String url = ""; 
    DocumentType doctype = doc.getDoctype(); 
    if(null != doctype)
    {
      NamedNodeMap entities = doctype.getEntities();
      Entity entity = (Entity)entities.getNamedItem(name);
      String notationName = entity.getNotationName();
      if(null != notationName) // then it's unparsed
      {
        // The draft says: "The XSLT processor may use the public 
        // identifier to generate a URI for the entity instead of the URI 
        // specified in the system identifier. If the XSLT processor does 
        // not use the public identifier to generate the URI, it must use 
        // the system identifier; if the system identifier is a relative 
        // URI, it must be resolved into an absolute URI using the URI of 
        // the resource containing the entity declaration as the base 
        // URI [RFC2396]."
        // So I'm falling a bit short here.
        url = entity.getSystemId();
        if(null == url)
        {
          url = entity.getPublicId();
        }
        else
        {
          // This should be resolved to an absolute URL, but that's hard 
          // to do from here.
        }
      }
    }
    return url;
  }  
  /**
   * Support for getParentOfNode.
   */
  private Node locateAttrParent(Element elem, Node attr)
  {
    Node parent = null;
    NamedNodeMap attrs = elem.getAttributes();
    if(null != attrs)
    {
      int nAttrs = attrs.getLength();
      for(int i = 0; i < nAttrs; i++)
      {
        if(attr == attrs.item(i))
        {
          parent = elem;
          break;
        }
      }
    }
    if(null == parent)
    {
      for(Node node = elem.getFirstChild(); null != node; node = node.getNextSibling())
      {
        if(Node.ELEMENT_NODE == node.getNodeType())
        {
          parent = locateAttrParent((Element)node, attr);
          if(null != parent) break;
        }
      }
    }
    return parent;
  }
    
  /**
   * The factory object used for creating nodes 
   * in the result tree.
   */
  protected Document m_DOMFactory = null;
  
  /**
   * Get the factory object required to create DOM nodes 
   * in the result tree.
   */
  public void setDOMFactory(Document domFactory)
  {
    this.m_DOMFactory = domFactory;
  }

  
  /**
   * Get the factory object required to create DOM nodes 
   * in the result tree.
   */
  public Document getDOMFactory()
  {
    if(null == this.m_DOMFactory)
    {
      this.m_DOMFactory = createDocument();
    }
    
    return this.m_DOMFactory;
  }
  
  /**
   * Get the textual contents of the node. If the node 
   * is an element, apply whitespace stripping rules, 
   * though I'm not sure if this is right (I'll fix 
   * or declare victory when I review the entire 
   * whitespace handling).
   */
  public static String getNodeData(Node node)
  {
    FastStringBuffer buf = StringBufferPool.get();
    String s;
    try
    {
      getNodeData(node, buf);
      s = (buf.length() > 0) ? buf.toString() : "";
    }
    finally
    {
      StringBufferPool.free(buf);
    }
    return s;
  }
  
  /**
   * Get the textual contents of the node. If the node 
   * is an element, apply whitespace stripping rules, 
   * though I'm not sure if this is right (I'll fix 
   * or declare victory when I review the entire 
   * whitespace handling).
   */
  public static void getNodeData(Node node, FastStringBuffer buf)
  {
    // String data = null;

    switch(node.getNodeType())
    {
    case Node.DOCUMENT_FRAGMENT_NODE:
      {
        NodeList mnl = node.getChildNodes();
        int n = mnl.getLength();
        for(int i = 0; i < n; i++)
          getNodeData(mnl.item(i), buf);
      }	  
      break;
    case Node.DOCUMENT_NODE:
    case Node.ELEMENT_NODE:
      {
        for(Node child = node.getFirstChild(); null != child; child = child.getNextSibling())
          getNodeData(child, buf);
      }
      break;
    case Node.TEXT_NODE:
    case Node.CDATA_SECTION_NODE:
      buf.append( node.getNodeValue() );
      break;
    case Node.ATTRIBUTE_NODE:
      buf.append( node.getNodeValue() );
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:      
      // warning(XPATHErrorResources.WG_PARSING_AND_PREPARING);        
      break;
    default:
      // ignore
      break;
    }
  }

}
