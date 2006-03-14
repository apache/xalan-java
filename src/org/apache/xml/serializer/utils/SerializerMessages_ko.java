/*
 * Copyright 1999-2006 The Apache Software Foundation.
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
 * has the required getContents() method that returns
 * an array of message-key/message associations.
 * <p>
 * The message keys are defined in {@link MsgKey}. The
 * messages that those keys map to are defined here.
 * <p>
 * The messages in the English version are intended to be
 * translated.
 *
 * This class is not a public API, it is only public because it is
 * used in org.apache.xml.serializer.
 *
 * @xsl.usage internal
 */
public class SerializerMessages_ko extends ListResourceBundle {

    /*
     * This file contains error and warning messages related to
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

    /** The lookup table for error messages.   */
    public Object[][] getContents() {
        Object[][] contents = new Object[][] {
            {   MsgKey.BAD_MSGKEY,
                "''{0}'' \uba54\uc2dc\uc9c0 \ud0a4\uac00 ''{1}'' \uba54\uc2dc\uc9c0 \ud074\ub798\uc2a4\uc5d0 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.BAD_MSGFORMAT,
                "''{1}'' \uba54\uc2dc\uc9c0 \ud074\ub798\uc2a4\uc5d0 \uc788\ub294 ''{0}'' \uba54\uc2dc\uc9c0\uc758 \ud615\uc2dd\uc774 \uc798\ubabb \ub418\uc5c8\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
                "''{0}'' \uc9c1\ub82c \ud504\ub85c\uadf8\ub7a8 \ud074\ub798\uc2a4\uac00 org.xml.sax.ContentHandler\ub97c \uad6c\ud604\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_RESOURCE_COULD_NOT_FIND,
                    "[ {0} ] \uc790\uc6d0\uc744 \ucc3e\uc744 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4.\n{1}" },

            {   MsgKey.ER_RESOURCE_COULD_NOT_LOAD,
                    "[ {0} ] \uc790\uc6d0\uc774 {1} \n {2} \t {3}\uc744(\ub97c) \ub85c\ub4dc\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_BUFFER_SIZE_LESSTHAN_ZERO,
                    "\ubc84\ud37c \ud06c\uae30 <=0" },

            {   MsgKey.ER_INVALID_UTF16_SURROGATE,
                    "\uc798\ubabb\ub41c UTF-16 \ub300\ub9ac\uc790(surrogate)\uac00 \ubc1c\uacac\ub418\uc5c8\uc2b5\ub2c8\ub2e4: {0} ?" },

            {   MsgKey.ER_OIERROR,
                "IO \uc624\ub958" },

            {   MsgKey.ER_ILLEGAL_ATTRIBUTE_POSITION,
                "\ud558\uc704 \ub178\ub4dc\uac00 \uc0dd\uc131\ub41c \uc774\ud6c4 \ub610\ub294 \uc694\uc18c\uac00 \uc791\uc131\ub418\uae30 \uc774\uc804\uc5d0 {0} \uc18d\uc131\uc744 \ucd94\uac00\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4. \uc18d\uc131\uc774 \ubb34\uc2dc\ub429\ub2c8\ub2e4." },

            /*
             * Note to translators:  The stylesheet contained a reference to a
             * namespace prefix that was undefined.  The value of the substitution
             * text is the name of the prefix.
             */
            {   MsgKey.ER_NAMESPACE_PREFIX,
                "''{0}'' \uc811\ub450\ubd80\uc5d0 \ub300\ud55c \uc774\ub984 \uacf5\uac04\uc774 \uc120\uc5b8\ub418\uc9c0 \uc54a\uc558\uc2b5\ub2c8\ub2e4." },

            /*
             * Note to translators:  This message is reported if the stylesheet
             * being processed attempted to construct an XML document with an
             * attribute in a place other than on an element.  The substitution text
             * specifies the name of the attribute.
             */
            {   MsgKey.ER_STRAY_ATTRIBUTE,
                "''{0}'' \uc18d\uc131\uc774 \uc694\uc18c\uc758 \uc678\ubd80\uc5d0 \uc788\uc2b5\ub2c8\ub2e4." },

            /*
             * Note to translators:  As with the preceding message, a namespace
             * declaration has the form of an attribute and is only permitted to
             * appear on an element.  The substitution text {0} is the namespace
             * prefix and {1} is the URI that was being used in the erroneous
             * namespace declaration.
             */
            {   MsgKey.ER_STRAY_NAMESPACE,
                "''{0}''=''{1}'' \uc774\ub984 \uacf5\uac04 \uc120\uc5b8\uc774 \uc694\uc18c\uc758 \uc678\ubd80\uc5d0 \uc788\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
                "''{0}''(CLASSPATH \ud655\uc778)\uc744(\ub97c) \ub85c\ub4dc\ud560 \uc218 \uc5c6\uc73c\ubbc0\ub85c, \ud604\uc7ac \uae30\ubcf8\uac12\ub9cc\uc744 \uc0ac\uc6a9\ud558\ub294 \uc911\uc785\ub2c8\ub2e4." },

            {   MsgKey.ER_ILLEGAL_CHARACTER,
                "{1}\uc758 \uc9c0\uc815\ub41c \ucd9c\ub825 \uc778\ucf54\ub529\uc5d0 \ud45c\uc2dc\ub418\uc9c0 \uc54a\uc740 \ubb34\uacb0\uc131 \uac12 {0}\uc758 \ubb38\uc790\ub97c \ucd9c\ub825\ud558\uc2ed\uc2dc\uc624. " },

            {   MsgKey.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
                "''{1}'' \ucd9c\ub825 \uba54\uc18c\ub4dc(CLASSPATH \ud655\uc778)\uc5d0 \ub300\ud55c ''{0}'' \ud2b9\uc131 \ud30c\uc77c\uc744 \ub85c\ub4dc\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_INVALID_PORT,
                "\uc798\ubabb\ub41c \ud3ec\ud2b8 \ubc88\ud638" },

            {   MsgKey.ER_PORT_WHEN_HOST_NULL,
                "\ud638\uc2a4\ud2b8\uac00 \ub110(null)\uc774\uba74 \ud3ec\ud2b8\ub97c \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_HOST_ADDRESS_NOT_WELLFORMED,
                "\ud638\uc2a4\ud2b8\uac00 \uc644\uc804\ud55c \uc8fc\uc18c\uac00 \uc544\ub2d9\ub2c8\ub2e4." },

            {   MsgKey.ER_SCHEME_NOT_CONFORMANT,
                "\uc2a4\ud0a4\ub9c8\uac00 \uc77c\uce58\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_SCHEME_FROM_NULL_STRING,
                "\ub110(null) \ubb38\uc790\uc5f4\uc5d0\uc11c \uc2a4\ud0a4\ub9c8\ub97c \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
                "\uacbd\ub85c\uc5d0 \uc798\ubabb\ub41c \uc774\uc2a4\ucf00\uc774\ud504 \uc21c\uc11c\uac00 \uc788\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_PATH_INVALID_CHAR,
                "\uacbd\ub85c\uc5d0 \uc798\ubabb\ub41c \ubb38\uc790\uac00 \uc788\uc2b5\ub2c8\ub2e4: {0}" },

            {   MsgKey.ER_FRAG_INVALID_CHAR,
                "\ub2e8\ud3b8\uc5d0 \uc798\ubabb\ub41c \ubb38\uc790\uac00 \uc788\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_FRAG_WHEN_PATH_NULL,
                "\uacbd\ub85c\uac00 \ub110(null)\uc774\uba74 \ub2e8\ud3b8\uc744 \uc124\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_FRAG_FOR_GENERIC_URI,
                "\uc77c\ubc18 URI\uc5d0 \ub300\ud574\uc11c\ub9cc \ub2e8\ud3b8\uc744 \uc124\uc815\ud560 \uc218 \uc788\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_NO_SCHEME_IN_URI,
                "URI\uc5d0 \uc2a4\ud0a4\ub9c8\uac00 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_CANNOT_INIT_URI_EMPTY_PARMS,
                "\ube48 \ub9e4\uac1c\ubcc0\uc218\ub85c URI\ub97c \ucd08\uae30\ud654\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_NO_FRAGMENT_STRING_IN_PATH,
                "\uacbd\ub85c \ubc0f \ub2e8\ud3b8 \ub458 \ub2e4\uc5d0 \ub2e8\ud3b8\uc744 \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_NO_QUERY_STRING_IN_PATH,
                "\uacbd\ub85c \ubc0f \uc870\ud68c \ubb38\uc790\uc5f4\uc5d0 \uc870\ud68c \ubb38\uc790\uc5f4\uc744 \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_NO_PORT_IF_NO_HOST,
                "\ud638\uc2a4\ud2b8\ub97c \uc9c0\uc815\ud558\uc9c0 \uc54a\uc740 \uacbd\uc6b0\uc5d0\ub294 \ud3ec\ud2b8\ub97c \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_NO_USERINFO_IF_NO_HOST,
                "\ud638\uc2a4\ud2b8\ub97c \uc9c0\uc815\ud558\uc9c0 \uc54a\uc740 \uacbd\uc6b0\uc5d0\ub294 Userinfo\ub97c \uc9c0\uc815\ud560 \uc218 \uc5c6\uc2b5\ub2c8\ub2e4." },
            {   MsgKey.ER_XML_VERSION_NOT_SUPPORTED,
                "\uacbd\uace0:  \uc694\uccad\ub41c \ucd9c\ub825 \ubb38\uc11c\uc758 \ubc84\uc804\uc740 ''{0}''\uc785\ub2c8\ub2e4. \ud558\uc9c0\ub9cc \uc774 XML \ubc84\uc804\uc740 \uc9c0\uc6d0\ub418\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4. \ucd9c\ub825 \ubb38\uc11c\uc758 \ubc84\uc804\uc740 ''1.0''\uc774 \ub429\ub2c8\ub2e4." },

            {   MsgKey.ER_SCHEME_REQUIRED,
                "\uc2a4\ud0a4\ub9c8\uac00 \ud544\uc694\ud569\ub2c8\ub2e4." },

            /*
             * Note to translators:  The words 'Properties' and
             * 'SerializerFactory' in this message are Java class names
             * and should not be translated.
             */
            {   MsgKey.ER_FACTORY_PROPERTY_MISSING,
                "SerializerFactory\uc5d0 \uc804\ub2ec\ub41c \ud2b9\uc131 \uc624\ube0c\uc81d\ud2b8\uc5d0 ''{0}'' \ud2b9\uc131\uc774 \uc5c6\uc2b5\ub2c8\ub2e4." },

            {   MsgKey.ER_ENCODING_NOT_SUPPORTED,
                "\uacbd\uace0: ''{0}'' \uc778\ucf54\ub529\uc740 Java Runtime\uc744 \uc9c0\uc6d0\ud558\uc9c0 \uc54a\uc2b5\ub2c8\ub2e4." },


        };

        return contents;
    }
}
