package org.apache.xpath.operations;

import org.apache.xalan.utils.QName;

import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;

import org.w3c.dom.Node;

import org.apache.xpath.res.XPATHErrorResources;

public class Variable extends Expression
{
  protected QName m_qname;
  
  public void setQName(QName qname)
  {
    m_qname = qname;
  }

  public XObject execute(XPathContext xctxt)
    throws org.xml.sax.SAXException
  {
    // Is the variable fetched always the same?
    XObject result;
    try
    {
      result = xctxt.getVariable(m_qname);
    }
    catch(Exception e)
    {
      error(xctxt, XPATHErrorResources.ER_COULDNOT_GET_VAR_NAMED, new Object[] {m_qname.getLocalPart()}); //"Could not get variable named "+varName);
      result = null;
    }

    if(null == result)
    {
      warn(xctxt, XPATHErrorResources.WG_ILLEGAL_VARIABLE_REFERENCE, new Object[] {m_qname.getLocalPart()}); //"VariableReference given for variable out "+
      result = new XNodeSet();
    }

    return result;
  }
}
