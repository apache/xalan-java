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

public class SerializerMessages_es extends ListResourceBundle {
  public Object[][] getContents() {
    Object[][] contents =  new Object[][] {
        // BAD_MSGKEY needs translation
        // BAD_MSGFORMAT needs translation
      { SerializerMessages.ER_SERIALIZER_NOT_CONTENTHANDLER,
        "La clase serializer ''{0}'' no implementa org.xml.sax.ContentHandler."},

      { SerializerMessages.ER_RESOURCE_COULD_NOT_FIND,
        "No se ha podido cargar el recurso [ {0} ].\n{1}"},

      { SerializerMessages.ER_RESOURCE_COULD_NOT_LOAD,
        "No se ha podido cargar el recurso [ {0} ]: {1} \n {2} \n {3}"},

      { SerializerMessages.ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "Tama\u00f1o de almacenamiento intermedio <=0"},

      { SerializerMessages.ER_INVALID_UTF16_SURROGATE,
        "\u00bfSe ha detectado un sustituto UTF-16 no v\u00e1lido: {0}?"},

      { SerializerMessages.ER_OIERROR,
        "Error de ES"},

      { SerializerMessages.ER_ILLEGAL_ATTRIBUTE_POSITION,
        "No se puede a\u00f1adir el atributo {0} despu\u00e9s de nodos hijo o antes de que se produzca un elemento. Se ignorar\u00e1 el atributo."},

      { SerializerMessages.ER_NAMESPACE_PREFIX,
        "No se ha declarado el espacio de nombres para el prefijo ''{0}''."},

        // ER_STRAY_ATTRIBUTE needs translation
      { SerializerMessages.ER_STRAY_NAMESPACE,
        "Declaraci\u00f3n del espacio de nombres ''{0}''=''{1}'' fuera del elemento."},

      { SerializerMessages.ER_COULD_NOT_LOAD_RESOURCE,
        "No se ha podido cargar ''{0}'' (compruebe la CLASSPATH), ahora s\u00f3lo se est\u00e1n utilizando los valores por omisi\u00f3n"},

        // ER_ILLEGAL_CHARACTER needs translation
      { SerializerMessages.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
        "No se ha podido cargar el archivo de propiedades ''{0}'' para el m\u00e9todo de salida ''{1}'' (compruebe la CLASSPATH)"},

      { SerializerMessages.ER_INVALID_PORT,
        "N\u00famero de puerto no v\u00e1lido"},

      { SerializerMessages.ER_PORT_WHEN_HOST_NULL,
        "No se puede establecer el puerto si el sistema principal es nulo"},

      { SerializerMessages.ER_HOST_ADDRESS_NOT_WELLFORMED,
        "El sistema principal no es una direcci\u00f3n bien formada"},

      { SerializerMessages.ER_SCHEME_NOT_CONFORMANT,
        "El esquema no es compatible."},

      { SerializerMessages.ER_SCHEME_FROM_NULL_STRING,
        "No se puede establecer un esquema de una serie nula"},

      { SerializerMessages.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
        "La v\u00eda de acceso contiene una secuencia de escape no v\u00e1lida"},

      { SerializerMessages.ER_PATH_INVALID_CHAR,
        "La v\u00eda de acceso contiene un car\u00e1cter no v\u00e1lido: {0}"},

      { SerializerMessages.ER_FRAG_INVALID_CHAR,
        "El fragmento contiene un car\u00e1cter no v\u00e1lido"},

      { SerializerMessages.ER_FRAG_WHEN_PATH_NULL,
        "No se puede establecer el fragmento si la v\u00eda de acceso es nula"},

      { SerializerMessages.ER_FRAG_FOR_GENERIC_URI,
        "S\u00f3lo se puede establecer el fragmento para un URI gen\u00e9rico"},

      { SerializerMessages.ER_NO_SCHEME_IN_URI,
        "No se ha encontrado un esquema en el URI: {0}"},

      { SerializerMessages.ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "No se puede inicializar el URI con par\u00e1metros vac\u00edos"},

      { SerializerMessages.ER_NO_FRAGMENT_STRING_IN_PATH,
        "No se puede especificar el fragmento en la v\u00eda de acceso y en el fragmento"},

      { SerializerMessages.ER_NO_QUERY_STRING_IN_PATH,
        "No se puede especificar la serie de consulta en la v\u00eda de acceso y en la serie de consulta"},

      { SerializerMessages.ER_NO_PORT_IF_NO_HOST,
        "No se puede especificar el puerto si no se ha especificado el sistema principal"},

      { SerializerMessages.ER_NO_USERINFO_IF_NO_HOST,
        "No se puede especificar la informaci\u00f3n de usuario si no se ha especificado el sistema principal"},

      { SerializerMessages.ER_SCHEME_REQUIRED,
        "\u00a1Se necesita un esquema!"}

    };
    return contents;
  }
}
