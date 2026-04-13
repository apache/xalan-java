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
package org.apache.xalan.processor;

import org.apache.xalan.res.XSLTErrorResources;

/**
 * This class processes parse events for an xsl:import element.
 * 
 * @see <a href="https://www.w3.org/TR/xslt-30/#element-import">import in XSLT 3.0 Specification</a>
 * 
 * Description: A stylesheet module may import another stylesheet module using an xsl:import declaration. 
 *              Importing a stylesheet module is the same as including it except that template rules and 
 *              other declarations in the importing module take precedence over template rules and 
 *              declarations in the imported module
 * 
 * @xsl.usage internal
 */
public class ProcessorImport extends ProcessorInclude
{
	static final long serialVersionUID = -8247537698214245237L;

	/**
	 * Get the stylesheet type associated with an imported stylesheet
	 *
	 * @return the type of the stylesheet
	 */
	protected int getStylesheetType()
	{
		return StylesheetHandler.STYPE_IMPORT;
	}

	/**
	 * Get the error number associated with this type of stylesheet importing itself
	 *
	 * @return the appropriate error number
	 */
	protected String getStylesheetInclErr()
	{
		return XSLTErrorResources.ER_IMPORTING_ITSELF;
	}

}
