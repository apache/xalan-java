/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the  "License");
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
public class SerializerMessages_cs extends ListResourceBundle {

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
                "Kl\u00ed\u010d zpr\u00e1vy ''{0}'' nen\u00ed obsa\u017een ve t\u0159\u00edd\u011b zpr\u00e1v ''{1}''" },

            {   MsgKey.BAD_MSGFORMAT,
                "Form\u00e1t zpr\u00e1vy ''{0}'' ve t\u0159\u00edd\u011b zpr\u00e1v ''{1}'' selhal. " },

            {   MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
                "T\u0159\u00edda serializace ''{0}'' neimplementuje obslu\u017en\u00fd program org.xml.sax.ContentHandler." },

            {   MsgKey.ER_RESOURCE_COULD_NOT_FIND,
                    "Nelze naj\u00edt zdroj [ {0} ].\n {1}" },

            {   MsgKey.ER_RESOURCE_COULD_NOT_LOAD,
                    "Nelze zav\u00e9st zdroj [ {0} ]: {1} \n {2} \t {3}" },

            {   MsgKey.ER_BUFFER_SIZE_LESSTHAN_ZERO,
                    "Velikost vyrovn\u00e1vac\u00ed pam\u011bti <=0" },

            {   MsgKey.ER_INVALID_UTF16_SURROGATE,
                    "Byla zji\u0161t\u011bna neplatn\u00e1 n\u00e1hrada UTF-16: {0} ?" },

            {   MsgKey.ER_OIERROR,
                "Chyba vstupu/v\u00fdstupu" },

            {   MsgKey.ER_ILLEGAL_ATTRIBUTE_POSITION,
                "Nelze p\u0159idat atribut {0} po uzlech potomk\u016f ani p\u0159ed t\u00edm, ne\u017e je vytvo\u0159en prvek. Atribut bude ignorov\u00e1n." },

            /*
             * Note to translators:  The stylesheet contained a reference to a
             * namespace prefix that was undefined.  The value of the substitution
             * text is the name of the prefix.
             */
            {   MsgKey.ER_NAMESPACE_PREFIX,
                "Obor n\u00e1zv\u016f pro p\u0159edponu ''{0}'' nebyl deklarov\u00e1n." },

            /*
             * Note to translators:  This message is reported if the stylesheet
             * being processed attempted to construct an XML document with an
             * attribute in a place other than on an element.  The substitution text
             * specifies the name of the attribute.
             */
            {   MsgKey.ER_STRAY_ATTRIBUTE,
                "Atribut ''{0}'' se nach\u00e1z\u00ed vn\u011b prvku." },

            /*
             * Note to translators:  As with the preceding message, a namespace
             * declaration has the form of an attribute and is only permitted to
             * appear on an element.  The substitution text {0} is the namespace
             * prefix and {1} is the URI that was being used in the erroneous
             * namespace declaration.
             */
            {   MsgKey.ER_STRAY_NAMESPACE,
                "Deklarace oboru n\u00e1zv\u016f ''{0}''=''{1}'' se nach\u00e1z\u00ed vn\u011b prvku." },

            {   MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
                "Nelze zav\u00e9st prost\u0159edek ''{0}'' (zkontrolujte prom\u011bnnou CLASSPATH) - budou pou\u017eity pouze v\u00fdchoz\u00ed prost\u0159edky" },

            {   MsgKey.ER_ILLEGAL_CHARACTER,
                "Byl proveden pokus o v\u00fdstup znaku s celo\u010d\u00edselnou hodnotou {0}, kter\u00e1 nen\u00ed reprezentov\u00e1na v ur\u010den\u00e9m v\u00fdstupn\u00edm k\u00f3dov\u00e1n\u00ed {1}." },

            {   MsgKey.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
                "Nelze na\u010d\u00edst soubor vlastnost\u00ed ''{0}'' pro v\u00fdstupn\u00ed metodu ''{1}'' (zkontrolujte prom\u011bnnou CLASSPATH)." },

            {   MsgKey.ER_INVALID_PORT,
                "Neplatn\u00e9 \u010d\u00edslo portu." },

            {   MsgKey.ER_PORT_WHEN_HOST_NULL,
                "M\u00e1-li hostitel hodnotu null, nelze nastavit port." },

            {   MsgKey.ER_HOST_ADDRESS_NOT_WELLFORMED,
                "Adresa hostitele m\u00e1 nespr\u00e1vn\u00fd form\u00e1t." },

            {   MsgKey.ER_SCHEME_NOT_CONFORMANT,
                "Sch\u00e9ma nevyhovuje." },

            {   MsgKey.ER_SCHEME_FROM_NULL_STRING,
                "Nelze nastavit sch\u00e9ma \u0159et\u011bzce s hodnotou null." },

            {   MsgKey.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
                "Cesta obsahuje neplatnou escape sekvenci" },

            {   MsgKey.ER_PATH_INVALID_CHAR,
                "Cesta obsahuje neplatn\u00fd znak: {0}" },

            {   MsgKey.ER_FRAG_INVALID_CHAR,
                "Fragment obsahuje neplatn\u00fd znak." },

            {   MsgKey.ER_FRAG_WHEN_PATH_NULL,
                "M\u00e1-li cesta hodnotu null, nelze nastavit fragment." },

            {   MsgKey.ER_FRAG_FOR_GENERIC_URI,
                "Fragment lze nastavit jen u generick\u00e9ho URI." },

            {   MsgKey.ER_NO_SCHEME_IN_URI,
                "V URI nebylo nalezeno \u017e\u00e1dn\u00e9 sch\u00e9ma" },

            {   MsgKey.ER_CANNOT_INIT_URI_EMPTY_PARMS,
                "URI nelze inicializovat s pr\u00e1zdn\u00fdmi parametry." },

            {   MsgKey.ER_NO_FRAGMENT_STRING_IN_PATH,
                "Fragment nelze ur\u010dit z\u00e1rove\u0148 v cest\u011b i ve fragmentu." },

            {   MsgKey.ER_NO_QUERY_STRING_IN_PATH,
                "V \u0159et\u011bzci cesty a dotazu nelze zadat \u0159et\u011bzec dotazu." },

            {   MsgKey.ER_NO_PORT_IF_NO_HOST,
                "Nen\u00ed-li ur\u010den hostitel, nelze zadat port." },

            {   MsgKey.ER_NO_USERINFO_IF_NO_HOST,
                "Nen\u00ed-li ur\u010den hostitel, nelze zadat \u00fadaje o u\u017eivateli." },
            {   MsgKey.ER_XML_VERSION_NOT_SUPPORTED,
                "Varov\u00e1n\u00ed: Je po\u017eadov\u00e1na verze ''{0}'' v\u00fdstupn\u00edho dokumentu. Tato verze form\u00e1tu XML nen\u00ed podporov\u00e1na. Bude pou\u017eita verze ''1.0'' v\u00fdstupn\u00edho dokumentu. " },

            {   MsgKey.ER_SCHEME_REQUIRED,
                "Je vy\u017eadov\u00e1no sch\u00e9ma!" },

            /*
             * Note to translators:  The words 'Properties' and
             * 'SerializerFactory' in this message are Java class names
             * and should not be translated.
             */
            {   MsgKey.ER_FACTORY_PROPERTY_MISSING,
                "Objekt vlastnost\u00ed p\u0159edan\u00fd faktorii SerializerFactory neobsahuje vlastnost ''{0}''. " },

            {   MsgKey.ER_ENCODING_NOT_SUPPORTED,
                "Varov\u00e1n\u00ed: K\u00f3dov\u00e1n\u00ed ''{0}'' nen\u00ed v b\u011bhov\u00e9m prost\u0159ed\u00ed Java podporov\u00e1no." },


        };

        return contents;
    }
}
