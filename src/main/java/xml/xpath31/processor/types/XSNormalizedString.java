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
package xml.xpath31.processor.types;

import org.apache.xpath.objects.ResultSequence;

/**
 * A representation of the xs:normalizedString datatype.
 */
public class XSNormalizedString extends XSString {

	private static final long serialVersionUID = 1297073093964057453L;
	
	private static final String XS_NORMALIZEDSTRING = "xs:normalizedString";
	
	// unicode codepoint decimal values of certain characters mentioned below,
	// relevant to the implementation of this class.
	
	private static int UC_VAL_CR = 13;
	
	private static int UC_VAL_LINE_FEED = 10;
	
	private static int UC_VAL_TAB = 9;
	
	/**
	 * Initialises this xs:normalizedString object, using the 
	 * supplied string.
	 * 
	 * @param normStr   the string value to initialise to
	 */
	public XSNormalizedString(String normStr) {
		super(normStr);
	}

	/**
	 * Initialize this xs:normalizedString value to null.
	 */
	public XSNormalizedString() {
		this(null);
	}

	/**
	 * Retrieves this datatype's full pathname.
	 * 
	 * @return "xs:normalizedString" which is the datatype's full pathname
	 */
	public String stringType() {
		return XS_NORMALIZEDSTRING;
	}

	/**
	 * Retrieves the datatype's name.
	 * 
	 * @return "normalizedString" which is the datatype's name
	 */
	public String typeName() {
		return "normalizedString";
	}

	/**
	 * Creates a new ResultSequence object, consisting of the string value
	 * within the supplied ResultSequence.
	 * 
	 * @param arg    the ResultSequence from which to get the string
	 * 
	 * @return       new ResultSequence consisting of the supplied string
	 * 
	 * @throws       javax.xml.transform.TransformerException
	 */
	public ResultSequence constructor(ResultSequence arg) throws javax.xml.transform.TransformerException {
		ResultSequence resultSeq = new ResultSequence();

		if (arg.size() == 0) {
		   return resultSeq;
		}

		XSAnyType xsAnyType = (XSAnyType)arg.item(0);

		String srcString = xsAnyType.stringValue();
		if (!isSatisfiesConstraints(srcString)) {
		   throw new javax.xml.transform.TransformerException("FORG0006 : The supplied string value cannot be used to construct "
		   		                                                          + "xs:normalizedString value, since the input string contains prohibited "
		   		                                                          + "characters for a xs:normalizedString value.");	
		}
		else {
		   resultSeq.add(new XSNormalizedString(srcString));	
		}

		return resultSeq;
	}
	
	/*
	 * Determine whether, the supplied string value satisfies constraints for 
	 * the datatype, xs:normalizedString. 
	 */
	protected boolean isSatisfiesConstraints(String srcString) {
	   
		boolean isXsNormalizedStr = true;
		
		// The xs:normalizedString value cannot contain, 'carriage return', 
		// 'line feed' and 'tab' characters.
		if ((srcString.indexOf(UC_VAL_CR) != -1) || (srcString.indexOf(UC_VAL_LINE_FEED) != -1) || 
				                                    (srcString.indexOf(UC_VAL_TAB) != -1)) {
		   // invalid input string
		   isXsNormalizedStr = false;
		}
		
		return isXsNormalizedStr;
		  
	} // isSatisfiesConstraints
	
    public int getType() {
       return CLASS_XS_NORMALIZED_STRING;
    }

}
