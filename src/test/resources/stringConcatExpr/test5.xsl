<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_b.xml -->
   
   <!-- An XSLT stylesheet test, to test the XPath union operator
        '|'. Since the XPath operator string '|' seems syntactically 
        similar to the XPath operator string '||' (an XPath string 
        concatenation operator), we test via this XSLT stylesheet, 
        the XPath operator '|'.
        
        Also, as per XSLT 3.0 spec, different behavior applies when the 
        "xsl:value-of" instruction is processed with XSLT 1.0 processor. 
        If no separator attribute is present on "xsl:value-of" instruction, 
        and if the select attribute is present, then all items in the 
        atomized result sequence other than the first are ignored. We also,
        test this "xsl:value-of" instruction functionality change, within
        this XSLT stylesheet. 
   -->            

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/temp">
      <result>
         <one>
            <xsl:value-of select="a | b"/>
         </one>
         <two>
	        <xsl:copy-of select="a | b"/>
         </two>
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