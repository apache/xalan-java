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
package org.apache.xalan.res;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

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
public class XSLTErrorResources extends PropertyResourceBundle
{
  public XSLTErrorResources()
  	throws java.io.IOException
  {
    super(null);
  }
  
  public XSLTErrorResources(InputStream is) 
  	throws java.io.IOException
  {
  	super(is);
  }

  /** The error suffix for construction error property keys.   */
  public static final String ERROR_SUFFIX = "ER";

  /** The warning suffix for construction error property keys.   */
  public static final String WARNING_SUFFIX = "WR";

//  /** Maximum error messages, this is needed to keep track of the number of messages.    */
//  public static final int MAX_CODE = 226;          
//
//  /** Maximum warnings, this is needed to keep track of the number of warnings.          */
//  public static final int MAX_WARNING = 26;
//
//  /** Maximum misc strings.   */
//  public static final int MAX_OTHERS = 45;
//
//  /** Maximum total warnings and error messages.          */
//  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;

  /*
   * Now fill in the message text.
   * First create an int for the message code. Make sure you
   * update MAX_CODE for error messages and MAX_WARNING for warnings
   * Then fill in the message text for that message code in the
   * array. Use the new error code as the index into the array.
   */

  // Error messages...
  public static final int ERROR0000 = 0;


  /** ER_NO_CURLYBRACE          */
  public static final int ER_NO_CURLYBRACE = 1;


  /** ER_ILLEGAL_ATTRIBUTE          */
  public static final int ER_ILLEGAL_ATTRIBUTE = 2;


  /** ER_NULL_SOURCENODE_APPLYIMPORTS          */
  public static final int ER_NULL_SOURCENODE_APPLYIMPORTS = 3;


  /** ER_CANNOT_ADD          */
  public static final int ER_CANNOT_ADD = 4;


  /** ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES          */
  public static final int ER_NULL_SOURCENODE_HANDLEAPPLYTEMPLATES = 5;


  /** ER_NO_NAME_ATTRIB          */
  public static final int ER_NO_NAME_ATTRIB = 6;


  /** ER_TEMPLATE_NOT_FOUND          */
  public static final int ER_TEMPLATE_NOT_FOUND = 7;


  /** ER_CANT_RESOLVE_NAME_AVT          */
  public static final int ER_CANT_RESOLVE_NAME_AVT = 8;


  /** ER_REQUIRES_ATTRIB          */
  public static final int ER_REQUIRES_ATTRIB = 9;


  /** ER_MUST_HAVE_TEST_ATTRIB          */
  public static final int ER_MUST_HAVE_TEST_ATTRIB = 10;


  /** ER_BAD_VAL_ON_LEVEL_ATTRIB          */
  public static final int ER_BAD_VAL_ON_LEVEL_ATTRIB = 11;


  /** ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  public static final int ER_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 12;


  /** ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  public static final int ER_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 13;


  /** ER_NEED_MATCH_ATTRIB          */
  public static final int ER_NEED_MATCH_ATTRIB = 14;


  /** ER_NEED_NAME_OR_MATCH_ATTRIB          */
  public static final int ER_NEED_NAME_OR_MATCH_ATTRIB = 15;


  /** ER_CANT_RESOLVE_NSPREFIX          */
  public static final int ER_CANT_RESOLVE_NSPREFIX = 16;


  /** ER_ILLEGAL_VALUE          */
  public static final int ER_ILLEGAL_VALUE = 17;


  /** ER_NO_OWNERDOC          */
  public static final int ER_NO_OWNERDOC = 18;


  /** ER_ELEMTEMPLATEELEM_ERR          */
  public static final int ER_ELEMTEMPLATEELEM_ERR = 19;


  /** ER_NULL_CHILD          */
  public static final int ER_NULL_CHILD = 20;


  /** ER_NEED_SELECT_ATTRIB          */
  public static final int ER_NEED_SELECT_ATTRIB = 21;


  /** ER_NEED_TEST_ATTRIB          */
  public static final int ER_NEED_TEST_ATTRIB = 22;


  /** ER_NEED_NAME_ATTRIB          */
  public static final int ER_NEED_NAME_ATTRIB = 23;


  /** ER_NO_CONTEXT_OWNERDOC          */
  public static final int ER_NO_CONTEXT_OWNERDOC = 24;


  /** ER_COULD_NOT_CREATE_XML_PROC_LIAISON          */
  public static final int ER_COULD_NOT_CREATE_XML_PROC_LIAISON = 25;


  /** ER_PROCESS_NOT_SUCCESSFUL          */
  public static final int ER_PROCESS_NOT_SUCCESSFUL = 26;


  /** ER_NOT_SUCCESSFUL          */
  public static final int ER_NOT_SUCCESSFUL = 27;


  /** ER_ENCODING_NOT_SUPPORTED          */
  public static final int ER_ENCODING_NOT_SUPPORTED = 28;


  /** ER_COULD_NOT_CREATE_TRACELISTENER          */
  public static final int ER_COULD_NOT_CREATE_TRACELISTENER = 29;


  /** ER_KEY_REQUIRES_NAME_ATTRIB          */
  public static final int ER_KEY_REQUIRES_NAME_ATTRIB = 30;


  /** ER_KEY_REQUIRES_MATCH_ATTRIB          */
  public static final int ER_KEY_REQUIRES_MATCH_ATTRIB = 31;


  /** ER_KEY_REQUIRES_USE_ATTRIB          */
  public static final int ER_KEY_REQUIRES_USE_ATTRIB = 32;


  /** ER_REQUIRES_ELEMENTS_ATTRIB          */
  public static final int ER_REQUIRES_ELEMENTS_ATTRIB = 33;


  /** ER_MISSING_PREFIX_ATTRIB          */
  public static final int ER_MISSING_PREFIX_ATTRIB = 34;


  /** ER_BAD_STYLESHEET_URL          */
  public static final int ER_BAD_STYLESHEET_URL = 35;


  /** ER_FILE_NOT_FOUND          */
  public static final int ER_FILE_NOT_FOUND = 36;


  /** ER_IOEXCEPTION          */
  public static final int ER_IOEXCEPTION = 37;


  /** ER_NO_HREF_ATTRIB          */
  public static final int ER_NO_HREF_ATTRIB = 38;


  /** ER_STYLESHEET_INCLUDES_ITSELF          */
  public static final int ER_STYLESHEET_INCLUDES_ITSELF = 39;


  /** ER_PROCESSINCLUDE_ERROR          */
  public static final int ER_PROCESSINCLUDE_ERROR = 40;


  /** ER_MISSING_LANG_ATTRIB          */
  public static final int ER_MISSING_LANG_ATTRIB = 41;


  /** ER_MISSING_CONTAINER_ELEMENT_COMPONENT          */
  public static final int ER_MISSING_CONTAINER_ELEMENT_COMPONENT = 42;


  /** ER_CAN_ONLY_OUTPUT_TO_ELEMENT          */
  public static final int ER_CAN_ONLY_OUTPUT_TO_ELEMENT = 43;


  /** ER_PROCESS_ERROR          */
  public static final int ER_PROCESS_ERROR = 44;


  /** ER_UNIMPLNODE_ERROR          */
  public static final int ER_UNIMPLNODE_ERROR = 45;


  /** ER_NO_SELECT_EXPRESSION          */
  public static final int ER_NO_SELECT_EXPRESSION = 46;


  /** ER_CANNOT_SERIALIZE_XSLPROCESSOR          */
  public static final int ER_CANNOT_SERIALIZE_XSLPROCESSOR = 47;


  /** ER_NO_INPUT_STYLESHEET          */
  public static final int ER_NO_INPUT_STYLESHEET = 48;


  /** ER_FAILED_PROCESS_STYLESHEET          */
  public static final int ER_FAILED_PROCESS_STYLESHEET = 49;


  /** ER_COULDNT_PARSE_DOC          */
  public static final int ER_COULDNT_PARSE_DOC = 50;


  /** ER_COULDNT_FIND_FRAGMENT          */
  public static final int ER_COULDNT_FIND_FRAGMENT = 51;


  /** ER_NODE_NOT_ELEMENT          */
  public static final int ER_NODE_NOT_ELEMENT = 52;


  /** ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_FOREACH_NEED_MATCH_OR_NAME_ATTRIB = 53;


  /** ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB          */
  public static final int ER_TEMPLATES_NEED_MATCH_OR_NAME_ATTRIB = 54;


  /** ER_NO_CLONE_OF_DOCUMENT_FRAG          */
  public static final int ER_NO_CLONE_OF_DOCUMENT_FRAG = 55;


  /** ER_CANT_CREATE_ITEM          */
  public static final int ER_CANT_CREATE_ITEM = 56;


  /** ER_XMLSPACE_ILLEGAL_VALUE          */
  public static final int ER_XMLSPACE_ILLEGAL_VALUE = 57;


  /** ER_NO_XSLKEY_DECLARATION          */
  public static final int ER_NO_XSLKEY_DECLARATION = 58;


  /** ER_CANT_CREATE_URL          */
  public static final int ER_CANT_CREATE_URL = 59;


  /** ER_XSLFUNCTIONS_UNSUPPORTED          */
  public static final int ER_XSLFUNCTIONS_UNSUPPORTED = 60;


  /** ER_PROCESSOR_ERROR          */
  public static final int ER_PROCESSOR_ERROR = 61;


  /** ER_NOT_ALLOWED_INSIDE_STYLESHEET          */
  public static final int ER_NOT_ALLOWED_INSIDE_STYLESHEET = 62;


  /** ER_RESULTNS_NOT_SUPPORTED          */
  public static final int ER_RESULTNS_NOT_SUPPORTED = 63;


  /** ER_DEFAULTSPACE_NOT_SUPPORTED          */
  public static final int ER_DEFAULTSPACE_NOT_SUPPORTED = 64;


  /** ER_INDENTRESULT_NOT_SUPPORTED          */
  public static final int ER_INDENTRESULT_NOT_SUPPORTED = 65;


  /** ER_ILLEGAL_ATTRIB          */
  public static final int ER_ILLEGAL_ATTRIB = 66;


  /** ER_UNKNOWN_XSL_ELEM          */
  public static final int ER_UNKNOWN_XSL_ELEM = 67;


  /** ER_BAD_XSLSORT_USE          */
  public static final int ER_BAD_XSLSORT_USE = 68;


  /** ER_MISPLACED_XSLWHEN          */
  public static final int ER_MISPLACED_XSLWHEN = 69;


  /** ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLWHEN_NOT_PARENTED_BY_XSLCHOOSE = 70;


  /** ER_MISPLACED_XSLOTHERWISE          */
  public static final int ER_MISPLACED_XSLOTHERWISE = 71;


  /** ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE          */
  public static final int ER_XSLOTHERWISE_NOT_PARENTED_BY_XSLCHOOSE = 72;


  /** ER_NOT_ALLOWED_INSIDE_TEMPLATE          */
  public static final int ER_NOT_ALLOWED_INSIDE_TEMPLATE = 73;


  /** ER_UNKNOWN_EXT_NS_PREFIX          */
  public static final int ER_UNKNOWN_EXT_NS_PREFIX = 74;


  /** ER_IMPORTS_AS_FIRST_ELEM          */
  public static final int ER_IMPORTS_AS_FIRST_ELEM = 75;


  /** ER_IMPORTING_ITSELF          */
  public static final int ER_IMPORTING_ITSELF = 76;


  /** ER_XMLSPACE_ILLEGAL_VAL          */
  public static final int ER_XMLSPACE_ILLEGAL_VAL = 77;


  /** ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL          */
  public static final int ER_PROCESSSTYLESHEET_NOT_SUCCESSFUL = 78;


  /** ER_SAX_EXCEPTION          */
  public static final int ER_SAX_EXCEPTION = 79;


  /** ER_FUNCTION_NOT_SUPPORTED          */
  public static final int ER_FUNCTION_NOT_SUPPORTED = 80;


  /** ER_XSLT_ERROR          */
  public static final int ER_XSLT_ERROR = 81;


  /** ER_CURRENCY_SIGN_ILLEGAL          */
  public static final int ER_CURRENCY_SIGN_ILLEGAL = 82;


  /** ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM          */
  public static final int ER_DOCUMENT_FUNCTION_INVALID_IN_STYLESHEET_DOM = 83;


  /** ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER          */
  public static final int ER_CANT_RESOLVE_PREFIX_OF_NON_PREFIX_RESOLVER = 84;


  /** ER_REDIRECT_COULDNT_GET_FILENAME          */
  public static final int ER_REDIRECT_COULDNT_GET_FILENAME = 85;


  /** ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT          */
  public static final int ER_CANNOT_BUILD_FORMATTERLISTENER_IN_REDIRECT = 86;


  /** ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX          */
  public static final int ER_INVALID_PREFIX_IN_EXCLUDERESULTPREFIX = 87;


  /** ER_MISSING_NS_URI          */
  public static final int ER_MISSING_NS_URI = 88;


  /** ER_MISSING_ARG_FOR_OPTION          */
  public static final int ER_MISSING_ARG_FOR_OPTION = 89;


  /** ER_INVALID_OPTION          */
  public static final int ER_INVALID_OPTION = 90;


  /** ER_MALFORMED_FORMAT_STRING          */
  public static final int ER_MALFORMED_FORMAT_STRING = 91;


  /** ER_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int ER_STYLESHEET_REQUIRES_VERSION_ATTRIB = 92;


  /** ER_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int ER_ILLEGAL_ATTRIBUTE_VALUE = 93;


  /** ER_CHOOSE_REQUIRES_WHEN          */
  public static final int ER_CHOOSE_REQUIRES_WHEN = 94;


  /** ER_NO_APPLY_IMPORT_IN_FOR_EACH          */
  public static final int ER_NO_APPLY_IMPORT_IN_FOR_EACH = 95;


  /** ER_CANT_USE_DTM_FOR_OUTPUT          */
  public static final int ER_CANT_USE_DTM_FOR_OUTPUT = 96;


  /** ER_CANT_USE_DTM_FOR_INPUT          */
  public static final int ER_CANT_USE_DTM_FOR_INPUT = 97;


  /** ER_CALL_TO_EXT_FAILED          */
  public static final int ER_CALL_TO_EXT_FAILED = 98;


  /** ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 99;


  /** ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 100;


  /** ER_XSLATTRSET_USED_ITSELF          */
  public static final int ER_XSLATTRSET_USED_ITSELF = 101;


  /** ER_CANNOT_MIX_XERCESDOM          */
  public static final int ER_CANNOT_MIX_XERCESDOM = 102;


  /** ER_TOO_MANY_LISTENERS          */
  public static final int ER_TOO_MANY_LISTENERS = 103;


  /** ER_IN_ELEMTEMPLATEELEM_READOBJECT          */
  public static final int ER_IN_ELEMTEMPLATEELEM_READOBJECT = 104;


  /** ER_DUPLICATE_NAMED_TEMPLATE          */
  public static final int ER_DUPLICATE_NAMED_TEMPLATE = 105;


  /** ER_INVALID_KEY_CALL          */
  public static final int ER_INVALID_KEY_CALL = 106;

  
  /** Variable is referencing itself          */
  public static final int ER_REFERENCING_ITSELF = 107;

  
  /** Illegal DOMSource input          */
  public static final int ER_ILLEGAL_DOMSOURCE_INPUT = 108;

	
	/** Class not found for option         */
  public static final int ER_CLASS_NOT_FOUND_FOR_OPTION = 109;

	
	/** Required Element not found         */
  public static final int ER_REQUIRED_ELEM_NOT_FOUND = 110;

  
  /** InputStream cannot be null         */
  public static final int ER_INPUT_CANNOT_BE_NULL = 111;

  
  /** URI cannot be null         */
  public static final int ER_URI_CANNOT_BE_NULL = 112;

  
  /** File cannot be null         */
  public static final int ER_FILE_CANNOT_BE_NULL = 113;

  
   /** InputSource cannot be null         */
  public static final int ER_SOURCE_CANNOT_BE_NULL = 114;

  
  /** Can't overwrite cause         */
  public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;

  
  /** Could not initialize BSF Manager        */
  public static final int ER_CANNOT_INIT_BSFMGR = 116;

  
  /** Could not compile extension       */
  public static final int ER_CANNOT_CMPL_EXTENSN = 117;

  
  /** Could not create extension       */
  public static final int ER_CANNOT_CREATE_EXTENSN = 118;

  
  /** Instance method call to method {0} requires an Object instance as first argument       */
  public static final int ER_INSTANCE_MTHD_CALL_REQUIRES = 119;

  
  /** Invalid element name specified       */
  public static final int ER_INVALID_ELEMENT_NAME = 120;

  
   /** Element name method must be static      */
  public static final int ER_ELEMENT_NAME_METHOD_STATIC = 121;

  
   /** Extension function {0} : {1} is unknown      */
  public static final int ER_EXTENSION_FUNC_UNKNOWN = 122;

  
   /** More than one best match for constructor for       */
  public static final int ER_MORE_MATCH_CONSTRUCTOR = 123;

  
   /** More than one best match for method      */
  public static final int ER_MORE_MATCH_METHOD = 124;

  
   /** More than one best match for element method      */
  public static final int ER_MORE_MATCH_ELEMENT = 125;

  
   /** Invalid context passed to evaluate       */
  public static final int ER_INVALID_CONTEXT_PASSED = 126;

  
   /** Pool already exists       */
  public static final int ER_POOL_EXISTS = 127;

  
   /** No driver Name specified      */
  public static final int ER_NO_DRIVER_NAME = 128;

  
   /** No URL specified     */
  public static final int ER_NO_URL = 129;

  
   /** Pool size is less than one    */
  public static final int ER_POOL_SIZE_LESSTHAN_ONE = 130;

  
   /** Invalid driver name specified    */
  public static final int ER_INVALID_DRIVER = 131;

  
   /** Did not find the stylesheet root    */
  public static final int ER_NO_STYLESHEETROOT = 132;

  
   /** Illegal value for xml:space     */
  public static final int ER_ILLEGAL_XMLSPACE_VALUE = 133;

  
   /** processFromNode failed     */
  public static final int ER_PROCESSFROMNODE_FAILED = 134;

  
   /** The resource [] could not load:     */
  public static final int ER_RESOURCE_COULD_NOT_LOAD = 135;

   
  
   /** Buffer size <=0     */
  public static final int ER_BUFFER_SIZE_LESSTHAN_ZERO = 136;

  
   /** Unknown error when calling extension    */
  public static final int ER_UNKNOWN_ERROR_CALLING_EXTENSION = 137;

  
   /** Prefix {0} does not have a corresponding namespace declaration    */
  public static final int ER_NO_NAMESPACE_DECL = 138;

  
   /** Element content not allowed for lang=javaclass   */
  public static final int ER_ELEM_CONTENT_NOT_ALLOWED = 139;

  
   /** Stylesheet directed termination   */
  public static final int ER_STYLESHEET_DIRECTED_TERMINATION = 140;

  
   /** 1 or 2   */
  public static final int ER_ONE_OR_TWO = 141;

  
   /** 2 or 3   */
  public static final int ER_TWO_OR_THREE = 142;

  
   /** Could not load {0} (check CLASSPATH), now using just the defaults   */
  public static final int ER_COULD_NOT_LOAD_RESOURCE = 143;

  
   /** Cannot initialize default templates   */
  public static final int ER_CANNOT_INIT_DEFAULT_TEMPLATES = 144;

  
   /** Result should not be null   */
  public static final int ER_RESULT_NULL = 145;

    
   /** Result could not be set   */
  public static final int ER_RESULT_COULD_NOT_BE_SET = 146;

  
   /** No output specified   */
  public static final int ER_NO_OUTPUT_SPECIFIED = 147;

  
   /** Can't transform to a Result of type   */
  public static final int ER_CANNOT_TRANSFORM_TO_RESULT_TYPE = 148;

  
   /** Can't transform to a Source of type   */
  public static final int ER_CANNOT_TRANSFORM_SOURCE_TYPE = 149;

  
   /** Null content handler  */
  public static final int ER_NULL_CONTENT_HANDLER = 150;

  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 151;

  
   /** parse can not be called if the ContentHandler has not been set */
  public static final int ER_CANNOT_CALL_PARSE = 152;

  
   /**  No parent for filter */
  public static final int ER_NO_PARENT_FOR_FILTER = 153;

  
  
   /**  No stylesheet found in: {0}, media */
  public static final int ER_NO_STYLESHEET_IN_MEDIA = 154;

  
   /**  No xml-stylesheet PI found in */
  public static final int ER_NO_STYLESHEET_PI = 155;

  
   /**  No default implementation found */
  public static final int ER_NO_DEFAULT_IMPL = 156;

  
   /**  ChunkedIntArray({0}) not currently supported */
  public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;

  
   /**  Offset bigger than slot */
  public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;

  
   /**  Coroutine not available, id= */
  public static final int ER_COROUTINE_NOT_AVAIL = 159;

  
   /**  CoroutineManager recieved co_exit() request */
  public static final int ER_COROUTINE_CO_EXIT = 160;

  
   /**  co_joinCoroutineSet() failed */
  public static final int ER_COJOINROUTINESET_FAILED = 161;

  
   /**  Coroutine parameter error () */
  public static final int ER_COROUTINE_PARAM = 162;

  
   /**  UNEXPECTED: Parser doTerminate answers  */
  public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;

  
   /**  parse may not be called while parsing */
  public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;

  
   /**  Error: typed iterator for axis  {0} not implemented  */
  public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;

  
   /**  Error: iterator for axis {0} not implemented  */
  public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;

  
   /**  Iterator clone not supported  */
  public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;

  
   /**  Unknown axis traversal type  */
  public static final int ER_UNKNOWN_AXIS_TYPE = 168;

  
   /**  Axis traverser not supported  */
  public static final int ER_AXIS_NOT_SUPPORTED = 169;

  
   /**  No more DTM IDs are available  */
  public static final int ER_NO_DTMIDS_AVAIL = 170;

  
   /**  Not supported  */
  public static final int ER_NOT_SUPPORTED = 171;

  
   /**  node must be non-null for getDTMHandleFromNode  */
  public static final int ER_NODE_NON_NULL = 172;

  
   /**  Could not resolve the node to a handle  */
  public static final int ER_COULD_NOT_RESOLVE_NODE = 173;

  
   /**  startParse may not be called while parsing */
  public static final int ER_STARTPARSE_WHILE_PARSING = 174;

  
   /**  startParse needs a non-null SAXParser  */
  public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;

  
   /**  could not initialize parser with */
  public static final int ER_COULD_NOT_INIT_PARSER = 176;

  
   /**  Value for property {0} should be a Boolean instance  */
  public static final int ER_PROPERTY_VALUE_BOOLEAN = 177;

  
   /**  exception creating new instance for pool  */
  public static final int ER_EXCEPTION_CREATING_POOL = 178;

  
   /**  Path contains invalid escape sequence  */
  public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;

  
   /**  Scheme is required!  */
  public static final int ER_SCHEME_REQUIRED = 180;

  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_IN_URI = 181;

  
   /**  No scheme found in URI  */
  public static final int ER_NO_SCHEME_INURI = 182;

  
   /**  Path contains invalid character:   */
  public static final int ER_PATH_INVALID_CHAR = 183;

  
   /**  Cannot set scheme from null string  */
  public static final int ER_SCHEME_FROM_NULL_STRING = 184;

  
   /**  The scheme is not conformant. */
  public static final int ER_SCHEME_NOT_CONFORMANT = 185;

  
   /**  Host is not a well formed address  */
  public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;

  
   /**  Port cannot be set when host is null  */
  public static final int ER_PORT_WHEN_HOST_NULL = 187;

  
   /**  Invalid port number  */
  public static final int ER_INVALID_PORT = 188;

  
   /**  Fragment can only be set for a generic URI  */
  public static final int ER_FRAG_FOR_GENERIC_URI = 189;

  
   /**  Fragment cannot be set when path is null  */
  public static final int ER_FRAG_WHEN_PATH_NULL = 190;

  
   /**  Fragment contains invalid character  */
  public static final int ER_FRAG_INVALID_CHAR = 191;

  
 
  
   /** Parser is already in use  */
  public static final int ER_PARSER_IN_USE = 192;

  
   /** Parser is already in use  */
  public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;

  
   /** Self-causation not permitted  */
  public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;

  
   /** src attribute not yet supported for  */
  public static final int ER_COULD_NOT_FIND_EXTERN_SCRIPT = 195;

  
  /** The resource [] could not be found     */
  public static final int ER_RESOURCE_COULD_NOT_FIND = 196;

  
   /** output property not recognized:  */
  public static final int ER_OUTPUT_PROPERTY_NOT_RECOGNIZED = 197;

  
   /** Userinfo may not be specified if host is not specified   */
  public static final int ER_NO_USERINFO_IF_NO_HOST = 198;

  
   /** Port may not be specified if host is not specified   */
  public static final int ER_NO_PORT_IF_NO_HOST = 199;

  
   /** Query string cannot be specified in path and query string   */
  public static final int ER_NO_QUERY_STRING_IN_PATH = 200;

  
   /** Fragment cannot be specified in both the path and fragment   */
  public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;

  
   /** Cannot initialize URI with empty parameters   */
  public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;

  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMLITRSLT = 203;

  
   /** Value for {0} should contain a parsable number   */
  public static final int ER_VALUE_SHOULD_BE_NUMBER = 204;


  
   /**  Value for {0} should equal 'yes' or 'no'   */
  public static final int ER_VALUE_SHOULD_EQUAL = 205;

 
   /**  Failed calling {0} method   */
  public static final int ER_FAILED_CALLING_METHOD = 206;

  
   /** Failed creating ElemLiteralResult instance   */
  public static final int ER_FAILED_CREATING_ELEMTMPL = 207;

  
   /**  Characters are not allowed at this point in the document   */
  public static final int ER_CHARS_NOT_ALLOWED = 208;

  
  /**  attribute is not allowed on the element   */
  public static final int ER_ATTR_NOT_ALLOWED = 209;

  
  /**  Method not yet supported    */
  public static final int ER_METHOD_NOT_SUPPORTED = 210;

 
  /**  Bad value    */
  public static final int ER_BAD_VALUE = 211;

  
  /**  attribute value not found   */
  public static final int ER_ATTRIB_VALUE_NOT_FOUND = 212;

  
  /**  attribute value not recognized    */
  public static final int ER_ATTRIB_VALUE_NOT_RECOGNIZED = 213;


  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;

  
  /** IncrementalSAXSource_Filter not currently restartable   */
  public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;

  
  /** Attempting to generate a namespace prefix with a null URI   */
  public static final int ER_NULL_URI_NAMESPACE = 216;

  
  /** Attempting to generate a namespace prefix with a null URI   */
  public static final int ER_NUMBER_TOO_BIG = 217;
  
// No Driver Name Specified!
  public static final int ER_NO_DRIVER_NAME_SPECIFIED = 228;

// No URL Specified!
  public static final int ER_NO_URL_SPECIFIED = 229; 

// Pool size is less than 1!
  public static final int ER_POOLSIZE_LESS_THAN_ONE = 230;

// Invalid Driver Name Specified!
  public static final int ER_INVALID_DRIVER_NAME = 231;

// ErrorListener
  public static final int ER_ERRORLISTENER = 232;

// Programmer's error! expr has no ElemTemplateElement parent!
  public static final int ER_ASSERT_NO_TEMPLATE_PARENT = 233;

// Programmer's assertion in RundundentExprEliminator: {0}
  public static final int ER_ASSERT_REDUNDENT_EXPR_ELIMINATOR = 234;

// Axis traverser not supported: {0}
  public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;

// ListingErrorHandler created with null PrintWriter!
  public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;

  // {0}is not allowed in this position in the stylesheet!
  public static final int ER_NOT_ALLOWED_IN_POSITION = 237;

  // Non-whitespace text is not allowed in this position in the stylesheet!
  public static final int ER_NONWHITESPACE_NOT_ALLOWED_IN_POSITION = 238;
  
  // This code is shared with warning codes.
  // Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!
  public static final int INVALID_TCHAR = 239;
  
  // SystemId Unknown
  public static final int ER_SYSTEMID_UNKNOWN = 240;
    
  // Location of error unknown  
  public static final int ER_LOCATION_UNKNOWN = 241;
  
  //The following codes are shared with the warning codes... 
  // Illegal value: {1} used for QNAME attribute: {0}
  public static final int INVALID_QNAME = 242;   
   
  // Illegal value\u003a {1} used for ENUM attribute\u003a {0}.  Valid values are\u003a {2}.
  public static final int INVALID_ENUM = 243;
   
  // Illegal value\u003a {1} used for NMTOKEN attribute\u003a {0}. 
  public static final int INVALID_NMTOKEN = 244;
      
  // Illegal value\u003a {1} used for NCNAME attribute\u003a {0}. 
  public static final int INVALID_NCNAME = 245;  
   
  // Illegal value\u003a {1} used for boolean attribute\u003a {0}. 
  public static final int INVALID_BOOLEAN = 246;  

  // Illegal value\u003a {1} used for number attribute\u003a {0}. 
  public static final int INVALID_NUMBER = 247;
  // End of shared codes...
  


  /*
    /**  Cannot find SAX1 driver class    *
  public static final int ER_CANNOT_FIND_SAX1_DRIVER = 190;

  
   /**  SAX1 driver class {0} found but cannot be loaded    *
  public static final int ER_SAX1_DRIVER_NOT_LOADED = 191;

  
   /**  SAX1 driver class {0} found but cannot be instantiated    *
  public static final int ER_SAX1_DRIVER_NOT_INSTANTIATED = 192;

  
   /**  SAX1 driver class {0} does not implement org.xml.sax.Parser    *
  public static final int ER_SAX1_DRIVER_NOT_IMPLEMENT_PARSER = 193;

  
   /**  System property org.xml.sax.parser not specified    *
  public static final int ER_PARSER_PROPERTY_NOT_SPECIFIED = 194;

  
   /**  Parser argument must not be null    *
  public static final int ER_PARSER_ARG_CANNOT_BE_NULL = 195;

  
   /**  Feature:    *
  public static final int ER_FEATURE = 196;

  
   /**  Property:    *
  public static final int ER_PROPERTY = 197;

  
   /** Null Entity Resolver  *
  public static final int ER_NULL_ENTITY_RESOLVER = 198;

  
   /** Null DTD handler  *
  public static final int ER_NULL_DTD_HANDLER = 199;

  
 */ 
  

  // Warnings...

  /** WG_FOUND_CURLYBRACE          */
  public static final int WG_FOUND_CURLYBRACE = 1;


  /** WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR          */
  public static final int WG_COUNT_ATTRIB_MATCHES_NO_ANCESTOR = 2;


  /** WG_EXPR_ATTRIB_CHANGED_TO_SELECT          */
  public static final int WG_EXPR_ATTRIB_CHANGED_TO_SELECT = 3;


  /** WG_NO_LOCALE_IN_FORMATNUMBER          */
  public static final int WG_NO_LOCALE_IN_FORMATNUMBER = 4;


  /** WG_LOCALE_NOT_FOUND          */
  public static final int WG_LOCALE_NOT_FOUND = 5;


  /** WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 6;


  /** WG_CANNOT_LOAD_REQUESTED_DOC          */
  public static final int WG_CANNOT_LOAD_REQUESTED_DOC = 7;


  /** WG_CANNOT_FIND_COLLATOR          */
  public static final int WG_CANNOT_FIND_COLLATOR = 8;


  /** WG_FUNCTIONS_SHOULD_USE_URL          */
  public static final int WG_FUNCTIONS_SHOULD_USE_URL = 9;


  /** WG_ENCODING_NOT_SUPPORTED_USING_UTF8          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_UTF8 = 10;


  /** WG_ENCODING_NOT_SUPPORTED_USING_JAVA          */
  public static final int WG_ENCODING_NOT_SUPPORTED_USING_JAVA = 11;


  /** WG_SPECIFICITY_CONFLICTS          */
  public static final int WG_SPECIFICITY_CONFLICTS = 12;


  /** WG_PARSING_AND_PREPARING          */
  public static final int WG_PARSING_AND_PREPARING = 13;


  /** WG_ATTR_TEMPLATE          */
  public static final int WG_ATTR_TEMPLATE = 14;


  /** WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE          */
  public static final int WG_CONFLICT_BETWEEN_XSLSTRIPSPACE_AND_XSLPRESERVESPACE =
    15;


  /** WG_ATTRIB_NOT_HANDLED          */
  public static final int WG_ATTRIB_NOT_HANDLED = 16;


  /** WG_NO_DECIMALFORMAT_DECLARATION          */
  public static final int WG_NO_DECIMALFORMAT_DECLARATION = 17;


  /** WG_OLD_XSLT_NS          */
  public static final int WG_OLD_XSLT_NS = 18;


  /** WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED          */
  public static final int WG_ONE_DEFAULT_XSLDECIMALFORMAT_ALLOWED = 19;


  /** WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE          */
  public static final int WG_XSLDECIMALFORMAT_NAMES_MUST_BE_UNIQUE = 20;


  /** WG_ILLEGAL_ATTRIBUTE          */
  public static final int WG_ILLEGAL_ATTRIBUTE = 21;


  /** WG_COULD_NOT_RESOLVE_PREFIX          */
  public static final int WG_COULD_NOT_RESOLVE_PREFIX = 22;


  /** WG_STYLESHEET_REQUIRES_VERSION_ATTRIB          */
  public static final int WG_STYLESHEET_REQUIRES_VERSION_ATTRIB = 23;


  /** WG_ILLEGAL_ATTRIBUTE_NAME          */
  public static final int WG_ILLEGAL_ATTRIBUTE_NAME = 24;


  /** WG_ILLEGAL_ATTRIBUTE_VALUE          */
  public static final int WG_ILLEGAL_ATTRIBUTE_VALUE = 25;


  /** WG_EMPTY_SECOND_ARG          */
  public static final int WG_EMPTY_SECOND_ARG = 26;
  
  /** WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML          */
  public static final int WG_PROCESSINGINSTRUCTION_NAME_CANT_BE_XML = 27;
  
  /** WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME          */
  public static final int WG_PROCESSINGINSTRUCTION_NOTVALID_NCNAME = 28;  
  
  
  //The following warning codes are shared with the error codes ... 
  // Illegal value: {1} used for CHAR attribute: {0}.  An attribute of type CHAR must be only 1 character!
  public static final int WG_INVALID_TCHAR = 239;
  
  
  // Illegal value: {1} used for QNAME attribute: {0}
  public static final int WG_INVALID_QNAME = 242;
  
   
  // Illegal value\u003a {1} used for ENUM attribute\u003a {0}.  Valid values are\u003a {2}.
  public static final int WG_INVALID_ENUM = 243;
  
   
  // Illegal value\u003a {1} used for NMTOKEN attribute\u003a {0}. 
  public static final int WG_INVALID_NMTOKEN = 244;
  
      
  // Illegal value\u003a {1} used for NCNAME attribute\u003a {0}. 
  public static final int WG_INVALID_NCNAME = 245;
  
   
  // Illegal value\u003a {1} used for boolean attribute\u003a {0}. 
  public static final int WG_INVALID_BOOLEAN = 246;
  

  // Illegal value\u003a {1} used for number attribute\u003a {0}. 
  public static final int WG_INVALID_NUMBER = 247;
  // End of codes that are shared...

  // Other miscellaneous text used inside the code...

  // ================= INFRASTRUCTURE ======================

  /** String for use when a bad error code was encountered.    */
  public static final String BAD_CODE = "BAD_CODE";

  /** String for use when formatting of the error string failed.   */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** General error string.   */
  public static final String ERROR_STRING = "#error";

  /** String to prepend to error messages.  */
  public static final String ERROR_HEADER = "Error: ";

  /** String to prepend to warning messages.    */
  public static final String WARNING_HEADER = "Warning: ";

  /** String to specify the XSLT module.  */
  public static final String XSL_HEADER = "XSLT ";

  /** String to specify the XML parser module.  */
  public static final String XML_HEADER = "XML ";

  /** I don't think this is used any more.
   * @deprecated  */
  public static final String QUERY_HEADER = "PATTERN ";

//  /**
//   * Get the lookup table. 
//   *
//   * @return The int to message lookup table.
//   */
//  public Object[][] getContents()
//  {
//    return contents;
//  }

  /**
   *   Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   *   of ResourceBundle.getBundle().
   *  
   *   @param className the name of the class that implements the resource bundle.
   *   @return the ResourceBundle
   *   @throws MissingResourceException
   */
  public static final ResourceBundle loadResourceBundle(String className)
          throws MissingResourceException
  {

    Locale locale = Locale.getDefault();
    String suffix = getResourceSuffix(locale);

    try
    {

      // first try with the given locale
      return ResourceBundle.getBundle(className
              + suffix, locale);
    } 
    catch (MissingResourceException e)
    {
      try  // try to fall back to en_US if we can't load
      {

        // Since we can't find the localized property file,
        // fall back to en_US.
        return ResourceBundle.getBundle(className,
                new Locale("en", "US"));
      }
      catch (MissingResourceException e2)
      {

        // Now we are really in trouble.
        // very bad, definitely very bad...not going to get very far
        throw new MissingResourceException(
          "Could not load any resource bundles.", className, "");
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
   * Get a string representation of the error code.
   *
   * @param errorCode Should be a valid error code less than {@link #MAX_CODE}.
   *
   * @return A string representation of the error code, or null if code is 
   * greater than MAX_CODE.
   */
  public static String getMessageKey(int errorCode)
  {

//    if (errorCode > MAX_CODE)
//      return null;
//    else
    {
      DecimalFormat df = new DecimalFormat("0000");

      return ERROR_SUFFIX + df.format(errorCode);
    }
  }

  /**
   * Get a string representation of the warning code.
   *
   * @param errorCode Should be a valid warning code less than {@link #MAX_WARNING}.
   *
   * @return A string representation of the warning code, or null if code is 
   * greater than MAX_WARNING.
   */
  public static String getWarningKey(int errorCode)
  {

//    if (errorCode > MAX_WARNING)
//      return null;
//    else
    {
      DecimalFormat df = new DecimalFormat("0000");

      return WARNING_SUFFIX + df.format(errorCode);
    }
  }

  /**
   * Get a string representation of the message code. (same as getMessageKey).
   *
   * @param errorCode Should be a valid error code less than {@link #MAX_CODE}.
   *
   * @return A string representation of the error code, or null if code is 
   * greater than MAX_CODE.
   */
  public static String getMKey(int errorCode)
  {
//    if (errorCode > MAX_CODE)
//      return null;
//    else
    {
      DecimalFormat df = new DecimalFormat("0000");

      return ERROR_SUFFIX + df.format(errorCode);
    }
  }

  /**
   * Get a string representation of the warning code.
   *
   * @param errorCode Should be a valid warning code less than {@link #MAX_WARNING}.
   *
   * @return A string representation of the warning code, or null if code is 
   * greater than MAX_WARNING.
   */
  public static String getWKey(int errorCode)
  {

//    if (errorCode > MAX_WARNING)
//      return null;
//    else
    {
      DecimalFormat df = new DecimalFormat("0000");

      return WARNING_SUFFIX + df.format(errorCode);
    }
  }
}
