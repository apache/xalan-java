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
package org.apache.xalan.res;

import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;

import java.text.DecimalFormat;

import org.apache.xalan.templates.Constants;
import org.apache.xalan.res.XSLResourceBundle;

/**
 * Set up error messages.
 * We build a two dimensional array of message keys and 
 * message strings. In order to add a new message here,
 * you need to first update the count of messages(MAX_CODE)or 
 * the count of warnings(MAX_WARNING). The array will be 
 * automatically filled in with the keys, but you need to 
 * fill in the actual message string. Follow the instructions
 * below.   
 */
public class XSLTErrorResources extends XSLResourceBundle 
{  

public static final String ERROR_SUFFIX = "ER";  
public static final String WARNING_SUFFIX = "WR";

public static final int MAX_CODE = 104;                  // this is needed to keep track of the number of messages          
public static final int MAX_WARNING = 25;             // this is needed to keep track of the number of warnings
public static final int MAX_OTHERS = 41;
public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING +1;

static final Object[][] contents = new Object[MAX_MESSAGES + MAX_OTHERS +1][2];

/* 
 * Now fill in the message keys.
 * This does not need to be updated. If MAX_CODE and MAX_WARNING
 * are correct, the keys will get filled in automatically with
 * the value ERxxxx (WRxxxx for warnings) where xxxx is a 
 * formatted number corresponding to the error code (i.e. ER0001).
 */ 
static 
{
  for(int i = 0; i < MAX_CODE+1; i++)
  {
    contents[i][0] = getMKey(i);
  }
  for(int i = 1; i < MAX_WARNING+1; i++)
  {
    contents[i+ MAX_CODE][0] = getWKey(i);
  }
}

/*
 * Now fill in the message text.
 * First create an int for the message code. Make sure you 
 * update MAX_CODE for error messages and MAX_WARNING for warnings
 * Then fill in the message text for that message code in the
 * array. Use the new error code as the index into the array.
 */ 

// Error messages...

public static final int ERROR0000 = 0;
static {contents[ERROR0000][1] 
          = "{0}";
}

public static final int ER_NO_CURLYBRACE = 1;
static {contents[ER_NO_CURLYBRACE][1] 
          = "Error: Can not have '{' within expression";
}

public static final int ER_ILLEGAL_ATTRIBUTE = 2;
static {contents[ER_ILLEGAL_ATTRIBUTE][1] 
          = "{0} has an illegal attribute: {1}";
}

public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;
static {contents[ER_NULL_SOURCENODE_APPLYIMPORTS][1] 
          = "sourceNode is null in xsl:apply-imports!";
}

public static final int ER_CANNOT_ADD = 4;
static {contents[ER_CANNOT_ADD][1] 
          = "Can not add {0} to {1}";
}

public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;
static {contents[ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES][1] 
          = "sourceNode is null in handleApplyTemplatesInstruction!";
}

public static final int ER_NO_NAME_ATTRIB = 6;
static {contents[ER_NO_NAME_ATTRIB][1] 
          = "{0} must have a name attribute.";
}

public static final int ER_TEMPLATE_NOT_FOUND = 7;
static {contents[ER_TEMPLATE_NOT_FOUND][1] 
          = "Could not find template named: {0}";
}

public static final int ER_CANT_RESOLVE_NAME_AVT = 8;
static {contents[ER_CANT_RESOLVE_NAME_AVT][1] 
          = "Could not resolve name AVT in xsl:call-template.";
}

public static final int ER_REQUIRES_ATTRIB = 9;
static {contents[ER_REQUIRES_ATTRIB][1] 
          = "{0} requires attribute: {1}";
}

public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;
static {contents[ER_MUST_HAVE_TEST_ATTRIB][1] 
          = "{0} must have a 'test' attribute.";
}

public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;
static {contents[ER_BAD_VAL_ON_LEVEL_ATTRIB][1] 
          = "Bad value on level attribute: {0}";
}

public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;
static {contents[ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML][1] 
          = "processing-instruction name can not be 'xml'";
}

public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;
static {contents[ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME][1] 
          = "processing-instruction name must be a valid NCName: {0}";
}

public static final int ER_NEED_MATCH_ATTRIB = 14;
static {contents[ER_NEED_MATCH_ATTRIB][1] 
          = "{0} must have a match attribute if it has a mode.";
}

public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;
static {contents[ER_NEED_NAME_OR_MATCH_ATTRIB][1] 
          = "{0} requires either a name or a match attribute.";
}

public static final int ER_CANT_RESOLVE_NSPREFIX = 16;
static {contents[ER_CANT_RESOLVE_NSPREFIX][1] 
          = "Can not resolve namespace prefix: {0}";
}

public static final int ER_ILLEGAL_VALUE = 17;
static {contents[ER_ILLEGAL_VALUE][1] 
          = "xml:space has an illegal value: {0}";
}

public static final int ER_NO_OWNERDOC = 18;
static {contents[ER_NO_OWNERDOC][1] 
          = "Child node does not have an owner document!";
}

public static final int ER_ELEMTEMPLATEELEM_ERR = 19;
static {contents[ER_ELEMTEMPLATEELEM_ERR][1] 
          = "ElemTemplateElement error: {0}";
}

public static final int ER_NULL_CHILD = 20;
static {contents[ER_NULL_CHILD][1] 
          = "Trying to add a null child!";
}

public static final int ER_NEED_SELECT_ATTRIB = 21;
static {contents[ER_NEED_SELECT_ATTRIB][1] 
          = "{0} requires a select attribute.";
}

public static final int ER_NEED_TEST_ATTRIB = 22;
static {contents[ER_NEED_TEST_ATTRIB][1] 
          = "xsl:when must have a 'test' attribute.";
}

public static final int ER_NEED_NAME_ATTRIB = 23;
static {contents[ER_NEED_NAME_ATTRIB][1] 
          = "xsl:with-param must have a 'name' attribute.";
}

public static final int ER_NO_CONTEXT_OWNERDOC = 24;
static {contents[ER_NO_CONTEXT_OWNERDOC][1] 
          = "context does not have an owner document!";
}

public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;
static {contents[ER_COULD_NOT_CREATE_XML_PROC_LIAISON][1] 
          = "Could not create XML Processor Liaison: {0}";
}

public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;
static {contents[ER_PROCESS_NOT_SUCCESSFUL][1] 
          = "Xalan: Process was not successful.";
}

public static final int ER_NOT_SUCCESSFUL = 27;
static {contents[ER_NOT_SUCCESSFUL][1] 
          = "Xalan: was not successful.";
}

public static final int ER_ENCODING_NOT_SUPPORTED = 28;
static {contents[ER_ENCODING_NOT_SUPPORTED][1] 
          = "Encoding not supported: {0}";
}

public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;
static {contents[ER_COULD_NOT_CREATE_TRACELISTENER][1] 
          = "Could not create TraceListener: {0}";
}

public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;
static {contents[ER_KEY_REQUIRES_NAME_ATTRIB][1] 
          = "xsl:key requires a 'name' attribute!";
}

public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;
static {contents[ER_KEY_REQUIRES_MATCH_ATTRIB][1] 
          = "xsl:key requires a 'match' attribute!";
}

public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;
static {contents[ER_KEY_REQUIRES_USE_ATTRIB][1] 
          = "xsl:key requires a 'use' attribute!";
}

public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;
static {contents[ER_REQUIRES_ELEMENTS_ATTRIB][1] 
          = "(StylesheetHandler) {0} requires an 'elements' attribute!";
}

public static final int ER_MISSING_PREFIX_ATTRIB = 34;
static {contents[ER_MISSING_PREFIX_ATTRIB][1] 
          = "(StylesheetHandler) {0} attribute 'prefix' is missing";
}

public static final int ER_BAD_STYLESHEET_URL = 35;
static {contents[ER_BAD_STYLESHEET_URL][1] 
          = "Stylesheet URL is bad: {0}";
}

public static final int ER_FILE_NOT_FOUND = 36;
static {contents[ER_FILE_NOT_FOUND][1] 
          = "Stylesheet file was not found: {0}";
}

public static final int ER_IOEXCEPTION = 37;
static {contents[ER_IOEXCEPTION][1] 
          = "Had IO Exception with stylesheet file: {0}";
}

public static final int ER_NO_HREF_ATTRIB = 38;
static {contents[ER_NO_HREF_ATTRIB][1] 
          = "(StylesheetHandler) Could not find href attribute for {0}";
}

public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;
static {contents[ER_STYLESHEET_INCLUDES_ITSELF][1] 
          = "(StylesheetHandler) {0} is directly or indirectly including itself!";
}

public static final int ER_PROCESSINCLUDE_ERROR = 40;
static {contents[ER_PROCESSINCLUDE_ERROR][1] 
          = "StylesheetHandler.processInclude error, {0}";
}

public static final int ER_MISSING_LANG_ATTRIB = 41;
static {contents[ER_MISSING_LANG_ATTRIB][1] 
          = "(StylesheetHandler) {0} attribute 'lang' is missing";
}

public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;
static {contents[ER_MISSING_CONTAINER_ELEMENT_COMPONENT][1] 
          = "(StylesheetHandler) misplaced {0} element?? Missing container element 'component'";
}

public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;
static {contents[ER_CAN_ONLY_OUTPUT_TO_ELEMENT][1] 
          = "Can only output to an Element, DocumentFragment, Document, or PrintWriter.";
}

public static final int ER_PROCESS_ERROR = 44;
static {contents[ER_PROCESS_ERROR][1] 
          = "StylesheetRoot.process error";
}

public static final int ER_UNIMPLNODE_ERROR = 45;
static {contents[ER_UNIMPLNODE_ERROR][1] 
          = "UnImplNode error: {0}";
}

public static final int ER_NO_SELECT_EXPRESSION = 46;
static {contents[ER_NO_SELECT_EXPRESSION][1] 
          = "Error! Did not find xpath select expression (-select).";
}

public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;
static {contents[ER_CANNOT_SERIALIZE_XSLPROCESSOR][1] 
          = "Can not serialize an XSLProcessor!";
}

public static final int ER_NO_INPUT_STYLESHEET = 48;
static {contents[ER_NO_INPUT_STYLESHEET][1] 
          = "Stylesheet input was not specified!";
}

public static final int ER_FAILED_PROCESS_STYLESHEET = 49;
static {contents[ER_FAILED_PROCESS_STYLESHEET][1] 
          = "Failed to process stylesheet!";
}

public static final int ER_COULDNT_PARSE_DOC = 50;
static {contents[ER_COULDNT_PARSE_DOC][1] 
          = "Could not parse {0} document!";
}

public static final int ER_COULDNT_FIND_FRAGMENT = 51;
static {contents[ER_COULDNT_FIND_FRAGMENT][1] 
          = "Could not find fragment: {0}";
}

public static final int ER_NODE_NOT_ELEMENT = 52;
static {contents[ER_NODE_NOT_ELEMENT][1] 
          = "Node pointed to by fragment identifier was not an element: {0}";
}

public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;
static {contents[ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB][1] 
          = "for-each must have either a match or name attribute";
}

public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;
static {contents[ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB][1] 
          = "templates must have either a match or name attribute";
}

public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;
static {contents[ER_NO_CLONE_OF_DOCUMENT_FRAG][1] 
          = "No clone of a document fragment!";
}

public static final int ER_CANT_CREATE_ITEM = 56;
static {contents[ER_CANT_CREATE_ITEM][1] 
          = "Can not create item in result tree: {0}";
}

public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;
static {contents[ER_XMLSPACE_ILLEGAL_VALUE][1] 
          = "xml:space in the source XML has an illegal value: {0}";
}

public static final int ER_NO_XSLKEY_DECLARATION = 58;
static {contents[ER_NO_XSLKEY_DECLARATION][1] 
          = "There is no xsl:key declaration for {0}!";
}

public static final int ER_CANT_CREATE_URL = 59;
static {contents[ER_CANT_CREATE_URL][1] 
          = "Error! Cannot create url for: {0}";
}

public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;
static {contents[ER_XSLFUNCTIONS_UNSUPPORTED][1] 
          = "xsl:functions is unsupported";
}

public static final int ER_PROCESSOR_ERROR = 61;
static {contents[ER_PROCESSOR_ERROR][1] 
          = "XSLT Processor Error";
}

public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;
static {contents[ER_NOT_ALLOWED_INSIDE_STYLESHEET][1] 
          = "(StylesheetHandler) {0} not allowed inside a stylesheet!";
}

public static final int ER_RESULTNS_NOT_SUPPORTED = 63;
static {contents[ER_RESULTNS_NOT_SUPPORTED][1] 
          = "result-ns no longer supported!  Use xsl:output instead.";
}

public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;
static {contents[ER_DEFAULTSPACE_NOT_SUPPORTED][1] 
          = "default-space no longer supported!  Use xsl:strip-space or xsl:preserve-space instead.";
}

public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;
static {contents[ER_INDENTRESULT_NOT_SUPPORTED][1] 
          = "indent-result no longer supported!  Use xsl:output instead.";
}

public static final int ER_ILLEGAL_ATTRIB = 66;
static {contents[ER_ILLEGAL_ATTRIB][1] 
          = "(StylesheetHandler) {0} has an illegal attribute: {1}";
}

public static final int ER_UNKNOWN_XSL_ELEM = 67;
static {contents[ER_UNKNOWN_XSL_ELEM][1] 
          = "Unknown XSL element: {0}";
}

public static final int ER_BAD_XSLSORT_USE = 68;
static {contents[ER_BAD_XSLSORT_USE][1] 
          = "(StylesheetHandler) xsl:sort can only be used with xsl:apply-templates or xsl:for-each.";
}

public static final int ER_MISPLACED_XSLWHEN = 69;
static {contents[ER_MISPLACED_XSLWHEN][1] 
          = "(StylesheetHandler) misplaced xsl:when!";
}

public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;
static {contents[ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE][1] 
          = "(StylesheetHandler) xsl:when not parented by xsl:choose!";
}

public static final int ER_MISPLACED_XSLOTHERWISE = 71;
static {contents[ER_MISPLACED_XSLOTHERWISE][1] 
          = "(StylesheetHandler) misplaced xsl:otherwise!";
}

public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;
static {contents[ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE][1] 
          = "(StylesheetHandler) xsl:otherwise not parented by xsl:choose!";
}

public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;
static {contents[ER_NOT_ALLOWED_INSIDE_TEMPLATE][1] 
          = "(StylesheetHandler) {0} is not allowed inside a template!";
}

public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;
static {contents[ER_UNKNOWN_EXT_NS_PREFIX][1] 
          = "(StylesheetHandler) {0} extension namespace prefix {1} unknown";
}

public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;
static {contents[ER_IMPORTS_AS_FIRST_ELEM][1] 
          = "(StylesheetHandler) Imports can only occur as the first elements in the stylesheet!";
}

public static final int ER_IMPORTING_ITSELF = 76;
static {contents[ER_IMPORTING_ITSELF][1] 
          = "(StylesheetHandler) {0} is directly or indirectly importing itself!";
}

public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;
static {contents[ER_XMLSPACE_ILLEGAL_VAL][1] 
          = "(StylesheetHandler) "+"xml:space has an illegal value: {0}";
}

public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;
static {contents[ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL][1] 
          = "processStylesheet not succesfull!";
}

public static final int ER_SAX_EXCEPTION = 79;
static {contents[ER_SAX_EXCEPTION][1] 
          = "SAX Exception";
}

public static final int ER_FUNCTION_NOT_SUPPORTED = 80;
static {contents[ER_FUNCTION_NOT_SUPPORTED][1] 
          = "Function not supported!";
}

public static final int ER_XSLT_ERROR = 81;
static {contents[ER_XSLT_ERROR][1] 
          = "XSLT Error";
}

public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;
static {contents[ER_CURRENCY_SIGN_ILLEGAL][1] 
          = "currency sign is not allowed in format pattern string";
}

public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;
static {contents[ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM][1] 
          = "Document function not supported in Stylesheet DOM!";
}

public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;
static {contents[ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER][1] 
          = "Can't resolve prefix of non-Prefix resolver!";
}

public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;
static {contents[ER_REDIRECT_COULDNT_GET_FILENAME][1] 
          = "Redirect extension: Could not get filename - file or select attribute must return vald string.";
}

public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;
static {contents[ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT][1] 
          = "Can not build FormatterListener in Redirect extension!";
}

public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;
static {contents[ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX][1] 
          = "Prefix in exclude-result-prefixes is not valid: {0}";
}

public static final int ER_MISSING_NS_URI = 88;
static {contents[ER_MISSING_NS_URI][1] 
          = "Missing namespace URI for specified prefix";
}

public static final int ER_MISSING_ARG_FOR_OPTION = 89;
static {contents[ER_MISSING_ARG_FOR_OPTION][1] 
          = "Missing argument for option: {0}";
}

public static final int ER_INVALID_OPTION = 90;
static {contents[ER_INVALID_OPTION][1] 
          = "Invalid option: {0}";
}

public static final int ER_MALFORMED_FORMAT_STRING = 91;
static {contents[ER_MALFORMED_FORMAT_STRING][1] 
          = "Malformed format string: {0}";
}

public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;
static {contents[ER_STYLESHEET_REQUIRES_VERSION_ATTRIB][1] 
          = "xsl:stylesheet requires a 'version' attribute!";
}

public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;
static {contents[ER_ILLEGAL_ATTRIBUTE_VALUE][1] 
			= "Attribute: {0} has an illegal value: {1}";
}

public static final int ER_CHOOSE_REQUIRES_WHEN = 94;
static {contents[ER_CHOOSE_REQUIRES_WHEN][1] 
			= "xsl:choose requires an xsl:when";
}

public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;
static {contents[ER_NO_APPLY_IMPORT_IN_FOR_EACH][1] 
			= "xsl:apply-imports not allowed in a xsl:for-each";
}

public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;
static {contents[ER_CANT_USE_DTM_FOR_OUTPUT][1] 
			= "Cannot use a DTMLiaison for an output DOM node... pass a org.apache.xalan.xpath.DOM2Helper instead!";
}

public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;
static {contents[ER_CANT_USE_DTM_FOR_INPUT][1] 
			= "Cannot use a DTMLiaison for a input DOM node... pass a org.apache.xalan.xpath.DOM2Helper instead!";
}

public static final int ER_CALL_TO_EXT_FAILED = 98;
static {contents[ER_CALL_TO_EXT_FAILED][1] 
          = "Call to extension element failed: {0}";
}

public static final int ER_PREFIX_MUST_RESOLVE = 99;
static {contents[ER_PREFIX_MUST_RESOLVE][1] 
          = "Prefix must resolve to a namespace: {0}";
}

public static final int ER_INVALID_UTF16_SURROGATE = 100;
static {contents[ER_INVALID_UTF16_SURROGATE][1] 
          = "Invalid UTF-16 surrogate detected: {0} ?";
}

public static final int ER_XSLATTRSET_USED_ITSELF = 101;
static {contents[ER_XSLATTRSET_USED_ITSELF][1] 
          = "xsl:attribute-set {0} used itself, which will cause an infinite loop.";
}

public static final int ER_CANNOT_MIX_XERCESDOM = 102;
static {contents[ER_CANNOT_MIX_XERCESDOM][1] 
          = "Can not mix non Xerces-DOM input with Xerces-DOM output!";      
}

public static final int ER_TOO_MANY_LISTENERS = 103;
static {contents[ER_TOO_MANY_LISTENERS][1] 
          = "addTraceListenersToStylesheet - TooManyListenersException";      
}

public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;
static {contents[ER_IN_ELEMTEMPLATEELEM_READOBJECT][1] 
          = "In ElemTemplateElement.readObject: {0}";      
}

// Warnings...

public static final int WG_FOUND_CURLYBRACE = 1;
static {contents[WG_FOUND_CURLYBRACE + MAX_CODE][1] 
          = "Found '}' but no attribute template open!";
}

public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;
static {contents[WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR + MAX_CODE][1] 
          = "Warning: count attribute does not match an ancestor in xsl:number! Target = {0}";
}

public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;
static {contents[WG_EXPR_ATTRIB_CHANGED_TO_SELECT + MAX_CODE][1] 
          = "Old syntax: The name of the 'expr' attribute has been changed to 'select'.";
}

public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;
static {contents[WG_NO_LOCALE_IN_FORMATNUMBER + MAX_CODE][1] 
          = "Xalan doesn't yet handle the locale name in the format-number function.";
}

public static final int WG_LOCALE_NOT_FOUND = 5;
static {contents[WG_LOCALE_NOT_FOUND + MAX_CODE][1] 
          = "Warning: Could not find locale for xml:lang={0}";
}

public static final int WG_CANNOT_MAKE_URL_FROM = 6;
static {contents[WG_CANNOT_MAKE_URL_FROM + MAX_CODE][1] 
          = "Can not make URL from: {0}";
}

public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;
static {contents[WG_CANNOT_LOAD_REQUESTED_DOC + MAX_CODE][1] 
          = "Can not load requested doc: {0}";
}

public static final int WG_CANNOT_FIND_COLLATOR = 8;
static {contents[WG_CANNOT_FIND_COLLATOR + MAX_CODE][1] 
          = "Could not find Collator for <sort xml:lang={0}";
}

public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;
static {contents[WG_FUNCTIONS_SHOULD_USE_URL + MAX_CODE][1] 
          = "Old syntax: the functions instruction should use a url of {0}";
}

public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;
static {contents[WG_ENCODING_NOT_SUPPORTED_USING_UTF8 + MAX_CODE][1] 
          = "encoding not supported: {0}, using UTF-8";
}

public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;
static {contents[WG_ENCODING_NOT_SUPPORTED_USING_JAVA + MAX_CODE][1] 
          = "encoding not supported: {0}, using Java {1}";
}

public static final int WG_SPECIFICITY_CONFLICTS = 12;
static {contents[WG_SPECIFICITY_CONFLICTS + MAX_CODE][1] 
          = "Specificity conflicts found: {0} Last found in stylesheet will be used.";
}

public static final int WG_PARSING_AND_PREPARING = 13;
static {contents[WG_PARSING_AND_PREPARING + MAX_CODE][1] 
          = "========= Parsing and preparing {0} ==========";
}

public static final int WG_ATTR_TEMPLATE = 14;
static {contents[WG_ATTR_TEMPLATE + MAX_CODE][1] 
          = "Attr Template, {0}";
}

public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE = 15;
static {contents[WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE + MAX_CODE][1] 
          = "Match conflict between xsl:strip-space and xsl:preserve-space";
}

public static final int WG_ATTRIB_NOT_HANDLED = 16;
static {contents[WG_ATTRIB_NOT_HANDLED + MAX_CODE][1] 
          = "Xalan does not yet handle the {0} attribute!";
}

public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;
static {contents[WG_NO_DECIMALFORMAT_DECLARATION + MAX_CODE][1] 
          = "No declaration found for decimal format: {0}";
}

public static final int WG_OLD_XSLT_NS = 18;
static {contents[WG_OLD_XSLT_NS + MAX_CODE][1] 
          = "Old XSLT Namespace: {0}";
}

public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;
static {contents[WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED + MAX_CODE][1] 
          = "Only one default xsl:decimal-format declaration is allowed. The last one will be used.";
}

public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;
static {contents[WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE + MAX_CODE][1] 
          = "xsl:decimal-format names must be unique. The last one will be used.";
}

public static final int WG_ILLEGAL_ATTRIBUTE = 21;
static {contents[WG_ILLEGAL_ATTRIBUTE + MAX_CODE][1] 
          = "{0} has an illegal attribute: {1}";
}

public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;
static {contents[WG_COULD_NOT_RESOLVE_PREFIX + MAX_CODE][1] 
          = "Could not resolve namespace prefix: {0}. The attribute will be ignored.";
}

public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;
static {contents[WG_STYLESHEET_REQUIRES_VERSION_ATTRIB + MAX_CODE][1] 
          = "xsl:stylesheet requires a 'version' attribute!";
}

public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;
static {contents[WG_ILLEGAL_ATTRIBUTE_NAME + MAX_CODE][1] 
          = "Illegal attribute name: {0}";
}

public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;
static {contents[WG_ILLEGAL_ATTRIBUTE_VALUE + MAX_CODE][1] 
          = "Illegal value used for attribute {0}: {1}";
}



// Other miscellaneous text used inside the code...

static  {
         
         contents[MAX_MESSAGES][0] = "ui_language";
         contents[MAX_MESSAGES][1] = "en";
         
         contents[MAX_MESSAGES + 1][0] = "help_language";
         contents[MAX_MESSAGES + 1][1] = "en";
         
         contents[MAX_MESSAGES + 2][0] = "language";
         contents[MAX_MESSAGES + 2][1] = "en";
         
         contents[MAX_MESSAGES + 3][0] = "BAD_CODE";
         contents[MAX_MESSAGES + 3][1] = "Parameter to createMessage was out of bounds";
  
         contents[MAX_MESSAGES + 4][0] = "FORMAT_FAILED";
         contents[MAX_MESSAGES + 4][1] = "Exception thrown during messageFormat call";
         
         contents[MAX_MESSAGES + 5][0] = "version";
         contents[MAX_MESSAGES + 5][1] = ">>>>>>> Xalan Version ";
         
         contents[MAX_MESSAGES + 6][0] = "version2";
         contents[MAX_MESSAGES + 6][1] = "<<<<<<<";
         
         contents[MAX_MESSAGES + 7][0] = "yes";
         contents[MAX_MESSAGES + 7][1] = "yes";
         
         contents[MAX_MESSAGES + 8][0] = "line";
         contents[MAX_MESSAGES + 8][1] = "Line #";
         
         contents[MAX_MESSAGES + 9][0] = "column";
         contents[MAX_MESSAGES + 9][1] = "Column #";
         
         contents[MAX_MESSAGES + 10][0] = "xsldone";
         contents[MAX_MESSAGES + 10][1] = "XSLProcessor: done";
         
         contents[MAX_MESSAGES + 11][0] = "xslProc_option";
         contents[MAX_MESSAGES + 11][1] = "=xslproc options:";
         
         contents[MAX_MESSAGES + 12][0] = "optionIN";
         contents[MAX_MESSAGES + 12][1] = "    -IN inputXMLURL";
         
         contents[MAX_MESSAGES + 13][0] = "optionXSL";
         contents[MAX_MESSAGES + 13][1] = "   [-XSL XSLTransformationURL]";
         
         contents[MAX_MESSAGES + 14][0] = "optionOUT";
         contents[MAX_MESSAGES + 14][1] = "   [-XSL XSLTransformationURL]";
         
         contents[MAX_MESSAGES + 15][0] = "optionLXCIN";
         contents[MAX_MESSAGES + 15][1] = "   [-LXCIN compiledStylesheetFileNameIn]";
         
         contents[MAX_MESSAGES + 16][0] = "optionLXCOUT";
         contents[MAX_MESSAGES + 16][1] = "   [-LXCOUT compiledStylesheetFileNameOutOut]";
         
         contents[MAX_MESSAGES + 17][0] = "optionPARSER";
         contents[MAX_MESSAGES + 17][1] = "   [-PARSER fully qualified class name of parser liaison]";
         
         contents[MAX_MESSAGES + 18][0] = "optionE";
         contents[MAX_MESSAGES + 18][1] = "   [-E (Do not expand entity refs)]";
         
         contents[MAX_MESSAGES + 19][0] = "optionV";
         contents[MAX_MESSAGES + 19][1] = "   [-E (Do not expand entity refs)]";
         
         contents[MAX_MESSAGES + 20][0] = "optionQC";
         contents[MAX_MESSAGES + 20][1] = "   [-QC (Quiet Pattern Conflicts Warnings)]";
         
         contents[MAX_MESSAGES + 21][0] = "optionQ";
         contents[MAX_MESSAGES + 21][1] = "   [-Q  (Quiet Mode)]";
         
         contents[MAX_MESSAGES + 22][0] = "optionLF";
         contents[MAX_MESSAGES + 22][1] = "   [-LF (Use linefeeds only on output {default is CR/LF})]";
         
         contents[MAX_MESSAGES + 23][0] = "optionCR";
         contents[MAX_MESSAGES + 23][1] = "   [-CR (Use carriage returns only on output {default is CR/LF})]";
         
         contents[MAX_MESSAGES + 24][0] = "optionESCAPE";
         contents[MAX_MESSAGES + 24][1] = "   [-ESCAPE (Which characters to escape {default is <>&\"\'\\r\\n}]";

         contents[MAX_MESSAGES + 25][0] = "optionINDENT";
         contents[MAX_MESSAGES + 25][1] = "   [-INDENT (Control how many spaces to indent {default is 0})]";

         contents[MAX_MESSAGES + 26][0] = "optionTT";
         contents[MAX_MESSAGES + 26][1] = "   [-TT (Trace the templates as they are being called.)]";

         contents[MAX_MESSAGES + 27][0] = "optionTG";
         contents[MAX_MESSAGES + 27][1] = "   [-TG (Trace each generation event.)]";

         contents[MAX_MESSAGES + 28][0] = "optionTS";
         contents[MAX_MESSAGES + 28][1] = "   [-TS (Trace each selection event.)]";

         contents[MAX_MESSAGES + 29][0] = "optionTTC";
         contents[MAX_MESSAGES + 29][1] = "   [-TTC (Trace the template children as they are being processed.)]";

         contents[MAX_MESSAGES + 30][0] = "optionTCLASS";
         contents[MAX_MESSAGES + 30][1] = "   [-TCLASS (TraceListener class for trace extensions.)]";

         contents[MAX_MESSAGES + 31][0] = "optionVALIDATE"; 
         contents[MAX_MESSAGES + 31][1] = "   [-VALIDATE (Set whether validation occurs.  Validation is off by default.)]";
         
         contents[MAX_MESSAGES + 32][0] = "optionEDUMP";
         contents[MAX_MESSAGES + 32][1] = "   [-EDUMP {optional filename} (Do stackdump on error.)]";
         
         contents[MAX_MESSAGES + 33][0] = "optionXML";
         contents[MAX_MESSAGES + 33][1] = "   [-XML (Use XML formatter and add XML header.)]";
         
         contents[MAX_MESSAGES + 34][0] = "optionTEXT";
         contents[MAX_MESSAGES + 34][1] = "   [-TEXT (Use simple Text formatter.)]";
         
         contents[MAX_MESSAGES + 35][0] = "optionHTML";
         contents[MAX_MESSAGES + 35][1] = "   [-HTML (Use HTML formatter.)]";
         
         contents[MAX_MESSAGES + 36][0] = "optionPARAM";
         contents[MAX_MESSAGES + 36][1] = "   [-PARAM name expression (Set a stylesheet parameter)]";

         contents[MAX_MESSAGES + 37][0] = "noParsermsg1";
         contents[MAX_MESSAGES + 37][1] = "XSL Process was not successful.";
         
         contents[MAX_MESSAGES + 38][0] = "noParsermsg2";
         contents[MAX_MESSAGES + 38][1] = "** Could not find parser **";
         
         contents[MAX_MESSAGES + 39][0] = "noParsermsg3";
         contents[MAX_MESSAGES + 39][1] = "Please check your classpath.";
         
         contents[MAX_MESSAGES + 40][0] = "noParsermsg4";
         contents[MAX_MESSAGES + 40][1] = "If you don't have IBM's XML Parser for Java, you can download it from";
         
         contents[MAX_MESSAGES + 41][0] = "noParsermsg5";
         contents[MAX_MESSAGES + 41][1] = "IBM's AlphaWorks: http://www.alphaworks.ibm.com/formula/xml";         
  }

  // ================= INFRASTRUCTURE ======================

  public static final String BAD_CODE = "BAD_CODE";
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  public static final String ERROR_STRING = "#error";
  public static final String ERROR_HEADER = "Error: ";
  public static final String WARNING_HEADER = "Warning: ";
  public static final String XSL_HEADER = "XSL ";
  public static final String XML_HEADER = "XML ";
  public static final String QUERY_HEADER = "PATTERN ";

  
  public Object[][] getContents()
  {
    return contents;
  }

/**
   * Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   * of ResourceBundle.getBundle(). 
   *
   * @param res the name of the resource to load. 
   * @param locale the locale to prefer when searching for the bundle
   * @return the ResourceBundle
   * @throws MissingResourceException  
   */
  public static final XSLTErrorResources loadResourceBundle (String className) 
	  throws MissingResourceException
  {
	Locale locale = Locale.getDefault();
	String suffix = getResourceSuffix(locale);  
    try
    {		
                                                           // first try with the given locale
      return (XSLTErrorResources)ResourceBundle.getBundle (className + suffix, locale);
    }
    catch (MissingResourceException e)
    {
      try                                                  // try to fall back to en_US if we can't load
      {
                                                           // Since we can't find the localized property file,
                                                           // fall back to en_US.
        return (XSLTErrorResources)ResourceBundle.getBundle (className, new Locale ("en", "US"));
      }
      catch (MissingResourceException e2)
      {
                                                              // Now we are really in trouble.
                                                              // very bad, definitely very bad...not going to get very far
        throw new MissingResourceException ("Could not load any resource bundles.", className, "");
      }
    }
  }
  
  /**
   * Return the resource file suffic for the indicated locale
   * For most locales, this will be based the language code.  However
   * for Chinese, we do distinguish between Taiwan and PRC
   *
   * @param locale the locale
   * @return an String suffix which canbe appended to a resource name
   */        
  private static final String getResourceSuffix(Locale locale)
  {
        String suffix = "_" + locale.getLanguage();
        
        String country = locale.getCountry();        
        
        if (country.equals("TW"))
            suffix += "_" + country;

        return suffix;
  }
  
  
  /**
   * Get the error string associated with the error code
   */ 
  public String getMessageKey(int errorCode)
  {
	  if (errorCode > MAX_CODE)
      return null;
    else
    {
      DecimalFormat df = new DecimalFormat("0000");
      return ERROR_SUFFIX + df.format(errorCode);	
    }  
  }
  
  /**
   * Get the error string associated with the error code
   */ 
  public String getWarningKey(int errorCode)
  {
	  if (errorCode > MAX_WARNING)
      return null;
	  else 
		{
      DecimalFormat df = new DecimalFormat("0000");
      return WARNING_SUFFIX + df.format(errorCode);	
    }
  }
  
  /**
   * Get the error string associated with the error code
   */ 
  public static String getMKey(int errorCode)
  {
	  if (errorCode > MAX_CODE)
      return null;
    else
    {
      DecimalFormat df = new DecimalFormat("0000");
      return ERROR_SUFFIX + df.format(errorCode);	
    }  
  }
  
  /**
   * Get the error string associated with the error code
   */ 
  public static String getWKey(int errorCode)
  {
	  if (errorCode > MAX_WARNING)
      return null;
	  else 
		{
      DecimalFormat df = new DecimalFormat("0000");
      return WARNING_SUFFIX + df.format(errorCode);	
    }
  }
}    

