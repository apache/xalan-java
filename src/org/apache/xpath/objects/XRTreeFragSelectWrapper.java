package org.apache.xpath.objects;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;

import org.apache.xml.utils.XMLString;

import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.Expression;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;

/**
 * This class makes an select statement act like an result tree fragment...
 * at least in its returned values and comparisons. Actually, a
 * more accurate description might be that its "values", like those of an
 * RTF, spans all the selected content rather than being taken only from the
 * first matching node.
 * 
 * But this really  _isn't_ a result tree fragment. The rtf() method is 
 * hardwired to throw an exception, and the type() method returns STRING.
 * 
 * (Some of the logic is swiped from XRTreeFrag, but we no longer
 * inherit from that class. Doing so was a bit of a kluge, and became
 * impractical when XRTreeFrag was rewritten as an XNodeSet derivitive.)
 * 
 * %REVIEW% Should we rename this class?
 */
public class XRTreeFragSelectWrapper extends XObject implements Cloneable
{
  XObject m_selected;
  boolean m_allowRelease;
  Expression m_expr;

  public XRTreeFragSelectWrapper(Expression expr)
  {
    m_expr=expr;
  }
  
  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(VariableComposeState vcs)
  {
    m_expr.fixupVariables(vcs);
  }
  
  /**
   * For support of literal objects in xpaths.
   *
   * @param xctxt The XPath execution context.
   *
   * @return This object.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    try
    {
      m_selected = m_expr.execute(xctxt);
      m_selected.allowDetachToRelease(m_allowRelease);
      XRTreeFragSelectWrapper xrtf = (XRTreeFragSelectWrapper)this.clone();
      return xrtf;
    }
    catch(CloneNotSupportedException cnse)
    {
      throw new javax.xml.transform.TransformerException(cnse);
    }
    
  }
  
  /**
   * Detaches the <code>DTMIterator</code> from the set which it iterated
   * over, releasing any computational resources and placing the iterator
   * in the INVALID state. After <code>detach</code> has been invoked,
   * calls to <code>nextNode</code> or <code>previousNode</code> will
   * raise a runtime exception.
   * 
   * In general, detach should only be called once on the object.
   */
  public void detach()
  {
    if(m_allowRelease)
    {
      m_selected.detach();
      m_selected = null;
      m_expr=null;
    }
    
    super.detach();
  }
   
  /**
   * Cast result object to an XMLString.
   *
   * @return The document fragment node data or the empty string. 
   */
  public XMLString xstr()
  {
    return m_selected.xstr();
  }

  /**
   * Cast result object to a string.
   *
   * @return The document fragment node data or the empty string. 
   */
  public String str()
  {
    return m_selected.str();
  }
  
  /**
   * Tell what kind of class this is.
   *
   * @return type CLASS_RTREEFRAG 
   */
  public int getType()
  {
    return CLASS_STRING;
  }

  /**
   * Cast result object to a result tree fragment.
   * Probably not needed since we no longer claim to inherit XRTreeFrag...
   *
   * @throws method-not-supported runtime expection.
   */
  public int rtf()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER, null)); //"rtf() not supported by XRTreeFragSelectWrapper!");
  }

  /**
   * Cast result object to a DTMIterator.
   *
   * @return The document fragment as a DTMIterator
   */
  public DTMIterator asNodeIterator()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER, null)); //"asNodeIterator() not supported by XRTreeFragSelectWrapper!");
  }
  
  public Object object()
  {
  	/* Would this be preferable?
  	if(m_selected!=null)
  		return m_selected.object();
  	else
  	*/
  		return m_expr;
  }
  

  //-------------------------------------------------------------
  // Code swiped from XRTreeFrag since we no longer inherit therefrom

 /**
   * Tell if two objects are functionally equal.
   * Use logic from XRTreeFrag, not standard XObject.equals()
   *
   * @param obj2 Object to compare this to
   *
   * @return True if the two objects are equal
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean equals(XObject obj2)
  {

    try
    {
      if (XObject.CLASS_NODESET == obj2.getType())
      {
  
        // In order to handle the 'all' semantics of 
        // nodeset comparisons, we always call the 
        // nodeset function.
        return ((XNodeSet)obj2).equalsExistential(this);
      }
      else if (XObject.CLASS_BOOLEAN == obj2.getType())
      {
        return bool() == obj2.bool();
      }
      else if (XObject.CLASS_NUMBER == obj2.getType())
      {
        return num() == obj2.num();
      }
      else if (XObject.CLASS_NODESET == obj2.getType())
      {
        return xstr().equals(obj2.xstr());
      }
      else if (XObject.CLASS_STRING == obj2.getType())
      {
        return xstr().equals(obj2.xstr());
      }
      else if (XObject.CLASS_RTREEFRAG == obj2.getType())
      {
  
        // Probably not so good.  Think about this.
        return xstr().equals(obj2.xstr());
      }
      else
      {
        return super.equals(obj2);
      }
    }
    catch(javax.xml.transform.TransformerException te)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(te);
    }
  }

  /**
   * Cast result object to a number.
   * Note that this is different from XNodeSet's definition; we convert
   * the entire string content, not just item(0).
   *
   * @return The result tree fragment as a number or NaN
   */
  public double num()
  {
    XMLString s = xstr();

    return s.toDouble();
  }
  
  /**
   * Cast result object to a boolean.  This always returns true for an
   * XRTreeFragSelectWrapper. (I'm not entirely sure why it should, but some
   * of the regression tests fail if it doesn't.)
   *
   * @return true
   */
  public boolean bool()
  {
    return true;
  }
    

}