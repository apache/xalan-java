/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xpath.objects;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

import java.io.Serializable;

import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.XPathContext;
import org.apache.xpath.NodeSet;
import org.apache.xpath.XPathException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.Expression;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath object, and is capable of
 * converting the object to various types, such as a string.
 * This class acts as the base class to other XPath type objects,
 * such as XString, and provides polymorphic casting capabilities.
 */
public class XObject extends Expression implements Serializable
{

  /** The java object which this object wraps.
   *  @serial  */
  protected Object m_obj;  // This may be NULL!!!

  /**
   * Create an XObject.
   */
  public XObject(){}

  /**
   * Create an XObject.
   *
   * @param obj Can be any object, should be a specific type 
   * for derived classes, or null.
   */
  public XObject(Object obj)
  {
    m_obj = obj;
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
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {
    return this;
  }

  /**
   * Create the right XObject based on the type of the object passed.
   *
   * @param val The java object which this object will wrap.
   *
   * @return the right XObject based on the type of the object passed.
   */
  static public XObject create(Object val)
  {

    XObject result;

    if (val instanceof XObject)
    {
      result = (XObject) val;
    }
    else if (val instanceof String)
    {
      result = new XString((String) val);
    }
    else if (val instanceof Boolean)
    {
      result = ((Boolean) val).booleanValue()
               ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    else if (val instanceof Double)
    {
      result = new XNumber(((Double) val).doubleValue());
    }
    else if (val instanceof DocumentFragment)
    {
      result = new XRTreeFrag((DocumentFragment) val);
    }
    else if (val instanceof Node)
    {
      result = new XNodeSet((Node) val);
    }
    else if (val instanceof NodeIterator)
    {
      result = new XNodeSet((NodeIterator) val);
    }
    else
    {
      result = new XObject(val);
    }

    return result;
  }

  /** Constant for NULL object type          */
  public static final int CLASS_NULL = -1;

  /** Constant for UNKNOWN object type         */
  public static final int CLASS_UNKNOWN = 0;

  /** Constant for BOOLEAN  object type        */
  public static final int CLASS_BOOLEAN = 1;

  /** Constant for NUMBER object type         */
  public static final int CLASS_NUMBER = 2;

  /** Constant for STRING object type         */
  public static final int CLASS_STRING = 3;

  /** Constant for NODESET object type         */
  public static final int CLASS_NODESET = 4;

  /** Constant for RESULT TREE FRAGMENT object type         */
  public static final int CLASS_RTREEFRAG = 5;

  /** Represents an unresolved variable type as an integer.          */
  public static final int CLASS_UNRESOLVEDVARIABLE = 600;

  /**
   * Tell what kind of class this is.
   *
   * @return CLASS_UNKNOWN
   */
  public int getType()
  {
    return CLASS_UNKNOWN;
  }

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#UNKNOWN" + object class name
   */
  public String getTypeString()
  {
    return "#UNKNOWN (" + object().getClass().getName() + ")";
  }

  /**
   * Cast result object to a number. Always issues an error.
   *
   * @return 0.0
   *
   * @throws javax.xml.transform.TransformerException
   */
  public double num() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NUMBER,
          new Object[]{ getTypeString() });  //"Can not convert "+getTypeString()+" to a number");

    return 0.0;
  }

  /**
   * Cast result object to a boolean. Always issues an error.
   *
   * @return false
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean bool() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NUMBER,
          new Object[]{ getTypeString() });  //"Can not convert "+getTypeString()+" to a number");

    return false;
  }

  /**
   * Cast result object to a string.
   *
   * @return The object as a string
   */
  public String str()
  {
    return m_obj.toString();
  }

  /**
   * Return the string representation of the object 
   *
   *
   * @return the string representation of the object
   */
  public String toString()
  {
    return str();
  }

  /**
   * Cast result object to a result tree fragment.
   *
   * @param support XPath context to use for the conversion
   *
   * @return the objec as a result tree fragment.
   */
  public DocumentFragment rtree(XPathContext support)
  {

    DocumentFragment result = rtree();

    if (null == result)
    {
      result =
        support.getDOMHelper().getDOMFactory().createDocumentFragment();

      Text textNode =
        support.getDOMHelper().getDOMFactory().createTextNode(str());

      result.appendChild(textNode);
    }

    return result;
  }

  /**
   * For functions to override.
   *
   * @return null
   */
  public DocumentFragment rtree()
  {
    return null;
  }

  /**
   * Return a java object that's closest to the representation
   * that should be handed to an extension.
   *
   * @return The object that this class wraps
   */
  public Object object()
  {
    return m_obj;
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

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NODELIST,
          new Object[]{ getTypeString() });  //"Can not convert "+getTypeString()+" to a NodeList!");

    return null;
  }

  /**
   * Cast result object to a nodelist. Always issues an error.
   *
   * @return The object as a NodeSet.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeSet mutableNodeset() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_MUTABLENODELIST,
          new Object[]{ getTypeString() });  //"Can not convert "+getTypeString()+" to a NodeSet!");

    return (NodeSet) m_obj;
  }

  /**
   * Cast object to type t.
   *
   * @param t Type of object to cast this to
   * @param support XPath context to use for the conversion 
   *
   * @return This object as the given type t
   *
   * @throws javax.xml.transform.TransformerException
   */
  public Object castToType(int t, XPathContext support)
          throws javax.xml.transform.TransformerException
  {

    Object result;

    switch (t)
    {
    case CLASS_STRING :
      result = str();
      break;
    case CLASS_NUMBER :
      result = new Double(num());
      break;
    case CLASS_NODESET :
      result = nodeset();
      break;
    case CLASS_BOOLEAN :
      result = new Boolean(bool());
      break;
    case CLASS_UNKNOWN :
      result = m_obj;
      break;
    case CLASS_RTREEFRAG :
      result = rtree(support);
      break;
    default :
      error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
            new Object[]{ getTypeString(),
                          Integer.toString(t) });  //"Can not convert "+getTypeString()+" to a type#"+t);

      result = null;
    }

    return result;
  }

  /**
   * Tell if one object is less than the other.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if this object is less than the given object 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean lessThan(XObject obj2) throws javax.xml.transform.TransformerException
  {

    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.  Because the arguments 
    // are backwards, we call the opposite comparison
    // function.
    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.greaterThan(this);

    return this.num() < obj2.num();
  }

  /**
   * Tell if one object is less than or equal to the other.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if this object is less than or equal to the given object 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean lessThanOrEqual(XObject obj2) throws javax.xml.transform.TransformerException
  {

    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.  Because the arguments 
    // are backwards, we call the opposite comparison
    // function.
    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.greaterThanOrEqual(this);

    return this.num() <= obj2.num();
  }

  /**
   * Tell if one object is greater than the other.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if this object is greater than the given object 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean greaterThan(XObject obj2) throws javax.xml.transform.TransformerException
  {

    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.  Because the arguments 
    // are backwards, we call the opposite comparison
    // function.
    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.lessThan(this);

    return this.num() > obj2.num();
  }

  /**
   * Tell if one object is greater than or equal to the other.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if this object is greater than or equal to the given object 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean greaterThanOrEqual(XObject obj2)
          throws javax.xml.transform.TransformerException
  {

    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.  Because the arguments 
    // are backwards, we call the opposite comparison
    // function.
    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.lessThanOrEqual(this);

    return this.num() >= obj2.num();
  }

  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if this object is equal to the given object 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean equals(XObject obj2) throws javax.xml.transform.TransformerException
  {

    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.
    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.equals(this);

    return m_obj.equals(obj2.m_obj);
  }

  /**
   * Tell if two objects are functionally not equal.
   *
   * @param obj2 Object to compare this to
   *
   * @return True if this object is not equal to the given object 
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean notEquals(XObject obj2) throws javax.xml.transform.TransformerException
  {

    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.
    if (obj2.getType() == XObject.CLASS_NODESET)
      return obj2.notEquals(this);

    return !equals(obj2);
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Error message to issue
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void error(int msg) throws javax.xml.transform.TransformerException
  {
    error(msg, null);
  }

  /**
   * Tell the user of an error, and probably throw an
   * exception.
   *
   * @param msg Error message to issue
   * @param args Arguments to use in the message 
   *
   * @throws javax.xml.transform.TransformerException
   */
  protected void error(int msg, Object[] args) throws javax.xml.transform.TransformerException
  {

    String fmsg = XSLMessages.createXPATHMessage(msg, args);

    // boolean shouldThrow = support.problem(m_support.XPATHPROCESSOR, 
    //                                      m_support.ERROR,
    //                                      null, 
    //                                      null, fmsg, 0, 0);
    // if(shouldThrow)
    {
      throw new XPathException(fmsg);
    }
  }
}
