<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test, for the XPath 3.1 function fn:avg.
        
        The XPath function fn:avg's usage examples, as illustrated
        within this stylesheet are borrowed from XPath 3.1 F&O spec. 
   -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="seq1" select="(3, 4, 5)"/>
    
    <xsl:variable name="seq2" select="(xs:float('INF'), xs:float('-INF'))"/>
    
    <xsl:variable name="seq3" select="($seq1, xs:float('NaN'))"/>
    
    <xsl:template match="/">
       <result>
         <one>
            <xsl:value-of select="avg($seq1)"/> 
         </one>
         <two>
            <xsl:value-of select="avg(())"/>
         </two>
         <three>
	        <xsl:value-of select="avg($seq2)"/>
         </three>
         <four>
	        <xsl:value-of select="avg($seq3)"/>
         </four>
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