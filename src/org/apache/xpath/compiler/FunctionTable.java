package org.apache.xpath.compiler;

import org.apache.xpath.Expression;
import org.apache.xpath.functions.Function;

/**
 * The function table for XPath.
 */
public class FunctionTable
{
  public static final int FUNC_CURRENT = 0;
  public static final int FUNC_LAST = 1;
  public static final int FUNC_POSITION = 2;
  public static final int FUNC_COUNT = 3;
  public static final int FUNC_ID = 4;
  public static final int FUNC_KEY = 5;
  // public static final int FUNC_DOC = 6;
  public static final int FUNC_LOCAL_PART = 7;
  public static final int FUNC_NAMESPACE = 8;
  public static final int FUNC_QNAME = 9;
  public static final int FUNC_GENERATE_ID = 10;
  public static final int FUNC_NOT = 11;
  public static final int FUNC_TRUE = 12;
  public static final int FUNC_FALSE = 13;
  public static final int FUNC_BOOLEAN = 14;
  public static final int FUNC_NUMBER = 15;
  public static final int FUNC_FLOOR = 16;
  public static final int FUNC_CEILING = 17;
  public static final int FUNC_ROUND = 18;
  public static final int FUNC_SUM = 19;
  public static final int FUNC_STRING = 20;
  public static final int FUNC_STARTS_WITH = 21;
  public static final int FUNC_CONTAINS = 22;
  public static final int FUNC_SUBSTRING_BEFORE = 23;
  public static final int FUNC_SUBSTRING_AFTER = 24;
  public static final int FUNC_NORMALIZE_SPACE = 25;
  public static final int FUNC_TRANSLATE = 26;
  public static final int FUNC_CONCAT = 27;
 // public static final int FUNC_FORMAT_NUMBER = 28;
  public static final int FUNC_SUBSTRING = 29;
  public static final int FUNC_STRING_LENGTH = 30;
  public static final int FUNC_SYSTEM_PROPERTY = 31;
  public static final int FUNC_LANG = 32;
  public static final int FUNC_EXT_FUNCTION_AVAILABLE = 33;
  public static final int FUNC_EXT_ELEM_AVAILABLE = 34;
    
  // Proprietary
  public static final int FUNC_DOCLOCATION = 35;

  public static final int FUNC_UNPARSED_ENTITY_URI = 36;

  /**
   * The function table.
   */
  public static FuncLoader m_functions[];
    
  /**
   * Number of built in functions.  Be sure to update this as 
   * built-in functions are added.
   */
  private static final int NUM_BUILT_IN_FUNCS = 37;

  /**
   * Number of built-in functions that may be added.
   */
  private static final int NUM_ALLOWABLE_ADDINS = 30;
  
  /**
   * The index to the next free function index.
   */
  static int m_funcNextFreeIndex = NUM_BUILT_IN_FUNCS;
  
  static
  {
    m_functions = new FuncLoader[NUM_BUILT_IN_FUNCS+NUM_ALLOWABLE_ADDINS];
    m_functions[FUNC_CURRENT] = new FuncLoader("FuncCurrent", FUNC_CURRENT);
    m_functions[FUNC_LAST] = new FuncLoader("FuncLast", FUNC_LAST);
    m_functions[FUNC_POSITION] = new FuncLoader("FuncPosition", FUNC_POSITION);
    m_functions[FUNC_COUNT] = new FuncLoader("FuncCount", FUNC_COUNT);
    m_functions[FUNC_ID] = new FuncLoader("FuncId", FUNC_ID);
    m_functions[FUNC_KEY] = new FuncLoader("org.apache.xalan.templates.FuncKey", FUNC_KEY);
    // m_functions[FUNC_DOC] = new FuncDoc();
    m_functions[FUNC_LOCAL_PART] = new FuncLoader("FuncLocalPart", FUNC_LOCAL_PART);
    m_functions[FUNC_NAMESPACE] = new FuncLoader("FuncNamespace", FUNC_NAMESPACE);
    m_functions[FUNC_QNAME] = new FuncLoader("FuncQname", FUNC_QNAME);
    m_functions[FUNC_GENERATE_ID] = new FuncLoader("FuncGenerateId", FUNC_GENERATE_ID);
    m_functions[FUNC_NOT] = new FuncLoader("FuncNot", FUNC_NOT);
    m_functions[FUNC_TRUE] = new FuncLoader("FuncTrue", FUNC_TRUE);
    m_functions[FUNC_FALSE] = new FuncLoader("FuncFalse", FUNC_FALSE);
    m_functions[FUNC_BOOLEAN] = new FuncLoader("FuncBoolean", FUNC_BOOLEAN);
    m_functions[FUNC_LANG] = new FuncLoader("FuncLang", FUNC_LANG);
    m_functions[FUNC_NUMBER] = new FuncLoader("FuncNumber", FUNC_NUMBER);
    m_functions[FUNC_FLOOR] = new FuncLoader("FuncFloor", FUNC_FLOOR);
    m_functions[FUNC_CEILING] = new FuncLoader("FuncCeiling", FUNC_CEILING);
    m_functions[FUNC_ROUND] = new FuncLoader("FuncRound", FUNC_ROUND);
    m_functions[FUNC_SUM] = new FuncLoader("FuncSum", FUNC_SUM);
    m_functions[FUNC_STRING] = new FuncLoader("FuncString", FUNC_STRING);
    m_functions[FUNC_STARTS_WITH] = new FuncLoader("FuncStartsWith", FUNC_STARTS_WITH);
    m_functions[FUNC_CONTAINS] = new FuncLoader("FuncContains", FUNC_CONTAINS);
    m_functions[FUNC_SUBSTRING_BEFORE] = new FuncLoader("FuncSubstringBefore", FUNC_SUBSTRING_BEFORE);
    m_functions[FUNC_SUBSTRING_AFTER] = new FuncLoader("FuncSubstringAfter", FUNC_SUBSTRING_AFTER);
    m_functions[FUNC_NORMALIZE_SPACE] = new FuncLoader("FuncNormalizeSpace", FUNC_NORMALIZE_SPACE);
    m_functions[FUNC_TRANSLATE] = new FuncLoader("FuncTranslate", FUNC_TRANSLATE);
    m_functions[FUNC_CONCAT] = new FuncLoader("FuncConcat", FUNC_CONCAT);
    //m_functions[FUNC_FORMAT_NUMBER] = new FuncFormatNumber();
    m_functions[FUNC_SYSTEM_PROPERTY] = new FuncLoader("FuncSystemProperty", FUNC_SYSTEM_PROPERTY);
    m_functions[FUNC_EXT_FUNCTION_AVAILABLE] = new FuncLoader("FuncExtFunctionAvailable", FUNC_EXT_FUNCTION_AVAILABLE);
    m_functions[FUNC_EXT_ELEM_AVAILABLE] = new FuncLoader("FuncExtElementAvailable", FUNC_EXT_ELEM_AVAILABLE);
    m_functions[FUNC_SUBSTRING] = new FuncLoader("FuncSubstring", FUNC_SUBSTRING);
    m_functions[FUNC_STRING_LENGTH] = new FuncLoader("FuncStringLength", FUNC_STRING_LENGTH);
    m_functions[FUNC_DOCLOCATION] = new FuncLoader("FuncDoclocation", FUNC_DOCLOCATION);
    m_functions[FUNC_UNPARSED_ENTITY_URI] = new FuncLoader("FuncUnparsedEntityURI", FUNC_UNPARSED_ENTITY_URI);
  }
  
  public static Function getFunction(int which) 
    throws org.xml.sax.SAXException
  {
    return m_functions[which].getFunction();
  }

  /**
   * Install a built-in function.
   * @param name The unqualified name of the function.
   * @param func A Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public static int installFunction(String name, Expression func)
  {   
    int funcIndex;
    Object funcIndexObj = Keywords.m_functions.get(name);
    if(null != funcIndexObj)
    {
      funcIndex = ((Integer)funcIndexObj).intValue();
    }
    else
    {
      funcIndex = m_funcNextFreeIndex;
      m_funcNextFreeIndex++;
      Keywords.m_functions.put(name, new Integer(funcIndex));
    }
    FuncLoader loader 
      = new FuncLoader(func.getClass().getName(), funcIndex);

    m_functions[funcIndex] = loader;
    return funcIndex;
  }
  
  /**
   * Install a built-in function at a specific index.
   * @param name The unqualified name of the function.
   * @param func A Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public static void installFunction(Expression func, int funcIndex)
  {
    FuncLoader loader 
      = new FuncLoader(func.getClass().getName(), funcIndex);

    m_functions[funcIndex] = loader;
  }
}
