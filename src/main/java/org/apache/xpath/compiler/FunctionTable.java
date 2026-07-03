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
package org.apache.xpath.compiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.apache.xpath.functions.Function;

/**
 * The function table for XPath 3.1 and XSLT 3.0.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Christine Li <jycli@apache.org>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3.1 and XSLT 3.0 specific changes, to this class)
 */
public class FunctionTable
{

  /** The 'current()' id. */
  public static final int FUNC_CURRENT = 0;

  /** The 'last()' id. */
  public static final int FUNC_LAST = 1;

  /** The 'position()' id. */
  public static final int FUNC_POSITION = 2;

  /** The 'count()' id. */
  public static final int FUNC_COUNT = 3;

  /** The 'id()' id. */
  public static final int FUNC_ID = 4;

  /** The 'key()' id (XSLT). */
  public static final int FUNC_KEY = 5;

  /** The 'local-name()' id. */
  public static final int FUNC_LOCAL_NAME = 7;

  /** The 'namespace-uri()' id. */
  public static final int FUNC_NAMESPACE_URI = 8;  

  /** The 'generate-id()' id. */
  public static final int FUNC_GENERATE_ID = 10;

  /** The 'not()' id. */
  public static final int FUNC_NOT = 11;

  /** The 'true()' id. */
  public static final int FUNC_TRUE = 12;

  /** The 'false()' id. */
  public static final int FUNC_FALSE = 13;

  /** The 'boolean()' id. */
  public static final int FUNC_BOOLEAN = 14;

  /** The 'number()' id. */
  public static final int FUNC_NUMBER = 15;

  /** The 'floor()' id. */
  public static final int FUNC_FLOOR = 16;

  /** The 'ceiling()' id. */
  public static final int FUNC_CEILING = 17;

  /** The 'round()' id. */
  public static final int FUNC_ROUND = 18;

  /** The 'sum()' id. */
  public static final int FUNC_SUM = 19;

  /** The 'string()' id. */
  public static final int FUNC_STRING = 20;

  /** The 'starts-with()' id. */
  public static final int FUNC_STARTS_WITH = 21;

  /** The 'contains()' id. */
  public static final int FUNC_CONTAINS = 22;

  /** The 'substring-before()' id. */
  public static final int FUNC_SUBSTRING_BEFORE = 23;

  /** The 'substring-after()' id. */
  public static final int FUNC_SUBSTRING_AFTER = 24;

  /** The 'normalize-space()' id. */
  public static final int FUNC_NORMALIZE_SPACE = 25;

  /** The 'translate()' id. */
  public static final int FUNC_TRANSLATE = 26;

  /** The 'concat()' id. */
  public static final int FUNC_CONCAT = 27;

  /** The 'substring()' id. */
  public static final int FUNC_SUBSTRING = 29;

  /** The 'string-length()' id. */
  public static final int FUNC_STRING_LENGTH = 30;

  /** The 'system-property()' id. */
  public static final int FUNC_SYSTEM_PROPERTY = 31;

  /** The 'lang()' id. */
  public static final int FUNC_LANG = 32;

  /** The 'function-available()' id (XSLT). */
  public static final int FUNC_EXT_FUNCTION_AVAILABLE = 33;

  /** The 'element-available()' id (XSLT). */
  public static final int FUNC_EXT_ELEM_AVAILABLE = 34;

  /** The 'unparsed-entity-uri()' id (XSLT). */
  public static final int FUNC_UNPARSED_ENTITY_URI = 36;
  
  /** The 'matches()' id. */
  public static final int FUNC_MATCHES = 37;
  
  /** The 'replace()' id. */
  public static final int FUNC_REPLACE = 38;
  
  /** The 'current-grouping-key()' id (XSLT). */
  public static final int FUNC_CURRENT_GROUPING_KEY = 39;
  
  /** The 'current-group()' id (XSLT). */
  public static final int FUNC_CURRENT_GROUP = 40;
  
  /** The 'abs()' id. */
  public static final int FUNC_ABS = 41;
  
  /** The 'regex-group()' id (XSLT). */
  public static final int FUNC_REGEX_GROUP = 42;
  
  /** The 'tokenize()' id. */
  public static final int FUNC_TOKENIZE = 43;
  
  /** The 'unparsed-text()' id. */
  public static final int FUNC_UNPARSED_TEXT = 44;
  
  /** The 'string-join()' id. */
  public static final int FUNC_STRING_JOIN = 45;
  
  /** The 'current-dateTime()' id. */
  public static final int FUNC_CURRENT_DATETIME = 46;
  
  /** The 'current-date()' id. */
  public static final int FUNC_CURRENT_DATE = 47;
  
  /** The 'current-time()' id. */
  public static final int FUNC_CURRENT_TIME = 48;
  
  /** The 'upper-case()' id. */
  public static final int FUNC_UPPER_CASE = 49;
  
  /** The 'lower-case()' id. */
  public static final int FUNC_LOWER_CASE = 50;
  
  /** The 'implicit-timezone()' id. */
  public static final int FUNC_IMPLICIT_TIMEZONE = 51;
  
  /** The 'index-of()' id. */
  public static final int FUNC_INDEX_OF = 52;
  
  /** The 'for-each()' id. */
  public static final int FUNC_FOR_EACH = 53;
  
  /** The 'filter()' id. */
  public static final int FUNC_FILTER = 54;
  
  /** The 'distinct-values()' id. */
  public static final int FUNC_DISTINCT_VALUES = 55;
  
  /** The 'math:pi()' id. */
  public static final int FUNC_MATH_PI = 56;
  
  /** The 'math:exp()' id. */
  public static final int FUNC_MATH_EXP = 57;
  
  /** The 'math:exp10()' id. */
  public static final int FUNC_MATH_EXP10 = 58;
  
  /** The 'math:log()' id. */
  public static final int FUNC_MATH_LOG = 59;
  
  /** The 'math:log10()' id. */
  public static final int FUNC_MATH_LOG10 = 60;
  
  /** The 'math:pow()' id. */
  public static final int FUNC_MATH_POW = 61;
  
  /** The 'math:sqrt()' id. */
  public static final int FUNC_MATH_SQRT = 62;
  
  /** The 'math:sin()' id. */
  public static final int FUNC_MATH_SIN = 63;
  
  /** The 'math:cos()' id. */
  public static final int FUNC_MATH_COS = 64;
  
  /** The 'math:tan()' id. */
  public static final int FUNC_MATH_TAN = 65;
  
  /** The 'math:asin()' id. */
  public static final int FUNC_MATH_ASIN = 66;
  
  /** The 'math:acos()' id. */
  public static final int FUNC_MATH_ACOS = 67;
  
  /** The 'math:atan()' id. */
  public static final int FUNC_MATH_ATAN = 68;
  
  /** The 'math:atan2()' id. */
  public static final int FUNC_MATH_ATAN2 = 69;
  
  /** The 'years-from-duration()' id. */
  public static final int FUNC_YEARS_FROM_DURATION = 70;
  
  /** The 'months-from-duration()' id. */
  public static final int FUNC_MONTHS_FROM_DURATION = 71;
  
  /** The 'days-from-duration()' id. */
  public static final int FUNC_DAYS_FROM_DURATION = 72;
  
  /** The 'hours-from-duration()' id. */
  public static final int FUNC_HOURS_FROM_DURATION = 73;
  
  /** The 'minutes-from-duration()' id. */
  public static final int FUNC_MINUTES_FROM_DURATION = 74;
  
  /** The 'seconds-from-duration()' id. */
  public static final int FUNC_SECONDS_FROM_DURATION = 75;
  
  /** The 'fold-left()' id. */
  public static final int FUNC_FOLD_LEFT = 76;
  
  /** The 'fold-right()' id. */
  public static final int FUNC_FOLD_RIGHT = 77;
  
  /** The 'for-each-pair()' id. */
  public static final int FUNC_FOR_EACH_PAIR = 78;
  
  /** The 'sort()' id. */
  public static final int FUNC_SORT = 79;
  
  /** The 'codepoints-to-string()' id. */
  public static final int FUNC_CODE_POINTS_TO_STRING = 80;
  
  /** The 'string-to-codepoints()' id. */
  public static final int FUNC_STRING_TO_CODE_POINTS = 81;
  
  /** The 'compare()' id. */
  public static final int FUNC_COMPARE = 82;
  
  /** The 'codepoint-equal()' id. */
  public static final int FUNC_CODEPOINT_EQUAL = 83;
  
  /** The 'empty()' id. */
  public static final int FUNC_EMPTY = 84;
  
  /** The 'exists()' id. */
  public static final int FUNC_EXISTS = 85;
  
  /** The 'head()' id. */
  public static final int FUNC_HEAD = 86;
  
  /** The 'tail()' id. */
  public static final int FUNC_TAIL = 87;
  
  /** The 'insert-before()' id. */
  public static final int FUNC_INSERT_BEFORE = 88;
  
  /** The 'remove()' id. */
  public static final int FUNC_REMOVE = 89;
  
  /** The 'reverse()' id. */
  public static final int FUNC_REVERSE = 90;
  
  /** The 'subsequence()' id. */
  public static final int FUNC_SUBSEQUENCE = 91;
  
  /** The 'unordered()' id. */
  public static final int FUNC_UNORDERED = 92;
  
  /** The 'parse-xml()' id. */
  public static final int FUNC_PARSE_XML = 93;
  
  /** The 'parse-xml-fragment()' id. */
  public static final int FUNC_PARSE_XML_FRAGMENT = 94;
  
  /** The 'avg()' id. */
  public static final int FUNC_AVG = 95;
  
  /** The 'max()' id. */
  public static final int FUNC_MAX = 96;
  
  /** The 'min()' id. */
  public static final int FUNC_MIN = 97;
  
  /** The 'contains-token()' id. */
  public static final int FUNC_CONTAINS_TOKEN = 98;
  
  /** The 'doc()' id. */
  public static final int FUNC_DOC = 99;
  
  /** The 'data()' id. */
  public static final int FUNC_DATA = 100;
  
  /** The 'node-name()' id. */
  public static final int FUNC_NODE_NAME = 101;
  
  /** The 'deep-equal()' id. */
  public static final int FUNC_DEEP_EQUAL = 102;
  
  /** The 'dateTime()' id. */
  public static final int FUNC_DATE_TIME = 103;
  
  /** The 'year-from-dateTime()' id. */
  public static final int FUNC_YEAR_FROM_DATE_TIME = 104;
  
  /** The 'month-from-dateTime()' id. */
  public static final int FUNC_MONTH_FROM_DATE_TIME = 105;
  
  /** The 'day-from-dateTime()' id. */
  public static final int FUNC_DAY_FROM_DATE_TIME = 106;
  
  /** The 'hours-from-dateTime()' id. */
  public static final int FUNC_HOURS_FROM_DATE_TIME = 107;
  
  /** The 'minutes-from-dateTime()' id. */
  public static final int FUNC_MINUTES_FROM_DATE_TIME = 108;
  
  /** The 'seconds-from-dateTime()' id. */
  public static final int FUNC_SECONDS_FROM_DATE_TIME = 109;
  
  /** The 'timezone-from-dateTime()' id. */
  public static final int FUNC_TIMEZONE_FROM_DATE_TIME = 110;
  
  /** The 'year-from-date()' id. */
  public static final int FUNC_YEAR_FROM_DATE = 111;
  
  /** The 'month-from-date()' id. */
  public static final int FUNC_MONTH_FROM_DATE = 112;
  
  /** The 'day-from-date()' id. */
  public static final int FUNC_DAY_FROM_DATE = 113;
  
  /** The 'timezone-from-date()' id. */
  public static final int FUNC_TIMEZONE_FROM_DATE = 114;
  
  /** The 'hours-from-time()' id. */
  public static final int FUNC_HOURS_FROM_TIME = 115;
  
  /** The 'minutes-from-time()' id. */
  public static final int FUNC_MINUTES_FROM_TIME = 116;
  
  /** The 'seconds-from-time()' id. */
  public static final int FUNC_SECONDS_FROM_TIME = 117;
  
  /** The 'timezone-from-time()' id. */
  public static final int FUNC_TIMEZONE_FROM_TIME = 118;
  
  /** The 'default-collation()' id. */
  public static final int FUNC_DEFAULT_COLLATION = 119;
  
  /** The 'base-uri()' id. */
  public static final int FUNC_BASE_URI = 120;
  
  /** The 'document-uri()' id. */
  public static final int FUNC_DOCUMENT_URI = 121;
  
  /** The 'array:size()' id. */
  public static final int FUNC_ARRAY_SIZE = 124;
  
  /** The 'array:get()' id. */
  public static final int FUNC_ARRAY_GET = 125;
  
  /** The 'array:put()' id. */
  public static final int FUNC_ARRAY_PUT = 126;
  
  /** The 'name()' id. */
  public static final int FUNC_NAME = 127;
  
  /** The 'resolve-QName()' id. */
  public static final int FUNC_RESOLVE_QNAME = 128;
  
  /** The 'QName()' id. */
  public static final int FUNC_QNAME = 129;
  
  /** The 'QName-equal()' id. */
  public static final int FUNC_QNAME_EQUAL = 130;
  
  /** The 'prefix-from-QName' id. */
  public static final int FUNC_PREFIX_FROM_QNAME = 131;
  
  /** The 'local-name-from-QName' id. */
  public static final int FUNC_LOCAL_NAME_FROM_QNAME = 132;
  
  /** The 'namespace-uri-from-QName' id. */
  public static final int FUNC_NAMESPACE_URI_FROM_QNAME = 133;
  
  /** The 'namespace-uri-for-prefix' id. */
  public static final int FUNC_NAMESPACE_URI_FOR_PREFIX = 134;
  
  /** The 'in-scope-prefixes' id. */
  public static final int FUNC_IN_SCOPE_PREFIXES = 135;
  
  /** The 'root' id. */
  public static final int FUNC_ROOT = 136;
  
  /** The 'map:size()' id. */
  public static final int FUNC_MAP_SIZE = 137;
  
  /** The 'map:keys()' id. */
  public static final int FUNC_MAP_KEYS = 138;
  
  /** The 'map:contains()' id. */
  public static final int FUNC_MAP_CONTAINS = 139;
  
  /** The 'map:get()' id. */
  public static final int FUNC_MAP_GET = 140;
  
  /** The 'map:put()' id. */
  public static final int FUNC_MAP_PUT = 141;
  
  /** The 'map:entry()' id. */
  public static final int FUNC_MAP_ENTRY = 142;
    
  /** The 'parse-json' id. */
  public static final int FUNC_PARSE_JSON = 143;
  
  /** The 'array:append()' id. */
  public static final int FUNC_ARRAY_APPEND = 144;
  
  /** The 'array:subarray()' id. */
  public static final int FUNC_ARRAY_SUBARRAY = 145;
  
  /** The 'array:remove()' id. */
  public static final int FUNC_ARRAY_REMOVE = 146;
  
  /** The 'array:insert-before()' id. */
  public static final int FUNC_ARRAY_INSERT_BEFORE = 147;
  
  /** The 'array:head()' id. */
  public static final int FUNC_ARRAY_HEAD = 148;
  
  /** The 'array:tail()' id. */
  public static final int FUNC_ARRAY_TAIL = 149;
  
  /** The 'array:reverse()' id. */
  public static final int FUNC_ARRAY_REVERSE = 150;
  
  /** The 'map:for-each()' id. */
  public static final int FUNC_MAP_FOREACH = 151;
  
  /** The 'array:join()' id. */
  public static final int FUNC_ARRAY_JOIN = 152;
  
  /** The 'array:for-each()' id. */
  public static final int FUNC_ARRAY_FOR_EACH = 153;
  
  /** The 'array:filter()' id. */
  public static final int FUNC_ARRAY_FILTER = 154;
  
  /** The 'array:for-each-pair()' id. */
  public static final int FUNC_ARRAY_FOR_EACH_PAIR = 155;
  
  /** The 'map:merge()' id. */
  public static final int FUNC_MAP_MERGE = 156;
  
  /** The 'array:fold-left()' id. */
  public static final int FUNC_ARRAY_FOLD_LEFT = 157;
  
  /** The 'array:fold-right()' id. */
  public static final int FUNC_ARRAY_FOLD_RIGHT = 158;
  
  /** The 'map:remove()' id. */
  public static final int FUNC_MAP_REMOVE = 159;
  
  /** The 'array:sort()' id. */
  public static final int FUNC_ARRAY_SORT = 160;
  
  /** The 'json-to-xml()' id. */
  public static final int FUNC_JSON_TO_XML = 161;
  
  /** The 'xml-to-json()' id. */
  public static final int FUNC_XML_TO_JSON = 162;
  
  /** The 'json-doc()' id. */
  public static final int FUNC_JSON_DOC = 163;
  
  /** The 'analyze-string()' id. */
  public static final int FUNC_ANALYZE_STRING = 164;
  
  /** The 'apply()' id. */
  public static final int FUNC_APPLY = 165;
  
  /** The 'map:find()' id. */
  public static final int FUNC_MAP_FIND = 166;
  
  /** The 'array:flatten()' id. */
  public static final int FUNC_ARRAY_FLATTEN = 167;
  
  /** The 'doc-available()' id. */
  public static final int FUNC_DOC_AVAILABLE = 168;
  
  /** The 'unparsed-text-lines()' id. */
  public static final int FUNC_UNPARSED_TEXT_LINES = 169;
  
  /** The 'collection()' id. */
  public static final int FUNC_COLLECTION = 170;
  
  /** The 'current-merge-group()' id (XSLT). */
  public static final int FUNC_CURRENT_MERGE_GROUP = 171;
  
  /** The 'current-merge-key()' id (XSLT). */
  public static final int FUNC_CURRENT_MERGE_KEY = 172;
  
  /** The 'transform()' id. */
  public static final int FUNC_TRANSFORM = 173;
  
  /** The 'ends-with()' id. */
  public static final int FUNC_ENDS_WITH = 174;
  
  /** The 'function-arity()' id. */
  public static final int FUNC_FUNCTION_ARITY = 175;
  
  /** The 'function-name()' id. */
  public static final int FUNC_FUNCTION_NAME = 176;
  
  /** The 'normalize-unicode()' id. */
  public static final int FUNC_NORMALIZE_UNICODE = 177;
  
  /** The 'round-half-to-even()' id. */
  public static final int FUNC_ROUND_HALF_TO_EVEN = 178;
  
  /** The 'error()' id. */
  public static final int FUNC_ERROR = 179;
  
  /** The 'random-number-generator()' id. */
  public static final int FUNC_RANDOM_NUMBER_GENERATOR = 180;
  
  /** The 'adjust-dateTime-to-timezone()' id. */
  public static final int FUNC_ADJUST_DATETIME_TO_TIMEZONE = 181;
  
  /** The 'adjust-date-to-timezone()' id. */
  public static final int FUNC_ADJUST_DATE_TO_TIMEZONE = 182;
  
  /** The 'adjust-time-to-timezone()' id. */
  public static final int FUNC_ADJUST_TIME_TO_TIMEZONE = 183;
  
  /** The 'format-integer()' id. */
  public static final int FUNC_FORMAT_INTEGER = 184;
  
  /** The 'environment-variable()' id. */
  public static final int FUNC_ENVIRONMENT_VARIABLE = 185;
  
  /** The 'available-environment-variables()' id. */
  public static final int FUNC_AVAILABLE_ENVIRONMENT_VARIABLES = 186;
  
  /** The 'unparsed-text-available()' id. */
  public static final int FUNC_UNPARSED_TEXT_AVAILABLE = 187;
  
  /** The 'copy-of()' id (XSLT). */
  public static final int FUNC_COPY_OF = 188;
  
  /** The 'serialize()' id. */
  public static final int FUNC_SERIALIZE = 189;
  
  /** The 'format-dateTime()' id. */
  public static final int FUNC_FORMAT_DATETIME = 190;
  
  /** The 'format-date()' id. */
  public static final int FUNC_FORMAT_DATE = 191;
  
  /** The 'format-time()' id. */
  public static final int FUNC_FORMAT_TIME = 192;
  
  /** The 'parse-ietf-date()' id. */
  public static final int FUNC_PARSE_IETF_DATE = 193;
  
  /** The '.()' id. */
  public static final int FUNC_PERIOD = 194;
  
  /** The 'zero-or-one()' id. */
  public static final int FUNC_ZERO_OR_ONE = 195;
  
  /** The 'one-or-more()' id. */
  public static final int FUNC_ONE_OR_MORE = 196;
  
  /** The 'exactly-one()' id. */
  public static final int FUNC_EXACTLY_ONE = 197;
  
  /** The 'path()' id. */
  public static final int FUNC_PATH = 198;
  
  /** The 'document()' id (XSLT). */
  public static final int FUNC_DOCUMENT = 199;
  
  /** The 'format-number()' id. */
  public static final int FUNC_FORMAT_NUMBER = 200;

  // Proprietary

  /** The 'document-location()' id (Proprietary). */
  public static final int FUNC_DOCLOCATION = 35;

  /**
   * The function table.
   */
  private static Class m_functions[];

  /** Table of function ID to function name associations. */
  private static Map<Integer, String> m_functionId = new HashMap<Integer, String>();
    
  /**
   * The function table contains customized functions
   */
  private Class m_functions_customer[] = new Class[NUM_ALLOWABLE_ADDINS];

  /**
   * Table of function name to function ID associations for customized functions
   */
  // private Map<String, Integer> m_functionId_customer = new HashMap<String, Integer>();
  
  /**
   * Number of built in functions. Please update this, as
   * built-in functions are added.
   */
  private static final int NUM_BUILT_IN_FUNCS = 201;

  /**
   * Number of built-in functions that may be added.
   */
  private static final int NUM_ALLOWABLE_ADDINS = 30;

  /**
   * The index to the next free function index.
   */
  private int m_funcNextFreeIndex = NUM_BUILT_IN_FUNCS;
  
  static {
	  m_functions = new Class[NUM_BUILT_IN_FUNCS];
	  
	  m_functions[FUNC_CURRENT] = org.apache.xpath.functions.FuncCurrent.class;
	  m_functions[FUNC_LAST] = org.apache.xpath.functions.context.FuncLast.class;
	  m_functions[FUNC_POSITION] = org.apache.xpath.functions.context.FuncPosition.class;
	  m_functions[FUNC_COUNT] = org.apache.xpath.functions.FuncCount.class;
	  m_functions[FUNC_ID] = org.apache.xpath.functions.FuncId.class;
	  m_functions[FUNC_KEY] = org.apache.xalan.templates.FuncKey.class;
	  m_functions[FUNC_LOCAL_NAME] = org.apache.xpath.functions.FuncLocalName.class;
	  m_functions[FUNC_NAMESPACE_URI] = org.apache.xpath.functions.FuncNamespaceUri.class;

	  m_functions[FUNC_NAME] = org.apache.xpath.functions.FuncName.class;

	  m_functions[FUNC_RESOLVE_QNAME] = org.apache.xpath.functions.FuncResolveQName.class;
	  m_functions[FUNC_QNAME] = org.apache.xpath.functions.FuncQName.class;
	  m_functions[FUNC_PREFIX_FROM_QNAME] = org.apache.xpath.functions.FuncPrefixFromQName.class;
	  m_functions[FUNC_LOCAL_NAME_FROM_QNAME] = org.apache.xpath.functions.FuncLocalNameFromQName.class;
	  m_functions[FUNC_NAMESPACE_URI_FROM_QNAME] = org.apache.xpath.functions.FuncNamespaceUriFromQName.class;
	  m_functions[FUNC_NAMESPACE_URI_FOR_PREFIX] = org.apache.xpath.functions.FuncNamespaceUriForPrefix.class;
	  m_functions[FUNC_IN_SCOPE_PREFIXES] = org.apache.xpath.functions.FuncInScopePrefixes.class;

	  m_functions[FUNC_GENERATE_ID] = org.apache.xpath.functions.FuncGenerateId.class;
	  m_functions[FUNC_NOT] = org.apache.xpath.functions.FuncNot.class;
	  m_functions[FUNC_TRUE] = org.apache.xpath.functions.FuncTrue.class;
	  m_functions[FUNC_FALSE] = org.apache.xpath.functions.FuncFalse.class;
	  m_functions[FUNC_BOOLEAN] = org.apache.xpath.functions.FuncBoolean.class;
	  m_functions[FUNC_LANG] = org.apache.xpath.functions.FuncLang.class;
	  m_functions[FUNC_ROOT] = org.apache.xpath.functions.FuncRoot.class;
	  m_functions[FUNC_NUMBER] = org.apache.xpath.functions.FuncNumber.class;
	  m_functions[FUNC_FLOOR] = org.apache.xpath.functions.FuncFloor.class;
	  m_functions[FUNC_CEILING] = org.apache.xpath.functions.FuncCeiling.class;
	  m_functions[FUNC_ROUND] = org.apache.xpath.functions.FuncRound.class;
	  m_functions[FUNC_ROUND_HALF_TO_EVEN] = org.apache.xpath.functions.FuncRoundHalfToEven.class;
	  m_functions[FUNC_SUM] = org.apache.xpath.functions.FuncSum.class;
	  m_functions[FUNC_STRING] = org.apache.xpath.functions.FuncString.class;

	  m_functions[FUNC_DATA] = org.apache.xpath.functions.FuncData.class;

	  m_functions[FUNC_CONTAINS] = org.apache.xpath.functions.string.FuncContains.class;
	  m_functions[FUNC_STARTS_WITH] = org.apache.xpath.functions.string.FuncStartsWith.class;
	  m_functions[FUNC_ENDS_WITH] = org.apache.xpath.functions.string.FuncEndsWith.class;	  
	  m_functions[FUNC_SUBSTRING_BEFORE] = org.apache.xpath.functions.string.FuncSubstringBefore.class;
	  m_functions[FUNC_SUBSTRING_AFTER] = org.apache.xpath.functions.string.FuncSubstringAfter.class;
	  m_functions[FUNC_NORMALIZE_SPACE] = org.apache.xpath.functions.string.FuncNormalizeSpace.class;
	  m_functions[FUNC_NORMALIZE_UNICODE] = org.apache.xpath.functions.string.FuncNormalizeUnicode.class;
	  m_functions[FUNC_TRANSLATE] = org.apache.xpath.functions.string.FuncTranslate.class;
	  m_functions[FUNC_CONCAT] = org.apache.xpath.functions.string.FuncConcat.class;
	  m_functions[FUNC_SYSTEM_PROPERTY] = org.apache.xpath.functions.FuncSystemProperty.class;
	  m_functions[FUNC_EXT_FUNCTION_AVAILABLE] = org.apache.xpath.functions.FuncExtFunctionAvailable.class;
	  m_functions[FUNC_EXT_ELEM_AVAILABLE] = org.apache.xpath.functions.FuncElemAvailable.class;
	  m_functions[FUNC_SUBSTRING] = org.apache.xpath.functions.string.FuncSubstring.class;
	  m_functions[FUNC_STRING_LENGTH] = org.apache.xpath.functions.string.FuncStringLength.class;
	  m_functions[FUNC_DOCLOCATION] = org.apache.xpath.functions.FuncDoclocation.class;
	  m_functions[FUNC_UNPARSED_ENTITY_URI] = org.apache.xpath.functions.FuncUnparsedEntityURI.class;

	  m_functions[FUNC_MATCHES] = org.apache.xpath.functions.string.FuncMatches.class;
	  m_functions[FUNC_REPLACE] = org.apache.xpath.functions.string.FuncReplace.class;

	  m_functions[FUNC_CURRENT_GROUP] = org.apache.xalan.templates.FuncCurrentGroup.class;
	  m_functions[FUNC_CURRENT_GROUPING_KEY] = org.apache.xalan.templates.FuncCurrentGroupingKey.class;

	  m_functions[FUNC_CURRENT_MERGE_GROUP] = org.apache.xalan.templates.FuncCurrentMergeGroup.class;
	  m_functions[FUNC_CURRENT_MERGE_KEY] = org.apache.xalan.templates.FuncCurrentMergeKey.class;

	  m_functions[FUNC_ABS] = org.apache.xpath.functions.FuncAbs.class;
	  m_functions[FUNC_REGEX_GROUP] = org.apache.xalan.templates.FuncRegexGroup.class;
	  m_functions[FUNC_TOKENIZE] = org.apache.xpath.functions.string.FuncTokenize.class;
	  m_functions[FUNC_ANALYZE_STRING] = org.apache.xpath.functions.string.FuncAnalyzeString.class;
	  m_functions[FUNC_UNPARSED_TEXT] = org.apache.xpath.functions.FuncUnparsedText.class;
	  m_functions[FUNC_UNPARSED_TEXT_LINES] = org.apache.xpath.functions.FuncUnparsedTextLines.class;
	  m_functions[FUNC_COLLECTION] = org.apache.xpath.functions.FuncCollection.class;
	  m_functions[FUNC_COLLECTION] = org.apache.xpath.functions.FuncCollection.class;
	  m_functions[FUNC_STRING_JOIN] = org.apache.xpath.functions.string.FuncStringJoin.class;
	  m_functions[FUNC_CURRENT_DATETIME] = org.apache.xpath.functions.context.FuncCurrentDateTime.class;
	  m_functions[FUNC_CURRENT_DATE] = org.apache.xpath.functions.context.FuncCurrentDate.class;
	  m_functions[FUNC_CURRENT_TIME] = org.apache.xpath.functions.context.FuncCurrentTime.class;
	  m_functions[FUNC_UPPER_CASE] = org.apache.xpath.functions.string.FuncUpperCase.class;
	  m_functions[FUNC_LOWER_CASE] = org.apache.xpath.functions.string.FuncLowerCase.class;
	  m_functions[FUNC_IMPLICIT_TIMEZONE] = org.apache.xpath.functions.context.FuncImplicitTimezone.class;
	  m_functions[FUNC_INDEX_OF] = org.apache.xpath.functions.FuncIndexOf.class;        
	  m_functions[FUNC_DISTINCT_VALUES] = org.apache.xpath.functions.FuncDistinctValues.class;

	  m_functions[FUNC_FOR_EACH] = org.apache.xpath.functions.hof.FuncForEach.class;
	  m_functions[FUNC_FILTER] = org.apache.xpath.functions.hof.FuncFilter.class;
	  m_functions[FUNC_FOLD_LEFT] = org.apache.xpath.functions.hof.FuncFoldLeft.class;
	  m_functions[FUNC_FOLD_RIGHT] = org.apache.xpath.functions.hof.FuncFoldRight.class;
	  m_functions[FUNC_FOR_EACH_PAIR] = org.apache.xpath.functions.hof.FuncForEachPair.class;
	  m_functions[FUNC_SORT] = org.apache.xpath.functions.hof.FuncSort.class;
	  m_functions[FUNC_APPLY] = org.apache.xpath.functions.hof.FuncApply.class;

	  /**
	   * XPath 3.1 built-in functions configurations for the math
	   * functions namespace http://www.w3.org/2005/xpath-functions/math
	   */
	   m_functions[FUNC_MATH_PI] = org.apache.xpath.functions.math.FuncMathPi.class;
	   m_functions[FUNC_MATH_EXP] = org.apache.xpath.functions.math.FuncMathExp.class;
	   m_functions[FUNC_MATH_EXP10] = org.apache.xpath.functions.math.FuncMathExp10.class;
	   m_functions[FUNC_MATH_LOG] = org.apache.xpath.functions.math.FuncMathLog.class;
	   m_functions[FUNC_MATH_LOG10] = org.apache.xpath.functions.math.FuncMathLog10.class;
	   m_functions[FUNC_MATH_POW] = org.apache.xpath.functions.math.FuncMathPow.class;
	   m_functions[FUNC_MATH_SQRT] = org.apache.xpath.functions.math.FuncMathSqrt.class;
	   m_functions[FUNC_MATH_SIN] = org.apache.xpath.functions.math.FuncMathSin.class;
	   m_functions[FUNC_MATH_COS] = org.apache.xpath.functions.math.FuncMathCos.class;
	   m_functions[FUNC_MATH_TAN] = org.apache.xpath.functions.math.FuncMathTan.class;
	   m_functions[FUNC_MATH_ASIN] = org.apache.xpath.functions.math.FuncMathAsin.class;
	   m_functions[FUNC_MATH_ACOS] = org.apache.xpath.functions.math.FuncMathAcos.class;
	   m_functions[FUNC_MATH_ATAN] = org.apache.xpath.functions.math.FuncMathAtan.class;
	   m_functions[FUNC_MATH_ATAN2] = org.apache.xpath.functions.math.FuncMathAtan2.class;

	   m_functions[FUNC_YEARS_FROM_DURATION] = org.apache.xpath.functions.duration.FuncYearsFromDuration.class;
	   m_functions[FUNC_MONTHS_FROM_DURATION] = org.apache.xpath.functions.duration.FuncMonthsFromDuration.class;
	   m_functions[FUNC_DAYS_FROM_DURATION] = org.apache.xpath.functions.duration.FuncDaysFromDuration.class;
	   m_functions[FUNC_HOURS_FROM_DURATION] = org.apache.xpath.functions.duration.FuncHoursFromDuration.class;
	   m_functions[FUNC_MINUTES_FROM_DURATION] = org.apache.xpath.functions.duration.FuncMinutesFromDuration.class;
	   m_functions[FUNC_SECONDS_FROM_DURATION] = org.apache.xpath.functions.duration.FuncSecondsFromDuration.class;

	   m_functions[FUNC_CODE_POINTS_TO_STRING] = org.apache.xpath.functions.string.FuncCodePointsToString.class;
	   m_functions[FUNC_STRING_TO_CODE_POINTS] = org.apache.xpath.functions.string.FuncStringToCodepoints.class;
	   m_functions[FUNC_COMPARE] = org.apache.xpath.functions.string.FuncCompare.class;
	   m_functions[FUNC_CODEPOINT_EQUAL] = org.apache.xpath.functions.string.FuncCodepointEqual.class;
	   m_functions[FUNC_CONTAINS_TOKEN] = org.apache.xpath.functions.string.FuncContainsToken.class;

	   m_functions[FUNC_EMPTY] = org.apache.xpath.functions.FuncEmpty.class;
	   m_functions[FUNC_EXISTS] = org.apache.xpath.functions.FuncExists.class;
	   m_functions[FUNC_HEAD] = org.apache.xpath.functions.FuncHead.class;
	   m_functions[FUNC_TAIL] = org.apache.xpath.functions.FuncTail.class;
	   m_functions[FUNC_INSERT_BEFORE] = org.apache.xpath.functions.FuncInsertBefore.class;
	   m_functions[FUNC_REMOVE] = org.apache.xpath.functions.FuncRemove.class;
	   m_functions[FUNC_REVERSE] = org.apache.xpath.functions.FuncReverse.class;
	   m_functions[FUNC_SUBSEQUENCE] = org.apache.xpath.functions.FuncSubsequence.class;
	   m_functions[FUNC_UNORDERED] = org.apache.xpath.functions.FuncUnordered.class;

	   m_functions[FUNC_PARSE_XML] = org.apache.xpath.functions.FuncParseXml.class;
	   m_functions[FUNC_PARSE_XML_FRAGMENT] = org.apache.xpath.functions.FuncParseXmlFragment.class;
	   m_functions[FUNC_SERIALIZE] = org.apache.xpath.functions.FuncSerialize.class;

	   m_functions[FUNC_AVG] = org.apache.xpath.functions.FuncAvg.class;
	   m_functions[FUNC_MAX] = org.apache.xpath.functions.FuncMax.class;
	   m_functions[FUNC_MIN] = org.apache.xpath.functions.FuncMin.class;

	   m_functions[FUNC_DOC] = org.apache.xpath.functions.FuncDoc.class;
	   m_functions[FUNC_DOC_AVAILABLE] = org.apache.xpath.functions.FuncDocAvailable.class;

	   m_functions[FUNC_NODE_NAME] = org.apache.xpath.functions.FuncNodeName.class;
	   m_functions[FUNC_DEEP_EQUAL] = org.apache.xpath.functions.FuncDeepEqual.class;

	   m_functions[FUNC_DATE_TIME] = org.apache.xpath.functions.datetime.FuncDateTime.class;
	   m_functions[FUNC_YEAR_FROM_DATE_TIME] = org.apache.xpath.functions.datetime.FuncYearFromDateTime.class;
	   m_functions[FUNC_MONTH_FROM_DATE_TIME] = org.apache.xpath.functions.datetime.FuncMonthFromDateTime.class;
	   m_functions[FUNC_DAY_FROM_DATE_TIME] = org.apache.xpath.functions.datetime.FuncDayFromDateTime.class;
	   m_functions[FUNC_HOURS_FROM_DATE_TIME] = org.apache.xpath.functions.datetime.FuncHoursFromDateTime.class;
	   m_functions[FUNC_MINUTES_FROM_DATE_TIME] = org.apache.xpath.functions.datetime.FuncMinutesFromDateTime.class;
	   m_functions[FUNC_SECONDS_FROM_DATE_TIME] = org.apache.xpath.functions.datetime.FuncSecondsFromDateTime.class;
	   m_functions[FUNC_TIMEZONE_FROM_DATE_TIME] = org.apache.xpath.functions.datetime.FuncTimezoneFromDateTime.class;
	   m_functions[FUNC_YEAR_FROM_DATE] = org.apache.xpath.functions.datetime.FuncYearFromDate.class;
	   m_functions[FUNC_MONTH_FROM_DATE] = org.apache.xpath.functions.datetime.FuncMonthFromDate.class;
	   m_functions[FUNC_DAY_FROM_DATE] = org.apache.xpath.functions.datetime.FuncDayFromDate.class;
	   m_functions[FUNC_TIMEZONE_FROM_DATE] = org.apache.xpath.functions.datetime.FuncTimezoneFromDate.class;
	   m_functions[FUNC_HOURS_FROM_TIME] = org.apache.xpath.functions.datetime.FuncHoursFromTime.class;
	   m_functions[FUNC_MINUTES_FROM_TIME] = org.apache.xpath.functions.datetime.FuncMinutesFromTime.class;
	   m_functions[FUNC_SECONDS_FROM_TIME] = org.apache.xpath.functions.datetime.FuncSecondsFromTime.class;
	   m_functions[FUNC_TIMEZONE_FROM_TIME] = org.apache.xpath.functions.datetime.FuncTimezoneFromTime.class;
	   
	   m_functions[FUNC_ADJUST_DATETIME_TO_TIMEZONE] = org.apache.xpath.functions.datetime.FuncAdjustDateTimeToTimezone.class;
	   m_functions[FUNC_ADJUST_DATE_TO_TIMEZONE] = org.apache.xpath.functions.datetime.FuncAdjustDateToTimezone.class;
	   m_functions[FUNC_ADJUST_TIME_TO_TIMEZONE] = org.apache.xpath.functions.datetime.FuncAdjustTimeToTimezone.class;
	   
	   m_functions[FUNC_FORMAT_DATETIME] = org.apache.xpath.functions.datetime.FuncFormatDateTime.class;
	   m_functions[FUNC_FORMAT_DATE] = org.apache.xpath.functions.datetime.FuncFormatDate.class;
	   m_functions[FUNC_FORMAT_TIME] = org.apache.xpath.functions.datetime.FuncFormatTime.class;
	   
	   m_functions[FUNC_PARSE_IETF_DATE] = org.apache.xpath.functions.datetime.FuncParseIetfDate.class;
	   
	   m_functions[FUNC_FORMAT_INTEGER] = org.apache.xpath.functions.FuncFormatInteger.class;

	   m_functions[FUNC_DEFAULT_COLLATION] = org.apache.xpath.functions.context.FuncDefaultCollation.class;
	   m_functions[FUNC_BASE_URI] = org.apache.xpath.functions.FuncBaseUri.class;
	   m_functions[FUNC_DOCUMENT_URI] = org.apache.xpath.functions.FuncDocumentUri.class;        

	   /**
	    * XPath 3.1 built-in functions configurations for the map
	    * functions namespace http://www.w3.org/2005/xpath-functions/map
	    */
	   m_functions[FUNC_MAP_MERGE] = org.apache.xpath.functions.map.FuncMapMerge.class;
	   m_functions[FUNC_MAP_SIZE] = org.apache.xpath.functions.map.FuncMapSize.class;
	   m_functions[FUNC_MAP_KEYS] = org.apache.xpath.functions.map.FuncMapKeys.class;
	   m_functions[FUNC_MAP_CONTAINS] = org.apache.xpath.functions.map.FuncMapContains.class;
	   m_functions[FUNC_MAP_GET] = org.apache.xpath.functions.map.FuncMapGet.class;
	   m_functions[FUNC_MAP_PUT] = org.apache.xpath.functions.map.FuncMapPut.class;
	   m_functions[FUNC_MAP_ENTRY] = org.apache.xpath.functions.map.FuncMapEntry.class;
	   m_functions[FUNC_MAP_FOREACH] = org.apache.xpath.functions.map.FuncMapForEach.class;
	   m_functions[FUNC_MAP_REMOVE] = org.apache.xpath.functions.map.FuncMapRemove.class;
	   m_functions[FUNC_MAP_FIND] = org.apache.xpath.functions.map.FuncMapFind.class;

	   /**
	    * XPath 3.1 built-in functions configurations for the array
	    * functions namespace http://www.w3.org/2005/xpath-functions/array
	    */
	   m_functions[FUNC_ARRAY_SIZE] = org.apache.xpath.functions.array.FuncArraySize.class;
	   m_functions[FUNC_ARRAY_GET] = org.apache.xpath.functions.array.FuncArrayGet.class;
	   m_functions[FUNC_ARRAY_PUT] = org.apache.xpath.functions.array.FuncArrayPut.class;
	   m_functions[FUNC_ARRAY_APPEND] = org.apache.xpath.functions.array.FuncArrayAppend.class;
	   m_functions[FUNC_ARRAY_SUBARRAY] = org.apache.xpath.functions.array.FuncSubarray.class;
	   m_functions[FUNC_ARRAY_REMOVE] = org.apache.xpath.functions.array.FuncArrayRemove.class;
	   m_functions[FUNC_ARRAY_INSERT_BEFORE] = org.apache.xpath.functions.array.FuncArrayInsertBefore.class;
	   m_functions[FUNC_ARRAY_HEAD] = org.apache.xpath.functions.array.FuncArrayHead.class;
	   m_functions[FUNC_ARRAY_TAIL] = org.apache.xpath.functions.array.FuncArrayTail.class;
	   m_functions[FUNC_ARRAY_REVERSE] = org.apache.xpath.functions.array.FuncArrayReverse.class;
	   m_functions[FUNC_ARRAY_JOIN] = org.apache.xpath.functions.array.FuncArrayJoin.class;
	   m_functions[FUNC_ARRAY_FOR_EACH] = org.apache.xpath.functions.array.FuncArrayForEach.class;
	   m_functions[FUNC_ARRAY_FILTER] = org.apache.xpath.functions.array.FuncArrayFilter.class;
	   m_functions[FUNC_ARRAY_FOR_EACH_PAIR] = org.apache.xpath.functions.array.FuncArrayForEachPair.class;
	   m_functions[FUNC_ARRAY_FOLD_LEFT] = org.apache.xpath.functions.array.FuncArrayFoldLeft.class;
	   m_functions[FUNC_ARRAY_FOLD_RIGHT] = org.apache.xpath.functions.array.FuncArrayFoldRight.class;
	   m_functions[FUNC_ARRAY_SORT] = org.apache.xpath.functions.array.FuncArraySort.class;
	   m_functions[FUNC_ARRAY_FLATTEN] = org.apache.xpath.functions.array.FuncArrayFlatten.class;
	   
	   m_functions[FUNC_TRANSFORM] = org.apache.xpath.functions.FuncTransform.class;

	   m_functions[FUNC_PARSE_JSON] = org.apache.xpath.functions.json.FuncParseJson.class;
	   m_functions[FUNC_JSON_DOC] = org.apache.xpath.functions.json.FuncJsonDoc.class;
	   m_functions[FUNC_JSON_TO_XML] = org.apache.xpath.functions.json.FuncJsonToXml.class;
	   m_functions[FUNC_XML_TO_JSON] = org.apache.xpath.functions.json.FuncXmlToJson.class;
	   
	   m_functions[FUNC_FUNCTION_ARITY] = org.apache.xpath.functions.FuncFunctionArity.class;
	   m_functions[FUNC_FUNCTION_NAME] = org.apache.xpath.functions.FuncFunctionName.class;
	   
	   m_functions[FUNC_ERROR] = org.apache.xpath.functions.FuncError.class;
	   
	   m_functions[FUNC_RANDOM_NUMBER_GENERATOR] = org.apache.xpath.functions.FuncRandomNumberGenerator.class;
	   
	   m_functions[FUNC_ENVIRONMENT_VARIABLE] = org.apache.xpath.functions.FuncEnvironmentVariable.class;
	   m_functions[FUNC_AVAILABLE_ENVIRONMENT_VARIABLES] = org.apache.xpath.functions.FuncAvailableEnvironmentVariables.class;
	   
	   m_functions[FUNC_UNPARSED_TEXT_AVAILABLE] = org.apache.xpath.functions.FuncUnparsedTextAvailable.class;
	   
	   m_functions[FUNC_COPY_OF] = org.apache.xalan.templates.FuncCopyOf.class;
	   
	   m_functions[FUNC_PERIOD] = org.apache.xpath.functions.FuncPeriod.class;
	   
	   m_functions[FUNC_ZERO_OR_ONE] = org.apache.xpath.functions.FuncZeroOrOne.class;
	   m_functions[FUNC_ONE_OR_MORE] = org.apache.xpath.functions.FuncOneOrMore.class;
	   m_functions[FUNC_EXACTLY_ONE] = org.apache.xpath.functions.FuncExactlyOne.class;
	   
	   m_functions[FUNC_PATH] = org.apache.xpath.functions.FuncPath.class;
	   
	   m_functions[FUNC_DOCUMENT] = org.apache.xalan.templates.FuncDocument.class;
	   m_functions[FUNC_FORMAT_NUMBER] = org.apache.xalan.templates.FuncFormatNumber.class;
  }

  static {
	  m_functionId.put(Integer.valueOf(FUNC_CURRENT), Keywords.FUNC_CURRENT_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_LAST), Keywords.FUNC_LAST_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_POSITION), Keywords.FUNC_POSITION_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_COUNT), Keywords.FUNC_COUNT_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_ID), Keywords.FUNC_ID_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_KEY), Keywords.FUNC_KEY_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_LOCAL_NAME), Keywords.FUNC_LOCAL_NAME_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_NAMESPACE_URI), Keywords.FUNC_NAMESPACE_URI_STRING);          
	  m_functionId.put(Integer.valueOf(FUNC_GENERATE_ID), Keywords.FUNC_GENERATE_ID_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_NOT), Keywords.FUNC_NOT_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_TRUE), Keywords.FUNC_TRUE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_FALSE), Keywords.FUNC_FALSE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_BOOLEAN), Keywords.FUNC_BOOLEAN_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_LANG), Keywords.FUNC_LANG_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_ROOT), Keywords.FUNC_ROOT_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_NUMBER), Keywords.FUNC_NUMBER_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_FLOOR), Keywords.FUNC_FLOOR_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_CEILING), Keywords.FUNC_CEILING_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_ROUND), Keywords.FUNC_ROUND_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_ROUND_HALF_TO_EVEN), Keywords.FUNC_ROUND_HALF_TO_EVEN_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_SUM), Keywords.FUNC_SUM_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_STRING), Keywords.FUNC_STRING_STRING);

	  m_functionId.put(Integer.valueOf(FUNC_DATA), Keywords.FUNC_DATA_STRING);

	  m_functionId.put(Integer.valueOf(FUNC_CONTAINS), Keywords.FUNC_CONTAINS_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_STARTS_WITH), Keywords.FUNC_STARTS_WITH_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_ENDS_WITH), Keywords.FUNC_ENDS_WITH_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_SUBSTRING_BEFORE), Keywords.FUNC_SUBSTRING_BEFORE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_SUBSTRING_AFTER), Keywords.FUNC_SUBSTRING_AFTER_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_NORMALIZE_SPACE), Keywords.FUNC_NORMALIZE_SPACE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_NORMALIZE_UNICODE), Keywords.FUNC_NORMALIZE_UNICODE);
	  m_functionId.put(Integer.valueOf(FUNC_TRANSLATE), Keywords.FUNC_TRANSLATE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_CONCAT), Keywords.FUNC_CONCAT_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_SYSTEM_PROPERTY), Keywords.FUNC_SYSTEM_PROPERTY_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_EXT_FUNCTION_AVAILABLE), Keywords.FUNC_EXT_FUNCTION_AVAILABLE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_EXT_ELEM_AVAILABLE), Keywords.FUNC_ELEM_AVAILABLE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_SUBSTRING), Keywords.FUNC_SUBSTRING_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_STRING_LENGTH), Keywords.FUNC_STRING_LENGTH_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_UNPARSED_ENTITY_URI), Keywords.FUNC_UNPARSED_ENTITY_URI_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_MATCHES), Keywords.FUNC_MATCHES_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_REPLACE), Keywords.FUNC_REPLACE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_DOCLOCATION), Keywords.FUNC_DOCLOCATION_STRING);

	  m_functionId.put(Integer.valueOf(FUNC_CURRENT_GROUP), Keywords.FUNC_CURRENT_GROUP);
	  m_functionId.put(Integer.valueOf(FUNC_CURRENT_GROUPING_KEY), Keywords.FUNC_CURRENT_GROUPING_KEY);

	  m_functionId.put(Integer.valueOf(FUNC_CURRENT_MERGE_GROUP), Keywords.FUNC_CURRENT_MERGE_GROUP);
	  m_functionId.put(Integer.valueOf(FUNC_CURRENT_MERGE_KEY), Keywords.FUNC_CURRENT_MERGE_KEY);

	  m_functionId.put(Integer.valueOf(FUNC_ABS), Keywords.FUNC_ABS);
	  m_functionId.put(Integer.valueOf(FUNC_REGEX_GROUP), Keywords.FUNC_REGEX_GROUP);
	  m_functionId.put(Integer.valueOf(FUNC_TOKENIZE), Keywords.FUNC_TOKENIZE);
	  m_functionId.put(Integer.valueOf(FUNC_ANALYZE_STRING), Keywords.FUNC_ANALYZE_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_UNPARSED_TEXT), Keywords.FUNC_UNPARSED_TEXT);
	  m_functionId.put(Integer.valueOf(FUNC_UNPARSED_TEXT_LINES), Keywords.FUNC_UNPARSED_TEXT_LINES);
	  m_functionId.put(Integer.valueOf(FUNC_COLLECTION), Keywords.FUNC_COLLECTION);
	  m_functionId.put(Integer.valueOf(FUNC_STRING_JOIN), Keywords.FUNC_STRING_JOIN);
	  m_functionId.put(Integer.valueOf(FUNC_CURRENT_DATETIME), Keywords.FUNC_CURRENT_DATETIME);
	  m_functionId.put(Integer.valueOf(FUNC_CURRENT_DATE), Keywords.FUNC_CURRENT_DATE);
	  m_functionId.put(Integer.valueOf(FUNC_CURRENT_TIME), Keywords.FUNC_CURRENT_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_UPPER_CASE), Keywords.FUNC_UPPER_CASE);
	  m_functionId.put(Integer.valueOf(FUNC_LOWER_CASE), Keywords.FUNC_LOWER_CASE);
	  m_functionId.put(Integer.valueOf(FUNC_IMPLICIT_TIMEZONE), Keywords.FUNC_IMPLICIT_TIMEZONE);
	  m_functionId.put(Integer.valueOf(FUNC_INDEX_OF), Keywords.FUNC_INDEX_OF);          
	  m_functionId.put(Integer.valueOf(FUNC_DISTINCT_VALUES), Keywords.FUNC_DISTINCT_VALUES);

	  m_functionId.put(Integer.valueOf(FUNC_FOR_EACH), Keywords.FUNC_FOR_EACH);
	  m_functionId.put(Integer.valueOf(FUNC_FILTER), Keywords.FUNC_FILTER);
	  m_functionId.put(Integer.valueOf(FUNC_FOLD_LEFT), Keywords.FUNC_FOLD_LEFT);
	  m_functionId.put(Integer.valueOf(FUNC_FOLD_RIGHT), Keywords.FUNC_FOLD_RIGHT);
	  m_functionId.put(Integer.valueOf(FUNC_FOR_EACH_PAIR), Keywords.FUNC_FOR_EACH_PAIR);
	  m_functionId.put(Integer.valueOf(FUNC_SORT), Keywords.FUNC_SORT);
	  m_functionId.put(Integer.valueOf(FUNC_APPLY), Keywords.FUNC_APPLY);

	  /**
	   * XPath 3.1 functions configurations for the math functions
	   * namespace http://www.w3.org/2005/xpath-functions/math
	   */
	  m_functionId.put(Integer.valueOf(FUNC_MATH_PI), Keywords.FUNC_MATH_PI);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_EXP), Keywords.FUNC_MATH_EXP);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_EXP10), Keywords.FUNC_MATH_EXP10);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_LOG), Keywords.FUNC_MATH_LOG);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_LOG10), Keywords.FUNC_MATH_LOG10);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_POW), Keywords.FUNC_MATH_POW);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_SQRT), Keywords.FUNC_MATH_SQRT);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_SIN), Keywords.FUNC_MATH_SIN);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_COS), Keywords.FUNC_MATH_COS);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_TAN), Keywords.FUNC_MATH_TAN);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_ASIN), Keywords.FUNC_MATH_ASIN);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_ACOS), Keywords.FUNC_MATH_ACOS);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_ATAN), Keywords.FUNC_MATH_ATAN);
	  m_functionId.put(Integer.valueOf(FUNC_MATH_ATAN2), Keywords.FUNC_MATH_ATAN2);

	  m_functionId.put(Integer.valueOf(FUNC_YEARS_FROM_DURATION), Keywords.FUNC_YEARS_FROM_DURATION);
	  m_functionId.put(Integer.valueOf(FUNC_MONTHS_FROM_DURATION), Keywords.FUNC_MONTHS_FROM_DURATION);
	  m_functionId.put(Integer.valueOf(FUNC_DAYS_FROM_DURATION), Keywords.FUNC_DAYS_FROM_DURATION);
	  m_functionId.put(Integer.valueOf(FUNC_HOURS_FROM_DURATION), Keywords.FUNC_HOURS_FROM_DURATION);
	  m_functionId.put(Integer.valueOf(FUNC_MINUTES_FROM_DURATION), Keywords.FUNC_MINUTES_FROM_DURATION);
	  m_functionId.put(Integer.valueOf(FUNC_SECONDS_FROM_DURATION), Keywords.FUNC_SECONDS_FROM_DURATION);

	  m_functionId.put(Integer.valueOf(FUNC_CODE_POINTS_TO_STRING), Keywords.FUNC_CODE_POINTS_TO_STRING);
	  m_functionId.put(Integer.valueOf(FUNC_STRING_TO_CODE_POINTS), Keywords.FUNC_STRING_TO_CODE_POINTS);
	  m_functionId.put(Integer.valueOf(FUNC_COMPARE), Keywords.FUNC_COMPARE);
	  m_functionId.put(Integer.valueOf(FUNC_CODEPOINT_EQUAL), Keywords.FUNC_CODEPOINT_EQUAL);
	  m_functionId.put(Integer.valueOf(FUNC_CONTAINS_TOKEN), Keywords.FUNC_CONTAINS_TOKEN);

	  m_functionId.put(Integer.valueOf(FUNC_EMPTY), Keywords.FUNC_EMPTY);
	  m_functionId.put(Integer.valueOf(FUNC_EXISTS), Keywords.FUNC_EXISTS);
	  m_functionId.put(Integer.valueOf(FUNC_HEAD), Keywords.FUNC_HEAD);
	  m_functionId.put(Integer.valueOf(FUNC_TAIL), Keywords.FUNC_TAIL);
	  m_functionId.put(Integer.valueOf(FUNC_INSERT_BEFORE), Keywords.FUNC_INSERT_BEFORE);
	  m_functionId.put(Integer.valueOf(FUNC_REMOVE), Keywords.FUNC_REMOVE);
	  m_functionId.put(Integer.valueOf(FUNC_REVERSE), Keywords.FUNC_REVERSE);
	  m_functionId.put(Integer.valueOf(FUNC_SUBSEQUENCE), Keywords.FUNC_SUBSEQUENCE);
	  m_functionId.put(Integer.valueOf(FUNC_UNORDERED), Keywords.FUNC_UNORDERED);

	  m_functionId.put(Integer.valueOf(FUNC_PARSE_XML), Keywords.FUNC_PARSE_XML);
	  m_functionId.put(Integer.valueOf(FUNC_PARSE_XML_FRAGMENT), Keywords.FUNC_PARSE_XML_FRAGMENT);
	  m_functionId.put(Integer.valueOf(FUNC_SERIALIZE), Keywords.FUNC_SERIALIZE);

	  m_functionId.put(Integer.valueOf(FUNC_AVG), Keywords.FUNC_AVG);
	  m_functionId.put(Integer.valueOf(FUNC_MAX), Keywords.FUNC_MAX);
	  m_functionId.put(Integer.valueOf(FUNC_MIN), Keywords.FUNC_MIN);

	  m_functionId.put(Integer.valueOf(FUNC_DOC), Keywords.FUNC_DOC);
	  m_functionId.put(Integer.valueOf(FUNC_DOC_AVAILABLE), Keywords.FUNC_DOC_AVAILABLE);

	  m_functionId.put(Integer.valueOf(FUNC_NODE_NAME), Keywords.FUNC_NODE_NAME);
	  m_functionId.put(Integer.valueOf(FUNC_DEEP_EQUAL), Keywords.FUNC_DEEP_EQUAL);

	  m_functionId.put(Integer.valueOf(FUNC_DATE_TIME), Keywords.FUNC_DATE_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_YEAR_FROM_DATE_TIME), Keywords.FUNC_YEAR_FROM_DATE_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_MONTH_FROM_DATE_TIME), Keywords.FUNC_MONTH_FROM_DATE_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_DAY_FROM_DATE_TIME), Keywords.FUNC_DAY_FROM_DATE_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_HOURS_FROM_DATE_TIME), Keywords.FUNC_HOURS_FROM_DATE_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_MINUTES_FROM_DATE_TIME), Keywords.FUNC_MINUTES_FROM_DATE_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_SECONDS_FROM_DATE_TIME), Keywords.FUNC_SECONDS_FROM_DATE_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_TIMEZONE_FROM_DATE_TIME), Keywords.FUNC_TIMEZONE_FROM_DATE_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_YEAR_FROM_DATE), Keywords.FUNC_YEAR_FROM_DATE);
	  m_functionId.put(Integer.valueOf(FUNC_MONTH_FROM_DATE), Keywords.FUNC_MONTH_FROM_DATE);
	  m_functionId.put(Integer.valueOf(FUNC_DAY_FROM_DATE), Keywords.FUNC_DAY_FROM_DATE);
	  m_functionId.put(Integer.valueOf(FUNC_TIMEZONE_FROM_DATE), Keywords.FUNC_TIMEZONE_FROM_DATE);
	  m_functionId.put(Integer.valueOf(FUNC_HOURS_FROM_TIME), Keywords.FUNC_HOURS_FROM_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_MINUTES_FROM_TIME), Keywords.FUNC_MINUTES_FROM_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_SECONDS_FROM_TIME), Keywords.FUNC_SECONDS_FROM_TIME);
	  m_functionId.put(Integer.valueOf(FUNC_TIMEZONE_FROM_TIME), Keywords.FUNC_TIMEZONE_FROM_TIME);
	  
	  m_functionId.put(Integer.valueOf(FUNC_ADJUST_DATETIME_TO_TIMEZONE), Keywords.FUNC_ADJUST_DATETIME_TO_TIMEZONE);
	  m_functionId.put(Integer.valueOf(FUNC_ADJUST_DATE_TO_TIMEZONE), Keywords.FUNC_ADJUST_DATE_TO_TIMEZONE);
	  m_functionId.put(Integer.valueOf(FUNC_ADJUST_TIME_TO_TIMEZONE), Keywords.FUNC_ADJUST_TIME_TO_TIMEZONE);
	  
	  m_functionId.put(Integer.valueOf(FUNC_FORMAT_DATETIME), Keywords.FUNC_FORMAT_DATETIME);
	  m_functionId.put(Integer.valueOf(FUNC_FORMAT_DATE), Keywords.FUNC_FORMAT_DATE);
	  m_functionId.put(Integer.valueOf(FUNC_FORMAT_TIME), Keywords.FUNC_FORMAT_TIME);
	  
	  m_functionId.put(Integer.valueOf(FUNC_PARSE_IETF_DATE), Keywords.FUNC_PARSE_IETF_DATE);
	  
	  m_functionId.put(Integer.valueOf(FUNC_FORMAT_INTEGER), Keywords.FUNC_FORMAT_INTEGER);

	  m_functionId.put(Integer.valueOf(FUNC_DEFAULT_COLLATION), Keywords.FUNC_DEFAULT_COLLATION);
	  m_functionId.put(Integer.valueOf(FUNC_BASE_URI), Keywords.FUNC_BASE_URI);
	  m_functionId.put(Integer.valueOf(FUNC_DOCUMENT_URI), Keywords.FUNC_DOCUMENT_URI);

	  /**
	   * XPath 3.1 functions configurations for the map functions
	   * namespace http://www.w3.org/2005/xpath-functions/map
	   */
	  m_functionId.put(Integer.valueOf(FUNC_MAP_MERGE), Keywords.FUNC_MAP_MERGE);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_SIZE), Keywords.FUNC_MAP_SIZE);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_KEYS), Keywords.FUNC_MAP_KEYS);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_CONTAINS), Keywords.FUNC_MAP_CONTAINS);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_GET), Keywords.FUNC_MAP_GET);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_FIND), Keywords.FUNC_MAP_FIND);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_PUT), Keywords.FUNC_MAP_PUT);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_ENTRY), Keywords.FUNC_MAP_ENTRY);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_REMOVE), Keywords.FUNC_MAP_REMOVE);
	  m_functionId.put(Integer.valueOf(FUNC_MAP_FOREACH), Keywords.FUNC_MAP_FOREACH);	  	  

	  /**
	   * XPath 3.1 functions configurations for the array functions
	   * namespace http://www.w3.org/2005/xpath-functions/array
	   */
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_SIZE), Keywords.FUNC_ARRAY_SIZE);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_GET), Keywords.FUNC_ARRAY_GET);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_PUT), Keywords.FUNC_ARRAY_PUT);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_APPEND), Keywords.FUNC_ARRAY_APPEND);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_SUBARRAY), Keywords.FUNC_ARRAY_SUBARRAY);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_REMOVE), Keywords.FUNC_ARRAY_REMOVE);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_INSERT_BEFORE), Keywords.FUNC_ARRAY_INSERT_BEFORE);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_HEAD), Keywords.FUNC_ARRAY_HEAD);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_TAIL), Keywords.FUNC_ARRAY_TAIL);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_REVERSE), Keywords.FUNC_ARRAY_REVERSE);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_JOIN), Keywords.FUNC_ARRAY_JOIN);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_FOR_EACH), Keywords.FUNC_ARRAY_FOR_EACH);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_FILTER), Keywords.FUNC_ARRAY_FILTER);	  
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_FOLD_LEFT), Keywords.FUNC_ARRAY_FOLD_LEFT);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_FOLD_RIGHT), Keywords.FUNC_ARRAY_FOLD_RIGHT);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_FOR_EACH_PAIR), Keywords.FUNC_ARRAY_FOR_EACH_PAIR);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_SORT), Keywords.FUNC_ARRAY_SORT);
	  m_functionId.put(Integer.valueOf(FUNC_ARRAY_FLATTEN), Keywords.FUNC_ARRAY_FLATTEN);

	  m_functionId.put(Integer.valueOf(FUNC_NAME), Keywords.FUNC_NAME_STRING);

	  m_functionId.put(Integer.valueOf(FUNC_RESOLVE_QNAME), Keywords.FUNC_RESOLVE_QNAME);
	  m_functionId.put(Integer.valueOf(FUNC_QNAME), Keywords.FUNC_QNAME);
	  m_functionId.put(Integer.valueOf(FUNC_PREFIX_FROM_QNAME), Keywords.FUNC_PREFIX_FROM_QNAME);
	  m_functionId.put(Integer.valueOf(FUNC_LOCAL_NAME_FROM_QNAME), Keywords.FUNC_LOCAL_NAME_FROM_QNAME);
	  m_functionId.put(Integer.valueOf(FUNC_NAMESPACE_URI_FROM_QNAME), Keywords.FUNC_NAMESPACE_URI_FROM_QNAME);
	  m_functionId.put(Integer.valueOf(FUNC_NAMESPACE_URI_FOR_PREFIX), Keywords.FUNC_NAMESPACE_URI_FOR_PREFIX);
	  m_functionId.put(Integer.valueOf(FUNC_IN_SCOPE_PREFIXES), Keywords.FUNC_IN_SCOPE_PREFIXES);
	  
	  m_functionId.put(Integer.valueOf(FUNC_TRANSFORM), Keywords.FUNC_TRANSFORM);

	  m_functionId.put(Integer.valueOf(FUNC_PARSE_JSON), Keywords.FUNC_PARSE_JSON);
	  m_functionId.put(Integer.valueOf(FUNC_JSON_DOC), Keywords.FUNC_JSON_DOC);
	  m_functionId.put(Integer.valueOf(FUNC_JSON_TO_XML), Keywords.FUNC_JSON_TO_XML);
	  m_functionId.put(Integer.valueOf(FUNC_XML_TO_JSON), Keywords.FUNC_XML_TO_JSON);
	  
	  m_functionId.put(Integer.valueOf(FUNC_FUNCTION_ARITY), Keywords.FUNC_FUNCTION_ARITY);
	  m_functionId.put(Integer.valueOf(FUNC_FUNCTION_NAME), Keywords.FUNC_FUNCTION_NAME);
	  
	  m_functionId.put(Integer.valueOf(FUNC_ERROR), Keywords.FUNC_ERROR);
	  
	  m_functionId.put(Integer.valueOf(FUNC_RANDOM_NUMBER_GENERATOR), Keywords.FUNC_RANDOM_NUMBER_GENERATOR);
	  
	  m_functionId.put(Integer.valueOf(FUNC_ENVIRONMENT_VARIABLE), Keywords.FUNC_ENVIRONMENT_VARIABLE);
	  m_functionId.put(Integer.valueOf(FUNC_AVAILABLE_ENVIRONMENT_VARIABLES), Keywords.FUNC_AVAILABLE_ENVIRONMENT_VARIABLES);
	  
	  m_functionId.put(Integer.valueOf(FUNC_UNPARSED_TEXT_AVAILABLE), Keywords.FUNC_UNPARSED_TEXT_AVAILABLE);
	  
	  m_functionId.put(Integer.valueOf(FUNC_COPY_OF), Keywords.FUNC_COPY_OF);
	  
	  m_functionId.put(Integer.valueOf(FUNC_PERIOD), Keywords.FROM_SELF_ABBREVIATED_STRING);
	  
	  m_functionId.put(Integer.valueOf(FUNC_ZERO_OR_ONE), Keywords.FUNC_ZERO_OR_ONE);
	  m_functionId.put(Integer.valueOf(FUNC_ONE_OR_MORE), Keywords.FUNC_ONE_OR_MORE);
	  m_functionId.put(Integer.valueOf(FUNC_EXACTLY_ONE), Keywords.FUNC_EXACTLY_ONE);
	  
	  m_functionId.put(Integer.valueOf(FUNC_PATH), Keywords.FUNC_PATH);
	  
	  m_functionId.put(Integer.valueOf(FUNC_DOCUMENT), Keywords.FUNC_DOCUMENT);
	  m_functionId.put(Integer.valueOf(FUNC_FORMAT_NUMBER), Keywords.FUNC_FORMAT_NUMBER);
  }
  
  /**
   * Class constructor.
   */
  public FunctionTable() {
	  // no op
  }
  
  /**
   * Return an XPath function's name in the static table. Needed to avoid
   * making the table publicly available.
   */
  public String getFunctionName(int funcID) {
	  
	  String result = null;

	  if (funcID < NUM_BUILT_IN_FUNCS) {		  		  
		  Set<Entry<Integer, String>> entrySet = m_functionId.entrySet();
		  Iterator<Entry<Integer, String>> iter1 = entrySet.iterator();
		  while (iter1.hasNext()) {
			 Entry<Integer, String> entry = iter1.next();
			 Integer funcId = entry.getKey();
			 String funcName = entry.getValue();
			 if (funcId.intValue() == funcID) {
				 result = funcName;
				 
				 break;
			 }
		  }
	  }

	  if (result == null) {
		  result = (m_functions_customer[funcID - NUM_BUILT_IN_FUNCS]).getName();  
	  }

	  return result;
  }

  /**
   * Obtain a new Function object from a function ID.
   *
   * @param which  The function ID, which may correspond to one of the FUNC_XXX 
   *    values found in {@link org.apache.xpath.compiler.FunctionTable}, but may 
   *    be a value installed by an external module. 
   *
   * @return a a new Function instance.
   *
   * @throws javax.xml.transform.TransformerException if ClassNotFoundException, 
   *    IllegalAccessException, or InstantiationException is thrown.
   */
  public Function getFunction(int which)
          throws javax.xml.transform.TransformerException
  {
	  try {
		  if (which < NUM_BUILT_IN_FUNCS) {			  			  
			  return (Function) m_functions[which].newInstance();
		  }
		  else
			  return (Function) m_functions_customer[which-NUM_BUILT_IN_FUNCS].newInstance();                  
	  } 
	  catch (IllegalAccessException ex){
		  throw new TransformerException(ex.getMessage());
	  } 
	  catch (InstantiationException ex){
		  throw new TransformerException(ex.getMessage());
	  }
  }
  
  /**
   * Method definition, to get function id from the supplied function 
   * name, for XPath 3.1 & XSLT 3 built-in functions from namespace 
   * http://www.w3.org/2005/xpath-functions. 
   * 
   * @param localName                  The supplied string value, for
   *                                   an XSL function local name.
   * @return                           Function id, for an XSL built-in
   *                                   function.
   */
  public Object getFunctionIdForXSLBuiltinFuncs(String localName) {
	  
	    Object id = null;
		
		switch (localName) {
		      case "abs":
			     id = FUNC_ABS;			     			     
			     break;
		      case "adjust-dateTime-to-timezone":
			     id = FUNC_ADJUST_DATETIME_TO_TIMEZONE;			     
			     break;		      
		      case "adjust-date-to-timezone":
			     id = FUNC_ADJUST_DATE_TO_TIMEZONE;
			     break;
		      case "adjust-time-to-timezone":
				 id = FUNC_ADJUST_TIME_TO_TIMEZONE;
				 break;
		      case "analyze-string":
			     id = FUNC_ANALYZE_STRING;
			     break;
		      case "apply":
				 id = FUNC_APPLY;
				 break;
		      case "available-environment-variables":
				 id = FUNC_AVAILABLE_ENVIRONMENT_VARIABLES;
				 break;
		      case "avg":
				 id = FUNC_AVG;
				 break;
		      case "base-uri":
				 id = FUNC_BASE_URI;
				 break;
		      case "boolean":
				 id = FUNC_BOOLEAN;
				 break;
		      case "ceiling":
			     id = FUNC_CEILING;
			     break;
		      case "codepoint-equal":
				 id = FUNC_CODEPOINT_EQUAL;
				 break;
		      case "codepoints-to-string":
			     id = FUNC_CODE_POINTS_TO_STRING;
				 break;
		      case "collation-key":
		    	 // Not implemented
				 break;
		      case "collection":
				 id = FUNC_COLLECTION;
				 break;
		      case "compare":
				 id = FUNC_COMPARE;
				 break;
		      case "concat":
				 id = FUNC_CONCAT;
				 break;
		      case "contains":
				 id = FUNC_CONTAINS;
				 break;
		      case "contains-token":
		    	  id = FUNC_CONTAINS_TOKEN;
		    	  break;
		      case "copy-of":
		    	  id = FUNC_COPY_OF;
		    	  break;
		      case "count":
		    	  id = FUNC_COUNT;
		    	  break;
		      case "current":
		    	  id = FUNC_CURRENT;
		    	  break;
		      case "current-date":
		    	  id = FUNC_CURRENT_DATE;
		    	  break;
		      case "current-dateTime":
		    	  id = FUNC_CURRENT_DATETIME;
		    	  break;
		      case "current-group":
		    	  id = FUNC_CURRENT_GROUP;
		    	  break;
		      case "current-grouping-key":
		    	  id = FUNC_CURRENT_GROUPING_KEY;
		    	  break;
		      case "current-merge-group":
		    	  id = FUNC_CURRENT_MERGE_GROUP;
		    	  break;
		      case "current-merge-key":
		    	  id = FUNC_CURRENT_MERGE_KEY;
		    	  break;
		      case "current-time":
		    	  id = FUNC_CURRENT_TIME;
		    	  break;
		      case "data":
		    	  id = FUNC_DATA;
		    	  break;
		      case "dateTime":
		    	  id = FUNC_DATE_TIME;
		    	  break;
		      case "day-from-date":
		    	  id = FUNC_DAY_FROM_DATE;
		    	  break;
		      case "day-from-dateTime":
		    	  id = FUNC_DAY_FROM_DATE_TIME;
		    	  break;
		      case "days-from-duration":
		    	  id = FUNC_DAYS_FROM_DURATION;
		    	  break;
		      case "deep-equal":
		    	  id = FUNC_DEEP_EQUAL;
		    	  break;
		      case "default-collation":
		    	  id = FUNC_DEFAULT_COLLATION;
		    	  break;
		      case "default-language":
		    	  // Not implemented
		    	  break;
		      case "distinct-values":
		    	  id = FUNC_DISTINCT_VALUES;
		    	  break;
		      case "doc":
		    	  id = FUNC_DOC;
		    	  break;
		      case "doc-available":
		    	  id = FUNC_DOC_AVAILABLE;
		    	  break;
		      case "document":
		    	  id = FUNC_DOCUMENT;
		    	  break;
		      case "document-uri":
		    	  id = FUNC_DOCUMENT_URI;
		    	  break;
		      case "element-available":
		    	  id = FUNC_EXT_ELEM_AVAILABLE;
		    	  break;
		      case "element-with-id":
		    	  // Not implemented
		    	  break;
		      case "empty":
		    	  id = FUNC_EMPTY;
		    	  break;
		      case "encode-for-uri":
		    	  // Not implemented
		    	  break;
		      case "ends-with":
		    	  id = FUNC_ENDS_WITH;
		    	  break;
		      case "environment-variable":
		    	  id = FUNC_ENVIRONMENT_VARIABLE;
		    	  break;
		      case "error":
		    	  id = FUNC_ERROR;
		    	  break;
		      case "escape-html-uri":
		    	  // Not implemented
		    	  break;
		      case "exactly-one":
		    	  id = FUNC_EXACTLY_ONE;
		    	  break;
		      case "exists":
		    	  id = FUNC_EXISTS;
		    	  break;
		      case "false":
		    	  id = FUNC_FALSE;
		    	  break;
		      case "filter":
		    	  id = FUNC_FILTER;
		    	  break;
		      case "floor":
		    	  id = FUNC_FLOOR;
		    	  break;
		      case "fold-left":
		    	  id = FUNC_FOLD_LEFT;
		    	  break;
		      case "fold-right":
		    	  id = FUNC_FOLD_RIGHT;
		    	  break;
		      case "for-each":
		    	  id = FUNC_FOR_EACH;
		    	  break;
		      case "for-each-pair":
		    	  id = FUNC_FOR_EACH_PAIR;
		    	  break;
		      case "format-date":
		    	  id = FUNC_FORMAT_DATE;
		    	  break;
		      case "format-dateTime":
		    	  id = FUNC_FORMAT_DATETIME;
		    	  break;
		      case "format-integer":
		    	  id = FUNC_FORMAT_INTEGER;
		    	  break;
		      case "format-number":
		    	  id = FUNC_FORMAT_NUMBER;
		    	  break;
		      case "format-time":
		    	  id = FUNC_FORMAT_TIME;
		    	  break;
		      case "function-arity":
		    	  id = FUNC_FUNCTION_ARITY;
		    	  break;
		      case "function-lookup":
		    	  // Not implemented
		    	  break;
		      case "function-name":
		    	  id = FUNC_FUNCTION_NAME;
		    	  break;
		      case "function-available":
		    	  id = FUNC_EXT_FUNCTION_AVAILABLE;
		    	  break;
		      case "generate-id":
		    	  id = FUNC_GENERATE_ID;
		    	  break;
		      case "has-children":
		    	  // Not implemented
		    	  break;
		      case "head":
		    	  id = FUNC_HEAD;
		    	  break;
		      case "hours-from-dateTime":
		    	  id = FUNC_HOURS_FROM_DATE_TIME;
		    	  break;
		      case "hours-from-duration":
		    	  id = FUNC_HOURS_FROM_DURATION;
		    	  break;
		      case "hours-from-time":
		    	  id = FUNC_HOURS_FROM_TIME;
		    	  break;
		      case "id":
		    	  id = FUNC_ID;
		    	  break;
		      case "idref":
		    	  // Not implemented
		    	  break;
		      case "implicit-timezone":
		    	  id = FUNC_IMPLICIT_TIMEZONE;
		    	  break;
		      case "index-of":
		    	  id = FUNC_INDEX_OF;
		    	  break;
		      case "innermost":
		    	  // Not implemented
		    	  break;
		      case "in-scope-prefixes":
		    	  id = FUNC_IN_SCOPE_PREFIXES;
		    	  break;
		      case "insert-before":
		    	  id = FUNC_INSERT_BEFORE;
		    	  break;
		      case "iri-to-uri":
		    	  // Not implemented
		    	  break;
		      case "json-doc":
		    	  id = FUNC_JSON_DOC;
		    	  break;
		      case "json-to-xml":
		    	  id = FUNC_JSON_TO_XML;
		    	  break;
		      case "key":
		    	  id = FUNC_KEY;
		    	  break;
		      case "lang":
		    	  id = FUNC_LANG;
		    	  break;
		      case "last":
		    	  id = FUNC_LAST;
		    	  break;
		      case "local-name":
		    	  id = FUNC_LOCAL_NAME;
		    	  break;
		      case "local-name-from-QName":
		    	  id = FUNC_LOCAL_NAME_FROM_QNAME;
		    	  break;
		      case "lower-case":
		    	  id = FUNC_LOWER_CASE;
		    	  break;
		      case "matches":
		    	  id = FUNC_MATCHES;
		    	  break;
		      case "max":
		    	  id = FUNC_MAX;
		    	  break;
		      case "min":
		    	  id = FUNC_MIN;
		    	  break;
		      case "minutes-from-dateTime":
		    	  id = FUNC_MINUTES_FROM_DATE_TIME;
		    	  break;
		      case "minutes-from-duration":
		    	  id = FUNC_MINUTES_FROM_DURATION;
		    	  break;
		      case "minutes-from-time":
		    	  id = FUNC_MINUTES_FROM_TIME;
		    	  break;
		      case "month-from-date":
		    	  id = FUNC_MONTH_FROM_DATE;
		    	  break;
		      case "month-from-dateTime":
		    	  id = FUNC_MONTH_FROM_DATE_TIME;
		    	  break;
		      case "months-from-duration":
		    	  id = FUNC_MONTHS_FROM_DURATION;
		    	  break;
		      case "name":
		    	  id = FUNC_NAME;
		    	  break;
		      case "namespace-uri":
		    	  id = FUNC_NAMESPACE_URI;
		    	  break;
		      case "namespace-uri-for-prefix":
		    	  id = FUNC_NAMESPACE_URI_FOR_PREFIX;
		    	  break;
		      case "namespace-uri-from-QName":
		    	  id = FUNC_NAMESPACE_URI_FROM_QNAME;
		    	  break;
		      case "nilled":
		    	  // Not implemented
		    	  break;
		      case "node-name":
		    	  id = FUNC_NODE_NAME;
		    	  break;
		      case "normalize-space":
		    	  id = FUNC_NORMALIZE_SPACE;
		    	  break;
		      case "normalize-unicode":
		    	  id = FUNC_NORMALIZE_UNICODE;
		    	  break;
		      case "not":
		    	  id = FUNC_NOT;
		    	  break;
		      case "number":
		    	  id = FUNC_NUMBER;
		    	  break;
		      case "one-or-more":
		    	  id = FUNC_ONE_OR_MORE;
		    	  break;
		      case "outermost":
		    	  // Not implemented
		    	  break;
		      case "parse-ietf-date":
		    	  id = FUNC_PARSE_IETF_DATE;
		    	  break;
		      case "parse-json":
		    	  id = FUNC_PARSE_JSON;
		    	  break;
		      case "parse-xml":
		    	  id = FUNC_PARSE_XML;
		    	  break;
		      case "parse-xml-fragment":
		    	  id = FUNC_PARSE_XML_FRAGMENT;
		    	  break;
		      case "path":
		    	  id = FUNC_PATH;
		    	  break;
		      case "position":
		    	  id = FUNC_POSITION;
		    	  break;
		      case "prefix-from-QName":
		    	  id = FUNC_PREFIX_FROM_QNAME;
		    	  break;
		      case "QName":
		    	  id = FUNC_QNAME;
		    	  break;
		      case "random-number-generator":
		    	  id = FUNC_RANDOM_NUMBER_GENERATOR;
		    	  break;
		      case "regex-group":
		    	  id = FUNC_REGEX_GROUP;
		    	  break;
		      case "remove":
		    	  id = FUNC_REMOVE;
		    	  break;
		      case "replace":
		    	  id = FUNC_REPLACE;
		    	  break;
		      case "resolve-QName":
		    	  id = FUNC_RESOLVE_QNAME;
		    	  break;
		      case "resolve-uri":
		    	  // Not implemented
		    	  break;
		      case "reverse":
		    	  id = FUNC_REVERSE;
		    	  break;
		      case "root":
		    	  id = FUNC_ROOT;
		    	  break;
		      case "round":
		    	  id = FUNC_ROUND;
		    	  break;
		      case "round-half-to-even":
		    	  id = FUNC_ROUND_HALF_TO_EVEN;
		    	  break;
		      case "seconds-from-dateTime":
		    	  id = FUNC_SECONDS_FROM_DATE_TIME;
		    	  break;
		      case "seconds-from-duration":
		    	  id = FUNC_SECONDS_FROM_DURATION;
		    	  break;
		      case "seconds-from-time":
		    	  id = FUNC_SECONDS_FROM_TIME;
		    	  break;
		      case "serialize":
		    	  id = FUNC_SERIALIZE;
		    	  break;
		      case "sort":
		    	  id = FUNC_SORT;
		    	  break;
		      case "starts-with":
		    	  id = FUNC_STARTS_WITH;
		    	  break;
		      case "static-base-uri":
		    	  // Not implemented
		    	  break;
		      case "string":
		    	  id = FUNC_STRING;
		    	  break;
		      case "string-join":
		    	  id = FUNC_STRING_JOIN;
		    	  break;
		      case "string-length":
		    	  id = FUNC_STRING_LENGTH;
		    	  break;
		      case "string-to-codepoints":
		    	  id = FUNC_STRING_TO_CODE_POINTS;
		    	  break;
		      case "subsequence":
		    	  id = FUNC_SUBSEQUENCE;
		    	  break;
		      case "substring":
		    	  id = FUNC_SUBSTRING;
		    	  break;
		      case "substring-after":
		    	  id = FUNC_SUBSTRING_AFTER;
		    	  break;
		      case "substring-before":
		    	  id = FUNC_SUBSTRING_BEFORE;
		    	  break;
		      case "sum":
		    	  id = FUNC_SUM;
		    	  break;
		      case "system-property":
		    	  id = FUNC_SYSTEM_PROPERTY;
		    	  break;
		      case "available-system-properties":
		    	  // Not implemented
		    	  break;
		      case "tail":
		    	  id = FUNC_TAIL;
		    	  break;
		      case "timezone-from-date":
		    	  id = FUNC_TIMEZONE_FROM_DATE;
		    	  break;
		      case "timezone-from-dateTime":
		    	  id = FUNC_TIMEZONE_FROM_DATE_TIME;
		    	  break;
		      case "timezone-from-time":
		    	  id = FUNC_TIMEZONE_FROM_TIME;
		    	  break;
		      case "tokenize":
		    	  id = FUNC_TOKENIZE;
		    	  break;
		      case "trace":
		    	  // Not implemented
		    	  break;
		      case "transform":
		    	  id = FUNC_TRANSFORM;
		    	  break;
		      case "translate":
		    	  id = FUNC_TRANSLATE;
		    	  break;
		      case "true":
		    	  id = FUNC_TRUE;
		    	  break;
		      case "unordered":
		    	  id = FUNC_UNORDERED;
		    	  break;
		      case "unparsed-entity-public-id":
		    	  // Not implemented
		    	  break;
		      case "unparsed-entity-uri":
		    	  id = FUNC_UNPARSED_ENTITY_URI;
		    	  break;
		      case "unparsed-text":
		    	  id = FUNC_UNPARSED_TEXT;
		    	  break;
		      case "unparsed-text-available":
		    	  id = FUNC_UNPARSED_TEXT_AVAILABLE;
		    	  break;
		      case "unparsed-text-lines":
		    	  id = FUNC_UNPARSED_TEXT_LINES;
		    	  break;
		      case "upper-case":
		    	  id = FUNC_UPPER_CASE;
		    	  break;
		      case "uri-collection":
		    	  // Not implemented
		    	  break;
		      case "xml-to-json":
		    	  id = FUNC_XML_TO_JSON;
		    	  break;
		      case "year-from-date":
		    	  id = FUNC_YEAR_FROM_DATE;
		    	  break;
		      case "year-from-dateTime":
		    	  id = FUNC_YEAR_FROM_DATE_TIME;
		    	  break;
		      case "years-from-duration":
		    	  id = FUNC_YEARS_FROM_DURATION;
		    	  break;
		      case "zero-or-one":
		    	  id = FUNC_ZERO_OR_ONE;
		    	  break;
		      case ".":
		    	  id = FUNC_PERIOD;
		    	  break;
			  default:
				 // no op 
		}
		
		return id;	
  }
  
  /**
   * Method definition, to get function id from the supplied function 
   * name, for XPath 3.1 built-in functions from namespace 
   * http://www.w3.org/2005/xpath-functions/math. 
   * 
   * @param localName                  The supplied string value, for
   *                                   an XSL function local name.
   * @return                           Function id, for an XSL built-in
   *                                   function.
   */
  public Object getFunctionIdForXPathBuiltinMathFuncs(String localName) {	    
	    
	    Object id = null;
		
		switch (localName) {
		      case Keywords.FUNC_MATH_ACOS:
		        id = FUNC_MATH_ACOS;
		        break;
		      case Keywords.FUNC_MATH_ASIN:
			     id = FUNC_MATH_ASIN;
			     break;
		      case Keywords.FUNC_MATH_ATAN:
				 id = FUNC_MATH_ATAN;
				 break;
		      case Keywords.FUNC_MATH_ATAN2:
				 id = FUNC_MATH_ATAN2;
				 break;
		      case Keywords.FUNC_MATH_COS:
			     id = FUNC_MATH_COS;
			     break;
		      case Keywords.FUNC_MATH_EXP:
			     id = FUNC_MATH_EXP;
			     break;		      		      
		      case Keywords.FUNC_MATH_EXP10:
			     id = FUNC_MATH_EXP10;
			     break;
		      case Keywords.FUNC_MATH_LOG:
				 id = FUNC_MATH_LOG;
				 break;
		      case Keywords.FUNC_MATH_LOG10:
			     id = FUNC_MATH_LOG10;
			     break;
		      case Keywords.FUNC_MATH_PI:
				 id = FUNC_MATH_PI;
				 break;
		      case Keywords.FUNC_MATH_POW:
			     id = FUNC_MATH_POW;
				 break;
		      case Keywords.FUNC_MATH_SIN:
				 id = FUNC_MATH_SIN;
				 break;
		      case Keywords.FUNC_MATH_SQRT:
				 id = FUNC_MATH_SQRT;
				 break;
		      case Keywords.FUNC_MATH_TAN:
				 id = FUNC_MATH_TAN;
				 break;
			  default:
				 // no op 
		}
		
		return id; 
  }
  
  /**
   * Method definition, to get function id from the supplied function 
   * name, for XPath 3.1 built-in functions from namespace 
   * http://www.w3.org/2005/xpath-functions/map. 
   * 
   * @param localName                  The supplied string value, for
   *                                   an XSL function local name.
   * @return                           Function id, for an XSL built-in
   *                                   function.
   */
  public Object getFunctionIdForXPathBuiltinMapFuncs(String localName) {		
		
	    Object id = null;
		
		switch (localName) {
		      case Keywords.FUNC_MAP_CONTAINS:
			     id = FUNC_MAP_CONTAINS;
			     break;
	          case Keywords.FUNC_MAP_ENTRY:
				 id = FUNC_MAP_ENTRY;
				 break;
	          case Keywords.FUNC_MAP_FIND:
				 id = FUNC_MAP_FIND;
				 break;
	          case Keywords.FUNC_MAP_FOREACH:
				 id = FUNC_MAP_FOREACH;
				 break;
	          case Keywords.FUNC_MAP_GET:
				 id = FUNC_MAP_GET;
				 break;
	          case Keywords.FUNC_MAP_KEYS:
				 id = FUNC_MAP_KEYS;
				 break;
		      case Keywords.FUNC_MAP_MERGE:
		         id = FUNC_MAP_MERGE;
		         break;		      		      		      		      
		      case Keywords.FUNC_MAP_PUT:
			     id = FUNC_MAP_PUT;
			     break;		      		      		      
		      case Keywords.FUNC_MAP_REMOVE:
				 id = FUNC_MAP_REMOVE;
				 break;
		      case Keywords.FUNC_MAP_SIZE:
				 id = FUNC_MAP_SIZE;
				 break;
			  default:
				 // no op 
		}
		
		return id;	
  }
  
  /**
   * Method definition, to get function id from the supplied function 
   * name, for XPath 3.1 built-in functions from namespace 
   * http://www.w3.org/2005/xpath-functions/array. 
   * 
   * @param localName                  The supplied string value, for
   *                                   an XSL function local name.
   * @return                           Function id, for an XSL built-in
   *                                   function.
   */
  public Object getFunctionIdForXPathBuiltinArrayFuncs(String localName) {	  
		
	    Object id = null;
		
		switch (localName) {
		      case Keywords.FUNC_ARRAY_APPEND:
		         id = FUNC_ARRAY_APPEND;
		         break;
		      case Keywords.FUNC_ARRAY_FILTER:
		         id = FUNC_ARRAY_FILTER;
		         break;
		      case Keywords.FUNC_ARRAY_FLATTEN:
				 id = FUNC_ARRAY_FLATTEN;
				 break;
		      case Keywords.FUNC_ARRAY_FOLD_LEFT:
				 id = FUNC_ARRAY_FOLD_LEFT;
				 break;
			  case Keywords.FUNC_ARRAY_FOLD_RIGHT:
				 id = FUNC_ARRAY_FOLD_RIGHT;
				 break;
			  case Keywords.FUNC_ARRAY_FOR_EACH:
				 id = FUNC_ARRAY_FOR_EACH;
				 break;		      
			  case Keywords.FUNC_ARRAY_FOR_EACH_PAIR:
				 id = FUNC_ARRAY_FOR_EACH_PAIR;
				 break;
			  case Keywords.FUNC_ARRAY_GET:
				 id = FUNC_ARRAY_GET;
				 break;
			  case Keywords.FUNC_ARRAY_HEAD:
				 id = FUNC_ARRAY_HEAD;
				 break;
			  case Keywords.FUNC_ARRAY_INSERT_BEFORE:
				 id = FUNC_ARRAY_INSERT_BEFORE;
				 break;
			  case Keywords.FUNC_ARRAY_JOIN:
				 id = FUNC_ARRAY_JOIN;
			     break;
			  case Keywords.FUNC_ARRAY_PUT:
				 id = FUNC_ARRAY_PUT;
				 break;
			  case Keywords.FUNC_ARRAY_REMOVE:
				 id = FUNC_ARRAY_REMOVE;
				 break;
			  case Keywords.FUNC_ARRAY_REVERSE:
			     id = FUNC_ARRAY_REVERSE;
				 break;	
		      case Keywords.FUNC_ARRAY_SIZE:
			     id = FUNC_ARRAY_SIZE;
			     break;
		      case Keywords.FUNC_ARRAY_SORT:
				 id = FUNC_ARRAY_SORT;
				 break;
		      case Keywords.FUNC_ARRAY_SUBARRAY:
			     id = FUNC_ARRAY_SUBARRAY;
			     break;		      		      		      
		      case Keywords.FUNC_ARRAY_TAIL:
			     id = FUNC_ARRAY_TAIL;
			     break;		      	      		      	      		      		      
			  default:
				 // no op 
		}
		
		return id;		
   }

  /**
   * Tell if a built-in, non-namespaced function is available.
   *
   * @param methName The local name of the function.
   *
   * @return True if the function can be executed.
   */
  public boolean functionAvailable(String methName)
  {	  
	  boolean result = false;
	  
	  Object funcId = getFunctionIdForXSLBuiltinFuncs(methName);
	  
	  if (funcId != null) {
		 result = true; 
	  }
	  
	  return result;
  }
  
}
