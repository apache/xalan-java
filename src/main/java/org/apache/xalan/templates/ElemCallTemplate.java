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

import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xml.utils.QName;
import org.apache.xpath.VariableStack;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Implementation of XSLT 3.0 instruction xsl:call-template.
 * 
 * @xsl.usage advanced
 */
public class ElemCallTemplate extends ElemForEach
{
	static final long serialVersionUID = 5009634612916030591L;

	/**
	 * An xsl:call-template element invokes a template by name;
	 * it has a required name attribute that identifies the template to be invoked.
	 * 
	 */
	public QName m_templateName = null;

	/**
	 * Set the "name" attribute.
	 * An xsl:call-template element invokes a template by name;
	 * it has a required name attribute that identifies the template to be invoked.
	 *
	 * @param name Name attribute to set
	 */
	public void setName(QName name)
	{
		m_templateName = name;
	}

	/**
	 * Get the "name" attribute.
	 * An xsl:call-template element invokes a template by name;
	 * it has a required name attribute that identifies the template to be invoked.
	 *
	 * @return Name attribute of this element
	 */
	public QName getName()
	{
		return m_templateName;
	}

	/**
	 * The template which is named by QName.
	 * 
	 */
	private ElemTemplate m_template = null;

	/**
	 * Class field, that represents the value of "xpath-default-namespace" 
	 * attribute.
	 */
	private String m_xpath_default_namespace = null;

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
	 * Class field, that represents the value of "expand-text" 
	 * attribute.
	 */
	private boolean m_expand_text;

	/**
	 * Variable to indicate whether, an attribute 'expand-text'
	 * is declared on xsl:apply-templates instruction.
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
	 * An XPath expression for 'use-when' attribute. 
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
	 * @return			XPath expression for attribute "use-when"
	 */
	public XPath getUseWhen()
	{
		return m_useWhen;
	}

	/**
	 * Used to implement XSLT tunnel parameters.
	 */
	protected final int MAX_PARAM_LIMIT = 100; 

	/**
	 * Get an int constant identifying the type of element.
	 * @see org.apache.xalan.templates.Constants
	 *
	 * @return           The token id for this element 
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_CALLTEMPLATE;
	}

	/**
	 * Return the node name.
	 *
	 * @return The name of this element
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_CALLTEMPLATE_STRING;
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

		// Call compose on each param no matter if this is apply-templates 
		// or call templates.
		int length = getParamElemCount();
		for (int i = 0; i < length; i++) 
		{
			ElemWithParam ewp = getParamElem(i);
			ewp.compose(sroot);
		}

		if ((m_templateName != null) && (m_template == null)) {
			m_template = this.getStylesheetRoot().getTemplateComposed(m_templateName);

			if (m_template != null) {
				length = getParamElemCount();
				for (int i = 0; i < length; i++) 
				{
					ElemWithParam ewp = getParamElem(i);
					ewp.m_index = -1;
					// Find the position of the param in the template being called, 
					// and set the index of the param slot.
					int etePos = 0;
					for (ElemTemplateElement ete = m_template.getFirstChildElem(); 
							ete != null; ete = ete.getNextSiblingElem()) 
					{
						if (ete.getXSLToken() == Constants.ELEMNAME_PARAMVARIABLE)
						{
							ElemParam ep = (ElemParam)ete;
							if (ep.getName().equals(ewp.getName()))
							{
								ewp.m_index = etePos;
							}
						}
						else
							break;
						etePos++;
					}
				}
			}
		}
	}

	/**
	 * This after the template's children have been composed.
	 */
	public void endCompose(StylesheetRoot sroot) throws TransformerException
	{
		int length = getParamElemCount();
		for (int i = 0; i < length; i++) 
		{
			ElemWithParam ewp = getParamElem(i);
			ewp.endCompose(sroot);
		}    

		super.endCompose(sroot);
	}

	/**
	 * Invoke an XSL named template.
	 * 
	 * @see <a href="http://www.w3.org/TR/xslt#named-templates">named-templates in XSLT Specification</a>
	 *
	 * @param transformer non-null reference to the the current transform-time state.
	 *
	 * @throws TransformerException
	 */
	public void execute(TransformerImpl transformer) throws TransformerException {

		if (transformer.getDebug())
			transformer.getTraceManager().emitTraceEvent(this);

		XPathContext xctxt = transformer.getXPathContext();

		final int sourceNode = xctxt.getCurrentNode();

		SourceLocator srcLocator = xctxt.getSAXLocator();

		if (m_useWhen != null) {
			boolean result1 = isXPathExpressionStatic(m_useWhen.getExpression());
			if (result1) {
				XObject useWhenResult = m_useWhen.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
				if (!useWhenResult.bool()) {
					return;
				}
			}
			else {
				throw new TransformerException("XPST0008 : XSL variables other than XSLT static variables/parameters, cannot be "
																															+ "used within XPath "
																															+ "static expression.", srcLocator);
			}
		}

		if (m_template == null) {
			m_template = getXslTemplateUsingXslPackage(transformer);
		}

		if (m_template != null) {      
			SourceLocator savedLocator = xctxt.getSAXLocator();

			VariableStack varStack = xctxt.getVarStack();
			int thisframe = varStack.getStackFrame();
			
			String nsUri = m_templateName.getNamespace();
			String localName = m_templateName.getLocalName();
			if ((Constants.S_XSLNAMESPACEURL).equals(nsUri) && !"initial-template".equals(localName)) {
				throw new TransformerException("XTSE0080 : An XSL named template uses reserved namespace " + Constants.S_XSLNAMESPACEURL 
						                                                                                   + " within its name.", srcLocator);
			}

			try {
				int nextFrame = varStack.link(m_template.m_frameSize);

				// We have to clear the section of the stack frame that has params 
				// so that the default param evaluation will work correctly.
				if (m_template.m_inArgsSize > 0)
				{
					varStack.clearLocalSlots(0, m_template.m_inArgsSize);

					if (m_withParamElems != null)
					{          
						varStack.setStackFrame(thisframe);
						int withParamSize = m_withParamElems.length;

						int maxParamStackFrameIndex = -1;

						for (int i = 0; i < withParamSize; i++) 
						{
							ElemWithParam ewp = m_withParamElems[i];
							if (ewp.m_index >= 0)
							{
								XObject xObj = ewp.getValue(transformer, sourceNode);
								
								String tunnelStrVal = ewp.getTunnel();
								xObj.setTunnel(tunnelStrVal);
								QName qName = ewp.getName();
								xObj.setQName(qName);
								
								varStack.setLocalVariable(ewp.m_index, xObj, nextFrame);
								maxParamStackFrameIndex = ewp.m_index; 
							}
						}

						propagateTunnelParameters(varStack, thisframe, nextFrame, maxParamStackFrameIndex);

						varStack.setStackFrame(nextFrame);
					}
					else {
						varStack.setStackFrame(thisframe);            
						int maxParamStackFrameIndex = -1;

						propagateTunnelParameters(varStack, thisframe, nextFrame, maxParamStackFrameIndex);             

						varStack.setStackFrame(nextFrame);
					}
				}
				
				xctxt.setSAXLocator(m_template);

				transformer.pushElemTemplateElement(m_template);
				m_template.execute(transformer);
			}
			finally
			{
				transformer.popElemTemplateElement();
				xctxt.setSAXLocator(savedLocator);

				varStack.unlink(thisframe);
			}
		}	
		else {
			throw new TransformerException("XPST0008 : An XSL named template declaration " + m_templateName.toString() 
			                                                                               + " not found.", srcLocator);
		}

		if (transformer.getDebug())
			transformer.getTraceManager().emitTraceEndEvent(this); 

	}

	/**
	 * Vector of xsl:param elements associated with this element.
	 */
	protected ElemWithParam[] m_withParamElems = null;

	/**
	 * Get the count xsl:param elements associated with this element.
	 * @return The number of xsl:param elements.
	 */
	public int getParamElemCount()
	{
		return (m_withParamElems == null) ? 0 : m_withParamElems.length;
	}

	/**
	 * Get a xsl:param element associated with this element.
	 *
	 * @param i Index of element to find
	 *
	 * @return xsl:param element at given index
	 */
	public ElemWithParam getParamElem(int i)
	{
		return m_withParamElems[i];
	}

	/**
	 * Set a xsl:param element associated with this element.
	 *
	 * @param ParamElem xsl:param element to set. 
	 */
	public void setParamElem(ElemWithParam ParamElem)
	{
		if (m_withParamElems == null)
		{
			m_withParamElems = new ElemWithParam[1];
			m_withParamElems[0] = ParamElem;
		}
		else
		{
			// Expensive 1 at a time growth, but this is done at build time, so 
			// I think it's OK.
			int length = m_withParamElems.length;
			ElemWithParam[] ewp = new ElemWithParam[length + 1];
			System.arraycopy(m_withParamElems, 0, ewp, 0, length);
			m_withParamElems = ewp;
			ewp[length] = ParamElem;
		}
	}

	/**
	 * Add a child to the child list.
	 * 
	 * <!ELEMENT xsl:apply-templates (xsl:sort|xsl:with-param)*>
	 * <!ATTLIST xsl:apply-templates
	 *   select %expr; "node()"
	 *   mode %qname; #IMPLIED
	 * >
	 *
	 * @param newChild Child to add to this node's children list
	 *
	 * @return The child that was just added the children list 
	 *
	 * @throws DOMException
	 */
	public ElemTemplateElement appendChild(ElemTemplateElement newChild)
	{

		int type = ((ElemTemplateElement) newChild).getXSLToken();

		if (Constants.ELEMNAME_WITHPARAM == type)
		{
			setParamElem((ElemWithParam) newChild);
		}

		// You still have to append, because this element can
		// contain a for-each, and other elements.
		return super.appendChild(newChild);
	}

	/**
	 * Call the children visitors.
	 * 
	 * @param visitor The visitor whose appropriate method will be called.
	 */
	public void callChildVisitors(XSLTVisitor visitor, boolean callAttrs)
	{
		super.callChildVisitors(visitor, callAttrs);
	}

	/**
	 * Method definition, to copy tunnel parameters stored within XPath 
	 * context's variable stack from the previous stack frame to next 
	 * stack frame.
	 */
	private void propagateTunnelParameters(VariableStack varStack, int thisframe, int nextFrame, 
			                                                              int maxParamStackFrameIndex) throws TransformerException {

		for (int i = 0; i < MAX_PARAM_LIMIT; i++) {
			XObject xObj1 = varStack.getLocalVariable(i, thisframe);
			if (xObj1 != null) {
				String tunnelValStr = xObj1.getTunnel();            	
				if ((xObj1.getTunnel() != null) && XslTransformEvaluationHelper.isTunnelAttributeYes(tunnelValStr)) {
					varStack.setLocalVariable(++maxParamStackFrameIndex, xObj1, nextFrame);
				}
			}
			else {
				break; 
			}
		}

		ElemTemplateElement parentElem = getParentElem();

		while (!(parentElem instanceof ElemTemplate)) {
			parentElem = parentElem.getParentElem(); 
		}

		List<XObject> tunnelParamObjList = parentElem.getTunnelParamObjList();

		int size1 = tunnelParamObjList.size();
		for (int idx = 0; idx < size1; idx++) {
			XObject var = tunnelParamObjList.get(idx);
			varStack.setLocalVariable(++maxParamStackFrameIndex, var, nextFrame);
		}
	}
	
	/**
	 * Method definition, to get xsl:template declaration object, using a possibly 
	 * available xsl:use-package instruction within the containing stylesheet object.
	 * 
	 * @param transformer                             An XSL run-time TransformerImpl object 
	 * @return                                        An xsl:template object              
	 * @throws TransformerException  
	 * @throws TransformerFactoryConfigurationError
	 */
	private ElemTemplate getXslTemplateUsingXslPackage(TransformerImpl transformer) throws TransformerException, 
			                                                                                         TransformerFactoryConfigurationError {
		
		ElemTemplate result = null;
		
		XPathContext xctxt = transformer.getXPathContext();
		final int sourceNode = xctxt.getCurrentNode();
		SourceLocator srcLocator = xctxt.getSAXLocator();  
		
		StylesheetRoot sroot = transformer.getStylesheet();

		ElemTemplateElement elemTemplateElement1 = sroot.getFirstChildElem();
		
		Stylesheet stylesheet2 = null;
		
		while (elemTemplateElement1 != null) {
			if (elemTemplateElement1 instanceof ElemUsePackage) {
				ElemUsePackage elemUsePackage = (ElemUsePackage)elemTemplateElement1;
				XPath useWhenExpr = elemUsePackage.getUseWhen();
				if (useWhenExpr != null) {
					XObject xObj = useWhenExpr.execute(xctxt, sourceNode, xctxt.getNamespaceContext());
					if (!xObj.bool()) {
						elemTemplateElement1 = elemTemplateElement1.getNextSiblingElem();

						continue;
					}						
				}

				System.setProperty(Constants.XSL_TRANSFORM_FACTORY_KEY, Constants.XSL_TRANSFORM_FACTORY_VALUE);

				try {
					TransformerFactory tfactory = TransformerFactory.newInstance();
					tfactory.setErrorListener(new DefaultErrorHandler(true));

					AVT packageAvt = elemUsePackage.getName();
					String packageName = packageAvt.evaluate(xctxt, sourceNode, xctxt.getNamespaceContext());

					DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
					dfactory.setNamespaceAware(true);
					dfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
					DocumentBuilder docBuilder = dfactory.newDocumentBuilder();    							    							

					URL resolvedUrl = null;
					URI uri = new URI(packageName);
					if (uri.isAbsolute()) {
						resolvedUrl = new URL(packageName); 
					}
					else {
						String stylesheetSystemId = srcLocator.getSystemId();    				            	
						if (stylesheetSystemId != null) {
							URI resolvedUriArg = (new URI(stylesheetSystemId)).resolve(packageName);
							resolvedUrl = resolvedUriArg.toURL();
						}
						else {
							resolvedUrl = new URL(packageName);
						}
					}

					String xslResolverUrlStr = resolvedUrl.toString();

					InputSource inpSrc = new InputSource(xslResolverUrlStr);

					Document xslDocument = docBuilder.parse(inpSrc);

					Templates templates = tfactory.newTemplates(new DOMSource(xslDocument, xslResolverUrlStr));

					/**
					 * This shall provide to us XSL expanded component definitions.
					 * We need to verify, that which of these components to use,
					 * using xsl:accept instruction, information from xsl:use-package.
					 */
					stylesheet2 = (Stylesheet)templates;

					ElemTemplate template = null;

					ElemTemplateElement elem1 = elemUsePackage.getFirstChildElem();
					boolean isUsePackageAllows = false;
					while ((elem1 != null) && (elem1 instanceof ElemAccept)) {
						ElemAccept elemAccept = (ElemAccept)elem1;
						String componentType = elemAccept.getComponent();    								
						if ("template".equals(componentType) || "*".equals(componentType)) {
							Vector componentNames = elemAccept.getNames();
							String compVisibilityValue = elemAccept.getVisibility();
							if ("public".equals(compVisibilityValue)) {    								    
								int xslTemplateCount = stylesheet2.getTemplateCount();								  
								for (int idx = 0; idx < xslTemplateCount; idx++) {
									template = stylesheet2.getTemplate(idx);
									Enumeration enum1 = componentNames.elements();
									while (enum1.hasMoreElements()) {
										QName qname = (QName)(enum1.nextElement());
										if (qname.equals(m_templateName)) {
											isUsePackageAllows = true;

											break;
										}
									}

									if (isUsePackageAllows) {
										break; 
									}
								}

								if ((template != null) && isUsePackageAllows) {
									break;	
								}
							}
						}

						if ((template != null) && isUsePackageAllows) {
							break;	
						}

						elem1 = elem1.getNextSiblingElem();
					}

					if ((template != null) && isUsePackageAllows) {
						result = template;

						break;
					}					  
				}
				catch (Exception ex) {

				}
			}

			elemTemplateElement1 = elemTemplateElement1.getNextSiblingElem();
		}

		if (result != null) {
			// Verify xsl:template object found via xsl:use-package instruction,
			// with xsl:expose instruction.
			
			ElemTemplateElement elemTemplateElem = stylesheet2.getFirstChildElem();
			boolean isXslExposeAllows = false;
			while (elemTemplateElem != null) {
				if (elemTemplateElem instanceof ElemExpose) {
					ElemExpose elemExpose = (ElemExpose)elemTemplateElem;					
					String componentType = elemExpose.getComponent();
					Vector nameVector = elemExpose.getNames();
					String visibility = elemExpose.getVisibility();
					if ("public".equals(visibility) && ("template".equals(componentType) || "*".equals(componentType))) {
						Enumeration enum1 = nameVector.elements();
						while (enum1.hasMoreElements()) {
							QName qname = (QName)(enum1.nextElement());
							if (qname.equals(result.getName())) {
								isXslExposeAllows = true;

								break;
							}
						}

						if (isXslExposeAllows) {
							break; 
						}
					}

					if (isXslExposeAllows) {
						break;	
					}
				}

				elemTemplateElem = elemTemplateElem.getNextSiblingElem();
			}

			if (!isXslExposeAllows) {
				result = null; 
			}
		}
		
		if ((result != null) && (stylesheet2 != null)) {
		   result.setStylesheet(stylesheet2);
		}
		
		return result;
	}
}
