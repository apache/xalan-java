package org.apache.xpath.functions;

import java.util.Vector;
import org.apache.xpath.Expression;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.*;
import org.apache.xalan.extensions.ExtensionsTable;

import org.w3c.dom.Node;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.traversal.NodeIterator;

public class FuncExtFunction extends Function
{
  String m_namespace;
  String m_extensionName; 
  Object m_methodKey;
  Vector m_argVec = new Vector();
  
  public FuncExtFunction(java.lang.String namespace, 
                         java.lang.String extensionName, 
                         Object methodKey) 
  {
    m_namespace = namespace;
    m_extensionName = extensionName; 
    m_methodKey = methodKey;
  }
  
  /**
   * Execute the function.  The function must return 
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   */
  public XObject execute(XPathContext xctxt) 
    throws org.xml.sax.SAXException
  {   
    XObject result;
    Vector argVec = new Vector();
    int nArgs = m_argVec.size();
    for(int i = 0; i < nArgs; i++)
    {
      Expression arg = (Expression)m_argVec.elementAt(i);
      argVec.addElement(arg.execute(xctxt));
    }
    ExtensionsTable etable = xctxt.getExtensionsTable();
    Object val = etable.extFunction(m_namespace, m_extensionName, 
                                    argVec, m_methodKey, xctxt);
    if(null != val)
    {
      if(val instanceof XObject)
      {
        result = (XObject)val;
      }
      // else if(val instanceof XLocator)
      // {
        // XLocator locator = (XLocator)val;
        // opPos = getNextOpPos(opPos+1);
        // result = locator.connectToNodes(this, opPos, argVec);  
        // System.out.println("nodeset len: "+result.nodeset().getLength());
      // }
      else if(val instanceof String)
      {
        result = new XString((String)val);
      }
      else if(val instanceof Boolean)
      {
        result = ((Boolean)val).booleanValue() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      }
      else if(val instanceof Double)
      {
        result = new XNumber(((Double)val).doubleValue());
      }
      else if(val instanceof DocumentFragment)
      {
        result = new XRTreeFrag((DocumentFragment)val);
      }
      else if(val instanceof NodeIterator)
      {
        result = new XNodeSet((NodeIterator)val);
      }
      else if(val instanceof Node)
      {
        result = new XNodeSet((Node)val);
      }
      else
      {
        result = new XObject(val);
      }
    }
    else
    {
      result = new XNull();
    }
    return result;

  }
  
  
  public void setArg(Expression arg, int argNum)
    throws WrongNumberArgsException
  {
    m_argVec.addElement(arg);
  }
  
  public void checkNumberArgs(int argNum)
    throws WrongNumberArgsException
  {
  }
}
