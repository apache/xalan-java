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
package org.apache.xalan.xpath;
 
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.DocumentFragment;
import java.util.Vector;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;
import org.apache.xalan.utils.PrefixResolver;
import org.apache.xalan.utils.QName;
import org.apache.xalan.xpath.res.XPATHErrorResources;
import org.apache.xalan.xpath.functions.Function;
import org.apache.xalan.xpath.functions.FuncLoader;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.extensions.ExtensionsTable;
import org.xml.sax.ErrorHandler;
import trax.TransformException;

/** 
 * <meta name="usage" content="general"/>
 * The XPath class represents the semantic parse tree of the XPath pattern.
 * It is the representation of the grammar which filters out
 * the choice for replacement order of the production rules.
 * In order to conserve memory and reduce object creation, the 
 * tree is represented as an array of integers:
 *    [op code][length][...]
 * where strings are represented within the array as 
 * indexes into the token tree.
 */
public class XPath extends OpMap
  implements Serializable
{      
   
  /**
   * Construct an XPath object.  The object must be initialized by the 
   * XPathParser.initXPath method.
   */
  public XPath()
  {
  }
  
  /**
   * getXLocatorHandler.
   */
  private XLocator createXLocatorHandler(XPathContext callbacks)
  {
    return callbacks.createXLocatorHandler();
  }

  /**
   * <meta name="usage" content="experimental"/>
   * Given an expression and a context, evaluate the XPath 
   * and call the callback as nodes are found.  Only some simple 
   * types of expresions right now can call back, so if this 
   * method returns null, then the callbacks have been called, otherwise
   * a valid XObject will be returned.
   * @param xctxt The execution context.
   * @param contextNode The node that "." expresses.
   * @param namespaceContext The context in which namespaces in the 
   * XPath are supposed to be expanded.
   * @exception SAXException thrown if the active ProblemListener decides 
   * the error condition is severe enough to halt processing.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @param stopAtFirst True if the search should stop once the first node in document 
   * order is found.
   * @return The result of the XPath or null if callbacks are used.
   * @exception SAXException thrown if 
   * the error condition is severe enough to halt processing.
   */
  public XObject execute(XPathContext xctxt, Node contextNode, 
                         PrefixResolver namespaceContext)
    throws org.xml.sax.SAXException
  {    
    PrefixResolver savedPrefixResolver = xctxt.getNamespaceContext();
    xctxt.setNamespaceContext(namespaceContext);
    xctxt.setCurrentNode(contextNode);
    XObject xobj = null;
    try
    {
      xobj = execute(xctxt, contextNode, 0);
    }
    finally
    {
      xctxt.setNamespaceContext(savedPrefixResolver);
      xctxt.setCurrentNode(null); // I think this is probably fine
    }
    return xobj;
  }
  
  /**
   * Get the match score of the given node.
   * @param context The current source tree context node.
   * @returns score, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  public double getMatchScore(XPathContext xctxt, Node context) 
    throws org.xml.sax.SAXException
  {
    double score = MATCH_SCORE_NONE;
    int opPos = 0;
    if(m_opMap[opPos] == OpCodes.OP_MATCHPATTERN)
    {
      opPos = getFirstChildPos(opPos);
      
      XLocator locator = xctxt.getSourceTreeManager().getXLocatorFromNode(context);
      
      if(null == locator)
        locator = xctxt.createXLocatorHandler();

      while(m_opMap[opPos] == OpCodes.OP_LOCATIONPATHPATTERN)
      {
        int nextOpPos = getNextOpPos(opPos);
        
        // opPos = getFirstChildPos(opPos);        
        score = locator.locationPathPattern(this, xctxt, context, opPos);

        if(score != MATCH_SCORE_NONE)
          break;
        opPos = nextOpPos;
      }
      
    }
    else
    {
      error(xctxt, context, XPATHErrorResources.ER_EXPECTED_MATCH_PATTERN, null); //"Expected match pattern in getMatchScore!");
    }
    
    return score;
  }
  
  /**
   * Install a built-in function.
   * @param name The unqualified name of the function.
   * @param funcIndex The index of the function in the table.
   * @param func A Implementation of an XPath Function object.
   * @return the position of the function in the internal index.
   */
  public void installFunction (String name, int funcIndex, Function func)
  {            
    FunctionTable.m_functions[funcIndex] = func;    
  }

  /**
   * OR two expressions and return the boolean result.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the one of the two arguments are true.
   */
  protected XBoolean or(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    XObject expr1 = execute(xctxt, context, opPos);
    if(!expr1.bool())
    {
      XObject expr2 = execute(xctxt, context, expr2Pos);
      return expr2.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    else
      return XBoolean.S_TRUE;
  }

  /**
   * OR two expressions and return the boolean result.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are both true.
   */
  protected XBoolean and(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    XObject expr1 = execute(xctxt, context, opPos);
    if(expr1.bool())
    {
      XObject expr2 = execute(xctxt, context, expr2Pos);
      return expr2.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    else
      return XBoolean.S_FALSE;
  }

  /**
   * Tell if two expressions are functionally not equal.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are not equal.
   */
  protected XBoolean notequals(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return (expr1.notEquals(expr2)) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }


  /**
   * Tell if two expressions are functionally equal.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if the two arguments are equal.
   */
  protected XBoolean equals(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return expr1.equals(expr2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
    
  /**
   * Tell if one argument is less than or equal to the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is less than or equal to arg 2.
   */
  protected XBoolean lte(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return expr1.lessThanOrEqual(expr2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }

  /**
   * Tell if one argument is less than the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is less than arg 2.
   */
  protected XBoolean lt(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return expr1.lessThan(expr2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }

  /**
   * Tell if one argument is greater than or equal to the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is greater than or equal to arg 2.
   */
  protected XBoolean gte(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return expr1.greaterThanOrEqual(expr2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }


  /**
   * Tell if one argument is greater than the other argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns XBoolean set to true if arg 1 is greater than arg 2.
   */
  protected XBoolean gt(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return expr1.greaterThan(expr2) ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }

  /**
   * Give the sum of two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns sum of arg1 and arg2.
   */
  protected XNumber plus(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return new XNumber(expr1.num() +  expr2.num());
  }

  /**
   * Give the difference of two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns difference of arg1 and arg2.
   */
  protected XNumber minus(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return new XNumber(expr1.num() -  expr2.num());
  }

  /**
   * Multiply two arguments.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 * arg2.
   */
  protected XNumber mult(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return new XNumber(expr1.num() *  expr2.num());
  }

  /**
   * Divide a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 / arg2.
   */
  protected XNumber div(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return new XNumber(expr1.num() / expr2.num());
  }

  /**
   * Return the remainder from a truncating division.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 mod arg2.
   */
  protected XNumber mod(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return new XNumber(expr1.num() %  expr2.num());
  }

  /**
   * Return the remainder from a truncating division.
   * (Quo is no longer supported by xpath).
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg1 mod arg2.
   */
  protected XNumber quo(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    // Actually, this is no longer supported by xpath...
    warn(xctxt, context, XPATHErrorResources.WG_QUO_NO_LONGER_DEFINED, null); //"Old syntax: quo(...) is no longer defined in XPath.");
    
    opPos = getFirstChildPos(opPos);
    int expr2Pos = getNextOpPos(opPos);
    
    XObject expr1 = execute(xctxt, context, opPos);
    XObject expr2 = execute(xctxt, context, expr2Pos);
    
    return new XNumber((int)(expr1.num() /  expr2.num()));
  }

  /**
   * Return the negation of a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns -arg.
   */
  protected XNumber neg(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(xctxt, context, opPos+2);
    
    return new XNumber(-expr1.num());
  }

  /**
   * Cast an expression to a string.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a string.
   */
  protected XString string(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(xctxt, context, opPos+2);
    
    return new XString(expr1.str());
  }

  /**
   * Cast an expression to a boolean.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a boolean.
   */
  protected XBoolean bool(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(xctxt, context, opPos+2);
    
    return expr1.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
  }
 
  /**
   * Cast an expression to a number.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg cast to a number.
   */
  protected XNumber number(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(xctxt, context, opPos+2);
    
    return new XNumber(expr1.num());
  }
  
  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns the union of node-set operands.
   */
  protected XNodeSet union(XPathContext xctxt, 
                           Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XLocator xlocator = xctxt.getSourceTreeManager().getXLocatorFromNode(context);
    
    if(null == xlocator)
        xlocator = xctxt.createXLocatorHandler();
      
    XNodeSet results = xlocator.union(this, xctxt, 
                                      context, opPos);
    
    return results;
  }

  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XString object.
   */
  protected XString literal(XPathContext xctxt, Node context, int opPos) 
  {
    opPos = getFirstChildPos(opPos);
    
    // TODO: It's too expensive to create an object every time...
    return (XString)m_tokenQueue[m_opMap[opPos]];
  }
  
  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XObject object.
   */
  protected XObject variable(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    opPos = getFirstChildPos(opPos);
    String varName = (String)m_tokenQueue[m_opMap[opPos]];
    // System.out.println("variable name: "+varName);
    // TODO: I don't this will be parsed right in the first place...
    QName qname = new QName(varName, xctxt.getNamespaceContext());
    XObject result;
    try
    {
      result = xctxt.getVariable(qname);
    }
    catch(Exception e)
    {
      error(xctxt, context, XPATHErrorResources.ER_COULDNOT_GET_VAR_NAMED, new Object[] {varName}); //"Could not get variable named "+varName);
      result = null;
    }

    if(null == result)
    {
      error(xctxt, context, XPATHErrorResources.ER_ILLEGAL_VARIABLE_REFERENCE, new Object[] {varName}); //"VariableReference given for variable out "+
                    //"of context or without definition!  Name = " + varName);
      result = new XNodeSet();
    }

    return result;
  }


  /**
   * Execute an expression as a group.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns arg.
   */
  protected XObject group(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    return execute(xctxt, context, opPos+2);
  }


  /**
   * Get a literal value.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns an XString object.
   */
  protected XNumber numberlit(XPathContext xctxt, Node context, int opPos) 
  {
    opPos = getFirstChildPos(opPos);
    
    return (XNumber)m_tokenQueue[m_opMap[opPos]];
  }
  
  /**
   * Execute a function argument.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the result of the argument expression.
   */
  protected XObject arg(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    return execute(xctxt, context, opPos+2);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Execute a location path.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @returns a node-set.
   */
  public XNodeSet locationPath(XPathContext xctxt, 
                               Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    XLocator xlocator = xctxt.getSourceTreeManager().getXLocatorFromNode(context);
    
    if(null == xlocator)
        xlocator = xctxt.createXLocatorHandler();
      
    XNodeSet results = xlocator.locationPath(this, xctxt, context, opPos);
    
    return results;
  }

  /**
   * <meta name="usage" content="advanced"/>
   * Evaluate a predicate.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns either a boolean or a number.
   */
  public XObject predicate(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject expr1 = execute(xctxt, context, opPos+2);
    int objType = expr1.getType();
    if((XObject.CLASS_NUMBER != objType) && (XObject.CLASS_BOOLEAN != objType))
    {
      expr1 = expr1.bool() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
    }
    
    return expr1;
  }
  
  /**
   * Handle an extension function.
   */
  protected XObject extfunction(XPathContext xctxt, Node context, int opPos, 
                                String namespace, String extensionName, 
                                Vector argVec, Object methodKey) 
    throws org.xml.sax.SAXException
  {
    XObject result;
    ExtensionsTable etable = xctxt.getExtensionsTable();
    Object val = etable.extFunction(namespace, extensionName, 
                                    argVec, methodKey);
    if(null != val)
    {
      if(val instanceof XObject)
      {
        result = (XObject)val;
      }
      else if(val instanceof XLocator)
      {
        XLocator locator = (XLocator)val;
        opPos = getNextOpPos(opPos+1);
        result = locator.connectToNodes(this, xctxt, context, opPos, argVec);  
        // System.out.println("nodeset len: "+result.nodeset().getLength());
      }
      else if(val instanceof String)
      {
        result = new XString((String)val);
      }
      else if(val instanceof Boolean)
      {
        result = ((Boolean)val).booleanValue() ? XBoolean.S_TRUE : XBoolean.S_FALSE;
      }
      else if(val instanceof Double)
      {
        result = new XNumber(((Double)val).doubleValue());
      }
      else if(val instanceof DocumentFragment)
      {
        result = new XRTreeFrag((DocumentFragment)val);
      }
      else if(val instanceof Node)
      {
        // First, see if we need to follow-up with a location path.
        opPos = getNextOpPos(opPos);
        XNodeSet mnl = null;
        if((opPos < m_opMap[MAPINDEX_LENGTH]) &&
           (OpCodes.OP_LOCATIONPATH == (m_opMap[opPos] & OpCodes.LOCATIONPATHEX_MASK)))
        {
          mnl = locationPath(xctxt, (Node)val, opPos);
        }
        result = (null == mnl) ? new XNodeSet((Node)val)
                                 : mnl;
      }
      else if(val instanceof NodeIterator)
      {
        // First, see if we need to follow-up with a location path.
        opPos = getNextOpPos(opPos);
        XNodeSet mnl = null;
        if((opPos < m_opMap[MAPINDEX_LENGTH]) &&
           (OpCodes.OP_LOCATIONPATH == (m_opMap[opPos] & OpCodes.LOCATIONPATHEX_MASK)))
        {
          NodeIterator nl = (NodeIterator)val;
          Node node;
          while(null != (node = nl.nextNode()))
          {
            XNodeSet xnl = locationPath(xctxt, node, opPos);
            if(null == xnl)
              mnl = xnl;
            else
              mnl.mutableNodeset().addNodes(xnl.nodeset());
          }
        }
        result = (null == mnl) ? new XNodeSet((NodeIterator)val)
                                 : mnl;
      }
      else
      {
        result = new XObject(val);
      }
    }
    else
    {
      result = new XNull();
    }
    return result;
  }


  /**
   * Computes the union of its operands which must be node-sets.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns the match score in the form of an XObject.
   */
  protected XObject matchPattern(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {
    XObject score = null;

    while(m_opMap[opPos] == OpCodes.OP_LOCATIONPATHPATTERN)
    {
      int nextOpPos = getNextOpPos(opPos);
      score = execute(xctxt, context, opPos);
      if(score.num() != MATCH_SCORE_NONE)
        break;
      opPos = nextOpPos;
    }
    if(null == score)
    {
      score = new XNumber(MATCH_SCORE_NONE);
    }
    
    return score;
  }

  /**
   * Execute a location path.
   * @param context The current source tree context node.
   * @param opPos The current position in the m_opMap array.
   * @returns score in an XNumber, one of MATCH_SCORE_NODETEST, 
   * MATCH_SCORE_NONE, MATCH_SCORE_OTHER, MATCH_SCORE_QNAME.
   */
  protected XNumber locationPathPattern(XPathContext xctxt, Node context, int opPos) 
    throws org.xml.sax.SAXException
  {    
    // opPos = getFirstChildPos(opPos);
    XLocator locator = xctxt.getSourceTreeManager().getXLocatorFromNode(context);
    
    if(null == locator)
        locator = xctxt.createXLocatorHandler();
      
    double results = locator.locationPathPattern(this, xctxt, context, opPos);
    
    return new XNumber(results);
  }

  /**
   * <meta name="usage" content="advanced"/>
   * This method is for building indexes of match patterns for fast lookup.
   * This allows a caller to get the name or type of a node, and quickly 
   * find the likely candidates that may match.
   */
  public Vector getTargetElementStrings()
  {
    Vector targetStrings = new Vector();

    int opPos = 2;

    while(m_opMap[opPos] == OpCodes.OP_LOCATIONPATHPATTERN)
    {
      int nextOpPos = getNextOpPos(opPos);
      opPos = getFirstChildPos(opPos);
      
      while( m_opMap[opPos] != OpCodes.ENDOP )
      {
        int nextStepPos = getNextOpPos(opPos);
        int nextOp = m_opMap[nextStepPos];
        if((nextOp == OpCodes.OP_PREDICATE) || (nextOp == OpCodes.ENDOP))
        {
          int stepType = m_opMap[opPos];
          opPos += 3;
          switch(stepType)
          {
          case OpCodes.OP_FUNCTION:
            targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
            break;
          case OpCodes.FROM_ROOT:
            targetStrings.addElement(PsuedoNames.PSEUDONAME_ROOT);
            break;
          case OpCodes.MATCH_ATTRIBUTE:
          case OpCodes.MATCH_ANY_ANCESTOR:
          case OpCodes.MATCH_IMMEDIATE_ANCESTOR:
            int tok = m_opMap[opPos];
            opPos++;
            switch(tok)
            {
            case OpCodes.NODETYPE_COMMENT:
              targetStrings.addElement(PsuedoNames.PSEUDONAME_COMMENT);
              break;
            case OpCodes.NODETYPE_TEXT:
              targetStrings.addElement(PsuedoNames.PSEUDONAME_TEXT);
              break;
            case OpCodes.NODETYPE_NODE:
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              break;
            case OpCodes.NODETYPE_ROOT:
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ROOT);
              break;
            case OpCodes.NODETYPE_ANYELEMENT:
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              break;
            case OpCodes.NODETYPE_PI:
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              break;
            case OpCodes.NODENAME:
              // Skip the namespace
              int tokenIndex = m_opMap[opPos+1];
              if(tokenIndex >= 0)
              {
                String targetName = (String)m_tokenQueue[tokenIndex];
                if(targetName.equals("*"))
                {
                  targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
                }
                else
                {
                  targetStrings.addElement(targetName);
                }
              }
              else
              {
                targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              }
              break;
            default:
              targetStrings.addElement(PsuedoNames.PSEUDONAME_ANY);
              break;
            }
            break;
          }
        }
        opPos = nextStepPos;
      }
      
      opPos = nextOpPos;
    }
    return targetStrings;
  }
    
  /**
   * Execute an extension function from an op code.
   */
  private XObject executeExtension(XPathContext xctxt, Node context, int opPos)
    throws org.xml.sax.SAXException
  {
    int endExtFunc = opPos+m_opMap[opPos+1]-1;
    opPos = getFirstChildPos(opPos);
    String ns = (String)m_tokenQueue[m_opMap[opPos]];
    opPos++;
    String funcName = (String)m_tokenQueue[m_opMap[opPos]];
    opPos++;
    Vector args = new Vector();
    while(opPos < endExtFunc)
    {
      int nextOpPos = getNextOpPos(opPos);
      args.addElement( execute(xctxt, context, opPos) );
      opPos = nextOpPos;
    }
    return extfunction(xctxt, context, opPos, ns, funcName, args, 
                       // Create a method key, for faster lookup.
      String.valueOf(m_opMap[opPos])+String.valueOf(((Object)this).hashCode()));
  }

  /**
   * Execute a function from an op code.
   */
  XObject executeFunction(XPathContext xctxt, Node context, int opPos)
    throws org.xml.sax.SAXException
  {
    int endFunc = opPos+m_opMap[opPos+1]-1;
    opPos = getFirstChildPos(opPos);
    int funcID = m_opMap[opPos];
    opPos++;
    if(-1 != funcID)
    {
      return FunctionTable.m_functions[funcID].execute(this, xctxt, context, opPos, funcID, endFunc);
    }
    else
    {
      warn(xctxt, context, XPATHErrorResources.WG_FUNCTION_TOKEN_NOT_FOUND, null); //"function token not found.");
      return null;
    }
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * Execute the XPath object from a given opcode position.
   * @param xctxt The execution context.
   * @param context The current source tree context node.
   * @param opPos The current position in the xpath.m_opMap array.
   * @param callback Interface that implements the processLocatedNode method.
   * @param callbackInfo Object that will be passed to the processLocatedNode method.
   * @return The result of the XPath.
   */
  public XObject execute(XPathContext xctxt, 
                         Node context, int opPos)
    throws org.xml.sax.SAXException
  {
    int op = m_opMap[opPos];
    switch(op)
    {
    case OpCodes.OP_XPATH: return execute(xctxt, context, opPos+2);
    case OpCodes.OP_OR: return or(xctxt, context, opPos);
    case OpCodes.OP_AND: return and(xctxt, context, opPos);
    case OpCodes.OP_NOTEQUALS: return notequals(xctxt, context, opPos);
    case OpCodes.OP_EQUALS: return equals(xctxt, context, opPos);
    case OpCodes.OP_LTE: return lte(xctxt, context, opPos);
    case OpCodes.OP_LT: return lt(xctxt, context, opPos);
    case OpCodes.OP_GTE: return gte(xctxt, context, opPos);
    case OpCodes.OP_GT: return gt(xctxt, context, opPos);
    case OpCodes.OP_PLUS: return plus(xctxt, context, opPos);
    case OpCodes.OP_MINUS: return minus(xctxt, context, opPos);
    case OpCodes.OP_MULT: return mult(xctxt, context, opPos);
    case OpCodes.OP_DIV: return div(xctxt, context, opPos);
    case OpCodes.OP_MOD: return mod(xctxt, context, opPos);
    case OpCodes.OP_QUO: return quo(xctxt, context, opPos);
    case OpCodes.OP_NEG: return neg(xctxt, context, opPos);
    case OpCodes.OP_STRING: return string(xctxt, context, opPos);
    case OpCodes.OP_BOOL: return bool(xctxt, context, opPos);
    case OpCodes.OP_NUMBER: return number(xctxt, context, opPos);
    case OpCodes.OP_UNION: return union(xctxt, context, opPos);
    case OpCodes.OP_LITERAL: return literal(xctxt, context, opPos);
    case OpCodes.OP_VARIABLE: return variable(xctxt, context, opPos);
    case OpCodes.OP_GROUP: return group(xctxt, context, opPos);
    case OpCodes.OP_NUMBERLIT: return numberlit(xctxt, context, opPos);
    case OpCodes.OP_ARGUMENT: return arg(xctxt, context, opPos);
    case OpCodes.OP_EXTFUNCTION: return executeExtension(xctxt, context, opPos);
    case OpCodes.OP_FUNCTION: return executeFunction(xctxt, context, opPos);
    case OpCodes.OP_LOCATIONPATH: return locationPath(xctxt, context, opPos);
    case OpCodes.OP_PREDICATE: return null; // should never hit this here.
    case OpCodes.OP_MATCHPATTERN: return matchPattern(xctxt, context, opPos+2);
    case OpCodes.OP_LOCATIONPATHPATTERN: return locationPathPattern(xctxt, context, opPos);
    default: if(op == OpCodes.OP_LOCATIONPATH_EX) return locationPath(xctxt, context, opPos);
             else error(xctxt, context, XPATHErrorResources.ER_UNKNOWN_OPCODE, new Object[] {Integer.toString(m_opMap[opPos])}); //"ERROR! Unknown op code: "+m_opMap[opPos]);
    }
    return null;
  }
  
  /**
   * Warn the user of an problem.
   */
  public void warn(XPathContext xctxt,
                   Node sourceNode, int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    String fmsg = XSLMessages.createXPATHWarning(msg, args); 
    
    ErrorHandler ehandler = xctxt.getPrimaryReader().getErrorHandler();

    if(null != ehandler)
    {
      // TO DO: Need to get stylesheet Locator from here.
      ehandler.warning(new TransformException(fmsg));
    }
  }

  /**
   * Tell the user of an assertion error, and probably throw an 
   * exception.
   */
  public void assert(boolean b, String msg)
    throws org.xml.sax.SAXException
  {
    if(!b)
    {
      String fMsg = XSLMessages.createXPATHMessage(XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION, new Object[] {msg}); 
      throw new RuntimeException(fMsg);
    }
  }

  /**
   * Tell the user of an error, and probably throw an 
   * exception.
   */
  public void error(XPathContext xctxt, Node sourceNode, int msg, Object[] args)
    throws org.xml.sax.SAXException
  {
    String fmsg = XSLMessages.createXPATHMessage(msg, args); 
    
    ErrorHandler ehandler = xctxt.getPrimaryReader().getErrorHandler();

    TransformException te = new TransformException(fmsg, 
                                                   xctxt.getSAXLocator());
                                                   
    if(null != ehandler)
      ehandler.fatalError(te);
    else
    {
      System.out.println(te.getMessage()
                         +"; file "+te.getSystemId()
                         +"; line "+te.getLineNumber()
                         +"; column "+te.getColumnNumber());
    }
  }
  
  /**
   * <meta name="usage" content="advanced"/>
   * The match score if no match is made.
   */
  public static final double MATCH_SCORE_NONE = Double.NEGATIVE_INFINITY;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern has the form 
   * of a QName optionally preceded by an @ character.
   */
  public static final double MATCH_SCORE_QNAME = 0.0;
  
  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern pattern has the form NCName:*.
   */
  public static final double MATCH_SCORE_NSWILD = -0.25;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern consists of just a NodeTest.
   */
  public static final double MATCH_SCORE_NODETEST = -0.5;

  /**
   * <meta name="usage" content="advanced"/>
   * The match score if the pattern consists of something 
   * other than just a NodeTest or just a qname.
   */
  public static final double MATCH_SCORE_OTHER = 0.5;

}
