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
 * An object of this class stores, an XSLT transformation run-time
 * information for an occurrence of a XPath 3.1 sequence type
 * kind test (for e.g, element(), element(elemName), attribute(), 
 * element(elemName, typeName)* etc. 
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class SequenceTypeKindTest {
    
    private int kindVal;
    
    // For XML element and attribute nodes
    private String nodeLocalName;
    
    // For XML element and attribute nodes
    private String nodeNsUri;
    
    // Data type's name (for e.g, string, integer etc)
    private String dataTypeLocalName;
    
    // XML namespace uri of the data type (for e.g, http://www.w3.org/2001/XMLSchema)
    private String dataTypeUri;

    public int getKindVal() {
        return kindVal;
    }

    public void setKindVal(int kindVal) {
        this.kindVal = kindVal;
    }

    public String getNodeLocalName() {
        return nodeLocalName;
    }

    public void setNodeLocalName(String nodeLocalName) {
        this.nodeLocalName = nodeLocalName;
    }

    public String getNodeNsUri() {
        return nodeNsUri;
    }

    public void setNodeNsUri(String nodeNsUri) {
        this.nodeNsUri = nodeNsUri;
    }

    public String getDataTypeName() {
        return dataTypeLocalName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeLocalName = dataTypeName;
    }
    
    public String getDataTypeUri() {
        return dataTypeUri;
    }

    public void setDataTypeUri(String dataTypeUri) {
        this.dataTypeUri = dataTypeUri;
    }
    
    /**
     * Check whether, one SequenceTypeKindTest object is functionally
     * equal to another SequenceTypeKindTest object.  
     */
    public boolean equal(SequenceTypeKindTest sequenceTypeKindTest) {
    	boolean isEqual = true;
    	
    	int kindVal2 = sequenceTypeKindTest.getKindVal();
    	String nodeLocalName2 = sequenceTypeKindTest.getNodeLocalName();
    	String nodeNsUri2 = sequenceTypeKindTest.getNodeNsUri();
        String dataTypeLocalName2 = sequenceTypeKindTest.getNodeLocalName();
        
        if (this.kindVal != kindVal2) {
           isEqual = false;
        }
        else if ((this.nodeLocalName == null) && (nodeLocalName2 == null)) {
           isEqual = true;
        }
        else if ((this.nodeLocalName == null) && (nodeLocalName2 != null)) {
           isEqual = false;
        }
        else if ((this.nodeLocalName != null) && (nodeLocalName2 == null)) {
           isEqual = false;
        }
        else if (!(this.nodeLocalName).equals(nodeLocalName2)) {
           isEqual = false;
        }
        else if ((this.nodeNsUri == null) && (nodeNsUri2 == null)) {
           isEqual = true;
        }
        else if ((this.nodeNsUri == null) && (nodeNsUri2 != null)) {
           isEqual = false;
        }
        else if ((this.nodeNsUri != null) && (nodeNsUri2 == null)) {
           isEqual = false;
        }
        else if (!(this.nodeNsUri).equals(nodeNsUri2)) {
           isEqual = false;
        }
        else if ((this.dataTypeLocalName == null) && (dataTypeLocalName2 == null)) {
           isEqual = true;
        }
        else if ((this.dataTypeLocalName == null) && (dataTypeLocalName2 != null)) {
           isEqual = false;
        }
        else if ((this.dataTypeLocalName != null) && (dataTypeLocalName2 == null)) {
           isEqual = false;
        }
        else if (!(this.dataTypeLocalName).equals(dataTypeLocalName2)) {
           isEqual = false;
        }
    	
    	return isEqual;
    }

}
