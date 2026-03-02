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

import javax.xml.transform.TransformerException;

import org.apache.xpath.objects.ResultSequence;

/**
 * An XML Schema data type representation, of the xs:QName datatype.
 */
public class XSQName extends XSCtrType {

	private static final long serialVersionUID = -8325816735017548359L;
	
	private static final String XS_QNAME = "xs:QName";
	
	private String prefix;
	
	private String localPart;
	
	private String namespaceUri;		
	
	private boolean _expanded;
	
	/**
	 * Default class constructor.
	 */
	public XSQName() { 
	   // no op
	}
	
	/**
	 * Class constructor.
	 */
	public XSQName(String prefix, String localPart, String namespaceUri) {
	   this.prefix = prefix;
	   this.localPart = localPart;
	   if (namespaceUri != null) {
	      this.namespaceUri = namespaceUri;
	      _expanded = true;
	   }
	}
	
	/**
	 * Class constructor.
	 */
	public XSQName(String prefix, String localPart) {	   
	   this.prefix = prefix;
	   this.localPart = localPart;
	   _expanded = false;
	}
	
	/**
	 * Class constructor.
	 */
	public XSQName(String localPart) {
	   this.localPart = localPart;
	}		
	
	@Override
	public ResultSequence constructor(ResultSequence arg) throws TransformerException {
        ResultSequence resultSeq = new ResultSequence();
        
        if (arg.size() == 0) {
           return resultSeq;     
        }
        
        XSAnyType xsAnyType = (XSAnyType)arg.item(0);
        
        if ((xsAnyType instanceof XSString) || (xsAnyType instanceof XSQName)) {
        	XSQName xsQName = parseQName(xsAnyType.stringValue());
        	if (xsQName != null) {
        	   resultSeq.add(xsQName);
        	}
        }
        else {
        	throw new TransformerException("FORG0006 : A xs:QName value can be constructed "
        			                            + "either using xs:string or an xs:QName value");
        }
           
        return resultSeq;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getLocalPart() {
		return localPart;
	}

	public void setLocalPart(String localPart) {
		this.localPart = localPart;
	}
	
	public String getNamespaceUri() {
		return namespaceUri;
	}

	public void setNamespaceUri(String namespaceUri) {
		this.namespaceUri = namespaceUri;
	}
	
	@Override
    public int hashCode() {
	   // We need to form somewhat like below, xs:QName object's
	   // sufficiently unique string value and get the corresponding 
	   // hashCode value thereafter.
	   String uniqueStrValue = "{" + prefix + ":" + 
                                     namespaceUri + "}" + 
                                     localPart;
	   
	   return uniqueStrValue.hashCode();
	}
	
	@Override
    public boolean equals(Object obj) {
		boolean isQNameEqual = false;
		
		if (obj instanceof XSQName) {
		   isQNameEqual = this.equals((XSQName)obj);
		}
		
		return isQNameEqual; 
	}
	
	/*
	 * Check whether, two xs:QName values are equal. 
	 */
	public boolean equals(XSQName xsQName) {
		boolean isQNameEqual = true;
		
		if (!localPart.equals(xsQName.getLocalPart())) {
			isQNameEqual = false;	
		}
		else if (((namespaceUri == null) && (xsQName.getNamespaceUri() != null)) ||				
			      !namespaceUri.equals(xsQName.getNamespaceUri())) {
			isQNameEqual = false;
		}
		else if ((namespaceUri != null) && (xsQName.getNamespaceUri() == null)) {
			isQNameEqual = false;	
		}
		else if ((namespaceUri != null) && (xsQName.getNamespaceUri() != null) && 
				 (!namespaceUri.equals(xsQName.getNamespaceUri()))) {
			isQNameEqual = false;
		}
				
		return isQNameEqual;
	}
	

	@Override
	public String typeName() {
		return "QName";
	}

	@Override
	public String stringType() {
		return XS_QNAME;
	}

	@Override
	public String stringValue() {
        String strVal = "";
		
		if (namespaceUri != null) {
			strVal = '{' + namespaceUri + '}' + localPart; 	
		}
		else {
			strVal = localPart; 
		}
		
		return strVal;
	}
	
	/**
	 * Construct an XSQName object, by parsing a provided string value
	 * as an argument to this method.
	 */
	public static XSQName parseQName(String str) {
	   XSQName qNameValue = null;
	   
	   int colonCharOccurenceCount = 0;
		
	   char[] strChrArr = str.toCharArray();		
	   for (int chrIndx = 0; chrIndx < strChrArr.length; chrIndx++) {
		  if (strChrArr[chrIndx] == ':') {
			  colonCharOccurenceCount += 1;  
		  }
	   }
		
	   if (colonCharOccurenceCount > 1) {
	      return null;
	   }
	   
	   String[] strTokens = str.split(":");
	   
	   if (strTokens.length == 1) {
		  qNameValue = new XSQName(strTokens[0]);
	   }
		
	   if (strTokens.length == 2) {
		  qNameValue = new XSQName(strTokens[0], strTokens[1]);
	   }
	   
	   return qNameValue;
	}
	
	/**
	 * Check whether this xs:QName object has a 
	 * non null expanded name. 
	 */
	public boolean isExpanded() {
	   return _expanded;
	}
	
	/**
	 * Get this xs:QName object's expanded name. 
	 */
	public String getExpandedName() {
	   String expandedName = "";
	   
	   if (namespaceUri != null) {
		  expandedName += namespaceUri + ":";
	   }
	   
	   expandedName = expandedName + localPart;
	   
	   return expandedName;
	}
	
	public int getType() {
        return CLASS_XS_QNAME;
    }

}
