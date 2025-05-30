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

import javax.xml.transform.TransformerException;

import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;

/**
 * Implementation of XSLT 3.0 xsl:output-character element within
 * xsl:character-map element.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ElemOutputCharacter extends ElemTemplateElement 
                                                          implements ExpressionOwner {

	static final long serialVersionUID = 5924859487723083394L;
	
	/**
	 * Class field representing "character" attribute.
	 */
	protected Integer m_charCodePoint;
	
	/**
	 * Set the "character" attribute.
	 * 
	 * @param codePoint
	 */
	public void setCharacter(Integer codePoint)
	{
		m_charCodePoint = codePoint;
	}

	/**
	 * Get the "character" attribute.
	 * 
	 * @return
	 */
	public Integer getCharacter()
	{
		return m_charCodePoint;
	}
	
	/**
	 * Class field representing "string" attribute.
	 */
	protected String m_string = null;
	
	/**
	 * Set the "string" attribute.
	 * 
	 * @param c
	 */
	public void setString(String str)
	{
		m_string = str;
	}

	/**
	 * Get the "string" attribute.
	 * 
	 * @return
	 */
	public String getString()
	{
		return m_string;
	}
	
	/**
	 * Get an int constant identifying the type of element.
	 * @see org.apache.xalan.templates.Constants
	 *
	 * @return The token ID for the element
	 */
	public int getXSLToken()
	{
		return Constants.ELEMNAME_OUTPUT_CHARACTER;
	}

	/**
	 * Return the node name.
	 *
	 * @return The element's name
	 */
	public String getNodeName()
	{
		return Constants.ELEMNAME_OUTPUT_CHARACTER_STRING;
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
	}

	/**
	 * This method is called after this element's children have been composed.
	 */
	public void endCompose(StylesheetRoot sroot) throws TransformerException
	{
		super.endCompose(sroot);		  
	}
	
	/**
	 * Execute an XSL xsl:output-character transformation.
	 */
	public void execute(TransformerImpl transformer) throws TransformerException {	    
		// NO OP
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
		// NO OP
		return null;
	}

	@Override
	public void setExpression(Expression exp) {		
		// NO OP		
	}

}
