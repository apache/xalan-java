<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!--  An XSL 3 stylesheet test case, to test xsl:template match 
        with pattern ".", and resolving which xsl:template to 
        use depending on priority of template.
        
        This XSL stylesheet example's algorithm has been borrowed
        from XSLT 3.0 spec.         
  -->			    

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <result>	   
	   <xsl:for-each select="unparsed-text-lines('input.txt')">
		  <xsl:variable name="item1" select="." as="xs:string"/>		      
		  <xsl:apply-templates select="$item1"/>
	   </xsl:for-each>
    </result>
  </xsl:template>

  <xsl:template match="." priority="3">
    <m><xsl:value-of select="."/></m>
  </xsl:template>
  
  <xsl:template match="." priority="2">
    <n><xsl:value-of select="."/></n>
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
