/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES{} LOSS OF
 * USE, DATA, OR PROFITS{} OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Santiago Pericas-Geertsen
 * @author G. Todd Miller 
 *
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.xml.transform.Result;

import org.apache.xml.res.XMLErrorResources;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.BoolStack;
import org.apache.xml.utils.Trie;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ToHTMLStream extends ToStream 
{
    /** State stack to keep track of if the current element has output 
     *  escaping disabled. 
     */
    protected final BoolStack m_isRawStack = new BoolStack();

    /** This flag is set while receiving events from the DTD */
    protected boolean m_inDTD = false;

    /** True if the current element is a block element.  (seems like 
     *  this needs to be a stack. -sb). */
    private boolean m_inBlockElem = false;

    /**
     * Map that tells which XML characters should have special treatment, and it
     *  provides character to entity name lookup.
     */
    protected static final CharInfo m_htmlcharInfo =
//        new CharInfo(CharInfo.HTML_ENTITIES_RESOURCE);
        CharInfo.getCharInfo(CharInfo.HTML_ENTITIES_RESOURCE);

    /** A digital search trie for fast, case insensitive lookup of ElemDesc objects. */
    static final Trie m_elementFlags = new Trie();

    static {

        // HTML 4.0 loose DTD
        m_elementFlags.put("BASEFONT", new ElemDesc(0 | ElemDesc.EMPTY));
        m_elementFlags.put(
            "FRAME",
            new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
        m_elementFlags.put("FRAMESET", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("NOFRAMES", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put(
            "ISINDEX",
            new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
        m_elementFlags.put(
            "APPLET",
            new ElemDesc(0 | ElemDesc.WHITESPACESENSITIVE));
        m_elementFlags.put("CENTER", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("DIR", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("MENU", new ElemDesc(0 | ElemDesc.BLOCK));

        // HTML 4.0 strict DTD
        m_elementFlags.put("TT", new ElemDesc(0 | ElemDesc.FONTSTYLE));
        m_elementFlags.put("I", new ElemDesc(0 | ElemDesc.FONTSTYLE));
        m_elementFlags.put("B", new ElemDesc(0 | ElemDesc.FONTSTYLE));
        m_elementFlags.put("BIG", new ElemDesc(0 | ElemDesc.FONTSTYLE));
        m_elementFlags.put("SMALL", new ElemDesc(0 | ElemDesc.FONTSTYLE));
        m_elementFlags.put("EM", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("STRONG", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("DFN", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("CODE", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("SAMP", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("KBD", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("VAR", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("CITE", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("ABBR", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put("ACRONYM", new ElemDesc(0 | ElemDesc.PHRASE));
        m_elementFlags.put(
            "SUP",
            new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL));
        m_elementFlags.put(
            "SUB",
            new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL));
        m_elementFlags.put(
            "SPAN",
            new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL));
        m_elementFlags.put(
            "BDO",
            new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL));
        m_elementFlags.put(
            "BR",
            new ElemDesc(
                0
                    | ElemDesc.SPECIAL
                    | ElemDesc.ASPECIAL
                    | ElemDesc.EMPTY
                    | ElemDesc.BLOCK));
        m_elementFlags.put("BODY", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put(
            "ADDRESS",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        m_elementFlags.put(
            "DIV",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        m_elementFlags.put("A", new ElemDesc(0 | ElemDesc.SPECIAL));
        m_elementFlags.put(
            "MAP",
            new ElemDesc(
                0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL | ElemDesc.BLOCK));
        m_elementFlags.put(
            "AREA",
            new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
        m_elementFlags.put(
            "LINK",
            new ElemDesc(
                0 | ElemDesc.HEADMISC | ElemDesc.EMPTY | ElemDesc.BLOCK));
        m_elementFlags.put(
            "IMG",
            new ElemDesc(
                0
                    | ElemDesc.SPECIAL
                    | ElemDesc.ASPECIAL
                    | ElemDesc.EMPTY
                    | ElemDesc.WHITESPACESENSITIVE));
        m_elementFlags.put(
            "OBJECT",
            new ElemDesc(
                0
                    | ElemDesc.SPECIAL
                    | ElemDesc.ASPECIAL
                    | ElemDesc.HEADMISC
                    | ElemDesc.WHITESPACESENSITIVE));
        m_elementFlags.put("PARAM", new ElemDesc(0 | ElemDesc.EMPTY));
        m_elementFlags.put(
            "HR",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET
                    | ElemDesc.EMPTY));
        m_elementFlags.put(
            "P",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        m_elementFlags.put(
            "H1",
            new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
        m_elementFlags.put(
            "H2",
            new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
        m_elementFlags.put(
            "H3",
            new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
        m_elementFlags.put(
            "H4",
            new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
        m_elementFlags.put(
            "H5",
            new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
        m_elementFlags.put(
            "H6",
            new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
        m_elementFlags.put(
            "PRE",
            new ElemDesc(0 | ElemDesc.PREFORMATTED | ElemDesc.BLOCK));
        m_elementFlags.put(
            "Q",
            new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL));
        m_elementFlags.put(
            "BLOCKQUOTE",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        m_elementFlags.put("INS", new ElemDesc(0));
        m_elementFlags.put("DEL", new ElemDesc(0));
        m_elementFlags.put(
            "DL",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        m_elementFlags.put("DT", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("DD", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put(
            "OL",
            new ElemDesc(0 | ElemDesc.LIST | ElemDesc.BLOCK));
        m_elementFlags.put(
            "UL",
            new ElemDesc(0 | ElemDesc.LIST | ElemDesc.BLOCK));
        m_elementFlags.put("LI", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("FORM", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("LABEL", new ElemDesc(0 | ElemDesc.FORMCTRL));
        m_elementFlags.put(
            "INPUT",
            new ElemDesc(
                0 | ElemDesc.FORMCTRL | ElemDesc.INLINELABEL | ElemDesc.EMPTY));
        m_elementFlags.put(
            "SELECT",
            new ElemDesc(0 | ElemDesc.FORMCTRL | ElemDesc.INLINELABEL));
        m_elementFlags.put("OPTGROUP", new ElemDesc(0));
        m_elementFlags.put("OPTION", new ElemDesc(0));
        m_elementFlags.put(
            "TEXTAREA",
            new ElemDesc(0 | ElemDesc.FORMCTRL | ElemDesc.INLINELABEL));
        m_elementFlags.put(
            "FIELDSET",
            new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM));
        m_elementFlags.put("LEGEND", new ElemDesc(0));
        m_elementFlags.put(
            "BUTTON",
            new ElemDesc(0 | ElemDesc.FORMCTRL | ElemDesc.INLINELABEL));
        m_elementFlags.put(
            "TABLE",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        m_elementFlags.put("CAPTION", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("THEAD", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("TFOOT", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("TBODY", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("COLGROUP", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put(
            "COL",
            new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
        m_elementFlags.put("TR", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put("TH", new ElemDesc(0));
        m_elementFlags.put("TD", new ElemDesc(0));
        m_elementFlags.put(
            "HEAD",
            new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.HEADELEM));
        m_elementFlags.put("TITLE", new ElemDesc(0 | ElemDesc.BLOCK));
        m_elementFlags.put(
            "BASE",
            new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
        m_elementFlags.put(
            "META",
            new ElemDesc(
                0 | ElemDesc.HEADMISC | ElemDesc.EMPTY | ElemDesc.BLOCK));
        m_elementFlags.put(
            "STYLE",
            new ElemDesc(
                0 | ElemDesc.HEADMISC | ElemDesc.RAW | ElemDesc.BLOCK));
        m_elementFlags.put(
            "SCRIPT",
            new ElemDesc(
                0
                    | ElemDesc.SPECIAL
                    | ElemDesc.ASPECIAL
                    | ElemDesc.HEADMISC
                    | ElemDesc.RAW));
        m_elementFlags.put(
            "NOSCRIPT",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        m_elementFlags.put("HTML", new ElemDesc(0 | ElemDesc.BLOCK));

        // From "John Ky" <hand@syd.speednet.com.au
        // Transitional Document Type Definition ()
        // file:///C:/Documents%20and%20Settings/sboag.BOAG600E/My%20Documents/html/sgml/loosedtd.html#basefont
        m_elementFlags.put("FONT", new ElemDesc(0 | ElemDesc.FONTSTYLE));

        // file:///C:/Documents%20and%20Settings/sboag.BOAG600E/My%20Documents/html/present/graphics.html#edef-STRIKE
        m_elementFlags.put("S", new ElemDesc(0 | ElemDesc.FONTSTYLE));
        m_elementFlags.put("STRIKE", new ElemDesc(0 | ElemDesc.FONTSTYLE));

        // file:///C:/Documents%20and%20Settings/sboag.BOAG600E/My%20Documents/html/present/graphics.html#edef-U
        m_elementFlags.put("U", new ElemDesc(0 | ElemDesc.FONTSTYLE));

        // From "John Ky" <hand@syd.speednet.com.au
        m_elementFlags.put("NOBR", new ElemDesc(0 | ElemDesc.FONTSTYLE));

        // HTML 4.0, section 16.5
        m_elementFlags.put(
            "IFRAME",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        // NS4 extensions
        m_elementFlags.put(
            "LAYER",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));
        m_elementFlags.put(
            "ILAYER",
            new ElemDesc(
                0
                    | ElemDesc.BLOCK
                    | ElemDesc.BLOCKFORM
                    | ElemDesc.BLOCKFORMFIELDSET));

        ElemDesc elemDesc;

        elemDesc = (ElemDesc) m_elementFlags.get("AREA");

        elemDesc.setAttr("HREF", ElemDesc.ATTRURL);
        elemDesc.setAttr("NOHREF", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("BASE");

        elemDesc.setAttr("HREF", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("BLOCKQUOTE");

        elemDesc.setAttr("CITE", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("Q");

        elemDesc.setAttr("CITE", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("INS");

        elemDesc.setAttr("CITE", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("DEL");

        elemDesc.setAttr("CITE", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("A");

        elemDesc.setAttr("HREF", ElemDesc.ATTRURL);
        elemDesc.setAttr("NAME", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("LINK");
        elemDesc.setAttr("HREF", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("INPUT");

        elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
        elemDesc.setAttr("USEMAP", ElemDesc.ATTRURL);
        elemDesc.setAttr("CHECKED", ElemDesc.ATTREMPTY);
        elemDesc.setAttr("DISABLED", ElemDesc.ATTREMPTY);
        elemDesc.setAttr("ISMAP", ElemDesc.ATTREMPTY);
        elemDesc.setAttr("READONLY", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("SELECT");

        elemDesc.setAttr("DISABLED", ElemDesc.ATTREMPTY);
        elemDesc.setAttr("MULTIPLE", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("OPTGROUP");

        elemDesc.setAttr("DISABLED", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("OPTION");

        elemDesc.setAttr("SELECTED", ElemDesc.ATTREMPTY);
        elemDesc.setAttr("DISABLED", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("TEXTAREA");

        elemDesc.setAttr("DISABLED", ElemDesc.ATTREMPTY);
        elemDesc.setAttr("READONLY", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("BUTTON");

        elemDesc.setAttr("DISABLED", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("SCRIPT");

        elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
        elemDesc.setAttr("FOR", ElemDesc.ATTRURL);
        elemDesc.setAttr("DEFER", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("IMG");

        elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
        elemDesc.setAttr("LONGDESC", ElemDesc.ATTRURL);
        elemDesc.setAttr("USEMAP", ElemDesc.ATTRURL);
        elemDesc.setAttr("ISMAP", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("OBJECT");

        elemDesc.setAttr("CLASSID", ElemDesc.ATTRURL);
        elemDesc.setAttr("CODEBASE", ElemDesc.ATTRURL);
        elemDesc.setAttr("DATA", ElemDesc.ATTRURL);
        elemDesc.setAttr("ARCHIVE", ElemDesc.ATTRURL);
        elemDesc.setAttr("USEMAP", ElemDesc.ATTRURL);
        elemDesc.setAttr("DECLARE", ElemDesc.ATTREMPTY);

        elemDesc = (ElemDesc) m_elementFlags.get("FORM");

        elemDesc.setAttr("ACTION", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("HEAD");

        elemDesc.setAttr("PROFILE", ElemDesc.ATTRURL);

        // Attribution to: "Voytenko, Dimitry" <DVoytenko@SECTORBASE.COM>
        elemDesc = (ElemDesc) m_elementFlags.get("FRAME");

        elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
        elemDesc.setAttr("LONGDESC", ElemDesc.ATTRURL);

        // HTML 4.0, section 16.5
        elemDesc = (ElemDesc) m_elementFlags.get("IFRAME");

        elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
        elemDesc.setAttr("LONGDESC", ElemDesc.ATTRURL);

        // NS4 extensions
        elemDesc = (ElemDesc) m_elementFlags.get("LAYER");

        elemDesc.setAttr("SRC", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("ILAYER");

        elemDesc.setAttr("SRC", ElemDesc.ATTRURL);

        elemDesc = (ElemDesc) m_elementFlags.get("DIV");

        elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
    }

    /**
     * Dummy element for elements not found.
     */
    static private final ElemDesc m_dummy = new ElemDesc(0 | ElemDesc.BLOCK);

    /** True if URLs should be specially escaped with the %xx form. */
    private boolean m_specialEscapeURLs = true;

    /** True if the META tag should be omitted. */
    private boolean m_omitMetaTag = false;

    /** Element description of the element currently being processed */
    private ElemDesc m_elementDesc = null;

    /**
     * Tells if the formatter should use special URL escaping.
     *
     * @param bool True if URLs should be specially escaped with the %xx form.
     */
    public void setSpecialEscapeURLs(boolean bool)
    {
        m_specialEscapeURLs = bool;
    }

    /**
     * Tells if the formatter should omit the META tag.
     *
     * @param bool True if the META tag should be omitted.
     */
    public void setOmitMetaTag(boolean bool)
    {
        m_omitMetaTag = bool;
    }

    /**
     * Specifies an output format for this serializer. It the
     * serializer has already been associated with an output format,
     * it will switch to the new format. This method should not be
     * called while the serializer is in the process of serializing
     * a document.
     *
     * @param format The output format to use
     */
    public void setOutputFormat(Properties format)
    {
 
        m_specialEscapeURLs =
            OutputPropertyUtils.getBooleanProperty(
                OutputPropertiesFactory.S_USE_URL_ESCAPING,
                format);

        m_omitMetaTag =
            OutputPropertyUtils.getBooleanProperty(
                OutputPropertiesFactory.S_OMIT_META_TAG,
                format);

        super.setOutputFormat(format);
    }

    /**
     * Tells if the formatter should use special URL escaping.
     *
     * @return True if URLs should be specially escaped with the %xx form.
     */
    private final boolean getSpecialEscapeURLs()
    {
        return m_specialEscapeURLs;
    }

    /**
     * Tells if the formatter should omit the META tag.
     *
     * @return True if the META tag should be omitted.
     */
    private final boolean getOmitMetaTag()
    {
        return m_omitMetaTag;
    }

    /**
     * Get a description of the given element.
     *
     * @param name non-null name of element, case insensitive.
     *
     * @return non-null reference to ElemDesc, which may be m_dummy if no 
     *         element description matches the given name.
     */
    public static final ElemDesc getElemDesc(String name)
    {
        /* this method used to return m_dummy  when name was null
         * but now it doesn't check and and requires non-null name.
         */
        Object obj = m_elementFlags.get(name);
        if (null != obj)
            return (ElemDesc)obj;
        return m_dummy;
    }

    /**
     * Default constructor.
     */
    public ToHTMLStream()
    {

        super();
        m_charInfo = m_htmlcharInfo;
        // initialize namespaces
        m_prefixMap = new NamespaceMappings();

    }

    /** The name of the current element. */
//    private String m_currentElementName = null;

    /**
     * Receive notification of the beginning of a document.
     *
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws org.xml.sax.SAXException
     */
    protected void startDocumentInternal() throws org.xml.sax.SAXException
    {
        super.startDocumentInternal();

        m_needToCallStartDocument = false; 
        m_needToOutputDocTypeDecl = true;
        m_startNewLine = false;
        setOmitXMLDeclaration(true);

        if (true == m_needToOutputDocTypeDecl)
        {
            String doctypeSystem = getDoctypeSystem();
            String doctypePublic = getDoctypePublic();
            if ((null != doctypeSystem) || (null != doctypePublic))
            {
                final java.io.Writer writer = m_writer;
                try
                {
                writer.write("<!DOCTYPE HTML");

                if (null != doctypePublic)
                {
                    writer.write(" PUBLIC \"");
                    writer.write(doctypePublic);
                    writer.write('"');
                }

                if (null != doctypeSystem)
                {
                    if (null == doctypePublic)
                        writer.write(" SYSTEM \"");
                    else
                        writer.write('"');

                    writer.write(doctypeSystem);
                    writer.write('"');
                }

                writer.write('>');
                outputLineSep();
                }
                catch(IOException e)
                {
                    throw new SAXException(e);
                }
            }
        }

        m_needToOutputDocTypeDecl = false;
    }

    /**
     * Receive notification of the end of a document. 
     *
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws org.xml.sax.SAXException
     */
    public final void endDocument() throws org.xml.sax.SAXException
    {
        
        flushPending();
        if (m_doIndent && !m_isprevtext)
        {
            try
            {
            outputLineSep();
            }
            catch(IOException e)
            {
                throw new SAXException(e);
            }
        }

        flushWriter();
        if (m_tracer != null)
            super.fireEndDoc();
    }

    /**
     *  Receive notification of the beginning of an element.
     *
     *
     *  @param namespaceURI
     *  @param localName
     *  @param name The element type name.
     *  @param atts The attributes attached to the element, if any.
     *  @throws org.xml.sax.SAXException Any SAX exception, possibly
     *             wrapping another exception.
     *  @see #endElement
     *  @see org.xml.sax.AttributeList
     */
    public void startElement(
        String namespaceURI,
        String localName,
        String name,
        Attributes atts)
        throws org.xml.sax.SAXException
    {

        // clean up any pending things first
        if (m_startTagOpen)
        {
            closeStartTag();
            m_startTagOpen = false;
        }
        else if (m_cdataTagOpen)
        {
            closeCDATA();
            m_cdataTagOpen = false;
        }
        else if (m_needToCallStartDocument)
        {
            startDocumentInternal();
            m_needToCallStartDocument = false;
        }


        // if this element has a namespace then treat it like XML
        if (null != namespaceURI && namespaceURI.length() > 0)
        {
            super.startElement(namespaceURI, localName, name, atts);

            return;
        }
        
        try
        {
            ElemDesc elemDesc = getElemDesc(name);
            // deal with indentation issues first
            if (m_doIndent)
            {

                boolean isBlockElement = elemDesc.is(ElemDesc.BLOCK);
                isBlockElement = elemDesc.is(ElemDesc.BLOCK);
                if (m_ispreserve)
                    m_ispreserve = false;
                else if (
                    (null != m_elementName)
                    && (!m_inBlockElem
                        || isBlockElement) /* && !isWhiteSpaceSensitive */
                    )
                {
                    m_startNewLine = true;

                    indent();

                }
                m_inBlockElem = !isBlockElement;
            }

            // save any attributes for later processing
            if (atts != null)
                addAttributes(atts);            

            // deal with the opening tag itself
            m_startTagOpen = true;
            m_elementDesc = elemDesc;
            m_isprevtext = false;
            final java.io.Writer writer = m_writer;
            writer.write('<');
            writer.write(name);
            
            
            // OPTIMIZE-EMPTY 
            if (elemDesc.is(ElemDesc.EMPTY))
            {
                // if the element is empty (has no children) then we can quit early and
                // not update all the other state information because the endElement() is
                // coming right away.  If you want to kill this optimization the corresponding
                // optimization "OPTIMIZE-EMPTY in endElement() must be killed too.
                if (m_tracer != null)
                    firePseudoAttributes();

                return;
            }

            // update any state information
            m_elementLocalName = localName;
            m_elementURI = namespaceURI;
            m_elementName = name;
            m_isRawStack.push(elemDesc.is(ElemDesc.RAW));
            m_currentElemDepth++; // current element is one element deeper

            if (m_tracer != null)
                firePseudoAttributes();

            
            if (elemDesc.is(ElemDesc.HEADELEM))
            {
                // This is the <HEAD> element, do some special processing
                if (m_startTagOpen)
                {
                    closeStartTag();
                    m_startTagOpen = false;
                }
                if (!m_omitMetaTag)
                {
                    if (m_doIndent)
                        indent();
                    writer.write(
                        "<META http-equiv=\"Content-Type\" content=\"text/html; charset=");
                    String encoding = getEncoding();
                    String encode = Encodings.getMimeEncoding(encoding);
                    writer.write(encode);
                    writer.write("\">");
                }
            }
        }
        catch (IOException e)
        {
            throw new SAXException(e);
        }
    }

    /**
     *  Receive notification of the end of an element.
     *
     *
     *  @param namespaceURI
     *  @param localName
     *  @param name The element type name
     *  @throws org.xml.sax.SAXException Any SAX exception, possibly
     *             wrapping another exception.
     */
    public final void endElement(
        final String namespaceURI,
        final String localName,
        final String name)
        throws org.xml.sax.SAXException
    {
        // deal with any pending issues
        if (m_cdataTagOpen)
            closeCDATA();

        // if the element has a namespace, treat it like XML, not HTML
        if (null != namespaceURI && namespaceURI.length() > 0)
        {
            super.endElement(namespaceURI, localName, name);

            return;
        }

        try
        {

            final ElemDesc elemDesc = getElemDesc(name);
            m_elementDesc = elemDesc;
            final boolean elemEmpty = elemDesc.is(ElemDesc.EMPTY);

            // deal with any indentation issues
            if (m_doIndent)
            {
                boolean isBlockElement = elemDesc.is(ElemDesc.BLOCK);
                boolean shouldIndent = false;

                if (m_ispreserve)
                {
                    m_ispreserve = false;
                }
                else if (m_doIndent && (!m_inBlockElem || isBlockElement))
                {
                    m_startNewLine = true;
                    shouldIndent = true;
                }
                if (!m_startTagOpen && shouldIndent)
                    indent();
                m_inBlockElem = !isBlockElement;
            }

            final java.io.Writer writer = m_writer;
            if (!m_startTagOpen)
            {
                writer.write("</");
                writer.write(name);
                writer.write('>');
            }
            else
            {
                // the start-tag was already closed.
                
                if (m_tracer != null)
                    super.fireStartElem(m_elementName);

                // the starting tag was still open when we received this endElement() call
                // so we need to process any gathered attributes NOW, before they go away.
                int nAttrs = m_attributes.getLength();
                if (nAttrs > 0)
                {
                    processAttributes(m_writer, nAttrs);
                    // clear attributes object for re-use with next element
                    m_attributes.clear();
                }
                if (!elemEmpty)
                {
                    // As per Dave/Paul recommendation 12/06/2000
                    // if (shouldIndent)
                    // writer.write('>');
                    //  indent(m_currentIndent);

                    writer.write("></");
                    writer.write(name);
                    writer.write('>');
                }
                else
                {
                    writer.write('>');
                }

                /* no need to call m_cdataSectionStates.pop();
                 * because pushCdataSectionState() was never called
                 * ... the endElement call came before we had a chance
                 * to push the state.
                 */

            }
            
            // clean up because the element has ended
            if (elemDesc.is(ElemDesc.WHITESPACESENSITIVE))
                m_ispreserve = true;
            m_isprevtext = false;
            m_elementURI = null;
            m_elementLocalName = null;

            // fire off the end element event
            if (m_tracer != null)
                super.fireEndElem(name);            
                           
            // OPTIMIZE-EMPTY                
            if (elemEmpty)
            {
                // a quick exit if the HTML element had no children.
                // This block of code can be removed if the corresponding block of code
                // in startElement() also labeled with "OPTIMIZE-EMPTY" is also removed
                m_startTagOpen = false;
                return;
            }

            // some more clean because the element has ended. 
            if (!m_startTagOpen)
            {
                if (m_cdataSectionElements != null)
                    m_cdataSectionStates.pop();
                if (m_doIndent && !m_preserves.isEmpty())
                    m_preserves.pop();
            }
            else
                m_startTagOpen = false;
            /* At this point m_startTagOpen is always false because
             * we don't have any open tags anymore, since we just 
             * wrote out a closing ">" 
             */ 

            m_currentElemDepth--;
            m_isRawStack.pop();
        }
        catch (IOException e)
        {
            throw new SAXException(e);
        }
    }

    /**
     * Process an attribute.
     * @param   writer The writer to write the processed output to.
     * @param   name   The name of the attribute.
     * @param   value   The value of the attribute.
     * @param   elemDesc The description of the HTML element 
     *           that has this attribute.
     *
     * @throws org.xml.sax.SAXException
     */
    protected void processAttribute(
        java.io.Writer writer,
        String name,
        String value,
        ElemDesc elemDesc)
        throws IOException
    {
        writer.write(' ');

        if (   ((value.length() == 0) || value.equalsIgnoreCase(name))
            && elemDesc != null 
            && elemDesc.isAttrFlagSet(name, ElemDesc.ATTREMPTY))
        {
            writer.write(name);
        }
        else
        {
            // %REVIEW% %OPT%
            // Two calls to single-char write may NOT
            // be more efficient than one to string-write...
            writer.write(name);
            writer.write("=\"");
            if (   elemDesc != null
                && elemDesc.isAttrFlagSet(name, ElemDesc.ATTRURL))
                writeAttrURI(writer, value, m_specialEscapeURLs);
            else
                writeAttrString(writer, value, this.getEncoding());
            writer.write('"');

        }
    }

    /**
     * Tell if a character is an ASCII digit.
     */
    private boolean isASCIIDigit(char c)
    {
        return (c >= '0' && c <= '9');
    }

    /**
     * Make an integer into an HH hex value.
     * Does no checking on the size of the input, since this 
     * is only meant to be used locally by writeAttrURI.
     * 
     * @param i must be a value less than 255.
     * 
     * @return should be a two character string.
     */
    private String makeHHString(int i)
    {
        String s = Integer.toHexString(i).toUpperCase();
        if (s.length() == 1)
        {
            s = "0" + s;
        }
        return s;
    }

    /**
    * Dmitri Ilyin: Makes sure if the String is HH encoded sign.
    * @param str must be 2 characters long
    *
    * @return true or false
    */
    private boolean isHHSign(String str)
    {
        boolean sign = true;
        try
        {
            char r = (char) Integer.parseInt(str, 16);
        }
        catch (NumberFormatException e)
        {
            sign = false;
        }
        return sign;
    }

    /**
     * Write the specified <var>string</var> after substituting non ASCII characters,
     * with <CODE>%HH</CODE>, where HH is the hex of the byte value.
     *
     * @param   string      String to convert to XML format.
     * @param doURLEscaping True if we should try to encode as 
     *                      per http://www.ietf.org/rfc/rfc2396.txt.
     *
     * @throws org.xml.sax.SAXException if a bad surrogate pair is detected.
     */
    public void writeAttrURI(
        final java.io.Writer writer, String string, boolean doURLEscaping)
        throws IOException
    {
        // http://www.ietf.org/rfc/rfc2396.txt says:
        // A URI is always in an "escaped" form, since escaping or unescaping a
        // completed URI might change its semantics.  Normally, the only time
        // escape encodings can safely be made is when the URI is being created
        // from its component parts; each component may have its own set of
        // characters that are reserved, so only the mechanism responsible for
        // generating or interpreting that component can determine whether or
        // not escaping a character will change its semantics. Likewise, a URI
        // must be separated into its components before the escaped characters
        // within those components can be safely decoded.
        //
        // ...So we do our best to do limited escaping of the URL, without 
        // causing damage.  If the URL is already properly escaped, in theory, this 
        // function should not change the string value.

        final char[] chars = string.toCharArray();
        final int end = chars.length;
        int cleanStart = 0;
        int cleanLength = 0;
        
        
        char ch = 0;
        for (int i = 0; i < end; i++)
        {
            ch = chars[i];

            if ((ch < 32) || (ch > 126))
            {
                if (cleanLength > 0)
                {
                    writer.write(chars, cleanStart, cleanLength);
                    cleanLength = 0;
                }
                if (doURLEscaping)
                {
                    // Encode UTF16 to UTF8.
                    // Reference is Unicode, A Primer, by Tony Graham.
                    // Page 92.

                    // Note that Kay doesn't escape 0x20...
                    //  if(ch == 0x20) // Not sure about this... -sb
                    //  {
                    //    writer.write(ch);
                    //  }
                    //  else 
                    if (ch <= 0x7F)
                    {
                        writer.write('%');
                        writer.write(makeHHString(ch));
                    }
                    else if (ch <= 0x7FF)
                    {
                        // Clear low 6 bits before rotate, put high 4 bits in low byte, 
                        // and set two high bits.
                        int high = (ch >> 6) | 0xC0;
                        int low = (ch & 0x3F) | 0x80;
                        // First 6 bits, + high bit
                        writer.write('%');
                        writer.write(makeHHString(high));
                        writer.write('%');
                        writer.write(makeHHString(low));
                    }
                    else if (isUTF16Surrogate(ch)) // high surrogate
                    {
                        // I'm sure this can be done in 3 instructions, but I choose 
                        // to try and do it exactly like it is done in the book, at least 
                        // until we are sure this is totally clean.  I don't think performance 
                        // is a big issue with this particular function, though I could be 
                        // wrong.  Also, the stuff below clearly does more masking than 
                        // it needs to do.

                        // Clear high 6 bits.
                        int highSurrogate = ((int) ch) & 0x03FF;

                        // Middle 4 bits (wwww) + 1
                        // "Note that the value of wwww from the high surrogate bit pattern
                        // is incremented to make the uuuuu bit pattern in the scalar value 
                        // so the surrogate pair don't address the BMP."
                        int wwww = ((highSurrogate & 0x03C0) >> 6);
                        int uuuuu = wwww + 1;

                        // next 4 bits
                        int zzzz = (highSurrogate & 0x003C) >> 2;

                        // low 2 bits
                        int yyyyyy = ((highSurrogate & 0x0003) << 4) & 0x30;

                        // Get low surrogate character.
                        ch = chars[++i];

                        // Clear high 6 bits.
                        int lowSurrogate = ((int) ch) & 0x03FF;

                        // put the middle 4 bits into the bottom of yyyyyy (byte 3)
                        yyyyyy = yyyyyy | ((lowSurrogate & 0x03C0) >> 6);

                        // bottom 6 bits.
                        int xxxxxx = (lowSurrogate & 0x003F);

                        int byte1 = 0xF0 | (uuuuu >> 2); // top 3 bits of uuuuu
                        int byte2 =
                            0x80 | (((uuuuu & 0x03) << 4) & 0x30) | zzzz;
                        int byte3 = 0x80 | yyyyyy;
                        int byte4 = 0x80 | xxxxxx;

                        writer.write('%');
                        writer.write(makeHHString(byte1));
                        writer.write('%');
                        writer.write(makeHHString(byte2));
                        writer.write('%');
                        writer.write(makeHHString(byte3));
                        writer.write('%');
                        writer.write(makeHHString(byte4));
                    }
                    else
                    {
                        int high = (ch >> 12) | 0xE0; // top 4 bits
                        int middle = ((ch & 0x0FC0) >> 6) | 0x80;
                        // middle 6 bits
                        int low = (ch & 0x3F) | 0x80;
                        // First 6 bits, + high bit
                        writer.write('%');
                        writer.write(makeHHString(high));
                        writer.write('%');
                        writer.write(makeHHString(middle));
                        writer.write('%');
                        writer.write(makeHHString(low));
                    }

                }
                else if (escapingNotNeeded(ch))
                {
                    writer.write(ch);
                }
                else
                {
                    writer.write("&#");
                    writer.write(Integer.toString(ch));
                    writer.write(';');
                }
                // In this character range we have first written out any previously accumulated 
                // "clean" characters, then processed the current more complicated character,
                // which may have incremented "i".
                // We now we reset the next possible clean character.
                cleanStart = i + 1;
            }
            // Since http://www.ietf.org/rfc/rfc2396.txt refers to the URI grammar as
            // not allowing quotes in the URI proper syntax, nor in the fragment 
            // identifier, we believe that it's OK to double escape quotes.
            else if (ch == '"')
            {
                // If the character is a '%' number number, try to avoid double-escaping.
                // There is a question if this is legal behavior.

                // Dmitri Ilyin: to check if '%' number number is invalid. It must be checked if %xx is a sign, that would be encoded
                // The encoded signes are in Hex form. So %xx my be in form %3C that is "<" sign. I will try to change here a little.

                //        if( ((i+2) < len) && isASCIIDigit(stringArray[i+1]) && isASCIIDigit(stringArray[i+2]) )

                // We are no longer escaping '%'

                if (cleanLength > 0)
                {
                    writer.write(chars, cleanStart, cleanLength);
                    cleanLength = 0;
                }   
                
                
                // Mike Kay encodes this as &#34;, so he may know something I don't?
                if (doURLEscaping)
                    writer.write("%22");
                else
                    writer.write("&quot;"); // we have to escape this, I guess.

                // We have written out any clean characters, then the escaped '%' and now we
                // We now we reset the next possible clean character.
                cleanStart = i + 1;    
            }
            else
            {
                // no processing for this character, just count how
                // many characters in a row that we have that need no processing
                cleanLength++;
            }
        }
        
        // are there any clean characters at the end of the array
        // that we haven't processed yet?
        if (cleanLength > 1)
        {
            // if the whole string can be written out as-is do so
            // otherwise write out the clean chars at the end of the
            // array
            if (cleanStart == 0)
                writer.write(string);
            else
                writer.write(chars, cleanStart, cleanLength);
        }
        else if (cleanLength == 1)
        {
            // a little optimization for 1 clean character
            // (we could have let the previous if(...) handle them all)
            writer.write(ch);
        }
    }

    /**
     * Writes the specified <var>string</var> after substituting <VAR>specials</VAR>,
     * and UTF-16 surrogates for character references <CODE>&amp;#xnn</CODE>.
     *
     * @param   string      String to convert to XML format.
     * @param   encoding    CURRENTLY NOT IMPLEMENTED.
     *
     * @throws org.xml.sax.SAXException
     */
    public void writeAttrString(
        final java.io.Writer writer, String string, String encoding)
        throws IOException
    {
        
        final char chars[] = string.toCharArray();
        final int end = chars.length;

        int cleanStart = 0;
        int cleanLength = 0;

        char ch = 0;
        for (int i = 0; i < end; i++)
        {
            ch = chars[i];

            // System.out.println("SPECIALSSIZE: "+SPECIALSSIZE);
            // System.out.println("ch: "+(int)ch);
            // System.out.println("m_maxCharacter: "+(int)m_maxCharacter);
            // System.out.println("m_attrCharsMap[ch]: "+(int)m_attrCharsMap[ch]);
            if (escapingNotNeeded(ch) && (!m_charInfo.isSpecial(ch)))
            {
                cleanLength++;
            }
            else if ('<' == ch || '>' == ch)
            {
                cleanLength++; // no escaping in this case, as specified in 15.2
            }
            else if (
                ('&' == ch) && ((i + 1) < end) && ('{' == chars[i + 1]))
            {
                cleanLength++; // no escaping in this case, as specified in 15.2
            }
            else
            {
                if (cleanLength > 0)
                {
                    writer.write(chars,cleanStart,cleanLength);
                    cleanLength = 0;
                }
                int pos = accumDefaultEntity(writer, ch, i, chars, end, false);

                if (i != pos)
                {
                    i = pos - 1;
                }
                else
                {
                    if (isUTF16Surrogate(ch))
                    {
 
                            i = writeUTF16Surrogate(ch, chars, i, end);

                    }

                    // The next is kind of a hack to keep from escaping in the case 
                    // of Shift_JIS and the like.

                    /*
                    else if ((ch < m_maxCharacter) && (m_maxCharacter == 0xFFFF)
                    && (ch != 160))
                    {
                    writer.write(ch);  // no escaping in this case
                    }
                    else
                    */
                    String entityName = m_charInfo.getEntityNameForChar(ch);
                    if (null != entityName)
                    {
                        writer.write('&');
                        writer.write(entityName);
                        writer.write(';');
                    }
                    else if (escapingNotNeeded(ch))
                    {
                        writer.write(ch); // no escaping in this case
                    }
                    else
                    {
                        writer.write("&#");
                        writer.write(Integer.toString(ch));
                        writer.write(';');
                    }
                }
                cleanStart = i + 1;
            }
        } // end of for()
        
        // are there any clean characters at the end of the array
        // that we haven't processed yet?
        if (cleanLength > 1)
        {
            // if the whole string can be written out as-is do so
            // otherwise write out the clean chars at the end of the
            // array
            if (cleanStart == 0)
                writer.write(string);
            else
                writer.write(chars, cleanStart, cleanLength);
        }
        else if (cleanLength == 1)
        {
            // a little optimization for 1 clean character
            // (we could have let the previous if(...) handle them all)
            writer.write(ch);
        }
    }



    /**
     * Receive notification of character data.
     *
     * <p>The Parser will call this method to report each chunk of
     * character data.  SAX parsers may return all contiguous character
     * data in a single chunk, or they may split it into several
     * chunks; however, all of the characters in any single event
     * must come from the same external entity, so that the Locator
     * provides useful information.</p>
     *
     * <p>The application must not attempt to read from the array
     * outside of the specified range.</p>
     *
     * <p>Note that some parsers will report whitespace using the
     * ignorableWhitespace() method rather than this one (validating
     * parsers must do so).</p>
     *
     * @param chars The characters from the XML document.
     * @param start The start position in the array.
     * @param length The number of characters to read from the array.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @see #ignorableWhitespace
     * @see org.xml.sax.Locator
     *
     * @throws org.xml.sax.SAXException
     */
    public final void characters(char chars[], int start, int length)
        throws org.xml.sax.SAXException
    {

        if (m_isRawStack.peekOrFalse())
        {
            try
            {
                if (m_startTagOpen)
                {
                    closeStartTag();
                    m_startTagOpen = false;
                }
                m_ispreserve = true;
                
//              With m_ispreserve just set true it looks like shouldIndent()
//              will always return false, so drop any possible indentation.
//              if (shouldIndent())
//                  indent();

                // writer.write("<![CDATA[");
                // writer.write(chars, start, length);
                writeNormalizedChars(chars, start, length, false);

                // writer.write("]]>");
                
                // time to generate characters event
                if (m_tracer != null)
                    super.fireCharEvent(chars, start, length);
                
                return;
            }
            catch (IOException ioe)
            {
                throw new org.xml.sax.SAXException(
                    XMLMessages.createXMLMessage(
                        XMLErrorResources.ER_OIERROR,
                        null),
                    ioe);
                //"IO error", ioe);
            }
        }
        else
        {
            super.characters(chars, start, length);
        }
    }

    /**
     *  Receive notification of cdata.
     *
     *  <p>The Parser will call this method to report each chunk of
     *  character data.  SAX parsers may return all contiguous character
     *  data in a single chunk, or they may split it into several
     *  chunks; however, all of the characters in any single event
     *  must come from the same external entity, so that the Locator
     *  provides useful information.</p>
     *
     *  <p>The application must not attempt to read from the array
     *  outside of the specified range.</p>
     *
     *  <p>Note that some parsers will report whitespace using the
     *  ignorableWhitespace() method rather than this one (validating
     *  parsers must do so).</p>
     *
     *  @param ch The characters from the XML document.
     *  @param start The start position in the array.
     *  @param length The number of characters to read from the array.
     *  @throws org.xml.sax.SAXException Any SAX exception, possibly
     *             wrapping another exception.
     *  @see #ignorableWhitespace
     *  @see org.xml.sax.Locator
     *
     * @throws org.xml.sax.SAXException
     */
    public final void cdata(char ch[], int start, int length)
        throws org.xml.sax.SAXException
    {

        if ((null != m_elementName)
            && (m_elementName.equalsIgnoreCase("SCRIPT")
                || m_elementName.equalsIgnoreCase("STYLE")))
        {
            try
            {
                if (m_startTagOpen)
                {
                    closeStartTag();
                    m_startTagOpen = false;
                }

                m_ispreserve = true;

                if (shouldIndent())
                    indent();

                // writer.write(ch, start, length);
                writeNormalizedChars(ch, start, length, true);
            }
            catch (IOException ioe)
            {
                throw new org.xml.sax.SAXException(
                    XMLMessages.createXMLMessage(
                        XMLErrorResources.ER_OIERROR,
                        null),
                    ioe);
                //"IO error", ioe);
            }
        }
        else
        {
            super.cdata(ch, start, length);
        }
    }

    /**
     *  Receive notification of a processing instruction.
     *
     *  @param target The processing instruction target.
     *  @param data The processing instruction data, or null if
     *         none was supplied.
     *  @throws org.xml.sax.SAXException Any SAX exception, possibly
     *             wrapping another exception.
     *
     * @throws org.xml.sax.SAXException
     */
    public void processingInstruction(String target, String data)
        throws org.xml.sax.SAXException
    {

        // Process any pending starDocument and startElement first.
        flushPending(); 
        
        // Use a fairly nasty hack to tell if the next node is supposed to be 
        // unescaped text.
        if (target.equals(Result.PI_DISABLE_OUTPUT_ESCAPING))
        {
            startNonEscaping();
        }
        else if (target.equals(Result.PI_ENABLE_OUTPUT_ESCAPING))
        {
            endNonEscaping();
        }
        else
        {
            try
            {
            if (m_startTagOpen)
            {
                closeStartTag();
                m_startTagOpen = false;
            }
            else if (m_needToCallStartDocument)
                startDocumentInternal();

            if (shouldIndent())
                indent();

            final java.io.Writer writer = m_writer;
            //writer.write("<?" + target);
            writer.write("<?");
            writer.write(target);

            if (data.length() > 0 && !Character.isSpaceChar(data.charAt(0)))
                writer.write(' '); 

            //writer.write(data + ">"); // different from XML
            writer.write(data); // different from XML
            writer.write('>'); // different from XML

            // Always output a newline char if not inside of an 
            // element. The whitespace is not significant in that
            // case.
            if (m_currentElemDepth <= 0)
                outputLineSep();

            m_startNewLine = true;
            }
            catch(IOException e)
            {
                throw new SAXException(e);
            }
        }
               
        // now generate the PI event
        if (m_tracer != null)
            super.fireEscapingEvent(target, data);
     }

    /**
     * Receive notivication of a entityReference.
     *
     * @param name non-null reference to entity name string.
     *
     * @throws org.xml.sax.SAXException
     */
    public final void entityReference(String name)
        throws org.xml.sax.SAXException
    {
        try
        {

        final java.io.Writer writer = m_writer;
        writer.write('&');
        writer.write(name);
        writer.write(';');
        
        } catch(IOException e)
        {
            throw new SAXException(e);
        }
    }
    /**
     * @see org.apache.xml.serializer.ExtendedContentHandler#endElement(String)
     */
    public final void endElement(String elemName) throws SAXException
    {
        endElement(null, null, elemName);
    }

    /**
     * Process the attributes, which means to write out the currently
     * collected attributes to the writer. The attributes are not
     * cleared by this method
     * 
     * @param writer the writer to write processed attributes to.
     * @param nAttrs the number of attributes in m_attributes 
     * to be processed
     *
     * @throws org.xml.sax.SAXException
     */
    public void processAttributes(java.io.Writer writer, int nAttrs)
        throws IOException,SAXException
    {
            /* 
             * process the collected attributes
             */
            for (int i = 0; i < nAttrs; i++)
            {
                processAttribute(
                    writer,
                    m_attributes.getQName(i),
                    m_attributes.getValue(i),
                    m_elementDesc);
            }
    }

    /**
     * For the enclosing elements starting tag write out out any attributes
     * followed by ">"
     *
     *@throws org.xml.sax.SAXException
     */
    protected void closeStartTag() throws SAXException
    {
            try
            {

            // finish processing attributes, time to fire off the start element event
            if (m_tracer != null)
                super.fireStartElem(m_elementName);  
            
            int nAttrs = m_attributes.getLength();   
            if (nAttrs>0)
            {
                processAttributes(m_writer, nAttrs);
                // clear attributes object for re-use with next element
                m_attributes.clear();
            }

            m_writer.write('>');

            /* whether Xalan or XSLTC, we have the prefix mappings now, so
             * lets determine if the current element is specified in the cdata-
             * section-elements list.
             */
            if (m_cdataSectionElements != null) 
                pushCdataSectionState();
            if (m_doIndent)
            {
                m_isprevtext = false;
                m_preserves.push(m_ispreserve);
            }
            
            }
            catch(IOException e)
            {
                throw new SAXException(e);
            }
    }
    /**
     * Initialize the serializer with the specified output stream and output
     * format. Must be called before calling any of the serialize methods.
     *
     * @param output The output stream to use
     * @param format The output format
     * @throws UnsupportedEncodingException The encoding specified   in the
     * output format is not supported
     */
    protected synchronized void init(OutputStream output, Properties format)
        throws UnsupportedEncodingException
    {
        if (null == format)
        {
            format = OutputPropertiesFactory.getDefaultMethodProperties(Method.HTML);
         }
        super.init(output,format, false);
    }
    
        /**
         * Specifies an output stream to which the document should be
         * serialized. This method should not be called while the
         * serializer is in the process of serializing a document.
         * <p>
         * The encoding specified in the output properties is used, or
         * if no encoding was specified, the default for the selected
         * output method.
         *
         * @param output The output stream
         */
        public void setOutputStream(OutputStream output)
        {

            try
            {
                Properties format;
                if (null == m_format)
                    format = OutputPropertiesFactory.getDefaultMethodProperties(Method.HTML);
                else
                    format = m_format;
                init(output, format, true);
            }
            catch (UnsupportedEncodingException uee)
            {

                // Should have been warned in init, I guess...
            }
        }    
        /**
         * This method is used when a prefix/uri namespace mapping
         * is indicated after the element was started with a
         * startElement() and before and endElement().
         * startPrefixMapping(prefix,uri) would be used before the
         * startElement() call.
         * @param uri the URI of the namespace
         * @param prefix the prefix associated with the given URI.
         *
         * @see org.apache.xml.serializer.ExtendedContentHandler#namespaceAfterStartElement(String, String)
         */
        public void namespaceAfterStartElement(String prefix, String uri)
            throws SAXException
        {
            // hack for XSLTC with finding URI for default namespace
            if (m_elementURI == null)
            {
                String prefix1 = getPrefixPart(m_elementName);
                if (prefix1 == null && EMPTYSTRING.equals(prefix))
                {
                    // the elements URI is not known yet, and it
                    // doesn't have a prefix, and we are currently
                    // setting the uri for prefix "", so we have
                    // the uri for the element... lets remember it
                    m_elementURI = uri;
                }
            }            
            startPrefixMapping(prefix,uri,false);
        }

    public void startDTD(String name, String publicId, String systemId)
        throws SAXException
    {
        m_inDTD = true;
        super.startDTD(name, publicId, systemId);
    }

    /**
     * Report the end of DTD declarations.
     * @throws org.xml.sax.SAXException The application may raise an exception.
     * @see #startDTD
     */
    public void endDTD() throws org.xml.sax.SAXException
    {
        m_inDTD = false;
        /* for ToHTMLStream the DOCTYPE is entirely output in the
         * startDocumentInternal() method, so don't do anything here
         */
    }
    /**
     * This method does nothing.
     */
    public void attributeDecl(
        String eName,
        String aName,
        String type,
        String valueDefault,
        String value)
        throws SAXException
    {
        // The internal DTD subset is not serialized by the ToHTMLStream serializer
    }

    /**
     * This method does nothing.
     */
    public void elementDecl(String name, String model) throws SAXException
    {
        // The internal DTD subset is not serialized by the ToHTMLStream serializer
    }
    /**
     * This method does nothing.
     */
    public void internalEntityDecl(String name, String value)
        throws SAXException
    {
        // The internal DTD subset is not serialized by the ToHTMLStream serializer
    }
    /**
     * This method does nothing.
     */
    public void externalEntityDecl(
        String name,
        String publicId,
        String systemId)
        throws SAXException
    {
        // The internal DTD subset is not serialized by the ToHTMLStream serializer
    }

    public void comment(char ch[], int start, int length)
            throws SAXException
    {
        // The internal DTD subset is not serialized by the ToHTMLStream serializer
        if (m_inDTD)
            return;
        super.comment(ch, start, length);
    }
    
    public boolean reset()
    {
        boolean ret = super.reset();
        if (!ret)
            return false;
        initToHTMLStream();
        return true;        
    }
    
    private void initToHTMLStream()
    {
        m_elementDesc = null;
        m_inBlockElem = false;
        m_inDTD = false;
        m_isRawStack.clear();
        m_omitMetaTag = false;
        m_specialEscapeURLs = true;     
    }
}
