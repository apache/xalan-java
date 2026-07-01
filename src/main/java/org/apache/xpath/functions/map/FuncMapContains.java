/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xpath.functions.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.SourceLocator;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMCursorIterator;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.LocPathIterator;
import org.apache.xpath.functions.Function2Args;
import org.apache.xpath.objects.ResultSequence;
import org.apache.xpath.objects.XMLNodeCursorImpl;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XPathMap;
import org.apache.xpath.objects.XString;

import xml.xpath31.processor.types.XSAnyURI;
import xml.xpath31.processor.types.XSBoolean;
import xml.xpath31.processor.types.XSDayTimeDuration;
import xml.xpath31.processor.types.XSDouble;
import xml.xpath31.processor.types.XSDuration;
import xml.xpath31.processor.types.XSFloat;
import xml.xpath31.processor.types.XSString;
import xml.xpath31.processor.types.XSUntypedAtomic;
import xml.xpath31.processor.types.XSYearMonthDuration;

/**
 * Implementation of an XPath 3.1 function, map:contains.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class FuncMapContains extends Function2Args {

	private static final long serialVersionUID = -749857579667076961L;
	
	/**
	 * Class constructor.
	 */
	public FuncMapContains() {
		m_defined_arity = new Short[] { 2 };	
	}

	/**
	 * Evaluate the function. The function must return a valid object.
	 * 
	 * @param xctxt                        An XPath context object
	 * @return                             A valid XObject
	 *
	 * @throws javax.xml.transform.TransformerException
	 */
	public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
	{
		XObject result = null;
		
		SourceLocator srcLocator = xctxt.getSAXLocator();
		
		final int sourceNode = xctxt.getCurrentNode();
	    
	    XObject xObj0 = getFunctionArgEffectiveValue(m_arg0, xctxt); 
	           
	    if ((xObj0 instanceof ResultSequence) && (((ResultSequence)xObj0).size() == 0)) {
	    	throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'contains' cannot have its first "
	    																											+ "argument as an empty "
	    																											+ "sequence.", srcLocator);  
	    }
	    
	    XPathMap xpathMap = null;
	    
	    if (xObj0 instanceof XPathMap) {
	       xpathMap = (XPathMap)xObj0;
	    }
	    else {
	       throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'contains' first "
	       		                                                                                           + "argument is not an xdm map.", srcLocator);
	    }
	    
	    Map<XObject, XObject> nativeMap = xpathMap.getNativeMap();
	    
	    Map<XObject, XObject> normalizedMap1 = XslTransformEvaluationHelper.getNormalizedClonedMap(nativeMap);
	    
	    if (m_arg1 instanceof LocPathIterator) {
	       DTMCursorIterator iter1 = null;
	       
	       try {
	          iter1 = ((LocPathIterator)m_arg1).asIterator(xctxt, sourceNode);
	       }
	       catch (Exception ex) {
	    	  throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'contains' cannot have its second "
																													   + "argument as an empty "
																													   + "sequence.", srcLocator);  
	       }
	       
	       int nextNode = iter1.nextNode();
	       if (nextNode == DTM.NULL) {
	    	   throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'contains' cannot have its second "
																													   + "argument as an empty "
																													   + "sequence.", srcLocator); 
	       }
	    }
	    
        XObject arg1Obj = getFunctionArgEffectiveValue(m_arg1, xctxt);
        
        if ((arg1Obj instanceof ResultSequence) && (((ResultSequence)arg1Obj).size() == 0)) {
        	throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'contains' cannot have its second "
																													+ "argument as an empty "
																													+ "sequence.", srcLocator);
        }
        else if ((arg1Obj instanceof XMLNodeCursorImpl) && (((XMLNodeCursorImpl)arg1Obj).getLength() == 0)) {
        	throw new javax.xml.transform.TransformerException("XPTY0004 : An XPath 3.1 map function 'contains' cannot have its second "
																													+ "argument as an empty "
																													+ "sequence.", srcLocator);
        }
	    	    
	    if (arg1Obj instanceof XString) {
	    	String str1 = ((XString)arg1Obj).str();
	    	str1 = str1.replace(" : ", ":");
	    	arg1Obj = new XSString(str1);
		}
	    else if (arg1Obj instanceof XSString) {
	    	String str1 = ((XSString)arg1Obj).stringValue();
	    	str1 = str1.replace(" : ", ":");
	    	arg1Obj = new XSString(str1);
		}
	    else if (arg1Obj instanceof XSUntypedAtomic) {
	       arg1Obj = new XSString(((XSUntypedAtomic)arg1Obj).stringValue());
	    }
	    else if (arg1Obj instanceof XSAnyURI) {
	       String str1 = ((XSAnyURI)arg1Obj).stringValue();
		   str1 = str1.replace(" : ", ":");
		   arg1Obj = new XSString(str1);
	    }
	    
	    if (normalizedMap1.get(arg1Obj) != null) {
	    	result = new XSBoolean(true);	
	    }
	    else if (arg1Obj instanceof XSDouble) {
	    	XSDouble xsDouble1 = (XSDouble)arg1Obj;
	    	if (xsDouble1.nan()) {
	    	   Set<XObject> keySet1 = normalizedMap1.keySet();
	    	   Iterator<XObject> iter1 = keySet1.iterator();
	    	   while (iter1.hasNext()) {
	    		  XObject xObj1 = iter1.next();
	    		  if ((xObj1 instanceof XSDouble) && ((XSDouble)xObj1).nan()) {
	    			  result = new XSBoolean(true);
	    			  
	    			  return result;
	    		  }
	    	   }
	    	   
	    	   result = new XSBoolean(false);
	    	   
	    	   return result;
	    	}
	    }
	    else if (arg1Obj instanceof XSFloat) {
	    	XSFloat xsFloat1 = (XSFloat)arg1Obj;
	    	if (xsFloat1.nan()) {
	    	   Set<XObject> keySet1 = normalizedMap1.keySet();
	    	   Iterator<XObject> iter1 = keySet1.iterator();
	    	   while (iter1.hasNext()) {
	    		  XObject xObj1 = iter1.next();
	    		  if ((xObj1 instanceof XSFloat) && ((XSFloat)xObj1).nan()) {
	    			  result = new XSBoolean(true);
	    			  
	    			  return result;
	    		  }
	    	   }
	    	   
               result = new XSBoolean(false);
	    	   
	    	   return result;
	    	}
	    }
	    else if ((arg1Obj instanceof XSDayTimeDuration) || (arg1Obj instanceof XSYearMonthDuration) 
	    		                                        || (arg1Obj instanceof XSDuration)) {
	    	XSDuration xsDurationObj1 = (XSDuration)arg1Obj;

	    	Set<XObject> keySet1 = normalizedMap1.keySet();
	    	Iterator<XObject> iter1 = keySet1.iterator();
	    	while (iter1.hasNext()) {
	    		XObject xObj1 = iter1.next();
	    		if ((xObj1 instanceof XSDayTimeDuration) || (xObj1 instanceof XSYearMonthDuration) 
	    				                                 || (xObj1 instanceof XSDuration)) {
	    			XSDuration xsDurationObj2 = (XSDuration)xObj1;
	    			if (xsDurationObj2.equals(xsDurationObj1)) {
	    				result = new XSBoolean(true);

	    				return result; 
	    			}
	    		}
	    	}

	    	result = new XSBoolean(false);

	    	return result;
	    }
	    else {
	    	result = new XSBoolean(false);	
	    }
	    
	    return result;
	}

}
