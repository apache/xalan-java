<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                			
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test XPath 3.1 string 
       concatenation operator "||". -->                
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:template name="main">
     <result>
	    <xsl:variable name="a1" select="'abc'"/>
		<xsl:variable name="a2" select="'pqr'"/>
		<m1><xsl:value-of select="'#'||$a1"/>,<xsl:value-of select="$a2||'#'"/></m1>
		<m2><xsl:value-of select="$a1||$a2||$a1"/></m2>
        <n1><xsl:value-of select="$a2||$a1||$a2"/></n1>
		<n2><xsl:value-of select="'#'||$a1||'#'"/></n2>
        <p1><xsl:value-of select="$a1 ||$a2"/></p1>		
		<p2><xsl:value-of select="$a1|| $a2"/></p2>		
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
