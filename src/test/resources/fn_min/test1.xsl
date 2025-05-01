<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT stylesheet test, to test the XPath 3.1 fn:min() 
         function.
         
         The examples of, function fn:min as used within this stylesheet,
         are borrowed from XPath 3.1 F&O spec, with slight modifications.         
    -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="seq1" select="(3, 4, 5)"/>
    
    <xsl:variable name="seq2" select="(xs:integer(5), xs:float(5), xs:double(10))"/>
    
    <xsl:variable name="seq3" select="(xs:float(0.0E0), xs:float(-0.0E0))"/>
    
    <xsl:variable name="seq4" select="(current-date(), xs:date('1900-01-01'))"/>
    
    <xsl:variable name="seq5" select="('a', 'b', 'c')"/>
    
    <xsl:template match="/">
       <result>
         <one>
            <xsl:value-of select="min($seq1)"/> 
         </one>
         <two>
            <xsl:value-of select="min($seq2)"/>
         </two>
         <three>
	        <xsl:value-of select="min($seq3)"/>
         </three>
         <four>
	        <xsl:value-of select="min($seq4)"/>
         </four>
         <five>
	        <xsl:value-of select="min($seq5)"/>
         </five>
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