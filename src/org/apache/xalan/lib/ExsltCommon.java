package org.apache.xalan.lib;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XRTreeFrag;

import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.utils.XMLString;

import org.xml.sax.SAXNotSupportedException;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.xslt.EnvironmentCheck;

import javax.xml.parsers.*;

/**
 * <meta name="usage" content="general"/>
 * This class contains EXSLT common extension functions.
 * It is accessed by specifying a namespace URI as follows:
 * <pre>
 *    xmlns:exslt="http://exslt.org/xalan/common"
 * </pre>
 * 
 * The documentation for each function has been copied from the relevant
 * EXSLT Implementer page.
 * 
 * @see <a href="http://www.exslt.org/">EXSLT</a>
 */
public class ExsltCommon
{
  /**
   * The exsl:object-type function returns a string giving the type of the object passed 
   * as the argument. The possible object types are: 'string', 'number', 'boolean', 
   * 'node-set', 'RTF', or 'external'. 
   * 
   * Most XSLT object types can be coerced to each other without error. However, there are 
   * certain coercions that raise errors, most importantly treating anything other than a 
   * node set as a node set. Authors of utilities such as named templates or user-defined 
   * extension functions may wish to give some flexibility in the parameter and argument values 
   * that are accepted by the utility; the exsl:object-type function enables them to do so.
   * 
   * The Xalan extensions MethodResolver converts 'object-type' to 'objectType'.
   * 
   * @param obj The object to be typed.
   * @return objectType 'string', 'number', 'boolean', 'node-set', 'RTF', or 'external'.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static String objectType (Object obj)
  {
    if (obj instanceof String)
      return "string";
    else if (obj instanceof Boolean)
      return "boolean";
    else if (obj instanceof Number)
      return "number";
    else if (obj instanceof DTMNodeIterator)
    {
      DTMIterator dtmI = ((DTMNodeIterator)obj).getDTMIterator();
      // Need to verify that OneStepIteratorForward is consistently the DTM iterator 
      // for rtfs and only rtfs.
      if (dtmI.getClass().getName().equals("org.apache.xpath.axes.OneStepIteratorForward"))
        return "RTF";
      else
        return "node-set";
    }
    else
      return "external";
  }
    
  /**
   * The exsl:node-set function converts a result tree fragment (which is what you get 
   * when you use the content of xsl:variable rather than its select attribute to give 
   * a variable value) into a node set. This enables you to process the XML that you create 
   * within a variable, and therefore do multi-step processing. 
   * 
   * You can also use this function to turn a string into a text node, which is helpful 
   * if you want to pass a string to a function that only accepts a node set.
   * 
   * The Xalan extensions MethodResolver converts 'node-set' to 'nodeSet'.
   * 
   * @param myProcesser is passed in by the Xalan extension processor
   * @param rtf The result tree fragment to be converted to a node-set.
   * 
   * @returns node-set with the contents of the result tree fragment.
   * 
   * Note: Already implemented in the xalan namespace as nodeset.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static NodeSet nodeSet(ExpressionContext myProcessor, Object rtf)
  {
    return Extensions.nodeset(myProcessor, rtf);
  }

  public static NodeSet intersection(NodeIterator ni1, NodeIterator ni2)
          throws javax.xml.transform.TransformerException
  {
    return Extensions.intersection(ni1, ni2);
  }
  
  public static NodeSet difference(NodeIterator ni1, NodeIterator ni2)
          throws javax.xml.transform.TransformerException
  {
    return Extensions.difference(ni1, ni2);
  }
  
  public static NodeSet distinct(ExpressionContext myContext, NodeIterator ni)
          throws javax.xml.transform.TransformerException
  {
    return Extensions.distinct(myContext, ni);
  }
  
  public static boolean hasSameNodes(NodeIterator ni1, NodeIterator ni2)
  {
    return Extensions.hasSameNodes(ni1, ni2);
  }

}