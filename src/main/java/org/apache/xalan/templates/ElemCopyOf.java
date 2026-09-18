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
package org.apache.xalan.templates;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.TreeWalker2Result;
import org.apache.xalan.xslt.util.XslTransformData;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xml.dtm.ref.DTMTreeWalker;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.Expression;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.composite.XPathSequenceTypeSupport;
import org.apache.xpath.functions.Function;
import org.apache.xpath.functions.XPathDynamicFunctionCall;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.objects.XdmAttributeItem;
import org.apache.xpath.types.XMLAttribute;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSDecimal;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSInteger;
import xml.xpath31.processor.types.XSQName;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of an XSLT instruction xsl:copy-of.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Morris Kwan <mkwan@apache.org>
 * @author Christine Li <jycli@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3.0 specific changes, to this class)
 * 
 * @xsl.usage advanced
 */
public class ElemCopyOf extends ElemTemplateElement
{
  static final long serialVersionUID = -7433828829497411127L;

  /**
   * The required select attribute contains an expression.
   */
  public XPath m_selectExpression = null;
  
  /**
   * True if the pattern is a simple ".".
   */
  private boolean m_isDot = false;
  
  /**
   * Class field, that represents the value of "xpath-default-namespace" 
   * attribute.
   */
  private String m_xpath_default_namespace = null;
  
  /**
   * Class field, that represents the value of "expand-text" 
   * attribute.
   */
  private boolean m_expand_text;
  
  /**
   * This class field is used during, XPath.fixupVariables(..) action 
   * as performed within object of this class.  
   */    
  private Vector m_vars;
  
  /**
   * This class field is used during, XPath.fixupVariables(..) action 
   * as performed within object of this class.  
   */
  private int m_globals_size;
  
  public final static char SPACE_CHAR = ' ';

  /**
   * Set the "select" attribute.
   * The required select attribute contains an expression.
   *
   * @param expr Expression for select attribute 
   */
  public void setSelect(XPath expr)
  {
    if (expr != null) {
       String patternStr = expr.getPatternString();

       m_isDot = (patternStr != null) && patternStr.equals(".");
    }
      
    m_selectExpression = expr;
  }

  /**
   * Get the "select" attribute.
   * The required select attribute contains an expression.
   *
   * @return Expression for select attribute 
   */
  public XPath getSelect()
  {
    return m_selectExpression;
  }
  
  /**
   * Set the value of "xpath-default-namespace" attribute.
   *
   * @param v   Value of the "xpath-default-namespace" attribute
   */
  public void setXpathDefaultNamespace(String v)
  {
 	 m_xpath_default_namespace = v; 
  }

  /**
   * Get the value of "xpath-default-namespace" attribute.
   *  
   * @return		  The value of "xpath-default-namespace" attribute 
   */
  public String getXpathDefaultNamespace() {
 	 return m_xpath_default_namespace;
  }

  /**
   * Variable to indicate whether, an attribute 'expand-text'
   * is declared on xsl:copy-of instruction.
   */
  private boolean m_expand_text_declared;

  /**
   * Set the value of "expand-text" attribute.
   *
   * @param v   Value of the "expand-text" attribute
   */
  public void setExpandText(boolean v)
  {
	  m_expand_text = v;
	  m_expand_text_declared = true;
  }

  /**
   * Get the value of "expand-text" attribute.
   *  
   * @return		  The value of "expand-text" attribute 
   */
  public boolean getExpandText() {
	  return m_expand_text;
  }
  
  /**
   * Get a boolean value indicating whether, an "expand-text" 
   * attribute has been declared. 
   */
  public boolean getExpandTextDeclared() {
	  return m_expand_text_declared;
  }
  
  /**
   * This class field represents, xsl:copy-of instruction's attribute 
   * copy-namespaces's value, with default value true.
   */
  private boolean m_copy_namespaces = true;
  
  /**
   * Set the value of "copy-namespaces" attribute.
   *
   * @param v   Value of the "copy-namespaces" attribute
   */
  public void setCopyNamespaces(boolean v)
  {
	  m_copy_namespaces = v;
  }

  /**
   * Get the value of "copy-namespaces" attribute.
   *  
   * @return		  The value of "copy-namespaces" attribute 
   */
  public boolean getCopyNamespaces() {
	  return m_copy_namespaces;
  }
  
  /**
   * An XPath expression for XSL attribute "use-when". 
   */
  private XPath m_useWhen = null;

  /**
   * Method definition, to set the value of XSL attribute 
   * "use-when".
   * 
   * @param xpath            XPath expression for attribute "use-when"
   */
  public void setUseWhen(XPath xpath)
  {
	  m_useWhen = xpath;  
  }

  /**
   * Method definition, to get the value of XSL attribute 
   * "use-when".
   * 
   * @return			    XPath expression for attribute "use-when"
   */
  public XPath getUseWhen()
  {
	  return m_useWhen;
  }
  
  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
    super.compose(sroot);
    
    java.util.Vector vnames = (sroot.getComposeState()).getVariableNames();
    
    m_vars = (Vector)(vnames.clone()); 
    m_globals_size = (sroot.getComposeState()).getGlobalsSize();

    if (m_selectExpression != null) {
        m_selectExpression.fixupVariables(vnames, m_globals_size);
    }
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return           The token id for this element
   */
  public int getXSLToken()
  {
    return Constants.ELEMNAME_COPY_OF;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
    return Constants.ELEMNAME_COPY_OF_STRING;
  }

  /**
   * The xsl:copy-of element can be used to insert a result tree
   * fragment into the result tree, without first converting it to
   * a string as xsl:value-of does.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
    
	if (transformer.getDebug())
    	transformer.getTraceManager().emitTraceEvent(this);

    boolean isXPathExprStrCheck = false;
    
    XPathContext xctxt = transformer.getXPathContext();
    setXPathContext(xctxt);
    
    SourceLocator srcLocator = xctxt.getSAXLocator();
    
    try {                                
      QName type = getType();
      
      String validationStr = getValidation();
        
      if ((type != null) && (validationStr != null)) {
      	  throw new TransformerException("XTTE1540 : An XSL copy-of instruction cannot have both the attributes "
      	  																										+ "'type' and 'validation'.", srcLocator); 
      }
      
      if (validationStr != null) {
    	  if (!isValidationStrOk(validationStr)) {
    		 throw new TransformerException("XTTE1540 : An XSL copy-of instruction's attribute 'validation' can only have one of following "
                                                                                                				+ "values : strict, lax, preserve, strip.", srcLocator);  
    	  }
      }
              
      final int sourceNode = xctxt.getCurrentNode();
      
      XObject value = null;
      
      if ((m_selectExpression != null) && (m_xpath_default_namespace != null)) {    		
    	  m_selectExpression = new XPath(m_selectExpression.getPatternString(), srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null, false, m_xpath_default_namespace);
      }
      
      Expression expr1 = m_selectExpression.getExpression();
      
      Function func = null;
      
      XPathDynamicFunctionCall dfc = null;
      
      if (expr1 instanceof LocPathIterator) {
    	  LocPathIterator locPathIterator = (LocPathIterator)expr1; 
    	  
    	  func = locPathIterator.getFuncExpr();
    	  
    	  dfc = locPathIterator.getDynamicFuncCallExpr();
    	  
    	  int nextNode = DTM.NULL;
    	  
    	  if (sourceNode != DTM.NULL) {
    	     nextNode = locPathIterator.asNode(xctxt);
    	  }
    	  
    	  if (!((transformer.getStylesheet()).isInitContextNodeSet()) && (nextNode == DTM.NULL)) {   		  
    		  ElemTemplateElement elemTemplateElement = getParentElem();
    		  QName templateName = null;
    		  boolean isError = false;
    		  
    		  while (elemTemplateElement != null) {
    			  if (elemTemplateElement instanceof ElemTemplate) {
    				  ElemTemplate elemTemplate = (ElemTemplate)elemTemplateElement;
    				  templateName = elemTemplate.getName();
    			  }
    			  else if (elemTemplateElement instanceof StylesheetRoot) {
    				  StylesheetRoot stylesheetRoot = (StylesheetRoot)elemTemplateElement;
    				  String initTemplateName = stylesheetRoot.getInitTemplateName();
    				  
    				  if (initTemplateName != null) {
    					  QName qName = new QName(initTemplateName);
    					  
    					  if (qName.equals(templateName)) {
    						  isError = true;

    						  break;
    					  }
    				  }
    			  }

    			  elemTemplateElement = elemTemplateElement.getParentElem();
    		  }

    		  if (isError) {
    			  throw new TransformerException("XPDY0002 : An XSL transformation attempts to access XPath focus with copy-of "
																								    					  		+ "instruction, but intial context "
																								    					  		+ "node is not set.", srcLocator);
    		  }
    	  }
      }
      
      String xpathPatternStr = m_selectExpression.getPatternString();      
      
      if (xpathPatternStr.startsWith("/") && !xpathPatternStr.startsWith("//")) {
    	  DTM dtm = xctxt.getDTM(sourceNode);
    	  int documentNodeHandle = dtm.getDocument();
    	  
    	  if (documentNodeHandle == DTM.NULL) {
    		  throw new TransformerException("XPDY0050 : An XPath expression string " + xpathPatternStr + " cannot be "
    		  		                                                                                    + "evaluated, because xdm tree containing "
    		  		                                                                                    + "the context item is not a document node.", srcLocator); 
    	  }
      }
      
      XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
      
      if (m_isDot && (xpath3ContextItem != null)) {
          value = xpath3ContextItem;  
      }
      else {    	  
    	  if ((sourceNode == DTM.NULL) && xpathPatternStr.startsWith("$")) {    		 
    		  String varRef = null;
    		  int idx = xpathPatternStr.indexOf('/');
    		  
    		  if (idx == -1) {
    			  idx = xpathPatternStr.indexOf('['); 	
    		  }    		  
    		  
    		  if (idx != -1) {
    			  varRef = xpathPatternStr.substring(0, idx);
    		  }
    		  else {
    			  varRef = xpathPatternStr; 
    		  }    			

    		  XPath xpath2 = new XPath(varRef, srcLocator, xctxt.getNamespaceContext(), XPath.SELECT, null);
    		  xpath2.fixupVariables(m_vars, m_globals_size);
    		  Expression expr2 = xpath2.getExpression();
    		  
    		  XObject xObj2 = expr2.execute(xctxt);
    		  
    		  if (xObj2 instanceof XMLNodeCursorImpl) {
    			  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj2;
    			  int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
    			  DTM dtm = xctxt.getDTM(nodeHandle);    			   
    			  
    			  int sourceNode1 = dtm.getParent(nodeHandle);
    			  
    			  isXPathExprStrCheck = true;
    			  
    			  xctxt.pushCurrentNode(sourceNode1);
    			  
    			  value = m_selectExpression.execute(xctxt, sourceNode1, this);    			  
    		  }
    		  else {
    			  value = xObj2; 
    		  }
    	  }
    	  else {
              value = m_selectExpression.execute(xctxt, sourceNode, this);
    	  }
      }

      if (transformer.getDebug()) {
         transformer.getTraceManager().emitSelectedEvent(sourceNode, this,
                                                                        "select", m_selectExpression, value);
      }
      
      if ((value != null) && (value.getType() == XObject.CLASS_NODESET)) {
    	  Expression xslCopyOfSelectExpr = m_selectExpression.getExpression();
    	  
    	  if (xslCopyOfSelectExpr instanceof FuncCurrentGroup) {
    		  ElemTemplateElement elemTemplateElement = getParentElem();
    		  
    		  while (elemTemplateElement != null) {
    			  if (elemTemplateElement instanceof ElemForEachGroup) {
    				  boolean isInpSeqAllAtomicValues = ((ElemForEachGroup)elemTemplateElement).getInpSeqIsAllAtomicValues();
    				  
    				  if (isInpSeqAllAtomicValues) {
    					  ResultSequence rSeq = new ResultSequence();
    					  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)value;
    					  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
    					  int nextNode = DTM.NULL;
    					  
    					  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
    						  XMLNodeCursorImpl node1 = new XMLNodeCursorImpl(nextNode, xctxt);
    						  XSString xsString = new XSString(node1.str());
    						  rSeq.add(xsString);
    					  }

    					  value = rSeq;
    				  }

    				  break;
    			  }

    			  elemTemplateElement = elemTemplateElement.getParentElem();
    		  }
    	  }
      }

      SerializationHandler rhandler = transformer.getSerializationHandler();

      if (value != null) {
            int xObjectType = value.getType();
            
            String strVal = null;
    
            switch (xObjectType) {           
                case XObject.CLASS_NODESET :
                  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)value;                  
                                    
                  if (!m_copy_namespaces) {
                	 copyOfNodeSetStripNsNodes(xmlNodeCursorImpl, transformer, xctxt, type, validationStr, rhandler);                	                 	 
                  }
                  else {
                	  xmlNodeCursorImpl.setTypeAttrForValidation(type);                                                      
                	  xmlNodeCursorImpl.setValidationAttrForValidation(validationStr);

                	  if (func != null) {
                		  try {
                			  DTMCursorIterator iter1 = xmlNodeCursorImpl.iter();
                			  int nextNode = DTM.NULL;
                			  
                			  while ((nextNode = iter1.nextNode()) != DTM.NULL) {
                				  xctxt.pushCurrentNode(nextNode);
                				  
                				  XObject evalResult = evaluateXPathSuffixFunction(xctxt, srcLocator, func, xmlNodeCursorImpl);
                				  
                				  ResultSequence rSeq = new ResultSequence();
                				  rSeq.add(evalResult);
                				  
                				  copyOfActionOnResultSequence(rSeq, transformer, rhandler, xctxt, false, this);
                			  }
                		  }
                		  finally {
                			  xctxt.popCurrentNode();
                		  }
                	  }
                	  else if (dfc != null) {
                		  try {
                			  DTMCursorIterator iter1 = xmlNodeCursorImpl.iter();
                			  int nextNode = DTM.NULL;
                			  
                			  while ((nextNode = iter1.nextNode()) != DTM.NULL) {
                				  xctxt.pushCurrentNode(nextNode);
                				  
                				  XObject evalResult = evaluateXPathSuffixDfc(xctxt, dfc, xmlNodeCursorImpl);
                				  
                				  ResultSequence rSeq = new ResultSequence();
                				  rSeq.add(evalResult);
                				  
                				  copyOfActionOnResultSequence(rSeq, transformer, rhandler, xctxt, false, this);
                			  }
                		  }
                		  finally {
                			  xctxt.popCurrentNode();
                		  }
                	  }
                	  else {
                		  copyOfActionOnNodeSet(xmlNodeCursorImpl, transformer, rhandler, xctxt);
                	  }
                  }
                  
                  break;
                case XObject.CLASS_RTREEFRAG :
                  SerializerUtils.outputResultTreeFragment(rhandler, value, transformer.getXPathContext());
                  
                  break;
                case XObject.CLASS_RESULT_SEQUENCE :         
                  ResultSequence rSeq = (ResultSequence)value;
                  
                  if (!m_copy_namespaces) {
                	  copyOfXdmSequenceStripNsNodes(rSeq, transformer, xctxt, type, validationStr, rhandler);
                  }
                  else {
                	  copyOfActionOnResultSequence(rSeq, transformer, rhandler, xctxt, false, this);
                  }
                  
                  break;
                case XObject.CLASS_ARRAY : 
                  XPathArray xpathArray = (XPathArray)value;
                  List<XObject> nativeArr = xpathArray.getNativeArray();
                  ResultSequence resultSequenceArr = getResultSequenceFromXPathArray(nativeArr);
                  
                  if (!m_copy_namespaces) {
                	  copyOfXdmSequenceStripNsNodes(resultSequenceArr, transformer, xctxt, type, validationStr, rhandler);
                  }
                  else {
                      copyOfActionOnResultSequence(resultSequenceArr, transformer, rhandler, xctxt, false, this);
                  }
                  
                  break;
                case XObject.CLASS_UNKNOWN :
                  if (value instanceof XMLAttribute) {
                	  XMLAttribute xmlAttribute = (XMLAttribute)value;                	  
                	  String prefix = xmlAttribute.getPrefix();
                	  String localName = xmlAttribute.getLocalName();                	                  	   
                	  String ns = xmlAttribute.getNamespaceUri();
                	  String attrValue = xmlAttribute.getAttrValue();
                	  String rawName = (prefix != null && !"".equals(prefix)) ? prefix + ":" + localName : localName;  
                	  rhandler.addAttribute(
                			             ns, 
                			             localName, 
                			             rawName, 
                			             "CDATA", 
                			             attrValue, false);
                  }
                  
                  break;	
                default :
                  // no op
            }
            
            if ((value instanceof XBoolean) || (value instanceof XNumber) || 
                                                                      (value instanceof XString)) {
                strVal = value.str();
                rhandler.characters(strVal.toCharArray(), 0, strVal.length());
            }
            else if (value instanceof XSAnyAtomicType) {
                strVal = ((XSAnyAtomicType)value).stringValue();
                rhandler.characters(strVal.toCharArray(), 0, strVal.length());
            }
            else if (value instanceof XSUntypedAtomic) {
                strVal = ((XSUntypedAtomic)value).stringValue();
                rhandler.characters(strVal.toCharArray(), 0, strVal.length());
            }
            else if (value instanceof XSUntyped) {
                strVal = ((XSUntyped)value).stringValue();
                rhandler.characters(strVal.toCharArray(), 0, strVal.length());
            }
            else if (value instanceof XPathMap) {
                throw new TransformerException("XTDE0450 : Cannot add an XSL map to an xdm node tree, "
                                                                             							+ "via XSL copy-of instruction.", srcLocator);
            }
            else if (value instanceof XPathInlineFunction) {
                throw new TransformerException("XTDE0450 : Cannot add an XSL function item to an xdm node tree, "
                                                                             							+ "via XSL copy-of instruction.", srcLocator);
            }
      }

    }
    catch(org.xml.sax.SAXException se) {
        throw new TransformerException(se);
    }
    finally {
      if (transformer.getDebug()) {
         transformer.getTraceManager().emitTraceEndEvent(this);
      }
      
      if (isXPathExprStrCheck) {
    	 xctxt.popCurrentNode(); 
      }
    }

  }

  /**
   * Add a child to the child list.
   *
   * @param newChild Child to add to this node's child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {

	  String lineNo = String.valueOf(newChild.getLineNumber());
	  String columnNo = String.valueOf(newChild.getColumnNumber());

	  error(XSLTErrorResources.ER_CANNOT_ADD,
										  new Object[]{ newChild.getNodeName(),
												  this.getNodeName(), lineNo, columnNo });
	  
	  return null;
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if (callAttrs) {
  	   m_selectExpression.getExpression().callVisitors(m_selectExpression, visitor);
  	}
  	
    super.callChildVisitors(visitor, callAttrs);
  }
  
  /**
   * Method definition, to do xsl:copy-of action on an XMLNodeCursorImpl 
   * object instance.
   * 
   * @param xmlNodeCursorImpl								The supplied XMLNodeCursorImpl object
   *                                                        instance.
   * @param transformer										The supplied TransformerImpl object
   *                                                        instance
   * @param handler                                         The supplied SerializationHandler object
   *                                                        instance.
   * @param xctxt                                           The supplied XPathContext object instance
   * @throws TransformerException
   * @throws SAXException
   */
  public static void copyOfActionOnNodeSet(XMLNodeCursorImpl xmlNodeCursorImpl, TransformerImpl transformer, SerializationHandler handler, 
		                                                                                          XPathContext xctxt) throws TransformerException, SAXException {	  	  	  	  
	  
	  DTMCursorIterator dtmIter = xmlNodeCursorImpl.iter();

      DTMTreeWalker tw = new TreeWalker2Result(transformer, handler);
      
      DTM dtm1 = xmlNodeCursorImpl.getDtm();      
      int pos = dtmIter.nextNode();

      while (pos != DTM.NULL) {    	  
          DTM dtm = null;
          
          if (dtm1 == null) {
        	  dtm = (xctxt.getDTMManager()).getDTM(pos);  
          }
          else {
        	  dtm = dtm1;
        	  tw.setDTM(dtm);
          }                                        
    	  
          short nodeType = dtm.getNodeType(pos);  

          if (nodeType == DTM.DOCUMENT_NODE) {
        	 /**
        	  * From the, first XML element node child of the document node, validate all
        	  * these XML sibling element nodes individually if required by XSL stylesheet,
        	  * and emit the nodes to XSL transform's output if validation succeeds.
        	  */
        	  
             for (int child = dtm.getFirstChild(pos); child != DTM.NULL; 
                                                              child = dtm.getNextSibling(child)) {
                 validateAndEmitElementNode(xmlNodeCursorImpl, xctxt, tw, child, dtm);
             }
          }          
          else if (nodeType == DTM.ELEMENT_NODE) {
        	  // Validate an XML element node if required by XSL stylesheet, and emit 
        	  // the node to XSL transform's output if validation succeeds.
        	  
        	  validateAndEmitElementNode(xmlNodeCursorImpl, xctxt, tw, pos, dtm);      		       		  
          }
          else if ((nodeType == DTM.ATTRIBUTE_NODE) || (nodeType == DTM.NAMESPACE_NODE)) {
        	  // Validate an XML attribute node if required by XSL stylesheet, and emit 
        	  // the node to XSL transform's output if validation succeeds.
        	  
        	  // XML namespace nodes are emitted to, XSL result tree as attributes
        	  
        	  validateAndEmitAttributeNode(xmlNodeCursorImpl, handler, xctxt, pos, dtm);
          }
          else {
        	  tw.traverse(pos);
          }
          
          pos = dtmIter.nextNode();
      } 
  }
  
  /**
   * Method definition, to do xsl:copy-of action on the supplied xdm sequence.
   * 
   * @param rSeq                                  The supplied xdm sequence object
   *                                              instance.
   * @param transformer                           An XSL transformer object instance
   * @param serializationHandler                  An XSL serializer object instance
   * @param xctxt                                 An XPath context object instance
   * @param isXslSeqDelimEmit                     Boolean value, indicating whether an
   *                                              internal delimiter should be emitted,
   *                                              when serializing an xdm sequence.                      
   * @param elemTemplateElem                      The current ElemTemplateElement object
   *                                              instance.
   * @throws TransformerException
   * @throws SAXException
   */
  public static void copyOfActionOnResultSequence(ResultSequence rSeq, TransformerImpl transformer, SerializationHandler serializationHandler, 
                                                  XPathContext xctxt, boolean isXslSeqDelimEmit, ElemTemplateElement elemTemplateElem) 
                                                		                                                                             throws TransformerException, SAXException {
      char[] spaceCharArr = new char[1];      
      spaceCharArr[0] = SPACE_CHAR;
      
      String strVal = null;
      
      int rSeqLength = rSeq.size();      
      
      for (int idx = 0; idx < rSeqLength; idx++) {             
         XObject xdmItem = rSeq.item(idx);
         
         if (xdmItem instanceof XMLNodeCursorImpl) {
        	 XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xdmItem;
        	 
        	 String nodeStrValue = xmlNodeCursorImpl.str();
        	 
        	 if ((Constants.XSL_DOCUMENT_INSTRUCTION_MARKER).equals(nodeStrValue)) {
        		continue; 
        	 }
        	 
        	 int nodeHandle = xmlNodeCursorImpl.asNode(xctxt);
        	 DTM dtm = xctxt.getDTM(nodeHandle);
        	 boolean flg1 = false;
        	 
        	 if (dtm.getNodeType(nodeHandle) == DTM.ELEMENT_NODE) {
        		 Node node = dtm.getNode(nodeHandle);
        		 String nodeName = node.getNodeName();
        		 
        		 try {
        			 if (nodeName.startsWith("b_")) {        				 
        				 Integer int1 = Integer.valueOf(nodeName.substring(2));        				 
        				 Element elemNode = (Element)node;
          				 xdmItem = new XString(elemNode.getTextContent());
        			 }
        			 else {
        				 flg1 = true;
        			 }
        		 }
        		 catch (Exception ex) {
        			 flg1 = true;
        		 }        		         		 
        	 }
        	 
        	 if (flg1) {
        		 xdmItem = rSeq.item(idx);
        		 xdmItem = xdmItem.getFresh();
    		 }
         }
         else if (xdmItem instanceof XdmAttributeItem) {
        	 XdmAttributeItem xdmAttributeItem = (XdmAttributeItem)xdmItem;
        	 String localName = xdmAttributeItem.getAttrLocalName();
        	 String namespace = xdmAttributeItem.getAttrNodeNs();
        	 String attrValueStr = xdmAttributeItem.getAttrStrValue();
        	         	 
        	 String rawName = localName;
        	 List<XMLNSDecl> prefixTable = (List<XMLNSDecl>)elemTemplateElem.getPrefixTable();
    	     String nsPrefix = XslTransformEvaluationHelper.getPrefixFromNsUri(namespace, prefixTable);
        	 
    	     if (nsPrefix != null) {
        		rawName = nsPrefix + ":" + rawName;  
        	 }
        	 
        	 serializationHandler.addAttribute(
						        			 namespace,
						        			 localName,
						        			 rawName,
						                     "CDATA",
						                     attrValueStr, true);
         }
         else if (xdmItem instanceof XSQName) {
        	 XSQName xsQName = (XSQName)xdmItem;
        	 String localPart = xsQName.getLocalPart();
        	 String nsUri = xsQName.getNamespaceUri();        	 
        	 
        	 if (Constants.XSL_ERROR_NAMESACE.equals(nsUri)) {
        	     List<XMLNSDecl> prefixTable = (List<XMLNSDecl>)elemTemplateElem.getPrefixTable();
        	     String nsPrefix = XslTransformEvaluationHelper.getPrefixFromNsUri(nsUri, prefixTable);
        	     xdmItem = new XString(nsPrefix + ":" + localPart);
        	 }
         }
         
         if ((xdmItem instanceof XBoolean) || (xdmItem instanceof XString)) {
             strVal = xdmItem.str();
             
             if (isXslSeqDelimEmit) {
                 strVal = strVal + ElemSequence.STRING_VAL_SER_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 
                 if (idx < (rSeq.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 } 
             }
         }
         else if ((xdmItem instanceof XNumber) || (xdmItem instanceof XSDouble)) {
        	 strVal = XslTransformEvaluationHelper.getStrVal(xdmItem);
        	 
        	 if (isXslSeqDelimEmit) {
                 strVal = strVal + ElemSequence.STRING_VAL_SER_DOUBLE_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 
                 if (idx < (rSeq.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 }
             }
         }
         else if (xdmItem instanceof XSInteger) {
        	 strVal = XslTransformEvaluationHelper.getStrVal(xdmItem);
        	 
        	 if (isXslSeqDelimEmit) {
                 strVal = strVal + ElemSequence.STRING_VAL_SER_INTEGER_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 
                 if (idx < (rSeq.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 }
             }
         }
         else if (xdmItem instanceof XSDecimal) {
        	 strVal = XslTransformEvaluationHelper.getStrVal(xdmItem);
        	 
        	 if (isXslSeqDelimEmit) {
                 strVal = strVal + ElemSequence.STRING_VAL_SER_DECIMAL_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 
                 if (idx < (rSeq.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 }
             }
         }
         else if (xdmItem instanceof XSFloat) {
        	 strVal = XslTransformEvaluationHelper.getStrVal(xdmItem);
        	 
        	 if (isXslSeqDelimEmit) {
                 strVal = strVal + ElemSequence.STRING_VAL_SER_FLOAT_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 
                 if (idx < (rSeq.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 }
             }
         }
         else if (xdmItem instanceof XSUntypedAtomic) {
             strVal = ((XSUntypedAtomic)xdmItem).stringValue();
             
             if (isXslSeqDelimEmit) {
                 strVal = strVal + ElemSequence.STRING_VAL_SER_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 if (idx < (rSeq.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 }
             }
         }
         else if (xdmItem instanceof XSAnyAtomicType) {
            strVal = ((XSAnyAtomicType)xdmItem).stringValue();
            
            if (isXslSeqDelimEmit) {
                strVal = strVal + ElemSequence.STRING_VAL_SER_SUFFIX;
                serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
            }
            else {
                serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                
                if (idx < (rSeq.size() - 1)) {                     
                   serializationHandler.characters(spaceCharArr, 0, 1);
                }
            }
         }         
         else if (xdmItem instanceof XSUntyped) {
             strVal = ((XSUntyped)xdmItem).stringValue();
             
             if (isXslSeqDelimEmit) {
                 strVal = strVal + ElemSequence.STRING_VAL_SER_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 
                 if (idx < (rSeq.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 }
             }
          }
         else if (xdmItem.getType() == XObject.CLASS_NODESET) {                 
             copyOfActionOnNodeSet((XMLNodeCursorImpl)xdmItem, transformer, serializationHandler, xctxt);
         }
         else if (xdmItem.getType() == XObject.CLASS_RESULT_SEQUENCE) {                 
             copyOfActionOnResultSequence((ResultSequence)xdmItem, transformer, serializationHandler, xctxt, 
            		                                                                                      isXslSeqDelimEmit, elemTemplateElem);
         }
      } 
   }
  
  /**
   * Method definition, to get the contents for an xdm array, 
   * as an xdm sequence. 
   * 
   * @param nativeArr                    The supplied list object instance, 
   *                                     that is the native contents for an 
   *                                     xdm array.
   * @return                             An xdm sequence object instance,
   *                                     corresponding to the supplied xdm 
   *                                     array.
   */
  public static ResultSequence getResultSequenceFromXPathArray(List<XObject> nativeArr) {
	 
	  ResultSequence rSeq = new ResultSequence();
	
	 int arrSize = nativeArr.size();
	 
	 for (int idx = 0; idx < arrSize; idx++) {
	    rSeq.add(nativeArr.get(idx)); 	
	 }
	
	 return rSeq;
  }
  
  /**
   * Method definition, to do validation for an xdm element node 
   * with an XML schema type definition, or an XML schema element 
   * declaration (when either of these are provided with an XSL 
   * transformation). If XML schema validation has been requested for an 
   * xdm element node, and an element node is valid with the schema 
   * type, or schema element declaration, then an xdm element node 
   * is emitted to XSL transformation's output.
   * 
   * If XML Schema validation has not been requested, for an xdm element 
   * node, with that case as well, an xdm element node is emitted to 
   * XSL transformation's output. 
   * 
   * @param xmlNodeCursorImpl                     An xdm node, for which xsl:copy-of result
   *                                              has been requested. 
   * @param xctxt                                 An XPath context object instance
   * @param tw                                    DTMTreeWalker object instance
   * @param nodeHandle                            DTM id for an xdm node, for which xsl:copy-of
   *                                              result has been requested.
   * @param dtm                                   Xalan-J DTM object instance, corresponding to
   *                                              XSL transformation, XML source document.
   * @throws TransformerException
   */    
  private static void validateAndEmitElementNode(XMLNodeCursorImpl xmlNodeCursorImpl, XPathContext xctxt, 
                                                 DTMTreeWalker tw, int nodeHandle, DTM dtm) throws TransformerException {	  

	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  try {        		  
		  Node node = dtm.getNode(nodeHandle);

		  QName type = xmlNodeCursorImpl.getTypeAttrForValidation();
		  String validation = xmlNodeCursorImpl.getValidationAttrForValidation();

		  if (type != null) {
			  StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
			  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
			  
			  if (xsModel != null) {
				  String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);        				  
				  XSTypeDefinition xsTypeDefn = xsModel.getTypeDefinition(type.getLocalName(), type.getNamespace());
				  
				  if (XPathSequenceTypeSupport.isXmlStrValid(xmlStr, null, xsTypeDefn)) {
					  tw.traverse(nodeHandle); 
				  }
			  }
			  else {
				  throw new TransformerException("FODC0005 : An XSL copy-of instruction has 'type' attribute to request validation of copy-of's result, but an XML input document has not "
																 				   										                               + "been validated using schema supplied via XSL import-schema instruction.", 
																 				   												                         srcLocator); 
			  }
		  }
		  else if (validation != null) {
			  if ((Constants.XS_VALIDATION_STRICT_STRING).equals(validation)) {
				  StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
				  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
				  
				  if (xsModel != null) {
					  String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
					  String nodeLocalName = node.getLocalName();
					  String nodeNamespace = node.getNamespaceURI();
					  XSElementDecl schemaElemDecl = (XSElementDecl)(xsModel.getElementDeclaration(nodeLocalName, nodeNamespace));
					  
					  if (schemaElemDecl != null) {
						  if (XPathSequenceTypeSupport.isXmlStrValid(xmlStr, schemaElemDecl, null)) {
							  tw.traverse(nodeHandle); 
						  }
					  }
					  else {
						  throw new TransformerException("FODC0005 : An XSL copy-of instruction has 'validation' attribute with value '" + 
																															     Constants.XS_VALIDATION_STRICT_STRING + "' to request validation of "
																															     + "copy-of's result, but the schema used to validate "
																															     + "an XML input document doesn't have global element declaration for "
																															     + "an element node produced by copy-of instruction.", srcLocator);
					  }
				  }
				  else {
					  throw new TransformerException("FODC0005 : An XSL copy-of instruction has 'validation' attribute to request "
																												 			    + "validation of copy-of's result, but an XML input "
																															    + "document has not been validated using schema supplied "
																															    + "via XSL import-schema instruction.", srcLocator); 
				  }
			  }
			  else if ((Constants.XS_VALIDATION_LAX_STRING).equals(validation)) {
				  StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
				  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
				  
				  if (xsModel != null) {
					  String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
					  String nodeLocalName = node.getLocalName();
					  String nodeNamespace = node.getNamespaceURI();
					  XSElementDecl schemaElemDecl = (XSElementDecl)(xsModel.getElementDeclaration(nodeLocalName, nodeNamespace));
					  
					  if (schemaElemDecl != null) {
						  if (XPathSequenceTypeSupport.isXmlStrValid(xmlStr, schemaElemDecl, null)) {
							  tw.traverse(nodeHandle); 
						  }
					  }            				  
				  }            			  
			  }

			  // The validation value 'strip' requires no validation.

			  // The validation value 'preserve' is currently not implemented.
		  }
		  else {
			  tw.traverse(nodeHandle); 
		  }
	  }
	  catch (TransformerException ex) {
		  throw new TransformerException(ex.getMessage(), srcLocator); 
	  }
	  catch (Exception ex) {
		  String errMesg = ex.getMessage();
		  
		  throw new TransformerException("XTTE1540 : An error occured while evaluating an XSL stylesheet "
																										  + "copy-of instruction." 
																										  + ((errMesg != null) ? " " + errMesg : ""), srcLocator);
	  }
	  finally {
		  xmlNodeCursorImpl.setTypeAttrForValidation(null);
		  xmlNodeCursorImpl.setValidationAttrForValidation(null); 
	  }
   }
  
   /**
    * Method definition, to do validation for an xdm attribute node 
    * with an XML schema type definition, or an XML schema attribute 
    * declaration (when either of these are provided with an XSL 
    * transformation). If XML schema validation has been requested for an 
    * xdm attribute node, and the attribute node is valid with the schema 
    * type, or schema attribute declaration, then an xdm attribute node 
    * is emitted to XSL transformation's output.
    * 
    * If XML Schema validation has not been requested, for an xdm attribute 
    * node, with that case as well, an xdm attribute node is emitted to 
    * XSL transformation's output. 
    * 
    * @param xmlNodeCursorImpl                     An xdm node, for which xsl:copy-of result
    *                                              has been requested.
    * @param serializationHandler                  An XSL transformation SerializationHandler object
    *                                              instance. 
    * @param xctxt                                 An XPath context object instance
    * @param pos                                   DTM id for an xdm node, for which xsl:copy-of
    *                                              result has been requested.
    * @param dtm                                   Xalan-J DTM object instance, corresponding to
    *                                              XSL transformation, XML source document.
    * @throws TransformerException
    */
   private static void validateAndEmitAttributeNode(XMLNodeCursorImpl xmlNodeCursorImpl, SerializationHandler serializationHandler,
												    					                XPathContext xctxt, int pos, DTM dtm) throws TransformerException {
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  try {
		  Node node = dtm.getNode(pos);
		  String attrLocalName = node.getLocalName();
		  String attrNodeNs = node.getNamespaceURI();
		  String attrStrValue = node.getNodeValue();
		  
		  QName type = xmlNodeCursorImpl.getTypeAttrForValidation();
		  String validation = xmlNodeCursorImpl.getValidationAttrForValidation();

		  if (type != null) {
			  StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
			  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
			  
			  if (xsModel != null) {
				  XSTypeDefinition xsTypeDefn = xsModel.getTypeDefinition(type.getLocalName(), type.getNamespace());
				  
				  if (xsTypeDefn != null) {
					  if (xsTypeDefn instanceof XSSimpleType) {
						  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)xsTypeDefn;
						  
						  try {
							  xsSimpleTypeDecl.validate(attrStrValue, null, null);
						  } 
						  catch (InvalidDatatypeValueException ex) {							
							  throw new TransformerException("FODC0005 : An XML attribute '" + attrLocalName + "' that has to be emitted by XSL copy-of "
																											 + "instruction, has a value that is not valid with type '" + 
																											 type.getLocalName() + "' referred by copy-of instruction's "
																											 + "'type' attribute. " + ex.getMessage(), srcLocator);
						  }
					  }
					  else {
						  throw new TransformerException("FODC0005 : An XSL copy-of instruction refers a type '" + type.getLocalName() + 
																											  "' that is not a schema simpleType, that cannot be used to validate "
																											  + "an attribute value.", srcLocator);
					  }
				  }
				  else {
					  throw new TransformerException("FODC0005 : An XSL copy-of instruction has 'type' attribute with "
																											  + "value '" + type.getLocalName() + "' to request "
																											  + "validation of copy-of's result, but the schema referred via "
																											  + "XSL import-schema instruction does'nt have a global type definition "
																											  + "with name '" + type.getLocalName() + "'.", srcLocator);
				  }
			  }
			  else {
				  throw new TransformerException("FODC0005 : An XSL copy-of instruction has 'type' attribute to request "
																											  + "validation of copy-of's result, but an XML input document has not "
																											  + "been validated using schema supplied via XSL import-schema instruction.", 
																											  srcLocator); 
			  } 
		  }
		  else if (validation != null) {
			  if ((Constants.XS_VALIDATION_STRICT_STRING).equals(validation)) {
				  StylesheetRoot stylesheetRoot = XslTransformData.m_stylesheetRoot;
				  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
				  
				  if (xsModel != null) {
					  XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(attrLocalName, attrNodeNs);
					  
					  if (attrDecl != null) {
						  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)attrDecl.getTypeDefinition();
						  
						  try {
							  xsSimpleTypeDecl.validate(attrStrValue, null, null);
						  } 
						  catch (InvalidDatatypeValueException ex) {							
							  throw new TransformerException("FODC0005 : An XML attribute '" + attrLocalName + "' that has to be emitted by XSL copy-of "
																											 + "instruction, has a value that is not valid with attribute "
																											 + "declaration available in the schema." + ex.getMessage(), 
																											 srcLocator);
						  }
					  }
					  else {
						  throw new TransformerException("FODC0005 : An XML attribute '" + attrLocalName + "' that has to be emitted by XSL copy-of "
																											 + "instruction, doesn't have a corresponding attribute declaration in the "
																											 + "schema to validate with. The validation 'strict' has been requested.", srcLocator);
					  }
				  }
				  else {
					  throw new TransformerException("XTTE1540 : An XSL copy-of instruction's attribute \"validation\" has value 'strict', but "
																									         + "an XML input document has not been validated with a schema "
																									         + "using XSL import-schema instruction.", srcLocator);
				  }
			  }
			  else if ((Constants.XS_VALIDATION_LAX_STRING).equals(validation)) {
				  XSModel xsModel = (XslTransformData.m_stylesheetRoot).getXsModel();
				  
				  if (xsModel != null) {
					  XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(attrLocalName, attrNodeNs);
					  
					  if (attrDecl != null) {
						  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)attrDecl.getTypeDefinition();
						  
						  try {
							  xsSimpleTypeDecl.validate(attrStrValue, null, null);
						  } 
						  catch (InvalidDatatypeValueException ex) {							
							  throw new TransformerException("FODC0005 : An XML attribute '" + attrLocalName + "' that has to be emitted by XSL copy-of "
																									         + "instruction, has a value that is not valid with attribute "
																									         + "declaration available in the schema." + ex.getMessage(), srcLocator);
						  }
					  }
				  }
			  }

			  // The validation value 'strip' requires no validation.

			  // The validation value 'preserve' is currently not implemented.
		  }

		  // Emit attribute to XSL transformation's output
		  SerializerUtils.addAttribute(serializationHandler, pos);
	  }
	  finally {
		  xmlNodeCursorImpl.setTypeAttrForValidation(null);
		  xmlNodeCursorImpl.setValidationAttrForValidation(null);
	  }
   }
   
   /**
    * Method definition, to copy a supplied xdm sequence, to XSL result tree,
    * where all xdm element nodes within the sequence are stripped with their
    * XML namespace nodes before copying them to an XSL result tree.
    * 
    * @param rSeq                                 The supplied xdm sequence object.
    * @param transformer                          An XSL TransformerImpl object
    * @param xctxt                                An XPath context object
    * @param type                                 An xdm type's qname value  if available,
    *                                             for validation of an xdm node.
    * @param validationStr                        The value of xsl:copy-of's 'validation'
    *                                             attribute of available, with possible
    *                                             values 'strict', 'lax', 'preserve', 'strip'.
    * @param rhandler                             Xalan's XSL SerializationHandler object instance.
    * @throws TransformerException
    * @throws SAXException
    */
   private void copyOfXdmSequenceStripNsNodes(ResultSequence rSeq, TransformerImpl transformer, XPathContext xctxt, QName type,
 		                                                                                        String validationStr, SerializationHandler rhandler)
 				                                                                                                    throws TransformerException, SAXException {	  
 	  int rSeqLength = rSeq.size();
 	  ResultSequence rSeq2 = new ResultSequence(); 
 	  
 	  for (int idx = 0; idx < rSeqLength; idx++) {
 		  XObject xObj = rSeq.item(idx);
 		  
 		  if (xObj instanceof XMLNodeCursorImpl) {
 			  XMLNodeCursorImpl xmlNodeCursorImpl = (XMLNodeCursorImpl)xObj;
 			  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
 			  
 			  int nextNode = DTM.NULL;
 			  
 			  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
 				  XMLNodeCursorImpl xdmNode = new XMLNodeCursorImpl(nextNode, xctxt);
 				  DTM dtm = xctxt.getDTM(nextNode);
 				  
 				  if (dtm.getNodeType(nextNode) == DTM.ELEMENT_NODE) {
 					  xdmNode = XslTransformEvaluationHelper.stripNsNodesFromXdmElementNode(xdmNode, xctxt);
 				  }

 				  xdmNode.setTypeAttrForValidation(type);                                                      
 				  xdmNode.setValidationAttrForValidation(validationStr);

 				  rSeq2.add(xdmNode);
 			  } 
 		  }
 		  else {
 			  rSeq2.add(xObj);
 		  }
 	  }

 	  copyOfActionOnResultSequence(rSeq2, transformer, rhandler, xctxt, false, this);
   }

   /**
    * Method definition, to copy a supplied xdm node set, to XSL result tree,
    * and stripping XML namespace nodes from supplied element nodes before copying 
    * the supplied nodeset to an XSL result tree.
    * 
    * @param xmlNodeCursorImpl                    The supplied xdm nodeset object instance
    * @param transformer                          An XSL TransformerImpl object instance
    * @param xctxt                                An XPath context object instance
    * @param type                                 An xdm type's qname value if available,
    *                                             for validation of an xdm node.
    * @param validationStr                        The value of xsl:copy-of instruction's 
    *                                             'validation' attribute if available, with possible
    *                                             values 'strict', 'lax', 'preserve', 'strip'.
    * @param rhandler                             An XSL transformation SerializationHandler object 
    *                                             instance. 
    * @throws TransformerException
    * @throws SAXException
    */
   private void copyOfNodeSetStripNsNodes(XMLNodeCursorImpl xmlNodeCursorImpl, TransformerImpl transformer, XPathContext xctxt, QName type,
 		                                                                                          String validationStr, SerializationHandler rhandler)
 				                                                                                                         throws TransformerException, SAXException {
 	  DTMCursorIterator dtmCursorIterator = xmlNodeCursorImpl.iter();
 	  int nextNode;
 	  
 	  while ((nextNode = dtmCursorIterator.nextNode()) != DTM.NULL) {
 		  XMLNodeCursorImpl xdmNode = new XMLNodeCursorImpl(nextNode, xctxt);
 		  DTM dtm = xctxt.getDTM(nextNode);
 		  
 		  if (dtm.getNodeType(nextNode) == DTM.ELEMENT_NODE) {
 			  xdmNode = XslTransformEvaluationHelper.stripNsNodesFromXdmElementNode(xdmNode, xctxt);
 		  }

 		  xdmNode.setTypeAttrForValidation(type);                                                      
 		  xdmNode.setValidationAttrForValidation(validationStr);

 		  copyOfActionOnNodeSet(xdmNode, transformer, rhandler, xctxt);
 	  }
   }

}
