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
public class XMLErrorResources_ko extends XMLErrorResources
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
    ER_FUNCTION_NOT_SUPPORTED, "\uae30\ub2a5\uc774 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4!"},

  
  /** Can't overwrite cause         */
  //public static final int ER_CANNOT_OVERWRITE_CAUSE = 115;


  {
    ER_CANNOT_OVERWRITE_CAUSE,
			"\uacb9\uccd0\uc4f8 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  No default implementation found */
  //public static final int ER_NO_DEFAULT_IMPL = 156;


  {
    ER_NO_DEFAULT_IMPL,
         "\uae30\ubcf8 \uad6c\ud604\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  ChunkedIntArray({0}) not currently supported */
  //public static final int ER_CHUNKEDINTARRAY_NOT_SUPPORTED = 157;


  {
    ER_CHUNKEDINTARRAY_NOT_SUPPORTED,
       "ChunkedIntArray({0})\ub294 \ud604\uc7ac \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4."},

  
   /**  Offset bigger than slot */
  //public static final int ER_OFFSET_BIGGER_THAN_SLOT = 158;


  {
    ER_OFFSET_BIGGER_THAN_SLOT,
       "\uc624\ud504\uc14b\uc774 \uc2ac\ub86f\ubcf4\ub2e4 \ud07d\ub2c8\ub2e4"},

  
   /**  Coroutine not available, id= */
  //public static final int ER_COROUTINE_NOT_AVAIL = 159;


  {
    ER_COROUTINE_NOT_AVAIL,
       "Coroutine\uc740 \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4, ID={0}"},

  
   /**  CoroutineManager recieved co_exit() request */
  //public static final int ER_COROUTINE_CO_EXIT = 160;


  {
    ER_COROUTINE_CO_EXIT,
       "CoroutineManager\uac00 co_exit() \uc694\uccad\uc744 \uc218\uc2e0\ud588\uc2b5\ub2c8\ub2e4"},

  
   /**  co_joinCoroutineSet() failed */
  //public static final int ER_COJOINROUTINESET_FAILED = 161;


  {
    ER_COJOINROUTINESET_FAILED,
       "co_joinCoroutineSet()\uc774 \uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4"},

  
   /**  Coroutine parameter error () */
  //public static final int ER_COROUTINE_PARAM = 162;


  {
    ER_COROUTINE_PARAM,
       "Coroutine \ub9e4\uac1c\ubcc0\uc218 \uc624\ub958({0})"},

  
   /**  UNEXPECTED: Parser doTerminate answers  */
  //public static final int ER_PARSER_DOTERMINATE_ANSWERS = 163;


  {
    ER_PARSER_DOTERMINATE_ANSWERS,
       "\n\uc608\uc0c1\uce58 \ubabb\ud55c \ubb38\uc81c: doTerminate \uad6c\ubb38 \ubd84\uc11d\uae30\uac00 {0}\uc5d0 \uc751\ub2f5\ud588\uc2b5\ub2c8\ub2e4. "},

  
   /**  parse may not be called while parsing */
  //public static final int ER_NO_PARSE_CALL_WHILE_PARSING = 164;


  {
    ER_NO_PARSE_CALL_WHILE_PARSING,
       "\uad6c\ubb38 \ubd84\uc11d\ud558\ub294 \ub3d9\uc548\uc5d0\ub294 \uad6c\ubb38 \ubd84\uc11d\uc744 \ud638\ucd9c\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  Error: typed iterator for axis  {0} not implemented  */
  //public static final int ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED = 165;


  {
    ER_TYPED_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\uc624\ub958: {0} \ucd95\uc5d0 \ub300\ud574 \uc785\ub825\ub41c \ubc18\ubcf5\uae30\uac00 \uad6c\ud604\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4."},

  
   /**  Error: iterator for axis {0} not implemented  */
  //public static final int ER_ITERATOR_AXIS_NOT_IMPLEMENTED = 166;


  {
    ER_ITERATOR_AXIS_NOT_IMPLEMENTED,
       "\uc624\ub958: {0} \ucd95\uc5d0 \ub300\ud55c \ubc18\ubcf5\uae30\uac00 \uad6c\ud604\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. "},

  
   /**  Iterator clone not supported  */
  //public static final int ER_ITERATOR_CLONE_NOT_SUPPORTED = 167;


  {
    ER_ITERATOR_CLONE_NOT_SUPPORTED,
       "\ubc18\ubcf5\uae30 \ubcf5\uc81c\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4"},

  
   /**  Unknown axis traversal type  */
  //public static final int ER_UNKNOWN_AXIS_TYPE = 168;


  {
    ER_UNKNOWN_AXIS_TYPE,
       "\uc54c \uc218 \uc5c6\ub294 \ucd95 \uc21c\ud68c \uc720\ud615: {0}"},

  
   /**  Axis traverser not supported  */
  //public static final int ER_AXIS_NOT_SUPPORTED = 169;


  {
    ER_AXIS_NOT_SUPPORTED,
       "\ucd95 \uc21c\ud68c\uae30\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4: {0}"},

  
   /**  No more DTM IDs are available  */
  //public static final int ER_NO_DTMIDS_AVAIL = 170;


  {
    ER_NO_DTMIDS_AVAIL,
       "\ub354 \uc774\uc0c1 DTM ID\ub97c \uc0ac\uc6a9\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4."},

  
   /**  Not supported  */
  //public static final int ER_NOT_SUPPORTED = 171;


  {
    ER_NOT_SUPPORTED,
       "\uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4: {0}"},

  
   /**  node must be non-null for getDTMHandleFromNode  */
  //public static final int ER_NODE_NON_NULL = 172;


  {
    ER_NODE_NON_NULL,
       "\ub178\ub4dc\ub294 getDTMHandleFromNode\uc5d0 \ub300\ud574 \ub110\uc774 \uc544\ub2c8\uc5b4\uc57c \ud569\ub2c8\ub2e4"},

  
   /**  Could not resolve the node to a handle  */
  //public static final int ER_COULD_NOT_RESOLVE_NODE = 173;


  {
    ER_COULD_NOT_RESOLVE_NODE,
       "\ub178\ub4dc\ub97c \ud578\ub4e4\ub85c \ubcc0\ud658\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  startParse may not be called while parsing */
  //public static final int ER_STARTPARSE_WHILE_PARSING = 174;


  {
    ER_STARTPARSE_WHILE_PARSING,
       "startParse\ub294 \uad6c\ubb38 \ubd84\uc11d \uc911\uc5d0 \ud638\ucd9c\ub420 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  startParse needs a non-null SAXParser  */
  //public static final int ER_STARTPARSE_NEEDS_SAXPARSER = 175;


  {
    ER_STARTPARSE_NEEDS_SAXPARSER,
       "startParse\uc5d0\ub294 \ub110\uc774 \uc544\ub2cc SAXParser\uac00 \ud544\uc694\ud569\ub2c8\ub2e4"},

  
   /**  could not initialize parser with */
  //public static final int ER_COULD_NOT_INIT_PARSER = 176;


  {
    ER_COULD_NOT_INIT_PARSER,
       "\ub2e4\uc74c\uc73c\ub85c \uad6c\ubb38 \ubd84\uc11d\uae30\ub97c \ucd08\uae30\ud654\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4: "},

  
   /**  exception creating new instance for pool  */
  //public static final int ER_EXCEPTION_CREATING_POOL = 178;


  {
    ER_EXCEPTION_CREATING_POOL,
       "\ud480\uc5d0 \ub300\ud55c \uc0c8 \uc778\uc2a4\ud134\uc2a4 \uc791\uc131 \uc911 \uc608\uc678\uac00 \ubc1c\uc0dd\ud588\uc2b5\ub2c8\ub2e4"},

  
   /**  Path contains invalid escape sequence  */
  //public static final int ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = 179;


  {
    ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
       "\uacbd\ub85c\uc5d0 \uc798\ubabb\ub41c \uc81c\uc5b4 \ubb38\uc790\uc5f4\uc774 \ud3ec\ud568\ub418\uc5b4 \uc788\uc2b5\ub2c8\ub2e4"},

  
   /**  Scheme is required!  */
  //public static final int ER_SCHEME_REQUIRED = 180;


  {
    ER_SCHEME_REQUIRED,
       "\uccb4\uacc4\uac00 \ud544\uc694\ud569\ub2c8\ub2e4!"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_IN_URI = 181;


  {
    ER_NO_SCHEME_IN_URI,
       "URI\uc5d0  \uccb4\uacc4\uac00 \uc5c6\uc2b5\ub2c8\ub2e4: {0}"},

  
   /**  No scheme found in URI  */
  //public static final int ER_NO_SCHEME_INURI = 182;


  {
    ER_NO_SCHEME_INURI,
       "URI\uc5d0 \uccb4\uacc4\uac00 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  Path contains invalid character:   */
  //public static final int ER_PATH_INVALID_CHAR = 183;


  {
    ER_PATH_INVALID_CHAR,
       "\uacbd\ub85c\uc5d0 \uc798\ubabb\ub41c \ubb38\uc790 {0}\uc774(\uac00) \ud3ec\ud568\ub418\uc5b4 \uc788\uc2b5\ub2c8\ub2e4."},

  
   /**  Cannot set scheme from null string  */
  //public static final int ER_SCHEME_FROM_NULL_STRING = 184;


  {
    ER_SCHEME_FROM_NULL_STRING,
       "\ub110 \ubb38\uc790\uc5f4\uc5d0\uc11c \uccb4\uacc4\ub97c \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  The scheme is not conformant. */
  //public static final int ER_SCHEME_NOT_CONFORMANT = 185;


  {
    ER_SCHEME_NOT_CONFORMANT,
       "\uccb4\uacc4\uac00 \uc77c\uce58\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4."},

  
   /**  Host is not a well formed address  */
  //public static final int ER_HOST_ADDRESS_NOT_WELLFORMED = 186;


  {
    ER_HOST_ADDRESS_NOT_WELLFORMED,
       "\ud638\uc2a4\ud2b8 \uc8fc\uc18c\uac00 \uc62c\ubc14\ub978 \ud615\uc2dd\uc774 \uc544\ub2d9\ub2c8\ub2e4"},

  
   /**  Port cannot be set when host is null  */
  //public static final int ER_PORT_WHEN_HOST_NULL = 187;


  {
    ER_PORT_WHEN_HOST_NULL,
       "\ud638\uc2a4\ud2b8\uac00 \ub110\uc774\uba74 \ud3ec\ud2b8\ub97c \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  Invalid port number  */
  //public static final int ER_INVALID_PORT = 188;


  {
    ER_INVALID_PORT,
       "\uc798\ubabb\ub41c \ud3ec\ud2b8 \ubc88\ud638\uc785\ub2c8\ub2e4"},

  
   /**  Fragment can only be set for a generic URI  */
  //public static final int ER_FRAG_FOR_GENERIC_URI = 189;


  {
    ER_FRAG_FOR_GENERIC_URI,
       "\ub2e8\ud3b8\uc740 \uc77c\ubc18 URI\uc5d0 \ub300\ud574\uc11c\ub9cc \uc124\uc815\ub420 \uc218 \uc788\uc2b5\ub2c8\ub2e4"},

  
   /**  Fragment cannot be set when path is null  */
  //public static final int ER_FRAG_WHEN_PATH_NULL = 190;


  {
    ER_FRAG_WHEN_PATH_NULL,
       "\uacbd\ub85c\uac00 \ub110\uc774\uba74 \ub2e8\ud3b8\uc744 \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /**  Fragment contains invalid character  */
  //public static final int ER_FRAG_INVALID_CHAR = 191;


  {
    ER_FRAG_INVALID_CHAR,
       "\ub2e8\ud3b8\uc5d0 \uc798\ubabb\ub41c \ubb38\uc790\uac00 \ud3ec\ud568\ub418\uc5b4 \uc788\uc2b5\ub2c8\ub2e4"},

  
 
  
   /** Parser is already in use  */
  //public static final int ER_PARSER_IN_USE = 192;


  {
    ER_PARSER_IN_USE,
        "\uad6c\ubb38 \ubd84\uc11d\uae30\uac00 \uc774\ubbf8 \uc0ac\uc6a9 \uc911\uc785\ub2c8\ub2e4"},

  
   /** Parser is already in use  */
  //public static final int ER_CANNOT_CHANGE_WHILE_PARSING = 193;


  {
    ER_CANNOT_CHANGE_WHILE_PARSING,
        "\uad6c\ubb38 \ubd84\uc11d \uc911\uc5d0\ub294 {0} {1}\uc744(\ub97c) \ubcc0\uacbd\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /** Self-causation not permitted  */
  //public static final int ER_SELF_CAUSATION_NOT_PERMITTED = 194;


  {
    ER_SELF_CAUSATION_NOT_PERMITTED,
        "\uc790\uccb4 \uc6d0\uc778 \uc81c\uacf5\uc740 \ud5c8\uc6a9\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4"},

  
   /** Userinfo may not be specified if host is not specified   */
  //public static final int ER_NO_USERINFO_IF_NO_HOST = 198;


  {
    ER_NO_USERINFO_IF_NO_HOST,
        "\ud638\uc2a4\ud2b8\uac00 \uc9c0\uc815\ub418\uc5b4 \uc788\uc9c0 \uc54a\uc73c\uba74 Userinfo\ub97c \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /** Port may not be specified if host is not specified   */
  //public static final int ER_NO_PORT_IF_NO_HOST = 199;


  {
    ER_NO_PORT_IF_NO_HOST,
        "\ud638\uc2a4\ud2b8\uac00 \uc9c0\uc815\ub418\uc5b4 \uc788\uc9c0 \uc54a\uc73c\uba74 \ud3ec\ud2b8\ub97c \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /** Query string cannot be specified in path and query string   */
  //public static final int ER_NO_QUERY_STRING_IN_PATH = 200;


  {
    ER_NO_QUERY_STRING_IN_PATH,
        "\uc9c8\uc758 \ubb38\uc790\uc5f4\uc744 \uacbd\ub85c \ub610\ub294 \uc9c8\uc758 \ubb38\uc790\uc5f4 \ub0b4\uc5d0 \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /** Fragment cannot be specified in both the path and fragment   */
  //public static final int ER_NO_FRAGMENT_STRING_IN_PATH = 201;


  {
    ER_NO_FRAGMENT_STRING_IN_PATH,
        "\ub2e8\ud3b8\uc744 \uacbd\ub85c\uc640 \ub2e8\ud3b8 \ubaa8\ub450\uc5d0 \uc9c0\uc815\ud560 \uc218\ub294 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
   /** Cannot initialize URI with empty parameters   */
  //public static final int ER_CANNOT_INIT_URI_EMPTY_PARMS = 202;


  {
    ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "\ube48 \ub9e4\uac1c\ubcc0\uc218\ub85c\ub294 URI\ub97c \ucd08\uae30\ud654\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
  /**  Method not yet supported    */
  //public static final int ER_METHOD_NOT_SUPPORTED = 210;


  {
    ER_METHOD_NOT_SUPPORTED,
        "\uc9c0\uc6d0\ub418\uc9c0 \uc54a\ub294 \uba54\uc18c\ub4dc\uc785\ub2c8\ub2e4 "},


  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_INCRSAXSRCFILTER_NOT_RESTARTABLE = 214;


  {
    ER_INCRSAXSRCFILTER_NOT_RESTARTABLE,
     "IncrementalSAXSource_Filter\ub97c \ub2e4\uc2dc \uc2dc\uc791\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},

  
  /** IncrementalSAXSource_Filter not currently restartable   */
  //public static final int ER_XMLRDR_NOT_BEFORE_STARTPARSE = 215;


  {
    ER_XMLRDR_NOT_BEFORE_STARTPARSE,
     "startParse \uc694\uccad \uc804\uc5d0 XMLReader\ub97c \uc218\ud589\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4"},


// Axis traverser not supported: {0}
  //public static final int ER_AXIS_TRAVERSER_NOT_SUPPORTED = 235;

  {
    ER_AXIS_TRAVERSER_NOT_SUPPORTED,
     "\ucd95 \ud2b8\ub798\ubc84\uc11c\uac00 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc74c: {0}"},


// ListingErrorHandler created with null PrintWriter!
  //public static final int ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER = 236;

  {
    ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER,
     "ListingErrorHandler\uac00 \ub110 PrintWriter\ub85c \uc791\uc131\ub428!"},


  //public static final int ER_SYSTEMID_UNKNOWN = 240;

  {
    ER_SYSTEMID_UNKNOWN,
     "\uc2dc\uc2a4\ud15c ID\ub97c \uc54c \uc218 \uc5c6\uc74c"},


  // Location of error unknown
  //public static final int ER_LOCATION_UNKNOWN = 241;

  {
    ER_LOCATION_UNKNOWN,
     "\uc624\ub958 \uc704\uce58\ub97c \uc54c \uc218 \uc5c6\uc74c"},
 

  /** Field ER_PREFIX_MUST_RESOLVE          */
  //public static final int ER_PREFIX_MUST_RESOLVE = 52;

 
  {
    ER_PREFIX_MUST_RESOLVE,
      "\uc811\ub450\uc5b4\uac00 \uc774\ub984 \uacf5\uac04 {0}\uc73c\ub85c(\ub85c) \uacb0\uc815\ub418\uc5b4\uc57c \ud569\ub2c8\ub2e4."},
 

  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  //public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;

 
  {
    ER_CREATEDOCUMENT_NOT_SUPPORTED,
      "createDocument()\ub294 XPathContext\uc5d0\uc11c \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4!"},
 

  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;

 
  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT,
      "\uc790\uc2dd \uc18d\uc131\uc5d0 \uc18c\uc720\uc790 \ubb38\uc11c\uac00 \uc5c6\uc2b5\ub2c8\ub2e4!"},
 

  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  //public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;

 
  {
    ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT,
      "\uc790\uc2dd \uc18d\uc131\uc5d0 \uc18c\uc720\uc790 \ubb38\uc11c \uc694\uc18c\uac00 \uc5c6\uc2b5\ub2c8\ub2e4!"},
 

  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  //public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;

 
  {
    ER_CANT_OUTPUT_TEXT_BEFORE_DOC,
      "\uacbd\uace0: \ubb38\uc11c \uc694\uc18c \uc804\uc5d0 \ud14d\uc2a4\ud2b8\ub97c \ucd9c\ub825\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4! \ubb34\uc2dc\ub429\ub2c8\ub2e4..."},
 

  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  //public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;

 
  {
    ER_CANT_HAVE_MORE_THAN_ONE_ROOT,
      "DOM\uc5d0 \ub450 \uac1c \uc774\uc0c1\uc758 \ub8e8\ud2b8\ub97c \uac00\uc9c8 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4!"},
 
  
   /**  Argument 'localName' is null  */
  //public static final int ER_ARG_LOCALNAME_NULL = 70;

 
  {
    ER_ARG_LOCALNAME_NULL,
       "'localName' \uc778\uc790\uac00 \ub110\uc785\ub2c8\ub2e4"},
 

  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The localname is the portion after the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** localname in QNAME should be a valid NCName */
  //public static final int ER_ARG_LOCALNAME_INVALID = 101;

 
  {
    ER_ARG_LOCALNAME_INVALID,
       "QNAME\uc758 \ub85c\uceec \uc774\ub984\uc740 \uc720\ud6a8\ud55c NCName\uc774\uc5b4\uc57c \ud569\ub2c8\ub2e4."},
 
  
  // Note to translators:  A QNAME has the syntactic form [NCName:]NCName
  // The prefix is the portion before the optional colon; the message indicates
  // that there is a problem with that part of the QNAME.

  /** prefix in QNAME should be a valid NCName */
  //public static final int ER_ARG_PREFIX_INVALID = 102;

 
  {
    ER_ARG_PREFIX_INVALID,
       "QNAME\uc758 \uc811\ub450\uc5b4\ub294 \uc720\ud6a8\ud55c NCName\uc774\uc5b4\uc57c \ud569\ub2c8\ub2e4."},

  { "BAD_CODE",
      "createMessage\uc758 \ub9e4\uac1c\ubcc0\uc218\uac00 \ubc14\uc6b4\ub4dc\ub97c \ubc97\uc5b4\ub0ac\uc2b5\ub2c8\ub2e4."},
  { "FORMAT_FAILED",
      "messageFormat \ud638\ucd9c \uc2dc \uc608\uc678 \ubc1c\uc0dd"},
  { "line", "\ud589 #"},
  { "column", "\uc5f4 #"}
  
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