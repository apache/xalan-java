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
 *     the documentation and/or other materials provided with the
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
package org.apache.xalan.xpath;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xpath.res.XPATHErrorResources;

/**
 * Class for XPath diagnostic functions.
 */
public class XPathDumper
{
  /**
   * Dump an XPath string to System.out.
   */
  public static void diagnoseXPathString( String str )
    throws org.xml.sax.SAXException
  {
    XPathParser processor = new XPathParser();
    XPath xpath = new XPath();
    processor.initXPath(xpath, str, null);
    diagnoseXPath(xpath, 0, 0);
  }

  static int diagnoseXPathBinaryOperation(String op, XPath xpath, int opPos, int indent)
  {
    System.out.println(op+" {");
    opPos+=2;

    opPos = diagnoseXPath(xpath, opPos, indent+1);

    opPos = diagnoseXPath(xpath, opPos, indent+1);

    indent(indent);
    System.out.println("}");
    return opPos;
  }

  static int diagnoseXPathUnaryOperation(String op, XPath xpath, int opPos, int indent)
  {
    System.out.println(op+" {");
    opPos+=2;
    opPos = diagnoseXPath(xpath, opPos, indent+1);
    indent(indent);
    System.out.println("}");
    return opPos;
  }

  private static int diagnoseXPathMultiOperation(String op, int multiOp, XPath xpath, int opPos, int indent)
  {
    System.out.println(op+" {");
    opPos+=2;
    while(xpath.m_opMap[opPos] == multiOp)
    {
      indent(indent+1);
      System.out.println("{");
      opPos = diagnoseXPath(xpath, opPos, indent+2);
      indent(indent+1);
      System.out.println("}");
    }
    indent(indent);
    System.out.println("}");
    return opPos;
  }

  private static int diagnoseToken(XPath xpath, int opPos)
  {
    System.out.print("{");
    System.out.print(xpath.m_tokenQueue[xpath.m_opMap[opPos]]);
    System.out.print("}");
    return opPos+1;
  }

  private static int diagnoseXPathSimpleOperation(String op, XPath xpath, int opPos, int indent)
  {
    opPos+=2;
    System.out.print(op);
    opPos = diagnoseToken(xpath, opPos);
    System.out.println("");
    return opPos;
  }

  private static int diagnoseXPathLocationStep(String op, XPath xpath, int opPos, int indent)
  {
    // int opLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH];
    int stepLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH+1];
    opPos+=3;
    System.out.print(op);
    if(stepLen > 3)
    {
      opPos = diagnoseXPath(xpath, opPos, 1);
    }
    System.out.println("");
    return opPos;
  }

  static int diagnoseXPath(XPath xpath, int opPos, int indent)
  {
    indent(indent);
    switch(xpath.m_opMap[opPos])
    {
    case OpCodes.OP_XPATH:
      opPos = diagnoseXPathUnaryOperation("OP_XPATH", xpath, opPos, indent);
      break;
    case OpCodes.EMPTY:
      System.out.println("{EMPTY}");
      opPos++;
      break;
    case OpCodes.OP_OR:
      opPos = diagnoseXPathBinaryOperation("OP_OR", xpath, opPos, indent);
      break;
    case OpCodes.OP_AND:
      opPos = diagnoseXPathBinaryOperation("OP_AND", xpath, opPos, indent);
      break;
    case OpCodes.OP_NOTEQUALS:
      opPos = diagnoseXPathBinaryOperation("OP_NOTEQUALS", xpath, opPos, indent);
      break;
    case OpCodes.OP_EQUALS:
      opPos = diagnoseXPathBinaryOperation("OP_EQUALS", xpath, opPos, indent);
      break;
    case OpCodes.OP_LTE:
      opPos = diagnoseXPathBinaryOperation("OP_LTE", xpath, opPos, indent);
      break;
    case OpCodes.OP_LT:
      opPos = diagnoseXPathBinaryOperation("OP_LT", xpath, opPos, indent);
      break;
    case OpCodes.OP_GTE:
      opPos = diagnoseXPathBinaryOperation("OP_GTE", xpath, opPos, indent);
      break;
    case OpCodes.OP_GT:
      opPos = diagnoseXPathBinaryOperation("OP_GT", xpath, opPos, indent);
      break;
    case OpCodes.OP_PLUS:
      opPos = diagnoseXPathBinaryOperation("OP_PLUS", xpath, opPos, indent);
      break;
    case OpCodes.OP_MINUS:
      opPos = diagnoseXPathBinaryOperation("OP_MINUS", xpath, opPos, indent);
      break;
    case OpCodes.OP_MULT:
      opPos = diagnoseXPathBinaryOperation("OP_MULT", xpath, opPos, indent);
      break;
    case OpCodes.OP_DIV:
      opPos = diagnoseXPathBinaryOperation("OP_DIV", xpath, opPos, indent);
      break;
    case OpCodes.OP_MOD:
      opPos = diagnoseXPathBinaryOperation("OP_MOD", xpath, opPos, indent);
      break;
    case OpCodes.OP_QUO:
      opPos = diagnoseXPathBinaryOperation("OP_QUO", xpath, opPos, indent);
      break;
    case OpCodes.OP_NEG:
      opPos = diagnoseXPathUnaryOperation("OP_NEG", xpath, opPos, indent);
      break;
    case OpCodes.OP_STRING:
      opPos = diagnoseXPathUnaryOperation("OP_STRING", xpath, opPos, indent);
      break;
    case OpCodes.OP_BOOL:
      opPos = diagnoseXPathUnaryOperation("OP_BOOL", xpath, opPos, indent);
      break;
    case OpCodes.OP_NUMBER:
      opPos = diagnoseXPathUnaryOperation("OP_NUMBER", xpath, opPos, indent);
      break;
    case OpCodes.OP_UNION:
      opPos = diagnoseXPathMultiOperation("OP_UNION", OpCodes.OP_LOCATIONPATH, xpath, opPos, indent);
      break;
    case OpCodes.OP_LITERAL:
      opPos = diagnoseXPathSimpleOperation("OP_LITERAL", xpath, opPos, indent);
      break;
    case OpCodes.OP_VARIABLE:
      opPos = diagnoseXPathSimpleOperation("OP_VARIABLE", xpath, opPos, indent);
      break;
    case OpCodes.OP_GROUP:
      opPos = diagnoseXPathUnaryOperation("OP_GROUP", xpath, opPos, indent);
      break;
    case OpCodes.OP_NUMBERLIT:
      opPos = diagnoseXPathSimpleOperation("OP_NUMBERLIT", xpath, opPos, indent);
      break;
    case OpCodes.OP_ARGUMENT:
      opPos = diagnoseXPathUnaryOperation("OP_ARGUMENT", xpath, opPos, indent);
      break;
    case OpCodes.OP_EXTFUNCTION:
      {
        System.out.println("OP_EXTFUNCTION {");
        int endExtFunc = opPos+xpath.m_opMap[opPos+1]-1;
        opPos+=2;
        indent(indent+1);
        opPos = diagnoseToken(xpath, opPos);
        System.out.print(":");
        opPos = diagnoseToken(xpath, opPos);
        System.out.println("");
        while(opPos < endExtFunc)
        {
          indent(indent+1);
          System.out.println("{");
          opPos = diagnoseXPath(xpath, opPos, indent+2);
          indent(indent+1);
          System.out.println("}");
        }
        indent(indent);
        System.out.println("}");
        if(xpath.m_opMap[opPos] != OpCodes.ENDOP)
        {
          System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
        }
        opPos++;
      }
      break;
    case OpCodes.OP_FUNCTION:
      {
        System.out.println("OP_FUNCTION {");
        int endFunc = opPos+xpath.m_opMap[opPos+1]-1;
        opPos+=2;
        indent(indent+1);
        int funcID = xpath.m_opMap[opPos];
        switch(funcID)
        {
        case FunctionTable.FUNC_LAST: System.out.print("FUNC_LAST"); break;
        case FunctionTable.FUNC_POSITION: System.out.print("FUNC_POSITION"); break;
        case FunctionTable.FUNC_COUNT: System.out.print("FUNC_COUNT"); break;
        case FunctionTable.FUNC_ID: System.out.print("FUNC_ID"); break;
        case FunctionTable.FUNC_KEY: System.out.print("FUNC_KEY"); break;
        // case FunctionTable.FUNC_DOC: System.out.print("FUNC_DOC"); break;
        case FunctionTable.FUNC_LOCAL_PART: System.out.print("FUNC_LOCAL_PART"); break;
        case FunctionTable.FUNC_NAMESPACE: System.out.print("FUNC_NAMESPACE"); break;
        case FunctionTable.FUNC_QNAME: System.out.print("FUNC_QNAME"); break;
        case FunctionTable.FUNC_GENERATE_ID: System.out.print("FUNC_GENERATE_ID"); break;
        case FunctionTable.FUNC_NOT: System.out.print("FUNC_NOT"); break;
        case FunctionTable.FUNC_TRUE: System.out.print("FUNC_TRUE"); break;
        case FunctionTable.FUNC_FALSE: System.out.print("FUNC_FALSE"); break;
        case FunctionTable.FUNC_BOOLEAN: System.out.print("FUNC_BOOLEAN"); break;
        case FunctionTable.FUNC_LANG: System.out.print("FUNC_LANG"); break;
        case FunctionTable.FUNC_NUMBER: System.out.print("FUNC_NUMBER"); break;
        case FunctionTable.FUNC_FLOOR: System.out.print("FUNC_FLOOR"); break;
        case FunctionTable.FUNC_CEILING: System.out.print("FUNC_CEILING"); break;
        case FunctionTable.FUNC_ROUND: System.out.print("FUNC_ROUND"); break;
        case FunctionTable.FUNC_SUM: System.out.print("FUNC_SUM"); break;
        case FunctionTable.FUNC_STRING: System.out.print("FUNC_STRING"); break;
        case FunctionTable.FUNC_STARTS_WITH: System.out.print("FUNC_STARTS_WITH"); break;
        case FunctionTable.FUNC_CONTAINS: System.out.print("FUNC_CONTAINS"); break;
        case FunctionTable.FUNC_SUBSTRING_BEFORE: System.out.print("FUNC_SUBSTRING_BEFORE"); break;
        case FunctionTable.FUNC_SUBSTRING_AFTER: System.out.print("FUNC_SUBSTRING_AFTER"); break;
        case FunctionTable.FUNC_NORMALIZE_SPACE: System.out.print("FUNC_NORMALIZE_SPACE"); break;
        case FunctionTable.FUNC_TRANSLATE: System.out.print("FUNC_TRANSLATE"); break;
        case FunctionTable.FUNC_CONCAT: System.out.print("FUNC_CONCAT"); break;
        //case FunctionTable.FUNC_FORMAT_NUMBER: System.out.print("FUNC_FORMAT_NUMBER"); break;
        case FunctionTable.FUNC_SYSTEM_PROPERTY: System.out.print("FUNC_SYSTEM_PROPERTY"); break;
        case FunctionTable.FUNC_EXT_FUNCTION_AVAILABLE: System.out.print("FUNC_EXT_FUNCTION_AVAILABLE"); break;
        case FunctionTable.FUNC_EXT_ELEM_AVAILABLE: System.out.print("FUNC_EXT_ELEM_AVAILABLE"); break;
        case FunctionTable.FUNC_SUBSTRING: System.out.print("FUNC_SUBSTRING"); break;
        case FunctionTable.FUNC_STRING_LENGTH: System.out.print("FUNC_STRING_LENGTH"); break;
        case FunctionTable.FUNC_DOCLOCATION: System.out.print("FUNC_DOCLOCATION"); break;
        }
        opPos++;
        System.out.println("");
        while(opPos < endFunc)
        {
          indent(indent+1);
          System.out.println("{");
          opPos = diagnoseXPath(xpath, opPos, indent+2);
          indent(indent+1);
          System.out.println("}");
        }
        indent(indent);
        System.out.println("}");
        if(xpath.m_opMap[opPos] != OpCodes.ENDOP)
        {
          System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
        }
        opPos++;
      }
      break;
    case OpCodes.OP_LOCATIONPATH:
    case OpCodes.OP_LOCATIONPATH_EX:
      System.out.println("OP_LOCATIONPATH"+" {");
      int endPath = opPos+xpath.m_opMap[opPos+1]-1;
      opPos+=2;
      while(opPos < endPath)
      {
        opPos = diagnoseXPath(xpath, opPos, indent+1);
      }
      indent(indent);
      System.out.println("}");
      if(xpath.m_opMap[opPos] != OpCodes.ENDOP)
      {
        System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
      }
      opPos++;
      break;
    case OpCodes.OP_PREDICATE:
      opPos = diagnoseXPathUnaryOperation("OP_PREDICATE", xpath, opPos, indent);
      if(xpath.m_opMap[opPos] != OpCodes.ENDOP)
      {
        System.out.println("ERROR! Could not find ENDOP after OP_LOCATIONPATH");
      }
      opPos++;
      break;
    case OpCodes.FROM_ANCESTORS:
      opPos = diagnoseXPathLocationStep("FROM_ANCESTORS", xpath, opPos, 1);
      break;
    case OpCodes.FROM_ANCESTORS_OR_SELF:
      opPos = diagnoseXPathLocationStep("FROM_ANCESTORS_OR_SELF", xpath, opPos, 1);
      break;
    case OpCodes.FROM_ATTRIBUTES:
      opPos = diagnoseXPathLocationStep("FROM_ATTRIBUTES", xpath, opPos, 1);
      break;
    case OpCodes.FROM_CHILDREN:
      opPos = diagnoseXPathLocationStep("FROM_CHILDREN", xpath, opPos, 1);
      break;
    case OpCodes.FROM_DESCENDANTS:
      opPos = diagnoseXPathLocationStep("FROM_DESCENDANTS", xpath, opPos, 1);
      break;
    case OpCodes.FROM_DESCENDANTS_OR_SELF:
      opPos = diagnoseXPathLocationStep("FROM_DESCENDANTS_OR_SELF", xpath, opPos, 1);
      break;
    case OpCodes.FROM_FOLLOWING:
      opPos = diagnoseXPathLocationStep("FROM_FOLLOWING", xpath, opPos, indent);
      break;
    case OpCodes.FROM_FOLLOWING_SIBLINGS:
      opPos = diagnoseXPathLocationStep("FROM_FOLLOWING_SIBLINGS", xpath, opPos, indent);
      break;
    case OpCodes.FROM_PARENT:
      opPos = diagnoseXPathLocationStep("FROM_PARENT", xpath, opPos, indent);
      break;
    case OpCodes.FROM_PRECEDING:
      opPos = diagnoseXPathLocationStep("FROM_PRECEDING", xpath, opPos, indent);
      break;
    case OpCodes.FROM_PRECEDING_SIBLINGS:
      opPos = diagnoseXPathLocationStep("FROM_PRECEDING_SIBLINGS", xpath, opPos, indent);
      break;
    case OpCodes.FROM_SELF:
      opPos = diagnoseXPathLocationStep("FROM_SELF", xpath, opPos, indent);
      break;
    case OpCodes.FROM_NAMESPACE:
      opPos = diagnoseXPathLocationStep("FROM_NAMESPACE", xpath, opPos, indent);
      break;
    // case OpCodes.FROM_ATTRIBUTE:
    //   opPos = diagnoseXPathLocationStep("FROM_ATTRIBUTE", xpath, opPos, indent);
    //  break;
    // case OpCodes.FROM_DOC:
    //  opPos = diagnoseXPathLocationStep("FROM_DOC", xpath, opPos, indent);
    //  break;
    // case OpCodes.FROM_DOCREF:
    //  opPos = diagnoseXPathLocationStep("FROM_DOCREF", xpath, opPos, indent);
    //  break;
    // case OpCodes.FROM_ID:
    //  opPos = diagnoseXPathLocationStep("FROM_ID", xpath, opPos, indent);
    //  break;
    // case OpCodes.FROM_IDREF:
    //  opPos = diagnoseXPathLocationStep("FROM_IDREF", xpath, opPos, indent);
    //  break;
    case OpCodes.FROM_ROOT:
      opPos = diagnoseXPathLocationStep("FROM_ROOT", xpath, opPos, indent);
      break;
    case OpCodes.NODETYPE_COMMENT:
      System.out.println("{NODETYPE_COMMENT}");
      opPos++;
      break;
    case OpCodes.NODETYPE_TEXT:
      System.out.println("{NODETYPE_TEXT}");
      opPos++;
      break;
    case OpCodes.NODETYPE_PI:
      int piLen = xpath.m_opMap[opPos-1];
      System.out.println("{NODETYPE_PI ");
      opPos++;
      if(piLen > 3)
      {
        opPos = diagnoseToken(xpath, opPos);
      }
      break;
    case OpCodes.NODETYPE_NODE:
      System.out.println("{NODETYPE_NODE}");
      opPos++;
      break;
    case OpCodes.NODETYPE_ROOT:
      System.out.println("{NODETYPE_ROOT}");
      opPos++;
      break;
    case OpCodes.NODETYPE_ANYELEMENT:
      System.out.println("{NODETYPE_ANYELEMENT}");
      opPos++;
      break;
    case OpCodes.NODENAME:
      System.out.print("{NODENAME ");
      opPos++;
      if(xpath.m_opMap[opPos] < 0)
      {
        System.out.print("{EMPTY}");
        opPos++;
      }
      else
      {
        opPos = diagnoseToken(xpath, opPos);
      }
      System.out.print(":");
      opPos = diagnoseToken(xpath, opPos);
      break;
    default:
	  System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[] {Integer.toString(xpath.m_opMap[opPos])})); //"ERROR! Unknown op code: "+xpath.m_opMap[opPos]);
    }
    return opPos;
  }

  static void indent(int amount)
  {
    int n = amount * 3;
    for(int i = 0;  i < n; i ++)
    {
      System.out.print(" ");
    }
  }

  private static String m_opLabel     = "[";
  private static String m_lenLabel    = "[";
  private static String m_arglenLabel = "[";
  private static String m_noLabel     = "[";
  private static String m_nTestLabel  = "[";
  private static String m_open = "[";
  private static String m_close = "]";

  /**
   * Dump an XPath string to System.out.
   */
  public static void diagnoseXPathString2( String str )
    throws org.xml.sax.SAXException
  {
    XPathParser processor = new XPathParser();
    XPath xpath = new XPath();
    processor.initXPath(xpath, str, null);
    diagnoseXPath2(xpath, 0, 0);
  }

  /**
   * Dump an XPath string to System.out.
   */
  public static void diagnoseXPathString3( String str )
    throws org.xml.sax.SAXException
  {
    XPathParser processor = new XPathParser();
    XPath xpath = new XPath();
    processor.initXPath(xpath, str, null);
    int len = xpath.m_opMap[xpath.MAPINDEX_LENGTH];
    for(int i = 0; i < len; i++)
    {
      System.out.println("["+xpath.m_opMap[i]+"]");
    }
  }

  private static void diagnoseNodeTest2(int opPos, String op)
  {
    System.out.print(m_nTestLabel+op+m_close);
  }

  private static void diagnoseOpNoLable2(int opPos, String op)
  {
    System.out.println(m_noLabel+op+m_close);
  }

  private static void diagnoseOpOnly2(int opPos, String op)
  {
    System.out.println(m_opLabel+op+m_close);
  }

  private static void diagnoseOp2(String op, XPath xpath, int opPos)
  {
    System.out.print(m_opLabel+op+m_close);
    int opLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH];
    System.out.println(m_open+opLen+m_close);
  }

  private static void diagnoseOp2SameLine(String op, XPath xpath, int opPos)
  {
    System.out.print(m_opLabel+op+m_close);
    int opLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH];
    System.out.print(m_open+opLen+m_close);
  }

  private static int diagnoseXPathBinaryOperation2(String op, XPath xpath, int opPos, int indent)
  {
    diagnoseOp2(op, xpath, opPos);
    opPos+=2;

    opPos = diagnoseXPath2(xpath, opPos, indent+1);

    opPos = diagnoseXPath2(xpath, opPos, indent+1);

    return opPos;
  }

  private static int diagnoseXPathUnaryOperation2(String op, XPath xpath, int opPos, int indent)
  {
    diagnoseOp2(op, xpath, opPos);
    opPos+=2;
    opPos = diagnoseXPath2(xpath, opPos, indent+1);
    return opPos;
  }

  private static int diagnoseXPathMultiOperation2(String op, int multiOp, XPath xpath, int opPos, int indent)
  {
    diagnoseOp2(op, xpath, opPos);
    opPos+=2;
    while(xpath.m_opMap[opPos] == multiOp)
    {
      opPos = diagnoseXPath2(xpath, opPos, indent+2);
    }
    return opPos;
  }

  private static int diagnoseToken2(XPath xpath, int opPos)
  {
    int tokenPos = xpath.m_opMap[opPos];
    String token = (tokenPos >= 0) ? xpath.m_tokenQueue[tokenPos].toString() :
                                     (tokenPos == OpCodes.ELEMWILDCARD) ?
                                     "*" : (tokenPos == OpCodes.EMPTY) ?
                                           "EMPTY" : "UNKNOWN";
    System.out.println(m_noLabel+token+m_close);
    return opPos+1;
  }

  private static int diagnoseToken2SameLine(XPath xpath, int opPos)
  {
    System.out.print(m_noLabel+xpath.m_tokenQueue[xpath.m_opMap[opPos]]+m_close);
    return opPos+1;
  }

  private static int diagnoseXPathSimpleOperation2(String op, XPath xpath, int opPos, int indent)
  {
    diagnoseOp2SameLine(op, xpath, opPos);
    opPos+=2;
    opPos = diagnoseToken2(xpath, opPos);
    return opPos;
  }

  private static int diagnoseXPathLocationStep2(String op, XPath xpath, int opPos, int indent)
  {
    int opLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH];
    int stepLen = xpath.m_opMap[opPos+xpath.MAPINDEX_LENGTH+1];
    System.out.print(m_opLabel+op+m_close);
    System.out.print(m_open+opLen+m_close);
    System.out.print(m_open+stepLen+m_close);
    opPos+=3;
    if(stepLen > 3)
    {
      opPos = diagnoseXPath2(xpath, opPos, 0);
    }
    return opPos;
  }

  private static int diagnoseXPath2(XPath xpath, int opPos, int indent)
  {
    indent(indent);
    switch(xpath.m_opMap[opPos])
    {
    case OpCodes.OP_XPATH:
      opPos = diagnoseXPathUnaryOperation2("OP_XPATH", xpath, opPos, indent);
      break;
    case OpCodes.EMPTY:
      diagnoseOpOnly2(opPos, "EMPTY");
      opPos++;
      break;
    case OpCodes.OP_OR:
      opPos = diagnoseXPathBinaryOperation2("OP_OR", xpath, opPos, indent);
      break;
    case OpCodes.OP_AND:
      opPos = diagnoseXPathBinaryOperation2("OP_AND", xpath, opPos, indent);
      break;
    case OpCodes.OP_NOTEQUALS:
      opPos = diagnoseXPathBinaryOperation2("OP_NOTEQUALS", xpath, opPos, indent);
      break;
    case OpCodes.OP_EQUALS:
      opPos = diagnoseXPathBinaryOperation2("OP_EQUALS", xpath, opPos, indent);
      break;
    case OpCodes.OP_LTE:
      opPos = diagnoseXPathBinaryOperation2("OP_LTE", xpath, opPos, indent);
      break;
    case OpCodes.OP_LT:
      opPos = diagnoseXPathBinaryOperation2("OP_LT", xpath, opPos, indent);
      break;
    case OpCodes.OP_GTE:
      opPos = diagnoseXPathBinaryOperation2("OP_GTE", xpath, opPos, indent);
      break;
    case OpCodes.OP_GT:
      opPos = diagnoseXPathBinaryOperation2("OP_GT", xpath, opPos, indent);
      break;
    case OpCodes.OP_PLUS:
      opPos = diagnoseXPathBinaryOperation2("OP_PLUS", xpath, opPos, indent);
      break;
    case OpCodes.OP_MINUS:
      opPos = diagnoseXPathBinaryOperation2("OP_MINUS", xpath, opPos, indent);
      break;
    case OpCodes.OP_MULT:
      opPos = diagnoseXPathBinaryOperation2("OP_MULT", xpath, opPos, indent);
      break;
    case OpCodes.OP_DIV:
      opPos = diagnoseXPathBinaryOperation2("OP_DIV", xpath, opPos, indent);
      break;
    case OpCodes.OP_MOD:
      opPos = diagnoseXPathBinaryOperation2("OP_MOD", xpath, opPos, indent);
      break;
    case OpCodes.OP_QUO:
      opPos = diagnoseXPathBinaryOperation2("OP_QUO", xpath, opPos, indent);
      break;
    case OpCodes.OP_NEG:
      opPos = diagnoseXPathUnaryOperation2("OP_NEG", xpath, opPos, indent);
      break;
    case OpCodes.OP_STRING:
      opPos = diagnoseXPathUnaryOperation2("OP_STRING", xpath, opPos, indent);
      break;
    case OpCodes.OP_BOOL:
      opPos = diagnoseXPathUnaryOperation2("OP_BOOL", xpath, opPos, indent);
      break;
    case OpCodes.OP_NUMBER:
      opPos = diagnoseXPathUnaryOperation2("OP_NUMBER", xpath, opPos, indent);
      break;
    case OpCodes.OP_UNION:
      opPos = diagnoseXPathMultiOperation2("OP_UNION", OpCodes.OP_LOCATIONPATH, xpath, opPos, indent);
      break;
    case OpCodes.OP_LITERAL:
      opPos = diagnoseXPathSimpleOperation2("OP_LITERAL", xpath, opPos, indent);
      break;
    case OpCodes.OP_VARIABLE:
      opPos = diagnoseXPathSimpleOperation2("OP_VARIABLE", xpath, opPos, indent);
      break;
    case OpCodes.OP_GROUP:
      opPos = diagnoseXPathUnaryOperation2("OP_GROUP", xpath, opPos, indent);
      break;
    case OpCodes.OP_NUMBERLIT:
      opPos = diagnoseXPathSimpleOperation2("OP_NUMBERLIT", xpath, opPos, indent);
      break;
    case OpCodes.OP_ARGUMENT:
      opPos = diagnoseXPathUnaryOperation2("OP_ARGUMENT", xpath, opPos, indent);
      break;
    case OpCodes.OP_EXTFUNCTION:
      {
        diagnoseOp2SameLine("OP_EXTFUNCTION", xpath, opPos);
        int endExtFunc = opPos+xpath.m_opMap[opPos+1]-1;
        opPos+=2;
        opPos = diagnoseToken2SameLine(xpath, opPos);
        opPos = diagnoseToken2(xpath, opPos);
        while(opPos < endExtFunc)
        {
          opPos = diagnoseXPath2(xpath, opPos, indent+2);
        }
        if(xpath.m_opMap[opPos] != OpCodes.ENDOP)
        {
          System.out.println("ERROR! Could not find ENDOP after OP_LOCATIONPATH");
        }
        indent(indent+1);
        diagnoseOpOnly2(opPos, "ENDOP");
        opPos++;
      }
      break;
    case OpCodes.OP_FUNCTION:
      {
        diagnoseOp2SameLine("OP_FUNCTION", xpath, opPos);
        int endFunc = opPos+xpath.m_opMap[opPos+1]-1;
        opPos+=2;
        int funcID = xpath.m_opMap[opPos];
        switch(funcID)
        {
        case FunctionTable.FUNC_LAST: diagnoseOpNoLable2(opPos, "FUNC_LAST"); break;
        case FunctionTable.FUNC_POSITION: diagnoseOpNoLable2(opPos, "FUNC_POSITION"); break;
        case FunctionTable.FUNC_COUNT: diagnoseOpNoLable2(opPos, "FUNC_COUNT"); break;
        case FunctionTable.FUNC_ID: diagnoseOpNoLable2(opPos, "FUNC_ID"); break;
        case FunctionTable.FUNC_KEY: diagnoseOpNoLable2(opPos, "FUNC_KEY"); break;
        // case FunctionTable.FUNC_DOC: diagnoseOpNoLable2(opPos, "FUNC_DOC"); break;
        case FunctionTable.FUNC_LOCAL_PART: diagnoseOpNoLable2(opPos, "FUNC_LOCAL_PART"); break;
        case FunctionTable.FUNC_NAMESPACE: diagnoseOpNoLable2(opPos, "FUNC_NAMESPACE"); break;
        case FunctionTable.FUNC_QNAME: diagnoseOpNoLable2(opPos, "FUNC_QNAME"); break;
        case FunctionTable.FUNC_GENERATE_ID: diagnoseOpNoLable2(opPos, "FUNC_GENERATE_ID"); break;
        case FunctionTable.FUNC_NOT: diagnoseOpNoLable2(opPos, "FUNC_NOT"); break;
        case FunctionTable.FUNC_TRUE: diagnoseOpNoLable2(opPos, "FUNC_TRUE"); break;
        case FunctionTable.FUNC_FALSE: diagnoseOpNoLable2(opPos, "FUNC_FALSE"); break;
        case FunctionTable.FUNC_BOOLEAN: diagnoseOpNoLable2(opPos, "FUNC_BOOLEAN"); break;
        case FunctionTable.FUNC_LANG: diagnoseOpNoLable2(opPos, "FUNC_LANG"); break;
        case FunctionTable.FUNC_NUMBER: diagnoseOpNoLable2(opPos, "FUNC_NUMBER"); break;
        case FunctionTable.FUNC_FLOOR: diagnoseOpNoLable2(opPos, "FUNC_FLOOR"); break;
        case FunctionTable.FUNC_CEILING: diagnoseOpNoLable2(opPos, "FUNC_CEILING"); break;
        case FunctionTable.FUNC_ROUND: diagnoseOpNoLable2(opPos, "FUNC_ROUND"); break;
        case FunctionTable.FUNC_SUM: diagnoseOpNoLable2(opPos, "FUNC_SUM"); break;
        case FunctionTable.FUNC_STRING: diagnoseOpNoLable2(opPos, "FUNC_STRING"); break;
        case FunctionTable.FUNC_STARTS_WITH: diagnoseOpNoLable2(opPos, "FUNC_STARTS_WITH"); break;
        case FunctionTable.FUNC_CONTAINS: diagnoseOpNoLable2(opPos, "FUNC_CONTAINS"); break;
        case FunctionTable.FUNC_SUBSTRING_BEFORE: diagnoseOpNoLable2(opPos, "FUNC_SUBSTRING_BEFORE"); break;
        case FunctionTable.FUNC_SUBSTRING_AFTER: diagnoseOpNoLable2(opPos, "FUNC_SUBSTRING_AFTER"); break;
        case FunctionTable.FUNC_NORMALIZE_SPACE: diagnoseOpNoLable2(opPos, "FUNC_NORMALIZE_SPACE"); break;
        case FunctionTable.FUNC_TRANSLATE: diagnoseOpNoLable2(opPos, "FUNC_TRANSLATE"); break;
        case FunctionTable.FUNC_CONCAT: diagnoseOpNoLable2(opPos, "FUNC_CONCAT"); break;
        //case FunctionTable.FUNC_FORMAT_NUMBER: diagnoseOpNoLable2(opPos, "FUNC_FORMAT_NUMBER"); break;
        case FunctionTable.FUNC_SYSTEM_PROPERTY: diagnoseOpNoLable2(opPos, "FUNC_SYSTEM_PROPERTY"); break;
        case FunctionTable.FUNC_EXT_FUNCTION_AVAILABLE: diagnoseOpNoLable2(opPos, "FUNC_EXT_FUNCTION_AVAILABLE"); break;
        case FunctionTable.FUNC_EXT_ELEM_AVAILABLE: diagnoseOpNoLable2(opPos, "FUNC_EXT_ELEM_AVAILABLE"); break;
        case FunctionTable.FUNC_SUBSTRING: diagnoseOpNoLable2(opPos, "FUNC_SUBSTRING"); break;
        case FunctionTable.FUNC_STRING_LENGTH: diagnoseOpNoLable2(opPos, "FUNC_STRING_LENGTH"); break;
        case FunctionTable.FUNC_DOCLOCATION: diagnoseOpNoLable2(opPos, "FUNC_DOCLOCATION"); break;
        }
        opPos++;
        while(opPos < endFunc)
        {
          // indent(indent+1);
          opPos = diagnoseXPath2(xpath, opPos, indent+2);
        }
        indent(indent);
        if(xpath.m_opMap[opPos] != OpCodes.ENDOP)
        {
          System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
        }
        indent(indent+1);
        diagnoseOpOnly2(opPos, "ENDOP");
        opPos++;
      }
      break;
    case OpCodes.OP_LOCATIONPATH_EX:
    case OpCodes.OP_LOCATIONPATH:
      diagnoseOp2("OP_LOCATIONPATH", xpath, opPos);
      int endPath = opPos+xpath.m_opMap[opPos+1]-1;
      opPos+=2;
      while(opPos < endPath)
      {
        opPos = diagnoseXPath2(xpath, opPos, indent+1);
      }
      if(xpath.m_opMap[opPos] != OpCodes.ENDOP)
      {
        System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_COULDNOT_FIND_ENDOP_AFTER_OPLOCATIONPATH, null)); //"ERROR! Could not find ENDOP after OP_LOCATIONPATH");
      }
      indent(indent+1);
      diagnoseOpOnly2(opPos, "ENDOP");
      opPos++;
      break;
    case OpCodes.OP_PREDICATE:
      indent(1);
      opPos = diagnoseXPathUnaryOperation2("OP_PREDICATE", xpath, opPos, indent+1);
      if(xpath.m_opMap[opPos] != OpCodes.ENDOP)
      {
        System.out.println("ERROR! Could not find ENDOP after OP_LOCATIONPATH");
      }
      indent(indent+2);
      diagnoseOpOnly2(opPos, "ENDOP");
      opPos++;
      break;
    case OpCodes.FROM_ANCESTORS:
      opPos = diagnoseXPathLocationStep2("FROM_ANCESTORS", xpath, opPos, 1);
      break;
    case OpCodes.FROM_ANCESTORS_OR_SELF:
      opPos = diagnoseXPathLocationStep2("FROM_ANCESTORS_OR_SELF", xpath, opPos, 1);
      break;
    case OpCodes.FROM_ATTRIBUTES:
      opPos = diagnoseXPathLocationStep2("FROM_ATTRIBUTES", xpath, opPos, 1);
      break;
    case OpCodes.FROM_CHILDREN:
      opPos = diagnoseXPathLocationStep2("FROM_CHILDREN", xpath, opPos, 1);
      break;
    case OpCodes.FROM_DESCENDANTS:
      opPos = diagnoseXPathLocationStep2("FROM_DESCENDANTS", xpath, opPos, 1);
      break;
    case OpCodes.FROM_DESCENDANTS_OR_SELF:
      opPos = diagnoseXPathLocationStep2("FROM_DESCENDANTS_OR_SELF", xpath, opPos, 1);
      break;
    case OpCodes.FROM_FOLLOWING:
      opPos = diagnoseXPathLocationStep2("FROM_FOLLOWING", xpath, opPos, indent);
      break;
    case OpCodes.FROM_FOLLOWING_SIBLINGS:
      opPos = diagnoseXPathLocationStep2("FROM_FOLLOWING_SIBLINGS", xpath, opPos, indent);
      break;
    case OpCodes.FROM_PARENT:
      opPos = diagnoseXPathLocationStep2("FROM_PARENT", xpath, opPos, indent);
      break;
    case OpCodes.FROM_PRECEDING:
      opPos = diagnoseXPathLocationStep2("FROM_PRECEDING", xpath, opPos, indent);
      break;
    case OpCodes.FROM_PRECEDING_SIBLINGS:
      opPos = diagnoseXPathLocationStep2("FROM_PRECEDING_SIBLINGS", xpath, opPos, indent);
      break;
    case OpCodes.FROM_SELF:
      opPos = diagnoseXPathLocationStep2("FROM_SELF", xpath, opPos, indent);
      break;
    case OpCodes.FROM_NAMESPACE:
      opPos = diagnoseXPathLocationStep2("FROM_NAMESPACE", xpath, opPos, indent);
      break;
    // case OpCodes.FROM_ATTRIBUTE:
    //   opPos = diagnoseXPathLocationStep("FROM_ATTRIBUTE", xpath, opPos, indent);
    //  break;
    // case OpCodes.FROM_DOC:
    //  opPos = diagnoseXPathLocationStep("FROM_DOC", xpath, opPos, indent);
    //  break;
    // case OpCodes.FROM_DOCREF:
    //  opPos = diagnoseXPathLocationStep("FROM_DOCREF", xpath, opPos, indent);
    //  break;
    // case OpCodes.FROM_ID:
    //  opPos = diagnoseXPathLocationStep("FROM_ID", xpath, opPos, indent);
    //  break;
    // case OpCodes.FROM_IDREF:
    //  opPos = diagnoseXPathLocationStep("FROM_IDREF", xpath, opPos, indent);
    //  break;
    case OpCodes.FROM_ROOT:
      opPos = diagnoseXPathLocationStep2("FROM_ROOT", xpath, opPos, indent);
      // opPos++;
      break;
    case OpCodes.NODETYPE_COMMENT:
      diagnoseNodeTest2(opPos, "NODETYPE_COMMENT");
      System.out.println();
      opPos++;
      break;
    case OpCodes.NODETYPE_TEXT:
      diagnoseNodeTest2(opPos, "NODETYPE_TEXT");
      System.out.println();
      opPos++;
      break;
    case OpCodes.NODETYPE_PI:
      int piLen = xpath.m_opMap[opPos-1];
      diagnoseNodeTest2(opPos, "NODETYPE_PI");
      opPos++;
      if(piLen > 3)
      {
        opPos = diagnoseToken(xpath, opPos);
      }
      break;
    case OpCodes.NODETYPE_NODE:
      diagnoseNodeTest2(opPos, "NODETYPE_NODE");
      System.out.println();
      opPos++;
      break;
    case OpCodes.NODETYPE_ROOT:
      diagnoseNodeTest2(opPos, "NODETYPE_ROOT");
      System.out.println();
      opPos++;
      break;
    case OpCodes.NODETYPE_ANYELEMENT:
      diagnoseNodeTest2(opPos, "NODETYPE_ANYELEMENT");
      System.out.println();
      opPos++;
      break;
    case OpCodes.NODENAME:
      diagnoseNodeTest2(opPos, "NODENAME");
      opPos++;
      if(xpath.m_opMap[opPos] < 0)
      {
        System.out.print(m_noLabel+"EMPTY"+m_close);
        opPos++;
      }
      else
      {
        opPos = diagnoseToken2(xpath, opPos);
      }
      opPos = diagnoseToken2(xpath, opPos);
      break;
    default:
      System.out.println(XSLMessages.createXPATHMessage(XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[] {Integer.toString(xpath.m_opMap[opPos])})); //"ERROR! Unknown op code: "+xpath.m_opMap[opPos]);
    }
    return opPos;
  }
}
