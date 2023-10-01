<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test xsl:iterate instruction. -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">     
     <result>
        <!-- Evaluate factorial of various positive integer numbers,
             provided by xsl:for-each instruction.
        -->
        <xsl:for-each select="1 to 7">
          <xsl:variable name="num" select="." as="xs:integer"/>
          <xsl:copy-of select="fn0:factorial($num)"/>
        </xsl:for-each>
     </result>
  </xsl:template>
  
  <!-- A stylesheet function, to evaluate factorial of a positive 
       integer number. 
  -->
  <xsl:function name="fn0:factorial" as="element(factorial)">
     <xsl:param name="inpNum" as="xs:integer"/>

     <xsl:choose>
        <xsl:when test="$inpNum = 1">
          <factorial num="{$inpNum}">
	         <xsl:value-of select="1"/>
     	  </factorial>
        </xsl:when>
        <xsl:otherwise>
           <xsl:iterate select="2 to $inpNum">
              <xsl:param name="result" select="1" as="xs:integer"/>
              <xsl:on-completion>
	             <factorial num="{$inpNum}">
	                <xsl:value-of select="$result"/>
	     	     </factorial>
	          </xsl:on-completion>
              <xsl:variable name="currVal" select="."/>
              <xsl:next-iteration>
     	         <xsl:with-param name="result" select="$result * $currVal" as="xs:integer"/>
              </xsl:next-iteration>
           </xsl:iterate>
        </xsl:otherwise>
     </xsl:choose>
  </xsl:function>
  
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