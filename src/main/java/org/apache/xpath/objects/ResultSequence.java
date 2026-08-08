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
package org.apache.xpath.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.xalan.xslt.util.XslTransformEvaluationHelper;

/**
 * This class represents, an XPath 3.1 xdm sequence.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class ResultSequence extends XObject
{
    static final long serialVersionUID = -5736721866747906182L;
    
    /**
     * java.util.List object, to store items of this XDM result sequence.
     */
    private List<XObject> m_list = new ArrayList<XObject>();
    
    /**
     * Class field, to represent whether a ResultSequence object
     * represents an xdm sequence of nodes that don't have an
     * xdm parent item. 
     */
    private boolean m_xdm_parentless_sibling_nodes = false;
    
    /**
     * Class field, to represent whether this sequence object
     * contains fully populated data, or only the two boundary
     * xs:integer values.
     * 
     * When the size of sequence is two, and value of m_is_seq_expanded 
     * is false, the this sequence object has been populated by XPath 3.1 
     * range 'to' expression's evaluation. 
     */
    private boolean m_is_seq_expanded = true;
    
    /**
     * Class constructor.
     */
    public ResultSequence() {}
    
    public int getType()
    {
        return CLASS_RESULT_SEQUENCE;
    }
    
    /** 
     * Append an item at the end of the sequence.
     */
    public void add(XObject item) {
        m_list.add(item);    
    }
    
    /**
     * Set an item at a particular index.
     */
    public void set(int idx, XObject item) {
        m_list.set(idx, item);
    }
    
    /**
     * Get an item stored at a particular index.
     */
    public XObject item(int idx) {
    	XObject result = null;
    	
    	XObject xObj = m_list.get(idx);
    	if (xObj instanceof XMLNodeCursorImpl) {
    		result = xObj.getFresh(); 
    	}
    	else {
    		result = xObj; 
    	}
    	
        return result;     
    }
    
    /**
     * Remove an item from the specified index.
     * 
     * @param idx
     */
    public void remove(int idx) {
       m_list.remove(idx);
    }
    
    /**
     * Remove all items from the sequence object.
     */
    public void clear() {
    	m_list.clear();
    }
    
    /**
     * Get the size of the current sequence object.
     */
    public int size() {
        return m_list.size();   
    }
    
    /**
     * Get the contents of this sequence object, as list of 
     * XObject objects. 
     */
    public List<XObject> getResultSequenceItems() {
        return m_list;   
    }
    
    /**
     * Cast result object to a boolean.
     *
     * @return True if the size of this 'ResultSequence' object
     * is greater than 0.
     */
    public boolean bool() {
        return (m_list.size() > 0);       
    }
    
    /**
     * Method definition, to get string value of the supplied 
     * ResultSequence object.
     * 
     * This method, produces a default serialization format of
     * string value, which is space character separated string 
     * values of the xdm items within this sequence.
     */
    public String str() {        
    	String result = null;
        
        StringBuffer strBuff = new StringBuffer();
        int rsSize = m_list.size();
        for (int idx = 0; idx < rsSize; idx++) {
           XObject xObj = item(idx);
           String itemStrValue = XslTransformEvaluationHelper.getStrVal(xObj);
           if (idx < (rsSize - 1)) {        	           	   
              strBuff.append(itemStrValue + " ");
           }
           else {
              strBuff.append(itemStrValue);
           }
        }
        
        result = strBuff.toString(); 
        
        return result;
    }
    
    public boolean equals(Object obj) {
    	boolean result = true;
    	
    	if (!(obj instanceof ResultSequence)) {
    	   return false;	
    	}
    	
    	String strVal1 = str();
    	String strVal2 = ((ResultSequence)obj).str();
    	
    	result = strVal1.equals(strVal2);
    	
    	return result;
    }
    
    public boolean equals(XObject xObj) {
        
    	boolean result = false;
        
        int size1 = this.size();
        for (int idx = 0; idx < size1; idx++) {
           XObject xObj1 = (XObject)(this.item(idx));
           if (xObj1.equals(xObj)) {
        	  result = true;
        	  
        	  break;
           }
        }
        
        return result;
    }
    
    public int hashCode() {
    	String strVal = str();
    	
    	return strVal.hashCode();
    }

	public boolean isXdmParentlessSiblingNodes() {
		return m_xdm_parentless_sibling_nodes;
	}

	public void setXdmParentlessSiblingNodes(boolean xdmParentlessSiblingNodes) {
		this.m_xdm_parentless_sibling_nodes = xdmParentlessSiblingNodes;
	}
	
	public boolean getSequenceExpanded() {
		return m_is_seq_expanded;
	}

	public void setSequenceExpanded(boolean seqExpanded) {
		this.m_is_seq_expanded = seqExpanded; 		
	}

}
