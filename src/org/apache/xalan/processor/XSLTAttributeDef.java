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
 *     the documentation and/or other materials provided with the
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
package org.apache.xalan.processor;

import org.apache.xalan.utils.StringToIntTable;
import java.lang.IllegalAccessException;
import java.lang.IndexOutOfBoundsException;
import java.lang.InstantiationException;
import java.lang.NoSuchMethodException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.StringBuffer;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.utils.QName;
import org.apache.xalan.utils.SystemIDResolver;
import org.apache.xalan.utils.StringVector;
import org.apache.xpath.XPath;
import org.xml.sax.SAXException;

/**
 * This class defines an attribute for an element in a XSLT stylesheet, 
 * is meant to reflect the structure defined in http://www.w3.org/TR/xslt#dtd, and the 
 * mapping between Xalan classes and the markup attributes in the element.
 */
public class XSLTAttributeDef
{
  /**
   * Construct an instance of XSLTAttributeDef.
   */
  XSLTAttributeDef(String namespace, String name, int type, boolean required)
  {
    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = type;
    this.m_required = required;
  }

  /**
   * Construct an instance of XSLTAttributeDef.
   */
  XSLTAttributeDef(String namespace, String name, int type, String defaultVal)
  {
    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = type;
    this.m_required = false;
    this.m_default = defaultVal;
  }

  /**
   * Construct an instance of XSLTAttributeDef that uses two 
   * enumerated values.
   */
  XSLTAttributeDef(String namespace, String name, boolean required,
                   String k1, int v1,
                   String k2, int v2)
  {
    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = this.T_ENUM;
    this.m_required = required;
    m_enums = new StringToIntTable(2);
    m_enums.put(k1, v1);
    m_enums.put(k2, v2);
  }

  /**
   * Construct an instance of XSLTAttributeDef that uses three 
   * enumerated values.
   */
  XSLTAttributeDef(String namespace, String name, boolean required,
                   String k1, int v1,
                   String k2, int v2,
                   String k3, int v3)
  {
    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = this.T_ENUM;
    this.m_required = required;
    m_enums = new StringToIntTable(3);
    m_enums.put(k1, v1);
    m_enums.put(k2, v2);
    m_enums.put(k3, v3);
  }

  /**
   * Construct an instance of XSLTAttributeDef that uses three 
   * enumerated values.
   */
  XSLTAttributeDef(String namespace, String name, boolean required,
                   String k1, int v1,
                   String k2, int v2,
                   String k3, int v3,
                   String k4, int v4)
  {
    this.m_namespace = namespace;
    this.m_name = name;
    this.m_type = this.T_ENUM;
    this.m_required = required;
    m_enums = new StringToIntTable(4);
    m_enums.put(k1, v1);
    m_enums.put(k2, v2);
    m_enums.put(k3, v3);
    m_enums.put(k4, v4);
  }

  static final int 
    T_CDATA = 1,
    
    // <!-- Used for the type of an attribute value that is a URI reference.-->
    T_URL = 2,
    
    // <!-- Used for the type of an attribute value that is an
    // attribute value template.-->
    T_AVT = 3, // Attribute Value Template
    
    // <!-- Used for the type of an attribute value that is a pattern.-->
    T_PATTERN = 4,
    
    // <!-- Used for the type of an attribute value that is an expression.-->
    T_EXPR = 5,
    
    // <!-- Used for the type of an attribute value that consists
    // of a single character.-->
    T_CHAR = 6, 
    
    // <!-- Used for the type of an attribute value that is a priority. -->
    T_PRIORITY = 7,
    
    // Used for boolean values
    T_YESNO = 8,
    
    // <!-- Used for the type of an attribute value that is a QName; the prefix
    // gets expanded by the XSLT processor. -->
    T_QNAME = 9,
    
    // <!-- Like qname but a whitespace-separated list of QNames. -->
    T_QNAMES = 10,
    
    // <!-- Used for enumerated values -->
    T_ENUM = 11,
    
    // Used for simple match patterns, i.e. xsl:strip-space spec.
    T_SIMPLEPATTERNLIST = 12,
    
    // Used for a known token.
    T_NMTOKEN = 13,

    // Used for a list of white-space delimited strings.
    T_STRINGLIST = 14
    ;

  static XSLTAttributeDef m_foreignAttr 
    = new XSLTAttributeDef("*", "*", XSLTAttributeDef.T_CDATA, false);

  /**
   * The allowed namespace for this element.
   */
  private String m_namespace;
  
  /**
   * Get the allowed namespace for this element.
   */
  String getNamespace() {return m_namespace; }
  
  /**
   * The name of this element.
   */
  private String m_name;
  
  /**
   * Get the name of this element.
   */
  String getName() {return m_name; }
  
  /**
   * The type of this attribute value.
   */
  private int m_type;
  
  /**
   * Get the type of this attribute value.
   */
  int getType() {return m_type; }
  
  /**
   * If this element is of type T_ENUM, this will contain 
   * a map from the attribute string to the Xalan integer 
   * value.
   */
  private StringToIntTable m_enums;

  /**
   * If this element is of type T_ENUM, this will return 
   * a map from the attribute string to the Xalan integer 
   * value.
   * @param key The XSLT attribute value.
   * @exception Throws NullPointerException if m_enums is null.
   */
  private int getEnum(String key) { return m_enums.get(key); }
  
  /**
   * The default value for this attribute.
   */
  private String m_default;
  
  /**
   * Get the default value for this attribute.
   */
  String getDefault() { return m_default; }

  
  /**
   * Set the default value for this attribute.
   */
  void setDefault(String def) { m_default = def; }

  /**
   * If true, this is a required attribute.
   */
  private boolean m_required;
  
  /**
   * Get whether or not this is a required attribute.
   */
  boolean getRequired() { return m_required; }
  
  String m_setterString = null;

  /**
   * Return a string that should represent the setter method.  
   * The setter method name will be created algorithmically
   */
  public String getSetterMethodName()
  {
    if(null == m_setterString)
    {
      if(m_foreignAttr == this)
      {
        return null;
      }
      else if(m_name.equals("*"))
      {
        m_setterString = "addLiteralResultAttribute";
        return m_setterString;
      }
      StringBuffer outBuf = new StringBuffer();
      outBuf.append("set");
      int n = m_name.length();
      for(int i = 0; i < n; i++)
      {
        char c = m_name.charAt(i);
        if('-' == c)
        {
          i++;
          c = m_name.charAt(i);
          c = Character.toUpperCase(c);
        }
        else if(0 == i)
        {
          c = Character.toUpperCase(c);
        }
        outBuf.append(c);
      }
      m_setterString = outBuf.toString();
    }
    return m_setterString;
  }
  
  /**
   * Process an attribute string of type T_AVT into 
   * a AVT value.
   */
  AVT processAVT(StylesheetHandler handler,
                    String uri, String name,
                    String rawName, String value)
    throws SAXException
  {
    AVT avt = new AVT(handler, uri, name, rawName, value);
    return avt;
  }
  
  /**
   * Process an attribute string of type T_CDATA into 
   * a String value.
   */
  Object processCDATA(StylesheetHandler handler,
                      String uri, String name,
                      String rawName, String value)
    throws SAXException
  {
    return value;
  }
  
  /**
   * Process an attribute string of type T_CHAR into 
   * a Character value.
   */
  Object processCHAR(StylesheetHandler handler,
                     String uri, String name,
                     String rawName, String value)
    throws SAXException
  {  
    if(value.length() != 1)
    {
      handler.error("An XSLT attribute of type T_CHAR must be only 1 character!", null);
    }
    
    return new Character(value.charAt(0));
  }
  
  /**
   * Process an attribute string of type T_ENUM into 
   * a int value.
   */
  Object processENUM(StylesheetHandler handler,
                     String uri, String name,
                     String rawName, String value)
    throws SAXException
  {
    int enum = this.getEnum(value);
    return new Integer(enum);
  }
  
  /**
   * Process an attribute string of type T_EXPR into 
   * an XPath value.
   */
  Object processEXPR(StylesheetHandler handler,
                     String uri, String name,
                     String rawName, String value)
    throws SAXException
  {
    XPath expr = handler.createXPath(value);
    return expr;
  }
  
  /**
   * Process an attribute string of type T_NMTOKEN into 
   * a String value.
   */
  Object processNMTOKEN(StylesheetHandler handler,
                        String uri, String name,
                        String rawName, String value)
    throws SAXException
  {
    return value;
  }
  
  /**
   * Process an attribute string of type T_PATTERN into 
   * an XPath match pattern value.
   */
  Object processPATTERN(StylesheetHandler handler,
                        String uri, String name,
                        String rawName, String value)
    throws SAXException
  {
    XPath pattern = handler.createMatchPatternXPath(value);
    return pattern;
  }
  
  /**
   * Process an attribute string of type T_PRIORITY into 
   * a double value.
   */
  Object processPRIORITY(StylesheetHandler handler,
                         String uri, String name,
                         String rawName, String value)
    throws SAXException
  {
    return Double.valueOf(value);
  }
  
  /**
   * Process an attribute string of type T_QNAME into 
   * a QName value.
   */
  Object processQNAME(StylesheetHandler handler,
                      String uri, String name,
                      String rawName, String value)
    throws SAXException
  {
    return new QName(value, handler);
  }
  
  /**
   * Process an attribute string of type T_QNAMES into 
   * a vector of QNames.
   */
  Vector processQNAMES(StylesheetHandler handler,
                       String uri, String name,
                       String rawName, String value)
    throws SAXException
  {
    StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
    int nQNames = tokenizer.countTokens();
    Vector qnames = new Vector(nQNames);
    for(int i = 0; i < nQNames; i++)
    {
      qnames.addElement(new QName(tokenizer.nextToken()));
    }
    return qnames;
  }
  
  /**
   * Process an attribute string of type T_SIMPLEPATTERNLIST into 
   * a vector of XPath match patterns.
   */
  Vector processSIMPLEPATTERNLIST(StylesheetHandler handler,
                                  String uri, String name,
                                  String rawName, String value)
    throws SAXException
  {
    StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
    int nPatterns = tokenizer.countTokens();
    Vector patterns = new Vector(nPatterns);
    for(int i = 0; i < nPatterns; i++)
    {
      XPath pattern = handler.createMatchPatternXPath(tokenizer.nextToken());
      patterns.addElement(pattern);
    }
    return patterns;
  }
  
  /**
   * Process an attribute string of type T_STRINGLIST into 
   * a vector of XPath match patterns.
   */
  StringVector processSTRINGLIST(StylesheetHandler handler,
                                  String uri, String name,
                                  String rawName, String value)
    throws SAXException
  {
    StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
    int nStrings = tokenizer.countTokens();
    StringVector strings = new StringVector(nStrings);
    for(int i = 0; i < nStrings; i++)
    {
      strings.addElement(tokenizer.nextToken());
    }
    return strings;
  }
  
  /**
   * Process an attribute string of type T_URL into 
   * a URL value.
   */
  String processURL(StylesheetHandler handler,
                    String uri, String name,
                    String rawName, String value)
    throws SAXException
  {
    return SystemIDResolver.getAbsoluteURI(value, 
                                             handler.getBaseIdentifier());
  }
  
  /**
   * Process an attribute string of type T_YESNO into 
   * a Boolean value.
   */
  private Boolean processYESNO(StylesheetHandler handler,
                       String uri, String name,
                       String rawName, String value)
    throws SAXException
  {
    return new Boolean(value.equals("yes") ? true : false);
  }
  
  /**
   * Process an attribute value.
   */
  Object processValue(StylesheetHandler handler,
                      String uri, String name,
                      String rawName, String value)
    throws SAXException
  {
    int type = getType();
    Object processedValue = null;
    switch(type)
    {
    case T_AVT:
      processedValue = processAVT(handler, uri, name, rawName, value);
      break;
    case T_CDATA:
      processedValue = processCDATA(handler, uri, name, rawName, value);
      break;
    case T_CHAR:
      processedValue = processCHAR(handler, uri, name, rawName, value);
      break;
    case T_ENUM:
      processedValue = processENUM(handler, uri, name, rawName, value);
      break;
    case T_EXPR:
      processedValue = processEXPR(handler, uri, name, rawName, value);
      break;
    case T_NMTOKEN:
      processedValue = processNMTOKEN(handler, uri, name, rawName, value);
      break;
    case T_PATTERN:
      processedValue = processPATTERN(handler, uri, name, rawName, value);
      break;
    case T_PRIORITY:
      processedValue = processPRIORITY(handler, uri, name, rawName, value);
      break;
    case T_QNAME:
      processedValue = processQNAME(handler, uri, name, rawName, value);
      break;
    case T_QNAMES:
      processedValue = processQNAMES(handler, uri, name, rawName, value);
      break;
    case T_SIMPLEPATTERNLIST:
      processedValue = processSIMPLEPATTERNLIST(handler, uri, name, rawName, value);
      break;
    case T_URL:
      processedValue = processURL(handler, uri, name, rawName, value);
      break;
    case T_YESNO:
      processedValue = processYESNO(handler, uri, name, rawName, value);
      break;
    case T_STRINGLIST:
      processedValue = processSTRINGLIST(handler, uri, name, rawName, value);
      break;
    default:
    }
    return processedValue;
  }
  
  /**
   * Set the default value of an attribute.
   */
  void setDefAttrValue(StylesheetHandler handler,
                       Object elem)
    throws SAXException
  {
    setAttrValue(handler, this.getNamespace(), this.getName(), this.getName(),
                    this.getDefault(), elem);
  }
  
  /**
   * Get the primative type for the class, if there 
   * is one.  If the class is a Double, for instance, 
   * this will return double.class.  If the class is not one 
   * of the 9 primative types, it will return the same 
   * class that was passed in.
   */
  private Class getPrimativeClass(Object obj)
  {
    if(obj instanceof XPath)
      return XPath.class;
    
    Class cl = obj.getClass();
    if (cl == Double.class) 
    {
      cl = double.class;
    }
    if (cl == Float.class) 
    {
      cl = float.class;
    }
    else if (cl == Boolean.class) 
    {
      cl = boolean.class;
    }
    else if (cl == Byte.class) 
    {
      cl = byte.class;
    }
    else if (cl == Character.class) 
    {
      cl = char.class;
    }
    else if (cl == Short.class) 
    {
      cl = short.class;
    }
    else if (cl == Integer.class) 
    {
      cl = int.class;
    }
    else if (cl == Long.class) 
    {
      cl = long.class;
    }
    return cl;
  }
  
  /**
   * Set a value on an attribute.
   */
  void setAttrValue(StylesheetHandler handler,
                    String attrUri, String attrLocalName, String attrRawName,
                    String attrValue, 
                    Object elem)
    throws SAXException
  {
    String setterString = getSetterMethodName();
    
    // If this is null, then it is a foreign namespace and we 
    // do not process it.
    if(null != setterString)
    {
      Object value = processValue(handler,
                                          attrUri, attrLocalName,
                                          attrRawName, 
                                          attrValue);
      try
      {
        Method meth;
        // First try to match with the primative value.
        Class[] argTypes = new Class[] { getPrimativeClass(value) };
        try
        {
          meth = elem.getClass().getMethod(setterString, argTypes);
       }
        catch(NoSuchMethodException nsme)
        {
          Class cl = ((Object)value).getClass();
          // If this doesn't work, try it with the non-primative value;
          argTypes[0] = cl;
          meth = elem.getClass().getMethod(setterString, argTypes);
        }
        Object[] args = new Object[] { value };
        meth.invoke(elem, args);
      }
      catch(NoSuchMethodException nsme)
      {
        handler.error("Failed calling "+setterString+" method!", nsme);
      }
      catch(IllegalAccessException iae)
      {
        handler.error("Failed calling "+setterString+" method!", iae);
      }
      catch(InvocationTargetException nsme)
      {
        handler.error("Failed calling "+setterString+" method!", nsme);
      }
    }
  }

}
