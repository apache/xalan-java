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
public class SerializerMessages_ca extends ListResourceBundle {

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
                "La clau del missatge ''{0}'' no est\u00e0 a la classe del missatge ''{1}''" },

            {   MsgKey.BAD_MSGFORMAT,
                "El format del missatge ''{0}'' a la classe del missatge ''{1}'' ha fallat." },

            {   MsgKey.ER_SERIALIZER_NOT_CONTENTHANDLER,
                "La classe de serialitzador ''{0}'' no implementa org.xml.sax.ContentHandler." },

            {   MsgKey.ER_RESOURCE_COULD_NOT_FIND,
                    "No s''ha trobat el recurs [ {0} ].\n {1}" },

            {   MsgKey.ER_RESOURCE_COULD_NOT_LOAD,
                    "No s''ha pogut carregar el recurs [ {0} ]: {1} \n {2} \t {3}" },

            {   MsgKey.ER_BUFFER_SIZE_LESSTHAN_ZERO,
                    "Grand\u00e0ria del buffer <=0" },

            {   MsgKey.ER_INVALID_UTF16_SURROGATE,
                    "S''ha detectat un suplent UTF-16 no v\u00e0lid: {0} ?" },

            {   MsgKey.ER_OIERROR,
                "Error d'E/S" },

            {   MsgKey.ER_ILLEGAL_ATTRIBUTE_POSITION,
                "No es pot afegir l''atribut {0} despr\u00e9s dels nodes subordinats o abans que es produeixi un element. Es passar\u00e0 per alt l''atribut." },

            /*
             * Note to translators:  The stylesheet contained a reference to a
             * namespace prefix that was undefined.  The value of the substitution
             * text is the name of the prefix.
             */
            {   MsgKey.ER_NAMESPACE_PREFIX,
                "No s''ha declarat l''espai de noms pel prefix ''{0}''." },

            /*
             * Note to translators:  This message is reported if the stylesheet
             * being processed attempted to construct an XML document with an
             * attribute in a place other than on an element.  The substitution text
             * specifies the name of the attribute.
             */
            {   MsgKey.ER_STRAY_ATTRIBUTE,
                "L''atribut ''{0}'' es troba fora de l''element." },

            /*
             * Note to translators:  As with the preceding message, a namespace
             * declaration has the form of an attribute and is only permitted to
             * appear on an element.  The substitution text {0} is the namespace
             * prefix and {1} is the URI that was being used in the erroneous
             * namespace declaration.
             */
            {   MsgKey.ER_STRAY_NAMESPACE,
                "La declaraci\u00f3 de l''espai de noms ''{0}''=''{1}'' es troba fora de l''element." },

            {   MsgKey.ER_COULD_NOT_LOAD_RESOURCE,
                "No s''ha pogut carregar ''{0}'' (comproveu CLASSPATH), ara s''est\u00e0 fent servir els valors per defecte." },

            {   MsgKey.ER_ILLEGAL_CHARACTER,
                "S''ha intentat un car\u00e0cter de sortida del valor integral {0} que no est\u00e0 representat a una codificaci\u00f3 de sortida especificada de {1}." },

            {   MsgKey.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
                "No s''ha pogut carregar el fitxer de propietats ''{0}'' del m\u00e8tode de sortida ''{1}'' (comproveu CLASSPATH)" },

            {   MsgKey.ER_INVALID_PORT,
                "N\u00famero de port no v\u00e0lid" },

            {   MsgKey.ER_PORT_WHEN_HOST_NULL,
                "El port no es pot establir quan el sistema principal \u00e9s nul" },

            {   MsgKey.ER_HOST_ADDRESS_NOT_WELLFORMED,
                "El format de l'adre\u00e7a del sistema principal no \u00e9s el correcte" },

            {   MsgKey.ER_SCHEME_NOT_CONFORMANT,
                "L'esquema no t\u00e9 conformitat." },

            {   MsgKey.ER_SCHEME_FROM_NULL_STRING,
                "No es pot establir un esquema des d'una cadena nul\u00b7la" },

            {   MsgKey.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
                "La via d'acc\u00e9s cont\u00e9 una seq\u00fc\u00e8ncia d'escapament no v\u00e0lida" },

            {   MsgKey.ER_PATH_INVALID_CHAR,
                "La via d''acc\u00e9s cont\u00e9 un car\u00e0cter no v\u00e0lid {0}" },

            {   MsgKey.ER_FRAG_INVALID_CHAR,
                "El fragment cont\u00e9 un car\u00e0cter no v\u00e0lid" },

            {   MsgKey.ER_FRAG_WHEN_PATH_NULL,
                "El fragment no es pot establir si la via d'acc\u00e9s \u00e9s nul\u00b7la" },

            {   MsgKey.ER_FRAG_FOR_GENERIC_URI,
                "El fragment nom\u00e9s es pot establir per a un URI gen\u00e8ric" },

            {   MsgKey.ER_NO_SCHEME_IN_URI,
                "No s'ha trobat cap esquema a l'URI" },

            {   MsgKey.ER_CANNOT_INIT_URI_EMPTY_PARMS,
                "No es pot inicialitzar l'URI amb par\u00e0metres buits" },

            {   MsgKey.ER_NO_FRAGMENT_STRING_IN_PATH,
                "No es pot especificar un fragment tant en la via d'acc\u00e9s com en el fragment" },

            {   MsgKey.ER_NO_QUERY_STRING_IN_PATH,
                "No es pot especificar una cadena de consulta en la via d'acc\u00e9s i la cadena de consulta" },

            {   MsgKey.ER_NO_PORT_IF_NO_HOST,
                "No es pot especificar el port si no s'especifica el sistema principal" },

            {   MsgKey.ER_NO_USERINFO_IF_NO_HOST,
                "No es pot especificar informaci\u00f3 de l'usuari si no s'especifica el sistema principal" },
            {   MsgKey.ER_XML_VERSION_NOT_SUPPORTED,
                "Av\u00eds: la versi\u00f3 del document de sortida s''ha sol\u00b7licitat que sigui ''{0}''. Aquesta versi\u00f3 de XML no est\u00e0 suportada. La versi\u00f3 del document de sortida ser\u00e0 ''1.0''." },

            {   MsgKey.ER_SCHEME_REQUIRED,
                "Es necessita l'esquema" },

            /*
             * Note to translators:  The words 'Properties' and
             * 'SerializerFactory' in this message are Java class names
             * and should not be translated.
             */
            {   MsgKey.ER_FACTORY_PROPERTY_MISSING,
                "L''objecte de propietats passat a SerializerFactory no t\u00e9 cap propietat ''{0}''." },

            {   MsgKey.ER_ENCODING_NOT_SUPPORTED,
                "Av\u00eds: el temps d''execuci\u00f3 de Java no d\u00f3na suport a la codificaci\u00f3 ''{0}''." },


        };

        return contents;
    }
}
