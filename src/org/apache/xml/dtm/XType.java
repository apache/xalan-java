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
package org.apache.xml.dtm;

import java.net.URL;
import java.util.Hashtable;

import org.apache.xml.utils.QName;

/**
 * The responsibility of XType is to provide static information about 
 * types, like the ID and the name.
 * 
 * Created Jul 16, 2002
 * @author sboag
 */
public class XType
{  
  // These values need to be what they are, at least for right now.  -sb
  // (This is because of the method resolver in the extension mechanism!)
  public static final int ANYTYPE = 0;
  public static final int BOOLEAN = 1;
  public static final int DOUBLE = 2;
  public static final int STRING = 3;
  public static final int NODE = 4;
  public static final int RTREEFRAG = 5;
  
  // Callers should expect the actual values of these constants to 
  // changed... i.e. they should not rely on the value itself.
  public static final int BASE64BINARY = 6;
  public static final int HEXBINARY = 7;
  public static final int FLOAT = 8;
  public static final int DECIMAL = 9;
  public static final int INTEGER = 10;
  public static final int ANYURI = 11;
  public static final int QNAME = 12;
  public static final int NOTATION = 13;
  public static final int DURATION = 14;
  public static final int DATETIME = 15;
  public static final int TIME = 16;
  public static final int DATE = 17;
  public static final int GYEARMONTH = 18;
  public static final int GYEAR = 19;
  public static final int GMONTHDAY = 20;
  public static final int GDAY = 21;
  public static final int GMONTH = 22;
  
  public static final int YEARMONTHDURATION = 23;
  public static final int DAYTIMEDURATION = 24;

  public static final int ANYSIMPLETYPE = 25;
  public static final int EMPTYSEQ = 26;
  
  // Answer for a sequence when it hasn't iterated. -sb
  public static final int SEQ = 27; 


  public static final int MAXTYPES = 27; 
  
  // This is probably primarily for diagnostic purposes.
  private static final String[] m_names = 
  {
    "AnyType", // ANYTYPE = 0;
    "boolean", // BOOLEAN = 1;
    "double", // DOUBLE = 2;
    "string", // STRING = 3;
    "NODE", // NODE = 4;
    "RTreeRfrag", // RTREEFRAG = 5;
    "base64Binary", // BASE64BINARY = 6;
    "hexBinary", // HEXBINARY = 7;
    "float", // FLOAT = 8;
    "decimal", // DECIMAL = 9;
    "integer", // INTEGER = 10;
    "anyURI", // ANYURI = 11;
    "QName", // QNAME = 12;
    "NOTATION", // NOTATION = 13;
    "duration", // DURATION = 14;
    "dateTime", // DATETIME = 15;
    "time", // TIME = 16;
    "date", // DATE = 17;
    "gYearMonth", // GYEARMONTH = 18;
    "gYear", // GYEAR = 19;
    "gMonthDay", // GMONTHDAY = 20;
    "gDay", // GDAY = 21;
    "gMonth", // GMONTH = 22;
    "yearMonthDuration", // YEARMONTHDURATION = 23;
    "dayTimeDuration", // DAYTIMEDURATION = 24;
    "AnySimpleType", // ANYSIMPLETYPE = 25;
    "EMPTY" // EMPTYSEQ = 26;
  };
  
  public static final int NOTHOMOGENOUS = 0xFFFFFFFF;
  
  private static Hashtable m_nameToIDs;
  
  static
  {
    m_nameToIDs = new Hashtable();
    m_nameToIDs.put("string", new Integer(STRING));
    m_nameToIDs.put("boolean", new Integer(BOOLEAN));
    m_nameToIDs.put("decimal", new Integer(DECIMAL));
    m_nameToIDs.put("integer", new Integer(INTEGER));
    m_nameToIDs.put("float", new Integer(FLOAT));
    m_nameToIDs.put("double", new Integer(DOUBLE));
    m_nameToIDs.put("duration", new Integer(DURATION));
    m_nameToIDs.put("dateTime", new Integer(DATETIME));
    m_nameToIDs.put("time", new Integer(TIME));
    m_nameToIDs.put("date", new Integer(DATE));
    m_nameToIDs.put("gYearMonth", new Integer(GYEARMONTH));
    m_nameToIDs.put("gYear", new Integer(GYEAR));
    m_nameToIDs.put("gMonthDay", new Integer(GMONTHDAY));
    m_nameToIDs.put("gDay", new Integer(GDAY));
    m_nameToIDs.put("gMonth", new Integer(GMONTH));
    m_nameToIDs.put("hexBinary", new Integer(HEXBINARY));
    m_nameToIDs.put("base64Binary", new Integer(BASE64BINARY));
    m_nameToIDs.put("anyURI", new Integer(ANYURI));
    m_nameToIDs.put("QName", new Integer(QNAME));
    m_nameToIDs.put("NOTATION", new Integer(NOTATION));
    m_nameToIDs.put("yearMonthDuration", new Integer(YEARMONTHDURATION));
    m_nameToIDs.put("dayTimeDuration", new Integer(DAYTIMEDURATION));
  }
  
  /** Manefest constant: Namespace of schema built-in datatypes.
   * Provided here as a coding convenience.
   * */
  public static final String XMLSCHEMA_DATATYPE_NAMESPACE="http://www.w3.org/2001/XMLSchema-datatypes";
  
  /** Manefest constant: Namespace of schema declarations.
   * Provided here as a coding convenience.
   * */
  public static final String XMLSCHEMA_NAMESPACE="http://www.w3.org/2001/XMLSchema";

  /**
   * Get the type ID that maps to a local name, The namespace 
   * URI of "http://www.w3.org/2001/XMLSchema-datatypes" is assumed.
   * 
   * @param localName The local name of a type.
   * @return one of STRING, BOOLEAN, etc, or ANYTYPE if the string does 
   * not map to any name.
   */
  public static int getTypeFromLocalName(String localName)
  {
    Integer idObj = (Integer)m_nameToIDs.get(localName);
    return (null != idObj) ? idObj.intValue() : ANYTYPE;
  }
  
  /**
   * Get the type ID that maps to a qualified name, The namespace 
   * of which must be URI of "http://www.w3.org/2001/XMLSchema-datatypes".
   * 
   * @param namespace of data type
   * @param localName of data type
   * @return one of STRING, BOOLEAN, etc, or ANYTYPE if the string does 
   * not map to any name.
   */
  public static int getTypeID(String namespace, String localName)
  {
    if(null == namespace 
      || (!namespace.equals(XMLSCHEMA_DATATYPE_NAMESPACE)
      && !namespace.equals(XMLSCHEMA_NAMESPACE)))
      return ANYTYPE; // Should be good enough to test for error.
    Integer idObj = (Integer)m_nameToIDs.get(localName);
    return (null != idObj) ? idObj.intValue() : ANYTYPE;
  }
  
  /**
   * Get the type ID that maps to a qualified name, The namespace 
   * of which must be URI of "http://www.w3.org/2001/XMLSchema-datatypes".
   * This should probably be moved into the DTM interface?
   * 
   * @param nodeHandle A handle to a node.
   * @return one of STRING, BOOLEAN, etc, or ANYTYPE if the string does 
   * not map to any name.
   */
  public static int getTypeID(DTM dtm, int nodeHandle)
  {
    String namespace = dtm.getSchemaTypeNamespace(nodeHandle);
    String localName = dtm.getSchemaTypeLocalName(nodeHandle);
    if(null == namespace 
      || (!namespace.equals(XMLSCHEMA_DATATYPE_NAMESPACE)
      && !namespace.equals(XMLSCHEMA_NAMESPACE)))
      return ANYTYPE; // Should be good enough to test for error.
    Integer idObj = (Integer)m_nameToIDs.get(localName);
    return (null != idObj) ? idObj.intValue() : ANYTYPE;
  }


  
  /**
   * Get the type ID that maps to a qualified name, The namespace 
   * of which must be URI of "http://www.w3.org/2001/XMLSchema-datatypes".
   * 
   * @param qname A qualifed name object.
   * @return one of STRING, BOOLEAN, etc, or ANYTYPE if the string does 
   * not map to any name.
   */
  public static int getTypeFromQName(QName qname)
  {
    String namespace = qname.getNamespaceURI();
    if(null == namespace 
      || (!namespace.equals(XMLSCHEMA_DATATYPE_NAMESPACE)
      && !namespace.equals(XMLSCHEMA_NAMESPACE)))
      return ANYTYPE; // Should be good enough to test for error.
    Integer idObj = (Integer)m_nameToIDs.get(qname.getLocalName());
    return (null != idObj) ? idObj.intValue() : ANYTYPE;
  }

  
  /**
   * Returns the nameToIDs table.
   * @return Hashtable that contains Strings as the key, and Integers as 
   * the value.
   */
  public static Hashtable getNameToIDs()
  {
    return m_nameToIDs;
  }
  
  /**
   * Get the type ID that maps to a local name, The namespace 
   * URI of "http://www.w3.org/2001/XMLSchema-datatypes" is assumed.
   * 
   * @param id one of EMPTYSEQ, NODE, STRING, BOOLEAN, etc.
   * @throws IndexOutOfBoundsException if id &gt;= 22, or id &lt; 0.
   */
  public static String getLocalNameFromType(int id)
  {
    return m_names[id];
  }


  /**
   * Returns the names table, primarily for diagnostic purposes.
   * @return String[]
   */
  public static String[] getNames()
  {
    return m_names;
  }
  
  /**
   * This method does it's best to infer the type name from a 
   * java object.
   * @param obj
   * @return String
   */
  public static String inferLocalNameFromJavaObject(Object obj)
  {
    //  public static final int STRING = 4;
    //  public static final int BOOLEAN = 5;
    //  public static final int BASE64BINARY = 6;
    //  public static final int HEXBINARY = 7;
    //  public static final int FLOAT = 8;
    //  public static final int DECIMAL = 9;
    //  public static final int INTEGER = 10;
    //  public static final int DOUBLE = 11;
    //  public static final int ANYURI = 12;
    //  public static final int QNAME = 13;
    //  public static final int NOTATION = 14;
    //  public static final int DURATION = 15;
    //  public static final int DATETIME = 16;
    //  public static final int TIME = 17;
    //  public static final int DATE = 18;
    //  public static final int GYEARMONTH = 19;
    //  public static final int GYEAR = 20;
    //  public static final int GMONTHDAY = 21;
    //  public static final int GDAY = 22;
    //  public static final int GMONTH = 23;
    if(obj instanceof String)
      return getLocalNameFromType(STRING);
    else if(obj instanceof Boolean)
      return getLocalNameFromType(BOOLEAN);
    else if(obj instanceof Float)
      return getLocalNameFromType(FLOAT);
    else if(obj instanceof Double)
      return getLocalNameFromType(DOUBLE);
    else if(obj instanceof Integer)
      return getLocalNameFromType(INTEGER);
    else if(obj instanceof URL)
      return getLocalNameFromType(ANYURI);
    else if(obj instanceof QName)
      return getLocalNameFromType(QNAME);
    // TBD: all the rest.
    else
      return "unknown";
  }

}
