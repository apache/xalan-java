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
package javax.xml.transform.stream;

/**
 * Class OutputKeys provides string constants that can be used to set
 * output properties on the Transformer class, or to retrieve
 * output properties on either Transformer or Templates instances.
 *
 * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
 * XSL Transformations (XSLT) W3C Recommendation</a>.
 */
public class OutputKeys
{
  /**
   * method = "xml" | "html" | "text" | <var>qname-but-not-ncname</var>.
   *
   * <p>The method attribute identifies the overall method that
   * should be used for outputting the result tree.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String METHOD = "method";

  /**
   * version = <var>nmtoken</var>.
   *
   * <p><code>version</code> specifies the version of the output
   * method.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>. 
   */
  public static final String VERSION = "version";

  /**
   * encoding = <var>string</var>.
   *
   * <p><code>encoding</code> specifies the preferred character
   * encoding that the XSLT processor should use to encode sequences of
   * characters as sequences of bytes; the value of the attribute should be
   * treated case-insensitively; the value must contain only characters in
   * the range #x21 to #x7E (i.e. printable ASCII characters); the value
   * should either be a <code>charset</code> registered with the Internet
   * Assigned Numbers Authority <a href="#IANA">[IANA]</a>,
   * <a href="#RFC2278">[RFC2278]</a> or start with <code>X-</code>.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String ENCODING = "encoding";

  /**
   * omit-xml-declaration = "yes" | "no".
   *
   * <p><code>omit-xml-declaration</code> specifies whether the XSLT
   * processor should output an XML declaration; the value must be
   * <code>yes</code> or <code>no</code>.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String OMIT_XML_DECLARATION = "omit-xml-declaration";

  /**
   * standalone = "yes" | "no".
   *
   * <p><code>standalone</code> specifies whether the XSLT processor
   * should output a standalone document declaration; the value must be
   * <code>yes</code> or <code>no</code>.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String STANDALONE = "standalone";

  /**
   * doctype-public = <var>string</var>.
   *
   * <p><code>doctype-public</code> specifies the public identifier
   * to be used in the document type declaration.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String DOCTYPE_PUBLIC = "doctype-public";

  /**
   * doctype-system = <var>string</var>.
   *
   * <p><code>doctype-system</code> specifies the system identifier
   * to be used in the document type declaration.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String DOCTYPE_SYSTEM = "doctype-system";

  /**
   * cdata-section-elements = <var>qnames</var>.
   *
   * <p><code>cdata-section-elements</code> specifies a list of the
   * names of elements whose text node children should be output using
   * CDATA sections.</p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String CDATA_SECTION_ELEMENTS =
    "cdata-section-elements";

  /**
   * indent = "yes" | "no".
   *
   * <p><code>indent</code> specifies whether the XSLT processor may
   * add additional whitespace when outputting the result tree; the value
   * must be <code>yes</code> or <code>no</code>.  </p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String INDENT = "indent";

  /**
   * media-type = <var>string</var>.
   *
   * <p><code>media-type</code> specifies the media type (MIME
   * content type) of the data that results from outputting the result
   * tree; the <code>charset</code> parameter should not be specified
   * explicitly; instead, when the top-level media type is
   * <code>text</code>, a <code>charset</code> parameter should be added
   * according to the character encoding actually used by the output
   * method.  </p>
   * @see <a href="http://www.w3.org/TR/xslt#output">section 16 of the
   * XSL Transformations (XSLT) W3C Recommendation</a>.
   */
  public static final String MEDIA_TYPE = "media-type";
}
