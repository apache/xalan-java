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

//import org.w3c.dom.*;
import java.util.Locale;
import java.util.Date;
import java.util.TimeZone;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

import javax.xml.transform.TransformerException;
import org.apache.xml.dtm.DTM;
import org.apache.xml.utils.CharacterBlockEnumeration;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.apache.xml.utils.DateTimeObj;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.parser.Token;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * <meta name="usage" content="general"/>
 * This class represents an XPath string object, and is capable of
 * converting the string to other types, such as a number.
 */
public class XDateTime extends XObject 
{

  /** Empty string XString object */
  public static XString EMPTYSTRING = new XString("");
  
  /**
   * Construct a XDate object, with a null value.
   */
  public XDateTime()
  {
    //super(null);
  }


  /**
   * Construct a XDate object.  This constructor exists for derived classes.
   *
   * @param val Date object this will wrap.
   */
  //protected XDateTime(Object val)
  //{
  //  super(val);
  //}

  /**
   * Construct a XDate object.
   *
   * @param val Date object this will wrap.
   */
  //public XDateTime(Date val)
  //{
  //  super(val);
  //}
  
  /**
   * Construct a XDate object.
   *
   * @param val String object this will wrap.
   */
  //public XDateTime(String val)
  //{
  //  super(val);
  //}
  
  DateTimeObj m_val;
  
  /**
   * Construct a XDate object.
   *
   * @param val String object this will wrap.
   */
  public XDateTime(DateTimeObj val)
  {
    //super(val);
    m_val = val; 
  }

  /**
   * Tell that this is a CLASS_DATE.
   *
   * @return type CLASS_DATE
   */
  public int getType()
  {
    return CLASS_DATETIME;
  }

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#DATE"
   */
  public String getTypeString()
  {
    return "#DATETIME";
  }

	public Object object()
	{
		return m_val;
	}
  

  /**
   * Cast result object to a number.
   *
   * @return 0.0 if this string is null, numeric value of this string
   * or NaN
   */
  public double num()
  {
  	return new Long(m_val.getDate().getTime()).doubleValue();
  	/*
  	if (m_obj instanceof DateTimeObj)
  	return (new Long(((DateTimeObj)m_obj).getDate().getTime()).doubleValue());
  	else if (m_obj instanceof Date)
  	{
  	long time = ((Date)m_obj).getTime();
  	return (new Long(time)).doubleValue();
  	}
    else if (m_obj instanceof String)
    return xstr().toDouble();
     else
    return Double.NaN;
    */
  }

  
  /**
   * Cast result object to a boolean.
   *
   * @return True if the length of this string object is greater
   * than 0.
   */
  public boolean bool()
  {
    return str().length() > 0;
  }
  

  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public String str()
  {
  	if (m_val != null)
	  	return m_val.toString();
    else
      return "";
  }
  
  

  /**
   * Cast result object to a result tree fragment.
   *
   * @param support Xpath context to use for the conversion
   *
   * @return A document fragment with this string as a child node
   */
  public int rtf(XPathContext support)
  {

    DTM frag = support.createDocumentFragment();

    frag.appendTextChild(str());

    return frag.getDocument();
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

    String str = str();

    ch.characters(str.toCharArray(), 0, str.length());
  }
  
  /**
   * Tell if two objects are functionally equal.
   *
   * @param obj2 Object to compare this to
   *
   * @return true if the two objects are equal
   *
   * @throws javax.xml.transform.TransformerException
   */
  public boolean equals(XObject obj2)
  {

    // In order to handle the 'all' semantics of 
    // nodeset comparisons, we always call the 
    // nodeset function.
    int t = obj2.getType();
    if(XObject.CLASS_DATETIME == t ||
	              XObject.CLASS_DATE == t ||
	              XObject.CLASS_TIME == t )
	   return m_val.equals(((XDateTime)obj2).m_val);
     else
       return false;
     
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
    return !equals(obj2);
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

    int t = obj2.getType();
    if(XObject.CLASS_DATETIME == t ||
	              XObject.CLASS_DATE == t ||
	              XObject.CLASS_TIME == t )
	   return m_val.lessThan(((XDateTime)obj2).m_val);
     else
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

    int t = obj2.getType();
    if(XObject.CLASS_DATETIME == t ||
	              XObject.CLASS_DATE == t ||
	              XObject.CLASS_TIME == t )
	   return m_val.lessThanOrEqual(((XDateTime)obj2).m_val);
     else
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
    int t = obj2.getType();
    if(XObject.CLASS_DATETIME == t ||
	              XObject.CLASS_DATE == t ||
	              XObject.CLASS_TIME == t )
	  return m_val.greaterThan(((XDateTime)obj2).m_val);
    else
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

    int t = obj2.getType();
    if(XObject.CLASS_DATETIME == t ||
	              XObject.CLASS_DATE == t ||
	              XObject.CLASS_TIME == t )
	  return m_val.greaterThanOrEqual(((XDateTime)obj2).m_val);
    else
      return this.num() >= obj2.num();
  }
  
  /**
   * @see org.apache.xpath.objects.XObject#datetime()
   */
  public DateTimeObj datetime() throws TransformerException
  {
    return m_val;
  }
  

}
