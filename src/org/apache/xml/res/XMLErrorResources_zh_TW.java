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
public class XMLErrorResources_zh_TW extends XMLErrorResources
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
    ER_FUNCTION_NOT_SUPPORTED, "\u4e0d\u652f\u63f4\u51fd\u5f0f\uff01"},

  
  /** Can't overwrite cause         */
  //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;


  {
    ER_CANNOT_OVERWRITE_CAUSE,
			"\u7121\u6cd5\u6539\u5beb\u539f\u56e0"},

  
   /**  No default implementation found */
  //public static final int ER_NO_DEFAULT_IMPL = 156;


  {
    ER_NO_DEFAULT_IMPL,
         "\u627e\u4e0d\u5230\u9810\u8a2d\u5efa\u7f6e"},

  
   /**  ChunkedIntArray({0}) not currently supported */
  //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;


  {
    ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
       "\u76ee\u524d\u4e0d\u652f\u63f4 ChunkedIntArray({0})"},

  
   /**  Offset bigger than slot */
  //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;


  {
    ER_OFFSET_BIGGER_THAN_SLOT,
       "\u504f\u79fb\u5927\u65bc\u4ecb\u9762\u69fd"},

  
   /**  Coroutine not available, id= */
  //public static final int ER_COROUTINE_NOT_AVAIL = 159;


  {
    ER_COROUTINE_NOT_AVAIL,
       "\u6c92\u6709 Coroutine \u53ef\u7528\uff0cid={0}"},

  
   /**  CoroutineManager recieved co_exit() request */
  //public static final int ER_COROUTINE_CO_EXIT = 160;


  {
    ER_COROUTINE_CO_EXIT,
       "CoroutineManager \u6536\u5230 co_exit() \u8981\u6c42"},

  
   /**  co_joinCoroutineSet() failed */
  //public static final int ER_COJOINROUTINESET_FAILED = 161;


  {
    ER_COJOINROUTINESET_FAILED,
       "co_joinCoroutineSet() \u5931\u6548"},

  
   /**  Coroutine parameter error () */
  //public static final int ER_COROUTINE_PARAM = 162;


  {
    ER_COROUTINE_PARAM,
       "Coroutine \u53c3\u6578\u932f\u8aa4 ({0})"},

  
   /**  UNEXPECTED: Parser doTerminate answers  */
  //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;


  {
    ER_PARSER_DOTERMINATE_ANSWERS,
       "\nUNEXPECTED: \u5256\u6790\u5668 doTerminate \u56de\u7b54 {0}"},

  
   /**  parse may not be called while parsing */
  //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;


  {
    ER_NO_PARSE_CALL_WHILE_PARSING,
       "\u5728\u9032\u884c\u5256\u6790\u6642\u672a\u80fd\u547c\u53eb\u5256\u6790"},

  
   /**  Error: typed iterator for axis  {0} not implemented  */
  //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;


  {
    ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\u932f\u8aa4\uff1a\u5c0d\u8ef8 {0} \u8f38\u5165\u7684\u91cd\u8986\u5668\u6c92\u6709\u57f7\u884c"},

  
   /**  Error: iterator for axis {0} not implemented  */
  //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;


  {
    ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\u932f\u8aa4\uff1a\u8ef8 {0} \u7684\u91cd\u8986\u5668\u6c92\u6709\u57f7\u884c "},

  
   /**  Iterator clone not supported  */
  //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;


  {
    ER_ITERATOR_CLONE_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\u91cd\u8986\u5668\u8907\u88fd"},

  
   /**  Unknown axis traversal type  */
  //public static final int ER_UNKNOWN_AXIS_TYPE = 168;


  {
    ER_UNKNOWN_AXIS_TYPE,
       "\u672a\u77e5\u8ef8\u904d\u6b77\u985e\u578b\uff1a{0}"},

  
   /**  Axis traverser not supported  */
  //public static final int ER_AXIS_NOT_SUPPORTED = 169;


  {
    ER_AXIS_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\u8ef8\u904d\u8a2a\u5668\uff1a{0}"},

  
   /**  No more DTM IDs are available  */
  //public static final int ER_NO_DTMIDS_AVAIL = 170;


  {
    ER_NO_DTMIDS_AVAIL,
       "\u6c92\u6709\u53ef\u7528\u7684 DTM ID"},

  
   /**  Not supported  */
  //public static final int ER_NOT_SUPPORTED = 171;


  {
    ER_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\uff1a{0}"},

  
   /**  node must be non-null for getDTMHandleFromNode  */
  //public static final int ER_NODE_NON_NULL = 172;


  {
    ER_NODE_NON_NULL,
       "\u5c0d getDTMHandleFromNode \u800c\u8a00\uff0c\u7bc0\u9ede\u5fc5\u9808\u70ba\u975e\u7a7a\u503c"},

  
   /**  Could not resolve the node to a handle  */
  //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;


  {
    ER_COULD_NOT_RESOLVE_NODE,
       "\u7121\u6cd5\u89e3\u8b6f\u7bc0\u9ede\u70ba\u63a7\u9ede"},

  
   /**  startParse may not be called while parsing */
  //public static final int ER_STARTPARSE_WHILE_PARSING = 174;


  {
    ER_STARTPARSE_WHILE_PARSING,
       "\u5728\u9032\u884c\u5256\u6790\u6642\u672a\u547c\u53eb startParse"},

  
   /**  startParse needs a non-null SAXParser  */
  //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;


  {
    ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse \u9700\u8981\u975e\u7a7a\u503c\u7684 SAXParser"},

  
   /**  could not initialize parser with */
  //public static final int ER_COULD_NOT_INIT_PARSER = 176;


  {
    ER_COULD_NOT_INIT_PARSER,
       "\u7121\u6cd5\u8d77\u59cb\u8a2d\u5b9a\u5256\u6790\u5668\uff0c\u4ee5"},

  
   /**  exception creating new instance for pool  */
  //public static final int ER_EXCEPTION_CREATING_POOL = 178;


  {
    ER_EXCEPTION_CREATING_POOL,
       "\u5efa\u7acb\u5132\u5b58\u6c60\u7684\u65b0\u6848\u4f8b\u6642\u767c\u751f\u7570\u5e38"},

  
   /**  Path contains invalid escape sequence  */
  //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;


  {
    ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "\u8def\u5f91\u5305\u542b\u7121\u6548\u9038\u51fa\u5e8f\u5217"},

  
   /**  Scheme is required!  */
  //public static final int ER_SCHEME_REQUIRED = 180;


  {
    ER_SCHEME_REQUIRED,
       "\u7db1\u8981\u662f\u5fc5\u9700\u7684\uff01"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_IN_URI = 181;


  {
    ER_NO_SCHEME_IN_URI,
       "\u5728 URI \u627e\u4e0d\u5230\u7db1\u8981\uff1a{0}"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_INURI = 182;


  {
    ER_NO_SCHEME_INURI,
       "\u5728 URI \u627e\u4e0d\u5230\u7db1\u8981"},

  
   /**  Path contains invalid character:   */
  //public static final int ER_PATH_INVALID_CHAR = 183;


  {
    ER_PATH_INVALID_CHAR,
       "\u8def\u5f91\u5305\u542b\u7121\u6548\u7684\u5b57\u5143\uff1a{0}"},

  
   /**  Cannot set scheme from null string  */
  //public static final int ER_SCHEME_FROM_NULL_STRING = 184;


  {
    ER_SCHEME_FROM_NULL_STRING,
       "\u7121\u6cd5\u5f9e\u7a7a\u5b57\u4e32\u8a2d\u5b9a\u7db1\u8981"},

  
   /**  The scheme is not conformant. */
  //public static final int ER_SCHEME_NOT_CONFORMANT = 185;


  {
    ER_SCHEME_NOT_CONFORMANT,
       "\u7db1\u8981\u4e0d\u4e00\u81f4\u3002"},

  
   /**  Host is not a well formed address  */
  //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;


  {
    ER_HOST_ADDRESS_NOT_WELLFORMED,
       "\u4e3b\u6a5f\u6c92\u6709\u5b8c\u6574\u7684\u4f4d\u5740"},

  
   /**  Port cannot be set when host is null  */
  //public static final int ER_PORT_WHEN_HOST_NULL = 187;


  {
    ER_PORT_WHEN_HOST_NULL,
       "\u4e3b\u6a5f\u70ba\u7a7a\u503c\u6642\uff0c\u7121\u6cd5\u8a2d\u5b9a\u901a\u8a0a\u57e0"},

  
   /**  Invalid port number  */
  //public static final int ER_INVALID_PORT = 188;


  {
    ER_INVALID_PORT,
       "\u7121\u6548\u7684\u901a\u8a0a\u57e0\u7de8\u865f"},

  
   /**  Fragment can only be set for a generic URI  */
  //public static final int ER_FRAG_FOR_GENERIC_URI = 189;


  {
    ER_FRAG_FOR_GENERIC_URI,
       "\u53ea\u80fd\u5c0d\u540c\u5c6c\u7684 URI \u8a2d\u5b9a\u7247\u6bb5"},

  
   /**  Fragment cannot be set when path is null  */
  //public static final int ER_FRAG_WHEN_PATH_NULL = 190;


  {
    ER_FRAG_WHEN_PATH_NULL,
       "\u8def\u5f91\u70ba\u7a7a\u503c\u6642\uff0c\u7121\u6cd5\u8a2d\u5b9a\u7247\u6bb5"},

  
   /**  Fragment contains invalid character  */
  //public static final int ER_FRAG_INVALID_CHAR = 191;


  {
    ER_FRAG_INVALID_CHAR,
       "\u7247\u6bb5\u5305\u542b\u7121\u6548\u5b57\u5143"},

  
 
  
   /** Parser is already in use  */
  //public static final int ER_PARSER_IN_USE = 192;


  {
    ER_PARSER_IN_USE,
        "\u5256\u6790\u5668\u5df2\u5728\u4f7f\u7528\u4e2d"},

  
   /** Parser is already in use  */
  //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;


  {
    ER_CANNOT_CHANGE_WHILE_PARSING,
        "\u5256\u6790\u6642\u7121\u6cd5\u8b8a\u66f4 {0} {1}"},

  
   /** Self-causation not permitted  */
  //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;


  {
    ER_SELF_CAUSATION_NOT_PERMITTED,
        "\u4e0d\u5141\u8a31\u81ea\u884c\u5f15\u8d77"},

  
   /** Userinfo may not be specified if host is not specified   */
  //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;


  {
    ER_NO_USERINFO_IF_NO_HOST,
        "\u5982\u679c\u6c92\u6709\u6307\u5b9a\u4e3b\u6a5f\uff0c\u4e0d\u53ef\u6307\u5b9a Userinfo"},

  
   /** Port may not be specified if host is not specified   */
  //public static final int ER_NO_PORT_IF_NO_HOST = 199;


  {
    ER_NO_PORT_IF_NO_HOST,
        "\u5982\u679c\u6c92\u6709\u6307\u5b9a\u4e3b\u6a5f\uff0c\u4e0d\u53ef\u6307\u5b9a\u901a\u8a0a\u57e0"},

  
   /** Query string cannot be specified in path and query string   */
  //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;


  {
    ER_NO_QUERY_STRING_IN_PATH,
        "\u5728\u8def\u5f91\u53ca\u67e5\u8a62\u5b57\u4e32\u4e2d\u4e0d\u53ef\u6307\u5b9a\u67e5\u8a62\u5b57\u4e32"},

  
   /** Fragment cannot be specified in both the path and fragment   */
  //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;


  {
    ER_NO_FRAGMENT_STRING_IN_PATH,
        "\u7121\u6cd5\u5728\u8def\u5f91\u548c\u7247\u6bb5\u4e2d\u6307\u5b9a\u7247\u6bb5"},

  
   /** Cannot initialize URI with empty parameters   */
  //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;


  {
    ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "\u7121\u6cd5\u8d77\u59cb\u8a2d\u5b9a\u7a7a\u767d\u53c3\u6578\u7684 URI"},

  
  /**  Method not yet supported    */
  //public static final int ER_METHOD_NOT_SUPPORTED = 210;


  {
    ER_METHOD_NOT_SUPPORTED,
        "\u4e0d\u652f\u63f4\u65b9\u6cd5 "},


  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;


  {
    ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
     "IncrementalSAXSource_Filter \u76ee\u524d\u7121\u6cd5\u91cd\u65b0\u555f\u52d5"},

  
  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;


  {
    ER_XMLRDR_NOT_BEFORE_STARTPARSE,
     "XMLReader \u4e0d\u5728 startParse \u8981\u6c42\u4e4b\u524d"},
  

  // Axis traverser not supported: {0}
    //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;
  
    {
      ER_AXIS_TRAVERSER_NOT_SUPPORTED,
       "\u4e0d\u652f\u63f4\u8ef8\u904d\u6b77\u5668\uff1a{0}"},
  

  // ListingErrorHandler created with null PrintWriter!
    //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;
  
    {
      ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
       "\u5efa\u7acb\u7684 ListingErrorHandler \u5177\u6709\u7a7a PrintWriter\uff01"},
  

    //public static final int ER_SYSTEMID_UNKNOWN = 240;
  
    {
      ER_SYSTEMID_UNKNOWN,
       "SystemId \u672a\u77e5"},
  

   // Location of error unknown
    //public static final int ER_LOCATION_UNKNOWN = 241;
  
    {
      ER_LOCATION_UNKNOWN,
       "\u672a\u77e5\u7684\u932f\u8aa4\u4f4d\u7f6e"},

  /** Field ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 52;


  {
    ER_PREFIX_MUST_RESOLVE,
      "\u524d\u7f6e\u5fc5\u9808\u89e3\u8b6f\u70ba\u540d\u7a31\u7a7a\u9593\uff1a{0}"},


  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


  {
    ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "createDocument() \u5728 XPathContext \u4e2d\u4e0d\u53d7\u652f\u63f4\uff01"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "\u5c6c\u6027\u5b50\u9805\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\uff01"},


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "\u5c6c\u6027\u5b50\u9805\u6c92\u6709\u64c1\u6709\u8005\u6587\u4ef6\u5143\u7d20\uff01"},


  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


  {
    ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "\u8b66\u544a\uff1a\u7121\u6cd5\u8f38\u51fa\u6587\u4ef6\u5143\u7d20\u4e4b\u524d\u7684\u6587\u5b57\uff01\u5ffd\u7565..."},


  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


  {
    ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "\u4e00\u500b DOM \u53ea\u80fd\u6709\u4e00\u500b\u6839\uff01"},

  
   /**  Argument 'localName' is null  */
  //public static final int ER_ARG_LOCALNAME_NULL = 70;


  {
    ER_ARG_LOCALNAME_NULL,
       "\u5f15\u6578 'localName' \u70ba\u7a7a\u503c"},


  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The localname is the portion after the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** localname in QNAME should be a valid NCName */
  //public static final int ER_ARG_LOCALNAME_INVALID = 101;
 

  {
    ER_ARG_LOCALNAME_INVALID,
       "QNAME \u4e2d\u7684 Localname \u61c9\u70ba\u6709\u6548\u7684 NCName"},

  
  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The prefix is the portion before the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.
 
  /** prefix in QNAME should be a valid NCName */
  //public static final int ER_ARG_PREFIX_INVALID = 102;
 

  {
    ER_ARG_PREFIX_INVALID,
       "QNAME \u4e2d\u7684\u524d\u7f6e\u61c9\u70ba\u6709\u6548\u7684 NCName"},

  { "BAD_CODE",
      "createMessage \u7684\u53c3\u6578\u8d85\u51fa\u754c\u9650"},
  { "FORMAT_FAILED",
      "\u5728 messageFormat \u547c\u53eb\u671f\u9593\u4e1f\u51fa\u7570\u5e38"},
  { "line", "\u884c #"},
  { "column", "\u76f4\u6b04 #"}
  
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