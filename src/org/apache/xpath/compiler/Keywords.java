package org.apache.xpath.compiler;

import java.util.Hashtable;
import org.apache.xalan.utils.StringKey;

public class Keywords
{
  static Hashtable m_keywords = new Hashtable();
  static Hashtable m_axisnames = new Hashtable();
  static Hashtable m_functions = new Hashtable();
  static Hashtable m_nodetypes = new Hashtable();

  private static final String FROM_ANCESTORS_STRING = "ancestor";
  private static final String FROM_ANCESTORS_OR_SELF_STRING = "ancestor-or-self";
  private static final String FROM_ATTRIBUTES_STRING = "attribute";
  private static final String FROM_CHILDREN_STRING = "child";
  private static final String FROM_DESCENDANTS_STRING = "descendant";
  private static final String FROM_DESCENDANTS_OR_SELF_STRING = "descendant-or-self";
  private static final String FROM_FOLLOWING_STRING = "following";
  private static final String FROM_FOLLOWING_SIBLINGS_STRING = "following-sibling";
  private static final String FROM_PARENT_STRING = "parent";
  private static final String FROM_PRECEDING_STRING = "preceding";
  private static final String FROM_PRECEDING_SIBLINGS_STRING = "preceding-sibling";
  private static final String FROM_SELF_STRING = "self";
  private static final String FROM_NAMESPACE_STRING = "namespace";

  private static final String FROM_SELF_ABBREVIATED_STRING = ".";
  private static final String NODETYPE_COMMENT_STRING = "comment";
  private static final String NODETYPE_TEXT_STRING = "text";
  private static final String NODETYPE_PI_STRING = "processing-instruction";
  private static final String NODETYPE_NODE_STRING = "node";
  private static final String FROM_ATTRIBUTE_STRING = "@";
  private static final String FROM_DOC_STRING = "document";
  private static final String FROM_DOCREF_STRING = "document";
  private static final String FROM_ID_STRING = "id";
  private static final String FROM_IDREF_STRING = "idref";
  private static final String NODETYPE_ANYELEMENT_STRING = "*";
  private static final String FUNC_CURRENT_STRING = "current";
  private static final String FUNC_LAST_STRING = "last";
  private static final String FUNC_POSITION_STRING = "position";
  private static final String FUNC_COUNT_STRING = "count";
  static final String FUNC_ID_STRING = "id";
  private static final String FUNC_IDREF_STRING = "idref";
  static final String FUNC_KEY_STRING = "key";
  private static final String FUNC_KEYREF_STRING = "keyref";
  private static final String FUNC_DOC_STRING = "doc";
  private static final String FUNC_DOCUMENT_STRING = "document";
  private static final String FUNC_DOCREF_STRING = "docref";
  private static final String FUNC_LOCAL_PART_STRING = "local-name";
  private static final String FUNC_NAMESPACE_STRING = "namespace-uri";
  private static final String FUNC_NAME_STRING = "name";
  private static final String FUNC_GENERATE_ID_STRING = "generate-id";
  private static final String FUNC_NOT_STRING = "not";
  private static final String FUNC_TRUE_STRING = "true";
  private static final String FUNC_FALSE_STRING = "false";
  private static final String FUNC_BOOLEAN_STRING = "boolean";
  private static final String FUNC_LANG_STRING = "lang";
  private static final String FUNC_NUMBER_STRING = "number";
  private static final String FUNC_FLOOR_STRING = "floor";
  private static final String FUNC_CEILING_STRING = "ceiling";
  private static final String FUNC_ROUND_STRING = "round";
  private static final String FUNC_SUM_STRING = "sum";
  private static final String FUNC_STRING_STRING = "string";
  private static final String FUNC_STARTS_WITH_STRING = "starts-with";
  private static final String FUNC_CONTAINS_STRING = "contains";
  private static final String FUNC_SUBSTRING_BEFORE_STRING = "substring-before";
  private static final String FUNC_SUBSTRING_AFTER_STRING = "substring-after";
  private static final String FUNC_NORMALIZE_SPACE_STRING = "normalize-space";
  private static final String FUNC_TRANSLATE_STRING = "translate";
  private static final String FUNC_CONCAT_STRING = "concat";
  //private static final String FUNC_FORMAT_NUMBER_STRING = "format-number";
  private static final String FUNC_SYSTEM_PROPERTY_STRING = "system-property";
  private static final String FUNC_EXT_FUNCTION_AVAILABLE_STRING = "function-available";
  private static final String FUNC_EXT_ELEM_AVAILABLE_STRING = "element-available";
  private static final String FUNC_SUBSTRING_STRING = "substring";
  private static final String FUNC_STRING_LENGTH_STRING = "string-length";
  private static final String FUNC_UNPARSED_ENTITY_URI_STRING = "unparsed-entity-uri";

  // Proprietary, built in functions
  private static final String FUNC_DOCLOCATION_STRING = "document-location";

  static
  {
    m_axisnames.put(new StringKey(FROM_ANCESTORS_STRING), new Integer(OpCodes.FROM_ANCESTORS));
    m_axisnames.put(new StringKey(FROM_ANCESTORS_OR_SELF_STRING), new Integer(OpCodes.FROM_ANCESTORS_OR_SELF));
    m_axisnames.put(new StringKey(FROM_ATTRIBUTES_STRING), new Integer(OpCodes.FROM_ATTRIBUTES));
    m_axisnames.put(new StringKey(FROM_CHILDREN_STRING), new Integer(OpCodes.FROM_CHILDREN));
    m_axisnames.put(new StringKey(FROM_DESCENDANTS_STRING), new Integer(OpCodes.FROM_DESCENDANTS));
    m_axisnames.put(new StringKey(FROM_DESCENDANTS_OR_SELF_STRING), new Integer(OpCodes.FROM_DESCENDANTS_OR_SELF));
    m_axisnames.put(new StringKey(FROM_FOLLOWING_STRING), new Integer(OpCodes.FROM_FOLLOWING));
    m_axisnames.put(new StringKey(FROM_FOLLOWING_SIBLINGS_STRING), new Integer(OpCodes.FROM_FOLLOWING_SIBLINGS));
    m_axisnames.put(new StringKey(FROM_PARENT_STRING), new Integer(OpCodes.FROM_PARENT));
    m_axisnames.put(new StringKey(FROM_PRECEDING_STRING), new Integer(OpCodes.FROM_PRECEDING));
    m_axisnames.put(new StringKey(FROM_PRECEDING_SIBLINGS_STRING), new Integer(OpCodes.FROM_PRECEDING_SIBLINGS));
    m_axisnames.put(new StringKey(FROM_SELF_STRING), new Integer(OpCodes.FROM_SELF));
    m_axisnames.put(new StringKey(FROM_NAMESPACE_STRING), new Integer(OpCodes.FROM_NAMESPACE));

    m_nodetypes.put(new StringKey(NODETYPE_COMMENT_STRING), new Integer(OpCodes.NODETYPE_COMMENT));
    m_nodetypes.put(new StringKey(NODETYPE_TEXT_STRING), new Integer(OpCodes.NODETYPE_TEXT));
    m_nodetypes.put(new StringKey(NODETYPE_PI_STRING), new Integer(OpCodes.NODETYPE_PI));
    m_nodetypes.put(new StringKey(NODETYPE_NODE_STRING), new Integer(OpCodes.NODETYPE_NODE));
    m_nodetypes.put(new StringKey(NODETYPE_ANYELEMENT_STRING), new Integer(OpCodes.NODETYPE_ANYELEMENT));

    m_keywords.put(new StringKey(FROM_SELF_ABBREVIATED_STRING), new Integer(OpCodes.FROM_SELF));
    // m_keywords.put(new StringKey(FROM_ATTRIBUTE_STRING), new Integer(OpCodes.FROM_ATTRIBUTE));
    // m_keywords.put(new StringKey(FROM_DOC_STRING), new Integer(OpCodes.FROM_DOC));
    // m_keywords.put(new StringKey(FROM_DOCREF_STRING), new Integer(OpCodes.FROM_DOCREF));
    // m_keywords.put(new StringKey(FROM_ID_STRING), new Integer(OpCodes.FROM_ID));
    // m_keywords.put(new StringKey(FROM_IDREF_STRING), new Integer(OpCodes.FROM_IDREF));

    m_keywords.put(new StringKey(FUNC_ID_STRING), new Integer(FunctionTable.FUNC_ID));
    m_keywords.put(new StringKey(FUNC_KEY_STRING), new Integer(FunctionTable.FUNC_KEY));
    // m_keywords.put(new StringKey(FUNC_DOCUMENT_STRING), new Integer(FunctionTable.FUNC_DOC));

    m_functions.put(new StringKey(FUNC_CURRENT_STRING), new Integer(FunctionTable.FUNC_CURRENT));
    m_functions.put(new StringKey(FUNC_LAST_STRING), new Integer(FunctionTable.FUNC_LAST));
    m_functions.put(new StringKey(FUNC_POSITION_STRING), new Integer(FunctionTable.FUNC_POSITION));
    m_functions.put(new StringKey(FUNC_COUNT_STRING), new Integer(FunctionTable.FUNC_COUNT));
    m_functions.put(new StringKey(FUNC_ID_STRING), new Integer(FunctionTable.FUNC_ID));
    m_functions.put(new StringKey(FUNC_KEY_STRING), new Integer(FunctionTable.FUNC_KEY));
    // m_functions.put(new StringKey(FUNC_DOCUMENT_STRING), new Integer(FunctionTable.FUNC_DOC));
    m_functions.put(new StringKey(FUNC_LOCAL_PART_STRING), new Integer(FunctionTable.FUNC_LOCAL_PART));
    m_functions.put(new StringKey(FUNC_NAMESPACE_STRING), new Integer(FunctionTable.FUNC_NAMESPACE));
    m_functions.put(new StringKey(FUNC_NAME_STRING), new Integer(FunctionTable.FUNC_QNAME));
    m_functions.put(new StringKey(FUNC_GENERATE_ID_STRING), new Integer(FunctionTable.FUNC_GENERATE_ID));
    m_functions.put(new StringKey(FUNC_NOT_STRING), new Integer(FunctionTable.FUNC_NOT));
    m_functions.put(new StringKey(FUNC_TRUE_STRING), new Integer(FunctionTable.FUNC_TRUE));
    m_functions.put(new StringKey(FUNC_FALSE_STRING), new Integer(FunctionTable.FUNC_FALSE));
    m_functions.put(new StringKey(FUNC_BOOLEAN_STRING), new Integer(FunctionTable.FUNC_BOOLEAN));
    m_functions.put(new StringKey(FUNC_LANG_STRING), new Integer(FunctionTable.FUNC_LANG));
    m_functions.put(new StringKey(FUNC_NUMBER_STRING), new Integer(FunctionTable.FUNC_NUMBER));
    m_functions.put(new StringKey(FUNC_FLOOR_STRING), new Integer(FunctionTable.FUNC_FLOOR));
    m_functions.put(new StringKey(FUNC_CEILING_STRING), new Integer(FunctionTable.FUNC_CEILING));
    m_functions.put(new StringKey(FUNC_ROUND_STRING), new Integer(FunctionTable.FUNC_ROUND));
    m_functions.put(new StringKey(FUNC_SUM_STRING), new Integer(FunctionTable.FUNC_SUM));
    m_functions.put(new StringKey(FUNC_STRING_STRING), new Integer(FunctionTable.FUNC_STRING));
    m_functions.put(new StringKey(FUNC_STARTS_WITH_STRING), new Integer(FunctionTable.FUNC_STARTS_WITH));
    m_functions.put(new StringKey(FUNC_CONTAINS_STRING), new Integer(FunctionTable.FUNC_CONTAINS));
    m_functions.put(new StringKey(FUNC_SUBSTRING_BEFORE_STRING), new Integer(FunctionTable.FUNC_SUBSTRING_BEFORE));
    m_functions.put(new StringKey(FUNC_SUBSTRING_AFTER_STRING), new Integer(FunctionTable.FUNC_SUBSTRING_AFTER));
    m_functions.put(new StringKey(FUNC_NORMALIZE_SPACE_STRING), new Integer(FunctionTable.FUNC_NORMALIZE_SPACE));
    m_functions.put(new StringKey(FUNC_TRANSLATE_STRING), new Integer(FunctionTable.FUNC_TRANSLATE));
    m_functions.put(new StringKey(FUNC_CONCAT_STRING), new Integer(FunctionTable.FUNC_CONCAT));
    //m_functions.put(new StringKey(FUNC_FORMAT_NUMBER_STRING), new Integer(FunctionTable.FUNC_FORMAT_NUMBER));
    m_functions.put(new StringKey(FUNC_SYSTEM_PROPERTY_STRING), new Integer(FunctionTable.FUNC_SYSTEM_PROPERTY));
    m_functions.put(new StringKey(FUNC_EXT_FUNCTION_AVAILABLE_STRING), new Integer(FunctionTable.FUNC_EXT_FUNCTION_AVAILABLE));
    m_functions.put(new StringKey(FUNC_EXT_ELEM_AVAILABLE_STRING), new Integer(FunctionTable.FUNC_EXT_ELEM_AVAILABLE));
    m_functions.put(new StringKey(FUNC_SUBSTRING_STRING), new Integer(FunctionTable.FUNC_SUBSTRING));
    m_functions.put(new StringKey(FUNC_STRING_LENGTH_STRING), new Integer(FunctionTable.FUNC_STRING_LENGTH));
    m_functions.put(new StringKey(FUNC_UNPARSED_ENTITY_URI_STRING), new Integer(FunctionTable.FUNC_UNPARSED_ENTITY_URI));

    // These aren't really functions.
    m_functions.put(new StringKey(NODETYPE_COMMENT_STRING), new Integer(OpCodes.NODETYPE_COMMENT));
    m_functions.put(new StringKey(NODETYPE_TEXT_STRING), new Integer(OpCodes.NODETYPE_TEXT));
    m_functions.put(new StringKey(NODETYPE_PI_STRING), new Integer(OpCodes.NODETYPE_PI));
    m_functions.put(new StringKey(NODETYPE_NODE_STRING), new Integer(OpCodes.NODETYPE_NODE));

    m_functions.put(new StringKey(FUNC_DOCLOCATION_STRING), new Integer(FunctionTable.FUNC_DOCLOCATION));
  }

}
