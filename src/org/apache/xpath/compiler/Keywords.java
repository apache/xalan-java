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
package org.apache.xpath.compiler;

import java.util.Hashtable;

import org.apache.xml.utils.StringKey;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class Keywords <needs-comment/>
 */
public class Keywords
{

  /** NEEDSDOC Field m_keywords          */
  static Hashtable m_keywords = new Hashtable();

  /** NEEDSDOC Field m_axisnames          */
  static Hashtable m_axisnames = new Hashtable();

  /** NEEDSDOC Field m_functions          */
  static Hashtable m_functions = new Hashtable();

  /** NEEDSDOC Field m_nodetypes          */
  static Hashtable m_nodetypes = new Hashtable();

  /** NEEDSDOC Field FROM_ANCESTORS_STRING          */
  private static final String FROM_ANCESTORS_STRING = "ancestor";

  /** NEEDSDOC Field FROM_ANCESTORS_OR_SELF_STRING          */
  private static final String FROM_ANCESTORS_OR_SELF_STRING =
    "ancestor-or-self";

  /** NEEDSDOC Field FROM_ATTRIBUTES_STRING          */
  private static final String FROM_ATTRIBUTES_STRING = "attribute";

  /** NEEDSDOC Field FROM_CHILDREN_STRING          */
  private static final String FROM_CHILDREN_STRING = "child";

  /** NEEDSDOC Field FROM_DESCENDANTS_STRING          */
  private static final String FROM_DESCENDANTS_STRING = "descendant";

  /** NEEDSDOC Field FROM_DESCENDANTS_OR_SELF_STRING          */
  private static final String FROM_DESCENDANTS_OR_SELF_STRING =
    "descendant-or-self";

  /** NEEDSDOC Field FROM_FOLLOWING_STRING          */
  private static final String FROM_FOLLOWING_STRING = "following";

  /** NEEDSDOC Field FROM_FOLLOWING_SIBLINGS_STRING          */
  private static final String FROM_FOLLOWING_SIBLINGS_STRING =
    "following-sibling";

  /** NEEDSDOC Field FROM_PARENT_STRING          */
  private static final String FROM_PARENT_STRING = "parent";

  /** NEEDSDOC Field FROM_PRECEDING_STRING          */
  private static final String FROM_PRECEDING_STRING = "preceding";

  /** NEEDSDOC Field FROM_PRECEDING_SIBLINGS_STRING          */
  private static final String FROM_PRECEDING_SIBLINGS_STRING =
    "preceding-sibling";

  /** NEEDSDOC Field FROM_SELF_STRING          */
  private static final String FROM_SELF_STRING = "self";

  /** NEEDSDOC Field FROM_NAMESPACE_STRING          */
  private static final String FROM_NAMESPACE_STRING = "namespace";

  /** NEEDSDOC Field FROM_SELF_ABBREVIATED_STRING          */
  private static final String FROM_SELF_ABBREVIATED_STRING = ".";

  /** NEEDSDOC Field NODETYPE_COMMENT_STRING          */
  private static final String NODETYPE_COMMENT_STRING = "comment";

  /** NEEDSDOC Field NODETYPE_TEXT_STRING          */
  private static final String NODETYPE_TEXT_STRING = "text";

  /** NEEDSDOC Field NODETYPE_PI_STRING          */
  private static final String NODETYPE_PI_STRING = "processing-instruction";

  /** NEEDSDOC Field NODETYPE_NODE_STRING          */
  private static final String NODETYPE_NODE_STRING = "node";

  /** NEEDSDOC Field FROM_ATTRIBUTE_STRING          */
  private static final String FROM_ATTRIBUTE_STRING = "@";

  /** NEEDSDOC Field FROM_DOC_STRING          */
  private static final String FROM_DOC_STRING = "document";

  /** NEEDSDOC Field FROM_DOCREF_STRING          */
  private static final String FROM_DOCREF_STRING = "document";

  /** NEEDSDOC Field FROM_ID_STRING          */
  private static final String FROM_ID_STRING = "id";

  /** NEEDSDOC Field FROM_IDREF_STRING          */
  private static final String FROM_IDREF_STRING = "idref";

  /** NEEDSDOC Field NODETYPE_ANYELEMENT_STRING          */
  private static final String NODETYPE_ANYELEMENT_STRING = "*";

  /** NEEDSDOC Field FUNC_CURRENT_STRING          */
  private static final String FUNC_CURRENT_STRING = "current";

  /** NEEDSDOC Field FUNC_LAST_STRING          */
  private static final String FUNC_LAST_STRING = "last";

  /** NEEDSDOC Field FUNC_POSITION_STRING          */
  private static final String FUNC_POSITION_STRING = "position";

  /** NEEDSDOC Field FUNC_COUNT_STRING          */
  private static final String FUNC_COUNT_STRING = "count";

  /** NEEDSDOC Field FUNC_ID_STRING          */
  static final String FUNC_ID_STRING = "id";

  /** NEEDSDOC Field FUNC_IDREF_STRING          */
  private static final String FUNC_IDREF_STRING = "idref";

  /** NEEDSDOC Field FUNC_KEY_STRING          */
  public static final String FUNC_KEY_STRING = "key";

  /** NEEDSDOC Field FUNC_KEYREF_STRING          */
  private static final String FUNC_KEYREF_STRING = "keyref";

  /** NEEDSDOC Field FUNC_DOC_STRING          */
  private static final String FUNC_DOC_STRING = "doc";

  /** NEEDSDOC Field FUNC_DOCUMENT_STRING          */
  private static final String FUNC_DOCUMENT_STRING = "document";

  /** NEEDSDOC Field FUNC_DOCREF_STRING          */
  private static final String FUNC_DOCREF_STRING = "docref";

  /** NEEDSDOC Field FUNC_LOCAL_PART_STRING          */
  private static final String FUNC_LOCAL_PART_STRING = "local-name";

  /** NEEDSDOC Field FUNC_NAMESPACE_STRING          */
  private static final String FUNC_NAMESPACE_STRING = "namespace-uri";

  /** NEEDSDOC Field FUNC_NAME_STRING          */
  private static final String FUNC_NAME_STRING = "name";

  /** NEEDSDOC Field FUNC_GENERATE_ID_STRING          */
  private static final String FUNC_GENERATE_ID_STRING = "generate-id";

  /** NEEDSDOC Field FUNC_NOT_STRING          */
  private static final String FUNC_NOT_STRING = "not";

  /** NEEDSDOC Field FUNC_TRUE_STRING          */
  private static final String FUNC_TRUE_STRING = "true";

  /** NEEDSDOC Field FUNC_FALSE_STRING          */
  private static final String FUNC_FALSE_STRING = "false";

  /** NEEDSDOC Field FUNC_BOOLEAN_STRING          */
  private static final String FUNC_BOOLEAN_STRING = "boolean";

  /** NEEDSDOC Field FUNC_LANG_STRING          */
  private static final String FUNC_LANG_STRING = "lang";

  /** NEEDSDOC Field FUNC_NUMBER_STRING          */
  private static final String FUNC_NUMBER_STRING = "number";

  /** NEEDSDOC Field FUNC_FLOOR_STRING          */
  private static final String FUNC_FLOOR_STRING = "floor";

  /** NEEDSDOC Field FUNC_CEILING_STRING          */
  private static final String FUNC_CEILING_STRING = "ceiling";

  /** NEEDSDOC Field FUNC_ROUND_STRING          */
  private static final String FUNC_ROUND_STRING = "round";

  /** NEEDSDOC Field FUNC_SUM_STRING          */
  private static final String FUNC_SUM_STRING = "sum";

  /** NEEDSDOC Field FUNC_STRING_STRING          */
  private static final String FUNC_STRING_STRING = "string";

  /** NEEDSDOC Field FUNC_STARTS_WITH_STRING          */
  private static final String FUNC_STARTS_WITH_STRING = "starts-with";

  /** NEEDSDOC Field FUNC_CONTAINS_STRING          */
  private static final String FUNC_CONTAINS_STRING = "contains";

  /** NEEDSDOC Field FUNC_SUBSTRING_BEFORE_STRING          */
  private static final String FUNC_SUBSTRING_BEFORE_STRING =
    "substring-before";

  /** NEEDSDOC Field FUNC_SUBSTRING_AFTER_STRING          */
  private static final String FUNC_SUBSTRING_AFTER_STRING = "substring-after";

  /** NEEDSDOC Field FUNC_NORMALIZE_SPACE_STRING          */
  private static final String FUNC_NORMALIZE_SPACE_STRING = "normalize-space";

  /** NEEDSDOC Field FUNC_TRANSLATE_STRING          */
  private static final String FUNC_TRANSLATE_STRING = "translate";

  /** NEEDSDOC Field FUNC_CONCAT_STRING          */
  private static final String FUNC_CONCAT_STRING = "concat";

  //private static final String FUNC_FORMAT_NUMBER_STRING = "format-number";

  /** NEEDSDOC Field FUNC_SYSTEM_PROPERTY_STRING          */
  private static final String FUNC_SYSTEM_PROPERTY_STRING = "system-property";

  /** NEEDSDOC Field FUNC_EXT_FUNCTION_AVAILABLE_STRING          */
  private static final String FUNC_EXT_FUNCTION_AVAILABLE_STRING =
    "function-available";

  /** NEEDSDOC Field FUNC_EXT_ELEM_AVAILABLE_STRING          */
  private static final String FUNC_EXT_ELEM_AVAILABLE_STRING =
    "element-available";

  /** NEEDSDOC Field FUNC_SUBSTRING_STRING          */
  private static final String FUNC_SUBSTRING_STRING = "substring";

  /** NEEDSDOC Field FUNC_STRING_LENGTH_STRING          */
  private static final String FUNC_STRING_LENGTH_STRING = "string-length";

  /** NEEDSDOC Field FUNC_UNPARSED_ENTITY_URI_STRING          */
  private static final String FUNC_UNPARSED_ENTITY_URI_STRING =
    "unparsed-entity-uri";

  // Proprietary, built in functions

  /** NEEDSDOC Field FUNC_DOCLOCATION_STRING          */
  private static final String FUNC_DOCLOCATION_STRING = "document-location";

  static
  {
    m_axisnames.put(new StringKey(FROM_ANCESTORS_STRING),
                    new Integer(OpCodes.FROM_ANCESTORS));
    m_axisnames.put(new StringKey(FROM_ANCESTORS_OR_SELF_STRING),
                    new Integer(OpCodes.FROM_ANCESTORS_OR_SELF));
    m_axisnames.put(new StringKey(FROM_ATTRIBUTES_STRING),
                    new Integer(OpCodes.FROM_ATTRIBUTES));
    m_axisnames.put(new StringKey(FROM_CHILDREN_STRING),
                    new Integer(OpCodes.FROM_CHILDREN));
    m_axisnames.put(new StringKey(FROM_DESCENDANTS_STRING),
                    new Integer(OpCodes.FROM_DESCENDANTS));
    m_axisnames.put(new StringKey(FROM_DESCENDANTS_OR_SELF_STRING),
                    new Integer(OpCodes.FROM_DESCENDANTS_OR_SELF));
    m_axisnames.put(new StringKey(FROM_FOLLOWING_STRING),
                    new Integer(OpCodes.FROM_FOLLOWING));
    m_axisnames.put(new StringKey(FROM_FOLLOWING_SIBLINGS_STRING),
                    new Integer(OpCodes.FROM_FOLLOWING_SIBLINGS));
    m_axisnames.put(new StringKey(FROM_PARENT_STRING),
                    new Integer(OpCodes.FROM_PARENT));
    m_axisnames.put(new StringKey(FROM_PRECEDING_STRING),
                    new Integer(OpCodes.FROM_PRECEDING));
    m_axisnames.put(new StringKey(FROM_PRECEDING_SIBLINGS_STRING),
                    new Integer(OpCodes.FROM_PRECEDING_SIBLINGS));
    m_axisnames.put(new StringKey(FROM_SELF_STRING),
                    new Integer(OpCodes.FROM_SELF));
    m_axisnames.put(new StringKey(FROM_NAMESPACE_STRING),
                    new Integer(OpCodes.FROM_NAMESPACE));
    m_nodetypes.put(new StringKey(NODETYPE_COMMENT_STRING),
                    new Integer(OpCodes.NODETYPE_COMMENT));
    m_nodetypes.put(new StringKey(NODETYPE_TEXT_STRING),
                    new Integer(OpCodes.NODETYPE_TEXT));
    m_nodetypes.put(new StringKey(NODETYPE_PI_STRING),
                    new Integer(OpCodes.NODETYPE_PI));
    m_nodetypes.put(new StringKey(NODETYPE_NODE_STRING),
                    new Integer(OpCodes.NODETYPE_NODE));
    m_nodetypes.put(new StringKey(NODETYPE_ANYELEMENT_STRING),
                    new Integer(OpCodes.NODETYPE_ANYELEMENT));
    m_keywords.put(new StringKey(FROM_SELF_ABBREVIATED_STRING),
                   new Integer(OpCodes.FROM_SELF));

    // m_keywords.put(new StringKey(FROM_ATTRIBUTE_STRING), new Integer(OpCodes.FROM_ATTRIBUTE));
    // m_keywords.put(new StringKey(FROM_DOC_STRING), new Integer(OpCodes.FROM_DOC));
    // m_keywords.put(new StringKey(FROM_DOCREF_STRING), new Integer(OpCodes.FROM_DOCREF));
    // m_keywords.put(new StringKey(FROM_ID_STRING), new Integer(OpCodes.FROM_ID));
    // m_keywords.put(new StringKey(FROM_IDREF_STRING), new Integer(OpCodes.FROM_IDREF));
    m_keywords.put(new StringKey(FUNC_ID_STRING),
                   new Integer(FunctionTable.FUNC_ID));
    m_keywords.put(new StringKey(FUNC_KEY_STRING),
                   new Integer(FunctionTable.FUNC_KEY));

    // m_keywords.put(new StringKey(FUNC_DOCUMENT_STRING), new Integer(FunctionTable.FUNC_DOC));
    m_functions.put(new StringKey(FUNC_CURRENT_STRING),
                    new Integer(FunctionTable.FUNC_CURRENT));
    m_functions.put(new StringKey(FUNC_LAST_STRING),
                    new Integer(FunctionTable.FUNC_LAST));
    m_functions.put(new StringKey(FUNC_POSITION_STRING),
                    new Integer(FunctionTable.FUNC_POSITION));
    m_functions.put(new StringKey(FUNC_COUNT_STRING),
                    new Integer(FunctionTable.FUNC_COUNT));
    m_functions.put(new StringKey(FUNC_ID_STRING),
                    new Integer(FunctionTable.FUNC_ID));
    m_functions.put(new StringKey(FUNC_KEY_STRING),
                    new Integer(FunctionTable.FUNC_KEY));

    // m_functions.put(new StringKey(FUNC_DOCUMENT_STRING), new Integer(FunctionTable.FUNC_DOC));
    m_functions.put(new StringKey(FUNC_LOCAL_PART_STRING),
                    new Integer(FunctionTable.FUNC_LOCAL_PART));
    m_functions.put(new StringKey(FUNC_NAMESPACE_STRING),
                    new Integer(FunctionTable.FUNC_NAMESPACE));
    m_functions.put(new StringKey(FUNC_NAME_STRING),
                    new Integer(FunctionTable.FUNC_QNAME));
    m_functions.put(new StringKey(FUNC_GENERATE_ID_STRING),
                    new Integer(FunctionTable.FUNC_GENERATE_ID));
    m_functions.put(new StringKey(FUNC_NOT_STRING),
                    new Integer(FunctionTable.FUNC_NOT));
    m_functions.put(new StringKey(FUNC_TRUE_STRING),
                    new Integer(FunctionTable.FUNC_TRUE));
    m_functions.put(new StringKey(FUNC_FALSE_STRING),
                    new Integer(FunctionTable.FUNC_FALSE));
    m_functions.put(new StringKey(FUNC_BOOLEAN_STRING),
                    new Integer(FunctionTable.FUNC_BOOLEAN));
    m_functions.put(new StringKey(FUNC_LANG_STRING),
                    new Integer(FunctionTable.FUNC_LANG));
    m_functions.put(new StringKey(FUNC_NUMBER_STRING),
                    new Integer(FunctionTable.FUNC_NUMBER));
    m_functions.put(new StringKey(FUNC_FLOOR_STRING),
                    new Integer(FunctionTable.FUNC_FLOOR));
    m_functions.put(new StringKey(FUNC_CEILING_STRING),
                    new Integer(FunctionTable.FUNC_CEILING));
    m_functions.put(new StringKey(FUNC_ROUND_STRING),
                    new Integer(FunctionTable.FUNC_ROUND));
    m_functions.put(new StringKey(FUNC_SUM_STRING),
                    new Integer(FunctionTable.FUNC_SUM));
    m_functions.put(new StringKey(FUNC_STRING_STRING),
                    new Integer(FunctionTable.FUNC_STRING));
    m_functions.put(new StringKey(FUNC_STARTS_WITH_STRING),
                    new Integer(FunctionTable.FUNC_STARTS_WITH));
    m_functions.put(new StringKey(FUNC_CONTAINS_STRING),
                    new Integer(FunctionTable.FUNC_CONTAINS));
    m_functions.put(new StringKey(FUNC_SUBSTRING_BEFORE_STRING),
                    new Integer(FunctionTable.FUNC_SUBSTRING_BEFORE));
    m_functions.put(new StringKey(FUNC_SUBSTRING_AFTER_STRING),
                    new Integer(FunctionTable.FUNC_SUBSTRING_AFTER));
    m_functions.put(new StringKey(FUNC_NORMALIZE_SPACE_STRING),
                    new Integer(FunctionTable.FUNC_NORMALIZE_SPACE));
    m_functions.put(new StringKey(FUNC_TRANSLATE_STRING),
                    new Integer(FunctionTable.FUNC_TRANSLATE));
    m_functions.put(new StringKey(FUNC_CONCAT_STRING),
                    new Integer(FunctionTable.FUNC_CONCAT));

    //m_functions.put(new StringKey(FUNC_FORMAT_NUMBER_STRING), new Integer(FunctionTable.FUNC_FORMAT_NUMBER));
    m_functions.put(new StringKey(FUNC_SYSTEM_PROPERTY_STRING),
                    new Integer(FunctionTable.FUNC_SYSTEM_PROPERTY));
    m_functions.put(new StringKey(FUNC_EXT_FUNCTION_AVAILABLE_STRING),
                    new Integer(FunctionTable.FUNC_EXT_FUNCTION_AVAILABLE));
    m_functions.put(new StringKey(FUNC_EXT_ELEM_AVAILABLE_STRING),
                    new Integer(FunctionTable.FUNC_EXT_ELEM_AVAILABLE));
    m_functions.put(new StringKey(FUNC_SUBSTRING_STRING),
                    new Integer(FunctionTable.FUNC_SUBSTRING));
    m_functions.put(new StringKey(FUNC_STRING_LENGTH_STRING),
                    new Integer(FunctionTable.FUNC_STRING_LENGTH));
    m_functions.put(new StringKey(FUNC_UNPARSED_ENTITY_URI_STRING),
                    new Integer(FunctionTable.FUNC_UNPARSED_ENTITY_URI));

    // These aren't really functions.
    m_functions.put(new StringKey(NODETYPE_COMMENT_STRING),
                    new Integer(OpCodes.NODETYPE_COMMENT));
    m_functions.put(new StringKey(NODETYPE_TEXT_STRING),
                    new Integer(OpCodes.NODETYPE_TEXT));
    m_functions.put(new StringKey(NODETYPE_PI_STRING),
                    new Integer(OpCodes.NODETYPE_PI));
    m_functions.put(new StringKey(NODETYPE_NODE_STRING),
                    new Integer(OpCodes.NODETYPE_NODE));
    m_functions.put(new StringKey(FUNC_DOCLOCATION_STRING),
                    new Integer(FunctionTable.FUNC_DOCLOCATION));
  }

  public static boolean functionAvailable(String methName)
  {

    try
    {
      Object tblEntry = m_functions.get(methName);
      if (null == tblEntry)
        return false;
      int funcType = ((Integer) tblEntry).intValue();
      switch (funcType)
      {
        case OpCodes.NODETYPE_COMMENT:
        case OpCodes.NODETYPE_TEXT:
        case OpCodes.NODETYPE_PI:
        case OpCodes.NODETYPE_NODE:
          return false;                 // These look like functions but they're NodeTests.

        default:
          return true;
      }
    }
    catch (Exception e)
    {
      return false;
    }
  }
}
