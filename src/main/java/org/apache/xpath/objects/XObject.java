/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
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
package org.apache.xpath.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPathCollationSupport;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathException;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.compiler.Keywords;
import org.apache.xpath.functions.XSL3FunctionService;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.types.XSGDay;
import org.apache.xpath.types.XSGMonth;
import org.apache.xpath.types.XSGMonthDay;
import org.apache.xpath.types.XSGYear;
import org.apache.xpath.types.XSGYearMonth;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSAnySimpleType;
import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDate;
import xml.xpath31.processor.types.XSDateTime;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
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
   */
  protected Object m_obj;
  
  /**
   * Stores XSLT parameter tunnel attribute's value.
   * This shall be null, if this XObject instance 
   * doesn't represent tunnel parameter's value.
   */
  private String m_tunnelStrVal;

  /**
   * Stores XSLT parameter's name, if the parameter
   * has an attribute 'tunnel.
   * This shall be null, if this XObject instance 
   * doesn't represent tunnel parameter's value.
   */
  private QName m_QName;

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
  
  public void setObject(Object obj) {
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
	  String strVal = XslTransformEvaluationHelper.getStrVal(this);
	  XMLString xmlStr = XMLStringFactoryImpl.getFactory().newstr(strVal);
	  xmlStr.dispatchCharactersEvents(ch);
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
  
  /** Constant for XPath 3.1 xs:anyURI object type */
  public static final int CLASS_XS_ANY_URI = 23;
  
  /** Constant for XPath 3.1 xs:QName object type */
  public static final int CLASS_XS_QNAME = 24;
  
  /** Constant for an XPath 3.1 arry object type */
  public static final int CLASS_ARRAY = 25;
  
  /** Constant for an XPath 3.1 map object type */
  public static final int CLASS_MAP = 26;
  
  /** Constant for XPath 3.1 xs:nonPositiveInteger object type */
  public static final int CLASS_NON_POSITIVE_INTEGER = 27;
  
  /** Constant for XPath 3.1 xs:negativeInteger object type */
  public static final int CLASS_NEGATIVE_INTEGER = 28;
  
  /** Constant for XPath 3.1 xs:nonNegativeInteger object type */
  public static final int CLASS_NON_NEGATIVE_INTEGER = 29;
  
  /** Constant for XPath 3.1 xs:positiveInteger object type */
  public static final int CLASS_POSITIVE_INTEGER = 30;
  
  /** Constant for XPath 3.1 xs:short object type */
  public static final int CLASS_SHORT = 31;
  
  /** Constant for XPath 3.1 xs:byte object type */
  public static final int CLASS_BYTE = 32;
  
  /** Constant for XPath 3.1 xs:unsignedLong object type */
  public static final int CLASS_UNSIGNED_LONG = 33;
  
  /** Constant for XPath 3.1 xs:unsignedInt object type */
  public static final int CLASS_UNSIGNED_INT = 34;
  
  /** Constant for XPath 3.1 xs:unsignedShort object type */
  public static final int CLASS_UNSIGNED_SHORT = 35;
  
  /** Constant for XPath 3.1 xs:unsignedByte object type */
  public static final int CLASS_UNSIGNED_BYTE = 36;
  
  /** Constant for XPath 3.1 xs:gYearMonth object type */
  public static final int CLASS_GYEAR_MONTH = 37;
  
  /** Constant for XPath 3.1 xs:gYear object type */
  public static final int CLASS_GYEAR = 38;
  
  /** Constant for XPath 3.1 xs:gMonthDay object type */
  public static final int CLASS_GMONTHDAY = 39;
  
  /** Constant for XPath 3.1 xs:gDay object type */
  public static final int CLASS_GDAY = 40;
  
  /** Constant for XPath 3.1 xs:gMonth object type */
  public static final int CLASS_GMONTH = 41;

  /** Represents an unresolved variable type as an integer. */
  public static final int CLASS_UNRESOLVEDVARIABLE = 600;
  
  /** 
   * An xdm item may have a type annotation conforming to this
   * schema type. A non-null value of this class field implies that,
   * this object instance has this type annotation.
   */
  private XSTypeDefinition m_xsTypeDefinition;
  
  /** 
   * If this variable has value true, then XPath "treat as" or 
   * "castable as" evaluations are required on this XObject instance.
   */
  private boolean m_isTreatAs;
  
  /**
   * An XSL stylesheet root object.
   */
  private StylesheetRoot m_xslStylesheetRoot;

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
  public DTMCursorIterator iter() throws javax.xml.transform.TransformerException
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
    if (obj2.getType() == XObject.CLASS_NODESET) {
       return obj2.greaterThan(this);
    }
    else if (this instanceof XSNumericType) {
       XSNumericType lXsNumericType = (XSNumericType)this;
       String lStr = lXsNumericType.stringValue();
       return Double.valueOf(lStr) < obj2.num();
    }
    else {
       return this.num() < obj2.num();
    }
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
       
	  boolean result = false;
	  
	  XPathContext xctxt = new XPathContext();
       
      XSTypeDefinition xsObj1Type = this.getXsTypeDefinition();
 	  XSTypeDefinition xsObj2Type = obj2.getXsTypeDefinition();
 	  
 	  if ((xsObj1Type != null) || (xsObj2Type != null)) { 		   		  
 		  String strVal1 = XslTransformEvaluationHelper.getStrVal(this);
 		  if ((XSL3FunctionService.XS_VALID_TRUE).equals(strVal1)) {
 			  strVal1 = str(); 
 		  }
 		  
 		  String strVal2 = XslTransformEvaluationHelper.getStrVal(obj2);
 		  if ((XSL3FunctionService.XS_VALID_TRUE).equals(strVal2)) {
 			  strVal2 = obj2.str(); 
 		  }
 		  
 		  XSNumericType lXsNumericValue = null;
 		  XSNumericType rXsNumericValue = null;
 		  
 		  if (xsObj1Type != null) {
 			  XSTypeDefinition xsObj1BaseType = xsObj1Type.getBaseType();			  
 			  if ((Constants.NS_XMLSCHEMA).equals(xsObj1BaseType.getNamespace())) {
 				  String lXsBaseTypeName = xsObj1BaseType.getName();
 				  if ((Keywords.XS_INTEGER).equals(lXsBaseTypeName)) {
 					  lXsNumericValue = new XSInteger(strVal1); 
 				  }
 				  else if ((Keywords.XS_DECIMAL).equals(lXsBaseTypeName)) {
 					  lXsNumericValue = new XSDecimal(strVal1);
 				  }
                   else if ((Keywords.XS_FLOAT).equals(lXsBaseTypeName)) {
                 	  lXsNumericValue = new XSFloat(strVal1);
 				  }
                   else if ((Keywords.XS_DOUBLE).equals(lXsBaseTypeName)) {
                 	  lXsNumericValue = new XSDouble(strVal1);
                   }
 			  }
 		  }
 		  
 		  if (xsObj2Type != null) {
 			  XSTypeDefinition xsObj2BaseType = xsObj2Type.getBaseType();			  
 			  if ((Constants.NS_XMLSCHEMA).equals(xsObj2BaseType.getNamespace())) {
 				  String rXsTypeName = xsObj2BaseType.getName();
 				  if ((Keywords.XS_INTEGER).equals(rXsTypeName)) {
 					  rXsNumericValue = new XSInteger(strVal2); 
 				  }
 				  else if ((Keywords.XS_DECIMAL).equals(rXsTypeName)) {
 					  rXsNumericValue = new XSDecimal(strVal2);
 				  }
                   else if ((Keywords.XS_FLOAT).equals(rXsTypeName)) {
                 	  rXsNumericValue = new XSFloat(strVal2);
 				  }
                   else if ((Keywords.XS_DOUBLE).equals(rXsTypeName)) {
                 	  rXsNumericValue = new XSDouble(strVal2);
                   }
 			  }
 		  }
 		  
 		  if ((lXsNumericValue != null) && (rXsNumericValue == null)) {
 			  result = lXsNumericValue.vcLessThan(obj2, expressionOwner, collationUri, isLtTest); 
 		  }
 		  else if ((lXsNumericValue == null) && (rXsNumericValue != null)) {
 			  result = this.vcLessThan(rXsNumericValue, expressionOwner, collationUri, isLtTest); 
 		  }
 		  else if ((lXsNumericValue != null) && (rXsNumericValue != null)) {
 			  result = lXsNumericValue.vcLessThan(rXsNumericValue, expressionOwner, collationUri, isLtTest); 
 		  }		  
 		  else if (this.vcLessThan(obj2, expressionOwner, collationUri, isLtTest)) {			  
 			  result = true;
 		  }
 		  
 		  return result;
 	   }      
 	   else if ((this instanceof XSDecimal) && (obj2 instanceof XSDecimal)) {
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
       else if ((this instanceof XSGDay) && (obj2 instanceof XSGDay)) {
    	  XSGDay val1 = (XSGDay)this;
    	  XSGDay val2 = (XSGDay)obj2;
    	  BigInteger date1 = (val1.getDate()).intValue();
   	      BigInteger date2 = (val2.getDate()).intValue();
   	      if (date1.compareTo(date2) < 0) {
 		     return true; 
 	      }
 	      else if (date1.compareTo(date2) > 0) {
  		     return false; 
  	      }
 	      else if (date1.compareTo(date2) == 0) {
  		     return false; 
  	      }
       }
       else if ((this instanceof XSGMonth) && (obj2 instanceof XSGMonth)) {
    	   XSGMonth val1 = (XSGMonth)this;
    	   XSGMonth val2 = (XSGMonth)obj2;
    	   BigInteger month1 = (val1.getMonth()).intValue();
    	   BigInteger month2 = (val2.getMonth()).intValue();
    	   if (month1.compareTo(month2) < 0) {
    		   return true; 
    	   }
    	   else if (month1.compareTo(month2) > 0) {
    		   return false; 
    	   }
    	   else if (month1.compareTo(month2) == 0) {
    		   return false; 
    	   }
       }
       else if ((this instanceof XSGMonthDay) && (obj2 instanceof XSGMonthDay)) {
    	   XSGMonthDay val1 = (XSGMonthDay)this;
    	   XSGMonthDay val2 = (XSGMonthDay)obj2;
    	   BigInteger month1 = (val1.getMonth()).intValue();
    	   BigInteger month2 = (val2.getMonth()).intValue();
    	   BigInteger day1 = (val1.getDay()).intValue();
    	   BigInteger day2 = (val2.getDay()).intValue();
    	   if (month1.compareTo(month2) < 0) {
    		  return true; 
    	   }
    	   else if (month1.compareTo(month2) > 0) {
     		  return false; 
     	   }
    	   else if ((month1.compareTo(month2) == 0) && (day1.compareTo(day2) == 0)) {
      		  return false; 
      	   }
    	   else if ((month1.compareTo(month2) == 0) && (day1.compareTo(day2) < 0)) {
       		  return true; 
       	   }
    	   else if ((month1.compareTo(month2) == 0) && (day1.compareTo(day2) > 0)) {
        	  return false; 
           }
       }
       else if ((this instanceof XSGYear) && (obj2 instanceof XSGYear)) {
    	   XSGYear val1 = (XSGYear)this;
    	   XSGYear val2 = (XSGYear)obj2;
    	   BigInteger year1 = (val1.getYear()).intValue();
    	   BigInteger year2 = (val2.getYear()).intValue();
    	   if (year1.compareTo(year2) < 0) {
    		   return true; 
    	   }
    	   else if (year1.compareTo(year2) > 0) {
    		   return false; 
    	   }
    	   else if (year1.compareTo(year2) == 0) {
    		   return false; 
    	   }
       }
       else if ((this instanceof XSGYearMonth) && (obj2 instanceof XSGYearMonth)) {
    	   XSGYearMonth val1 = (XSGYearMonth)this;
    	   XSGYearMonth val2 = (XSGYearMonth)obj2;
    	   BigInteger year1 = (val1.getYear()).intValue();
    	   BigInteger year2 = (val2.getYear()).intValue();
    	   BigInteger month1 = (val1.getMonth()).intValue();
    	   BigInteger month2 = (val2.getMonth()).intValue();
    	   if (year1.compareTo(year2) < 0) {
    		  return true; 
    	   }
    	   else if (year1.compareTo(year2) > 0) {
     		  return false; 
     	   }
    	   else if ((year1.compareTo(year2) == 0) && (month1.compareTo(month2) == 0)) {
      		  return false; 
      	   }
    	   else if ((year1.compareTo(year2) == 0) && (month1.compareTo(month2) < 0)) {
       		  return true; 
       	   }
    	   else if ((year1.compareTo(year2) == 0) && (month1.compareTo(month2) > 0)) {
        	  return false; 
           }
       }
       else if ((this instanceof XSAnyAtomicType) && (obj2 instanceof XSAnyAtomicType)) {
    	  emitXsAnyAtomicTypeError(obj2, expressionOwner);   
       }
       
       boolean isOperandNodeSet1 = false;
       boolean isOperandNodeSet2 = false;
       
       if (this.getType() == XObject.CLASS_NODESET) {       
          isOperandNodeSet1 = true;
          if ((((XMLNodeCursorImpl)this).getLength() > 1)) {
              error(isLtTest ? XPATHErrorResources.ER_LT_OPERAND_CARDINALITY_ERROR : 
                                                              XPATHErrorResources.ER_GE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);
          }
       }
       
       if (obj2.getType() == XObject.CLASS_NODESET) {
          isOperandNodeSet2 = true; 
          if ((((XMLNodeCursorImpl)obj2).getLength() > 1)) {
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
	  
	  boolean result = false;
	  
	  XPathContext xctxt = new XPathContext();
       
      XSTypeDefinition xsObj1Type = this.getXsTypeDefinition();
  	  XSTypeDefinition xsObj2Type = obj2.getXsTypeDefinition();
  	  
  	  if ((xsObj1Type != null) || (xsObj2Type != null)) {  		  
  		  String strVal1 = XslTransformEvaluationHelper.getStrVal(this);
  		  if ((XSL3FunctionService.XS_VALID_TRUE).equals(strVal1)) {
  			  strVal1 = str(); 
  		  }
  		  
  		  String strVal2 = XslTransformEvaluationHelper.getStrVal(obj2);
  		  if ((XSL3FunctionService.XS_VALID_TRUE).equals(strVal2)) {
  			  strVal2 = obj2.str(); 
  		  }
  		  
  		  XSNumericType lXsNumericValue = null;
  		  XSNumericType rXsNumericValue = null;
  		  
  		  if (xsObj1Type != null) {
  			  XSTypeDefinition xsObj1BaseType = xsObj1Type.getBaseType();			  
  			  if ((Constants.NS_XMLSCHEMA).equals(xsObj1BaseType.getNamespace())) {
  				  String lXsBaseTypeName = xsObj1BaseType.getName();
  				  if ((Keywords.XS_INTEGER).equals(lXsBaseTypeName)) {
  					  lXsNumericValue = new XSInteger(strVal1); 
  				  }
  				  else if ((Keywords.XS_DECIMAL).equals(lXsBaseTypeName)) {
  					  lXsNumericValue = new XSDecimal(strVal1);
  				  }
                    else if ((Keywords.XS_FLOAT).equals(lXsBaseTypeName)) {
                  	  lXsNumericValue = new XSFloat(strVal1);
  				  }
                    else if ((Keywords.XS_DOUBLE).equals(lXsBaseTypeName)) {
                  	  lXsNumericValue = new XSDouble(strVal1);
                    }
  			  }
  		  }
  		  
  		  if (xsObj2Type != null) {
  			  XSTypeDefinition xsObj2BaseType = xsObj2Type.getBaseType();			  
  			  if ((Constants.NS_XMLSCHEMA).equals(xsObj2BaseType.getNamespace())) {
  				  String rXsTypeName = xsObj2BaseType.getName();
  				  if ((Keywords.XS_INTEGER).equals(rXsTypeName)) {
  					  rXsNumericValue = new XSInteger(strVal2); 
  				  }
  				  else if ((Keywords.XS_DECIMAL).equals(rXsTypeName)) {
  					  rXsNumericValue = new XSDecimal(strVal2);
  				  }
                    else if ((Keywords.XS_FLOAT).equals(rXsTypeName)) {
                  	  rXsNumericValue = new XSFloat(strVal2);
  				  }
                    else if ((Keywords.XS_DOUBLE).equals(rXsTypeName)) {
                  	  rXsNumericValue = new XSDouble(strVal2);
                    }
  			  }
  		  }
  		  
  		  if ((lXsNumericValue != null) && (rXsNumericValue == null)) {
  			  result = lXsNumericValue.vcGreaterThan(obj2, expressionOwner, collationUri, isGtTest); 
  		  }
  		  else if ((lXsNumericValue == null) && (rXsNumericValue != null)) {
  			  result = this.vcGreaterThan(rXsNumericValue, expressionOwner, collationUri, isGtTest); 
  		  }
  		  else if ((lXsNumericValue != null) && (rXsNumericValue != null)) {
  			  result = lXsNumericValue.vcGreaterThan(rXsNumericValue, expressionOwner, collationUri, isGtTest); 
  		  }		  
  		  else if (this.vcGreaterThan(obj2, expressionOwner, collationUri, isGtTest)) {			  
  			  result = true;
  		  }
  		  
  		  return result;
  	   }      
  	   else if ((this instanceof XSDecimal) && (obj2 instanceof XSDecimal)) {
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
       else if ((this instanceof XSGDay) && (obj2 instanceof XSGDay)) {
    	   XSGDay val1 = (XSGDay)this;
    	   XSGDay val2 = (XSGDay)obj2;
    	   BigInteger date1 = (val1.getDate()).intValue();
    	   BigInteger date2 = (val2.getDate()).intValue();
    	   if (date1.compareTo(date2) < 0) {
    		   return false; 
    	   }
    	   else if (date1.compareTo(date2) > 0) {
    		   return true; 
    	   }
    	   else if (date1.compareTo(date2) == 0) {
    		   return false; 
    	   }
       }
       else if ((this instanceof XSGMonth) && (obj2 instanceof XSGMonth)) {
    	   XSGMonth val1 = (XSGMonth)this;
    	   XSGMonth val2 = (XSGMonth)obj2;
    	   BigInteger month1 = (val1.getMonth()).intValue();
    	   BigInteger month2 = (val2.getMonth()).intValue();
    	   if (month1.compareTo(month2) < 0) {
    		   return false; 
    	   }
    	   else if (month1.compareTo(month2) > 0) {
    		   return true; 
    	   }
    	   else if (month1.compareTo(month2) == 0) {
    		   return false; 
    	   }
       }
       else if ((this instanceof XSGMonthDay) && (obj2 instanceof XSGMonthDay)) {
    	   XSGMonthDay val1 = (XSGMonthDay)this;
    	   XSGMonthDay val2 = (XSGMonthDay)obj2;
    	   BigInteger month1 = (val1.getMonth()).intValue();
    	   BigInteger month2 = (val2.getMonth()).intValue();
    	   BigInteger day1 = (val1.getDay()).intValue();
    	   BigInteger day2 = (val2.getDay()).intValue();
    	   if (month1.compareTo(month2) < 0) {
    		  return false; 
    	   }
    	   else if (month1.compareTo(month2) > 0) {
     		  return true; 
     	   }
    	   else if ((month1.compareTo(month2) == 0) && (day1.compareTo(day2) == 0)) {
      		  return false; 
      	   }
    	   else if ((month1.compareTo(month2) == 0) && (day1.compareTo(day2) < 0)) {
       		  return false; 
       	   }
    	   else if ((month1.compareTo(month2) == 0) && (day1.compareTo(day2) > 0)) {
        	  return true; 
           }
       }
       else if ((this instanceof XSGYear) && (obj2 instanceof XSGYear)) {
    	   XSGYear val1 = (XSGYear)this;
    	   XSGYear val2 = (XSGYear)obj2;
    	   BigInteger year1 = (val1.getYear()).intValue();
    	   BigInteger year2 = (val2.getYear()).intValue();
    	   if (year1.compareTo(year2) < 0) {
    		   return false; 
    	   }
    	   else if (year1.compareTo(year2) > 0) {
    		   return true; 
    	   }
    	   else if (year1.compareTo(year2) == 0) {
    		   return false; 
    	   }
       }
       else if ((this instanceof XSGYearMonth) && (obj2 instanceof XSGYearMonth)) {
    	   XSGYearMonth val1 = (XSGYearMonth)this;
    	   XSGYearMonth val2 = (XSGYearMonth)obj2;
    	   BigInteger year1 = (val1.getYear()).intValue();
    	   BigInteger year2 = (val2.getYear()).intValue();
    	   BigInteger month1 = (val1.getMonth()).intValue();
    	   BigInteger month2 = (val2.getMonth()).intValue();
    	   if (year1.compareTo(year2) < 0) {
    		  return false; 
    	   }
    	   else if (year1.compareTo(year2) > 0) {
     		  return true; 
     	   }
    	   else if ((year1.compareTo(year2) == 0) && (month1.compareTo(month2) == 0)) {
      		  return false; 
      	   }
    	   else if ((year1.compareTo(year2) == 0) && (month1.compareTo(month2) < 0)) {
       		  return false; 
       	   }
    	   else if ((year1.compareTo(year2) == 0) && (month1.compareTo(month2) > 0)) {
        	  return true; 
           }
       }
       else if ((this instanceof XSAnyAtomicType) && (obj2 instanceof XSAnyAtomicType)) {
    	  emitXsAnyAtomicTypeError(obj2, expressionOwner);   
       }
       
       boolean isOperandNodeSet1 = false;
       boolean isOperandNodeSet2 = false;
       
       if (this.getType() == XObject.CLASS_NODESET) {       
          isOperandNodeSet1 = true;
          if ((((XMLNodeCursorImpl)this).getLength() > 1)) {
              error(isGtTest ? XPATHErrorResources.ER_GT_OPERAND_CARDINALITY_ERROR : 
                                                              XPATHErrorResources.ER_LE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);
          }
       }
       
       if (obj2.getType() == XObject.CLASS_NODESET) {
          isOperandNodeSet2 = true; 
          if ((((XMLNodeCursorImpl)obj2).getLength() > 1)) {
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
	  boolean result = false;

	  if ((this instanceof XSNumericType) && (obj2 instanceof XSNumericType)) {
		  String lStr = ((XSNumericType)this).stringValue();
		  BigDecimal lBigDecimal = new BigDecimal(lStr);		  
		  String rStr = ((XSNumericType)obj2).stringValue();
		  BigDecimal rBigDecimal = new BigDecimal(rStr);
		  
		  result = (lBigDecimal.compareTo(rBigDecimal) == 0); 
	  }
	  else if ((this instanceof XSNumericType) && (obj2 instanceof XNumber)) {
		  String lStr = ((XSNumericType)this).stringValue();
		  BigDecimal lBigDecimal = new BigDecimal(lStr);		  
		  BigDecimal rBigDecimal = BigDecimal.valueOf(((XNumber)obj2).num());
		  
		  result = (lBigDecimal.compareTo(rBigDecimal) == 0);
	  }
	  else if ((this instanceof XNumber) && (obj2 instanceof XSNumericType)) {
		  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)this).num());		  
		  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)obj2).stringValue());
		  
		  result = (lBigDecimal.compareTo(rBigDecimal) == 0);
	  }
	  else if ((this instanceof XNumber) && (obj2 instanceof XNumber)) {
		  double lDouble = ((XNumber)this).num();
		  double rDouble = ((XNumber)obj2).num();
		  
		  result = (lDouble == rDouble);
	  }
	  else if ((this instanceof XSBoolean) && (obj2 instanceof XSBoolean)) {
		  result = ((XSBoolean)this).equals((XSBoolean)obj2);    
	  }
	  else if ((this instanceof XBoolean) && (obj2 instanceof XBoolean)) {
		  boolean lBool = ((XBoolean)this).bool();
		  boolean rBool = ((XBoolean)obj2).bool();
		  
		  result = (lBool == rBool);
	  }
	  else if ((this instanceof XSBoolean) && (obj2 instanceof XBoolean)) {
		  boolean lBool = ((XSBoolean)this).bool();
		  boolean rBool = ((XBoolean)obj2).bool();
		  
		  result = (lBool == rBool);    
	  }
	  else if ((this instanceof XBoolean) && (obj2 instanceof XSBoolean)) {
		  boolean lBool = ((XBoolean)this).bool();
		  boolean rBool = ((XSBoolean)obj2).bool();
		  
		  result = (lBool == rBool);    
	  }	
	  else if (this instanceof XSAnyURI) {
		  boolean isEqual = false;

		  try {
			  isEqual = ((XSAnyURI)this).eq(obj2);
		  } 
		  catch (TransformerException ex) {
			  isEqual = false;
		  }   

		  result = isEqual;
	  }	    
	  else if ((this instanceof XSString) && (obj2 instanceof XSString)) {
		  String lStr = ((XSString)this).stringValue();
		  String rStr = ((XSString)obj2).stringValue();
		  
		  result = lStr.equals(rStr);
	  }
	  else if ((this instanceof XSString) && (obj2 instanceof XString)) {
		  String lStr = ((XSString)this).stringValue();
		  String rStr = ((XString)obj2).str();
		  
		  result = lStr.equals(rStr);
	  }
	  else if ((this instanceof XString) && (obj2 instanceof XSString)) {           
		  String lStr = ((XString)this).str();
		  String rStr = ((XSString)obj2).stringValue();
		  
		  result = lStr.equals(rStr);
	  }
	  else if ((this instanceof XString) && (obj2 instanceof XString)) {
		  String lStr = ((XString)this).str();
		  String rStr = ((XString)obj2).str();
		  
		  result = lStr.equals(rStr);
	  }
	  else if ((this instanceof XSDate) && (obj2 instanceof XSDate)) {
		  result = ((XSDate)this).equals((XSDate)obj2);    
	  }
	  else if ((this instanceof XSDateTime) && (obj2 instanceof XSDateTime)) {
		  result = ((XSDateTime)this).equals((XSDateTime)obj2);    
	  }
	  else if ((this instanceof XSTime) && (obj2 instanceof XSTime)) {
		  result = ((XSTime)this).equals((XSTime)obj2);    
	  }	        
	  else if (obj2.getType() == XObject.CLASS_NODESET) {
		  result = obj2.equals(this);
	  }	
	  else if (m_obj != null) {
		  result = m_obj.equals(obj2.m_obj);
	  }
	  else {
		  result = (obj2.m_obj == null);
	  }

	  return result;
  }
  
  /**
   * Perform equality check for values between two XObject instances, considering
   * string comparison collation.  
   */
  public boolean equals(XObject obj2, String collationUri, 
		                                               XPathCollationSupport xpathCollationSupport) throws TransformerException {
	  boolean isEquals = false;
	  
	  if ((this instanceof XSString) && (obj2 instanceof XSString)) {
		 String lStr = ((XSString)this).stringValue();
		 String rStr = ((XSString)obj2).stringValue();
		 
		 int strComparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, collationUri);
         if (strComparisonResult == 0) {
            isEquals = true; 
         }
	  }
	  else if ((this instanceof XSString) && (obj2 instanceof XString)) {
		 String lStr = ((XSString)this).stringValue();
		 String rStr = ((XString)obj2).str();
		 
		 int strComparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, collationUri);
         if (strComparisonResult == 0) {
            isEquals = true; 
         }
	  }
      else if ((this instanceof XString) && (obj2 instanceof XSString)) {
    	 String lStr = ((XString)this).str();
 		 String rStr = ((XSString)obj2).stringValue();
 		 
 		 int strComparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, collationUri);
         if (strComparisonResult == 0) {
            isEquals = true; 
         } 
	  }
      else if ((this instanceof XString) && (obj2 instanceof XString)) {
    	 String lStr = ((XString)this).str();
  		 String rStr = ((XString)obj2).str();
  		 
  		 int strComparisonResult = xpathCollationSupport.compareStringsUsingCollation(lStr, rStr, collationUri);
         if (strComparisonResult == 0) {
            isEquals = true; 
         }  
	  }
      else {
    	 isEquals = this.equals(obj2);
      }
	  
	  return isEquals; 
  }
  
  public boolean equals(Object obj2) {
	 boolean isEqual = false;
	 
	 try {
		if (obj2 instanceof ResultSequence) {
		   obj2 = ((ResultSequence)obj2).item(0);	
		}		
		isEqual = this.vcEquals((XObject)obj2, null, true);
	 } 
	 catch (TransformerException ex) {
	    // no op
	 }
	 
	 return isEqual;
  }
  
  public int hashCode() {
	 String strVal = null;
	 
	 if (this instanceof XSAnySimpleType) {
		strVal = ((XSAnySimpleType)this).stringValue();
	 }
	 else {
		strVal = this.str(); 
	 }
	 
	 return strVal.hashCode();
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
	  boolean result = false;
	  
	  XSTypeDefinition xsObj1Type = this.getXsTypeDefinition();
	  XSTypeDefinition xsObj2Type = obj2.getXsTypeDefinition();
	  
	  if ((xsObj1Type != null) || (xsObj2Type != null)) {
		  String strVal1 = XslTransformEvaluationHelper.getStrVal(this);
		  if ((XSL3FunctionService.XS_VALID_TRUE).equals(strVal1)) {
			  strVal1 = str(); 
		  }
		  
		  String strVal2 = XslTransformEvaluationHelper.getStrVal(obj2);
		  if ((XSL3FunctionService.XS_VALID_TRUE).equals(strVal2)) {
			  strVal2 = obj2.str(); 
		  }
		  
		  XSNumericType lXsNumericValue = null;
		  XSNumericType rXsNumericValue = null;
		  
		  if (xsObj1Type != null) {
			  XSTypeDefinition xsObj1BaseType = xsObj1Type.getBaseType();			  
			  if ((Constants.NS_XMLSCHEMA).equals(xsObj1BaseType.getNamespace())) {
				  String lXsBaseTypeName = xsObj1BaseType.getName();
				  if ((Keywords.XS_INTEGER).equals(lXsBaseTypeName)) {
					  lXsNumericValue = new XSInteger(strVal1); 
				  }
				  else if ((Keywords.XS_DECIMAL).equals(lXsBaseTypeName)) {
					  lXsNumericValue = new XSDecimal(strVal1);
				  }
                  else if ((Keywords.XS_FLOAT).equals(lXsBaseTypeName)) {
                	  lXsNumericValue = new XSFloat(strVal1);
				  }
                  else if ((Keywords.XS_DOUBLE).equals(lXsBaseTypeName)) {
                	  lXsNumericValue = new XSDouble(strVal1);
                  }
			  }
		  }
		  
		  if (xsObj2Type != null) {
			  XSTypeDefinition xsObj2BaseType = xsObj2Type.getBaseType();			  
			  if ((Constants.NS_XMLSCHEMA).equals(xsObj2BaseType.getNamespace())) {
				  String rXsTypeName = xsObj2BaseType.getName();
				  if ((Keywords.XS_INTEGER).equals(rXsTypeName)) {
					  rXsNumericValue = new XSInteger(strVal2); 
				  }
				  else if ((Keywords.XS_DECIMAL).equals(rXsTypeName)) {
					  rXsNumericValue = new XSDecimal(strVal2);
				  }
                  else if ((Keywords.XS_FLOAT).equals(rXsTypeName)) {
                	  rXsNumericValue = new XSFloat(strVal2);
				  }
                  else if ((Keywords.XS_DOUBLE).equals(rXsTypeName)) {
                	  rXsNumericValue = new XSDouble(strVal2);
                  }
			  }
		  }
		  
		  if ((lXsNumericValue != null) && (rXsNumericValue == null)) {
			  result = lXsNumericValue.vcEquals(obj2, expressionOwner, isEqTest); 
		  }
		  else if ((lXsNumericValue == null) && (rXsNumericValue != null)) {
			  result = rXsNumericValue.vcEquals(this, expressionOwner, isEqTest); 
		  }
		  else if ((lXsNumericValue != null) && (rXsNumericValue != null)) {
			  result = lXsNumericValue.vcEquals(rXsNumericValue, expressionOwner, isEqTest); 
		  }		  
		  else if (strVal1.equals(strVal2)) {			  
			  result = true;
		  }
		  
		  return result;
	  }
	  else if ((this instanceof XSNumericType) && (obj2 instanceof XSNumericType)) {
		  String lStr = ((XSNumericType)this).stringValue();
		  BigDecimal lBigDecimal = new BigDecimal(lStr);		  
		  String rStr = ((XSNumericType)obj2).stringValue();
		  BigDecimal rBigDecimal = new BigDecimal(rStr);

		  result = (lBigDecimal.compareTo(rBigDecimal) == 0); 
	  }
	  else if ((this instanceof XSNumericType) && (obj2 instanceof XNumber)) {
		  String lStr = ((XSNumericType)this).stringValue();
		  BigDecimal lBigDecimal = new BigDecimal(lStr);		  
		  BigDecimal rBigDecimal = BigDecimal.valueOf(((XNumber)obj2).num());

		  result = (lBigDecimal.compareTo(rBigDecimal) == 0);
	  }
	  else if ((this instanceof XNumber) && (obj2 instanceof XSNumericType)) {
		  BigDecimal lBigDecimal = BigDecimal.valueOf(((XNumber)this).num());		  
		  BigDecimal rBigDecimal = new BigDecimal(((XSNumericType)obj2).stringValue());

		  result = (lBigDecimal.compareTo(rBigDecimal) == 0);
	  }
	  else if ((this instanceof XNumber) && (obj2 instanceof XNumber)) {
		  double lDouble = ((XNumber)this).num();
		  double rDouble = ((XNumber)obj2).num();

		  result = (lDouble == rDouble);
	  }
	  else if ((this instanceof XSNumericType || this instanceof XNumber) && 
			                                         !(obj2 instanceof XSNumericType || obj2 instanceof XNumber)) {
		  result = false;
	  }
	  else if (!(this instanceof XSNumericType || this instanceof XNumber) && 
                                                      (obj2 instanceof XSNumericType || obj2 instanceof XNumber)) {
          result = false;
      }
	  else if ((this instanceof XSBoolean) && (obj2 instanceof XSBoolean)) {
		  result = ((XSBoolean)this).equals((XSBoolean)obj2);    
	  }
	  else if ((this instanceof XBoolean) && (obj2 instanceof XBoolean)) {
		  boolean lBool = ((XBoolean)this).bool();
		  boolean rBool = ((XBoolean)obj2).bool();
		  result = (lBool == rBool);
	  }
	  else if ((this instanceof XSBoolean) && (obj2 instanceof XBoolean)) {
		  boolean lBool = ((XSBoolean)this).bool();
		  boolean rBool = ((XBoolean)obj2).bool();
		  result = (lBool == rBool);    
	  }
	  else if ((this instanceof XBoolean) && (obj2 instanceof XSBoolean)) {
		  boolean lBool = ((XBoolean)this).bool();
		  boolean rBool = ((XSBoolean)obj2).bool();
		  result = (lBool == rBool);    
	  }
	  else if (this instanceof XSAnyURI) {
		  boolean isEqual = false;

		  try {
			  isEqual = ((XSAnyURI)this).eq(obj2);
		  } 
		  catch (TransformerException ex) {
			  throw ex;
		  }   

		  result = isEqual;
	  }
	  else if ((this instanceof XSString) && (obj2 instanceof XSString)) {
		  result = ((XSString)this).equals((XSString)obj2);    
	  }
	  else if ((this instanceof XString) && (obj2 instanceof XString)) {
		  String lStr = (((XString)this)).str();
		  String rStr = (((XString)obj2)).str();

		  result = (new XSString(lStr)).equals(new XSString(rStr));
	  }
	  else if ((this instanceof XSString) && (obj2 instanceof XString)) {
		  String lStr = ((((XSString)this))).stringValue();
		  String rStr = (((XString)obj2)).str();

		  result = (new XSString(lStr)).equals(new XSString(rStr));
	  }
	  else if ((this instanceof XString) && (obj2 instanceof XSString)) {
		  String lStr = (((XString)this)).str();
		  String rStr = ((((XSString)obj2))).stringValue();

		  result = (new XSString(lStr)).equals(new XSString(rStr));
	  }
	  else if (this instanceof XSDate) {
		  if (obj2 instanceof XSDate) {
			  result = ((XSDate)this).equals((XSDate)obj2);
		  }
		  else {
			  result = false; 
		  }    
	  }    
	  else if (this instanceof XSDateTime) {
		  if (obj2 instanceof XSDateTime) {
			  result = ((XSDateTime)this).equals((XSDateTime)obj2);
		  }
		  else {
			  result = false; 
		  }   
	  }
	  else if (this instanceof XSTime) {
		  if (obj2 instanceof XSTime) {
			  result = ((XSTime)this).equals((XSTime)obj2);
		  }
		  else {
			  result = false; 
		  }   
	  }
	  else if (this instanceof XSDuration) {
		  if (obj2 instanceof XSDuration) {
		     result = ((XSDuration)this).equals((XSDuration)obj2);
		  }
		  else {
			 result = false; 
		  }
	  }
	  else if (this instanceof XSGYearMonth) {
		  if (obj2 instanceof XSGYearMonth) {
		     result = ((XSGYearMonth)this).eq((XSGYearMonth)obj2);
		  }
		  else {
			 result = false; 
		  }
	  }
	  else if (this instanceof XSGYear) {
		  if (obj2 instanceof XSGYear) {
		     result = ((XSGYear)this).eq((XSGYear)obj2);
		  }
		  else {
			 result = false; 
		  }
	  }
	  else if (this instanceof XSGMonthDay) {
		  if (obj2 instanceof XSGMonthDay) {
		     result = ((XSGMonthDay)this).eq((XSGMonthDay)obj2);
		  }
		  else {
			 result = false; 
		  }
	  }
	  else if (this instanceof XSGDay) {
		  if (obj2 instanceof XSGDay) {
		     result = ((XSGDay)this).eq((XSGDay)obj2);
		  }
		  else {
			 result = false; 
		  }
	  }
	  else if (this instanceof XSGMonth) {
		  if (obj2 instanceof XSGMonth) {
		     result = ((XSGMonth)this).eq((XSGMonth)obj2);
		  }
		  else {
			 result = false; 
		  }
	  }
	  else if ((this instanceof XSAnyAtomicType) && (obj2 instanceof XSAnyAtomicType)) {
   	      emitXsAnyAtomicTypeError(obj2, expressionOwner);   
      }
	  else if (this.getType() == XObject.CLASS_NODESET) {
		  if ((((XMLNodeCursorImpl)this).getLength() > 1)) {
			  error(isEqTest ? XPATHErrorResources.ER_EQ_OPERAND_CARDINALITY_ERROR : 
				  XPATHErrorResources.ER_NE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);    
		  }
		  else if (obj2.getType() == XObject.CLASS_NODESET) {
			  if ((((XMLNodeCursorImpl)obj2).getLength() > 1)) {
				  error(isEqTest ? XPATHErrorResources.ER_EQ_OPERAND_CARDINALITY_ERROR : 
					  XPATHErrorResources.ER_NE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);    
			  }
			  else {
				  result = this.equals(obj2); 
			  }
		  }
		  else {
			  result = this.equals(obj2);
		  }
	  }    
	  else if (obj2.getType() == XObject.CLASS_NODESET) { 
		  if ((((XMLNodeCursorImpl)obj2).getLength() > 1)) {
			  error(isEqTest ? XPATHErrorResources.ER_EQ_OPERAND_CARDINALITY_ERROR : 
				  XPATHErrorResources.ER_NE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);    
		  }
		  else if (this.getType() == XObject.CLASS_NODESET) {
			  if ((((XMLNodeCursorImpl)this).getLength() > 1)) {
				  error(isEqTest ? XPATHErrorResources.ER_EQ_OPERAND_CARDINALITY_ERROR : 
					  XPATHErrorResources.ER_NE_OPERAND_CARDINALITY_ERROR, null, expressionOwner);    
			  }
			  else {
				  result = obj2.equals(this); 
			  }
		  }
		  else {
			  result = obj2.equals(this);
		  }
	  }
	  else if (m_obj != null) {
		  result = m_obj.equals(obj2.m_obj);
	  }
	  else {
		  result = (obj2.m_obj == null);
	  }

	  return result;
    
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

  public void setTunnel(String tunnelStrVal) {
	 m_tunnelStrVal = tunnelStrVal;	
  }
  
  public String getTunnel() {
	 return m_tunnelStrVal;  
  }

  public void setQName(QName qName) {
	 m_QName = qName;	
  }
  
  public QName getQName() {
	 return m_QName; 
  }

  public XSTypeDefinition getXsTypeDefinition() {
	 return m_xsTypeDefinition;
  }

  public void setXsTypeDefinition(XSTypeDefinition xsTypeDefinition) {
	 this.m_xsTypeDefinition = xsTypeDefinition;
  }

  public void setTreatAs(boolean isTreatAs) {
	 this.m_isTreatAs = isTreatAs;
  }
  
  public boolean isTreatAs() {
	 return m_isTreatAs; 
  }
  
  public void setXslStylesheetRoot(StylesheetRoot xslStylesheetRoot) {
     this.m_xslStylesheetRoot = xslStylesheetRoot; 
  }
  
  public StylesheetRoot getXslStylesheetRoot() {
	 return m_xslStylesheetRoot;  
  }
  
  private void emitXsAnyAtomicTypeError(XObject xObject, ExpressionNode expressionOwner) throws TransformerException {
	 String typeName1 = ((XSAnyAtomicType)this).typeName();
	 String typeName2 = ((XSAnyAtomicType)xObject).typeName();
		   
	 error(XPATHErrorResources.ER_TYPE_COMPARISON_ERROR, new Object[] {"xs:" + typeName1, 
			                                                                         "xs:" + typeName2}, expressionOwner);
  }

}
