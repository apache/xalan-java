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
public class XGYearMonth extends XDate 
{

  /**
   * Construct a XDate object, with a null value.
   */
  protected XGYearMonth()
  {
    super();
  }


  /**
   * Construct a XDate object.  This constructor exists for derived classes.
   *
   * @param val Date object this will wrap.
   */
  //protected XGYearMonth(Object val)
  //{
  //  super(val);
  //}

  /**
   * Construct a XDate object.
   *
   * @param val String object this will wrap.
   */
  //public XGYearMonth(Date val)
  //{
  //  super(val);
  //}
  
  /**
   * Construct a XDate object.
   *
   * @param val DateTimeObj object this will wrap.
   */
  public XGYearMonth(DateTimeObj val)
  {
    super(val);
  }

  /**
   * Tell that this is a CLASS_GYEARMONTH.
   *
   * @return type CLASS_GYEARMONTH
   */
  public int getType()
  {
    return CLASS_GYEARMONTH;
  }

  /**
   * Given a request type, return the equivalent string.
   * For diagnostic purposes.
   *
   * @return type string "#GYEARMONTH"
   */
  public String getTypeString()
  {
    return "#GYEARMONTH";
  }

  

  
  

  /**
   * Cast result object to a string.
   *
   * @return The string this wraps or the empty string if null
   */
  public String str()
  {
  	return super.str();
  	/*
  	if (m_obj!= null)
  	{
  		if (m_obj instanceof Date)
  		{
  	Date date = (Date) m_obj;
  	SimpleDateFormat dateFormat = new SimpleDateFormat(DateTimeObj.gym);      
    return dateFormat.format(date);
  		}
  		else if (m_obj instanceof DateTimeObj)
  		return ((DateTimeObj)m_obj).toString();
  		else if(m_obj instanceof String)
  	   return (String)m_obj;
  	}
      return "";
      */
  }
  
  

  

  

  
  
  

  

  

  
  
 
}
