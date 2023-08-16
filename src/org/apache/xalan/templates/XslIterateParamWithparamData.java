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
 * A class to support evaluation of xsl:iterate instruction.
         
   @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
/*
 * An object of this class, stores information about one xsl:param 
 * element or one xsl:next-iteration->xsl:with-param element, for a 
 * particular xsl:iterate instruction.
 */
public class XslIterateParamWithparamData {
    
    public QName nameVal;
    
    public XPath selectVal;

    public QName getNameVal() {
        return nameVal;
    }

    public void setNameVal(QName nameVal) {
        this.nameVal = nameVal;
    }

    public XPath getSelectVal() {
        return selectVal;
    }

    public void setSelectVal(XPath selectVal) {
        this.selectVal = selectVal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        
        XslIterateParamWithparamData paramWithparamData = 
                                                (XslIterateParamWithparamData)obj;
        return nameVal.equals(paramWithparamData.getNameVal());
    }

}
