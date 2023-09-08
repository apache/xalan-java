<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
    
    <!-- An XSLT stylesheet test case, to test the sequence type
         declaration attribute "as" on an xsl:variable instruction.
    -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="seq1" select="('2005-10-12', '2501-10-12')" as="xs:date*"/>
        
    <xsl:template match="/">       
       <result>
	      <xsl:for-each select="$seq1">
	         <xsl:variable name="currentDtVal" select="."/>
	         <info val="{$currentDtVal}">
	            <xsl:choose>
	               <xsl:when test="$currentDtVal lt current-date()">
	                  <lessThanCurrentDate/>
	               </xsl:when>
	               <xsl:otherwise>
	                  <currentDateOrAfter/>
	               </xsl:otherwise>
	            </xsl:choose>
	         </info>
	      </xsl:for-each>
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