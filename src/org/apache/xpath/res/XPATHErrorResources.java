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
package org.apache.xpath.res;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * <meta name="usage" content="advanced"/>
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first update the count of messages(MAX_CODE)or
 * the count of warnings(MAX_WARNING). The array will be
 * automatically filled in with the keys, but you need to
 * fill in the actual message string. Follow the instructions
 * below.
 */
public class XPATHErrorResources extends PropertyResourceBundle
{
  
  public XPATHErrorResources()
  	throws java.io.IOException
  {
    super(null);
  }
  
  public XPATHErrorResources(InputStream is) 
  	throws java.io.IOException
  {
  	super(is);
  }

  /** Field ERROR_SUFFIX          */
  public static final String ERROR_SUFFIX = "ER";

  /** Field WARNING_SUFFIX          */
  public static final String WARNING_SUFFIX = "WR";

//  /** Field MAX_CODE          */
//  public static final int MAX_CODE = 84;  // this is needed to keep track of the number of messages          
//
//  /** Field MAX_WARNING          */
//  public static final int MAX_WARNING = 11;  // this is needed to keep track of the number of warnings
//
//  /** Field MAX_OTHERS          */
//  public static final int MAX_OTHERS = 20;
//
//  /** Field MAX_MESSAGES          */
//  public static final int MAX_MESSAGES = MAX_CODE + MAX_WARNING + 1;
//
//  /** Field contents          */
//  static final Object[][] contents =
//    new Object[MAX_MESSAGES + MAX_OTHERS + 1][2];

  /*
  * Now fill in the message text.
  * First create an int for the message code. Make sure you
  * update MAX_CODE for error messages and MAX_WARNING for warnings
  * Then fill in the message text for that message code in the
  * array. Use the new error code as the index into the array.
  */

  // Error messages...

  /** Field ERROR0000          */
  public static final int ERROR0000 = 0;

  /** Field ER_CURRENT_NOT_ALLOWED_IN_MATCH          */
  public static final int ER_CURRENT_NOT_ALLOWED_IN_MATCH = 1;


  /** Field ER_CURRENT_TAKES_NO_ARGS          */
  public static final int ER_CURRENT_TAKES_NO_ARGS = 2;


  /** Field ER_DOCUMENT_REPLACED          */
  public static final int ER_DOCUMENT_REPLACED = 3;


  /** Field ER_CONTEXT_HAS_NO_OWNERDOC          */
  public static final int ER_CONTEXT_HAS_NO_OWNERDOC = 4;


  /** Field ER_LOCALNAME_HAS_TOO_MANY_ARGS          */
  public static final int ER_LOCALNAME_HAS_TOO_MANY_ARGS = 5;


  /** Field ER_NAMESPACEURI_HAS_TOO_MANY_ARGS          */
  public static final int ER_NAMESPACEURI_HAS_TOO_MANY_ARGS = 6;


  /** Field ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS          */
  public static final int ER_NORMALIZESPACE_HAS_TOO_MANY_ARGS = 7;


  /** Field ER_NUMBER_HAS_TOO_MANY_ARGS          */
  public static final int ER_NUMBER_HAS_TOO_MANY_ARGS = 8;


  /** Field ER_NAME_HAS_TOO_MANY_ARGS          */
  public static final int ER_NAME_HAS_TOO_MANY_ARGS = 9;


  /** Field ER_STRING_HAS_TOO_MANY_ARGS          */
  public static final int ER_STRING_HAS_TOO_MANY_ARGS = 10;


  /** Field ER_STRINGLENGTH_HAS_TOO_MANY_ARGS          */
  public static final int ER_STRINGLENGTH_HAS_TOO_MANY_ARGS = 11;


  /** Field ER_TRANSLATE_TAKES_3_ARGS          */
  public static final int ER_TRANSLATE_TAKES_3_ARGS = 12;


  /** Field ER_UNPARSEDENTITYURI_TAKES_1_ARG          */
  public static final int ER_UNPARSEDENTITYURI_TAKES_1_ARG = 13;


  /** Field ER_NAMESPACEAXIS_NOT_IMPLEMENTED          */
  public static final int ER_NAMESPACEAXIS_NOT_IMPLEMENTED = 14;


  /** Field ER_UNKNOWN_AXIS          */
  public static final int ER_UNKNOWN_AXIS = 15;


  /** Field ER_UNKNOWN_MATCH_OPERATION          */
  public static final int ER_UNKNOWN_MATCH_OPERATION = 16;


  /** Field ER_INCORRECT_ARG_LENGTH          */
  public static final int ER_INCORRECT_ARG_LENGTH = 17;


  /** Field ER_CANT_CONVERT_TO_NUMBER          */
  public static final int ER_CANT_CONVERT_TO_NUMBER = 18;


  /** Field ER_CANT_CONVERT_TO_NODELIST          */
  public static final int ER_CANT_CONVERT_TO_NODELIST = 19;


  /** Field ER_CANT_CONVERT_TO_MUTABLENODELIST          */
  public static final int ER_CANT_CONVERT_TO_MUTABLENODELIST = 20;


  /** Field ER_CANT_CONVERT_TO_TYPE          */
  public static final int ER_CANT_CONVERT_TO_TYPE = 21;


  /** Field ER_EXPECTED_MATCH_PATTERN          */
  public static final int ER_EXPECTED_MATCH_PATTERN = 22;


  /** Field ER_COULDNOT_GET_VAR_NAMED          */
  public static final int ER_COULDNOT_GET_VAR_NAMED = 23;


  /** Field ER_UNKNOWN_OPCODE          */
  public static final int ER_UNKNOWN_OPCODE = 24;


  /** Field ER_EXTRA_ILLEGAL_TOKENS          */
  public static final int ER_EXTRA_ILLEGAL_TOKENS = 25;


  /** Field ER_EXPECTED_DOUBLE_QUOTE          */
  public static final int ER_EXPECTED_DOUBLE_QUOTE = 26;


  /** Field ER_EXPECTED_SINGLE_QUOTE          */
  public static final int ER_EXPECTED_SINGLE_QUOTE = 27;


  /** Field ER_EMPTY_EXPRESSION          */
  public static final int ER_EMPTY_EXPRESSION = 28;


  /** Field ER_EXPECTED_BUT_FOUND          */
  public static final int ER_EXPECTED_BUT_FOUND = 29;


  /** Field ER_INCORRECT_PROGRAMMER_ASSERTION          */
  public static final int ER_INCORRECT_PROGRAMMER_ASSERTION = 30;


  /** Field ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL          */
  public static final int ER_BOOLEAN_ARG_NO_LONGER_OPTIONAL = 31;


  /** Field ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG          */
  public static final int ER_FOUND_COMMA_BUT_NO_PRECEDING_ARG = 32;


  /** Field ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG          */
  public static final int ER_FOUND_COMMA_BUT_NO_FOLLOWING_ARG = 33;


  /** Field ER_PREDICATE_ILLEGAL_SYNTAX          */
  public static final int ER_PREDICATE_ILLEGAL_SYNTAX = 34;


  /** Field ER_ILLEGAL_AXIS_NAME          */
  public static final int ER_ILLEGAL_AXIS_NAME = 35;


  /** Field ER_UNKNOWN_NODETYPE          */
  public static final int ER_UNKNOWN_NODETYPE = 36;


  /** Field ER_PATTERN_LITERAL_NEEDS_BE_QUOTED          */
  public static final int ER_PATTERN_LITERAL_NEEDS_BE_QUOTED = 37;


  /** Field ER_COULDNOT_BE_FORMATTED_TO_NUMBER          */
  public static final int ER_COULDNOT_BE_FORMATTED_TO_NUMBER = 38;


  /** Field ER_COULDNOT_CREATE_XMLPROCESSORLIAISON          */
  public static final int ER_COULDNOT_CREATE_XMLPROCESSORLIAISON = 39;


  /** Field ER_DIDNOT_FIND_XPATH_SELECT_EXP          */
  public static final int ER_DIDNOT_FIND_XPATH_SELECT_EXP = 40;


  /** Field ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH          */
  public static final int ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH = 41;


  /** Field ER_ERROR_OCCURED          */
  public static final int ER_ERROR_OCCURED = 42;


  /** Field ER_ILLEGAL_VARIABLE_REFERENCE          */
  public static final int ER_ILLEGAL_VARIABLE_REFERENCE = 43;


  /** Field ER_AXES_NOT_ALLOWED          */
  public static final int ER_AXES_NOT_ALLOWED = 44;


  /** Field ER_KEY_HAS_TOO_MANY_ARGS          */
  public static final int ER_KEY_HAS_TOO_MANY_ARGS = 45;


  /** Field ER_COUNT_TAKES_1_ARG          */
  public static final int ER_COUNT_TAKES_1_ARG = 46;


  /** Field ER_COULDNOT_FIND_FUNCTION          */
  public static final int ER_COULDNOT_FIND_FUNCTION = 47;


  /** Field ER_UNSUPPORTED_ENCODING          */
  public static final int ER_UNSUPPORTED_ENCODING = 48;


  /** Field ER_PROBLEM_IN_DTM_NEXTSIBLING          */
  public static final int ER_PROBLEM_IN_DTM_NEXTSIBLING = 49;


  /** Field ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL          */
  public static final int ER_CANNOT_WRITE_TO_EMPTYNODELISTIMPL = 50;


  /** Field ER_SETDOMFACTORY_NOT_SUPPORTED          */
  public static final int ER_SETDOMFACTORY_NOT_SUPPORTED = 51;


  /** Field ER_PREFIX_MUST_RESOLVE          */
  public static final int ER_PREFIX_MUST_RESOLVE = 52;


  /** Field ER_PARSE_NOT_SUPPORTED          */
  public static final int ER_PARSE_NOT_SUPPORTED = 53;


  /** Field ER_CREATEDOCUMENT_NOT_SUPPORTED          */
  public static final int ER_CREATEDOCUMENT_NOT_SUPPORTED = 54;


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT          */
  public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT = 55;


  /** Field ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT          */
  public static final int ER_CHILD_HAS_NO_OWNER_DOCUMENT_ELEMENT = 56;


  /** Field ER_SAX_API_NOT_HANDLED          */
  public static final int ER_SAX_API_NOT_HANDLED = 57;


  /** Field ER_IGNORABLE_WHITESPACE_NOT_HANDLED          */
  public static final int ER_IGNORABLE_WHITESPACE_NOT_HANDLED = 58;


  /** Field ER_DTM_CANNOT_HANDLE_NODES          */
  public static final int ER_DTM_CANNOT_HANDLE_NODES = 59;


  /** Field ER_XERCES_CANNOT_HANDLE_NODES          */
  public static final int ER_XERCES_CANNOT_HANDLE_NODES = 60;


  /** Field ER_XERCES_PARSE_ERROR_DETAILS          */
  public static final int ER_XERCES_PARSE_ERROR_DETAILS = 61;


  /** Field ER_XERCES_PARSE_ERROR          */
  public static final int ER_XERCES_PARSE_ERROR = 62;


  /** Field ER_CANT_OUTPUT_TEXT_BEFORE_DOC          */
  public static final int ER_CANT_OUTPUT_TEXT_BEFORE_DOC = 63;


  /** Field ER_CANT_HAVE_MORE_THAN_ONE_ROOT          */
  public static final int ER_CANT_HAVE_MORE_THAN_ONE_ROOT = 64;


  /** Field ER_INVALID_UTF16_SURROGATE          */
  public static final int ER_INVALID_UTF16_SURROGATE = 65;


  /** Field ER_OIERROR          */
  public static final int ER_OIERROR = 66;


  /** Field ER_CANNOT_CREATE_URL          */
  public static final int ER_CANNOT_CREATE_URL = 67;


  /** Field ER_XPATH_READOBJECT          */
  public static final int ER_XPATH_READOBJECT = 68;

  
  /** Field ER_XPATH_READOBJECT         */
  public static final int ER_FUNCTION_TOKEN_NOT_FOUND = 69;

  
   /**  Argument 'localName' is null  */
  public static final int ER_ARG_LOCALNAME_NULL = 70;

  
   /**  Can not deal with XPath type:   */
  public static final int ER_CANNOT_DEAL_XPATH_TYPE = 71;

  
   /**  This NodeSet is not mutable  */
  public static final int ER_NODESET_NOT_MUTABLE = 72;

  
   /**  This NodeSetDTM is not mutable  */
  public static final int ER_NODESETDTM_NOT_MUTABLE = 73;

  
   /**  Variable not resolvable:   */
  public static final int ER_VAR_NOT_RESOLVABLE = 74;

  
   /** Null error handler  */
  public static final int ER_NULL_ERROR_HANDLER = 75;

  
   /**  Programmer's assertion: unknown opcode  */
  public static final int ER_PROG_ASSERT_UNKNOWN_OPCODE = 76;

  
   /**  0 or 1   */
  public static final int ER_ZERO_OR_ONE = 77;

  
  
   /**  rtf() not supported by XRTreeFragSelectWrapper   */
  public static final int ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 78;

  
   /**  asNodeIterator() not supported by XRTreeFragSelectWrapper   */
  public static final int ER_ASNODEITERATOR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER = 79;

  
   /**  fsb() not supported for XStringForChars   */
  public static final int ER_FSB_NOT_SUPPORTED_XSTRINGFORCHARS = 80;

  
   /**  Could not find variable with the name of   */
  public static final int ER_COULD_NOT_FIND_VAR = 81;

  
   /**  XStringForChars can not take a string for an argument   */
  public static final int ER_XSTRINGFORCHARS_CANNOT_TAKE_STRING = 82;

  
   /**  The FastStringBuffer argument can not be null   */
  public static final int ER_FASTSTRINGBUFFER_CANNOT_BE_NULL = 83;

  
   /**  2 or 3   */
  public static final int ER_TWO_OR_THREE = 84;

  
// Variable accessed before it is bound!
  public static final int ER_VARIABLE_ACCESSED_BEFORE_BIND = 85;
  
  // XStringForFSB can not take a string for an argument!
  public static final int ER_FSB_CANNOT_TAKE_STRING = 86;
  
  // Error! Setting the root of a walker to null!
  public static final int ER_SETTING_WALKER_ROOT_TO_NULL = 87;
  
  // This NodeSetDTM can not iterate to a previous node!
  public static final int ER_NODESETDTM_CANNOT_ITERATE = 88;
  
  // This NodeSet can not iterate to a previous node!
  public static final int ER_NODESET_CANNOT_ITERATE = 89;

  // This NodeSetDTM can not do indexing or counting functions!
  public static final int ER_NODESETDTM_CANNOT_INDEX = 90;
  
  // This NodeSet can not do indexing or counting functions!
  public static final int ER_NODESET_CANNOT_INDEX = 91;

  // Can not call setShouldCacheNodes after nextNode has been called!
  public static final int ER_CANNOT_CALL_SETSHOULDCACHENODE = 92;
  
  // {0} only allows {1} arguments
  public static final int ER_ONLY_ALLOWS = 93;
  
  // Programmer's assertion in getNextStepPos: unknown stepType: {0}
  public static final int ER_UNKNOWN_STEP = 94;
  
  /** Problem with RelativeLocationPath */
  public static final int ER_EXPECTED_REL_LOC_PATH = 95;


   /** Problem with LocationPath */
  public static final int ER_EXPECTED_LOC_PATH = 96;


   /** Problem with Step */
  public static final int ER_EXPECTED_LOC_STEP = 97;


   /** Problem with NodeTest */
  public static final int ER_EXPECTED_NODE_TEST = 98;


   /** Expected step pattern */
  public static final int ER_EXPECTED_STEP_PATTERN = 99;

  
   /** Expected relative path pattern */
  public static final int ER_EXPECTED_REL_PATH_PATTERN = 100;
  
  /** localname in QNAME should be a valid NCName */  
  public static final int ER_ARG_LOCALNAME_INVALID = 101;

  /** prefix in QNAME should be a valid NCName */
  public static final int ER_ARG_PREFIX_INVALID = 102;

 



  
  // Warnings...

  /** Field WG_LOCALE_NAME_NOT_HANDLED          */
  public static final int WG_LOCALE_NAME_NOT_HANDLED = 1;


  /** Field WG_PROPERTY_NOT_SUPPORTED          */
  public static final int WG_PROPERTY_NOT_SUPPORTED = 2;


  /** Field WG_DONT_DO_ANYTHING_WITH_NS          */
  public static final int WG_DONT_DO_ANYTHING_WITH_NS = 3;


  /** Field WG_SECURITY_EXCEPTION          */
  public static final int WG_SECURITY_EXCEPTION = 4;


  /** Field WG_QUO_NO_LONGER_DEFINED          */
  public static final int WG_QUO_NO_LONGER_DEFINED = 5;


  /** Field WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST          */
  public static final int WG_NEED_DERIVED_OBJECT_TO_IMPLEMENT_NODETEST = 6;


  /** Field WG_FUNCTION_TOKEN_NOT_FOUND          */
  public static final int WG_FUNCTION_TOKEN_NOT_FOUND = 7;


  /** Field WG_COULDNOT_FIND_FUNCTION          */
  public static final int WG_COULDNOT_FIND_FUNCTION = 8;


  /** Field WG_CANNOT_MAKE_URL_FROM          */
  public static final int WG_CANNOT_MAKE_URL_FROM = 9;


  /** Field WG_EXPAND_ENTITIES_NOT_SUPPORTED          */
  public static final int WG_EXPAND_ENTITIES_NOT_SUPPORTED = 10;


  /** Field WG_ILLEGAL_VARIABLE_REFERENCE          */
  public static final int WG_ILLEGAL_VARIABLE_REFERENCE = 11;


  /** Field WG_UNSUPPORTED_ENCODING          */
  public static final int WG_UNSUPPORTED_ENCODING = 12;


  // Other miscellaneous text used inside the code...

  // ================= INFRASTRUCTURE ======================

  /** Field BAD_CODE          */
  public static final String BAD_CODE = "BAD_CODE";

  /** Field FORMAT_FAILED          */
  public static final String FORMAT_FAILED = "FORMAT_FAILED";

  /** Field ERROR_RESOURCES          */
  public static final String ERROR_RESOURCES =
    "org.apache.xpath.res.XPATHErrorResources";

  /** Field ERROR_STRING          */
  public static final String ERROR_STRING = "#error";

  /** Field ERROR_HEADER          */
  public static final String ERROR_HEADER = "Error: ";

  /** Field WARNING_HEADER          */
  public static final String WARNING_HEADER = "Warning: ";

  /** Field XSL_HEADER          */
  public static final String XSL_HEADER = "XSL ";

  /** Field XML_HEADER          */
  public static final String XML_HEADER = "XML ";

  /** Field QUERY_HEADER          */
  public static final String QUERY_HEADER = "PATTERN ";

//  /**
//   * Get the association list.
//   *
//   * @return The association list.
//   */
//  public Object[][] getContents()
//  {
//    return contents;
//  }

  /**
   * Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   * of ResourceBundle.getBundle().
   *
   * @param res the name of the resource to load.
   * @param locale the locale to prefer when searching for the bundle
   *
   * @param className Name of local-specific subclass.
   * @return the ResourceBundle
   * @throws MissingResourceException
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
   * Get the error string associated with the error code
   *
   * @param errorCode Error code
   *
   * @return error string associated with the given error code
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
   * Get the warning string associated with the error code
   *
   * @param errorCode Error code
   * 
   * @return warning string associated with the given error code
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
   * Get the key string for an error based on the integer representation.
   *
   * @param errorCode Error code
   * 
   * @return key string that may be used for lookup in the association table.
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
   * Get the key string for an warning based on the integer representation.
   *
   * @param errorCode Error code
   * 
   * @return key string that may be used for lookup in the association table.
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
