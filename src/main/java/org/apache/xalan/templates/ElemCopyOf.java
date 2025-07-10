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
package org.apache.xalan.templates;

import java.util.List;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.TreeWalker2Result;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xalan.xslt.util.XslTransformSharedDatastore;
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
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.composite.SequenceTypeSupport;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathArray;
import org.apache.xpath.objects.XPathInlineFunction;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;
import org.apache.xpath.types.XMLAttribute;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import xml.xpath31.processor.types.XSAnyAtomicType;
import xml.xpath31.processor.types.XSUntyped;
import xml.xpath31.processor.types.XSUntypedAtomic;

/**
 * Implementation of XSLT xsl:copy-of instruction.
 * 
 * @author Scott Boag <scott_boag@us.ibm.com>
 * @author Morris Kwan <mkwan@apache.org>
 * @author Christine Li <jycli@apache.org>
 *         
 * @author Mukul Gandhi <mukulg@apache.org>
 *         (XSLT 3 specific changes, to this class)
 * 
 * @xsl.usage advanced
 */
public class ElemCopyOf extends ElemTemplateElement
{
  static final long serialVersionUID = -7433828829497411127L;

  /**
   * The required select attribute contains an expression.
   * @serial
   */
  public XPath m_selectExpression = null;
  
  /**
   * True if the pattern is a simple ".".
   */
  private boolean m_isDot = false;
  
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
   * @return The token ID for this element
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

    try {      
      XPathContext xctxt = transformer.getXPathContext();
        
      setXPathContext(xctxt);
        
      SourceLocator srcLocator = xctxt.getSAXLocator();
      
      QName type = getType();
      String validationStr = getValidation();
        
      if ((type != null) && (validationStr != null)) {
      	  throw new TransformerException("XTTE1540 : An xsl:copy-of instruction cannot have both the attributes "
      	  																						+ "'type' and 'validation'.", srcLocator); 
      }      
              
      int sourceNode = xctxt.getCurrentNode();
      
      XObject value = null;
      
      XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
      if (m_isDot && (xpath3ContextItem != null)) {
          value = xpath3ContextItem;  
      }
      else {
          value = m_selectExpression.execute(xctxt, sourceNode, this);
      }

      if (transformer.getDebug()) {
         transformer.getTraceManager().emitSelectedEvent(sourceNode, this,
                                                                        "select", m_selectExpression, value);
      }

      SerializationHandler rhandler = transformer.getSerializationHandler();

      if (value != null) {
            int xObjectType = value.getType();
            String strVal = null;
    
            switch (xObjectType) {           
                case XObject.CLASS_NODESET :
                  XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)value;
                  xNodeSet.setTypeAttrForValidation(type);
                  if (validationStr != null) {
                	  if (!isValidationStrOk(validationStr)) {
                		 throw new TransformerException("XTTE1540 : An xsl:copy-of instruction's attribute 'validation' can only have one of following "
                                                                                                          + "values : strict, lax, preserve, strip.", srcLocator);  
                	  }
                  }
                  xNodeSet.setValidationAttrForValidation(validationStr);
                  copyOfActionOnNodeSet(xNodeSet, transformer, rhandler, xctxt);          
                  break;
                case XObject.CLASS_RTREEFRAG :
                  SerializerUtils.outputResultTreeFragment(
                                                        rhandler, value, transformer.getXPathContext());
                  break;
                case XObject.CLASS_RESULT_SEQUENCE :         
                  ResultSequence resultSequence = (ResultSequence)value;          
                  copyOfActionOnResultSequence(resultSequence, transformer, rhandler, xctxt, false);          
                  break;
                case XObject.CLASS_ARRAY : 
                  XPathArray xpathArray = (XPathArray)value;
                  List<XObject> nativeArr = xpathArray.getNativeArray();
                  ResultSequence resultSequenceArr = getResultSequenceFromXPathArray(nativeArr);
                  copyOfActionOnResultSequence(resultSequenceArr, transformer, rhandler, xctxt, false);
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
                throw new TransformerException("XTDE0450 : Cannot add a map to an xdm node tree, "
                                                                             + "via xsl:copy-of instruction.", srcLocator);
            }
            else if (value instanceof XPathInlineFunction) {
                throw new TransformerException("XTDE0450 : Cannot add a function item to an xdm node tree, "
                                                                             + "via xsl:copy-of instruction.", srcLocator);
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

    error(XSLTErrorResources.ER_CANNOT_ADD,
          new Object[]{ newChild.getNodeName(),
                        this.getNodeName() });
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
   *  Given an an XNodeSet node object, this method does XSL copy-of action on the node.
   */
  public static void copyOfActionOnNodeSet(XMLNodeCursorImpl nodeSet, TransformerImpl transformer, 
                                                                      SerializationHandler serializationHandler, XPathContext xctxt) 
                                                                               throws TransformerException, SAXException {
	  DTMCursorIterator dtmIter = nodeSet.iter();

      DTMTreeWalker tw = new TreeWalker2Result(transformer, serializationHandler);
      int pos;
      
      DTM dtm1 = nodeSet.getDtm();

      while ((pos = dtmIter.nextNode()) != DTM.NULL) {    	  
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
        	 // From the 1st XML element node child of document node, validate all 
        	 // these sibling element nodes individually if required by XSL stylesheet,
        	 // and emit the nodes to XSL transform's output if validation succeeds.
             for (int child = dtm.getFirstChild(pos); child != DTM.NULL; 
                      child = dtm.getNextSibling(child)) {
                 validateAndEmitElementNode(nodeSet, xctxt, tw, child, dtm);
             }
          }          
          else if (nodeType == DTM.ELEMENT_NODE) {
        	  // Validate an XML element node if required by XSL stylesheet, and emit 
        	  // the node to XSL transform's output if validation succeeds.
        	  validateAndEmitElementNode(nodeSet, xctxt, tw, pos, dtm);      		       		  
          }
          else if (nodeType == DTM.ATTRIBUTE_NODE) {
        	  // Validate an XML attribute node if required by XSL stylesheet, and emit 
        	  // the node to XSL transform's output if validation succeeds.
        	  validateAndEmitAttributeNode(nodeSet, serializationHandler, xctxt, pos, dtm);
          }
          else {
              tw.traverse(pos);
          }
      } 
  }
  
  /**
   *  Given an XSL sequence object, this method does xsl:copy-of action on the sequence.
   */
  public static void copyOfActionOnResultSequence(ResultSequence resultSequence, TransformerImpl transformer, 
                                                  SerializationHandler serializationHandler, 
                                                  XPathContext xctxt, boolean xslSeqProc) throws TransformerException, SAXException {
      char[] spaceCharArr = new char[1];      
      spaceCharArr[0] = SPACE_CHAR;
      
      String strVal = null;
      
      for (int idx = 0; idx < resultSequence.size(); idx++) {             
         XObject xdmItem = resultSequence.item(idx);
         
         if ((xdmItem instanceof XBoolean) || (xdmItem instanceof XNumber) || (xdmItem instanceof XString)) {
             strVal = xdmItem.str();
             if (xslSeqProc) {
                 strVal = strVal + ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 if (idx < (resultSequence.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 } 
             }
         }
         else if (xdmItem instanceof XSAnyAtomicType) {
            strVal = ((XSAnyAtomicType)xdmItem).stringValue();
            if (xslSeqProc) {
                strVal = strVal + ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX;
                serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
            }
            else {
                serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                if (idx < (resultSequence.size() - 1)) {                     
                   serializationHandler.characters(spaceCharArr, 0, 1);
                }
            }
         }
         else if (xdmItem instanceof XSUntypedAtomic) {
             strVal = ((XSUntypedAtomic)xdmItem).stringValue();
             if (xslSeqProc) {
                 strVal = strVal + ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 if (idx < (resultSequence.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 }
             }
          }
         else if (xdmItem instanceof XSUntyped) {
             strVal = ((XSUntyped)xdmItem).stringValue();
             if (xslSeqProc) {
                 strVal = strVal + ElemSequence.STRING_VAL_SERIALIZATION_SUFFIX;
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
             }
             else {
                 serializationHandler.characters(strVal.toCharArray(), 0, strVal.length());
                 if (idx < (resultSequence.size() - 1)) {                     
                    serializationHandler.characters(spaceCharArr, 0, 1);
                 }
             }
          }
         else if (xdmItem.getType() == XObject.CLASS_NODESET) {                 
             copyOfActionOnNodeSet((XMLNodeCursorImpl)xdmItem, transformer, serializationHandler, xctxt);
         }
         else if (xdmItem.getType() == XObject.CLASS_RESULT_SEQUENCE) {                 
             copyOfActionOnResultSequence((ResultSequence)xdmItem, transformer, serializationHandler, xctxt, xslSeqProc);
         }
      } 
   }
  
  /**
   * Get the contents of an XPath array, as an ResultSequence object. 
   */
  public static ResultSequence getResultSequenceFromXPathArray(List<XObject> xpathArr) {
	 ResultSequence rSeq = new ResultSequence();
	
	 int arrSize = xpathArr.size();
	 for (int idx = 0; idx < arrSize; idx++) {
	    rSeq.add(xpathArr.get(idx)); 	
	 }
	
	 return rSeq;
  }
  
  /**
   * This method does validation of an XML element node with a schema type or 
   * schema element declaration. If an element node is valid with the schema 
   * type or schema element declaration, then the node is emitted to XSL 
   * transformation's output.
   */
  private static void validateAndEmitElementNode(XMLNodeCursorImpl nodeSet, XPathContext xctxt, 
                                                 DTMTreeWalker tw, int nodeHandle, DTM dtm) throws TransformerException {	  

	  SourceLocator srcLocator = xctxt.getSAXLocator();
	  
	  try {        		  
		  Node node = dtm.getNode(nodeHandle);

		  QName type = nodeSet.getTypeAttrForValidation();
		  String validation = nodeSet.getValidationAttrForValidation();

		  if (type != null) {
			  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.m_stylesheetRoot;
			  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
			  if (xsModel != null) {
				  String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);        				  
				  XSTypeDefinition xsTypeDefn = xsModel.getTypeDefinition(type.getLocalName(), type.getNamespace());
				  if (SequenceTypeSupport.isXmlStrValid(xmlStr, null, xsTypeDefn)) {
					  tw.traverse(nodeHandle); 
				  }
			  }
			  else {
				  throw new TransformerException("FODC0005 : An xsl:copy-of instruction has 'type' attribute to request "
																 				   + "validation of xsl:copy-of's result, but an XML input document has not "
																				   + "been validated using schema supplied via xsl:import-schema instruction.", 
																				  srcLocator); 
			  }
		  }
		  else if (validation != null) {
			  if ((Constants.XS_VALIDATION_STRICT_STRING).equals(validation)) {
				  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.m_stylesheetRoot;
				  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
				  if (xsModel != null) {
					  String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
					  String nodeLocalName = node.getLocalName();
					  String nodeNamespace = node.getNamespaceURI();
					  XSElementDecl schemaElemDecl = (XSElementDecl)(xsModel.getElementDeclaration(nodeLocalName, nodeNamespace));
					  if (schemaElemDecl != null) {
						  if (SequenceTypeSupport.isXmlStrValid(xmlStr, schemaElemDecl, null)) {
							  tw.traverse(nodeHandle); 
						  }
					  }
					  else {
						  throw new TransformerException("FODC0005 : An xsl:copy-of instruction has 'validation' attribute with value '" + 
																				     Constants.XS_VALIDATION_STRICT_STRING + "' to request validation of "
																				     + "xsl:copy-of's result, but the schema used to validate "
																				     + "an XML input document doesn't have global element declaration for "
																				     + "an element node produced by xsl:copy-of instruction.", srcLocator);
					  }
				  }
				  else {
					  throw new TransformerException("FODC0005 : An xsl:copy-of instruction has 'validation' attribute to request "
																	 			    + "validation of xsl:copy-of's result, but an XML input "
																				    + "document has not been validated using schema supplied "
																				    + "via xsl:import-schema instruction.", srcLocator); 
				  }
			  }
			  else if ((Constants.XS_VALIDATION_LAX_STRING).equals(validation)) {
				  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.m_stylesheetRoot;
				  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
				  if (xsModel != null) {
					  String xmlStr = XslTransformEvaluationHelper.serializeXmlDomElementNode(node);
					  String nodeLocalName = node.getLocalName();
					  String nodeNamespace = node.getNamespaceURI();
					  XSElementDecl schemaElemDecl = (XSElementDecl)(xsModel.getElementDeclaration(nodeLocalName, nodeNamespace));
					  if (schemaElemDecl != null) {
						  if (SequenceTypeSupport.isXmlStrValid(xmlStr, schemaElemDecl, null)) {
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
																				  + "xsl:copy-of instruction." 
																				  + ((errMesg != null) ? " " + errMesg : ""), srcLocator);
	  }
	  finally {
		  nodeSet.setTypeAttrForValidation(null);
		  nodeSet.setValidationAttrForValidation(null); 
	  }
   }
  
  /**
   * This method does validation of an XML attribute node with a schema type or 
   * schema attribute declaration. If an attribute node is valid with the schema 
   * type or schema attribute declaration, then the node is emitted to XSL 
   * transformation's output.
   */
   private static void validateAndEmitAttributeNode(XMLNodeCursorImpl nodeSet, SerializationHandler serializationHandler,
												    XPathContext xctxt, int pos, DTM dtm) throws TransformerException {
	  
	  SourceLocator srcLocator = xctxt.getSAXLocator();

	  try {
		  Node node = dtm.getNode(pos);
		  String attrLocalName = node.getLocalName();
		  String attrNodeNs = node.getNamespaceURI();
		  String attrStrValue = node.getNodeValue();
		  
		  QName type = nodeSet.getTypeAttrForValidation();
		  String validation = nodeSet.getValidationAttrForValidation();

		  if (type != null) {
			  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.m_stylesheetRoot;
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
							  throw new TransformerException("FODC0005 : An attribute '" + attrLocalName + "' that has to be emitted by xsl:copy-of "
																						  + "instruction, has a value which is not valid with type '" + 
																						  type.getLocalName() + "' referred by xsl:copy-of instruction's "
																						  + "'type' attribute. " + ex.getMessage(), srcLocator);
						  }
					  }
					  else {
						  throw new TransformerException("FODC0005 : An xsl:copy-of instruction refers a type '" + type.getLocalName() + 
																						  "' that is not a schema simpleType, which cannot be used to validate "
																						  + "an attribute value.", srcLocator);
					  }
				  }
				  else {
					  throw new TransformerException("FODC0005 : An xsl:copy-of instruction has 'type' attribute with "
																						  + "value '" + type.getLocalName() + "' to request "
																						  + "validation of xsl:copy-of's result, but the schema referred via "
																						  + "xsl:import-schema instruction does'nt have a global type definition "
																						  + "with name '" + type.getLocalName() + "'.", srcLocator);
				  }
			  }
			  else {
				  throw new TransformerException("FODC0005 : An xsl:copy-of instruction has 'type' attribute to request "
																						  + "validation of xsl:copy-of's result, but an XML input document has not "
																						  + "been validated using schema supplied via xsl:import-schema instruction.", 
																						  srcLocator); 
			  } 
		  }
		  else if (validation != null) {
			  if ((Constants.XS_VALIDATION_STRICT_STRING).equals(validation)) {
				  StylesheetRoot stylesheetRoot = XslTransformSharedDatastore.m_stylesheetRoot;
				  XSModel xsModel = stylesheetRoot.getXsModel();        			          			          			  
				  if (xsModel != null) {
					  XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(attrLocalName, attrNodeNs);
					  if (attrDecl != null) {
						  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)attrDecl.getTypeDefinition();
						  try {
							  xsSimpleTypeDecl.validate(attrStrValue, null, null);
						  } 
						  catch (InvalidDatatypeValueException ex) {							
							  throw new TransformerException("FODC0005 : An attribute '" + attrLocalName + "' that has to be emitted by xsl:copy-of "
																							  + "instruction, has a value which is not valid with attribute "
																							  + "declaration available in the schema." + ex.getMessage(), 
																							  srcLocator);
						  }
					  }
					  else {
						  throw new TransformerException("FODC0005 : An attribute '" + attrLocalName + "' that has to be emitted by xsl:copy-of "
																							  + "instruction, doesn't have a corresponding attribute declaration in the "
																							  + "schema to validate with. The validation 'strict' has been requested.", srcLocator);
					  }
				  }
				  else {
					  throw new TransformerException("XTTE1540 : An xsl:copy-of instruction's attribute \"validation\" has value 'strict', but "
																									  + "an XML input document has not been validated with a schema "
																									  + "using xsl:import-schema instruction.", srcLocator);
				  }
			  }
			  else if ((Constants.XS_VALIDATION_LAX_STRING).equals(validation)) {
				  XSModel xsModel = (XslTransformSharedDatastore.m_stylesheetRoot).getXsModel();
				  if (xsModel != null) {
					  XSAttributeDeclaration attrDecl = xsModel.getAttributeDeclaration(attrLocalName, attrNodeNs);
					  if (attrDecl != null) {
						  XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl)attrDecl.getTypeDefinition();
						  try {
							  xsSimpleTypeDecl.validate(attrStrValue, null, null);
						  } 
						  catch (InvalidDatatypeValueException ex) {							
							  throw new TransformerException("FODC0005 : An attribute '" + attrLocalName + "' that has to be emitted by xsl:copy-of "
																									  + "instruction, has a value which is not valid with attribute "
																									  + "declaration available in the schema." + ex.getMessage(), 
																									  srcLocator);
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
		  nodeSet.setTypeAttrForValidation(null);
		  nodeSet.setValidationAttrForValidation(null);
	  }
   }

}
