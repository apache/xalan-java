<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test xsl:iterate instruction. -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">     
     <result>
        <!-- Evaluate factorial of a positive integer number, by invoking a 
             stylesheet named template. 
        -->
        <xsl:variable name="inpNum" select="5" as="xs:integer"/>
        <xsl:call-template name="factorial">
           <xsl:with-param name="inpNum" select="$inpNum" as="xs:integer"/>
        </xsl:call-template>
        
        <!-- Evaluate factorial of another positive integer number, by invoking a 
             stylesheet named template. 
        -->
	    <xsl:variable name="inpNum" select="6" as="xs:integer"/>
	    <xsl:call-template name="factorial">
	       <xsl:with-param name="inpNum" select="$inpNum" as="xs:integer"/>
        </xsl:call-template>
     </result>
  </xsl:template>
  
  <!-- A stylesheet named template, to evaluate factorial of a positive 
       integer number. 
  -->
  <xsl:template name="factorial" as="element(factorial)">
     <xsl:param name="inpNum" as="xs:integer"/>
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