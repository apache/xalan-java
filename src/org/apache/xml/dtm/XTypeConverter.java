/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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

import java.util.Date;

import org.apache.xerces.util.URI;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.XMLString;

/**
 * This interface defines a type that can convert to the given type.
 * 
 * Created Jul 18, 2002
 * @author sboag
 */
public interface XTypeConverter
{
  //  public static final int ANYTYPE = 2;
  //  public static final int ANYSIMPLETYPE = 3;
  
  /**
   * Return a java object that's closest to the representation
   * that should be handed to an extension.
   *
   * @return The object that this class wraps
   */
  public Object object();
  
  //  public static final int STRING = 4;
  /**
   * Cast result object to a string.
   *
   * @return The object as a string
   */
  public String str();

  /**
   * Cast result object to an XString.
   *
   * @return The object as a string
   */
  public XMLString xstr();
  
  //  public static final int BOOLEAN = 5;
  /**
   * Cast result object to a float.
   *
   * @return float value.
   */
  public boolean bool();  
  
  //  public static final int BASE64BINARY = 6;
  /**
   * Cast result object to a float.
   *
   * @return float value.
   */
  public Object base64Binary();
  
  //  public static final int HEXBINARY = 7;
  /**
   * Cast result object to a float.
   *
   * @return float value.
   */
  public Object hexBinary();
  
  //  public static final int FLOAT = 8;
  /**
   * Cast result object to a float.
   *
   * @return float value.
   */
  public float floatCast();

  
  //  public static final int DECIMAL = 9;
  /**
   * Cast result object to a double (for lack of something better. -sb)
   *
   * @return 0.0
   */
  public double decimal();

  //  public static final int INTEGER = 10;
  /**
   * Cast result object to a integer.
   *
   * @return integer value.
   */
  public int integer();

  //  public static final int DOUBLE = 11;
  /**
   * Cast result object to a double.
   *
   * @return double value.
   */
  public double doubleCast();
  
  //  public static final int ANYURI = 12;
  /**
   * Cast result object to a double.
   *
   * @return URI object
   */
  public URI anyuri();
  
  //  public static final int QNAME = 13;
  /**
   * Cast result object to a double.
   *
   * @return QName object.
   */
  public QName qname();
  
  //  public static final int NOTATION = 14;
  /**
   * Cast result object to a String that representa a NOTATION.
   * TBD: I think this needs to be a special object!
   *
   * @return String object
   */
  public String notation();  // not sure what to do with this!

  //  public static final int DURATION = 15;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Object duration();  // Use myriam's Duration

  //  public static final int DATETIME = 16;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Date datetime();  // Use myriam's 
  
  //  public static final int TIME = 17;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Object time();  // Use myriam's 

  //  public static final int DATE = 18;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Date date();  // Use myriam's 

  //  public static final int GYEARMONTH = 19;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Object gYearMonth();  // Use myriam's 

  //  public static final int GYEAR = 20;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Object gYear();  // Use myriam's 

  //  public static final int GMONTHDAY = 21;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Object gMonthDay();  // Use myriam's 

  //  public static final int GDAY = 22;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Object gDay();  // Use myriam's 

  //  public static final int GMONTH = 23;
  /**
   * Cast result object to a duration.
   *
   * @return Duration object
   */
  public Object gMonth();  // Use myriam's 
}
