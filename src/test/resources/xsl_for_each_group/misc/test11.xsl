<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xpath-default-namespace="http://ns0"
                xmlns="http://ns0"				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
   
  <!-- use with test3.xml -->
   
  <!-- An XSL stylesheet test case, to test xsl:for-each-group instruction 
        using attribute 'group-adjacent' to group XML input information within 
        a namespace, and using XSLT attribute xpath-default-namespace. --> 			    		

  <xsl:output method="xml" indent="yes"/>				
  
  <xsl:template match="/">
	 <result>
	   <xsl:apply-templates select="p"/>
	 </result>
  </xsl:template>
  
  <xsl:template match="p">
    <xsl:for-each-group select="node()" group-adjacent="self::ul or self::ol">
        <xsl:choose>
            <xsl:when test="current-grouping-key()">
               <xsl:copy-of select="current-group()"/>  
            </xsl:when>
            <xsl:otherwise>
               <p>
                  <xsl:copy-of select="current-group()"/>
               </p>
            </xsl:otherwise>  
        </xsl:choose>
    </xsl:for-each-group>
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
