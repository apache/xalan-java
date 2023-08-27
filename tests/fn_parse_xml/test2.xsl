<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- An XSLT stylesheet test, to test the XPath 3.1 fn:parse-xml() 
         function.
         
         Within this stylesheet, we parse an XML document string value
         with function call fn:parse-xml, and do an XSLT transformation
         on the XML document node returned by the function call 
         fn:parse-xml. -->                
                
    <xsl:output method="xml" indent="yes"/>
    
    <!-- The XML document string value, passed to fn:parse-xml 
         function call within below mentioned xsl:variable 
         declaration is following (with XML special characters 
         escaped within function call fn:parse-xml's argument, 
         according to XML conventions),
                  
         <alpha>
            <a>hello</a>
            <b>world</b>
            <c>this</c>
            <d>is</d>
            <e>an xml sample document</e>
         </alpha>          
    -->
    <xsl:variable name="xmlDocNode" select="parse-xml('&lt;alpha&gt;
                                                          &lt;a&gt;hello&lt;/a&gt;
                                                          &lt;b&gt;world&lt;/b&gt;
                                                          &lt;c&gt;this&lt;/c&gt;
                                                          &lt;d&gt;is&lt;/d&gt;
                                                          &lt;e&gt;an xml sample document&lt;/e&gt;
                                                       &lt;/alpha&gt;')"/>
        
    <xsl:template match="/">
       <result>
          <xsl:apply-templates select="$xmlDocNode/alpha"/>
       </result>
    </xsl:template>
    
    <xsl:template match="alpha">
      <alpha>
         <xsl:apply-templates select="*" mode="m1"/>
      </alpha>
    </xsl:template>
    
    <xsl:template match="*" mode="m1">
       <xsl:element name="{name()}">
          <xsl:attribute name="strLen" select="string-length(.)"/>
          <xsl:copy-of select="node()"/>
       </xsl:element>
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