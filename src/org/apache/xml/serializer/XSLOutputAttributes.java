/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights reserved.
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
 * originally based on software copyright (c) 2003, International Business
 * Machines, Inc., http://www.ibm.com.  For more information on the Apache
 * Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xml.serializer;

import java.util.Vector;

/**
 * This interface has methods associated with the XSLT xsl:output attribues
 * specified in the stylesheet that effect the format of the document output.
 * 
 * In an XSLT stylesheet these attributes appear for example as:
 * <pre>
 * <xsl:output method="xml" omit-xml-declaration="no" indent="yes"/> 
 * </pre>
 * The xsl:output attributes covered in this interface are:
 * <pre>
 * version
 * encoding
 * omit-xml-declarations
 * standalone
 * doctype-public
 * doctype-system
 * cdata-section-elements
 * indent
 * media-type
 * </pre>
 * 
 * The one attribute not covered in this interface is <code>method</code> as
 * this value is implicitly chosen by the serializer that is created, for
 * example ToXMLStream vs. ToHTMLStream or another one.
 */
public interface XSLOutputAttributes
{
    /**
     * Returns the previously set value of the value to be used as the public
     * identifier in the document type declaration (DTD).
     *
     *@return the public identifier to be used in the DOCTYPE declaration in the
     * output document.
     */
    public String getDoctypePublic();
    /**
     * Returns the previously set value of the value to be used
     * as the system identifier in the document type declaration (DTD).
     * @return the system identifier to be used in the DOCTYPE declaration in
     * the output document.
     *
     */ 
    public String getDoctypeSystem();
    /**
     * @return the character encoding to be used in the output document.
     */    
    public String getEncoding();
    /**
	 * @return true if the output document should be indented to visually
	 * indicate its structure.
     */    
    public boolean getIndent();
    
    /**
     * @return the number of spaces to indent for each indentation level.
     */
    public int getIndentAmount();
    /**
     * @return the mediatype the media-type or MIME type associated with the
     * output document.
     */    
    public String getMediaType();
    /**
     * @return true if the XML declaration is to be omitted from the output
     * document.
     */    
    public boolean getOmitXMLDeclaration();
    /**
      * @return a value of "yes" if the <code>standalone</code> delaration is to
      * be included in the output document.
      */    
    public String getStandalone();
    /**
     * @return the version of the output format.
     */    
    public String getVersion();






    /**
     * Sets the value coming from the xsl:output cdata-section-elements
     * stylesheet property.
     * 
     * This sets the elements whose text elements are to be output as CDATA
     * sections.
     * @param URI_and_localNames pairs of namespace URI and local names that
     * identify elements whose text elements are to be output as CDATA sections.
     * The namespace of the local element must be the given URI to match. The
     * qName is not given because the prefix does not matter, only the namespace
     * URI to which that prefix would map matters, so the prefix itself is not
     * relevant in specifying which elements have their text to be output as
     * CDATA sections.
     */
    public void setCdataSectionElements(Vector URI_and_localNames);

    /** Set the value coming from the xsl:output doctype-public and doctype-system stylesheet properties
     * @param system the system identifier to be used in the DOCTYPE declaration
     * in the output document.
     * @param pub the public identifier to be used in the DOCTYPE declaration in
     * the output document.
     */
    public void setDoctype(String system, String pub);

    /** Set the value coming from the xsl:output doctype-public stylesheet attribute.
      * @param doctype the public identifier to be used in the DOCTYPE
      * declaration in the output document.
      */
    public void setDoctypePublic(String doctype);
    /** Set the value coming from the xsl:output doctype-system stylesheet attribute.
      * @param doctype the system identifier to be used in the DOCTYPE
      * declaration in the output document.
      */
    public void setDoctypeSystem(String doctype);
    /**
     * Sets the character encoding coming from the xsl:output encoding stylesheet attribute.
     * @param encoding the character encoding
     */
    public void setEncoding(String encoding);
    /**
     * Sets the value coming from the xsl:output indent stylesheet
     * attribute.
     * @param indent true if the output document should be indented to visually
     * indicate its structure.
     */
    public void setIndent(boolean indent);
    /**
     * Sets the value coming from the xsl:output media-type stylesheet attribute.
     * @param mediatype the media-type or MIME type associated with the output
     * document.
     */
    public void setMediaType(String mediatype);
    /**
     * Sets the value coming from the xsl:output omit-xml-declaration stylesheet attribute
     * @param b true if the XML declaration is to be omitted from the output
     * document.
     */
    public void setOmitXMLDeclaration(boolean b);
    /**
     * Sets the value coming from the xsl:output standalone stylesheet attribute.
     * @param standalone a value of "yes" indicates that the
     * <code>standalone</code> delaration is to be included in the output
     * document.
     */
    public void setStandalone(String standalone);
    /**
     * Sets the value coming from the xsl:output version attribute.
     * @param version the version of the output format.
     */
    public void setVersion(String version);

}
