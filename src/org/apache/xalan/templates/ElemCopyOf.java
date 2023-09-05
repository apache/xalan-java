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
package org.apache.xalan.templates;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.TreeWalker2Result;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMTreeWalker;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XString;
import org.apache.xpath.xs.types.XSNumericType;
import org.apache.xpath.xs.types.XSString;
import org.apache.xpath.xs.types.XSUntyped;
import org.apache.xpath.xs.types.XSUntypedAtomic;
import org.xml.sax.SAXException;

/**
 * Implementation of XSLT xsl:copy-of instruction.
 * 
 * XSLT 3.0 spec, provides following definition of xsl:copy-of
 * instruction,
 *  
 * <xsl:copy-of
          select = expression
          copy-accumulators? = boolean
          copy-namespaces? = boolean
          type? = eqname
          validation? = "strict" | "lax" | "preserve" | "strip" />
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
  
  // The following two fields of this class, are used during 
  // XPath.fixupVariables(..) action as performed within object of 
  // this class.    
  private Vector fVars;    
  private int fGlobalsSize;
  
  private final char SPACE_CHAR = ' ';

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
    
    fVars = (Vector)(vnames.clone()); 
    fGlobalsSize = (sroot.getComposeState()).getGlobalsSize();

    if (m_selectExpression != null) {
        m_selectExpression.fixupVariables(vnames, fGlobalsSize);
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
    	transformer.getTraceManager().fireTraceEvent(this);

    try
    {
      XPathContext xctxt = transformer.getXPathContext();
              
      int sourceNode = xctxt.getCurrentNode();
      
      XObject value = null;
      
      XObject xpath3ContextItem = xctxt.getXPath3ContextItem();
      if (m_isDot && (xpath3ContextItem != null)) {
         value = xpath3ContextItem;  
      }
      else {
         value = m_selectExpression.execute(xctxt, sourceNode, this);
      }

      if (transformer.getDebug())
        transformer.getTraceManager().fireSelectedEvent(sourceNode, this,
                                                        "select", m_selectExpression, value);

      SerializationHandler handler = transformer.getSerializationHandler();

      if (value != null) {
        int type = value.getType();
        String str;

        switch (type)
        {
        case XObject.CLASS_BOOLEAN :
        case XObject.CLASS_NUMBER :
        case XObject.CLASS_STRING :
          str = value.str();

          handler.characters(str.toCharArray(), 0, str.length());
          break;            
        case XObject.CLASS_NODESET :          
          copyOfActionOnNodeSet((XNodeSet)value, transformer, handler, xctxt);          
          break;
        case XObject.CLASS_RTREEFRAG :
          SerializerUtils.outputResultTreeFragment(
                                                handler, value, transformer.getXPathContext());
          break;
        case XObject.CLASS_RESULT_SEQUENCE :
          // added for XSLT 3.0          
          ResultSequence resultSequence = (ResultSequence)value;
          
          copyOfActionOnResultSequence(resultSequence, transformer, handler, xctxt);          
          break;
        default :          
          str = value.str();
          handler.characters(str.toCharArray(), 0, str.length());
          
          break;
        }
        
        if (value instanceof XSNumericType) {
           str = ((XSNumericType)value).stringValue();
           handler.characters(str.toCharArray(), 0, str.length());
        }
      }

    }
    catch(org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
    finally
    {
      if (transformer.getDebug())
        transformer.getTraceManager().fireTraceEndEvent(this);
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
                        this.getNodeName() });  //"Can not add " +((ElemTemplateElement)newChild).m_elemName +

    //" to " + this.m_elemName);
    return null;
  }
  
  /**
   * Call the children visitors.
   * @param visitor The visitor whose appropriate method will be called.
   */
  protected void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
  {
  	if(callAttrs)
  		m_selectExpression.getExpression().callVisitors(m_selectExpression, visitor);
    super.callChildVisitors(visitor, callAttrs);
  }
  
  /**
   *  Method to perform xsl:copy-of instruction's action, on an XNodeSet object.
   */
  private void copyOfActionOnNodeSet(XNodeSet nodeSet, TransformerImpl transformer, 
                                                               SerializationHandler serializationHandler, XPathContext xctxt) 
                                                                                          throws TransformerException, SAXException {
      DTMIterator dtmIter = nodeSet.iter();

      DTMTreeWalker tw = new TreeWalker2Result(transformer, serializationHandler);
      int pos;                 

      while ((pos = dtmIter.nextNode()) != DTM.NULL) {
          DTM dtm = xctxt.getDTMManager().getDTM(pos);
          short t = dtm.getNodeType(pos);

          if (t == DTM.DOCUMENT_NODE) {
             for (int child = dtm.getFirstChild(pos); child != DTM.NULL; 
                      child = dtm.getNextSibling(child)) {
                tw.traverse(child);
             }
          }
          else if (t == DTM.ATTRIBUTE_NODE) {
             SerializerUtils.addAttribute(serializationHandler, pos);
          }
          else {
             tw.traverse(pos);
          }
      } 
  }
  
  /**
   * Method to perform xsl:copy-of instruction's action, on an ResultSequence object.
   */
  private void copyOfActionOnResultSequence(ResultSequence resultSequence, TransformerImpl transformer, 
                                                                      SerializationHandler serializationHandler, 
                                                                                       XPathContext xctxt) throws TransformerException, SAXException {
      char[] spaceCharArr = new char[1];      
      spaceCharArr[0] = SPACE_CHAR;
      
      for (int idx = 0; idx < resultSequence.size(); idx++) {             
         XObject sequenceItem = resultSequence.item(idx);
         
         if (sequenceItem instanceof XString) {
             String str = sequenceItem.str();
             serializationHandler.characters(str.toCharArray(), 0, str.length());
             if (idx < (resultSequence.size() - 1)) {                     
                serializationHandler.characters(spaceCharArr, 0, 1);
             }
         }
         else if (sequenceItem instanceof XSString) {
             String str = ((XSString)sequenceItem).stringValue();
             serializationHandler.characters(str.toCharArray(), 0, str.length());
             if (idx < (resultSequence.size() - 1)) {                     
                serializationHandler.characters(spaceCharArr, 0, 1);
             }
         }
         else if (sequenceItem.getType() == XObject.CLASS_NUMBER) {
             String str = ((XNumber)sequenceItem).str();
             serializationHandler.characters(str.toCharArray(), 0, str.length());
             if (idx < (resultSequence.size() - 1)) {                     
                serializationHandler.characters(spaceCharArr, 0, 1);
             }
         }
         else if (sequenceItem instanceof XSNumericType) {
             String str = ((XSNumericType)sequenceItem).stringValue();
             serializationHandler.characters(str.toCharArray(), 0, str.length());
             if (idx < (resultSequence.size() - 1)) {                     
                serializationHandler.characters(spaceCharArr, 0, 1);
             }
         }
         else if (sequenceItem instanceof XSUntyped) {
             String str = ((XSUntyped)sequenceItem).stringValue();
             serializationHandler.characters(str.toCharArray(), 0, str.length());
             if (idx < (resultSequence.size() - 1)) {                     
                serializationHandler.characters(spaceCharArr, 0, 1);
             }
         }
         else if (sequenceItem instanceof XSUntypedAtomic) {
             String str = ((XSUntypedAtomic)sequenceItem).stringValue();
             serializationHandler.characters(str.toCharArray(), 0, str.length());
             if (idx < (resultSequence.size() - 1)) {                     
                serializationHandler.characters(spaceCharArr, 0, 1);
             } 
         }
         else if (sequenceItem.getType() == XObject.CLASS_NODESET) {                 
             copyOfActionOnNodeSet((XNodeSet)sequenceItem, transformer, serializationHandler, xctxt);
         }
      } 
  }

}
