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
package org.apache.xml.utils;

/**
 * <meta name="usage" content="advanced"/>
 * Primary constants used by the XSLT Processor
 */
public class Constants
{

  /** 
   * Mnemonics for standard XML Namespace URIs, as Java Strings:
   * <ul>
   * <li>S_XMLNAMESPACEURI (http://www.w3.org/XML/1998/namespace) is the
   * URI permanantly assigned to the "xml:" prefix. This is used for some
   * features built into the XML specification itself, such as xml:space 
   * and xml:lang. It was defined by the W3C's XML Namespaces spec.</li>
   * <li>S_XSLNAMESPACEURL (http://www.w3.org/1999/XSL/Transform) is the
   * URI which indicates that a name may be an XSLT directive. In most
   * XSLT stylesheets, this is bound to the "xsl:" prefix. It's defined
   * by the W3C's XSLT Recommendation.</li>
   * <li>S_OLDXSLNAMESPACEURL (http://www.w3.org/XSL/Transform/1.0) was
   * used in early prototypes of XSLT processors for much the same purpose
   * as S_XSLNAMESPACEURL. It is now considered obsolete, and the version
   * of XSLT which it signified is not fully compatable with the final
   * XSLT Recommendation, so what it really signifies is a badly obsolete
   * stylesheet.</li>
   * </ul> */
  public static final String 
	S_XMLNAMESPACEURI = "http://www.w3.org/XML/1998/namespace", 
	S_XSLNAMESPACEURL = "http://www.w3.org/1999/XSL/Transform", 
	S_OLDXSLNAMESPACEURL = "http://www.w3.org/XSL/Transform/1.0";

  /** Authorship mnemonics, as Java Strings. Not standardized, 
   * as far as I know.
   * <ul>
   * <li>S_VENDOR -- the name of the organization/individual who published
   * this XSLT processor. </li>
   * <li>S_VENDORURL -- URL where one can attempt to retrieve more
   * information about this publisher and product.</li>
   * </ul>
   */
  public static final String 
	S_VENDOR = "Apache Software Foundation", 
	S_VENDORURL = "http://xml.apache.org";

  /** S_BUILTIN_EXTENSIONS_URL is a mnemonic for the XML Namespace 
   *(http://xml.apache.org/xalan) predefined to signify Xalan's
   * built-in XSLT Extensions. When used in stylesheets, this is often 
   * bound to the "xalan:" prefix.
   */
  public static final String 
    S_BUILTIN_EXTENSIONS_URL = "http://xml.apache.org/xalan"; 

  /**
   * The old built-in extension url. It is still supported for
   * backward compatibility.
   */
  public static final String 
    S_BUILTIN_OLD_EXTENSIONS_URL = "http://xml.apache.org/xslt"; 
  
  /**
   * Xalan extension namespaces.
   */
  public static final String 
    // The old namespace for Java extension
    S_EXTENSIONS_OLD_JAVA_URL = "http://xml.apache.org/xslt/java",
    // The new namespace for Java extension
    S_EXTENSIONS_JAVA_URL = "http://xml.apache.org/xalan/java",
    S_EXTENSIONS_LOTUSXSL_JAVA_URL = "http://xsl.lotus.com/java",
    S_EXTENSIONS_XALANLIB_URL = "http://xml.apache.org/xalan",
    S_EXTENSIONS_REDIRECT_URL = "http://xml.apache.org/xalan/redirect",
    S_EXTENSIONS_PIPE_URL = "http://xml.apache.org/xalan/PipeDocument",
    S_EXTENSIONS_SQL_URL = "http://xml.apache.org/xalan/sql";
  
  /**
   * EXSLT extension namespaces.
   */
  public static final String
    S_EXSLT_COMMON_URL = "http://exslt.org/common",
    S_EXSLT_MATH_URL = "http://exslt.org/math",
    S_EXSLT_SETS_URL = "http://exslt.org/sets",
    S_EXSLT_DATETIME_URL = "http://exslt.org/dates-and-times",
    S_EXSLT_FUNCTIONS_URL = "http://exslt.org/functions",
    S_EXSLT_DYNAMIC_URL = "http://exslt.org/dynamic",
    S_EXSLT_STRINGS_URL = "http://exslt.org/strings";
    
    
  /**
   * The minimum version of XSLT supported by this processor.
   */
  public static final double XSLTVERSUPPORTED = 1.0;
}
