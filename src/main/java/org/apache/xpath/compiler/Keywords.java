/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License");
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
package org.apache.xpath.compiler;

import java.util.Hashtable;

/**
 * Table of strings to operation code lookups
 * to support implementation of XPath.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Gary L Peskin <garyp@apache.org>
 * @author Christine Li <jycli@apache.org>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3 specific changes, to this class)
 * 
 * @xsl.usage internal
 */
public class Keywords
{

  /** Table of keywords to opcode associations. */
  private static Hashtable m_keywords = new Hashtable();

  /** Table of axes names to opcode associations. */
  private static Hashtable m_axisnames = new Hashtable();

  /** Table of function name to function ID associations. */
  private static Hashtable m_nodetests = new Hashtable();

  /** Table of node type strings to opcode associations. */
  private static Hashtable m_nodetypes = new Hashtable();

  /** ancestor axes string. */
  private static final String FROM_ANCESTORS_STRING = "ancestor";

  /** ancestor-or-self axes string. */
  private static final String FROM_ANCESTORS_OR_SELF_STRING = "ancestor-or-self";

  /** attribute axes string. */
  private static final String FROM_ATTRIBUTES_STRING = "attribute";

  /** child axes string. */
  private static final String FROM_CHILDREN_STRING = "child";

  /** descendant-or-self axes string. */
  private static final String FROM_DESCENDANTS_STRING = "descendant";

  /** ancestor axes string. */
  private static final String FROM_DESCENDANTS_OR_SELF_STRING = "descendant-or-self";

  /** following axes string. */
  private static final String FROM_FOLLOWING_STRING = "following";

  /** following-sibling axes string. */
  private static final String FROM_FOLLOWING_SIBLINGS_STRING = "following-sibling";

  /** parent axes string. */
  private static final String FROM_PARENT_STRING = "parent";

  /** preceding axes string. */
  private static final String FROM_PRECEDING_STRING = "preceding";

  /** preceding-sibling axes string. */
  private static final String FROM_PRECEDING_SIBLINGS_STRING = "preceding-sibling";

  /** self axes string. */
  private static final String FROM_SELF_STRING = "self";

  /** namespace axes string. */
  private static final String FROM_NAMESPACE_STRING = "namespace";

  /** self axes abreviated string. */
  private static final String FROM_SELF_ABBREVIATED_STRING = ".";

  /** comment node test string. */
  private static final String NODETYPE_COMMENT_STRING = "comment";

  /** text node test string. */
  private static final String NODETYPE_TEXT_STRING = "text";

  /** processing-instruction node test string. */
  private static final String NODETYPE_PI_STRING = "processing-instruction";

  /** Any node test string. */
  private static final String NODETYPE_NODE_STRING = "node";
  
  /** Context item node test string. Added for XSLT 3.0. */
  private static final String NODETYPE_CONTEXT_ITEM_STRING = ".";
  
  /** Document node test string. Added for XSLT 3.0. */
  private static final String NODETYPE_DOCUMENT_STRING = "document-node";

  /** Wildcard element string. */
  private static final String NODETYPE_ANYELEMENT_STRING = "*";

  /** current function string. */
  public static final String FUNC_CURRENT_STRING = "current";

  /** last function string. */
  public static final String FUNC_LAST_STRING = "last";

  /** position function string. */
  public static final String FUNC_POSITION_STRING = "position";

  /** count function string. */
  public static final String FUNC_COUNT_STRING = "count";

  /** id function string. */
  static final String FUNC_ID_STRING = "id";

  /** key function string (XSLT). */
  public static final String FUNC_KEY_STRING = "key";

  /** local-name function string. */
  public static final String FUNC_LOCAL_NAME_STRING = "local-name";

  /** namespace-uri function string. */
  public static final String FUNC_NAMESPACE_URI_STRING = "namespace-uri";

  /** name function string. */
  public static final String FUNC_NAME_STRING = "name";

  /** generate-id function string (XSLT). */
  public static final String FUNC_GENERATE_ID_STRING = "generate-id";

  /** not function string. */
  public static final String FUNC_NOT_STRING = "not";

  /** true function string. */
  public static final String FUNC_TRUE_STRING = "true";

  /** false function string. */
  public static final String FUNC_FALSE_STRING = "false";

  /**
   * boolean function string. we use this same keyword string 
   * for XML Schema data type xs:boolean as well.
   */
  public static final String FUNC_BOOLEAN_STRING = "boolean";

  /** lang function string. */
  public static final String FUNC_LANG_STRING = "lang";
  
  /** root function string. */
  public static final String FUNC_ROOT_STRING = "root";

  /** number function string. */
  public static final String FUNC_NUMBER_STRING = "number";

  /** floor function string. */
  public static final String FUNC_FLOOR_STRING = "floor";

  /** ceiling function string. */
  public static final String FUNC_CEILING_STRING = "ceiling";

  /** round function string. */
  public static final String FUNC_ROUND_STRING = "round";

  /** sum function string. */
  public static final String FUNC_SUM_STRING = "sum";

  /** string function string. */
  public static final String FUNC_STRING_STRING = "string";
  
  /** data function string. */
  public static final String FUNC_DATA_STRING = "data";

  /** starts-with function string. */
  public static final String FUNC_STARTS_WITH_STRING = "starts-with";
  
  /** ends-with function string. */
  public static final String FUNC_ENDS_WITH_STRING = "ends-with";

  /** contains function string. */
  public static final String FUNC_CONTAINS_STRING = "contains";

  /** substring-before function string. */
  public static final String FUNC_SUBSTRING_BEFORE_STRING = "substring-before";

  /** substring-after function string. */
  public static final String FUNC_SUBSTRING_AFTER_STRING = "substring-after";

  /** normalize-space function string. */
  public static final String FUNC_NORMALIZE_SPACE_STRING = "normalize-space";

  /** translate function string. */
  public static final String FUNC_TRANSLATE_STRING = "translate";

  /** concat function string. */
  public static final String FUNC_CONCAT_STRING = "concat";

  /** system-property function string. */
  public static final String FUNC_SYSTEM_PROPERTY_STRING = "system-property";

  /** function-available function string (XSLT). */
  public static final String FUNC_EXT_FUNCTION_AVAILABLE_STRING = "function-available";

  /** element-available function string (XSLT). */
  public static final String FUNC_EXT_ELEM_AVAILABLE_STRING = "element-available";

  /** substring function string. */
  public static final String FUNC_SUBSTRING_STRING = "substring";

  /** string-length function string. */
  public static final String FUNC_STRING_LENGTH_STRING = "string-length";

  /** unparsed-entity-uri function string (XSLT). */
  public static final String FUNC_UNPARSED_ENTITY_URI_STRING = "unparsed-entity-uri";
  
  /** matches function string. */
  public static final String FUNC_MATCHES_STRING = "matches";
  
  /** replace function string. */
  public static final String FUNC_REPLACE_STRING = "replace";
  
  /** current-grouping-key function string (XSLT). */
  public static final String FUNC_CURRENT_GROUPING_KEY = "current-grouping-key";
  
  /** current-group function string (XSLT). */
  public static final String FUNC_CURRENT_GROUP = "current-group";
  
  /** abs function string. */
  public static final String FUNC_ABS = "abs";
  
  /** regex-group function string (XSLT). */
  public static final String FUNC_REGEX_GROUP = "regex-group";
  
  /** tokenize function string. */
  public static final String FUNC_TOKENIZE = "tokenize";
  
  /** analyze-string function string. */
  public static final String FUNC_ANALYZE_STRING = "analyze-string";
  
  /** unparsed-text function string. */
  public static final String FUNC_UNPARSED_TEXT = "unparsed-text";
  
  /** string-join function string. */
  public static final String FUNC_STRING_JOIN = "string-join";
  
  /** current-dateTime function string. */
  public static final String FUNC_CURRENT_DATETIME = "current-dateTime";
  
  /** current-date function string. */
  public static final String FUNC_CURRENT_DATE = "current-date";
  
  /** current-time function string. */
  public static final String FUNC_CURRENT_TIME = "current-time";
  
  /** upper-case function string. */
  public static final String FUNC_UPPER_CASE = "upper-case";
  
  /** lower-case function string. */
  public static final String FUNC_LOWER_CASE = "lower-case";
  
  /** implicit-timezone function string. */
  public static final String FUNC_IMPLICIT_TIMEZONE = "implicit-timezone";
  
  /** index-of function string. */
  public static final String FUNC_INDEX_OF = "index-of";
  
  /** distinct-values function string. */
  public static final String FUNC_DISTINCT_VALUES = "distinct-values";
  
  /** for-each function string. */
  public static final String FUNC_FOR_EACH = "for-each";
  
  /** filter function string. */
  public static final String FUNC_FILTER = "filter";
  
  /** fold-left function string. */
  public static final String FUNC_FOLD_LEFT = "fold-left";
  
  /** fold-right function string. */
  public static final String FUNC_FOLD_RIGHT = "fold-right";
  
  /** for-each-pair function string. */
  public static final String FUNC_FOR_EACH_PAIR = "for-each-pair";
  
  /** sort function string. */
  public static final String FUNC_SORT = "sort";
  
  /** math:pi function string. */
  public static final String FUNC_MATH_PI = "pi";
  
  /** math:exp function string. */
  public static final String FUNC_MATH_EXP = "exp";
  
  /** math:exp10 function string. */
  public static final String FUNC_MATH_EXP10 = "exp10";
  
  /** math:log function string. */
  public static final String FUNC_MATH_LOG = "log";
  
  /** math:log10 function string. */
  public static final String FUNC_MATH_LOG10 = "log10";
  
  /** math:pow function string. */
  public static final String FUNC_MATH_POW = "pow";
  
  /** math:sqrt function string. */
  public static final String FUNC_MATH_SQRT = "sqrt";
  
  /** math:sin function string. */
  public static final String FUNC_MATH_SIN = "sin";
  
  /** math:cos function string. */
  public static final String FUNC_MATH_COS = "cos";
  
  /** math:tan function string. */
  public static final String FUNC_MATH_TAN = "tan";
  
  /** math:asin function string. */
  public static final String FUNC_MATH_ASIN = "asin";
  
  /** math:acos function string. */
  public static final String FUNC_MATH_ACOS = "acos";
  
  /** math:atan function string. */
  public static final String FUNC_MATH_ATAN = "atan";
  
  /** math:atan2 function string. */
  public static final String FUNC_MATH_ATAN2 = "atan2";
  
  /** years-from-duration function string. */
  public static final String FUNC_YEARS_FROM_DURATION = "years-from-duration";
  
  /** months-from-duration function string. */
  public static final String FUNC_MONTHS_FROM_DURATION = "months-from-duration";
  
  /** days-from-duration function string. */
  public static final String FUNC_DAYS_FROM_DURATION = "days-from-duration";
  
  /** hours-from-duration function string. */
  public static final String FUNC_HOURS_FROM_DURATION = "hours-from-duration";
  
  /** minutes-from-duration function string. */
  public static final String FUNC_MINUTES_FROM_DURATION = "minutes-from-duration";
  
  /** seconds-from-duration function string. */
  public static final String FUNC_SECONDS_FROM_DURATION = "seconds-from-duration";
  
  /** codepoints-to-string function string. */
  public static final String FUNC_CODE_POINTS_TO_STRING = "codepoints-to-string";
  
  /** string-to-codepoints function string. */
  public static final String FUNC_STRING_TO_CODE_POINTS = "string-to-codepoints";
  
  /** compare function string. */
  public static final String FUNC_COMPARE = "compare";
  
  /** codepoint-equal function string. */
  public static final String FUNC_CODEPOINT_EQUAL = "codepoint-equal";
  
  /** contains-token function string. */
  public static final String FUNC_CONTAINS_TOKEN = "contains-token";
  
  /**
   * XML Schema built-in data type name keywords.
   */
  
  /** xs:string data type string. */
  public static final String XS_STRING = "string";
  
  /** xs:normalizedString data type string. */
  public static final String XS_NORMALIZED_STRING = "normalizedString";
  
  /** xs:token data type string. */
  public static final String XS_TOKEN = "token";
  
  /** xs:decimal data type string. */
  public static final String XS_DECIMAL = "decimal";
  
  /** xs:float data type string. */
  public static final String XS_FLOAT = "float";
  
  /** xs:double data type string. */
  public static final String XS_DOUBLE = "double";
  
  /** xs:integer data type string. */
  public static final String XS_INTEGER = "integer";
  
  /** xs:nonPositiveInteger data type string. */
  public static final String XS_NON_POSITIVE_INTEGER = "nonPositiveInteger";
  
  /** xs:negativeInteger data type string. */
  public static final String XS_NEGATIVE_INTEGER = "negativeInteger";
  
  /** xs:nonNegativeInteger data type string. */
  public static final String XS_NON_NEGATIVE_INTEGER = "nonNegativeInteger";
  
  /** xs:positiveInteger data type string. */
  public static final String XS_POSITIVE_INTEGER = "positiveInteger";
  
  /** xs:long data type string. */
  public static final String XS_LONG = "long";
  
  /** xs:int data type string. */
  public static final String XS_INT = "int";
  
  /** xs:short data type string. */
  public static final String XS_SHORT = "short";
  
  /** xs:short data type string. */
  public static final String XS_BYTE = "byte";
  
  /** xs:short data type string. */
  public static final String XS_UNSIGNED_LONG = "unsignedLong";
  
  /** xs:short data type string. */
  public static final String XS_UNSIGNED_INT = "unsignedInt";
  
  /** xs:short data type string. */
  public static final String XS_UNSIGNED_SHORT = "unsignedShort";
  
  /** xs:short data type string. */
  public static final String XS_UNSIGNED_BYTE = "unsignedByte";
  
  /** xs:date data type string. */
  public static final String XS_DATE = "date";
  
  /** xs:dateTime data type string. */
  public static final String XS_DATETIME = "dateTime";
  
  /** xs:duration data type string. */
  public static final String XS_DURATION = "duration";
  
  /** xs:yearMonthDuration data type string. */
  public static final String XS_YEAR_MONTH_DURATION = "yearMonthDuration";
  
  /** xs:dayTimeDuration data type string. */
  public static final String XS_DAY_TIME_DURATION = "dayTimeDuration";
  
  /** xs:time data type string. */
  public static final String XS_TIME = "time";
  
  /** xs:gYearMonth data type string. */
  public static final String XS_GYEAR_MONTH = "gYearMonth";
  
  /** xs:gYear data type string. */
  public static final String XS_GYEAR = "gYear";
  
  /** xs:gMonthDay data type string. */
  public static final String XS_GMONTH_DAY = "gMonthDay";
  
  /** xs:gDay data type string. */
  public static final String XS_GDAY = "gDay";
  
  /** xs:gMonth data type string. */
  public static final String XS_GMONTH = "gMonth";
  
  /** xs:anyURI data type string. */
  public static final String XS_ANY_URI = "anyURI";
  
  /** xs:QName data type string. */
  public static final String XS_QNAME = "QName";
    
  /** xs:anyAtomicType data type string. */
  public static final String XS_ANY_ATOMIC_TYPE = "anyAtomicType";
  
  /** xs:untypedAtomic data type string. */
  public static final String XS_UNTYPED_ATOMIC = "untypedAtomic";
  
  /** xs:untyped data type string. */
  public static final String XS_UNTYPED = "untyped";
  
  /** xs:base64Binary data type string. */
  public static final String XS_BASE64BINARY = "base64Binary";
  
  /** xs:hexBinary data type string. */
  public static final String XS_HEXBINARY = "hexBinary";
  
  /** xs:language data type string. */
  public static final String XS_LANGUAGE = "language";
  
  /** xs:Name data type string. */
  public static final String XS_NAME = "Name";
  
  /** xs:NCName data type string. */
  public static final String XS_NCNAME = "NCName";
  
  /** xs:NMTOKEN data type string. */
  public static final String XS_NMTOKEN = "NMTOKEN";
  
  /** xs:ID data type string. */
  public static final String XS_ID = "ID";
  
  /** xs:IDREF data type string. */
  public static final String XS_IDREF = "IDREF";
  
  
  /** empty function string. */
  public static final String FUNC_EMPTY = "empty";
  
  /** exists function string. */
  public static final String FUNC_EXISTS = "exists";
  
  /** head function string. */
  public static final String FUNC_HEAD = "head";
  
  /** tail function string. */
  public static final String FUNC_TAIL = "tail";
  
  /** insert-before function string. */
  public static final String FUNC_INSERT_BEFORE = "insert-before";
  
  /** remove function string. */
  public static final String FUNC_REMOVE = "remove";
  
  /** reverse function string. */
  public static final String FUNC_REVERSE = "reverse";
  
  /** subsequence function string. */
  public static final String FUNC_SUBSEQUENCE = "subsequence";
  
  /** unordered function string. */
  public static final String FUNC_UNORDERED = "unordered";
  
  /** parse-xml function string. */
  public static final String FUNC_PARSE_XML = "parse-xml";
  
  /** parse-xml-fragment function string. */
  public static final String FUNC_PARSE_XML_FRAGMENT = "parse-xml-fragment";
  
  /** avg function string. */
  public static final String FUNC_AVG = "avg";
  
  /** max function string. */
  public static final String FUNC_MAX = "max";
  
  /** min function string. */
  public static final String FUNC_MIN = "min";
  
  /** doc function string. */
  public static final String FUNC_DOC = "doc";
  
  /** node-name function string. */
  public static final String FUNC_NODE_NAME = "node-name";
  
  /** deep-equal function string. */
  public static final String FUNC_DEEP_EQUAL = "deep-equal";
  
  /** dateTime function string. */
  public static final String FUNC_DATE_TIME = "dateTime";
  
  /** year-from-dateTime function string. */
  public static final String FUNC_YEAR_FROM_DATE_TIME = "year-from-dateTime";
  
  /** month-from-dateTime function string. */
  public static final String FUNC_MONTH_FROM_DATE_TIME = "month-from-dateTime";
  
  /** day-from-dateTime function string. */
  public static final String FUNC_DAY_FROM_DATE_TIME = "day-from-dateTime";
  
  /** hours-from-dateTime function string. */
  public static final String FUNC_HOURS_FROM_DATE_TIME = "hours-from-dateTime";
  
  /** minutes-from-dateTime function string. */
  public static final String FUNC_MINUTES_FROM_DATE_TIME = "minutes-from-dateTime";
  
  /** seconds-from-dateTime function string. */
  public static final String FUNC_SECONDS_FROM_DATE_TIME = "seconds-from-dateTime";
  
  /** timezone-from-dateTime function string. */
  public static final String FUNC_TIMEZONE_FROM_DATE_TIME = "timezone-from-dateTime";
  
  /** year-from-date function string. */
  public static final String FUNC_YEAR_FROM_DATE = "year-from-date";
  
  /** year-from-date function string. */
  public static final String FUNC_MONTH_FROM_DATE = "month-from-date";
  
  /** day-from-date function string. */
  public static final String FUNC_DAY_FROM_DATE = "day-from-date";
  
  /** timezone-from-date function string. */
  public static final String FUNC_TIMEZONE_FROM_DATE = "timezone-from-date";
  
  /** hours-from-time function string. */
  public static final String FUNC_HOURS_FROM_TIME = "hours-from-time";
  
  /** minutes-from-time function string. */
  public static final String FUNC_MINUTES_FROM_TIME = "minutes-from-time";
  
  /** seconds-from-time function string. */
  public static final String FUNC_SECONDS_FROM_TIME = "seconds-from-time";
  
  /** timezone-from-time function string. */
  public static final String FUNC_TIMEZONE_FROM_TIME = "timezone-from-time";
  
  /** default-collation function string. */
  public static final String FUNC_DEFAULT_COLLATION = "default-collation";
  
  /** base-uri function string. */
  public static final String FUNC_BASE_URI = "base-uri";
  
  /** document-uri function string. */
  public static final String FUNC_DOCUMENT_URI = "document-uri";
  
  /** array:size function string. */
  public static final String FUNC_ARRAY_SIZE = "size";
  
  /** array:get function string. */
  public static final String FUNC_ARRAY_GET = "get";
  
  /** array:put function string. */
  public static final String FUNC_ARRAY_PUT = "put";
  
  /** resolve-QName function string. */
  public static final String FUNC_RESOLVE_QNAME = "resolve-QName";
  
  /** QName function string. */
  public static final String FUNC_QNAME = "QName";
  
  /** prefix-from-QName function string. */
  public static final String FUNC_PREFIX_FROM_QNAME = "prefix-from-QName";
  
  /** local-name-from-QName function string. */
  public static final String FUNC_LOCAL_NAME_FROM_QNAME = "local-name-from-QName";
  
  /** namespace-uri-from-QName function string. */
  public static final String FUNC_NAMESPACE_URI_FROM_QNAME = "namespace-uri-from-QName";
  
  /** namespace-uri-for-prefix function string. */
  public static final String FUNC_NAMESPACE_URI_FOR_PREFIX = "namespace-uri-for-prefix";
  
  /** in-scope-prefixes function string. */
  public static final String FUNC_IN_SCOPE_PREFIXES = "in-scope-prefixes";
  
  /** map:merge function string. */
  public static final String FUNC_MAP_MERGE = "merge";
  
  /** map:size function string. */
  public static final String FUNC_MAP_SIZE = "size";
  
  /** map:keys function string. */
  public static final String FUNC_MAP_KEYS = "keys";
  
  /** map:contains function string. */
  public static final String FUNC_MAP_CONTAINS = "contains";
  
  /** map:get function string. */
  public static final String FUNC_MAP_GET = "get";
  
  /** map:put function string. */
  public static final String FUNC_MAP_PUT = "put";
  
  /** map:entry function string. */
  public static final String FUNC_MAP_ENTRY = "entry";
  
  /** map:for-each function string. */
  public static final String FUNC_MAP_FOREACH = "for-each";
  
  /** map:remove function string. */
  public static final String FUNC_MAP_REMOVE = "remove";
  
  /** map:find function string. */
  public static final String FUNC_MAP_FIND = "find";
  
  /** parse-json function string. */
  public static final String FUNC_PARSE_JSON = "parse-json";
  
  /** json-doc function string. */
  public static final String FUNC_JSON_DOC = "json-doc";
  
  /** json-to-xml function string. */
  public static final String FUNC_JSON_TO_XML = "json-to-xml";
  
  /** xml-to-json function string. */
  public static final String FUNC_XML_TO_JSON = "xml-to-json";
  
  /** array:append function string. */
  public static final String FUNC_ARRAY_APPEND = "append";
  
  /** array:subarray function string. */
  public static final String FUNC_ARRAY_SUBARRAY = "subarray";
  
  /** array:remove function string. */
  public static final String FUNC_ARRAY_REMOVE = "remove";
  
  /** array:insert-before function string. */
  public static final String FUNC_ARRAY_INSERT_BEFORE = "insert-before";
  
  /** array:head function string. */
  public static final String FUNC_ARRAY_HEAD = "head";
  
  /** array:tail function string. */
  public static final String FUNC_ARRAY_TAIL = "tail";
  
  /** array:reverse function string. */
  public static final String FUNC_ARRAY_REVERSE = "reverse";
  
  /** array:join function string. */
  public static final String FUNC_ARRAY_JOIN = "join";
  
  /** array:for-each function string. */
  public static final String FUNC_ARRAY_FOR_EACH = "for-each";
  
  /** array:filter function string. */
  public static final String FUNC_ARRAY_FILTER = "filter";
  
  /** array:for-each-pair function string. */
  public static final String FUNC_ARRAY_FOR_EACH_PAIR = "for-each-pair";
  
  /** array:fold-left function string. */
  public static final String FUNC_ARRAY_FOLD_LEFT = "fold-left";
  
  /** array:fold-right function string. */
  public static final String FUNC_ARRAY_FOLD_RIGHT = "fold-right";
  
  /** array:sort function string. */
  public static final String FUNC_ARRAY_SORT = "sort";
  
  /** array:flatten function string. */
  public static final String FUNC_ARRAY_FLATTEN = "flatten";
  
  /** apply function string. */
  public static final String FUNC_APPLY = "apply";
  
  /** doc-available function string. */
  public static final String FUNC_DOC_AVAILABLE = "doc-available";
  
  /** unparsed-text-lines function string. */
  public static final String FUNC_UNPARSED_TEXT_LINES = "unparsed-text-lines";
  
  /** collection function string. */
  public static final String FUNC_COLLECTION = "collection";
  
  /** current-merge-group function string (XSLT). */
  public static final String FUNC_CURRENT_MERGE_GROUP = "current-merge-group";
  
  /** current-merge-key function string (XSLT). */
  public static final String FUNC_CURRENT_MERGE_KEY = "current-merge-key";
  
  /** transform function string. */
  public static final String FUNC_TRANSFORM = "transform";
  
  // Proprietary, built in functions

  /** current function string (Proprietary). */
  public static final String FUNC_DOCLOCATION_STRING = "document-location";

  static {	  
	  m_axisnames.put(FROM_ANCESTORS_STRING, new Integer(OpCodes.FROM_ANCESTORS));
	  m_axisnames.put(FROM_ANCESTORS_OR_SELF_STRING, new Integer(OpCodes.FROM_ANCESTORS_OR_SELF));
	  m_axisnames.put(FROM_ATTRIBUTES_STRING, new Integer(OpCodes.FROM_ATTRIBUTES));
	  m_axisnames.put(FROM_CHILDREN_STRING, new Integer(OpCodes.FROM_CHILDREN));
	  m_axisnames.put(FROM_DESCENDANTS_STRING, new Integer(OpCodes.FROM_DESCENDANTS));
	  m_axisnames.put(FROM_DESCENDANTS_OR_SELF_STRING, new Integer(OpCodes.FROM_DESCENDANTS_OR_SELF));
	  m_axisnames.put(FROM_FOLLOWING_STRING, new Integer(OpCodes.FROM_FOLLOWING));
	  m_axisnames.put(FROM_FOLLOWING_SIBLINGS_STRING, new Integer(OpCodes.FROM_FOLLOWING_SIBLINGS));
	  m_axisnames.put(FROM_PARENT_STRING, new Integer(OpCodes.FROM_PARENT));
	  m_axisnames.put(FROM_PRECEDING_STRING, new Integer(OpCodes.FROM_PRECEDING));
	  m_axisnames.put(FROM_PRECEDING_SIBLINGS_STRING, new Integer(OpCodes.FROM_PRECEDING_SIBLINGS));
	  m_axisnames.put(FROM_SELF_STRING, new Integer(OpCodes.FROM_SELF));
	  m_axisnames.put(FROM_NAMESPACE_STRING, new Integer(OpCodes.FROM_NAMESPACE));
	  m_nodetypes.put(NODETYPE_COMMENT_STRING, new Integer(OpCodes.NODETYPE_COMMENT));
	  m_nodetypes.put(NODETYPE_TEXT_STRING, new Integer(OpCodes.NODETYPE_TEXT));
	  m_nodetypes.put(NODETYPE_PI_STRING, new Integer(OpCodes.NODETYPE_PI));
	  m_nodetypes.put(NODETYPE_NODE_STRING, new Integer(OpCodes.NODETYPE_NODE));
	  m_nodetypes.put(NODETYPE_ANYELEMENT_STRING, new Integer(OpCodes.NODETYPE_ANYELEMENT));

	  // Added for XSLT 3.0
	  m_nodetypes.put(NODETYPE_CONTEXT_ITEM_STRING, new Integer(OpCodes.NODETYPE_CONTEXT_ITEM));
	  m_nodetypes.put(NODETYPE_DOCUMENT_STRING, new Integer(OpCodes.NODETYPE_DOCUMENT));

	  m_keywords.put(FROM_SELF_ABBREVIATED_STRING, new Integer(OpCodes.FROM_SELF));
	  m_keywords.put(FUNC_ID_STRING, new Integer(FunctionTable.FUNC_ID));
	  m_keywords.put(FUNC_KEY_STRING, new Integer(FunctionTable.FUNC_KEY));
	  m_nodetests.put(NODETYPE_COMMENT_STRING, new Integer(OpCodes.NODETYPE_COMMENT));
	  m_nodetests.put(NODETYPE_TEXT_STRING, new Integer(OpCodes.NODETYPE_TEXT));
	  m_nodetests.put(NODETYPE_PI_STRING, new Integer(OpCodes.NODETYPE_PI));
	  m_nodetests.put(NODETYPE_NODE_STRING, new Integer(OpCodes.NODETYPE_NODE));
  }
  
  static Object getAxisName(String key){
	  return m_axisnames.get(key);
  }

  static Object lookupNodeTest(String key){
	  return m_nodetests.get(key);
  }

  static Object getKeyWord(String key){
	  return m_keywords.get(key);
  }

  static Object getNodeType(String key){
	  return m_nodetypes.get(key);
  }      
}
