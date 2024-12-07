<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- An XSLT stylesheet test case, to test XPath 3.1 "instance of" 
         expression, checking type comparison with data type 
         xs:anyAtomicType. -->                               

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
      <xsl:variable name="uri1" select="xs:anyURI('http://www.example.com/test1')"/>      
      <xsl:variable name="decimal1" select="xs:decimal(5)"/>
      <xsl:variable name="string1" select="xs:string('hello')"/>
      <result>
        <one>
           <xsl:value-of select="$uri1 instance of xs:anyAtomicType"/>
        </one>
        <two>
	       <xsl:value-of select="$decimal1 instance of xs:anyAtomicType"/>
        </two>
        <three>
           <xsl:value-of select="$string1 instance of xs:anyAtomicType"/>
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
