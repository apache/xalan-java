<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
    
    <!-- Author: mukulg@apache.org -->
   
    <!-- An XSLT stylesheet test, to test the XPath 3.1 fn:parse-xml-fragment()
         function. The XPath function fn:parse-xml-fragment's usage examples,
         as used within this stylesheet are borrowed from XPath 3.1 F&O spec. 
         
         The XML special characters present within, function call 
         fn:parse-xml-fragment's argument, have been escaped according to XML
         conventions.
    -->
         
    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="xmlDocNode1" select="parse-xml-fragment('&lt;alpha&gt;abcd&lt;/alpha&gt;&lt;beta&gt;abcd&lt;/beta&gt;')"/>
    
    <xsl:variable name="xmlDocNode2" select="parse-xml-fragment('He was &lt;i&gt;so&lt;/i&gt; kind')"/>
    
    <xsl:variable name="xmlDocNode3" select="parse-xml-fragment('')"/>
    
    <xsl:variable name="xmlDocNode4" select="parse-xml-fragment(' ')"/>
    
    <xsl:template match="/">
       <result>
         <one>
            <xsl:copy-of select="$xmlDocNode1"/>
         </one>
         <two>
	        <xsl:copy-of select="$xmlDocNode2"/>
         </two>
         <three>
	        <xsl:copy-of select="$xmlDocNode3"/>
         </three>
         <four>
	        <xsl:copy-of select="$xmlDocNode4"/>
         </four>
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