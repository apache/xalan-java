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

public class SerializerMessages_pt_BR extends ListResourceBundle {
  public Object[][] getContents() {
    Object[][] contents =  new Object[][] {
        // BAD_MSGKEY needs translation
        // BAD_MSGFORMAT needs translation
      { SerializerMessages.ER_SERIALIZER_NOT_CONTENTHANDLER,
        "A classe de serializador ''{0}'' n\u00e3o implementa org.xml.sax.ContentHandler."},

      { SerializerMessages.ER_RESOURCE_COULD_NOT_FIND,
        "O recurso [ {0} ] n\u00e3o p\u00f4de ser encontrado.\n {1}"},

      { SerializerMessages.ER_RESOURCE_COULD_NOT_LOAD,
        "O recurso [ {0} ] n\u00e3o p\u00f4de carregar: {1} \n {2} \n {3}"},

      { SerializerMessages.ER_BUFFER_SIZE_LESSTHAN_ZERO,
        "Tamanho do buffer <=0"},

      { SerializerMessages.ER_INVALID_UTF16_SURROGATE,
        "Detectado substituto UTF-16 inv\u00e1lido: {0} ?"},

      { SerializerMessages.ER_OIERROR,
        "Erro de E/S"},

      { SerializerMessages.ER_ILLEGAL_ATTRIBUTE_POSITION,
        "Imposs\u00edvel incluir atributo {0} depois de n\u00f3s filhos ou antes da gera\u00e7\u00e3o de um elemento. O atributo ser\u00e1 ignorado."},

      { SerializerMessages.ER_NAMESPACE_PREFIX,
        "Namespace para prefixo ''{0}'' n\u00e3o foi declarado. "},

        // ER_STRAY_ATTRIBUTE needs translation
      { SerializerMessages.ER_STRAY_NAMESPACE,
        "Declara\u00e7\u00e3o de namespace ''{0}''=''{1}'' fora do elemento. "},

      { SerializerMessages.ER_COULD_NOT_LOAD_RESOURCE,
        "N\u00e3o foi poss\u00edvel carregar ''{0}'' (verifique CLASSPATH), utilizando agora somente os padr\u00f5es"},

        // ER_ILLEGAL_CHARACTER needs translation
      { SerializerMessages.ER_COULD_NOT_LOAD_METHOD_PROPERTY,
        "N\u00e3o foi poss\u00edvel carregar o arquivo de propriedade ''{0}'' para o m\u00e9todo de sa\u00edda ''{1}'' (verifique CLASSPATH)"},

      { SerializerMessages.ER_INVALID_PORT,
        "N\u00famero de porta inv\u00e1lido"},

      { SerializerMessages.ER_PORT_WHEN_HOST_NULL,
        "A porta n\u00e3o pode ser definida quando o host \u00e9 nulo"},

      { SerializerMessages.ER_HOST_ADDRESS_NOT_WELLFORMED,
        "O host n\u00e3o \u00e9 um endere\u00e7o formado corretamente"},

      { SerializerMessages.ER_SCHEME_NOT_CONFORMANT,
        "O esquema n\u00e3o est\u00e1 em conformidade."},

      { SerializerMessages.ER_SCHEME_FROM_NULL_STRING,
        "Imposs\u00edvel definir esquema a partir da cadeia nula"},

      { SerializerMessages.ER_PATH_CONTAINS_INVALID_ESCAPE_SEQUENCE,
        "O caminho cont\u00e9m seq\u00fc\u00eancia de escape inv\u00e1lida"},

      { SerializerMessages.ER_PATH_INVALID_CHAR,
        "O caminho cont\u00e9m caractere inv\u00e1lido: {0}"},

      { SerializerMessages.ER_FRAG_INVALID_CHAR,
        "O fragmento cont\u00e9m caractere inv\u00e1lido"},

      { SerializerMessages.ER_FRAG_WHEN_PATH_NULL,
        "O fragmento n\u00e3o pode ser definido quando o caminho \u00e9 nulo"},

      { SerializerMessages.ER_FRAG_FOR_GENERIC_URI,
        "O fragmento s\u00f3 pode ser definido para um URI gen\u00e9rico"},

      { SerializerMessages.ER_NO_SCHEME_IN_URI,
        "Nenhum esquema encontrado no URI: {0}"},

      { SerializerMessages.ER_CANNOT_INIT_URI_EMPTY_PARMS,
        "Imposs\u00edvel inicializar URI com par\u00e2metros vazios"},

      { SerializerMessages.ER_NO_FRAGMENT_STRING_IN_PATH,
        "O fragmento n\u00e3o pode ser especificado no caminho e fragmento"},

      { SerializerMessages.ER_NO_QUERY_STRING_IN_PATH,
        "A cadeia de consulta n\u00e3o pode ser especificada na cadeia de consulta e caminho"},

      { SerializerMessages.ER_NO_PORT_IF_NO_HOST,
        "Port n\u00e3o pode ser especificado se host n\u00e3o for especificado"},

      { SerializerMessages.ER_NO_USERINFO_IF_NO_HOST,
        "Userinfo n\u00e3o pode ser especificado se host n\u00e3o for especificado"},

      { SerializerMessages.ER_SCHEME_REQUIRED,
        "O esquema \u00e9 obrigat\u00f3rio!"}

    };
    return contents;
  }
}
