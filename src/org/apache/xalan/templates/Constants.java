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
package org.apache.xalan.templates;

/**
 * <meta name="usage" content="advanced"/>
 * Primary constants used in the TransformerImpl classes.
 */
public class Constants
{

  /** NEEDSDOC Field S_XMLNAMESPACEURI, S_XSLNAMESPACEURL, S_OLDXSLNAMESPACEURL, S_XPATHNAMESPACEURL, S_XPATHNAMESPACEVERSION, S_VENDOR, S_VENDORURL, S_BUILTIN_EXTENSIONS_URL, PARSER_PATH, LIAISON_CLASS          */
  public static final String S_XMLNAMESPACEURI =
    "http://www.w3.org/XML/1998/namespace", S_XSLNAMESPACEURL =
    "http://www.w3.org/1999/XSL/Transform", S_OLDXSLNAMESPACEURL =
    "http://www.w3.org/XSL/Transform/1.0", S_XPATHNAMESPACEURL =
    "http://www.w3.org/XSL/Transform", S_XPATHNAMESPACEVERSION =
    "1.0", S_VENDOR = "Apache Software Foundation", S_VENDORURL =
    "http://xml.apache.org",

  /*
  * Special apache namespace for built-in extensions.
  */
  S_BUILTIN_EXTENSIONS_URL = "http://xml.apache.org/xslt", PARSER_PATH =
    "com/ibm/xml/parser/Parser",

  //  LIAISON_CLASS = "org.apache.xpath.DOM2Helper";
  LIAISON_CLASS = "org.apache.xalan.dtm.DTMLiaison";

  /**
   * The minimum version of XSLT supported.
   */
  public static final double XSLTVERSUPPORTED = 1.0;

  /**
   * IDs for XSL element types. These are associated
   * with the string literals in the TransformerImpl class.
   * Don't change the numbers.
   */
  public static final int ELEMNAME_UNDEFINED = -1, ELEMNAME_WITHPARAM = 2,
                          ELEMNAME_ADDATTRIBUTE = 4, ELEMNAME_ANCHOR = 22,

  //  ELEMNAME_ANCHOR_PATTERN = 23,
  ELEMNAME_APPLY_TEMPLATES = 50, ELEMNAME_USE = 34, ELEMNAME_CHILDREN = 6,
                                 ELEMNAME_CHOOSE = 37, ELEMNAME_COMMENT = 59,  // my own
                                 ELEMNAME_CONSTRUCT = 7,  // my own
                                 ELEMNAME_CONTENTS = 8, ELEMNAME_COPY = 9,
                                 ELEMNAME_COPY_OF = 74,
                                 ELEMNAME_DECIMALFORMAT = 83,
                                 ELEMNAME_DEFINEATTRIBUTESET = 40,

  //  ELEMNAME_DEFINECONSTANT = 29,
  //  ELEMNAME_DEFINEMACRO = 10,
  ELEMNAME_DEFINESCRIPT = 11, ELEMNAME_DISPLAYIF = 12,  // my own
                              ELEMNAME_EMPTY = 14, ELEMNAME_EVAL = 15,
                              ELEMNAME_EXPECTEDCHILDREN = 16,
                              ELEMNAME_EXTENSION = 54,
                              ELEMNAME_EXTENSIONHANDLER = 63,
                              ELEMNAME_FOREACH = 28, ELEMNAME_KEY = 31,
                              ELEMNAME_IF = 36, ELEMNAME_IMPORT = 26,
                              ELEMNAME_INCLUDE = 27,
                              ELEMNAME_CALLTEMPLATE = 17,
                              ELEMNAME_PARAMVARIABLE = 41,
                              ELEMNAME_NUMBER = 35, ELEMNAME_NSALIAS = 84,
                              ELEMNAME_OTHERWISE = 39, ELEMNAME_PI = 58,
                              ELEMNAME_PRESERVESPACE = 33,
                              ELEMNAME_REMOVEATTRIBUTE = 5,
                              ELEMNAME_TEMPLATE = 19, ELEMNAME_SORT = 64,
                              ELEMNAME_STRIPSPACE = 32,
                              ELEMNAME_STYLESHEET = 25, ELEMNAME_TEXT = 42,
                              ELEMNAME_VALUEOF = 30, ELEMNAME_WHEN = 38,

  // Pattern by example support  
  ELEMNAME_ROOT = 44, ELEMNAME_ANY = 45, ELEMNAME_ELEMENT = 46,
                      ELEMNAME_TARGETELEMENT = 47, ELEMNAME_ATTRIBUTE = 48,
                      ELEMNAME_TARGETATTRIBUTE = 49, ELEMNAME_URL = 52,  // my own
                      ELEMNAME_CALL = 55,  // my own

  //  ELEMNAME_WITHPARAM = 56,
  ELEMNAME_FALLBACK = 57,  // my own
  ELEMNAME_TARGETPI = 60,  // my own
  ELEMNAME_TARGETCOMMENT = 61,  // my own
  ELEMNAME_TARGETTEXT = 62,  // my own
  ELEMNAME_CSSSTYLECONVERSION = 65,  // my own
  ELEMNAME_COUNTER = 66, ELEMNAME_COUNTERS = 67,
  ELEMNAME_COUNTERINCREMENT = 68, ELEMNAME_COUNTERRESET = 69,
  ELEMNAME_COUNTERSCOPE = 71, ELEMNAME_APPLY_IMPORTS = 72,
  ELEMNAME_VARIABLE = 73, ELEMNAME_MESSAGE = 75, ELEMNAME_LOCALE = 76,
  ELEMNAME_LITERALRESULT = 77, ELEMNAME_TEXTLITERALRESULT = 78,
  ELEMNAME_EXTENSIONCALL = 79, ELEMNAME_EXTENSIONDECL = 85,
  ELEMNAME_EXTENSIONSCRIPT = 86, ELEMNAME_OUTPUT = 80,
  ELEMNAME_COMPONENT = 81, ELEMNAME_SCRIPT = 82;

  // Next free number: 87

  /**
   * Literals for XSL element names.  Note that there are more
   * names than IDs, because some names map to the same ID.
   */
  public static final String ELEMNAME_COMPONENT_STRING = "component",
                             ELEMNAME_SCRIPT_STRING = "script",
                             ELEMNAME_ARG_STRING = "arg",
                             ELEMNAME_ANCHOR_STRING = "anchor",
                             ELEMNAME_ANY_STRING = "any",  // pattern-by-example support
                             ELEMNAME_APPLY_IMPORTS_STRING = "apply-imports",
                             ELEMNAME_APPLY_TEMPLATES_STRING = "apply-templates",
                             ELEMNAME_ATTRIBUTESET_STRING = "attribute-set",
                             ELEMNAME_ATTRIBUTE_STRING = "attribute",  // pattern-by-example support
                             ELEMNAME_CALLTEMPLATEARG_STRING = "invoke-arg",
                             ELEMNAME_CALLTEMPLATE_STRING = "call-template",
                             ELEMNAME_CALL_STRING = "call",
                             ELEMNAME_CHILDREN_STRING = "children",
                             ELEMNAME_CHOOSE_STRING = "choose",
                             ELEMNAME_COMMENT_STRING = "comment",
                             ELEMNAME_CONSTRUCT_STRING = "construct",  // my own
                             ELEMNAME_CONTENTS_STRING =
                               "contents", ELEMNAME_COPY_OF_STRING =
                               "copy-of", ELEMNAME_COPY_STRING =
                               "copy", ELEMNAME_DECIMALFORMAT_STRING =
                               "decimal-format", ELEMNAME_COUNTERINCREMENT_STRING =
                               "counter-increment", ELEMNAME_COUNTERRESET_STRING =
                               "counter-reset", ELEMNAME_COUNTERSCOPE_STRING =
                               "counter-scope", ELEMNAME_COUNTERS_STRING =
                               "counters", ELEMNAME_COUNTER_STRING =
                               "counter", ELEMNAME_CSSSTYLECONVERSION_STRING =
                               "css-style-conversion", ELEMNAME_DISPLAYIF_STRING =
                               "display-if",  // my own
                             ELEMNAME_ELEMENT_STRING = "element",  // pattern-by-example support
                             ELEMNAME_EMPTY_STRING = "empty",
                             ELEMNAME_EVAL_STRING = "eval",
                             ELEMNAME_EXPECTEDCHILDREN_STRING = "expectedchildren",
                             ELEMNAME_EXTENSIONHANDLER_STRING = "code-dispatcher",
                             ELEMNAME_EXTENSION_STRING = "functions",
                             ELEMNAME_FALLBACK_STRING = "fallback",
                             ELEMNAME_FOREACH_STRING = "for-each",
                             ELEMNAME_IF_STRING = "if",
                             ELEMNAME_IMPORT_STRING = "import",
                             ELEMNAME_INCLUDE_STRING = "include",
                             ELEMNAME_KEY_STRING = "key",
                             ELEMNAME_LOCALE_STRING = "locale",
                             ELEMNAME_MESSAGE_STRING = "message",
                             ELEMNAME_NUMBER_STRING = "number",
                             ELEMNAME_NSALIAS_STRING = "namespace-alias",
                             ELEMNAME_OTHERWISE_STRING = "otherwise",
                             ELEMNAME_OUTPUT_STRING = "output",
                             ELEMNAME_PARAMVARIABLE_STRING = "param",
                             ELEMNAME_PI_OLD_STRING = "pi",
                             ELEMNAME_PI_STRING = "processing-instruction",
                             ELEMNAME_PRESERVESPACE_STRING = "preserve-space",
                             ELEMNAME_ROOT_STRING = "root",  // pattern-by-example support
                             ELEMNAME_SORT_STRING = "sort",
                             ELEMNAME_STRIPSPACE_STRING = "strip-space",
                             ELEMNAME_STYLESHEET_STRING = "stylesheet",
                             ELEMNAME_TARGETATTRIBUTE_STRING =
                               "target-attribute",  // pattern-by-example support
                             ELEMNAME_TARGETCOMMENT_STRING = "target-comment",
                             ELEMNAME_TARGETELEMENT_STRING = "target-element",  // pattern-by-example support
                             ELEMNAME_TARGETPI_STRING = "target-pi",
                             ELEMNAME_TARGETTEXT_STRING = "target-text",
                             ELEMNAME_TEMPLATE_STRING = "template",
                             ELEMNAME_TEXT_STRING = "text",
                             ELEMNAME_TRANSFORM_STRING = "transform",
                             ELEMNAME_URL_STRING = "uri",  // pattern-by-example support
                             ELEMNAME_USE_STRING = "use",
                             ELEMNAME_VALUEOF_STRING = "value-of",
                             ELEMNAME_VARIABLE_STRING = "variable",
                             ELEMNAME_WHEN_STRING = "when",
                             ELEMNAME_WITHPARAM_STRING = "with-param";

  /** NEEDSDOC Field ATTRNAME_OUTPUT_METHOD, ATTRNAME_AMOUNT, ATTRNAME_ANCESTOR, ATTRNAME_ARCHIVE, ATTRNAME_ATTRIBUTE, ATTRNAME_ATTRIBUTE_SET, ATTRNAME_CASEORDER, ATTRNAME_CLASS, ATTRNAME_CLASSID, ATTRNAME_CODEBASE, ATTRNAME_CODETYPE, ATTRNAME_CONDITION, ATTRNAME_COPYTYPE, ATTRNAME_COUNT, ATTRNAME_DATATYPE, ATTRNAME_DECIMALSEPARATOR, ATTRNAME_DEFAULT, ATTRNAME_DEFAULTSPACE, ATTRNAME_DEPTH, ATTRNAME_DIGIT, ATTRNAME_DIGITGROUPSEP, ATTRNAME_DISABLE_OUTPUT_ESCAPING, ATTRNAME_ELEMENT, ATTRNAME_ELEMENTS, ATTRNAME_EXPR, ATTRNAME_EXTENSIONELEMENTPREFIXES, ATTRNAME_FORMAT, ATTRNAME_FROM, ATTRNAME_GROUPINGSEPARATOR, ATTRNAME_GROUPINGSIZE, ATTRNAME_HREF, ATTRNAME_ID, ATTRNAME_IMPORTANCE, ATTRNAME_INDENTRESULT, ATTRNAME_INFINITY, ATTRNAME_LANG, ATTRNAME_LETTERVALUE, ATTRNAME_LEVEL, ATTRNAME_MATCH, ATTRNAME_METHOD, ATTRNAME_MINUSSIGN, ATTRNAME_MODE, ATTRNAME_NAME, ATTRNAME_NAMESPACE, ATTRNAME_NAN, ATTRNAME_NDIGITSPERGROUP, ATTRNAME_NS, ATTRNAME_ONLY, ATTRNAME_ORDER, ATTRNAME_OUTPUT_CDATA_SECTION_ELEMENTS, ATTRNAME_OUTPUT_DOCTYPE_PUBLIC, ATTRNAME_OUTPUT_DOCTYPE_SYSTEM, ATTRNAME_OUTPUT_ENCODING, ATTRNAME_OUTPUT_INDENT, ATTRNAME_OUTPUT_MEDIATYPE, ATTRNAME_OUTPUT_STANDALONE, ATTRNAME_OUTPUT_VERSION, ATTRNAME_OUTPUT_OMITXMLDECL, ATTRNAME_PATTERNSEPARATOR, ATTRNAME_PERCENT, ATTRNAME_PERMILLE, ATTRNAME_PRIORITY, ATTRNAME_REFID, ATTRNAME_RESULTNS, ATTRNAME_RESULT_PREFIX, ATTRNAME_SELECT, ATTRNAME_SEQUENCESRC, ATTRNAME_STYLE, ATTRNAME_STYLESHEET_PREFIX, ATTRNAME_TERMINATE, ATTRNAME_TEST, ATTRNAME_TOSTRING, ATTRNAME_TYPE, ATTRNAME_USE, ATTRNAME_USEATTRIBUTESETS, ATTRNAME_VALUE, ATTRNAME_VERSION, ATTRNAME_XMLNSDEF, ATTRNAME_XMLNS, ATTRNAME_XMLSPACE, ATTRNAME_ZERODIGIT, ATTRNAME_EXCLUDE_RESULT_PREFIXES          */
  public static final String ATTRNAME_OUTPUT_METHOD = "method",  // qname, 
                             ATTRNAME_AMOUNT = "amount", ATTRNAME_ANCESTOR =
                               "ancestor", ATTRNAME_ARCHIVE =
                               "archive", ATTRNAME_ATTRIBUTE =
                               "attribute", ATTRNAME_ATTRIBUTE_SET =
                               "attribute-set", ATTRNAME_CASEORDER =
                               "case-order", ATTRNAME_CLASS =
                               "class", ATTRNAME_CLASSID =
                               "classid", ATTRNAME_CODEBASE =
                               "codebase", ATTRNAME_CODETYPE =
                               "type", ATTRNAME_CONDITION =
                               "condition", ATTRNAME_COPYTYPE =
                               "copy-type", ATTRNAME_COUNT =
                               "count", ATTRNAME_DATATYPE =
                               "data-type", ATTRNAME_DECIMALSEPARATOR =
                               "decimal-separator", ATTRNAME_DEFAULT =
                               "default", ATTRNAME_DEFAULTSPACE =
                               "default-space", ATTRNAME_DEPTH =
                               "with-children", ATTRNAME_DIGIT =
                               "digit", ATTRNAME_DIGITGROUPSEP =
                               "digit-group-sep", ATTRNAME_DISABLE_OUTPUT_ESCAPING =
                               "disable-output-escaping", ATTRNAME_ELEMENT =
                               "element", ATTRNAME_ELEMENTS =
                               "elements", ATTRNAME_EXPR =
                               "expr", ATTRNAME_EXTENSIONELEMENTPREFIXES =
                               "extension-element-prefixes", ATTRNAME_FORMAT =
                               "format", ATTRNAME_FROM =
                               "from", ATTRNAME_GROUPINGSEPARATOR =
                               "grouping-separator", ATTRNAME_GROUPINGSIZE =
                               "grouping-size", ATTRNAME_HREF =
                               "href", ATTRNAME_ID =
                               "id", ATTRNAME_IMPORTANCE =
                               "importance", ATTRNAME_INDENTRESULT =
                               "indent-result", ATTRNAME_INFINITY =
                               "infinity", ATTRNAME_LANG =
                               "lang", ATTRNAME_LETTERVALUE =
                               "letter-value", ATTRNAME_LEVEL =
                               "level", ATTRNAME_MATCH =
                               "match", ATTRNAME_METHOD =
                               "calls", ATTRNAME_MINUSSIGN =
                               "minus-sign", ATTRNAME_MODE =
                               "mode", ATTRNAME_NAME =
                               "name", ATTRNAME_NAMESPACE =
                               "namespace", ATTRNAME_NAN =
                               "NaN", ATTRNAME_NDIGITSPERGROUP =
                               "n-digits-per-group", ATTRNAME_NS =
                               "ns", ATTRNAME_ONLY = "only", ATTRNAME_ORDER =
                               "order", ATTRNAME_OUTPUT_CDATA_SECTION_ELEMENTS =
                               "cdata-section-elements", ATTRNAME_OUTPUT_DOCTYPE_PUBLIC =
                               "doctype-public", ATTRNAME_OUTPUT_DOCTYPE_SYSTEM =
                               "doctype-system", ATTRNAME_OUTPUT_ENCODING =
                               "encoding", ATTRNAME_OUTPUT_INDENT =
                               "indent", ATTRNAME_OUTPUT_MEDIATYPE =
                               "media-type", ATTRNAME_OUTPUT_STANDALONE =
                               "standalone", ATTRNAME_OUTPUT_VERSION =
                               "version", ATTRNAME_OUTPUT_OMITXMLDECL =
                               "omit-xml-declaration", ATTRNAME_PATTERNSEPARATOR =
                               "pattern-separator", ATTRNAME_PERCENT =
                               "percent", ATTRNAME_PERMILLE =
                               "per-mille", ATTRNAME_PRIORITY =
                               "priority", ATTRNAME_REFID =
                               "refID", ATTRNAME_RESULTNS =
                               "result-ns", ATTRNAME_RESULT_PREFIX =
                               "result-prefix", ATTRNAME_SELECT =
                               "select", ATTRNAME_SEQUENCESRC =
                               "sequence-src", ATTRNAME_STYLE =
                               "style", ATTRNAME_STYLESHEET_PREFIX =
                               "stylesheet-prefix", ATTRNAME_TERMINATE =
                               "terminate", ATTRNAME_TEST =
                               "test", ATTRNAME_TOSTRING =
                               "to-string", ATTRNAME_TYPE =
                               "type", ATTRNAME_USE =
                               "use", ATTRNAME_USEATTRIBUTESETS =
                               "use-attribute-sets", ATTRNAME_VALUE =
                               "value", ATTRNAME_VERSION =
                               "version", ATTRNAME_XMLNSDEF =
                               "xmlns", ATTRNAME_XMLNS =
                               "xmlns:", ATTRNAME_XMLSPACE =
                               "xml:space", ATTRNAME_ZERODIGIT =
                               "zero-digit", ATTRNAME_EXCLUDE_RESULT_PREFIXES =
                               "exclude-result-prefixes";

  /** NEEDSDOC Field TATTRNAME_OUTPUT_METHOD, TATTRNAME_AMOUNT, TATTRNAME_ANCESTOR, TATTRNAME_ARCHIVE, TATTRNAME_ATTRIBUTE, TATTRNAME_ATTRIBUTE_SET, TATTRNAME_CASEORDER, TATTRNAME_CLASS, TATTRNAME_CLASSID, TATTRNAME_CODEBASE, TATTRNAME_CODETYPE, TATTRNAME_CONDITION, TATTRNAME_COPYTYPE, TATTRNAME_COUNT, TATTRNAME_DATATYPE, TATTRNAME_DEFAULT, TATTRNAME_DEFAULTSPACE, TATTRNAME_DEPTH, TATTRNAME_DIGITGROUPSEP, TATTRNAME_DISABLE_OUTPUT_ESCAPING, TATTRNAME_ELEMENT, TATTRNAME_ELEMENTS, TATTRNAME_EXPR, TATTRNAME_EXTENSIONELEMENTPREFIXES, TATTRNAME_FORMAT, TATTRNAME_FROM, TATTRNAME_GROUPINGSEPARATOR, TATTRNAME_GROUPINGSIZE, TATTRNAME_HREF, TATTRNAME_ID, TATTRNAME_IMPORTANCE, TATTRNAME_INDENTRESULT, TATTRNAME_LANG, TATTRNAME_LETTERVALUE, TATTRNAME_LEVEL, TATTRNAME_MATCH, TATTRNAME_METHOD, TATTRNAME_MODE, TATTRNAME_NAME, TATTRNAME_NAMESPACE, TATTRNAME_NDIGITSPERGROUP, TATTRNAME_NS, TATTRNAME_ONLY, TATTRNAME_ORDER, TATTRNAME_OUTPUT_CDATA_SECTION_ELEMENTS, TATTRNAME_OUTPUT_DOCTYPE_PUBLIC, TATTRNAME_OUTPUT_DOCTYPE_SYSTEM, TATTRNAME_OUTPUT_ENCODING, TATTRNAME_OUTPUT_INDENT, TATTRNAME_OUTPUT_MEDIATYPE, TATTRNAME_OUTPUT_STANDALONE, TATTRNAME_OUTPUT_VERSION, TATTRNAME_OUTPUT_OMITXMLDECL, TATTRNAME_PRIORITY, TATTRNAME_REFID, TATTRNAME_RESULTNS, TATTRNAME_SELECT, TATTRNAME_SEQUENCESRC, TATTRNAME_STYLE, TATTRNAME_TEST, TATTRNAME_TOSTRING, TATTRNAME_TYPE, TATTRNAME_USE, TATTRNAME_USEATTRIBUTESETS, TATTRNAME_VALUE, TATTRNAME_XMLNSDEF, TATTRNAME_XMLNS, TATTRNAME_XMLSPACE, TATTRNAME_EXCLUDE_RESULT_PREFIXES          */
  public static final int TATTRNAME_OUTPUT_METHOD = 1, TATTRNAME_AMOUNT = 2,
                          TATTRNAME_ANCESTOR = 3, TATTRNAME_ARCHIVE = 4,
                          TATTRNAME_ATTRIBUTE = 5,
                          TATTRNAME_ATTRIBUTE_SET = 6,
                          TATTRNAME_CASEORDER = 7, TATTRNAME_CLASS = 8,
                          TATTRNAME_CLASSID = 9, TATTRNAME_CODEBASE = 10,
                          TATTRNAME_CODETYPE = 11, TATTRNAME_CONDITION = 12,
                          TATTRNAME_COPYTYPE = 13, TATTRNAME_COUNT = 14,
                          TATTRNAME_DATATYPE = 15, TATTRNAME_DEFAULT = 16,
                          TATTRNAME_DEFAULTSPACE = 17, TATTRNAME_DEPTH = 18,
                          TATTRNAME_DIGITGROUPSEP = 19,
                          TATTRNAME_DISABLE_OUTPUT_ESCAPING = 20,
                          TATTRNAME_ELEMENT = 21, TATTRNAME_ELEMENTS = 22,
                          TATTRNAME_EXPR = 23,
                          TATTRNAME_EXTENSIONELEMENTPREFIXES = 24,
                          TATTRNAME_FORMAT = 25, TATTRNAME_FROM = 26,
                          TATTRNAME_GROUPINGSEPARATOR = 27,
                          TATTRNAME_GROUPINGSIZE = 28, TATTRNAME_HREF = 29,
                          TATTRNAME_ID = 30, TATTRNAME_IMPORTANCE = 31,
                          TATTRNAME_INDENTRESULT = 32, TATTRNAME_LANG = 33,
                          TATTRNAME_LETTERVALUE = 34, TATTRNAME_LEVEL = 35,
                          TATTRNAME_MATCH = 36, TATTRNAME_METHOD = 37,
                          TATTRNAME_MODE = 38, TATTRNAME_NAME = 39,
                          TATTRNAME_NAMESPACE = 40,
                          TATTRNAME_NDIGITSPERGROUP = 41, TATTRNAME_NS = 42,
                          TATTRNAME_ONLY = 43, TATTRNAME_ORDER = 44,
                          TATTRNAME_OUTPUT_CDATA_SECTION_ELEMENTS = 45,
                          TATTRNAME_OUTPUT_DOCTYPE_PUBLIC = 46,
                          TATTRNAME_OUTPUT_DOCTYPE_SYSTEM = 47,
                          TATTRNAME_OUTPUT_ENCODING = 48,
                          TATTRNAME_OUTPUT_INDENT = 49,
                          TATTRNAME_OUTPUT_MEDIATYPE = 50,
                          TATTRNAME_OUTPUT_STANDALONE = 51,
                          TATTRNAME_OUTPUT_VERSION = 52,
                          TATTRNAME_OUTPUT_OMITXMLDECL = 53,
                          TATTRNAME_PRIORITY = 54, TATTRNAME_REFID = 55,
                          TATTRNAME_RESULTNS = 56, TATTRNAME_SELECT = 57,
                          TATTRNAME_SEQUENCESRC = 58, TATTRNAME_STYLE = 59,
                          TATTRNAME_TEST = 60, TATTRNAME_TOSTRING = 61,
                          TATTRNAME_TYPE = 62, TATTRNAME_USE = 63,
                          TATTRNAME_USEATTRIBUTESETS = 64,
                          TATTRNAME_VALUE = 65, TATTRNAME_XMLNSDEF = 66,
                          TATTRNAME_XMLNS = 67, TATTRNAME_XMLSPACE = 68,
                          TATTRNAME_EXCLUDE_RESULT_PREFIXES = 69;

  /** NEEDSDOC Field ATTRVAL_OUTPUT_METHOD_HTML, ATTRVAL_OUTPUT_METHOD_XML, ATTRVAL_OUTPUT_METHOD_TEXT          */
  public static final String ATTRVAL_OUTPUT_METHOD_HTML = "html",
                             ATTRVAL_OUTPUT_METHOD_XML = "xml",
                             ATTRVAL_OUTPUT_METHOD_TEXT = "text";

  // For space-att

  /** NEEDSDOC Field ATTRVAL_PRESERVE, ATTRVAL_STRIP          */
  public static final int ATTRVAL_PRESERVE = 1, ATTRVAL_STRIP = 2;

  // For indent-result

  /** NEEDSDOC Field ATTRVAL_YES, ATTRVAL_NO          */
  public static final boolean ATTRVAL_YES = true, ATTRVAL_NO = false;

  // For letter-value attribute (part of conversion attributes).

  /** NEEDSDOC Field ATTRVAL_ALPHABETIC, ATTRVAL_OTHER, ATTRVAL_TRADITIONAL          */
  public static final String ATTRVAL_ALPHABETIC = "alphabetic",
                             ATTRVAL_OTHER = "other",
                             ATTRVAL_TRADITIONAL = "traditional";

  // For level attribute in xsl:number.

  /** NEEDSDOC Field ATTRVAL_SINGLE, ATTRVAL_MULTI, ATTRVAL_ANY          */
  public static final String ATTRVAL_SINGLE = "single",
                             ATTRVAL_MULTI = "multiple", ATTRVAL_ANY = "any";

  // For Stylesheet-prefix and result-prefix in xsl:namespace-alias 

  /** NEEDSDOC Field ATTRVAL_DEFAULT_PREFIX          */
  public static final String ATTRVAL_DEFAULT_PREFIX = "#default";

  // Integer equivelents for above

  /** NEEDSDOC Field NUMBERLEVEL_SINGLE, NUMBERLEVEL_MULTI, NUMBERLEVEL_ANY, MAX_MULTI_COUNTING_DEPTH          */
  public static final int NUMBERLEVEL_SINGLE = 1, NUMBERLEVEL_MULTI = 2,
                          NUMBERLEVEL_ANY = 3, MAX_MULTI_COUNTING_DEPTH = 32;

  // some stuff for my patterns-by-example

  /** NEEDSDOC Field ATTRVAL_THIS, ATTRVAL_PARENT, ATTRVAL_ANCESTOR, ATTRVAL_ID          */
  public static final String ATTRVAL_THIS = ".", ATTRVAL_PARENT = "..",
                             ATTRVAL_ANCESTOR = "ancestor", ATTRVAL_ID = "id";

  // Stuff for sorting

  /** NEEDSDOC Field ATTRVAL_DATATYPE_TEXT, ATTRVAL_DATATYPE_NUMBER, ATTRVAL_ORDER_ASCENDING, ATTRVAL_ORDER_DESCENDING, ATTRVAL_CASEORDER_UPPER, ATTRVAL_CASEORDER_LOWER          */
  public static final String ATTRVAL_DATATYPE_TEXT = "text",
                             ATTRVAL_DATATYPE_NUMBER = "number",
                             ATTRVAL_ORDER_ASCENDING = "ascending",
                             ATTRVAL_ORDER_DESCENDING = "descending",
                             ATTRVAL_CASEORDER_UPPER = "upper-first",
                             ATTRVAL_CASEORDER_LOWER = "lower-first";

  // some stuff for Decimal-format

  /** NEEDSDOC Field ATTRVAL_INFINITY, ATTRVAL_NAN, DEFAULT_DECIMAL_FORMAT          */
  public static final String ATTRVAL_INFINITY = "Infinity",
                             ATTRVAL_NAN = "NaN",
                             DEFAULT_DECIMAL_FORMAT = "#default";

  // temp dummy 

  /** NEEDSDOC Field ATTRNAME_XXXX          */
  public static final String ATTRNAME_XXXX = "XXXX";

  /** NEEDSDOC Field ERROR_RESOURCES, XSLT_RESOURCE, LANG_BUNDLE_NAME, MULT_ORDER, MULT_PRECEDES, MULT_FOLLOWS, LANG_ORIENTATION, LANG_RIGHTTOLEFT, LANG_LEFTTORIGHT, LANG_NUMBERING, LANG_ADDITIVE, LANG_MULT_ADD, LANG_MULTIPLIER, LANG_MULTIPLIER_CHAR, LANG_NUMBERGROUPS, LANG_NUM_TABLES, LANG_ALPHABET, LANG_TRAD_ALPHABET          */
  public static final String ERROR_RESOURCES =
    "org.apache.xalan.res.XSLTErrorResources", XSLT_RESOURCE =
    "org.apache.xalan.res.XSLTResourceBundle", LANG_BUNDLE_NAME =
    "org.apache.xalan.res.XSLTResources", MULT_ORDER =
    "multiplierOrder", MULT_PRECEDES = "precedes", MULT_FOLLOWS =
    "follows", LANG_ORIENTATION = "orientation", LANG_RIGHTTOLEFT =
    "rightToLeft", LANG_LEFTTORIGHT = "leftToRight", LANG_NUMBERING =
    "numbering", LANG_ADDITIVE = "additive", LANG_MULT_ADD =
    "multiplicative-additive", LANG_MULTIPLIER =
    "multiplier", LANG_MULTIPLIER_CHAR =
    "multiplierChar", LANG_NUMBERGROUPS = "numberGroups", LANG_NUM_TABLES =
    "tables", LANG_ALPHABET = "alphabet", LANG_TRAD_ALPHABET = "tradAlphabet";
}
