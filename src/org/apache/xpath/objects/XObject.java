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

import java.io.Serializable;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.XType;
import org.apache.xml.utils.DateTimeObj;
import org.apache.xml.utils.Duration;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.VariableComposeState;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathException;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath object, and is capable of
 * converting the object to various types, such as a string.
 * This class acts as the base class to other XPath type objects,
 * such as XString, and provides polymorphic casting capabilities.
 * 
 * Caveat: Default method implementations provided herein may be
 * inefficient, since they have to retrieve the "contained object"
 * which may not exist until demanded.
 */
public abstract class XObject extends Expression implements Serializable, Cloneable
{
  /**
   * Create an undifferentiated XObject.
   * Note: Abstract methods can't be constructed...
   * ... and constructors don't inherit, even as abstracts.
   */
  /*
  protected XObject(){}
  */

  /**
   * Create an XObject.
   * Note: Abstract methods can't be constructed...
   * ... and constructors don't inherit, even as abstracts.
   *
   * @param obj Can be any object, should be a specific type
   * for derived classes, or null.
   */
  /*
  protected XObject(Object obj)
  {
    //m_obj = obj;
  }
  */

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
    return this;
  }

  /**
   * Specify if it's OK for detach to release the iterator for reuse.
   * This function should be called with a value of false for objects that are 
   * stored in variables.
   * Calling this with a value of false on a XNodeSet will cause the nodeset 
   * to be cached.
   *
   * @param allowRelease true if it is OK for detach to release this iterator
   * for pooling.
   */
  public void allowDetachToRelease(boolean allowRelease){}

  /**
   * Detaches the <code>DTMIterator</code> from the set which it iterated
   * over, releasing any computational resources and placing the iterator
   * in the INVALID state. After <code>detach</code> has been invoked,
   * calls to <code>nextNode</code> or <code>previousNode</code> will
   * raise a runtime exception.
   */
  public void detach(){}

  /**
   * Forces the object to release it's resources.  This is more harsh than
   * detach().
   */
  public void destruct()
  {

    if (null != object())
    {
      allowDetachToRelease(true);
      detach();
      
      // %BUG% When m_obj was part of this class, we were
      // nulling it out at this point. Do we need to move
      // this logic down into the subclasses?
	  //	  m_obj = null;
    }
  }
  
  /**
   * Reset for fresh reuse.
   */
  public void reset()
  {
  }

  /**
   * Directly call the
   * characters method on the passed ContentHandler for the
   * string-value. Multiple calls to the
   * ContentHandler's characters methods may well occur for a single call to
   * this method.
   *
   * @param ch A non-null reference to a ContentHandler.
   *
   * @throws org.xml.sax.SAXException
   */
  public void dispatchCharactersEvents(org.xml.sax.ContentHandler ch)
          throws org.xml.sax.SAXException
  {
    xstr().dispatchCharactersEvents(ch);
  }

  /**
   * Create the right XObject based on the type of the object passed.  This 
   * function can not make an XObject that exposes DOM Nodes, NodeLists, and 
   * NodeIterators to the XSLT stylesheet as node-sets.
   *
   * @param val The java object which this object will wrap.
   *
   * @return the right XObject based on the type of the object passed.
   */
  static public XObject create(Object val)
  {
    return XObjectFactory.create(val);
  }
  
  /**
   * Create the right XObject based on the type of the object passed.
   * This function <emph>can</emph> make an XObject that exposes DOM Nodes, NodeLists, and 
   * NodeIterators to the XSLT stylesheet as node-sets.
   *
   * @param val The java object which this object will wrap.
   * @param xctxt The XPath context.
   *
   * @return the right XObject based on the type of the object passed.
   */
  static public XObject create(Object val, XPathContext xctxt)
  {
    return XObjectFactory.create(val, xctxt);
  }

  /** Constant for NULL object type */
  public static final int CLASS_NULL = -1;

  /** Constant for UNKNOWN object type */
  public static final int CLASS_UNKNOWN = XType.ANYTYPE;

  /** Constant for BOOLEAN  object type */
  public static final int CLASS_BOOLEAN = XType.BOOLEAN;

  /** Constant for NUMBER object type */
  public static final int CLASS_NUMBER = XType.DOUBLE;

  /** Constant for STRING object type */
  public static final int CLASS_STRING = XType.STRING;

  /** Constant for NODESET object type */
  public static final int CLASS_NODESET = XType.NODE;

  /** Constant for RESULT TREE FRAGMENT object type */
  public static final int CLASS_RTREEFRAG = XType.RTREEFRAG;
	
	/** Constant for DATE object type */
  public static final int CLASS_DATE = XType.DATE;
  
  /** Constant for DATETIME object type */
  public static final int CLASS_DATETIME = XType.DATETIME;
  
  /** Constant for DURATION object type */
  public static final int CLASS_DURATION = XType.DURATION;
  
  /** Constant for DURATION object type */
  public static final int CLASS_YMDURATION = XType.YEARMONTHDURATION;


/** Constant for DURATION object type */
  public static final int CLASS_DTDURATION = XType.DAYTIMEDURATION;

  
  /** Constant for TIME object type */
  public static final int CLASS_TIME = XType.TIME;
  
  /** Constant for GDAY object type */
  public static final int CLASS_GDAY = XType.GDAY;
  
  /** Constant for GMONTH object type */
  public static final int CLASS_GMONTH = XType.GMONTH;
  
  /** Constant for GMONTHDAY object type */
  public static final int CLASS_GMONTHDAY = XType.GMONTHDAY;
  
  /** Constant for GYEAR object type */
  public static final int CLASS_GYEAR = XType.GYEAR;
  
  /** Constant for GYEARMONTH object type */
  public static final int CLASS_GYEARMONTH = XType.GYEARMONTH;

  /** Represents an unresolved variable type as an integer. */
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
   * Get the type of the value that this object represents.
   * For most objects this is the same as getType(), but for 
   * nodes it is the type of the inner value.
   *
   * @return one of the 19 simple types from XType.
   */
  public int getValueType()
  {
    return getType();
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
   * Get result object as a integer.
   *
   * @return At this level, the num() value cast to an integer.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public int integer() throws javax.xml.transform.TransformerException
  {

    return (int)num();
  }
  
  /**
   * Get result object as a integer.
   *
   * @return At this level, the num() value cast to an integer.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public float floatVal() throws javax.xml.transform.TransformerException
  {

    return (float)num();
  }


  
  /**
   * Cast result object to a number, but allow side effects, such as the 
   * incrementing of an iterator.
   *
   * @return numeric value of the string conversion from the 
   * next node in the NodeSetDTM, or NAN if no node was found
   */
  public double numWithSideEffects()  throws javax.xml.transform.TransformerException
  {
    return num();
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
   * Cast result object to a boolean, but allow side effects, such as the 
   * incrementing of an iterator.
   *
   * @return True if there is a next node in the nodeset
   */
  public boolean boolWithSideEffects() throws javax.xml.transform.TransformerException
  {
    return bool();
  }


  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public XMLString xstr()
  {
    return XMLStringFactoryImpl.getFactory().newstr(str());
  }

  /**
   * Cast result object to a string.
   *
   * @return The object as a string
   */
  public String str()
  {
  	Object obj=object();
    return (obj != null) ? obj.toString() : "";
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
  public int rtf(XPathContext support)
  {

    int result = rtf();

    if (DTM.NULL == result)
    {
      DTM frag = support.createDocumentFragment();

      // %OPT%
      frag.appendTextChild(str());

      result = frag.getDocument();
    }

    return result;
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
    DocumentFragment docFrag = null;
    int result = rtf();

    if (DTM.NULL == result)
    {
      DTM frag = support.createDocumentFragment();

      // %OPT%
      frag.appendTextChild(str());

      docFrag = (DocumentFragment)frag.getNode(frag.getDocument());
    }
    else
    {
      DTM frag = support.getDTM(result);
      docFrag = (DocumentFragment)frag.getNode(frag.getDocument());
    }

    return docFrag;
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
   * For functions to override.
   *
   * @return null
   */
  public int rtf()
  {
    return DTM.NULL;
  }

  /**
   * Return a java object that's closest to the representation
   * that should be handed to an extension.
   *
   * @return The object that this class wraps
   */
  abstract public Object object();
  
  /**
   * Return the sequence representing this object.
   * @return XSequence
   */
  public XSequence xseq()
  {
  	Object obj=object();
    try
    {
      // This feels a bit heavy weight, but most of the 
      // derived classes will override this method.
      if(null == obj || getType() == CLASS_NULL)
        return XSequence.EMPTY;
      else if(obj instanceof XSequence)
        return (XSequence)((XSequence)obj).clone(); // Clone??
      else
        return new XSequenceSingleton(this);
    }
    catch (CloneNotSupportedException e)
    {
      return (XSequence)obj;
    }
  }

  /**
   * Cast result object to a nodelist. Always issues an error.
   *
   * @return null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DTMIterator iter() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NODELIST,
          new Object[]{ getTypeString() });  //"Can not convert "+getTypeString()+" to a NodeList!");

    return null;
  }
  
  /**
   * Get a fresh copy of the object.  For use with variables.
   *
   * @return This object, unless overridden by subclass.
   */
  public XObject getFresh()
  {
    return this;
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
   * @return null
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeList nodelist() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_NODELIST,
          new Object[]{ getTypeString() });  //"Can not convert "+getTypeString()+" to a NodeList!");

    return null;
  }
  
  /**
   * Get the node representation of this object, or return DTM.NULL if 
   * there is no node representation.
   * 
   * @return int DTM.NULL if there is no node representation, or the 
   * node handle.
   */
  public int getNodeHandle()
  {
    return DTM.NULL;
  }


  /**
   * Cast result object to a nodelist. Always issues an error.
   *
   * @return The object as a NodeSetDTM.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public NodeSetDTM mutableNodeset()
          throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_MUTABLENODELIST,
          new Object[]{ getTypeString() });  //"Can not convert "+getTypeString()+" to a NodeSetDTM!");

    return (NodeSetDTM) object();
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
      result = iter();
      break;
    case CLASS_BOOLEAN :
      result = new Boolean(bool());
      break;
    case CLASS_UNKNOWN :
      result = object();
      break;

    // %TBD%  What to do here?
    //    case CLASS_RTREEFRAG :
    //      result = rtree(support);
    //      break;
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
  public boolean lessThan(XObject obj2)
          throws javax.xml.transform.TransformerException
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
  public boolean lessThanOrEqual(XObject obj2)
          throws javax.xml.transform.TransformerException
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
  public boolean greaterThan(XObject obj2)
          throws javax.xml.transform.TransformerException
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
  public boolean equalsExistential(XObject obj2)
  {
    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.
    if (obj2.isNodesetExpr())
    {
      // Note: obj2 _must_ overload this function, or we
      // dive into infinite recursion if "this" is also a
      // nodeset.
      return obj2.equalsExistential(this);
    }

    return equals(obj2);
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
  public boolean equals(XObject obj2)
  {
    Object obj = object();
    if (null != obj)
      return obj.equals(obj2.object());
    else
    {
      return obj2.object() == null;
    }
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
  public boolean notEquals(XObject obj2)
          throws javax.xml.transform.TransformerException
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
  protected void error(int msg)
          throws javax.xml.transform.TransformerException
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
  protected void error(int msg, Object[] args)
          throws javax.xml.transform.TransformerException
  {

    String fmsg = XSLMessages.createXPATHMessage(msg, args);

    // boolean shouldThrow = support.problem(m_support.XPATHPROCESSOR, 
    //                                      m_support.ERROR,
    //                                      null, 
    //                                      null, fmsg, 0, 0);
    // if(shouldThrow)
    {
      throw new XPathException(fmsg, this);
    }
  }
  
  
  /**
   * XObjects should not normally need to fix up variables.
   */
  public void fixupVariables(VariableComposeState vcs)
  {
    // no-op
  }


  /**
   * Cast result object to a string.
   *
   *
   * NEEDSDOC @param fsb
   * @return The string this wraps or the empty string if null
   */
  public void appendToFsb(org.apache.xml.utils.FastStringBuffer fsb)
  {
    fsb.append(str());
  }
  
  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	assertion(false, "callVisitors should not be called for this object!!!");
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!isSameClass(expr))
  		return false;
  		
  	// If equals at the expression level calls deepEquals, I think we're 
  	// still safe from infinite recursion since this object overrides 
  	// equals.  I hope.
  	if(!this.equalsExistential((XObject)expr))
  		return false;
  		
  	return true;
  }
  
  /**
   * Tell if this node should have it's PathExpr ancestory reduced.
   */
  public boolean isPathExprReduced()
  {
  	return true;
  }
  
  /**
   * Tell if this item is a proper sequence, or should be treated as a 
   * value.  This is needed for nested sequences (we implement sequences 
   * as nested, even though it appears from the caller's perspective and 
   * from XPath's perspective that sequences are not nested).  XObjects 
   * that return true from this method must implement XSequence, though not 
   * all XObjects that implement XSequence must return true from this 
   * method.
   * @return true if this sequence can be treated as a value or node, falst 
   *          if this is a proper sequence.
   */
  public boolean isSequenceProper()
  {
    return false;
  }

  /**
   * @see org.apache.xml.dtm.XSequence#isSingletonOrEmpty()
   */
  public boolean isSingletonOrEmpty()
  {
    return true;
  }

  /**
   * Cast result object to a DURATION.
   *
   * @return Duration
   *
   * @throws javax.xml.transform.TransformerException
   */
  public Duration duration() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
          new Object[]{ getTypeString(), "DURATION" });  //"Can not convert "+getTypeString()+" to a number");

    return null; // To shut up compiler
  }
  
  /**
   * Cast result object to a DAYTIMEDURATION.
   *
   * @return Duration
   *
   * @throws javax.xml.transform.TransformerException
   */
  public Duration daytimeDuration() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
          new Object[]{ getTypeString(), "DAYTIMEDURATION" });  //"Can not convert "+getTypeString()+" to a number");

    return null; // To shut up compiler
  }
  
  /**
   * Cast result object to a YEARMONTHDURATION.
   *
   * @return Duration
   *
   * @throws javax.xml.transform.TransformerException
   */
  public Duration yearmonthDuration() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
          new Object[]{ getTypeString(), "YEARMONTHDURATION" });  //"Can not convert "+getTypeString()+" to a number");

    return null; // To shut up compiler
  }

  /**
   * Cast result object to a DATE.
   *
   * @return DateTimeObj
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DateTimeObj date() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
          new Object[]{ getTypeString(), "DATE" });  //"Can not convert "+getTypeString()+" to a number");

    return null; // To shut up compiler
  }
  
  /**
   * Cast result object to a DATETIME.
   *
   * @return DateTimeObj
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DateTimeObj datetime() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
          new Object[]{ getTypeString(), "DATETIME" });  //"Can not convert "+getTypeString()+" to a number");

    return null; // To shut up compiler
  }

  /**
   * Cast result object to a TIME.
   *
   * @return DateTimeObj
   *
   * @throws javax.xml.transform.TransformerException
   */
  public DateTimeObj time() throws javax.xml.transform.TransformerException
  {

    error(XPATHErrorResources.ER_CANT_CONVERT_TO_TYPE,
          new Object[]{ getTypeString(), "TIME" });  //"Can not convert "+getTypeString()+" to a number");

    return null; // To shut up compiler
  }

}
