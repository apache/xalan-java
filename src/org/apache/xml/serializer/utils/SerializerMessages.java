/*
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id$
 */
package org.apache.xml.serializer.utils;


import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * An instance of this class is a ListResourceBundle that
 * holds "static final String" keys names to look get the
 * up the
 * kto look up the messages
 * Set up error messages.
 * We build a two dimensional array of message keys and
 * message strings. In order to add a new message here,
 * you need to first add a String constant. And you need
 * to enter key, value pair as part of the contents
 * array. 
 * 
 * This class is not a public API, it is only public because it is 
 * used in org.apache.xml.serializer.
 * 
 * @xsl.usage internal
 */
public class SerializerMessages extends ListResourceBundle
{

/*
 * This file contains error and warning messages related to Xalan
 * Serializer Error Handling.
 *
 *  General notes to translators:

 *  1) A stylesheet is a description of how to transform an input XML document
 *     into a resultant XML document (or HTML document or text).  The
 *     stylesheet itself is described in the form of an XML document.

 *
 *  2) An element is a mark-up tag in an XML document; an attribute is a
 *     modifier on the tag.  For example, in <elem attr='val' attr2='val2'>
 *     "elem" is an element name, "attr" and "attr2" are attribute names with
 *     the values "val" and "val2", respectively.
 *
 *  3) A namespace declaration is a special attribute that is used to associate
 *     a prefix with a URI (the namespace).  The meanings of element names and
 *     attribute names that use that prefix are defined with respect to that
 *     namespace.
 *
 *
 */
 
  /* 
   * Message keys
   */


    // Message keys required by the message utility class (in case it has problems
  // with messages in this class)
  public static String BAD_MSGKEY = "BAD_CODE";

  /** String to use if the message format operation failed.  */
  public static String BAD_MSGFORMAT = "FORMAT_FAILED";
  
  // Message keys used by the serializer
  public static final String ER_RESOURCE_COULD_NOT_FIND = "ER_RESOURCE_COULD_NOT_FIND";
  public static final String ER_RESOURCE_COULD_NOT_LOAD = "ER_RESOURCE_COULD_NOT_LOAD";
  public static final String ER_BUFFER_SIZE_LESSTHAN_ZERO = "ER_BUFFER_SIZE_LESSTHAN_ZERO";
  public static final String ER_INVALID_UTF16_SURROGATE = "ER_INVALID_UTF16_SURROGATE";
  public static final String ER_OIERROR = "ER_OIERROR";
  public static final String ER_NAMESPACE_PREFIX = "ER_NAMESPACE_PREFIX";
  public static final String ER_STRAY_ATTRIBUTE = "ER_STRAY_ATTIRBUTE";
  public static final String ER_STRAY_NAMESPACE = "ER_STRAY_NAMESPACE";
  public static final String ER_COULD_NOT_LOAD_RESOURCE = "ER_COULD_NOT_LOAD_RESOURCE";
  public static final String ER_COULD_NOT_LOAD_METHOD_PROPERTY = "ER_COULD_NOT_LOAD_METHOD_PROPERTY";
  public static final String ER_SERIALIZER_NOT_CONTENTHANDLER = "ER_SERIALIZER_NOT_CONTENTHANDLER";
  public static final String ER_ILLEGAL_ATTRIBUTE_POSITION = "ER_ILLEGAL_ATTRIBUTE_POSITION";
  public static final String ER_ILLEGAL_CHARACTER = "ER_ILLEGAL_CHARACTER";
  

  public static String ER_INVALID_PORT = "ER_INVALID_PORT";
  public static String ER_PORT_WHEN_HOST_NULL = "ER_PORT_WHEN_HOST_NULL";
  public static String ER_HOST_ADDRESS_NOT_WELLFORMED = "ER_HOST_ADDRESS_NOT_WELLFORMED";
  public static String ER_SCHEME_NOT_CONFORMANT = "ER_SCHEME_NOT_CONFORMANT";
  public static String ER_SCHEME_FROM_NULL_STRING = "ER_SCHEME_FROM_NULL_STRING";
  public static String ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE = "ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE";
  public static String ER_PATH_INVALID_CHAR = "ER_PATH_INVALID_CHAR";
  public static String ER_NO_SCHEME_INURI = "ER_NO_SCHEME_INURI";
  public static String ER_FRAG_INVALID_CHAR = "ER_FRAG_INVALID_CHAR";
  public static String ER_FRAG_WHEN_PATH_NULL = "ER_FRAG_WHEN_PATH_NULL";
  public static String ER_FRAG_FOR_GENERIC_URI = "ER_FRAG_FOR_GENERIC_URI";
  public static String ER_NO_SCHEME_IN_URI = "ER_NO_SCHEME_IN_URI";
  public static String ER_CANNOT_INIT_URI_EMPTY_PARMS = "ER_CANNOT_INIT_URI_EMPTY_PARMS";
  public static String ER_NO_FRAGMENT_STRING_IN_PATH = "ER_NO_FRAGMENT_STRING_IN_PATH";
  public static String ER_NO_QUERY_STRING_IN_PATH = "ER_NO_QUERY_STRING_IN_PATH";
  public static String ER_NO_PORT_IF_NO_HOST = "ER_NO_PORT_IF_NO_HOST";
  public static String ER_NO_USERINFO_IF_NO_HOST = "ER_NO_USERINFO_IF_NO_HOST";
  public static String ER_SCHEME_REQUIRED = "ER_SCHEME_REQUIRED";

  /*
   * Now fill in the message text.
   * Then fill in the message text for that message code in the
   * array. Use the new error code as the index into the array.
   */

  // Error messages...

  /** The lookup table for error messages.   */
  public static final Object[][] contents = {        
    {BAD_MSGKEY, 
      "The message key ''{0}'' is not in the message class ''{1}''"},
    
    {BAD_MSGFORMAT, 
      "The format of message ''{0}'' in message class ''{1}'' failed." },
    
    {ER_SERIALIZER_NOT_CONTENTHANDLER,
      "The serializer class ''{0}'' does not implement org.xml.sax.ContentHandler."},
    
    {ER_RESOURCE_COULD_NOT_FIND,
      "The resource [ {0} ] could not be found.\n {1}" },
    
    {ER_RESOURCE_COULD_NOT_LOAD,
      "The resource [ {0} ] could not load: {1} \n {2} \t {3}" },
    
    {ER_BUFFER_SIZE_LESSTHAN_ZERO,
      "Buffer size <=0" },
    
    {ER_INVALID_UTF16_SURROGATE,
      "Invalid UTF-16 surrogate detected: {0} ?" },
    
    {ER_OIERROR,
      "IO error" },
    
    {ER_ILLEGAL_ATTRIBUTE_POSITION,
      "Cannot add attribute {0} after child nodes or before an element is produced.  Attribute will be ignored."},

      /*
       * Note to translators:  The stylesheet contained a reference to a
       * namespace prefix that was undefined.  The value of the substitution
       * text is the name of the prefix.
       */
    {ER_NAMESPACE_PREFIX,
      "Namespace for prefix ''{0}'' has not been declared." },
      /*
       * Note to translators:  This message is reported if the stylesheet
       * being processed attempted to construct an XML document with an
       * attribute in a place other than on an element.  The substitution text
       * specifies the name of the attribute.
       */
    {ER_STRAY_ATTRIBUTE,
      "Attribute ''{0}'' outside of element." },

      /*
       * Note to translators:  As with the preceding message, a namespace
       * declaration has the form of an attribute and is only permitted to
       * appear on an element.  The substitution text {0} is the namespace
       * prefix and {1} is the URI that was being used in the erroneous
       * namespace declaration.
       */
    {ER_STRAY_NAMESPACE,
      "Namespace declaration ''{0}''=''{1}'' outside of element." },

    {ER_COULD_NOT_LOAD_RESOURCE,
      "Could not load ''{0}'' (check CLASSPATH), now using just the defaults"},

    { ER_ILLEGAL_CHARACTER,
       "Attempt to output character of integral value {0} that is not represented in specified output encoding of {1}."},
    
    {ER_COULD_NOT_LOAD_METHOD_PROPERTY,
      "Could not load the propery file ''{0}'' for output method ''{1}'' (check CLASSPATH)" },
      
      
    { ER_INVALID_PORT , "Invalid port number" },
    { ER_PORT_WHEN_HOST_NULL , "Port cannot be set when host is null" },
    { ER_HOST_ADDRESS_NOT_WELLFORMED , "Host is not a well formed address" }, 
    { ER_SCHEME_NOT_CONFORMANT , "The scheme is not conformant." }, 
    { ER_SCHEME_FROM_NULL_STRING , "Cannot set scheme from null string" }, 
    { ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE , "Path contains invalid escape sequence" },
    { ER_PATH_INVALID_CHAR , "Path contains invalid character: {0}" }, 
    { ER_FRAG_INVALID_CHAR , "Fragment contains invalid character" },
    { ER_FRAG_WHEN_PATH_NULL , "Fragment cannot be set when path is null" },
    { ER_FRAG_FOR_GENERIC_URI , "Fragment can only be set for a generic URI" },
    { ER_NO_SCHEME_IN_URI , "No scheme found in URI" },
    { ER_CANNOT_INIT_URI_EMPTY_PARMS , "Cannot initialize URI with empty parameters" },
    { ER_NO_FRAGMENT_STRING_IN_PATH , "Fragment cannot be specified in both the path and fragment" }, 
    { ER_NO_QUERY_STRING_IN_PATH , "Query string cannot be specified in path and query string" }, 
    { ER_NO_PORT_IF_NO_HOST , "Port may not be specified if host is not specified" }, 
    { ER_NO_USERINFO_IF_NO_HOST , "Userinfo may not be specified if host is not specified" }, 
    { ER_SCHEME_REQUIRED , "Scheme is required!" } 
    
  
  };

  /**
   * Get the association list.
   * This method is needed to define the abstract method
   * in a base class. 
   *
   * @return The association list.
   */
  public Object[][] getContents()
  {
    return contents;
  }
}
