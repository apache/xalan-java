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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.functions.FuncDoc;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;

/**
 * Implementation of the XSLT 3.0 xsl:source-document instruction.
 * 
 * Ref : https://www.w3.org/TR/xslt-30/#source-document-instruction
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 *  
 * @xsl.usage advanced
 */
public class ElemSourceDocument extends ElemTemplateElement 
                                                implements ExpressionOwner
{
  private static final long serialVersionUID = 3034441680350015299L;

  /**
   * The "href" avt value.
   */
  private AVT m_href = null;
  
  /**
   * The "streamable" boolean value.
   */
  private boolean m_streamable = false;
  
  /**
   * The class constructor.
   */
  public ElemSourceDocument() {}

  /**
   * Get the value of "href" attribute.
   *
   * @return 	avt value for the "href" attribute.
   */
  public AVT getHref()
  {
      return m_href;
  }

  /**
   * Set the value of "href" attribute.
   *
   * @param href	avt value for the "href" attribute.
   */
  public void setHref(AVT href)
  {
      this.m_href = href;  
  }
  
  /**
   * Set the value of "streamable" attribute.
   *
   * @param streamable The boolean value for the "streamable" attribute.
   */
  public void setStreamable(boolean streamable)
  {
      m_streamable = streamable;   
  }
  
  /**
   * Get the value of "streamable" attribute.
   *
   * @return The boolean value for the "streamable" attribute.
   */
  public boolean getStreamable()
  {
      return m_streamable;
  }

  /**
   * This function is called after everything else has been
   * recomposed, and allows the template to set remaining
   * values that may be based on some other property that
   * depends on recomposition.
   *
   * @param sroot
   *
   * @throws TransformerException
   */
  public void compose(StylesheetRoot sroot) throws TransformerException
  {
	  super.compose(sroot);
  }
  
  /**
   * This function is called after the template's children have been composed.
   */
  public void endCompose(StylesheetRoot sroot) throws TransformerException
  {     
	  super.endCompose(sroot);
  }

  /**
   * Get an int constant identifying the type of element.
   * @see org.apache.xalan.templates.Constants
   *
   * @return The token ID for this element
   */
  public int getXSLToken()
  {
      return Constants.ELEMNAME_SOURCEDOCUMENT;
  }

  /**
   * Return the node name.
   *
   * @return The element's name
   */
  public String getNodeName()
  {
      return Constants.ELEMNAME_SOURCEDOCUMENT_STRING;
  }

  /**
   * Execute the xsl:source-document transformation.
   *
   * @param transformer non-null reference to the the current transform-time state.
   *
   * @throws TransformerException
   */
  public void execute(TransformerImpl transformer) throws TransformerException
  {
	    
	    XPathContext xctxt = transformer.getXPathContext();
	    
	    SourceLocator srcLocator = xctxt.getSAXLocator(); 
	    
	    if (m_streamable) {
	       Source source = transformer.getSource();	       	       
	       if (!(source instanceof SAXSource)) {
	    	   String srcClassName = (source.getClass()).getName();
	    	   throw new TransformerException("XTTE0505 : xsl:source-document instruction has attribute "
	    	   		                                            + "\"streamable\" with value 'yes', but XSL transformation "
	    	   		                                            + "has not been invoked with a SAXSource document input. "
	    	   		                                            + "This XSL transformation has been invoked with " + 
	    	   		                                               srcClassName + " document input.", srcLocator);  
	       }
	    }
	  
	    try {
	    	transformer.pushCurrentTemplateRuleIsNull(true);

	    	if (transformer.getDebug()) {
	    		transformer.getTraceManager().emitTraceEvent(this);
	    	}

	    	String hrefStrVal = m_href.evaluate(xctxt, xctxt.getContextNode(), this);		// Mandatory
	    	
	    	URL resolvedHrefUrl = null;

			try {
				URI url = new URI(hrefStrVal);
				String stylesheetSystemId = srcLocator.getSystemId();      // base uri of stylesheet, if available

				if (!url.isAbsolute() && (stylesheetSystemId != null)) {
					URI resolvedUriArg = (new URI(stylesheetSystemId)).resolve(hrefStrVal);
					resolvedHrefUrl = resolvedUriArg.toURL(); 
				}

				if (resolvedHrefUrl == null) {
					resolvedHrefUrl = new URL(hrefStrVal);   
				}
			}
			catch (URISyntaxException ex) {
				throw new javax.xml.transform.TransformerException("FODC0005 : An xsl:source-document instruction's href uri '" + hrefStrVal + "' "
																					+ "is not a valid absolute uri, "
																					+ "or cannot be resolved to an absolute uri.", srcLocator);  
			}
			catch (MalformedURLException ex) {
				throw new javax.xml.transform.TransformerException("FODC0005 : An xsl:source-document instruction's href uri '" + hrefStrVal + "' "
																					+ "is not a valid absolute uri, or cannot be resolved to "
																					+ "an absolute uri.", srcLocator);
			}

	    	FuncDoc funcDoc = new FuncDoc();        
	    	XObject xdmDocNode = funcDoc.getDocumentNode(xctxt, hrefStrVal);
	    	
	    	if (xdmDocNode instanceof XMLNodeCursorImpl) {
	    		XMLNodeCursorImpl xNodeSet = (XMLNodeCursorImpl)xdmDocNode;
	    		DTMCursorIterator dtmIterator = xNodeSet.iterRaw();
	    		int hrefDocNodeHandle = dtmIterator.nextNode();
	    		xctxt.pushCurrentNode(hrefDocNodeHandle);
	    		
	    		for (ElemTemplateElement t = this.m_firstChild; t != null;
	    																t = t.m_nextSibling) {
	    			xctxt.setSAXLocator(t);
	    			transformer.setCurrentElement(t);                   
	    			t.execute(transformer);
	    		}
	    	}
	    	else {
	    		// If the result of an attempt, to fetch a document node from 
	    		// value of xsl:source-document element's 'href' attribute is 
	    		// not an XDM node, the result of xsl:source-document's 
	    		// processing is empty.        	
	    		return;	
	    	}
        }
        finally {
            if (transformer.getDebug()) {
    	       transformer.getTraceManager().emitTraceEndEvent(this);
            }
            
            xctxt.popCurrentNode();
            transformer.popCurrentTemplateRuleIsNull();
        }
  }

  /**
   * Add an XSL stylesheet child information.
   *
   * @param newChild Child to add to child list
   *
   * @return Child just added to child list
   */
  public ElemTemplateElement appendChild(ElemTemplateElement newChild)
  {
      return super.appendChild(newChild);
  }
  
  /**
   * Call the children visitors.
   * 
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callChildVisitors(XSLTVisitor visitor, boolean callAttributes)
  {    
        super.callChildVisitors(visitor, callAttributes);
  }

  @Override
  public Expression getExpression() {
	  return null;
  }

  @Override
  public void setExpression(Expression exp) {
	  // NO OP
  }

}
