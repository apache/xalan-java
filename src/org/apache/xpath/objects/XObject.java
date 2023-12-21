/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xpath.objects;

import java.io.Serializable;

import org.apache.xalan.res.XSLMessages;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathException;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.res.XPATHErrorResources;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInt;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSLong;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSTime;

/**
 * This class represents an XPath object, and is capable of
 * converting the object to various types, such as a string.
 * This class acts as the base class to other XPath type objects,
 * such as XString, and provides polymorphic casting capabilities.
 * 
 * @xsl.usage general
 */
public class XObject extends Expression implements Serializable, Cloneable
{
  
  static final long serialVersionUID = -821887098985662951L;

  /**
   * The java object which this object wraps.
   *  @serial  
   */
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
    setObject(obj);
  }
  
  protected void setObject(Object obj) {
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

    if (null != m_obj)
    {
      allowDetachToRelease(true);
      detach();

      setObject(null);
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
  public static final int CLASS_UNKNOWN = 0;

  /** Constant for BOOLEAN  object type */
  public static final int CLASS_BOOLEAN = 1;

  /** Constant for NUMBER object type */
  public static final int CLASS_NUMBER = 2;

  /** Constant for STRING object type */
  public static final int CLASS_STRING = 3;

  /** Constant for NODESET object type */
  public static final int CLASS_NODESET = 4;

  /** Constant for RESULT TREE FRAGMENT object type */
  public static final int CLASS_RTREEFRAG = 5;
  
  /** Constant for XPath 3.1 sequence object type */
  public static final int CLASS_RESULT_SEQUENCE = 6;
  
  /** Constant for XPath 3.1 function item object type */
  public static final int CLASS_FUNCTION_ITEM = 7;
  
  /** Constant for XPath 3.1 xs:untypedAtomic object type */
  public static final int CLASS_XS_UNTYPED_ATOMIC = 8;
  
  /** Constant for XPath 3.1 xs:date object type */
  public static final int CLASS_XS_DATE = 9;
  
  /** Constant for XPath 3.1 xs:dateTime object type */
  public static final int CLASS_XS_DATETIME = 10;
  
  /** Constant for XPath 3.1 xs:time object type */
  public static final int CLASS_XS_TIME = 11;
  
  /** Constant for XPath 3.1 xs:duration object type */
  public static final int CLASS_XS_DURATION = 12;
  
  /** Constant for XPath 3.1 xs:dayTimeDuration object type */
  public static final int CLASS_XS_DAYTIME_DURATION = 13;
  
  /** Constant for XPath 3.1 xs:yearMonthDuration object type */
  public static final int CLASS_XS_YEARMONTH_DURATION = 14;
  
  /** Constant for XPath 3.1 xs:decimal object type */
  public static final int CLASS_XS_DECIMAL = 15;
  
  /** Constant for XPath 3.1 xs:integer object type */
  public static final int CLASS_XS_INTEGER = 16;
  
  /** Constant for XPath 3.1 xs:long object type */
  public static final int CLASS_XS_LONG = 17;
  
  /** Constant for XPath 3.1 xs:int object type */
  public static final int CLASS_XS_INT = 18;
  
  /** Constant for XPath 3.1 xs:double object type */
  public static final int CLASS_XS_DOUBLE = 19;
  
  /** Constant for XPath 3.1 xs:float object type */
  public static final int CLASS_XS_FLOAT = 20;
  
  /** Constant for XPath 3.1 xs:normalizedString object type */
  public static final int CLASS_XS_NORMALIZED_STRING = 21;
  
  /** Constant for XPath 3.1 xs:token object type */
  public static final int CLASS_XS_TOKEN = 22;

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
    return (m_obj != null) ? m_obj.toString() : "";
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

    return (NodeSetDTM) m_obj;
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
      result = bool() ? Boolean.TRUE : Boolean.FALSE;
      break;
    case CLASS_UNKNOWN :
      result = m_obj;
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
   * Tell if one object is less than an another one, using the rules 
   * of value comparison operator "lt".
   *
   * @param obj2                Object to compare this to
   * @param expressionOwner     this object is used, for error reporting
   * @param collationUri        collation uri, that needs to be used for string
   *                            comparison.
   * @param isLtTest            is this method called, to check "lt" or "ge"
   *                            operation. true, if that's "lt" check and false
   *                            otherwise. 
   *
   * @return True if this object is less than the given object
   *
   * @throws javax.xml.transform.TransformerException             
   */
  public boolean vcLessThan(XObject obj2, ExpressionNode expressionOwner, 
                                                               String collationUri, boolean isLtTest) 
                                                                                throws javax.xml.transform.TransformerException {       
       
       XPathContext xctxt = new XPathContext();
      
       if ((this instanceof XSDecimal) && (obj2 instanceof XSDecimal)) {
          return ((XSDecimal)this).lt((XSDecimal)obj2);        
       }
       else if ((this instanceof XSFloat) && (obj2 instanceof XSFloat)) {
          return ((XSFloat)this).lt((XSFloat)obj2);        
       }
       else if ((this instanceof XSDouble) && (obj2 instanceof XSDouble)) {
          return ((XSDouble)this).lt((XSDouble)obj2);        
       }
       else if ((this instanceof XSBoolean) && (obj2 instanceof XSBoolean)) {
          return ((XSBoolean)this).lt((XSBoolean)obj2);    
       }
       else if ((this instanceof XSInteger) && (obj2 instanceof XSInteger)) {
          return ((XSInteger)this).lt((XSInteger)obj2);    
       }
       else if ((this instanceof XSLong) && (obj2 instanceof XSLong)) {
          return ((XSLong)this).lt((XSLong)obj2);    
       }
       else if ((this instanceof XSInt) && (obj2 instanceof XSInt)) {
          return ((XSInt)this).lt((XSInt)obj2);    
       }
       else if ((this instanceof XSDate) && (obj2 instanceof XSDate)) {
          return ((XSDate)this).lt((XSDate)obj2);    
       }
       else if ((this instanceof XSDateTime) && (obj2 instanceof XSDateTime)) {
          return ((XSDateTime)this).lt((XSDateTime)obj2);    
       }
       else if ((this instanceof XSTime) && (obj2 instanceof XSTime)) {
          return ((XSTime)this).lt((XSTime)obj2);    
       }
       else if ((this instanceof XNumber) && (obj2 instanceof XNumber)) {
          return ((XNumber)this).num() < ((XNumber)obj2).num(); 
       }
       else if ((this instanceof XSNumericType) && (obj2 instanceof XNumber)) {
    	  String lStr = ((XSNumericType)this).stringValue();
    	  XSDouble lDouble = new XSDouble(lStr);
    	  
    	  double rdbl = ((XNumber)obj2).num();
    	  XSDouble rDouble = new XSDouble(rdbl);
    	  
    	  return lDouble.lt(rDouble);
       }
       else if ((this instanceof XNumber) && (obj2 instanceof XSNumericType)) {     	  
     	  double ldbl = ((XNumber)this).num();
     	  XSDouble lDouble = new XSDouble(ldbl);
     	  
     	  String rStr = ((XSNumericType)obj2).stringValue();
    	  XSDouble rDouble = new XSDouble(rStr);
     	  
     	  return lDouble.lt(rDouble);
       }
       else if ((this instanceof XSNumericType) && (obj2 instanceof XSNumericType)) {     	  
    	  String lStr = ((XSNumericType)this).stringValue();
      	  XSDouble lDouble = new XSDouble(lStr);
      	  
      	  String rStr = ((XSNumericType)obj2).stringValue();
     	  XSDouble rDouble = new XSDouble(rStr);
      	  
      	  return lDouble.lt(rDouble);
       }
       else if ((this instanceof XString) && (obj2 instanceof XString)) {
          String lStr = (((XString)this)).str();
          String rStr = (((XString)obj2)).str();                      
           
          XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
           
          int comparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, 
                                                                                            collationUri);
           
          return (comparisonResult < 0) ? true : false;
       }
       else if ((this instanceof XSString) && (obj2 instanceof XSString)) {
          String lStr = (((XSString)this)).stringValue();
          String rStr = (((XSString)obj2)).stringValue();                      
           
          XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
           
          int comparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, 
                                                                                            collationUri);
           
          return (comparisonResult < 0) ? true : false;
       }
       else if ((this instanceof XSString) && (obj2 instanceof XString)) {
          String lStr = (((XSString)this)).stringValue();
          String rStr = (((XString)obj2)).str();                      
           
          XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
           
          int comparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, 
                                                                                            collationUri);
           
          return (comparisonResult < 0) ? true : false;
       }
       else if ((this instanceof XString) && (obj2 instanceof XSString)) {
          String lStr = (((XString)this)).str();
          String rStr = (((XSString)obj2)).stringValue();                      
           
          XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
           
          int comparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, 
                                                                                            collationUri);
           
          return (comparisonResult < 0) ? true : false;
       }
       
       boolean isOperandNodeSet1 = false;
       boolean isOperandNodeSet2 = false;
       
       if (this.getType() == XObject.CLASS_NODESET) {       
          isOperandNodeSet1 = true;
          if ((((XNodeSet)this).getLength() > 1)) {
              error(isLtTest ? XPATHErrorResources.ER_LT_OPERAND_CARDINALITY_ERROR : 
                                                              XPATHErrorResources.ER_GE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);
          }
       }
       
       if (obj2.getType() == XObject.CLASS_NODESET) {
          isOperandNodeSet2 = true; 
          if ((((XNodeSet)obj2).getLength() > 1)) {
              error(isLtTest ? XPATHErrorResources.ER_LT_OPERAND_CARDINALITY_ERROR : 
                                                              XPATHErrorResources.ER_GE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);    
          }
       }
       
       if (isOperandNodeSet1 || (this instanceof XNumber)) {
           return this.num() < obj2.num();    
       }    
       else if (isOperandNodeSet2 || (obj2 instanceof XNumber)) {
           return obj2.num() < this.num();    
       }
       
       return true;
  }
  
  /**
   * Tell if one object is greater than an another one, using the rules 
   * of value comparison operator "gt".
   *
   * @param obj2                Object to compare this to
   * @param expressionOwner     this object is used, for error reporting
   * @param collationUri        collation uri, that needs to be used for string
   *                            comparison. 
   * @param isGtTest            is this method called, to check "gt" or "le"
   *                            operation. true, if that's "gt" check and false
   *                            otherwise.
   *
   * @return True if this object is greater than the given object
   *
   * @throws javax.xml.transform.TransformerException            
   */
  public boolean vcGreaterThan(XObject obj2, ExpressionNode expressionOwner, 
                                                  String collationUri, boolean isGtTest) 
                                                                 throws javax.xml.transform.TransformerException {
       
       XPathContext xctxt = new XPathContext();
      
       if ((this instanceof XSDecimal) && (obj2 instanceof XSDecimal)) {
          return ((XSDecimal)this).gt((XSDecimal)obj2);        
       }
       else if ((this instanceof XSFloat) && (obj2 instanceof XSFloat)) {
          return ((XSFloat)this).gt((XSFloat)obj2);        
       }
       else if ((this instanceof XSDouble) && (obj2 instanceof XSDouble)) {
          return ((XSDouble)this).gt((XSDouble)obj2);        
       }
       else if ((this instanceof XSBoolean) && (obj2 instanceof XSBoolean)) {
          return ((XSBoolean)this).gt((XSBoolean)obj2);    
       }
       else if ((this instanceof XSInteger) && (obj2 instanceof XSInteger)) {
          return ((XSInteger)this).gt((XSInteger)obj2);    
       }
       else if ((this instanceof XSLong) && (obj2 instanceof XSLong)) {
          return ((XSLong)this).gt((XSLong)obj2);    
       }
       else if ((this instanceof XSInt) && (obj2 instanceof XSInt)) {
          return ((XSInt)this).gt((XSInt)obj2);    
       }
       else if ((this instanceof XSDate) && (obj2 instanceof XSDate)) {
          return ((XSDate)this).gt((XSDate)obj2);    
       }
       else if ((this instanceof XSDateTime) && (obj2 instanceof XSDateTime)) {
          return ((XSDateTime)this).gt((XSDateTime)obj2);    
       }
       else if ((this instanceof XSTime) && (obj2 instanceof XSTime)) {
          return ((XSTime)this).gt((XSTime)obj2);    
       }
       else if ((this instanceof XSNumericType) && (obj2 instanceof XNumber)) {
     	  String lStr = ((XSNumericType)this).stringValue();
     	  XSDouble lDouble = new XSDouble(lStr);
     	  
     	  double rdbl = ((XNumber)obj2).num();
     	  XSDouble rDouble = new XSDouble(rdbl);
     	  
     	  return lDouble.gt(rDouble);
       }
       else if ((this instanceof XNumber) && (obj2 instanceof XSNumericType)) {     	  
      	  double ldbl = ((XNumber)this).num();
      	  XSDouble lDouble = new XSDouble(ldbl);
      	  
      	  String rStr = ((XSNumericType)obj2).stringValue();
     	  XSDouble rDouble = new XSDouble(rStr);
      	  
      	  return lDouble.gt(rDouble);
       }
       else if ((this instanceof XSNumericType) && (obj2 instanceof XSNumericType)) {     	  
     	  String lStr = ((XSNumericType)this).stringValue();
       	  XSDouble lDouble = new XSDouble(lStr);
       	  
       	  String rStr = ((XSNumericType)obj2).stringValue();
      	  XSDouble rDouble = new XSDouble(rStr);
       	  
       	  return lDouble.gt(rDouble);
       }
       else if ((this instanceof XString) && (obj2 instanceof XString)) {          
          String lStr = (((XString)this)).str();
          String rStr = (((XString)obj2)).str();
          
          XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
          
          int comparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, 
                                                                                            collationUri);
          
          return (comparisonResult > 0) ? true : false;
       }
       else if ((this instanceof XSString) && (obj2 instanceof XSString)) {
          String lStr = (((XSString)this)).stringValue();
          String rStr = (((XSString)obj2)).stringValue();                      
           
          XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
           
          int comparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, 
                                                                                            collationUri);
           
          return (comparisonResult > 0) ? true : false;
       }
       else if ((this instanceof XSString) && (obj2 instanceof XString)) {
          String lStr = (((XSString)this)).stringValue();
          String rStr = (((XString)obj2)).str();                      
           
          XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
           
          int comparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, 
                                                                                            collationUri);
           
          return (comparisonResult > 0) ? true : false;
       }
       else if ((this instanceof XString) && (obj2 instanceof XSString)) {
          String lStr = (((XString)this)).str();
          String rStr = (((XSString)obj2)).stringValue();                      
           
          XPathCollationSupport xpathCollationSupport = xctxt.getXPathCollationSupport();
           
          int comparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, 
                                                                                            collationUri);
           
          return (comparisonResult > 0) ? true : false;
       }
       
       boolean isOperandNodeSet1 = false;
       boolean isOperandNodeSet2 = false;
       
       if (this.getType() == XObject.CLASS_NODESET) {       
          isOperandNodeSet1 = true;
          if ((((XNodeSet)this).getLength() > 1)) {
              error(isGtTest ? XPATHErrorResources.ER_GT_OPERAND_CARDINALITY_ERROR : 
                                                              XPATHErrorResources.ER_LE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);
          }
       }
       
       if (obj2.getType() == XObject.CLASS_NODESET) {
          isOperandNodeSet2 = true; 
          if ((((XNodeSet)obj2).getLength() > 1)) {
              error(isGtTest ? XPATHErrorResources.ER_GT_OPERAND_CARDINALITY_ERROR : 
                                                              XPATHErrorResources.ER_LE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);    
          }
       }
       
       if (isOperandNodeSet1 || (this instanceof XNumber)) {
          return this.num() > obj2.num();    
       }    
       else if (isOperandNodeSet2 || (obj2 instanceof XNumber)) {
          return obj2.num() > this.num();    
       }
       
       return true;
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
  public boolean equals(XObject obj2)
  {
    if ((this instanceof XSDecimal) && (obj2 instanceof XSDecimal)) {
       return ((XSDecimal)this).equals((XSDecimal)obj2);        
    }
    else if ((this instanceof XSFloat) && (obj2 instanceof XSFloat)) {
       return ((XSFloat)this).equals((XSFloat)obj2);        
    }
    else if ((this instanceof XSDouble) && (obj2 instanceof XSDouble)) {
       return ((XSDouble)this).equals((XSDouble)obj2);        
    }
    else if ((this instanceof XSBoolean) && (obj2 instanceof XSBoolean)) {
       return ((XSBoolean)this).equals((XSBoolean)obj2);    
    }
    else if ((this instanceof XSInteger) && (obj2 instanceof XSInteger)) {
       return ((XSInteger)this).equals((XSInteger)obj2);    
    }
    else if ((this instanceof XSLong) && (obj2 instanceof XSLong)) {
       return ((XSLong)this).equals((XSLong)obj2);    
    }
    else if ((this instanceof XSInt) && (obj2 instanceof XSInt)) {
       return ((XSInt)this).equals((XSInt)obj2);    
    }
    else if ((this instanceof XSDate) && (obj2 instanceof XSDate)) {
       return ((XSDate)this).equals((XSDate)obj2);    
    }
    else if ((this instanceof XSDateTime) && (obj2 instanceof XSDateTime)) {
        return ((XSDateTime)this).equals((XSDateTime)obj2);    
    }
    else if ((this instanceof XSTime) && (obj2 instanceof XSTime)) {
        return ((XSTime)this).equals((XSTime)obj2);    
    }
    else if ((this instanceof XSInteger) && (obj2 instanceof XNumber)) {
       double lDouble = ((XSInteger)this).doubleValue();
       double rDouble = ((XNumber)obj2).num();
       return (lDouble == rDouble); 
    }
    else if ((this instanceof XNumber) && (obj2 instanceof XSInteger)) {
       double lDouble = ((XNumber)this).num();
       double rDouble = ((XSInteger)obj2).doubleValue();
       return (lDouble == rDouble);      
    }
        
    if (obj2.getType() == XObject.CLASS_NODESET) {
       return obj2.equals(this);
    }

    if (m_obj != null) {
       return m_obj.equals(obj2.m_obj);
    }
    else {
       return obj2.m_obj == null;
    }    
  }
  
  /**
   * Tell if two objects are functionally equal, using the rules 
   * of value comparison operator "eq".
   *
   * @param obj2                Object to compare this to
   * @param expressionOwner     this object is used, for error reporting 
   * @param isEqTest            is this method called, to check "eq" or "ne"
   *                            operation. true, if that's "eq" check and false
   *                            otherwise. 
   *
   * @return True if this object is equal to the given object
   *
   * @throws javax.xml.transform.TransformerException 
   */
  public boolean vcEquals(XObject obj2, ExpressionNode expressionOwner, boolean isEqTest) 
                                                               throws javax.xml.transform.TransformerException
  {
    if ((this instanceof XSDecimal) && (obj2 instanceof XSDecimal)) {
       return ((XSDecimal)this).equals((XSDecimal)obj2);        
    }
    else if ((this instanceof XSFloat) && (obj2 instanceof XSFloat)) {
       return ((XSFloat)this).equals((XSFloat)obj2);        
    }
    else if ((this instanceof XSDouble) && (obj2 instanceof XSDouble)) {
       return ((XSDouble)this).equals((XSDouble)obj2);        
    }
    else if ((this instanceof XSBoolean) && (obj2 instanceof XSBoolean)) {
       return ((XSBoolean)this).equals((XSBoolean)obj2);    
    }
    else if ((this instanceof XSInteger) && (obj2 instanceof XSInteger)) {
       return ((XSInteger)this).equals((XSInteger)obj2);    
    }
    else if ((this instanceof XSLong) && (obj2 instanceof XSLong)) {
       return ((XSLong)this).equals((XSLong)obj2);    
    }
    else if ((this instanceof XSInt) && (obj2 instanceof XSInt)) {
       return ((XSInt)this).equals((XSInt)obj2);    
    }
    else if ((this instanceof XSDate) && (obj2 instanceof XSDate)) {
       return ((XSDate)this).equals((XSDate)obj2);    
    }    
    else if ((this instanceof XSDateTime) && (obj2 instanceof XSDateTime)) {
       return ((XSDateTime)this).equals((XSDateTime)obj2);    
    }
    else if ((this instanceof XSTime) && (obj2 instanceof XSTime)) {
       return ((XSTime)this).equals((XSTime)obj2);    
    }
    else if ((this instanceof XSNumericType) && (obj2 instanceof XNumber)) {
  	   String lStr = ((XSNumericType)this).stringValue();
  	   XSDouble lDouble = new XSDouble(lStr);
  	  
  	   double rdbl = ((XNumber)obj2).num();
  	   XSDouble rDouble = new XSDouble(rdbl);
  	  
  	   return lDouble.equals(rDouble);
    }
    else if ((this instanceof XNumber) && (obj2 instanceof XSNumericType)) {     	  
   	   double ldbl = ((XNumber)this).num();
   	   XSDouble lDouble = new XSDouble(ldbl);
   	  
   	   String rStr = ((XSNumericType)obj2).stringValue();
  	   XSDouble rDouble = new XSDouble(rStr);
   	  
   	   return lDouble.equals(rDouble);
    }
    else if ((this instanceof XSNumericType) && (obj2 instanceof XSNumericType)) {     	  
  	   String lStr = ((XSNumericType)this).stringValue();
       XSDouble lDouble = new XSDouble(lStr);
    	  
       String rStr = ((XSNumericType)obj2).stringValue();
   	   XSDouble rDouble = new XSDouble(rStr);
    	  
       return lDouble.equals(rDouble);
    }
    else if ((this instanceof XSString) && (obj2 instanceof XSString)) {
       return ((XSString)this).equals((XSString)obj2);    
    }
    else if ((this instanceof XString) && (obj2 instanceof XString)) {
       String lStr = (((XString)this)).str();
       String rStr = (((XString)obj2)).str();
        
       return (new XSString(lStr)).equals(new XSString(rStr));
    }
    else if ((this instanceof XSString) && (obj2 instanceof XString)) {
       String lStr = ((((XSString)this))).stringValue();
       String rStr = (((XString)obj2)).str();
        
       return (new XSString(lStr)).equals(new XSString(rStr));
    }
    else if ((this instanceof XString) && (obj2 instanceof XSString)) {
       String lStr = (((XString)this)).str();
       String rStr = ((((XSString)obj2))).stringValue();
        
       return (new XSString(lStr)).equals(new XSString(rStr));
    }
    
    boolean isOperandNodeSet1 = false;
    boolean isOperandNodeSet2 = false;
    
    if (this.getType() == XObject.CLASS_NODESET) {       
       isOperandNodeSet1 = true;
       if ((((XNodeSet)this).getLength() > 1)) {
           error(isEqTest ? XPATHErrorResources.ER_EQ_OPERAND_CARDINALITY_ERROR : 
                                                           XPATHErrorResources.ER_NE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);    
       }
    }
    
    if (obj2.getType() == XObject.CLASS_NODESET) {
       isOperandNodeSet2 = true; 
       if ((((XNodeSet)obj2).getLength() > 1)) {
           error(isEqTest ? XPATHErrorResources.ER_EQ_OPERAND_CARDINALITY_ERROR : 
                                                           XPATHErrorResources.ER_NE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);    
       }
    }
    
    if (isOperandNodeSet1 || (this instanceof XNumber)) {
        return this.equals(obj2);    
    }    
    else if (isOperandNodeSet2 || (obj2 instanceof XNumber)) {
        return obj2.equals(this);    
    }

    if (m_obj != null) {
        return m_obj.equals(obj2.m_obj);
    }
    else {
        return obj2.m_obj == null;
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
  protected void error(String msg)
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
  protected void error(String msg, Object[] args)
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
  
  protected void error(String msg, Object[] args, ExpressionNode expressionOwner)
          throws javax.xml.transform.TransformerException
  {
      String fmsg = XSLMessages.createXPATHMessage(msg, args);
      
      throw new XPathException(fmsg, expressionOwner);
  }
  
  
  /**
   * XObjects should not normally need to fix up variables.
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
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
   * @see org.apache.xpath.XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
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
  	if(!this.equals((XObject)expr))
  		return false;
  		
  	return true;
  }

}
