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
package org.apache.xalan.serialize;

import java.util.Stack;

import java.io.Writer;
import java.io.IOException;

import java.util.Hashtable;
import java.util.Vector;

import org.xml.sax.*;

import org.apache.xml.utils.BoolStack;
import org.apache.xml.utils.Trie;
import org.apache.xalan.res.XSLMessages;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xalan.serialize.OutputFormat;
import org.apache.xalan.serialize.Method;
import org.apache.xalan.serialize.helpers.HTMLOutputFormat;
import org.apache.xml.utils.StringToIntTable;


/**
 * <meta name="usage" content="general"/>
 * FormatterToHTML formats SAX-style events into XML.
 * Warning: this class will be replaced by the Xerces Serializer classes.
 */
public class FormatterToHTML extends FormatterToXML
{
  // StringVector m_parents = new StringVector();

  /** NEEDSDOC Field m_isRawStack          */
  BoolStack m_isRawStack = new BoolStack();

  /** NEEDSDOC Field m_inBlockElem          */
  boolean m_inBlockElem = false;

  /** NEEDSDOC Field s_HTMLlat1          */
  static String[] s_HTMLlat1 = { "nbsp", "iexcl", "cent", "pound", "curren",
                                 "yen", "brvbar", "sect", "uml", "copy",
                                 "ordf", "laquo", "not", "shy", "reg", "macr",
                                 "deg", "plusmn", "sup2", "sup3", "acute",
                                 "micro", "para", "middot", "cedil", "sup1",
                                 "ordm", "raquo", "frac14", "frac12",
                                 "frac34", "iquest", "Agrave", "Aacute",
                                 "Acirc", "Atilde", "Auml", "Aring", "AElig",
                                 "Ccedil", "Egrave", "Eacute", "Ecirc",
                                 "Euml", "Igrave", "Iacute", "Icirc", "Iuml",
                                 "ETH", "Ntilde", "Ograve", "Oacute", "Ocirc",
                                 "Otilde", "Ouml", "times", "Oslash",
                                 "Ugrave", "Uacute", "Ucirc", "Uuml",
                                 "Yacute", "THORN", "szlig", "agrave",
                                 "aacute", "acirc", "atilde", "auml", "aring",
                                 "aelig", "ccedil", "egrave", "eacute",
                                 "ecirc", "euml", "igrave", "iacute", "icirc",
                                 "iuml", "eth", "ntilde", "ograve", "oacute",
                                 "ocirc", "otilde", "ouml", "divide",
                                 "oslash", "ugrave", "uacute", "ucirc",
                                 "uuml", "yacute", "thorn", "yuml" };

  /** NEEDSDOC Field HTMLsymbol1          */
  static String[] HTMLsymbol1 = { "Alpha", "Beta", "Gamma", "Delta",
                                  "Epsilon", "Zeta", "Eta", "Theta", "Iota",
                                  "Kappa", "Lambda", "Mu", "Nu", "Xi",
                                  "Omicron", "Pi", "Rho", "", "Sigma", "Tau",
                                  "Upsilon", "Phi", "Chi", "Psi", "Omega" };

  /** NEEDSDOC Field HTMLsymbol2          */
  static String[] HTMLsymbol2 = { "alpha", "beta", "gamma", "delta",
                                  "epsilon", "zeta", "eta", "theta", "iota",
                                  "kappa", "lambda", "mu", "nu", "xi",
                                  "omicron", "pi", "rho", "sigmaf", "sigma",
                                  "tau", "upsilon", "phi", "chi", "psi",
                                  "omega", "thetasym", "upsih", "piv" };

  // "fnof",
  // static   String[]  HTMLsymbol2 =    {"bull",    "hellip",    
  //                                     "prime",    "Prime",    "oline",    "frasl",    
  //                                     "weierp",    "image",    "real",    "trade",    
  //                                     "alefsym",    "larr",    "uarr",    "rarr",    
  //                                     "darr",    "harr",    "crarr",    "lArr",    
  //                                     "uArr",    "rArr",    "dArr",    "hArr",    
  //                                     "forall",    "part",    "exist",    "empty",    
  //                                     "nabla",    "isin",    "notin",    "ni",    
  //                                     "prod",    "sum",    "minus",    "lowast",    
  //                                     "radic",    "prop",    "infin",    "ang",    
  //                                     "and",    "or",    "cap",    "cup",    "int",    
  //                                     "there4",    "sim",    "cong",    "asymp",    
  //                                     "ne",    "equiv",    "le",    "ge",    "sub",    
  //                                     "sup",    "nsub",    "sube",    "supe",    
  //                                     "oplus",    "otimes",    "perp",    "sdot",    
  //                                     "lceil",    "rceil",    "lfloor",    "rfloor",    
  //                                     "lang",    "rang",    "loz",    "spades",    
  //                                      "clubs",    "hearts",    "diams"};

  /** NEEDSDOC Field m_elementFlags          */
  static Trie m_elementFlags = new Trie();

  static
  {

    // HTML 4.0 loose DTD
    m_elementFlags.put("BASEFONT", new ElemDesc(0 | ElemDesc.EMPTY));
    m_elementFlags.put("FRAME",
                       new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
    m_elementFlags.put("FRAMESET", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("NOFRAMES", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("ISINDEX",
                       new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
    m_elementFlags.put("APPLET",
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
    m_elementFlags.put("SUP",
                       new ElemDesc(0 | ElemDesc.SPECIAL
                                    | ElemDesc.ASPECIAL));
    m_elementFlags.put("SUB",
                       new ElemDesc(0 | ElemDesc.SPECIAL
                                    | ElemDesc.ASPECIAL));
    m_elementFlags.put("SPAN",
                       new ElemDesc(0 | ElemDesc.SPECIAL
                                    | ElemDesc.ASPECIAL));
    m_elementFlags.put("BDO",
                       new ElemDesc(0 | ElemDesc.SPECIAL
                                    | ElemDesc.ASPECIAL));
    m_elementFlags.put("BR",
                       new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL
                                    | ElemDesc.EMPTY | ElemDesc.BLOCK));
    m_elementFlags.put("BODY", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("ADDRESS",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM
                                    | ElemDesc.BLOCKFORMFIELDSET));
    m_elementFlags.put("DIV",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM
                                    | ElemDesc.BLOCKFORMFIELDSET));
    m_elementFlags.put("A", new ElemDesc(0 | ElemDesc.SPECIAL));
    m_elementFlags.put("MAP",
                       new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL
                                    | ElemDesc.BLOCK));
    m_elementFlags.put("AREA",
                       new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
    m_elementFlags.put("LINK",
                       new ElemDesc(0 | ElemDesc.HEADMISC | ElemDesc.EMPTY
                                    | ElemDesc.BLOCK));
    m_elementFlags.put("IMG",
                       new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL
                                    | ElemDesc.EMPTY
                                    | ElemDesc.WHITESPACESENSITIVE));
    m_elementFlags.put("OBJECT",
                       new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL
                                    | ElemDesc.HEADMISC
                                    | ElemDesc.WHITESPACESENSITIVE));
    m_elementFlags.put("PARAM", new ElemDesc(0 | ElemDesc.EMPTY));
    m_elementFlags.put("HR",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM
                                    | ElemDesc.BLOCKFORMFIELDSET
                                    | ElemDesc.EMPTY));
    m_elementFlags.put("P",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM
                                    | ElemDesc.BLOCKFORMFIELDSET));
    m_elementFlags.put("H1",
                       new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
    m_elementFlags.put("H2",
                       new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
    m_elementFlags.put("H3",
                       new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
    m_elementFlags.put("H4",
                       new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
    m_elementFlags.put("H5",
                       new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
    m_elementFlags.put("H6",
                       new ElemDesc(0 | ElemDesc.HEAD | ElemDesc.BLOCK));
    m_elementFlags.put("PRE",
                       new ElemDesc(0 | ElemDesc.PREFORMATTED
                                    | ElemDesc.BLOCK));
    m_elementFlags.put("Q",
                       new ElemDesc(0 | ElemDesc.SPECIAL
                                    | ElemDesc.ASPECIAL));
    m_elementFlags.put("BLOCKQUOTE",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM
                                    | ElemDesc.BLOCKFORMFIELDSET));
    m_elementFlags.put("INS", new ElemDesc(0));
    m_elementFlags.put("DEL", new ElemDesc(0));
    m_elementFlags.put("DL",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM
                                    | ElemDesc.BLOCKFORMFIELDSET));
    m_elementFlags.put("DT", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("DD", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("OL",
                       new ElemDesc(0 | ElemDesc.LIST | ElemDesc.BLOCK));
    m_elementFlags.put("UL",
                       new ElemDesc(0 | ElemDesc.LIST | ElemDesc.BLOCK));
    m_elementFlags.put("LI", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("FORM", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("LABEL", new ElemDesc(0 | ElemDesc.FORMCTRL));
    m_elementFlags.put("INPUT",
                       new ElemDesc(0 | ElemDesc.FORMCTRL
                                    | ElemDesc.INLINELABEL | ElemDesc.EMPTY));
    m_elementFlags.put("SELECT",
                       new ElemDesc(0 | ElemDesc.FORMCTRL
                                    | ElemDesc.INLINELABEL));
    m_elementFlags.put("OPTGROUP", new ElemDesc(0));
    m_elementFlags.put("OPTION", new ElemDesc(0));
    m_elementFlags.put("TEXTAREA",
                       new ElemDesc(0 | ElemDesc.FORMCTRL
                                    | ElemDesc.INLINELABEL));
    m_elementFlags.put("FIELDSET",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM));
    m_elementFlags.put("LEGEND", new ElemDesc(0));
    m_elementFlags.put("BUTTON",
                       new ElemDesc(0 | ElemDesc.FORMCTRL
                                    | ElemDesc.INLINELABEL));
    m_elementFlags.put("TABLE",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM
                                    | ElemDesc.BLOCKFORMFIELDSET));
    m_elementFlags.put("CAPTION", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("THEAD", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("TFOOT", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("TBODY", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("COLGROUP", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("COL",
                       new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
    m_elementFlags.put("TR", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("TH", new ElemDesc(0));
    m_elementFlags.put("TD", new ElemDesc(0));
    m_elementFlags.put("HEAD",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.HEADELEM));
    m_elementFlags.put("TITLE", new ElemDesc(0 | ElemDesc.BLOCK));
    m_elementFlags.put("BASE",
                       new ElemDesc(0 | ElemDesc.EMPTY | ElemDesc.BLOCK));
    m_elementFlags.put("META",
                       new ElemDesc(0 | ElemDesc.HEADMISC | ElemDesc.EMPTY
                                    | ElemDesc.BLOCK));
    m_elementFlags.put("STYLE",
                       new ElemDesc(0 | ElemDesc.HEADMISC | ElemDesc.RAW
                                    | ElemDesc.BLOCK));
    m_elementFlags.put("SCRIPT",
                       new ElemDesc(0 | ElemDesc.SPECIAL | ElemDesc.ASPECIAL
                                    | ElemDesc.HEADMISC | ElemDesc.RAW));
    m_elementFlags.put("NOSCRIPT",
                       new ElemDesc(0 | ElemDesc.BLOCK | ElemDesc.BLOCKFORM
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

    ElemDesc elemDesc;

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

    elemDesc = (ElemDesc) m_elementFlags.get("INPUT");

    elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
    elemDesc.setAttr("USEMAP", ElemDesc.ATTRURL);
    elemDesc.setAttr("CHECKED", ElemDesc.ATTREMPTY);
    elemDesc.setAttr("DISABLED", ElemDesc.ATTREMPTY);
    elemDesc.setAttr("READONLY", ElemDesc.ATTREMPTY);

    elemDesc = (ElemDesc) m_elementFlags.get("SELECT");

    elemDesc.setAttr("READONLY", ElemDesc.ATTREMPTY);
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

    elemDesc = (ElemDesc) m_elementFlags.get("IMG");

    elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
    elemDesc.setAttr("LONGDESC", ElemDesc.ATTRURL);
    elemDesc.setAttr("USEMAP", ElemDesc.ATTRURL);

    elemDesc = (ElemDesc) m_elementFlags.get("OBJECT");

    elemDesc.setAttr("CLASSID", ElemDesc.ATTRURL);
    elemDesc.setAttr("CODEBASE", ElemDesc.ATTRURL);
    elemDesc.setAttr("DATA", ElemDesc.ATTRURL);
    elemDesc.setAttr("ARCHIVE", ElemDesc.ATTRURL);
    elemDesc.setAttr("USEMAP", ElemDesc.ATTRURL);

    elemDesc = (ElemDesc) m_elementFlags.get("FORM");

    elemDesc.setAttr("ACTION", ElemDesc.ATTRURL);

    elemDesc = (ElemDesc) m_elementFlags.get("HEAD");

    elemDesc.setAttr("PROFILE", ElemDesc.ATTRURL);

    // Attribution to: "Voytenko, Dimitry" <DVoytenko@SECTORBASE.COM>
    elemDesc = (ElemDesc) m_elementFlags.get("FRAME");

    elemDesc.setAttr("SRC", ElemDesc.ATTRURL);
  }

  /**
   * Dummy element for elements not found.
   */
  static private ElemDesc m_dummy = new ElemDesc(0 | ElemDesc.BLOCK);

  /** NEEDSDOC Field m_specialEscapeURLs          */
  private boolean m_specialEscapeURLs = true;

  /**
   * Tells if the formatter should use special URL escaping.
   *
   * NEEDSDOC @param bool
   */
  public void setSpecialEscapeURLs(boolean bool)
  {
    m_specialEscapeURLs = bool;
  }

  /**
   * Tells if the formatter should use special URL escaping.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean getSpecialEscapeURLs()
  {
    return m_specialEscapeURLs;
  }

  /**
   * Get a description of the given element.
   *
   * NEEDSDOC @param name
   *
   * NEEDSDOC ($objectName$) @return
   */
  ElemDesc getElemDesc(String name)
  {

    if (null != name)
    {
      Object obj = m_elementFlags.get(name);

      if (null != obj)
        return (ElemDesc) obj;
    }

    return m_dummy;
  }

  /**
   * Default constructor.
   */
  public FormatterToHTML()
  {
    super();
  }

  /**
   * Constructor using a writer.
   * @param writer        The character output stream to use.
   */
  public FormatterToHTML(Writer writer)
  {
    super(writer);
  }

  /**
   * Constructor using an output stream, and a simple OutputFormat.
   * @param writer        The character output stream to use.
   *
   * NEEDSDOC @param os
   *
   * @throws java.io.UnsupportedEncodingException
   */
  public FormatterToHTML(java.io.OutputStream os)
          throws java.io.UnsupportedEncodingException
  {

    OutputFormat of = new HTMLOutputFormat("UTF-8");

    this.init(os, of);
  }

  /**
   * Constructor using a writer.
   * @param writer        The character output stream to use.
   *
   * NEEDSDOC @param xmlListener
   */
  public FormatterToHTML(FormatterToXML xmlListener)
  {

    super(xmlListener);

    m_doIndent = true;  // TODO: But what if the user wants to set it???
  }

  /**
   * Set the attribute characters what will require special mapping.
   */
  protected void initAttrCharsMap()
  {

    super.initAttrCharsMap();

    m_attrCharsMap[(int) '\n'] = 'S';

    // XSLT Spec: The html output method should not 
    // escape < characters occurring in attribute values.
    m_attrCharsMap[(int) '<'] = '\0';
    m_attrCharsMap[(int) '>'] = '\0';
    m_attrCharsMap[0x0A] = 'S';
    m_attrCharsMap[0x0D] = 'S';

    int n = (255 > SPECIALSSIZE) ? 255 : SPECIALSSIZE;

    for (int i = 160; i < n; i++)
    {
      m_attrCharsMap[i] = 'S';
    }
  }

  /**
   * Set the characters what will require special mapping.
   */
  protected void initCharsMap()
  {

    initAttrCharsMap();

    int n = (m_maxCharacter > SPECIALSSIZE) ? SPECIALSSIZE : m_maxCharacter;

    for (int i = 0; i < n; i++)
    {
      m_charsMap[i] = '\0';
    }

    m_charsMap[(int) '\n'] = 'S';
    m_charsMap[(int) '<'] = 'S';
    m_charsMap[(int) '>'] = 'S';
    m_charsMap[(int) '&'] = 'S';

    for (int i = 0; i < 10; i++)
    {
      m_charsMap[i] = 'S';
    }

    m_charsMap[0x0A] = 'S';
    m_charsMap[0x0D] = 'S';
    n = (255 > SPECIALSSIZE) ? 255 : SPECIALSSIZE;

    for (int i = 160; i < n; i++)
    {
      m_charsMap[i] = 'S';
    }

    for (int i = m_maxCharacter; i < SPECIALSSIZE; i++)
    {
      m_charsMap[i] = 'S';
    }
  }

  /** NEEDSDOC Field m_currentElementName          */
  String m_currentElementName = null;

  // protected boolean shouldIndent()
  // {
  //   return (!m_ispreserve && !m_isprevtext);
  // }

  /**
   * Receive notification of the beginning of a document.
   *
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startDocument() throws org.xml.sax.SAXException
  {

    m_needToOutputDocTypeDecl = true;
    m_startNewLine = false;
    m_shouldNotWriteXMLHeader = true;

    if (true == m_needToOutputDocTypeDecl)
    {
      if ((null != m_doctypeSystem) || (null != m_doctypePublic))
      {
        accum("<!DOCTYPE HTML");

        if (null != m_doctypePublic)
        {
          accum(" PUBLIC \"");
          accum(m_doctypePublic);
          accum("\"");
        }

        if (null != m_doctypeSystem)
        {
          if (null == m_doctypePublic)
            accum(" SYSTEM \"");
          else
            accum(" \"");

          accum(m_doctypeSystem);
          accum("\"");
        }

        accum(">");
        accum(m_lineSep);
      }
    }

    m_needToOutputDocTypeDecl = false;
  }

  /**
   *  Receive notification of the beginning of an element.
   * 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   *  @param name The element type name.
   *  @param atts The attributes attached to the element, if any.
   *  @exception org.xml.sax.SAXException Any SAX exception, possibly
   *             wrapping another exception.
   *  @see #endElement
   *  @see org.xml.sax.AttributeList
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          String namespaceURI, String localName, String name, Attributes atts)
            throws org.xml.sax.SAXException
  {

    boolean savedDoIndent = m_doIndent;
    boolean noLineBreak;

    writeParentTagEnd();
    pushState(namespaceURI, localName, m_format.getCDataElements(),
              m_cdataSectionStates);
    pushState(namespaceURI, localName, m_format.getNonEscapingElements(),
              m_disableOutputEscapingStates);

    String nameUpper = name.toUpperCase();
    ElemDesc elemDesc = getElemDesc(nameUpper);

    // ElemDesc parentElemDesc = getElemDesc(m_currentElementName);
    boolean isBlockElement = elemDesc.is(ElemDesc.BLOCK);
    boolean isHeadElement = elemDesc.is(ElemDesc.HEADELEM);

    // boolean isWhiteSpaceSensitive = elemDesc.is(ElemDesc.WHITESPACESENSITIVE);
    if (m_ispreserve)
      m_ispreserve = false;
    else if (m_doIndent && (null != m_currentElementName)
             && (!m_inBlockElem || isBlockElement)

    /* && !isWhiteSpaceSensitive */
    )
    {
      m_startNewLine = true;

      indent(m_currentIndent);
    }

    m_inBlockElem = !isBlockElement;

    m_isRawStack.push(elemDesc.is(ElemDesc.RAW));

    m_currentElementName = nameUpper;

    // m_parents.push(m_currentElementName);
    this.accum('<');
    this.accum(name);

    int nAttrs = atts.getLength();

    for (int i = 0; i < nAttrs; i++)
    {
      processAttribute(atts.getQName(i), elemDesc, atts.getValue(i));
    }

    // Flag the current element as not yet having any children.
    openElementForChildren();

    m_currentIndent += this.indent;
    m_isprevtext = false;
    m_doIndent = savedDoIndent;

    if (isHeadElement)
    {
      writeParentTagEnd();

      if (m_doIndent)
        indent(m_currentIndent);

      accum(
        "<META http-equiv=\"Content-Type\" content=\"text/html; charset=");

      String encoding = Encodings.getMimeEncoding(m_encoding);

      accum(encoding);
      accum('"');
      accum('>');
    }
  }

  /**
   *  Receive notification of the end of an element.
   * 
   *
   * NEEDSDOC @param namespaceURI
   * NEEDSDOC @param localName
   *  @param name The element type name
   *  @exception org.xml.sax.SAXException Any SAX exception, possibly
   *             wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String namespaceURI, String localName, String name)
          throws org.xml.sax.SAXException
  {

    m_currentIndent -= this.indent;

    // name = name.toUpperCase();
    boolean hasChildNodes = childNodesWereAdded();

    // System.out.println(m_currentElementName);
    // m_parents.pop();
    m_isRawStack.pop();

    String nameUpper = name.toUpperCase();
    ElemDesc elemDesc = getElemDesc(nameUpper);

    // ElemDesc parentElemDesc = getElemDesc(m_currentElementName);
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

      // indent(m_currentIndent);
    }

    m_inBlockElem = !isBlockElement;

    if (hasChildNodes)
    {
      if (shouldIndent)
        indent(m_currentIndent);

      this.accum("</");
      this.accum(name);
      this.accum('>');

      m_currentElementName = name;
    }
    else
    {
      if (!elemDesc.is(ElemDesc.EMPTY))
      {
        this.accum('>');

        if (shouldIndent)
          indent(m_currentIndent);

        this.accum('<');
        this.accum('/');
        this.accum(name);
        this.accum('>');
      }
      else
      {
        this.accum('>');
      }
    }

    if (elemDesc.is(ElemDesc.WHITESPACESENSITIVE))
      m_ispreserve = true;

    if (hasChildNodes)
    {
      if (!m_preserves.isEmpty())
        m_preserves.pop();
    }

    m_isprevtext = false;

    m_disableOutputEscapingStates.pop();
    m_cdataSectionStates.pop();
  }

  /**
   * Process an attribute.
   * @param   name   The name of the attribute.
   * NEEDSDOC @param elemDesc
   * @param   value   The value of the attribute.
   *
   * @throws org.xml.sax.SAXException
   */
  protected void processAttribute(
          String name, ElemDesc elemDesc, String value) throws org.xml.sax.SAXException
  {

    String nameUpper = name.toUpperCase();

    this.accum(' ');

    if (elemDesc.isAttrFlagSet(nameUpper, ElemDesc.ATTREMPTY)
            && ((value.length() == 0) || value.equalsIgnoreCase(name)))
    {
      this.accum(name);
    }
    else
    {
      this.accum(name);
      this.accum('=');
      this.accum('\"');

      if (m_specialEscapeURLs
              && elemDesc.isAttrFlagSet(nameUpper, ElemDesc.ATTRURL))
        writeAttrURI(value, this.m_encoding);
      else
        writeAttrString(value, this.m_encoding);

      this.accum('\"');
    }
  }

  /** NEEDSDOC Field MASK1          */
  static final int MASK1 = 0xFF00;

  /** NEEDSDOC Field MASK2          */
  static final int MASK2 = 0x00FF;

  /**
   * Write the specified <var>string</var> after substituting non ASCII characters,
   * with <CODE>%HH</CODE>, where HH is the hex of the byte value.
   *
   * @param   string      String to convert to XML format.
   * @param   specials    Chracters, should be represeted in chracter referenfces.
   * @param   encoding    CURRENTLY NOT IMPLEMENTED.
   * @see #backReference
   *
   * @throws org.xml.sax.SAXException
   */
  public void writeAttrURI(String string, String encoding) throws org.xml.sax.SAXException
  {

    char[] stringArray = string.toCharArray();
    int len = stringArray.length;

    for (int i = 0; i < len; i++)
    {
      char ch = stringArray[i];

      // if first 8 bytes are 0, no need to append them.
      if ((ch < 9) || (ch > 127)
              || /*(ch == '"') || -sb, as per #PDIK4L9LZY */ (ch == ' '))
      {
        int b1 = (int) ((((int) ch) & MASK1) >> 8);
        int b2 = (int) (((int) ch) & MASK2);

        if (b1 != 0)
        {
          accum("%");
          accum(Integer.toHexString(b1));
        }

        accum("%");
        accum(Integer.toHexString(b2));
      }
      else if (ch == '"')
      {
        accum('&');
        accum('q');
        accum('u');
        accum('o');
        accum('t');
        accum(';');
      }
      else
      {
        accum(ch);
      }
    }
  }

  /**
   * Writes the specified <var>string</var> after substituting <VAR>specials</VAR>,
   * and UTF-16 surrogates for character references <CODE>&amp;#xnn</CODE>.
   *
   * @param   string      String to convert to XML format.
   * @param   encoding    CURRENTLY NOT IMPLEMENTED.
   * @see #backReference
   *
   * @throws org.xml.sax.SAXException
   */
  public void writeAttrString(String string, String encoding)
          throws org.xml.sax.SAXException
  {

    final char chars[] = string.toCharArray();
    final int strLen = chars.length;

    for (int i = 0; i < strLen; i++)
    {
      char ch = chars[i];

      // System.out.println("SPECIALSSIZE: "+SPECIALSSIZE);
      // System.out.println("ch: "+(int)ch);
      // System.out.println("m_maxCharacter: "+(int)m_maxCharacter);
      // System.out.println("m_attrCharsMap[ch]: "+(int)m_attrCharsMap[ch]);
      if ((ch < SPECIALSSIZE) && (m_attrCharsMap[ch] != 'S'))
      {
        accum(ch);
      }
      else if (('&' == ch) && ((i + 1) < strLen) && ('{' == chars[i + 1]))
      {
        accum(ch);  // no escaping in this case, as specified in 15.2
      }
      else
      {
        int pos = accumDefaultEntity(ch, i, chars, strLen, false);

        if (i != pos)
        {
          i = pos - 1;
        }
        else
        {
          if (0xd800 <= ch && ch < 0xdc00)
          {

            // UTF-16 surrogate
            int next;

            if (i + 1 >= strLen)
            {
              throw new org.xml.sax.SAXException(
                XSLMessages.createXPATHMessage(
                  XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                  new Object[]{ Integer.toHexString(ch) }));  //"Invalid UTF-16 surrogate detected: "

              //+Integer.toHexString(ch)+ " ?");
            }
            else
            {
              next = chars[++i];

              if (!(0xdc00 <= next && next < 0xe000))
                throw new org.xml.sax.SAXException(
                  XSLMessages.createXPATHMessage(
                    XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                    new Object[]{
                      Integer.toHexString(ch) + " "
                      + Integer.toHexString(next) }));  //"Invalid UTF-16 surrogate detected: "

              //+Integer.toHexString(ch)+" "+Integer.toHexString(next));
              next = ((ch - 0xd800) << 10) + next - 0xdc00 + 0x00010000;
            }

            accum("&#");
            accum(Integer.toString(next));
            accum(';');

            /*} else if (null != ctbc && !ctbc.canConvert(ch)) {
            accum("&#x");
            accum(Integer.toString((int)ch, 16));
            accum(";");*/
          }

          // The next is kind of a hack to keep from escaping in the case 
          // of Shift_JIS and the like.
          /*
          else if ((ch < m_maxCharacter) && (m_maxCharacter == 0xFFFF)
          && (ch != 160))
          {
          accum(ch);  // no escaping in this case
          }
          else 
          */
          if ((ch >= 160) && (ch <= 255))
          {
            accum('&');
            accum(s_HTMLlat1[((int) ch) - 160]);
            accum(';');
          }
          else if ((ch >= 913) && (ch <= 937) && (ch != 930))
          {
            accum('&');
            accum(HTMLsymbol1[((int) ch) - 913]);
            accum(';');
          }
          else if ((ch >= 945) && (ch <= 969))
          {
            accum('&');
            accum(HTMLsymbol2[((int) ch) - 945]);
            accum(';');
          }
          else if ((ch >= 977) && (ch <= 978))
          {
            accum('&');

            // substracting the number of unused characters
            accum(HTMLsymbol2[((int) ch) - 945 - 7]);
            accum(';');
          }
          else if ((ch == 982))
          {
            accum('&');

            // substracting the number of unused characters
            accum(HTMLsymbol2[((int) ch) - 945 - 10]);
            accum(';');
          }
          else if (402 == ch)
          {
            accum("&fnof;");
          }
          else if (ch < m_maxCharacter)
          {
            accum(ch);  // no escaping in this case
          }
          else
          {
            if (ch < m_maxCharacter)
            {
              accum(ch);  // no escaping in this case
            }
            else
            {
              accum("&#");
              accum(Integer.toString(ch));
              accum(';');
            }
          }
        }
      }
    }
  }

  /**
   * NEEDSDOC Method copyEntityIntoBuf 
   *
   *
   * NEEDSDOC @param s
   * NEEDSDOC @param pos
   *
   * NEEDSDOC (copyEntityIntoBuf) @return
   *
   * @throws org.xml.sax.SAXException
   */
  private int copyEntityIntoBuf(String s, int pos) throws org.xml.sax.SAXException
  {

    int l = s.length();

    accum('&');

    for (int i = 0; i < l; i++)
    {
      accum(s.charAt(i));
    }

    accum(';');

    return pos;
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
   * @exception org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   * @see #ignorableWhitespace
   * @see org.xml.sax.Locator
   *
   * @throws org.xml.sax.SAXException
   */
  public void characters(char chars[], int start, int length)
          throws org.xml.sax.SAXException
  {

    if (0 == length)
      return;

    if (m_inCData)
    {
      cdata(chars, start, length);

      return;
    }

    if (isEscapingDisabled())
    {
      charactersRaw(chars, start, length);

      return;
    }

    if (!m_isRawStack.isEmpty() && m_isRawStack.peek())
    {
      try
      {
        writeParentTagEnd();

        m_ispreserve = true;

        if (shouldIndent())
          indent(m_currentIndent);

        // this.accum("<![CDATA[");
        // this.accum(chars, start, length);
        writeNormalizedChars(chars, start, length, false);

        // this.accum("]]>");
        return;
      }
      catch (IOException ioe)
      {
        throw new org.xml.sax.SAXException(
          XSLMessages.createXPATHMessage(
          XPATHErrorResources.ER_OIERROR, null), ioe);  //"IO error", ioe);
      }
    }

    writeParentTagEnd();

    m_ispreserve = true;

    int pos = 0;
    int end = start + length;
    int startClean = start;
    int lengthClean = 0;

    for (int i = start; i < end; i++)
    {
      char ch = chars[i];

      if ((ch < SPECIALSSIZE) && (m_charsMap[ch] != 'S'))
      {

        // accum(ch);
        lengthClean++;

        continue;
      }
      else if ((0x0A == ch) && ((i + 1) < end) && (0x0D == chars[i + 1]))
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 2;

        outputLineSep();

        i++;
      }

      if ((0x0D == ch) && ((i + 1) < end) && (0x0A == chars[i + 1]))
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 2;

        outputLineSep();

        i++;
      }
      else if (0x0D == ch)
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 2;

        outputLineSep();

        i++;
      }
      else if ('\n' == ch)
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;

        outputLineSep();
      }
      else if ('<' == ch)
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;
        pos = copyEntityIntoBuf("lt", pos);
      }
      else if ('>' == ch)
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;
        pos = copyEntityIntoBuf("gt", pos);
      }
      else if ('&' == ch)
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;
        pos = copyEntityIntoBuf("amp", pos);
      }
      else if ((ch >= 9) && (ch <= 126))
      {
        lengthClean++;

        // accum(ch);
      }
      else if ((ch >= 160) && (ch <= 255))
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;
        pos = copyEntityIntoBuf(s_HTMLlat1[((int) ch) - 160], pos);
      }
      else if ((ch >= 913) && (ch <= 937) && (ch != 930))
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;
        pos = copyEntityIntoBuf(HTMLsymbol1[((int) ch) - 913], pos);
      }
      else if ((ch >= 945) && (ch <= 969))
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;
        pos = copyEntityIntoBuf(HTMLsymbol2[((int) ch) - 945], pos);
      }
      else if ((ch >= 977) && (ch <= 978))
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;

        // subtract the unused characters 
        pos = copyEntityIntoBuf(HTMLsymbol2[((int) ch) - 945 - 7], pos);
      }
      else if ((ch == 982))
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;

        // subtract the unused characters
        pos = copyEntityIntoBuf(HTMLsymbol2[((int) ch) - 945 - 10], pos);
      }
      else if (402 == ch)
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;
        pos = copyEntityIntoBuf("fnof", pos);
      }
      else if (m_isUTF8 && (0xd800 <= ch && ch < 0xdc00))
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        // UTF-16 surrogate
        int next;

        if (i + 1 >= length)
        {
          throw new org.xml.sax.SAXException(
            XSLMessages.createXPATHMessage(
              XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
              new Object[]{ Integer.toHexString(ch) }));  //"Invalid UTF-16 surrogate detected: "

          //+Integer.toHexString(ch)+ " ?");
        }
        else
        {
          next = chars[++i];

          if (!(0xdc00 <= next && next < 0xe000))
            throw new org.xml.sax.SAXException(
              XSLMessages.createXPATHMessage(
                XPATHErrorResources.ER_INVALID_UTF16_SURROGATE,
                new Object[]{
                  Integer.toHexString(ch) + " "
                  + Integer.toHexString(next) }));  //"Invalid UTF-16 surrogate detected: "

          //+Integer.toHexString(ch)+" "+Integer.toHexString(next));
          next = ((ch - 0xd800) << 10) + next - 0xdc00 + 0x00010000;
        }

        accum('&');
        accum('#');

        String intStr = Integer.toString(next);
        int nIntStr = intStr.length();

        for (int k = 0; k < nIntStr; k++)
        {
          accum(intStr.charAt(k));
        }

        accum(';');

        startClean = i + 1;
      }
      else if ((ch >= '\u007F') && (ch <= m_maxCharacter))
      {

        // Hope this is right...
        lengthClean++;

        // accum(ch);
      }
      else
      {
        if (lengthClean > 0)
        {
          accum(chars, startClean, lengthClean);

          lengthClean = 0;
        }

        startClean = i + 1;

        accum('&');
        accum('#');

        String intStr = Integer.toString(ch);
        int nIntStr = intStr.length();

        for (int k = 0; k < nIntStr; k++)
        {
          accum(intStr.charAt(k));
        }

        accum(';');
      }
    }

    if (lengthClean > 0)
    {
      accum(chars, startClean, lengthClean);
    }

    m_isprevtext = true;
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
   *  @exception org.xml.sax.SAXException Any SAX exception, possibly
   *             wrapping another exception.
   *  @see #ignorableWhitespace
   *  @see org.xml.sax.Locator
   *
   * @throws org.xml.sax.SAXException
   */
  public void cdata(char ch[], int start, int length) throws org.xml.sax.SAXException
  {

    if ((null != m_currentElementName)
            && (m_currentElementName.equalsIgnoreCase("SCRIPT")
                || m_currentElementName.equalsIgnoreCase("STYLE")))
    {
      try
      {
        writeParentTagEnd();

        m_ispreserve = true;

        if (shouldIndent())
          indent(m_currentIndent);

        // this.accum(ch, start, length);
        writeNormalizedChars(ch, start, length, true);
      }
      catch (IOException ioe)
      {
        throw new org.xml.sax.SAXException(
          XSLMessages.createXPATHMessage(
          XPATHErrorResources.ER_OIERROR, null), ioe);  //"IO error", ioe);
      }
    }

    /*
    else if(m_stripCData) // should normally always be false
    {
      try
      {
        writeParentTagEnd();
        m_ispreserve = true;
        if (shouldIndent())
          indent(m_currentIndent);
        // this.accum("<![CDATA[");
        this.accum(ch, start, length);
        // this.accum("]]>");
      }
      catch(IOException ioe)
      {
        throw new org.xml.sax.SAXException(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_OIERROR, null),ioe); //"IO error", ioe);
      }
    }
    */
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
   *  @exception org.xml.sax.SAXException Any SAX exception, possibly
   *             wrapping another exception.
   *
   * @throws org.xml.sax.SAXException
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {

    // Use a fairly nasty hack to tell if the next node is supposed to be 
    // unescaped text.
    if (target.equals(javax.xml.transform.Result.PI_DISABLE_OUTPUT_ESCAPING))
    {
      m_disableOutputEscapingStates.setTop(true);
    }
    else
    {
      writeParentTagEnd();

      if (shouldIndent())
        indent(m_currentIndent);

      this.accum("<?" + target);

      if (data.length() > 0 &&!Character.isSpaceChar(data.charAt(0)))
        this.accum(" ");

      this.accum(data + ">");  // different from XML

      m_startNewLine = true;
    }
  }

  /**
   * Receive notivication of a entityReference.
   *
   * NEEDSDOC @param name
   *
   * @throws org.xml.sax.SAXException
   */
  public void entityReference(String name) throws org.xml.sax.SAXException
  {

    this.accum("&");
    this.accum(name);
    this.accum(";");
  }
}
