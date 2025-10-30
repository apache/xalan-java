<?xml version="1.0" encoding="utf-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="#all"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
                
    <!-- use with test1.xml -->
    
    <!-- An XSL stylesheet test case, to solve with an XSL stylesheet 
         alternative algorithm, an XSL transformation use case specified within W3C 
         XSLT 3.0 test case insn\apply-templates\conflict-resolution-0503.
    -->                
	
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="*">
       <xsl:copy><xsl:apply-templates/></xsl:copy>
    </xsl:template>
  
	<xsl:template match="*[fn0:isParentNodeNameEqual(.)]">
       <xsl:copy>
	      <xsl:attribute name="parent-recursive">yes</xsl:attribute>
          <xsl:apply-templates/>
	   </xsl:copy>
    </xsl:template>
	
	<xsl:function name="fn0:isParentNodeNameEqual" as="xs:boolean">
	   <xsl:param name="node1" as="element()"/>
	   <xsl:variable name="node2" select="$node1/.." as="node()"/>
       <xsl:variable name="nameValue1" select="name($node1)" as="xs:string"/>
	   <xsl:variable name="nameValue2" select="name($node2)" as="xs:string"/>
	   <xsl:sequence select="$nameValue1 eq $nameValue2"/>
	</xsl:function>
	
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
