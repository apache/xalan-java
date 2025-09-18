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
package org.apache.xalan.templates;

import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;

/**
 * A class definition, that represents xsl:iterate instruction's 
 * xsl:param definition, an xsl:next-iteration instruction's 
 * xsl:with-param definition.
 *        
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class XslIterateParamWithparamData {
    
    private QName m_name;
    
    private XPath m_select;
    
    public QName getName() {
        return this.m_name;
    }

    public void setName(QName name) {
        this.m_name = name;
    }

    public XPath getSelect() {
        return this.m_select;
    }

    public void setSelect(XPath select) {
        this.m_select = select;
    }
    
    @Override
    public boolean equals(Object obj2) {
        if (this == obj2) {
            return true;
        }
        else if ((obj2 == null) || (getClass() != obj2.getClass())) {
            return false;
        }
        
        XslIterateParamWithparamData paramWithparamData = 
                                                (XslIterateParamWithparamData)obj2;
        return m_name.equals(paramWithparamData.getName());
    }

}
