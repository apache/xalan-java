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
package org.apache.xpath.functions;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMManager;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionNode;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.ExtensionsProvider;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNull;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.res.XPATHErrorResources;
import org.apache.xpath.res.XPATHMessages;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSNumericType;
import xml.xpath31.processor.types.XSString;

/**
 * An object of this class represents an XPath constructor function, 
 * XSL stylesheet function or Xalan-J's extension function.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Christine Li <jycli@apache.org>, Morris Kwan <mkwan@apache.org>
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XPath 3 specific changes, to this class)
 * 
 * @xsl.usage advanced
 */
public class XSL3ConstructorOrExtensionFunction extends Function
{
  static final long serialVersionUID = 5196115554693708718L;

  /**
   * The namespace for the extension function, which should not normally
   *  be null or empty.
   *  @serial    
   */
  String m_namespace;

  /**
   * The local name of the extension.
   *  @serial   
   */
  String m_extensionName;

  /**
   * Unique method key, which is passed to ExtensionsTable#extFunction in
   *  order to allow caching of the method.
   *  @serial 
   */
  Object m_methodKey;

  /**
   * Array of static expressions which represent the parameters to the
   *  function.
   *  @serial   
   */
  Vector m_argVec = new Vector();

  /**
   * This function is used to fixup variables from QNames to stack frame
   * indexes at stylesheet build time.
   * @param vars List of QNames that correspond to variables.  This list
   * should be searched backwards for the first qualified name that
   * corresponds to the variable reference qname.  The position of the
   * QName in the vector from the start of the vector will be its position
   * in the stack frame (but variables above the globalsTop value will need
   * to be offset to the current stack frame).
   * NEEDSDOC @param globalsSize
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {

    if (null != m_argVec)
    {
      int nArgs = m_argVec.size();

      for (int i = 0; i < nArgs; i++)
      {
        Expression arg = (Expression) m_argVec.elementAt(i);

        arg.fixupVariables(vars, globalsSize);
      }
    }
  }
  
  /**
   * Return the namespace of the extension function.
   *
   * @return The namespace of the extension function.
   */
  public String getNamespace()
  {
    return m_namespace;
  }
  
  /**
   * Return the name of the extension function.
   *
   * @return The name of the extension function.
   */
  public String getFunctionName()
  {
    return m_extensionName;
  }
  
  /**
   * Return the method key of the extension function.
   *
   * @return The method key of the extension function.
   */
  public Object getMethodKey()
  {
    return m_methodKey;
  }

  /** 
   * Return the nth argument passed to the extension function.
   * 
   * @param n The argument number index.
   * @return The Expression object at the given index.
   */    
  public Expression getArg(int n) {
    if (n >= 0 && n < m_argVec.size())
      return (Expression) m_argVec.elementAt(n);
    else
      return null;
  }

  /**
   * Return the number of arguments that were passed
   * into this extension function.
   *
   * @return The number of arguments.
   */    
  public int getArgCount() {
    return m_argVec.size();
  }

  /**
   * Create a new FuncExtFunction based on the qualified name of the extension,
   * and a unique method key.
   *
   * @param namespace The namespace for the extension function, which should
   *                  not normally be null or empty.
   * @param extensionName The local name of the extension.
   * @param methodKey Unique method key, which is passed to
   *                  ExtensionsTable#extFunction in order to allow caching
   *                  of the method.
   */
  public XSL3ConstructorOrExtensionFunction(java.lang.String namespace,
                         										java.lang.String extensionName, Object methodKey)
  {
    m_namespace = namespace;
    m_extensionName = extensionName;
    m_methodKey = methodKey;
  }

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {
    if (xctxt.isSecureProcessing())
      throw new javax.xml.transform.TransformerException(
        XPATHMessages.createXPATHMessage(
          XPATHErrorResources.ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED,
          new Object[] {toString()}));
      
    XObject result = null;
    
    try {
       // Making a call to an XPath constructor function, or XSL 
       // stylesheet function implementations.
       XSL3FunctionService xslFunctionService = xctxt.getXSLFunctionService();
       result = xslFunctionService.callFunction(this, null, xctxt);
    } 
    catch (TransformerException ex) {        
       throw new TransformerException(ex.getMessage(), xctxt.getSAXLocator());
    }           
    
    if (result == null) {
    	// Attempting to make a call to an XPath extension function
    	Vector argVec = new Vector();
    	int nArgs = m_argVec.size();

    	for (int i = 0; i < nArgs; i++)
    	{
    		Expression arg = (Expression) m_argVec.elementAt(i);

    		XObject xobj = arg.execute(xctxt);
    		
    		if (xobj instanceof XSString) {
    			xobj = new XString(((XSString)xobj).stringValue());
    			xobj.allowDetachToRelease(false);
    			argVec.addElement(xobj);    			
    		}
    		else if (xobj instanceof XSBoolean) {
    			xobj = new XBoolean(((XSBoolean)xobj).bool());
    			xobj.allowDetachToRelease(false);
    			argVec.addElement(xobj);    			
    		}
    		else if (xobj instanceof XSNumericType) {
    			Double dbl = Double.valueOf(((XSNumericType)xobj).stringValue());
    			xobj = new XNumber(dbl);
    			xobj.allowDetachToRelease(false);
    			argVec.addElement(xobj);
    		}
    		else if (xobj instanceof XMLNodeCursorImpl) {    			    			
    			XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xobj;
    			int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
    			DTMManager dtmManager = xctxt.getDTMManager();
    			DTM dtm = dtmManager.getDTM(nodeHandle);
    			Node node = dtm.getNode(nodeHandle);    			
    			try {
    				String xmlStr1 = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);

    				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    				dbf.setNamespaceAware(true);

    				DocumentBuilder dBuilder = dbf.newDocumentBuilder();

    				Document document = dBuilder.parse(new ByteArrayInputStream(xmlStr1.getBytes()));
    				xobj.allowDetachToRelease(false);
    				argVec.addElement((Node)document);
				} 
    			catch (Exception ex) {
					throw new TransformerException(ex.getMessage());
				}    			    			
    		}
    		else if (xobj instanceof ResultSequence) {    			
    			ResultSequence rSeq = (ResultSequence)xobj;
    			int seqLength = rSeq.size();
    			XObject[] xObjArr = new XObject[seqLength];
    			for (int idx = 0; idx < seqLength; idx++) {
    				XObject seqitem = rSeq.item(idx);
    				if (seqitem instanceof XSString) {    					
    					seqitem = new XString(((XSString)seqitem).stringValue());    					
    				}
    				else if (seqitem instanceof XSBoolean) {    					
    					seqitem = new XBoolean(((XSBoolean)seqitem).bool());
    				}
    				else if (seqitem instanceof XSNumericType) {
    					Double dbl = Double.valueOf(((XSNumericType)seqitem).stringValue());
    					XNumber xNum = new XNumber(dbl.doubleValue());
    					seqitem = xNum;
    				}

    				seqitem.allowDetachToRelease(false);    			   
    				xObjArr[idx] = seqitem;
    			}
    			
    			argVec.add(xObjArr);
    		}
    		else if (xobj instanceof XPathMap) { 
    			XPathMap xpathMap = (XPathMap)xobj;
    			Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
    			xobj.allowDetachToRelease(false);
    			argVec.add(nativeMap);
    		}
    		else if (xobj instanceof XPathArray) {    			
    			XPathArray xpathArr = (XPathArray)xobj;
    			int arrLength = xpathArr.size();
    			XObject[] xObjArr = new XObject[arrLength];
    			for (int idx = 0; idx < arrLength; idx++) {
    				XObject seqitem = xpathArr.get(idx);
    				if (seqitem instanceof XSString) {
    					seqitem = new XString(((XSString)seqitem).stringValue());   				   
    				}
    				else if (seqitem instanceof XSBoolean) {
    					seqitem = new XBoolean(((XSBoolean)seqitem).bool());   				   
    				}
    				else if (seqitem instanceof XSNumericType) {
    					Double dbl = Double.valueOf(((XSNumericType)seqitem).stringValue());
    					XNumber xNum = new XNumber(dbl.doubleValue());
    					seqitem = xNum;
    				}

    				seqitem.allowDetachToRelease(false);    			   
    				xObjArr[idx] = seqitem;
    			}
    			
    			argVec.add(xObjArr);
    		}    		
    		else {
    			xobj.allowDetachToRelease(false);
    			argVec.addElement(xobj);
    		}
    	}

    	ExtensionsProvider extProvider = (ExtensionsProvider)xctxt.getOwnerObject();    	
    	Object val = extProvider.extFunction(this, argVec);

    	if (null != val)
    	{
    		if ((val instanceof NativeObject) || (val instanceof NativeArray)) {    		   	    		   
    		   result = getXPathObjectFromJsObject(val, xctxt); 
    		}
    		else {
    		   result = XObject.create(val, xctxt);
    		}
    	}
    	else
    	{
    		result = new XNull();
    	}
    }

    return result;
  }

  /**
   * Set an argument expression for a function.  This method is called by the
   * XPath compiler.
   *
   * @param arg non-null expression that represents the argument.
   * @param argNum The argument number index.
   *
   * @throws WrongNumberArgsException If the argNum parameter is beyond what
   * is specified for this function.
   */
  public void setArg(Expression arg, int argNum)
          throws WrongNumberArgsException
  {
    m_argVec.addElement(arg);
    arg.exprSetParent(this);
  }

  /**
   * Check that the number of arguments passed to this function is correct.
   *
   *
   * @param argNum The number of arguments that is being passed to the function.
   *
   * @throws WrongNumberArgsException
   */
  public void checkNumberArgs(int argNum) throws WrongNumberArgsException{}


  class ArgExtOwner implements ExpressionOwner
  {
  
    Expression m_exp;
  	
  	ArgExtOwner(Expression exp)
  	{
  		m_exp = exp;
  	}
  	
    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_exp;
    }

    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
    	exp.exprSetParent(XSL3ConstructorOrExtensionFunction.this);
    	m_exp = exp;
    }
  }
  
  
  /**
   * Call the visitors for the function arguments.
   */
  public void callArgVisitors(XPathVisitor visitor)
  {
      for (int i = 0; i < m_argVec.size(); i++)
      {
         Expression exp = (Expression)m_argVec.elementAt(i);
         exp.callVisitors(new ArgExtOwner(exp), visitor);
      }
    
  }

  /**
   * Set the parent node.
   * For an extension function, we also need to set the parent
   * node for all argument expressions.
   * 
   * @param n The parent node
   */
  public void exprSetParent(ExpressionNode n) 
  {
    super.exprSetParent(n);
      
    int nArgs = m_argVec.size();

    for (int i = 0; i < nArgs; i++)
    {
      Expression arg = (Expression) m_argVec.elementAt(i);

      arg.exprSetParent(n);
    }		
  }

  /**
   * Constructs and throws a WrongNumberArgException with the appropriate
   * message for this function object.  This class supports an arbitrary
   * number of arguments, so this method must never be called.
   *
   * @throws WrongNumberArgsException
   */
  protected void reportWrongNumberArgs() throws WrongNumberArgsException {
    String fMsg = XSLMessages.createXPATHMessage(
        XPATHErrorResources.ER_INCORRECT_PROGRAMMER_ASSERTION,
        new Object[]{ "Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called." });

    throw new RuntimeException(fMsg);
  }
  
  /**
   * Return the name of the extesion function in string format
   */
  public String toString()
  {
    if (m_namespace != null && m_namespace.length() > 0)
      return "{" + m_namespace + "}" + m_extensionName;
    else
      return m_extensionName;
  }
  
  public Vector getArgVector() {
	 return m_argVec;  
  }
  
  /**
   * Method definition, to get an XPath typed object from an object typed 
   * according to JavaScript implementation.
   * 
   * @param obj1						An object that needs to be converted 
   *                                    to an XPath typed object.
   * @param xctxt                       An XPath context object
   * @return                            The result XPath typed object
   */
  private XObject getXPathObjectFromJsObject(Object obj1, XPathContext xctxt) {
		 
	  XObject result = null;

	  if (obj1 instanceof NativeObject) {
		  XPathMap xpathMap = new XPathMap();
		  NativeObject nativeObj = (NativeObject)obj1;
		  Set<Object> keySet = nativeObj.keySet();
		  Iterator<Object> iter1 = keySet.iterator();
		  while (iter1.hasNext()) {
			  Object key = iter1.next();
			  XObject x1 = XObject.create(key, xctxt);
			  Object value = nativeObj.get(key);
			  if ((value instanceof NativeObject) || (value instanceof NativeArray)) {			   
				  XObject xObjValue = getXPathObjectFromJsObject(value, xctxt);
				  xpathMap.put(x1, xObjValue); 
			  }
			  else {			  
				  XObject y1 = XObject.create(value, xctxt);
				  xpathMap.put(x1, y1); 
			  }
		  }

		  result = xpathMap;
	  }
	  else if (obj1 instanceof NativeArray) { 
		  XPathArray xpathArr = new XPathArray();
		  NativeArray nativeArr = (NativeArray)obj1;
		  int arrSize = nativeArr.size();
		  for (int idx = 0; idx < arrSize; idx++) {
			  Object obj2 = nativeArr.get(idx);
			  XObject xObj = null;
			  if ((obj2 instanceof NativeObject) || (obj2 instanceof NativeArray)) {	
				  xObj = getXPathObjectFromJsObject(obj2, xctxt); 
			  }
			  else {
				  xObj = XObject.create(obj2, xctxt);  
			  }

			  xpathArr.add(xObj);
		  }

		  result = xpathArr;
	  }
	  else {
		  result = XObject.create(obj1, xctxt); 
	  }

	  return result;
   }

}
