package org.apache.xalan.transformer;

import org.apache.xalan.templates.Stylesheet;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.CDATASection;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.EntityReference;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xalan.res.XSLTErrorResources;

public class ClonerToResultTree
{
  private ResultTreeHandler m_rth;
  private TransformerImpl m_transformer;
  
  public ClonerToResultTree(TransformerImpl transformer, ResultTreeHandler rth)
  {
    m_rth = rth;
    m_transformer = transformer;
  }
  
  /**
   * Clone an element with or without children.
   * TODO: Fix or figure out node clone failure!
   * the error condition is severe enough to halt processing.
   */
  public void cloneToResultTree(Stylesheet stylesheetTree, Node node,
                                boolean shouldCloneWithChildren,
                                boolean overrideStrip,
                                boolean shouldCloneAttributes)
    throws SAXException
  {
    boolean stripWhiteSpace = false;
    XPathContext xctxt = m_transformer.getXPathContext();
    DOMHelper dhelper = xctxt.getDOMHelper();

    switch(node.getNodeType())
    {
    case Node.TEXT_NODE:
      {
        // If stripWhiteSpace is false, then take this as an override and
        // just preserve the space, otherwise use the XSL whitespace rules.
        if(!overrideStrip)
        {
          // stripWhiteSpace = isLiteral ? true : shouldStripSourceNode(node);
          stripWhiteSpace = false;
        }
        Text tx = (Text)node;
        String data = null;
        // System.out.println("stripWhiteSpace = "+stripWhiteSpace+", "+tx.getData());
        if(stripWhiteSpace)
        {
          if(!dhelper.isIgnorableWhitespace(tx))
          {
            data = tx.getData();
            if((null != data) && (0 == data.trim().length()))
            {
              data = null;
            }
          }
        }
        else
        {
          Node parent = node.getParentNode();
          if(null != parent)
          {
            if( Node.DOCUMENT_NODE != parent.getNodeType())
            {
              data = tx.getData();
              if((null != data) && (0 == data.length()))
              {
                data = null;
              }
            }
          }
          else
          {
            data = tx.getData();
            if((null != data) && (0 == data.length()))
            {
              data = null;
            }
          }
        }

        if(null != data)
        {
          // TODO: Hack around the issue of comments next to literals.
          // This would be, when a comment is present, the whitespace
          // after the comment must be added to the literal.  The
          // parser should do this, but XML4J doesn't seem to.
          // <foo>some lit text
          //     <!-- comment -->
          //     </foo>
          // Loop through next siblings while they are comments, then,
          // if the node after that is a ignorable text node, append
          // it to the text node just added.
          if(dhelper.isIgnorableWhitespace(tx))
          {
            m_rth.ignorableWhitespace(data.toCharArray(), 0, data.length());
          }
          else
          {
            m_rth.characters(data.toCharArray(), 0, data.length());
          }
        }
      }
      break;
    case Node.DOCUMENT_NODE:
      // Can't clone a document, but refrain from throwing an error
      // so that copy-of will work
      break;
    case Node.ELEMENT_NODE:
      {
        Attributes atts;
        if(shouldCloneAttributes)
        {
          m_rth.addAttributes( node );
          m_rth.processNSDecls(node);
        }
        String ns = dhelper.getNamespaceOfNode(node);
        String localName = dhelper.getLocalNameOfNode(node);
        m_rth.startElement (ns, localName, node.getNodeName());
      }
      break;
    case Node.CDATA_SECTION_NODE:
      {
        m_rth.startCDATA();
        String data = ((CDATASection)node).getData();
        m_rth.characters(data.toCharArray(), 0, data.length());
        m_rth.endCDATA();
      }
      break;
    case Node.ATTRIBUTE_NODE:
      {
        String ns = dhelper.getNamespaceOfNode(node);
        String localName = dhelper.getLocalNameOfNode(node);
        m_rth.addAttribute(ns, localName, node.getNodeName(), "CDATA", 
                     ((Attr)node).getValue());
      }
      break;
    case Node.COMMENT_NODE:
      {
        m_rth.comment(((Comment)node).getData());
      }
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      {
        
        m_transformer.getMsgMgr().error(null, node, XSLTErrorResources.ER_NO_CLONE_OF_DOCUMENT_FRAG); //"No clone of a document fragment!");
      }
      break;
    case Node.ENTITY_REFERENCE_NODE:
      {
        EntityReference er = (EntityReference)node;
        m_rth.entityReference(er.getNodeName());
      }
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      {
        ProcessingInstruction pi = (ProcessingInstruction)node;
        m_rth.processingInstruction(pi.getTarget(), pi.getData());
      }
      break;
    default:
      m_transformer.getMsgMgr().error(XSLTErrorResources.ER_CANT_CREATE_ITEM, new Object[] {node.getNodeName()}); //"Can not create item in result tree: "+node.getNodeName());
    }

  } // end cloneToResultTree function
}
