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
package org.apache.xml.res;


/**
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first add a String constant. And you need
 * to enter key, value pair as part of the contents
 * array. You also need to update MAX_CODE for error strings
 * and MAX_WARNING for warnings ( Needed for only information
 * purpose )
 */
public class XMLErrorResources_ja extends XMLErrorResources
{

  /** Maximum error messages, this is needed to keep track of the number of messages.    */
  public static final int MAX_CODE = 61;

  /** Maximum warnings, this is needed to keep track of the number of warnings.          */
  public static final int MAX_WARNING = 0;

  /** Maximum misc strings.   */
  public static final int MAX_OTHERS = 4;

  /** Maximum total warnings and error messages.          */
  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;


  // Error messages...

  /** The lookup table for error messages.   */
  public static final Object[][] contents = {

  /** Error message ID that has a null message, but takes in a single object.    */
    {"ER0000" , "{0}" },
 


  /** ER_FUNCTION_NOT_SUPPORTED          */
  //public static final int ER_FUNCTION_NOT_SUPPORTED = 80;


  {
    ER_FUNCTION_NOT_SUPPORTED, "Function \u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093\u3002"},

  
  /** Can't overwrite cause         */
  //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;


  {
    ER_CANNOT_OVERWRITE_CAUSE,
			"cause \u3092\u4e0a\u66f8\u304d\u3067\u304d\u307e\u305b\u3093"},

  
   /**  No default implementation found */
  //public static final int ER_NO_DEFAULT_IMPL = 156;


  {
    ER_NO_DEFAULT_IMPL,
         "\u30c7\u30d5\u30a9\u30eb\u30c8\u5b9f\u88c5\u304c\u898b\u3064\u304b\u308a\u307e\u305b\u3093"},
//         "No default implementation found "},

  
   /**  ChunkedIntArray({0}) not currently supported */
  //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;


  {
    ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
       "ChunkedIntArray({0}) \u306f\u73fe\u5728\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "ChunkedIntArray({0}) not currently supported"},

  
   /**  Offset bigger than slot */
  //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;


  {
    ER_OFFSET_BIGGER_THAN_SLOT,
       "\u30b9\u30ed\u30c3\u30c8\u3088\u308a\u3082\u5927\u304d\u3044\u30aa\u30d5\u30bb\u30c3\u30c8"},
//       "Offset bigger than slot"},

  
   /**  Coroutine not available, id= */
  //public static final int ER_COROUTINE_NOT_AVAIL = 159;


  {
    ER_COROUTINE_NOT_AVAIL,
       "\u30b3\u30eb\u30fc\u30c1\u30f3\u306f\u7121\u52b9\u3067\u3059\u3002id={0}"},
//       "Coroutine not available, id={0}"},

  
   /**  CoroutineManager recieved co_exit() request */
  //public static final int ER_COROUTINE_CO_EXIT = 160;


  {
    ER_COROUTINE_CO_EXIT,
       "CoroutineManager \u306f co_exit() \u8981\u6c42\u3092\u53d7\u3051\u53d6\u308a\u307e\u3057\u305f"},
//       "CoroutineManager received co_exit() request"},

  
   /**  co_joinCoroutineSet() failed */
  //public static final int ER_COJOINROUTINESET_FAILED = 161;


  {
    ER_COJOINROUTINESET_FAILED,
       "co_joinCoroutineSet() \u306f\u5931\u6557\u3057\u307e\u3057\u305f"},
//       "co_joinCoroutineSet() failed"},

  
   /**  Coroutine parameter error () */
  //public static final int ER_COROUTINE_PARAM = 162;


  {
    ER_COROUTINE_PARAM,
       "\u30b3\u30eb\u30fc\u30c1\u30f3\u30d1\u30e9\u30e1\u30fc\u30bf\u30a8\u30e9\u30fc ({0})"},
//       "Coroutine parameter error ({0})"},

  
   /**  UNEXPECTED: Parser doTerminate answers  */
  //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;


  {
    ER_PARSER_DOTERMINATE_ANSWERS,
       "\nUNEXPECTED: \u30d1\u30fc\u30b5 doTerminate \u306e\u7b54\u3048 {0}"},
//       "\nUNEXPECTED: Parser doTerminate answers {0}"},

  
   /**  parse may not be called while parsing */
  //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;


  {
    ER_NO_PARSE_CALL_WHILE_PARSING,
       "\u69cb\u6587\u89e3\u6790\u4e2d\u306b parse \u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093"},
//       "parse may not be called while parsing"},

  
   /**  Error: typed iterator for axis  {0} not implemented  */
  //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;


  {
    ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\u30a8\u30e9\u30fc: \u5165\u529b\u3055\u308c\u305f\u8ef8\u306e\u53cd\u5fa9\u5b50 {0} \u306f\u5b9f\u88c5\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "Error: typed iterator for axis  {0} not implemented"},

  
   /**  Error: iterator for axis {0} not implemented  */
  //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;


  {
    ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\u30a8\u30e9\u30fc: \u8ef8\u306e\u53cd\u5fa9\u5b50 {0} \u306f\u5b9f\u88c5\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "Error: iterator for axis {0} not implemented "},

  
   /**  Iterator clone not supported  */
  //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;


  {
    ER_ITERATOR_CLONE_NOT_SUPPORTED,
       "\u53cd\u5fa9\u5b50\u30af\u30ed\u30fc\u30f3\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//       "Iterator clone not supported"},

  
   /**  Unknown axis traversal type  */
  //public static final int ER_UNKNOWN_AXIS_TYPE = 168;


  {
    ER_UNKNOWN_AXIS_TYPE,
       "\u672a\u77e5\u306e\u8ef8\u30c8\u30e9\u30d0\u30fc\u30b5\u30eb\u30bf\u30a4\u30d7: {0}"},
//       "Unknown axis traversal type: {0}"},

  
   /**  Axis traverser not supported  */
  //public static final int ER_AXIS_NOT_SUPPORTED = 169;


  {
    ER_AXIS_NOT_SUPPORTED,
       "\u8ef8\u30c8\u30e9\u30d0\u30fc\u30b5\u30eb\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093: {0}"},
//       "Axis traverser not supported: {0}"},

  
   /**  No more DTM IDs are available  */
  //public static final int ER_NO_DTMIDS_AVAIL = 170;


  {
    ER_NO_DTMIDS_AVAIL,
       "\u3053\u308c\u4ee5\u4e0a\u306e DTM ID \u306f\u7121\u52b9\u3067\u3059"},
//       "No more DTM IDs are available"},

  
   /**  Not supported  */
  //public static final int ER_NOT_SUPPORTED = 171;


  {
    ER_NOT_SUPPORTED,
       "\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093: {0}"},
//       "Not supported: {0}"},

  
   /**  node must be non-null for getDTMHandleFromNode  */
  //public static final int ER_NODE_NON_NULL = 172;


  {
    ER_NODE_NON_NULL,
       "getDTMHandleFromNode \u306e\u30ce\u30fc\u30c9\u306f null \u4ee5\u5916\u3067\u306a\u304f\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//       "Node must be non-null for getDTMHandleFromNode"},

  
   /**  Could not resolve the node to a handle  */
  //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;


  {
    ER_COULD_NOT_RESOLVE_NODE,
       "\u30ce\u30fc\u30c9\u3092\u30cf\u30f3\u30c9\u30eb\u306b\u5909\u3048\u308b\u3053\u3068\u304c\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//       "Could not resolve the node to a handle"},

  
   /**  startParse may not be called while parsing */
  //public static final int ER_STARTPARSE_WHILE_PARSING = 174;


  {
    ER_STARTPARSE_WHILE_PARSING,
       "\u69cb\u6587\u89e3\u6790\u4e2d\u306b startParse \u3092\u547c\u3073\u51fa\u3059\u3053\u3068\u306f\u3067\u304d\u307e\u305b\u3093"},
//       "startParse may not be called while parsing"},

  
   /**  startParse needs a non-null SAXParser  */
  //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;


  {
    ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse \u306f null \u3067\u306a\u3044 SAXParser \u3092\u5fc5\u8981\u3068\u3057\u307e\u3059"},
//       "startParse needs a non-null SAXParser"},

  
   /**  could not initialize parser with */
  //public static final int ER_COULD_NOT_INIT_PARSER = 176;


  {
    ER_COULD_NOT_INIT_PARSER,
       "\u30d1\u30fc\u30b5\u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093\u3067\u3057\u305f"},
//       "could not initialize parser with"},

  
   /**  exception creating new instance for pool  */
  //public static final int ER_EXCEPTION_CREATING_POOL = 178;


  {
    ER_EXCEPTION_CREATING_POOL,
       "\u4f8b\u5916\u306b\u3088\u308a\u30d7\u30fc\u30eb\u306b\u65b0\u3057\u3044\u30a4\u30f3\u30b9\u30bf\u30f3\u30b9\u3092\u4f5c\u6210\u3057\u3066\u3044\u307e\u3059"},
//       "exception creating new instance for pool"},

  
   /**  Path contains invalid escape sequence  */
  //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;


  {
    ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "\u30d1\u30b9\u306b\u7121\u52b9\u306a\u30a8\u30b9\u30b1\u30fc\u30d7\u30b7\u30fc\u30b1\u30f3\u30b9\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059"},
//       "Path contains invalid escape sequence"},

  
   /**  Scheme is required!  */
  //public static final int ER_SCHEME_REQUIRED = 180;


  {
    ER_SCHEME_REQUIRED,
       "\u30b9\u30ad\u30fc\u30de\u304c\u5fc5\u8981\u3067\u3059\u3002"},
//       "Scheme is required!"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_IN_URI = 181;


  {
    ER_NO_SCHEME_IN_URI,
       "URI \u306b\u30b9\u30ad\u30fc\u30de\u304c\u3042\u308a\u307e\u305b\u3093: {0}"},
//       "No scheme found in URI: {0}"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_INURI = 182;


  {
    ER_NO_SCHEME_INURI,
       "URI \u306b\u30b9\u30ad\u30fc\u30de\u304c\u3042\u308a\u307e\u305b\u3093"},
//       "No scheme found in URI"},

  
   /**  Path contains invalid character:   */
  //public static final int ER_PATH_INVALID_CHAR = 183;


  {
    ER_PATH_INVALID_CHAR,
       "\u30d1\u30b9\u306b\u7121\u52b9\u306a\u6587\u5b57\u5217\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059: {0}"},
//       "Path contains invalid character: {0}"},

  
   /**  Cannot set scheme from null string  */
  //public static final int ER_SCHEME_FROM_NULL_STRING = 184;


  {
    ER_SCHEME_FROM_NULL_STRING,
       "null \u6587\u5b57\u5217\u304b\u3089\u30b9\u30ad\u30fc\u30de\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//       "Cannot set scheme from null string"},

  
   /**  The scheme is not conformant. */
  //public static final int ER_SCHEME_NOT_CONFORMANT = 185;


  {
    ER_SCHEME_NOT_CONFORMANT,
       "\u30b9\u30ad\u30fc\u30de\u304c\u4e00\u81f4\u3057\u307e\u305b\u3093\u3002"},
//       "The scheme is not conformant."},

  
   /**  Host is not a well formed address  */
  //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;


  {
    ER_HOST_ADDRESS_NOT_WELLFORMED,
       "\u30db\u30b9\u30c8\u304c\u6b63\u3057\u3044\u5f62\u5f0f\u306e\u30a2\u30c9\u30ec\u30b9\u3067\u306f\u3042\u308a\u307e\u305b\u3093"},
//      "Host is not a well formed address"},

  
   /**  Port cannot be set when host is null  */
  //public static final int ER_PORT_WHEN_HOST_NULL = 187;


  {
    ER_PORT_WHEN_HOST_NULL,
       "\u30db\u30b9\u30c8\u304c null \u306e\u3068\u304d\u3001\u30dd\u30fc\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//       "Port cannot be set when host is null"},

  
   /**  Invalid port number  */
  //public static final int ER_INVALID_PORT = 188;


  {
    ER_INVALID_PORT,
       "\u7121\u52b9\u306a\u30dd\u30fc\u30c8\u756a\u53f7"},
//       "Invalid port number"},

  
   /**  Fragment can only be set for a generic URI  */
  //public static final int ER_FRAG_FOR_GENERIC_URI = 189;


  {
    ER_FRAG_FOR_GENERIC_URI,
       "\u6c4e\u7528 URI \u306b\u5bfe\u3057\u3066\u306e\u307f\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u3059"},
//       "Fragment can only be set for a generic URI"},

  
   /**  Fragment cannot be set when path is null  */
  //public static final int ER_FRAG_WHEN_PATH_NULL = 190;


  {
    ER_FRAG_WHEN_PATH_NULL,
       "\u30d1\u30b9\u304c null \u306e\u3068\u304d\u3001\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u3092\u8a2d\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//       "Fragment cannot be set when path is null"},

  
   /**  Fragment contains invalid character  */
  //public static final int ER_FRAG_INVALID_CHAR = 191;


  {
    ER_FRAG_INVALID_CHAR,
       "\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u306b\u7121\u52b9\u306a\u6587\u5b57\u5217\u304c\u542b\u307e\u308c\u3066\u3044\u307e\u3059"},
//       "Fragment contains invalid character"},

  
 
  
   /** Parser is already in use  */
  //public static final int ER_PARSER_IN_USE = 192;


  {
    ER_PARSER_IN_USE,
        "\u30d1\u30fc\u30b5\u306f\u3059\u3067\u306b\u4f7f\u308f\u308c\u3066\u3044\u307e\u3059"},
//        "Parser is already in use"},

  
   /** Parser is already in use  */
  //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;


  {
    ER_CANNOT_CHANGE_WHILE_PARSING,
        "\u69cb\u6587\u89e3\u6790\u4e2d\u3001{0} {1} \u3092\u5909\u66f4\u3067\u304d\u307e\u305b\u3093"},
//        "Cannot change {0} {1} while parsing"},

  
   /** Self-causation not permitted  */
  //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;


  {
    ER_SELF_CAUSATION_NOT_PERMITTED,
        "\u81ea\u8eab\u304c\u539f\u56e0\u3068\u306a\u3063\u3066\u306f\u306a\u308a\u307e\u305b\u3093"},
//        "Self-causation not permitted"},

  
   /** Userinfo may not be specified if host is not specified   */
  //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;


  {
    ER_NO_USERINFO_IF_NO_HOST,
        "\u30db\u30b9\u30c8\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u306a\u3044\u3068\u304d\u3001Userinfo \u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//        "Userinfo may not be specified if host is not specified"},

  
   /** Port may not be specified if host is not specified   */
  //public static final int ER_NO_PORT_IF_NO_HOST = 199;


  {
    ER_NO_PORT_IF_NO_HOST,
        "\u30db\u30b9\u30c8\u304c\u6307\u5b9a\u3055\u308c\u3066\u3044\u306a\u3044\u3068\u304d\u3001Port \u3092\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//        "Port may not be specified if host is not specified"},

  
   /** Query string cannot be specified in path and query string   */
  //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;


  {
    ER_NO_QUERY_STRING_IN_PATH,
        "\u30d1\u30b9\u304a\u3088\u3073\u7167\u4f1a\u6587\u5b57\u5217\u3067 Query \u6587\u5b57\u5217\u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//        "Query string cannot be specified in path and query string"},

  
   /** Fragment cannot be specified in both the path and fragment   */
  //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;


  {
    ER_NO_FRAGMENT_STRING_IN_PATH,
        "\u30d1\u30b9\u304a\u3088\u3073\u30d5\u30e9\u30b0\u30e1\u30f3\u30c8\u306e\u4e21\u65b9\u3067\u3001Fragment \u306f\u6307\u5b9a\u3067\u304d\u307e\u305b\u3093"},
//        "Fragment cannot be specified in both the path and fragment"},

  
   /** Cannot initialize URI with empty parameters   */
  //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;


  {
    ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "\u7a7a\u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u3092\u4f7f\u3063\u3066 URI \u3092\u521d\u671f\u5316\u3067\u304d\u307e\u305b\u3093"},
//        "Cannot initialize URI with empty parameters"},
  
  /**  Method not yet supported    */
  //public static final int ER_METHOD_NOT_SUPPORTED = 210;


  {
    ER_METHOD_NOT_SUPPORTED,
        "\u30e1\u30bd\u30c3\u30c9\u306f\u307e\u3060\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093"},
//        "Method not yet supported "},


  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;


  {
    ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
     "IncrementalSAXSource_Filter \u306f\u73fe\u5728\u518d\u8d77\u52d5\u3067\u304d\u307e\u305b\u3093"},
//     "IncrementalSAXSource_Filter not currently restartable"},

  
  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;


  {
    ER_XMLRDR_NOT_BEFORE_STARTPARSE,
     "XMLReader \u306f startParse \u8981\u6c42\u3088\u308a\u524d\u306b\u914d\u7f6e\u3067\u304d\u307e\u305b\u3093"},
//     "XMLReader not before startParse request"},


// Axis traverser not supported: {0}
  //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;

  {
    ER_AXIS_TRAVERSER_NOT_SUPPORTED,
     "\u25bc\u8ef8\u30c8\u30e9\u30d0\u30fc\u30b5\u30eb\u306f\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u307e\u305b\u3093: {0}"},
//     "Axis traverser not supported: {0}"},


// ListingErrorHandler created with null PrintWriter!
  //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;

   {
    ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
     "\u25bcListingErrorHandler \u306e\u4f5c\u6210\u6642\u306b null PrintWriter \u304c\u6307\u5b9a\u3055\u308c\u307e\u3057\u305f!"},
//     "ListingErrorHandler created with null PrintWriter!"},


  //public static final int ER_SYSTEMID_UNKNOWN = 240;

  {
    ER_SYSTEMID_UNKNOWN,
     "\u25bc\u30b7\u30b9\u30c6\u30e0 ID \u304c\u4e0d\u660e\u3067\u3059"},
//     "SystemId Unknown"},


  // Location of error unknown
  //public static final int ER_LOCATION_UNKNOWN = 241;

  {
    ER_LOCATION_UNKNOWN,
     "\u25bc\u30a8\u30e9\u30fc\u306e\u5834\u6240\u304c\u4e0d\u660e\u3067\u3059"},
//     "Location of error unknown"},


  /** Field ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 52;


  {
    ER_PREFIX_MUST_RESOLVE,
      "\u63a5\u982d\u8f9e\u306f\u540d\u524d\u7a7a\u9593\u306b\u5909\u3048\u308b\u5fc5\u8981\u304c\u3042\u308a\u307e\u3059: {0}"},
//      "Prefix must resolve to a namespace: {0}"},


  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


  {
    ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "createDocument() \u306f XPathContext \u3067\u30b5\u30dd\u30fc\u30c8\u3055\u308c\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "createDocument() not supported in XPathContext!"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "\u5c5e\u6027 child \u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u3092\u4fdd\u6301\u3057\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "Attribute child does not have an owner document!"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "\u5c5e\u6027 child \u306f\u6240\u6709\u8005\u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u8981\u7d20\u3092\u4fdd\u6301\u3057\u3066\u3044\u307e\u305b\u3093\u3002"},
//      "Attribute child does not have an owner document element!"},


  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


  {
    ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "\u8b66\u544a: \u30c9\u30ad\u30e5\u30e1\u30f3\u30c8\u8981\u7d20\u3088\u308a\u524d\u306b\u30c6\u30ad\u30b9\u30c8\u3092\u51fa\u529b\u3067\u304d\u307e\u305b\u3093\u3002\u7121\u8996\u3057\u307e\u3059..."},
//      "Warning: can't output text before document element!  Ignoring..."},


  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


  {
    ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "DOM \u306b\u306f\u8907\u6570\u306e\u30eb\u30fc\u30c8\u3092\u4fdd\u6301\u3067\u304d\u307e\u305b\u3093\u3002"},
//      "Can't have more than one root on a DOM!"},

  
   /**  Argument 'localName' is null  */
  //public static final int ER_ARG_LOCALNAME_NULL = 70;


  {
    ER_ARG_LOCALNAME_NULL,
       "\u5f15\u6570 'localName' \u304c null \u3067\u3059"},
//       "Argument 'localName' is null"},


  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The localname is the portion after the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** localname in QNAME should be a valid NCName */
  //public static final int ER_ARG_LOCALNAME_INVALID = 101;


  {
    ER_ARG_LOCALNAME_INVALID,
       "\u25bcQNAME \u5185\u306e\u30ed\u30fc\u30ab\u30eb\u540d\u306f\u6709\u52b9\u306a NCName \u3067\u306a\u3051\u308c\u3070\u306a\u308a\u307e\u305b\u3093"},

  
  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The prefix is the portion before the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** prefix in QNAME should be a valid NCName */
  //public static final int ER_ARG_PREFIX_INVALID = 102;


  {
    ER_ARG_PREFIX_INVALID,
       "\u25bcQNAME \u5185\u306e\u63a5\u982d\u8f9e\u306f\u6709\u52b9\u306a NCName \u3067\u306a\u3051\u308c\u3070\u306a\u308a\u307e\u305b\u3093"},

  { "BAD_CODE",
      "createMessage \u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u304c\u7bc4\u56f2\u5916\u3067\u3057\u305f"},
  { "FORMAT_FAILED",
      "messageFormat \u547c\u3073\u51fa\u3057\u3067\u4f8b\u5916\u304c\u30b9\u30ed\u30fc\u3055\u308c\u307e\u3057\u305f"},
  { "line", "\u884c\u756a\u53f7"},
  { "column", "\u5217\u756a\u53f7"}
  
  };

  /**
   * Get the association list.
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return contents;
  }  
}