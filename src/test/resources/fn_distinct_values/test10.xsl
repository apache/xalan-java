<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0"> 
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- This XSL 3 stylesheet test case tests, that for XPath 
         function fn:distinct-values NaN values as equal. 
         W3C XSLT 3.0 test suite, mentions this fact within one
         of its test cases.
    -->                              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <result>
    	  <xsl:variable name="v1" select="number('NaN')"/>
	      <xsl:variable name="v2" select="number('nan')"/>
          <xsl:value-of select="distinct-values((1, 2, 4, $v1, 2, 6, 4, $v2))" separator=" "/>
       </result>
    </xsl:template>
    
    <!--
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
    -->
    
</xsl:stylesheet>
