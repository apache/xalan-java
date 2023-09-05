<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
    
    <!-- An XSLT stylesheet test case, to test the sequence type
         declaration attribute "as" on an xsl:variable instruction.
         
         Within this stylesheet example, the values of the variables
         are determined using the sequence constructor provided as
         xsl:variable's XML child contents. The values of the 
         stylesheet variables need to conform to the sequence type 
         mentioned as value of xsl:variable's "as" attribute.  
    -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="var1" as="element()*">
	   <elem1>a1</elem1>
	   <elem2>b1</elem2>
	   <elem3>c1</elem3>
	</xsl:variable>
	<xsl:variable name="var2" as="element(*)*">
	   <elem1>p2</elem1>
	   <elem2>q2</elem2>
	   <elem3>r2</elem3>
	</xsl:variable>
	<xsl:variable name="var3" as="element(a1)*">
	   <a1>m3</a1>
	   <a1>n3</a1>
	   <a1>o3</a1>
	</xsl:variable>
    
    <xsl:template match="/">       
       <result>
	      <one>
	         <xsl:copy-of select="$var1"/>
	      </one>
	      <two>
	         <xsl:copy-of select="$var2"/>
	      </two>
	      <three>
	         <xsl:copy-of select="$var3"/>
	      </three>
       </result> 
    </xsl:template>
    
    <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the  "License");
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
    -->
    
</xsl:stylesheet>