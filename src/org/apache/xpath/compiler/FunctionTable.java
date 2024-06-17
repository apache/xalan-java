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
package org.apache.xpath.compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.xpath.functions.Function;

/**
 * The function table for XPath 3.1.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Christine Li <jycli@apache.org>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3 specific changes, to this class)
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

  // Proprietary

  /** The 'document-location()' id (Proprietary). */
  public static final int FUNC_DOCLOCATION = 35;
  
  /**
   * XPath 3.1 built-in functions namespace uri, for most of the functions
   * available to XPath 3.1 language users. The XPath functions available within
   * this namespace, may be used without binding the function name with an XML 
   * namespace, or binding with a non-null XML namespace (the commonly used XML 
   * namespace prefix for this namespace uri is "fn", as specified by
   * XPath 3.1 spec).
   */
  public static final String XPATH_BUILT_IN_FUNCS_NS_URI = "http://www.w3.org/2005/xpath-functions";
  
  /**
   * XPath 3.1 built-in functions namespace uri, for maths trigonometric and exponential 
   * functions. The XPath functions available within this namespace, must be used by 
   * qualifying the function name with an XML namespace bound to this uri (the commonly 
   * used XML namespace prefix for this namespace uri is "math", as specified by 
   * XPath 3.1 spec).
   */
  public static final String XPATH_BUILT_IN_MATH_FUNCS_NS_URI = "http://www.w3.org/2005/xpath-functions/math";
  
  static final Integer[] XPATH_MATH_FUNC_IDS = new Integer[] { new Integer(FUNC_MATH_PI), new Integer(FUNC_MATH_EXP),
                                                               new Integer(FUNC_MATH_EXP10), new Integer(FUNC_MATH_LOG),
                                                               new Integer(FUNC_MATH_LOG10), new Integer(FUNC_MATH_POW),
                                                               new Integer(FUNC_MATH_SQRT), new Integer(FUNC_MATH_SIN),
                                                               new Integer(FUNC_MATH_COS), new Integer(FUNC_MATH_TAN),
                                                               new Integer(FUNC_MATH_ASIN), new Integer(FUNC_MATH_ACOS),
                                                               new Integer(FUNC_MATH_ATAN), new Integer(FUNC_MATH_ATAN2) };
  
  static final List<Integer> XPATH_MATH_FUNC_IDS_ARR = Arrays.asList(XPATH_MATH_FUNC_IDS);
  
  /**
   * XPath 3.1 built-in functions namespace uri, for map related functions. The XPath functions available 
   * within this namespace, must be used by qualifying the function name with an XML namespace bound to this 
   * uri (the commonly used XML namespace prefix for this namespace uri is "map", as specified by 
   * XPath 3.1 spec).
   */
  public static final String XPATH_BUILT_IN_MAP_FUNCS_NS_URI = "http://www.w3.org/2005/xpath-functions/map";
    
  static final Integer[] XPATH_MAP_FUNC_IDS = new Integer[] { new Integer(FUNC_MAP_MERGE), new Integer(FUNC_MAP_SIZE), 
		                                                      new Integer(FUNC_MAP_KEYS), new Integer(FUNC_MAP_CONTAINS), 
		                                                      new Integer(FUNC_MAP_GET), new Integer(FUNC_MAP_PUT), 
		                                                      new Integer(FUNC_MAP_ENTRY), new Integer(FUNC_MAP_FOREACH),
		                                                      new Integer(FUNC_MAP_REMOVE) };
  
  static final List<Integer> XPATH_MAP_FUNC_IDS_ARR = Arrays.asList(XPATH_MAP_FUNC_IDS);
  
  /**
   * XPath 3.1 built-in functions namespace uri, for array related functions. The XPath functions available 
   * within this namespace, must be used by qualifying the function name with an XML namespace bound to this 
   * uri (the commonly used XML namespace prefix for this namespace uri is "array", as specified by 
   * XPath 3.1 spec).
   */
  public static final String XPATH_BUILT_IN_ARRAY_FUNCS_NS_URI = "http://www.w3.org/2005/xpath-functions/array";
    
  static final Integer[] XPATH_ARRAY_FUNC_IDS = new Integer[] { new Integer(FUNC_ARRAY_SIZE), new Integer(FUNC_ARRAY_GET),
		                                                        new Integer(FUNC_ARRAY_PUT), new Integer(FUNC_ARRAY_APPEND),
		                                                        new Integer(FUNC_ARRAY_SUBARRAY), new Integer(FUNC_ARRAY_REMOVE),
		                                                        new Integer(FUNC_ARRAY_INSERT_BEFORE),new Integer(FUNC_ARRAY_HEAD),
		                                                        new Integer(FUNC_ARRAY_TAIL),new Integer(FUNC_ARRAY_REVERSE), 
		                                                        new Integer(FUNC_ARRAY_JOIN), new Integer(FUNC_ARRAY_FOR_EACH),
		                                                        new Integer(FUNC_ARRAY_FILTER), new Integer(FUNC_ARRAY_FOR_EACH_PAIR),
		                                                        new Integer(FUNC_ARRAY_FOLD_LEFT), new Integer(FUNC_ARRAY_FOLD_RIGHT),
		                                                        new Integer(FUNC_ARRAY_SORT) };
  
  static final List<Integer> XPATH_ARRAY_FUNC_IDS_ARR = Arrays.asList(XPATH_ARRAY_FUNC_IDS);

  /**
   * The function table.
   */
  private static Class m_functions[];

  /** Table of function name to function ID associations. */
  private static HashMap m_functionID = new HashMap();
    
  /**
   * The function table contains customized functions
   */
  private Class m_functions_customer[] = new Class[NUM_ALLOWABLE_ADDINS];

  /**
   * Table of function name to function ID associations for customized functions
   */
  private HashMap m_functionID_customer = new HashMap();
  
  /**
   * Number of built in functions. Be sure to update this as
   * built-in functions are added.
   */
  private static final int NUM_BUILT_IN_FUNCS = 162;

  /**
   * Number of built-in functions that may be added.
   */
  private static final int NUM_ALLOWABLE_ADDINS = 30;

  /**
   * The index to the next free function index.
   */
  private int m_funcNextFreeIndex = NUM_BUILT_IN_FUNCS;
  
  static
  {
    m_functions = new Class[NUM_BUILT_IN_FUNCS];
    m_functions[FUNC_CURRENT] = org.apache.xpath.functions.FuncCurrent.class;
    m_functions[FUNC_LAST] = org.apache.xpath.functions.FuncLast.class;
    m_functions[FUNC_POSITION] = org.apache.xpath.functions.FuncPosition.class;
    m_functions[FUNC_COUNT] = org.apache.xpath.functions.FuncCount.class;
    m_functions[FUNC_ID] = org.apache.xpath.functions.FuncId.class;
    m_functions[FUNC_KEY] =
      org.apache.xalan.templates.FuncKey.class;
    m_functions[FUNC_LOCAL_NAME] = 
      org.apache.xpath.functions.FuncLocalName.class;
    m_functions[FUNC_NAMESPACE_URI] = 
      org.apache.xpath.functions.FuncNamespaceUri.class;
    
    m_functions[FUNC_NAME] = org.apache.xpath.functions.FuncName.class;
    
    m_functions[FUNC_RESOLVE_QNAME] = org.apache.xpath.functions.FuncResolveQName.class;
    m_functions[FUNC_QNAME] = org.apache.xpath.functions.FuncQName.class;
    m_functions[FUNC_PREFIX_FROM_QNAME] = org.apache.xpath.functions.FuncPrefixFromQName.class;
    m_functions[FUNC_LOCAL_NAME_FROM_QNAME] = org.apache.xpath.functions.FuncLocalNameFromQName.class;
    m_functions[FUNC_NAMESPACE_URI_FROM_QNAME] = org.apache.xpath.functions.FuncNamespaceUriFromQName.class;
    m_functions[FUNC_NAMESPACE_URI_FOR_PREFIX] = org.apache.xpath.functions.FuncNamespaceUriForPrefix.class;
    m_functions[FUNC_IN_SCOPE_PREFIXES] = org.apache.xpath.functions.FuncInScopePrefixes.class;
    
    m_functions[FUNC_GENERATE_ID] = 
      org.apache.xpath.functions.FuncGenerateId.class;
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
    m_functions[FUNC_SUM] = org.apache.xpath.functions.FuncSum.class;
    m_functions[FUNC_STRING] = org.apache.xpath.functions.FuncString.class;
    
    m_functions[FUNC_DATA] = org.apache.xpath.functions.FuncData.class;
    
    m_functions[FUNC_STARTS_WITH] = 
      org.apache.xpath.functions.FuncStartsWith.class;
    m_functions[FUNC_CONTAINS] = org.apache.xpath.functions.FuncContains.class;
    m_functions[FUNC_SUBSTRING_BEFORE] = 
      org.apache.xpath.functions.FuncSubstringBefore.class;
    m_functions[FUNC_SUBSTRING_AFTER] = 
      org.apache.xpath.functions.FuncSubstringAfter.class;
    m_functions[FUNC_NORMALIZE_SPACE] = 
      org.apache.xpath.functions.FuncNormalizeSpace.class;
    m_functions[FUNC_TRANSLATE] = 
      org.apache.xpath.functions.FuncTranslate.class;
    m_functions[FUNC_CONCAT] = org.apache.xpath.functions.FuncConcat.class;
    m_functions[FUNC_SYSTEM_PROPERTY] = 
      org.apache.xpath.functions.FuncSystemProperty.class;
    m_functions[FUNC_EXT_FUNCTION_AVAILABLE] =
      org.apache.xpath.functions.FuncExtFunctionAvailable.class;
    m_functions[FUNC_EXT_ELEM_AVAILABLE] =
      org.apache.xpath.functions.FuncExtElementAvailable.class;
    m_functions[FUNC_SUBSTRING] = 
      org.apache.xpath.functions.FuncSubstring.class;
    m_functions[FUNC_STRING_LENGTH] = 
      org.apache.xpath.functions.FuncStringLength.class;
    m_functions[FUNC_DOCLOCATION] = 
      org.apache.xpath.functions.FuncDoclocation.class;
    m_functions[FUNC_UNPARSED_ENTITY_URI] =
      org.apache.xpath.functions.FuncUnparsedEntityURI.class;
    
    m_functions[FUNC_MATCHES] = 
      org.apache.xpath.functions.FuncMatches.class;
    m_functions[FUNC_REPLACE] = 
      org.apache.xpath.functions.FuncReplace.class;
    m_functions[FUNC_CURRENT_GROUPING_KEY] = 
      org.apache.xalan.templates.FuncCurrentGroupingKey.class;
    m_functions[FUNC_CURRENT_GROUP] = 
      org.apache.xalan.templates.FuncCurrentGroup.class;
    m_functions[FUNC_ABS] = 
      org.apache.xpath.functions.FuncAbs.class;
    m_functions[FUNC_REGEX_GROUP] = 
      org.apache.xalan.templates.FuncRegexGroup.class;
    m_functions[FUNC_TOKENIZE] = 
      org.apache.xpath.functions.FuncTokenize.class;
    m_functions[FUNC_UNPARSED_TEXT] = 
      org.apache.xpath.functions.FuncUnparsedText.class;
    m_functions[FUNC_STRING_JOIN] = 
      org.apache.xpath.functions.FuncStringJoin.class;
    m_functions[FUNC_CURRENT_DATETIME] = 
      org.apache.xpath.functions.FuncCurrentDateTime.class;
    m_functions[FUNC_CURRENT_DATE] = 
      org.apache.xpath.functions.FuncCurrentDate.class;
    m_functions[FUNC_CURRENT_TIME] = 
      org.apache.xpath.functions.FuncCurrentTime.class;
    m_functions[FUNC_UPPER_CASE] = 
      org.apache.xpath.functions.FuncUpperCase.class;
    m_functions[FUNC_LOWER_CASE] = 
      org.apache.xpath.functions.FuncLowerCase.class;
    m_functions[FUNC_IMPLICIT_TIMEZONE] = 
      org.apache.xpath.functions.FuncImplicitTimezone.class;
    m_functions[FUNC_INDEX_OF] = 
      org.apache.xpath.functions.FuncIndexOf.class;        
    m_functions[FUNC_DISTINCT_VALUES] = 
      org.apache.xpath.functions.FuncDistinctValues.class;
    
    m_functions[FUNC_FOR_EACH] = 
      org.apache.xpath.functions.FuncForEach.class;
    m_functions[FUNC_FILTER] = 
      org.apache.xpath.functions.FuncFilter.class;
    m_functions[FUNC_FOLD_LEFT] = 
      org.apache.xpath.functions.FuncFoldLeft.class;
    m_functions[FUNC_FOLD_RIGHT] = 
      org.apache.xpath.functions.FuncFoldRight.class;
    m_functions[FUNC_FOR_EACH_PAIR] = 
      org.apache.xpath.functions.FuncForEachPair.class;
    m_functions[FUNC_SORT] = 
      org.apache.xpath.functions.FuncSort.class;
    
    // XPath 3.1 built-in functions configurations for the math 
    // functions namespace http://www.w3.org/2005/xpath-functions/math    
    m_functions[FUNC_MATH_PI] = 
      org.apache.xpath.functions.math.FuncMathPi.class;
    m_functions[FUNC_MATH_EXP] = 
      org.apache.xpath.functions.math.FuncMathExp.class;
    m_functions[FUNC_MATH_EXP10] = 
      org.apache.xpath.functions.math.FuncMathExp10.class;
    m_functions[FUNC_MATH_LOG] = 
      org.apache.xpath.functions.math.FuncMathLog.class;
    m_functions[FUNC_MATH_LOG10] = 
      org.apache.xpath.functions.math.FuncMathLog10.class;
    m_functions[FUNC_MATH_POW] = 
      org.apache.xpath.functions.math.FuncMathPow.class;
    m_functions[FUNC_MATH_SQRT] = 
      org.apache.xpath.functions.math.FuncMathSqrt.class;
    m_functions[FUNC_MATH_SIN] = 
      org.apache.xpath.functions.math.FuncMathSin.class;
    m_functions[FUNC_MATH_COS] = 
      org.apache.xpath.functions.math.FuncMathCos.class;
    m_functions[FUNC_MATH_TAN] = 
      org.apache.xpath.functions.math.FuncMathTan.class;
    m_functions[FUNC_MATH_ASIN] = 
      org.apache.xpath.functions.math.FuncMathAsin.class;
    m_functions[FUNC_MATH_ACOS] = 
      org.apache.xpath.functions.math.FuncMathAcos.class;
    m_functions[FUNC_MATH_ATAN] = 
      org.apache.xpath.functions.math.FuncMathAtan.class;
    m_functions[FUNC_MATH_ATAN2] = 
      org.apache.xpath.functions.math.FuncMathAtan2.class;
    
    m_functions[FUNC_YEARS_FROM_DURATION] = 
      org.apache.xpath.functions.FuncYearsFromDuration.class;
    m_functions[FUNC_MONTHS_FROM_DURATION] = 
      org.apache.xpath.functions.FuncMonthsFromDuration.class;
    m_functions[FUNC_DAYS_FROM_DURATION] = 
      org.apache.xpath.functions.FuncDaysFromDuration.class;
    m_functions[FUNC_HOURS_FROM_DURATION] = 
      org.apache.xpath.functions.FuncHoursFromDuration.class;
    m_functions[FUNC_MINUTES_FROM_DURATION] = 
      org.apache.xpath.functions.FuncMinutesFromDuration.class;
    m_functions[FUNC_SECONDS_FROM_DURATION] = 
      org.apache.xpath.functions.FuncSecondsFromDuration.class;
    
    m_functions[FUNC_CODE_POINTS_TO_STRING] = 
      org.apache.xpath.functions.FuncCodePointsToString.class;
    m_functions[FUNC_STRING_TO_CODE_POINTS] = 
      org.apache.xpath.functions.FuncStringToCodepoints.class;
    m_functions[FUNC_COMPARE] = 
      org.apache.xpath.functions.FuncCompare.class;
    m_functions[FUNC_CODEPOINT_EQUAL] = 
      org.apache.xpath.functions.FuncCodepointEqual.class;
    m_functions[FUNC_CONTAINS_TOKEN] = 
      org.apache.xpath.functions.FuncContainsToken.class;
    
    m_functions[FUNC_EMPTY] = 
      org.apache.xpath.functions.FuncEmpty.class;
    m_functions[FUNC_EXISTS] = 
      org.apache.xpath.functions.FuncExists.class;
    m_functions[FUNC_HEAD] = 
      org.apache.xpath.functions.FuncHead.class;
    m_functions[FUNC_TAIL] = 
      org.apache.xpath.functions.FuncTail.class;
    m_functions[FUNC_INSERT_BEFORE] = 
      org.apache.xpath.functions.FuncInsertBefore.class;
    m_functions[FUNC_REMOVE] = 
      org.apache.xpath.functions.FuncRemove.class;
    m_functions[FUNC_REVERSE] = 
      org.apache.xpath.functions.FuncReverse.class;
    m_functions[FUNC_SUBSEQUENCE] = 
      org.apache.xpath.functions.FuncSubsequence.class;
    m_functions[FUNC_UNORDERED] = 
      org.apache.xpath.functions.FuncUnordered.class;
    
    m_functions[FUNC_PARSE_XML] = 
      org.apache.xpath.functions.FuncParseXml.class;
    m_functions[FUNC_PARSE_XML_FRAGMENT] = 
      org.apache.xpath.functions.FuncParseXmlFragment.class;
    
    m_functions[FUNC_AVG] = org.apache.xpath.functions.FuncAvg.class;
    m_functions[FUNC_MAX] = org.apache.xpath.functions.FuncMax.class;
    m_functions[FUNC_MIN] = org.apache.xpath.functions.FuncMin.class;
    
    m_functions[FUNC_DOC] = org.apache.xpath.functions.FuncDoc.class;
    
    m_functions[FUNC_NODE_NAME] = org.apache.xpath.functions.FuncNodeName.class;
    m_functions[FUNC_DEEP_EQUAL] = org.apache.xpath.functions.FuncDeepEqual.class;
    
    m_functions[FUNC_DATE_TIME] = org.apache.xpath.functions.FuncDateTime.class;
    m_functions[FUNC_YEAR_FROM_DATE_TIME] = org.apache.xpath.functions.FuncYearFromDateTime.class;
    m_functions[FUNC_MONTH_FROM_DATE_TIME] = org.apache.xpath.functions.FuncMonthFromDateTime.class;
    m_functions[FUNC_DAY_FROM_DATE_TIME] = org.apache.xpath.functions.FuncDayFromDateTime.class;
    m_functions[FUNC_HOURS_FROM_DATE_TIME] = org.apache.xpath.functions.FuncHoursFromDateTime.class;
    m_functions[FUNC_MINUTES_FROM_DATE_TIME] = org.apache.xpath.functions.FuncMinutesFromDateTime.class;
    m_functions[FUNC_SECONDS_FROM_DATE_TIME] = org.apache.xpath.functions.FuncSecondsFromDateTime.class;
    m_functions[FUNC_TIMEZONE_FROM_DATE_TIME] = org.apache.xpath.functions.FuncTimezoneFromDateTime.class;
    m_functions[FUNC_YEAR_FROM_DATE] = org.apache.xpath.functions.FuncYearFromDate.class;
    m_functions[FUNC_MONTH_FROM_DATE] = org.apache.xpath.functions.FuncMonthFromDate.class;
    m_functions[FUNC_DAY_FROM_DATE] = org.apache.xpath.functions.FuncDayFromDate.class;
    m_functions[FUNC_TIMEZONE_FROM_DATE] = org.apache.xpath.functions.FuncTimezoneFromDate.class;
    m_functions[FUNC_HOURS_FROM_TIME] = org.apache.xpath.functions.FuncHoursFromTime.class;
    m_functions[FUNC_MINUTES_FROM_TIME] = org.apache.xpath.functions.FuncMinutesFromTime.class;
    m_functions[FUNC_SECONDS_FROM_TIME] = org.apache.xpath.functions.FuncSecondsFromTime.class;
    m_functions[FUNC_TIMEZONE_FROM_TIME] = org.apache.xpath.functions.FuncTimezoneFromTime.class;
    
    m_functions[FUNC_DEFAULT_COLLATION] = org.apache.xpath.functions.FuncDefaultCollation.class;
    m_functions[FUNC_BASE_URI] = org.apache.xpath.functions.FuncBaseUri.class;
    m_functions[FUNC_DOCUMENT_URI] = org.apache.xpath.functions.FuncDocumentUri.class;        
    
    // XPath 3.1 built-in functions configurations for the map 
    // functions namespace http://www.w3.org/2005/xpath-functions/map
    m_functions[FUNC_MAP_MERGE] = org.apache.xpath.functions.map.FuncMapMerge.class;
    m_functions[FUNC_MAP_SIZE] = org.apache.xpath.functions.map.FuncMapSize.class;
    m_functions[FUNC_MAP_KEYS] = org.apache.xpath.functions.map.FuncMapKeys.class;
    m_functions[FUNC_MAP_CONTAINS] = org.apache.xpath.functions.map.FuncMapContains.class;
    m_functions[FUNC_MAP_GET] = org.apache.xpath.functions.map.FuncMapGet.class;
    m_functions[FUNC_MAP_PUT] = org.apache.xpath.functions.map.FuncMapPut.class;
    m_functions[FUNC_MAP_ENTRY] = org.apache.xpath.functions.map.FuncMapEntry.class;
    m_functions[FUNC_MAP_FOREACH] = org.apache.xpath.functions.map.FuncMapForEach.class;
    m_functions[FUNC_MAP_REMOVE] = org.apache.xpath.functions.map.FuncMapRemove.class;
    
    // XPath 3.1 built-in functions configurations for the array 
    // functions namespace http://www.w3.org/2005/xpath-functions/array    
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
    
    m_functions[FUNC_PARSE_JSON] = org.apache.xpath.functions.FuncParseJson.class;
    m_functions[FUNC_JSON_TO_XML] = org.apache.xpath.functions.FuncJsonToXml.class;
    
  }

  static{
          m_functionID.put(Keywords.FUNC_CURRENT_STRING,
                          new Integer(FunctionTable.FUNC_CURRENT));
          m_functionID.put(Keywords.FUNC_LAST_STRING,
                          new Integer(FunctionTable.FUNC_LAST));
          m_functionID.put(Keywords.FUNC_POSITION_STRING,
                          new Integer(FunctionTable.FUNC_POSITION));
          m_functionID.put(Keywords.FUNC_COUNT_STRING,
                          new Integer(FunctionTable.FUNC_COUNT));
          m_functionID.put(Keywords.FUNC_ID_STRING,
                          new Integer(FunctionTable.FUNC_ID));
          m_functionID.put(Keywords.FUNC_KEY_STRING,
                          new Integer(FunctionTable.FUNC_KEY));
          m_functionID.put(Keywords.FUNC_LOCAL_NAME_STRING,
                          new Integer(FunctionTable.FUNC_LOCAL_NAME));
          m_functionID.put(Keywords.FUNC_NAMESPACE_URI_STRING,
                          new Integer(FunctionTable.FUNC_NAMESPACE_URI));          
          m_functionID.put(Keywords.FUNC_GENERATE_ID_STRING,
                          new Integer(FunctionTable.FUNC_GENERATE_ID));
          m_functionID.put(Keywords.FUNC_NOT_STRING,
                          new Integer(FunctionTable.FUNC_NOT));
          m_functionID.put(Keywords.FUNC_TRUE_STRING,
                          new Integer(FunctionTable.FUNC_TRUE));
          m_functionID.put(Keywords.FUNC_FALSE_STRING,
                          new Integer(FunctionTable.FUNC_FALSE));
          m_functionID.put(Keywords.FUNC_BOOLEAN_STRING,
                          new Integer(FunctionTable.FUNC_BOOLEAN));
          m_functionID.put(Keywords.FUNC_LANG_STRING,
                          new Integer(FunctionTable.FUNC_LANG));
          m_functionID.put(Keywords.FUNC_ROOT_STRING,
                          new Integer(FunctionTable.FUNC_ROOT));
          m_functionID.put(Keywords.FUNC_NUMBER_STRING,
                          new Integer(FunctionTable.FUNC_NUMBER));
          m_functionID.put(Keywords.FUNC_FLOOR_STRING,
                          new Integer(FunctionTable.FUNC_FLOOR));
          m_functionID.put(Keywords.FUNC_CEILING_STRING,
                          new Integer(FunctionTable.FUNC_CEILING));
          m_functionID.put(Keywords.FUNC_ROUND_STRING,
                          new Integer(FunctionTable.FUNC_ROUND));
          m_functionID.put(Keywords.FUNC_SUM_STRING,
                          new Integer(FunctionTable.FUNC_SUM));
          m_functionID.put(Keywords.FUNC_STRING_STRING,
                          new Integer(FunctionTable.FUNC_STRING));
          
          m_functionID.put(Keywords.FUNC_DATA_STRING,
                          new Integer(FunctionTable.FUNC_DATA));
          
          m_functionID.put(Keywords.FUNC_STARTS_WITH_STRING,
                          new Integer(FunctionTable.FUNC_STARTS_WITH));
          m_functionID.put(Keywords.FUNC_CONTAINS_STRING,
                          new Integer(FunctionTable.FUNC_CONTAINS));
          m_functionID.put(Keywords.FUNC_SUBSTRING_BEFORE_STRING,
                          new Integer(FunctionTable.FUNC_SUBSTRING_BEFORE));
          m_functionID.put(Keywords.FUNC_SUBSTRING_AFTER_STRING,
                          new Integer(FunctionTable.FUNC_SUBSTRING_AFTER));
          m_functionID.put(Keywords.FUNC_NORMALIZE_SPACE_STRING,
                          new Integer(FunctionTable.FUNC_NORMALIZE_SPACE));
          m_functionID.put(Keywords.FUNC_TRANSLATE_STRING,
                          new Integer(FunctionTable.FUNC_TRANSLATE));
          m_functionID.put(Keywords.FUNC_CONCAT_STRING,
                          new Integer(FunctionTable.FUNC_CONCAT));
          m_functionID.put(Keywords.FUNC_SYSTEM_PROPERTY_STRING,
                          new Integer(FunctionTable.FUNC_SYSTEM_PROPERTY));
          m_functionID.put(Keywords.FUNC_EXT_FUNCTION_AVAILABLE_STRING,
                          new Integer(FunctionTable.FUNC_EXT_FUNCTION_AVAILABLE));
          m_functionID.put(Keywords.FUNC_EXT_ELEM_AVAILABLE_STRING,
                          new Integer(FunctionTable.FUNC_EXT_ELEM_AVAILABLE));
          m_functionID.put(Keywords.FUNC_SUBSTRING_STRING,
                          new Integer(FunctionTable.FUNC_SUBSTRING));
          m_functionID.put(Keywords.FUNC_STRING_LENGTH_STRING,
                          new Integer(FunctionTable.FUNC_STRING_LENGTH));
          m_functionID.put(Keywords.FUNC_UNPARSED_ENTITY_URI_STRING,
                          new Integer(FunctionTable.FUNC_UNPARSED_ENTITY_URI));
          m_functionID.put(Keywords.FUNC_MATCHES_STRING,
                          new Integer(FunctionTable.FUNC_MATCHES));
          m_functionID.put(Keywords.FUNC_REPLACE_STRING,
                          new Integer(FunctionTable.FUNC_REPLACE));
          m_functionID.put(Keywords.FUNC_DOCLOCATION_STRING,
                          new Integer(FunctionTable.FUNC_DOCLOCATION));          
          m_functionID.put(Keywords.FUNC_CURRENT_GROUPING_KEY,
                          new Integer(FunctionTable.FUNC_CURRENT_GROUPING_KEY));
          m_functionID.put(Keywords.FUNC_CURRENT_GROUP,
                          new Integer(FunctionTable.FUNC_CURRENT_GROUP));
          m_functionID.put(Keywords.FUNC_ABS,
                          new Integer(FunctionTable.FUNC_ABS));
          m_functionID.put(Keywords.FUNC_REGEX_GROUP,
                          new Integer(FunctionTable.FUNC_REGEX_GROUP));
          m_functionID.put(Keywords.FUNC_TOKENIZE,
                          new Integer(FunctionTable.FUNC_TOKENIZE));
          m_functionID.put(Keywords.FUNC_UNPARSED_TEXT,
                          new Integer(FunctionTable.FUNC_UNPARSED_TEXT));
          m_functionID.put(Keywords.FUNC_STRING_JOIN,
                          new Integer(FunctionTable.FUNC_STRING_JOIN));
          m_functionID.put(Keywords.FUNC_CURRENT_DATETIME,
                          new Integer(FunctionTable.FUNC_CURRENT_DATETIME));
          m_functionID.put(Keywords.FUNC_CURRENT_DATE,
                          new Integer(FunctionTable.FUNC_CURRENT_DATE));
          m_functionID.put(Keywords.FUNC_CURRENT_TIME,
                          new Integer(FunctionTable.FUNC_CURRENT_TIME));
          m_functionID.put(Keywords.FUNC_UPPER_CASE,
                          new Integer(FunctionTable.FUNC_UPPER_CASE));
          m_functionID.put(Keywords.FUNC_LOWER_CASE,
                          new Integer(FunctionTable.FUNC_LOWER_CASE));
          m_functionID.put(Keywords.FUNC_IMPLICIT_TIMEZONE,
                          new Integer(FunctionTable.FUNC_IMPLICIT_TIMEZONE));
          m_functionID.put(Keywords.FUNC_INDEX_OF,
                          new Integer(FunctionTable.FUNC_INDEX_OF));          
          m_functionID.put(Keywords.FUNC_DISTINCT_VALUES,
                          new Integer(FunctionTable.FUNC_DISTINCT_VALUES));
          
          m_functionID.put(Keywords.FUNC_FOR_EACH,
                          new Integer(FunctionTable.FUNC_FOR_EACH));
          m_functionID.put(Keywords.FUNC_FILTER,
                          new Integer(FunctionTable.FUNC_FILTER));
          m_functionID.put(Keywords.FUNC_FOLD_LEFT,
                          new Integer(FunctionTable.FUNC_FOLD_LEFT));
          m_functionID.put(Keywords.FUNC_FOLD_RIGHT,
                          new Integer(FunctionTable.FUNC_FOLD_RIGHT));
          m_functionID.put(Keywords.FUNC_FOR_EACH_PAIR,
                          new Integer(FunctionTable.FUNC_FOR_EACH_PAIR));
          m_functionID.put(Keywords.FUNC_SORT,
                          new Integer(FunctionTable.FUNC_SORT));
          
          // XPath 3.1 functions configurations for the math functions 
          // namespace http://www.w3.org/2005/xpath-functions/math
          m_functionID.put(Keywords.FUNC_MATH_PI,
                          new Integer(FunctionTable.FUNC_MATH_PI));
          m_functionID.put(Keywords.FUNC_MATH_EXP,
                          new Integer(FunctionTable.FUNC_MATH_EXP));
          m_functionID.put(Keywords.FUNC_MATH_EXP10,
                          new Integer(FunctionTable.FUNC_MATH_EXP10));
          m_functionID.put(Keywords.FUNC_MATH_LOG,
                          new Integer(FunctionTable.FUNC_MATH_LOG));
          m_functionID.put(Keywords.FUNC_MATH_LOG10,
                          new Integer(FunctionTable.FUNC_MATH_LOG10));
          m_functionID.put(Keywords.FUNC_MATH_POW,
                          new Integer(FunctionTable.FUNC_MATH_POW));
          m_functionID.put(Keywords.FUNC_MATH_SQRT,
                          new Integer(FunctionTable.FUNC_MATH_SQRT));
          m_functionID.put(Keywords.FUNC_MATH_SIN,
                          new Integer(FunctionTable.FUNC_MATH_SIN));
          m_functionID.put(Keywords.FUNC_MATH_COS,
                          new Integer(FunctionTable.FUNC_MATH_COS));
          m_functionID.put(Keywords.FUNC_MATH_TAN,
                          new Integer(FunctionTable.FUNC_MATH_TAN));
          m_functionID.put(Keywords.FUNC_MATH_ASIN,
                          new Integer(FunctionTable.FUNC_MATH_ASIN));
          m_functionID.put(Keywords.FUNC_MATH_ACOS,
                         new Integer(FunctionTable.FUNC_MATH_ACOS));
          m_functionID.put(Keywords.FUNC_MATH_ATAN,
                         new Integer(FunctionTable.FUNC_MATH_ATAN));
          m_functionID.put(Keywords.FUNC_MATH_ATAN2,
                         new Integer(FunctionTable.FUNC_MATH_ATAN2));
          
         m_functionID.put(Keywords.FUNC_YEARS_FROM_DURATION,
                         new Integer(FunctionTable.FUNC_YEARS_FROM_DURATION));
         m_functionID.put(Keywords.FUNC_MONTHS_FROM_DURATION,
                         new Integer(FunctionTable.FUNC_MONTHS_FROM_DURATION));
         m_functionID.put(Keywords.FUNC_DAYS_FROM_DURATION,
                         new Integer(FunctionTable.FUNC_DAYS_FROM_DURATION));
         m_functionID.put(Keywords.FUNC_HOURS_FROM_DURATION,
                         new Integer(FunctionTable.FUNC_HOURS_FROM_DURATION));
         m_functionID.put(Keywords.FUNC_MINUTES_FROM_DURATION,
                         new Integer(FunctionTable.FUNC_MINUTES_FROM_DURATION));
         m_functionID.put(Keywords.FUNC_SECONDS_FROM_DURATION,
                         new Integer(FunctionTable.FUNC_SECONDS_FROM_DURATION));
         
         m_functionID.put(Keywords.FUNC_CODE_POINTS_TO_STRING,
                         new Integer(FunctionTable.FUNC_CODE_POINTS_TO_STRING));
         m_functionID.put(Keywords.FUNC_STRING_TO_CODE_POINTS,
                         new Integer(FunctionTable.FUNC_STRING_TO_CODE_POINTS));
         m_functionID.put(Keywords.FUNC_COMPARE,
                         new Integer(FunctionTable.FUNC_COMPARE));
         m_functionID.put(Keywords.FUNC_CODEPOINT_EQUAL,
                         new Integer(FunctionTable.FUNC_CODEPOINT_EQUAL));
         m_functionID.put(Keywords.FUNC_CONTAINS_TOKEN,
                         new Integer(FunctionTable.FUNC_CONTAINS_TOKEN));
         
         m_functionID.put(Keywords.FUNC_EMPTY,
                         new Integer(FunctionTable.FUNC_EMPTY));
         m_functionID.put(Keywords.FUNC_EXISTS,
                         new Integer(FunctionTable.FUNC_EXISTS));
         m_functionID.put(Keywords.FUNC_HEAD,
                         new Integer(FunctionTable.FUNC_HEAD));
         m_functionID.put(Keywords.FUNC_TAIL,
                         new Integer(FunctionTable.FUNC_TAIL));
         m_functionID.put(Keywords.FUNC_INSERT_BEFORE,
                         new Integer(FunctionTable.FUNC_INSERT_BEFORE));
         m_functionID.put(Keywords.FUNC_REMOVE,
                         new Integer(FunctionTable.FUNC_REMOVE));
         m_functionID.put(Keywords.FUNC_REVERSE,
                         new Integer(FunctionTable.FUNC_REVERSE));
         m_functionID.put(Keywords.FUNC_SUBSEQUENCE,
                         new Integer(FunctionTable.FUNC_SUBSEQUENCE));
         m_functionID.put(Keywords.FUNC_UNORDERED,
                         new Integer(FunctionTable.FUNC_UNORDERED));
         
         m_functionID.put(Keywords.FUNC_PARSE_XML,
                         new Integer(FunctionTable.FUNC_PARSE_XML));
         m_functionID.put(Keywords.FUNC_PARSE_XML_FRAGMENT,
                         new Integer(FunctionTable.FUNC_PARSE_XML_FRAGMENT));
         
         m_functionID.put(Keywords.FUNC_AVG,
                         new Integer(FunctionTable.FUNC_AVG));
         m_functionID.put(Keywords.FUNC_MAX,
                         new Integer(FunctionTable.FUNC_MAX));
         m_functionID.put(Keywords.FUNC_MIN,
                         new Integer(FunctionTable.FUNC_MIN));
         
         m_functionID.put(Keywords.FUNC_DOC,
                         new Integer(FunctionTable.FUNC_DOC));
         
         m_functionID.put(Keywords.FUNC_NODE_NAME,
                         new Integer(FunctionTable.FUNC_NODE_NAME));
         m_functionID.put(Keywords.FUNC_DEEP_EQUAL,
                         new Integer(FunctionTable.FUNC_DEEP_EQUAL));
         
         m_functionID.put(Keywords.FUNC_DATE_TIME,
                         new Integer(FunctionTable.FUNC_DATE_TIME));
         m_functionID.put(Keywords.FUNC_YEAR_FROM_DATE_TIME,
                         new Integer(FunctionTable.FUNC_YEAR_FROM_DATE_TIME));
         m_functionID.put(Keywords.FUNC_MONTH_FROM_DATE_TIME,
                         new Integer(FunctionTable.FUNC_MONTH_FROM_DATE_TIME));
         m_functionID.put(Keywords.FUNC_DAY_FROM_DATE_TIME,
                         new Integer(FunctionTable.FUNC_DAY_FROM_DATE_TIME));
         m_functionID.put(Keywords.FUNC_HOURS_FROM_DATE_TIME,
                         new Integer(FunctionTable.FUNC_HOURS_FROM_DATE_TIME));
         m_functionID.put(Keywords.FUNC_MINUTES_FROM_DATE_TIME,
                         new Integer(FunctionTable.FUNC_MINUTES_FROM_DATE_TIME));
         m_functionID.put(Keywords.FUNC_SECONDS_FROM_DATE_TIME,
                         new Integer(FunctionTable.FUNC_SECONDS_FROM_DATE_TIME));
         m_functionID.put(Keywords.FUNC_TIMEZONE_FROM_DATE_TIME,
                         new Integer(FunctionTable.FUNC_TIMEZONE_FROM_DATE_TIME));
         m_functionID.put(Keywords.FUNC_YEAR_FROM_DATE,
                         new Integer(FunctionTable.FUNC_YEAR_FROM_DATE));
         m_functionID.put(Keywords.FUNC_MONTH_FROM_DATE,
                         new Integer(FunctionTable.FUNC_MONTH_FROM_DATE));
         m_functionID.put(Keywords.FUNC_DAY_FROM_DATE,
                         new Integer(FunctionTable.FUNC_DAY_FROM_DATE));
         m_functionID.put(Keywords.FUNC_TIMEZONE_FROM_DATE,
                         new Integer(FunctionTable.FUNC_TIMEZONE_FROM_DATE));
         m_functionID.put(Keywords.FUNC_HOURS_FROM_TIME,
                         new Integer(FunctionTable.FUNC_HOURS_FROM_TIME));
         m_functionID.put(Keywords.FUNC_MINUTES_FROM_TIME,
                         new Integer(FunctionTable.FUNC_MINUTES_FROM_TIME));
         m_functionID.put(Keywords.FUNC_SECONDS_FROM_TIME,
                         new Integer(FunctionTable.FUNC_SECONDS_FROM_TIME));
         m_functionID.put(Keywords.FUNC_TIMEZONE_FROM_TIME,
                         new Integer(FunctionTable.FUNC_TIMEZONE_FROM_TIME));
         
         m_functionID.put(Keywords.FUNC_DEFAULT_COLLATION,
                         new Integer(FunctionTable.FUNC_DEFAULT_COLLATION));
         m_functionID.put(Keywords.FUNC_BASE_URI,
                         new Integer(FunctionTable.FUNC_BASE_URI));
         m_functionID.put(Keywords.FUNC_DOCUMENT_URI,
                         new Integer(FunctionTable.FUNC_DOCUMENT_URI));
         
         // XPath 3.1 functions configurations for the map functions 
         // namespace http://www.w3.org/2005/xpath-functions/map
         m_functionID.put(Keywords.FUNC_MAP_MERGE,
                          new Integer(FunctionTable.FUNC_MAP_MERGE));
         m_functionID.put(Keywords.FUNC_MAP_SIZE,
                          new Integer(FunctionTable.FUNC_MAP_SIZE));
         m_functionID.put(Keywords.FUNC_MAP_KEYS,
                          new Integer(FunctionTable.FUNC_MAP_KEYS));
         m_functionID.put(Keywords.FUNC_MAP_CONTAINS,
                         new Integer(FunctionTable.FUNC_MAP_CONTAINS));
         m_functionID.put(Keywords.FUNC_MAP_GET,
                         new Integer(FunctionTable.FUNC_MAP_GET));
         m_functionID.put(Keywords.FUNC_MAP_PUT,
                         new Integer(FunctionTable.FUNC_MAP_PUT));
         m_functionID.put(Keywords.FUNC_MAP_ENTRY,
                         new Integer(FunctionTable.FUNC_MAP_ENTRY));
         m_functionID.put(Keywords.FUNC_MAP_FOREACH,
                         new Integer(FunctionTable.FUNC_MAP_FOREACH));
         m_functionID.put(Keywords.FUNC_MAP_REMOVE,
                         new Integer(FunctionTable.FUNC_MAP_REMOVE));
         
         // XPath 3.1 functions configurations for the array functions 
         // namespace http://www.w3.org/2005/xpath-functions/array
         m_functionID.put(Keywords.FUNC_ARRAY_SIZE,
                          new Integer(FunctionTable.FUNC_ARRAY_SIZE));
         m_functionID.put(Keywords.FUNC_ARRAY_GET,
                          new Integer(FunctionTable.FUNC_ARRAY_GET));
         m_functionID.put(Keywords.FUNC_ARRAY_PUT,
                          new Integer(FunctionTable.FUNC_ARRAY_PUT));
         m_functionID.put(Keywords.FUNC_ARRAY_APPEND,
                          new Integer(FunctionTable.FUNC_ARRAY_APPEND));
         m_functionID.put(Keywords.FUNC_ARRAY_SUBARRAY,
                          new Integer(FunctionTable.FUNC_ARRAY_SUBARRAY));
         m_functionID.put(Keywords.FUNC_ARRAY_REMOVE,
                          new Integer(FunctionTable.FUNC_ARRAY_REMOVE));
         m_functionID.put(Keywords.FUNC_ARRAY_INSERT_BEFORE,
                          new Integer(FunctionTable.FUNC_ARRAY_INSERT_BEFORE));
         m_functionID.put(Keywords.FUNC_ARRAY_HEAD,
                          new Integer(FunctionTable.FUNC_ARRAY_HEAD));
         m_functionID.put(Keywords.FUNC_ARRAY_TAIL,
                          new Integer(FunctionTable.FUNC_ARRAY_TAIL));
         m_functionID.put(Keywords.FUNC_ARRAY_REVERSE,
                          new Integer(FunctionTable.FUNC_ARRAY_REVERSE));
         m_functionID.put(Keywords.FUNC_ARRAY_JOIN,
                          new Integer(FunctionTable.FUNC_ARRAY_JOIN));
         m_functionID.put(Keywords.FUNC_ARRAY_FILTER,
                          new Integer(FunctionTable.FUNC_ARRAY_FILTER));
         m_functionID.put(Keywords.FUNC_ARRAY_FOR_EACH_PAIR,
                          new Integer(FunctionTable.FUNC_ARRAY_FOR_EACH_PAIR));
         m_functionID.put(Keywords.FUNC_ARRAY_FOLD_LEFT,
                          new Integer(FunctionTable.FUNC_ARRAY_FOLD_LEFT));
         m_functionID.put(Keywords.FUNC_ARRAY_FOLD_RIGHT,
                          new Integer(FunctionTable.FUNC_ARRAY_FOLD_RIGHT));
         m_functionID.put(Keywords.FUNC_ARRAY_SORT,
                          new Integer(FunctionTable.FUNC_ARRAY_SORT));
         
         m_functionID.put(Keywords.FUNC_NAME_STRING,
                          new Integer(FunctionTable.FUNC_NAME));
         
         m_functionID.put(Keywords.FUNC_RESOLVE_QNAME,
                          new Integer(FunctionTable.FUNC_RESOLVE_QNAME));
         m_functionID.put(Keywords.FUNC_QNAME,
                          new Integer(FunctionTable.FUNC_QNAME));
         m_functionID.put(Keywords.FUNC_PREFIX_FROM_QNAME,
                          new Integer(FunctionTable.FUNC_PREFIX_FROM_QNAME));
         m_functionID.put(Keywords.FUNC_LOCAL_NAME_FROM_QNAME,
                          new Integer(FunctionTable.FUNC_LOCAL_NAME_FROM_QNAME));
         m_functionID.put(Keywords.FUNC_NAMESPACE_URI_FROM_QNAME,
                          new Integer(FunctionTable.FUNC_NAMESPACE_URI_FROM_QNAME));
         m_functionID.put(Keywords.FUNC_NAMESPACE_URI_FOR_PREFIX,
                          new Integer(FunctionTable.FUNC_NAMESPACE_URI_FOR_PREFIX));
         m_functionID.put(Keywords.FUNC_IN_SCOPE_PREFIXES,
                          new Integer(FunctionTable.FUNC_IN_SCOPE_PREFIXES));
         
         m_functionID.put(Keywords.FUNC_PARSE_JSON,
                          new Integer(FunctionTable.FUNC_PARSE_JSON));
         m_functionID.put(Keywords.FUNC_JSON_TO_XML,
                          new Integer(FunctionTable.FUNC_JSON_TO_XML));
  }
  
  public FunctionTable(){
  }
  
  /**
   * Return the name of the a function in the static table. Needed to avoid
   * making the table publicly available.
   */
  String getFunctionName(int funcID) {
      if (funcID < NUM_BUILT_IN_FUNCS) return m_functions[funcID].getName();
      else return m_functions_customer[funcID - NUM_BUILT_IN_FUNCS].getName();
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
  Function getFunction(int which)
          throws javax.xml.transform.TransformerException
  {
          try{
              if (which < NUM_BUILT_IN_FUNCS) 
                  return (Function) m_functions[which].newInstance();
              else 
                  return (Function) m_functions_customer[
                      which-NUM_BUILT_IN_FUNCS].newInstance();                  
          }catch (IllegalAccessException ex){
                  throw new TransformerException(ex.getMessage());
          }catch (InstantiationException ex){
                  throw new TransformerException(ex.getMessage());
          }
  }
  
  /**
   * Obtain a function ID from a given function name
   * @param key the function name in a java.lang.String format.
   * @return a function ID, which may correspond to one of the FUNC_XXX values
   * found in {@link org.apache.xpath.compiler.FunctionTable}, but may be a 
   * value installed by an external module.
   */
  Object getFunctionID(String key){
          Object id = m_functionID_customer.get(key);
          if (null == id) id = m_functionID.get(key);
          return id;
  }
  
  /**
   * Install a built-in function.
   * @param name The unqualified name of the function, must not be null
   * @param func A Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public int installFunction(String name, Class func)
  {

    int funcIndex;
    Object funcIndexObj = getFunctionID(name);

    if (null != funcIndexObj)
    {
      funcIndex = ((Integer) funcIndexObj).intValue();
      
      if (funcIndex < NUM_BUILT_IN_FUNCS){
              funcIndex = m_funcNextFreeIndex++;
              m_functionID_customer.put(name, new Integer(funcIndex)); 
      }
      m_functions_customer[funcIndex - NUM_BUILT_IN_FUNCS] = func;          
    }
    else
    {
            funcIndex = m_funcNextFreeIndex++;
                          
            m_functions_customer[funcIndex-NUM_BUILT_IN_FUNCS] = func;
                    
            m_functionID_customer.put(name, 
                new Integer(funcIndex));   
    }
    return funcIndex;
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
      Object tblEntry = m_functionID.get(methName);
      if (null != tblEntry) return true;
      else{
              tblEntry = m_functionID_customer.get(methName);
              return (null != tblEntry)? true : false;
      }
  }
  
}
