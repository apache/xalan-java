/*
 * Copyright 1999-2004 The Apache Software Foundation.
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

public class SerializerMessages_ca extends ListResourceBundle {
  public Object[][] getContents() {
    Object[][] contents =  new Object[][] {
      { SerializerMessages.BAD_MSGKEY,
        "The message key ''{0}'' is not in the message class ''{1}''"},

      { SerializerMessages.BAD_MSGFORMAT,
        "The format of message ''{0}'' in message class ''{1}'' failed."},

      { SerializerMessages.ER_SERIALIZER_NOT_CONTENTHANDLER,
        "The serializer class ''{0}'' does not implement org.xml.sax.ContentHandler."},

      { SerializerMessages.ER_RESOURCE_COULD_NOT_FIND,
        "The resource [ {0} ] could not be found.\n {1}"},

      { SerializerMessages.ER_RESOURCE_COULD_NOT_LOAD,
        "The resource [ {0} ] could not load: {1} \n {2} \n {3}"},

      { SerializerMessages.ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "Buffer size <=0"},

      { SerializerMessages.ER_INVALID_UTF16_SURROGATE,
        "Invalid UTF-16 surrogate detected: {0} ?"},

      { SerializerMessages.ER_OIERROR,
        "IO error"},

      { SerializerMessages.ER_ILLEGAL_ATTRIBUTE_POSITION,
        "Cannot add attribute {0} after child nodes or before an element is produced.  Attribute will be ignored."},

      { SerializerMessages.ER_NAMESPACE_PREFIX,
        "Namespace for prefix ''{0}'' has not been declared."},

      { SerializerMessages.ER_STRAY_ATTRIBUTE,
        "Attribute ''{0}'' outside of element."},

      { SerializerMessages.ER_STRAY_NAMESPACE,
        "Namespace declaration ''{0}''=''{1}'' outside of element."},

      { SerializerMessages.ER_COULD_NOT_LOAD_RESOURCE,
        "Could not load ''{0}'' (check CLASSPATH), now using just the defaults"},

      { SerializerMessages.ER_ILLEGAL_CHARACTER,
        "Attempt to output character of integral value {0} that is not represented in specified output encoding of {1}."},

      { SerializerMessages.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
        "Could not load the propery file ''{0}'' for output method ''{1}'' (check CLASSPATH)"},

      { SerializerMessages.ER_INVALID_PORT,
        "Invalid port number"},

      { SerializerMessages.ER_PORT_WHEN_HOST_NULL,
        "Port cannot be set when host is null"},

      { SerializerMessages.ER_HOST_ADDRESS_NOT_WELLFORMED,
        "Host is not a well formed address"},

      { SerializerMessages.ER_SCHEME_NOT_CONFORMANT,
        "The scheme is not conformant."},

      { SerializerMessages.ER_SCHEME_FROM_NULL_STRING,
        "Cannot set scheme from null string"},

      { SerializerMessages.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
        "Path contains invalid escape sequence"},

      { SerializerMessages.ER_PATH_INVALID_CHAR,
        "Path contains invalid character: {0}"},

      { SerializerMessages.ER_FRAG_INVALID_CHAR,
        "Fragment contains invalid character"},

      { SerializerMessages.ER_FRAG_WHEN_PATH_NULL,
        "Fragment cannot be set when path is null"},

      { SerializerMessages.ER_FRAG_FOR_GENERIC_URI,
        "Fragment can only be set for a generic URI"},

      { SerializerMessages.ER_NO_SCHEME_IN_URI,
        "No scheme found in URI"},

      { SerializerMessages.ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "Cannot initialize URI with empty parameters"},

      { SerializerMessages.ER_NO_FRAGMENT_STRING_IN_PATH,
        "Fragment cannot be specified in both the path and fragment"},

      { SerializerMessages.ER_NO_QUERY_STRING_IN_PATH,
        "Query string cannot be specified in path and query string"},

      { SerializerMessages.ER_NO_PORT_IF_NO_HOST,
        "Port may not be specified if host is not specified"},

      { SerializerMessages.ER_NO_USERINFO_IF_NO_HOST,
        "Userinfo may not be specified if host is not specified"},

      { SerializerMessages.ER_SCHEME_REQUIRED,
        "Scheme is required!"}

    };
    return contents;
  }
}
