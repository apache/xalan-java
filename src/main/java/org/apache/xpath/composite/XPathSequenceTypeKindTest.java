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
package org.apache.xpath.composite;

/**
 * A class definition, that stores, an XPath run-time information for 
 * a sequence type kind test expression (for e.g, element(), element(elemName), 
 * attribute(), element(elemName, typeName)* etc. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XPathSequenceTypeKindTest {
    
	/**
	 * Xalan-J specific valid values for this variable are
	 * specified in the class SequenceTypeSupport.
	 */
    private int m_kindVal;
    
    /**
     * An XML element or attribute node's local name.
     */
    private String m_nodeLocalName;
    
    /**
     * An XML element or attribute node's namespace uri.
     */
    private String m_nodeNsUri;
    
    /**
     * Data type's local name.
     */
    private String m_dataTypeLocalName;
    
    /**
     * Data type's namespace uri.
     */
    private String m_dataTypeNsUri;
    
    /**
     * Sequence type kind test may have, another contained sequence 
     * type kind test, for e.g document-node(element(elem_name)).
     */
    private XPathSequenceTypeKindTest m_seqTypeSubKindTest;

    public int getKindVal() {
        return m_kindVal;
    }

    public void setKindVal(int kindVal) {
        this.m_kindVal = kindVal;
    }

    public String getNodeLocalName() {
        return m_nodeLocalName;
    }

    public void setNodeLocalName(String nodeLocalName) {
        this.m_nodeLocalName = nodeLocalName;
    }

    public String getNodeNsUri() {
        return m_nodeNsUri;
    }

    public void setNodeNsUri(String nodeNsUri) {
        this.m_nodeNsUri = nodeNsUri;
    }

    public String getDataTypeLocalName() {
        return m_dataTypeLocalName;
    }

    public void setDataTypeLocalName(String dataTypeName) {
        this.m_dataTypeLocalName = dataTypeName;
    }
    
    public String getDataTypeNsUri() {
        return m_dataTypeNsUri;
    }

    public void setDataTypeNsUri(String dataTypeNsUri) {
        this.m_dataTypeNsUri = dataTypeNsUri;
    }
    
    public XPathSequenceTypeKindTest getSeqTypeSubKindTest() {
    	return m_seqTypeSubKindTest;
    }
    
    public void setSeqTypeSubKindTest(XPathSequenceTypeKindTest seqTypeSubKindTest) {
    	this.m_seqTypeSubKindTest = seqTypeSubKindTest;
    }
    
    /**
     * Method definition, to check whether, one SequenceTypeKindTest object 
     * is functionally equal to another SequenceTypeKindTest object.
     * 
     * @param sequenceTypeKindTest2					   An SequenceTypeKindTest object instance,
     *                                                 that needs to be compared with this
     *                                                 SequenceTypeKindTest object instance.
     * @return										   Boolean value true or false
     */
    public boolean equal(XPathSequenceTypeKindTest sequenceTypeKindTest2) {
    	
    	boolean result = true;
    	
    	int kindVal2 = sequenceTypeKindTest2.getKindVal();
    	
    	String nodeLocalName2 = sequenceTypeKindTest2.getNodeLocalName();
    	String nodeNsUri2 = sequenceTypeKindTest2.getNodeNsUri();
        String dataTypeLocalName2 = sequenceTypeKindTest2.getDataTypeLocalName();
        String dataTypeUri2 = sequenceTypeKindTest2.getDataTypeNsUri();
        
        if (this.m_kindVal != kindVal2) {
           result = false;
        }
        else if ((this.m_nodeLocalName == null) && (nodeLocalName2 == null)) {
           result = true;
        }
        else if ((this.m_nodeLocalName == null) && (nodeLocalName2 != null)) {
           result = false;
        }
        else if ((this.m_nodeLocalName != null) && (nodeLocalName2 == null)) {
           result = false;
        }
        else if (!(this.m_nodeLocalName).equals(nodeLocalName2)) {
           result = false;
        }
        else if ((this.m_nodeNsUri == null) && (nodeNsUri2 != null)) {
           result = false;
        }
        else if ((this.m_nodeNsUri != null) && (nodeNsUri2 == null)) {
           result = false;
        }
        else if ((m_nodeNsUri != null) && !(this.m_nodeNsUri).equals(nodeNsUri2)) {
           result = false;
        }
        else if ((this.m_dataTypeLocalName == null) && (dataTypeLocalName2 == null)) {
           result = true;
        }
        else if ((this.m_dataTypeLocalName == null) && (dataTypeLocalName2 != null)) {
           result = false;
        }
        else if ((this.m_dataTypeLocalName != null) && (dataTypeLocalName2 == null)) {
           result = false;
        }
        else if ((m_dataTypeLocalName != null) && !(this.m_dataTypeLocalName).equals(dataTypeLocalName2)) {
           result = false;
        }
        else if ((this.m_dataTypeNsUri == null) && (dataTypeUri2 != null)) {
           result = false;
        }
        else if ((this.m_dataTypeNsUri != null) && (dataTypeUri2 == null)) {
           result = false;
        }
        else if ((m_dataTypeNsUri != null) && !(this.m_dataTypeNsUri).equals(dataTypeUri2)) {
           result = false;
        }
    	
    	return result;
    }

}
