<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath 3.1 expression for 
         literal sequence. This stylesheet test case, is motivated by XPath 
         examples available within W3C XSLT 3.0 test case insn\try\try-019.         
    -->                
				
    <xsl:output method="xml" indent="yes"/>				

    <xsl:template match="/">
       <result>
	      <xsl:variable name="v1" select="'xyz'"/>
	      <xsl:variable name="v2" select="'pqr'"/>
		  <xsl:variable name="v3" select="'abc', tokenize('X/Y/Z', '/')[last()], ('#' || $v1 || '#'), $v2"/>
		  <xsl:variable name="v4" select="('abc', tokenize('X/Y/Z', '/')[last()], ('#' || $v1 || '#'), $v2)"/>
		  <xsl:variable name="v5" select="('abc', tokenize('X/Y/Z', '/')[1], ('#' || $v1 || '#'), $v2)"/>
		  <xsl:variable name="v6" select="('abc', tokenize('X/Y/Z', '/')[2], ('#' || $v1 || '#'), $v2)"/>
	      <one size="{count($v3)}"><xsl:value-of select="$v3"/></one>
	      <two size="{count($v4)}"><xsl:value-of select="$v4"/></two>
		  <three size="{count($v5)}"><xsl:value-of select="$v5"/></three>
		  <four size="{count($v6)}"><xsl:value-of select="$v6"/></four>
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
