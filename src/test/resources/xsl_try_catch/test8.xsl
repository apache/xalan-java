<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"			    
				xmlns:err="http://www.w3.org/2005/xqt-errors"
			    exclude-result-prefixes="xs err"
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test xsl:try and xsl:catch 
       instructions. 
  
       This stylesheet reads a CSV document file using XPath function 
       fn:unparsed-text-lines. A numeric addition is performed within
       an xsl:try instruction. If an addition fails due to incorrectly typed 
       operands, then xsl:catch instruction produces a recovery result 
       (an XML element "error"), otherwise addition details get available
       with an XML element "sum". When a particular XPath addition operation 
       fails, the subsequent xsl:for-each iterations are continued to be 
       processed.
       
       In the absence of xsl:try instruction processing, an XSL stylesheet 
       processing will be aborted after the first addition operation failure.                     
  -->				

  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="fileLines" select="unparsed-text-lines('info1.csv')" as="xs:string*"/>

  <xsl:template match="/">
     <result>
	   <xsl:for-each select="$fileLines">
	     <xsl:variable name="line" select="." as="xs:string"/>
	     <xsl:variable name="lineTokens" select="tokenize($line, ',')" as="xs:string*"/>
		 <xsl:variable name="a" select="$lineTokens[6]"/>
		 <xsl:variable name="b" select="$lineTokens[7]"/>
		 <xsl:variable name="xslTryResult1">
			 <xsl:try rollback-output="no">
				<sum id="{$lineTokens[1]}" num1="{$a}" num2="{$b}"><xsl:value-of select="$a + $b"/></sum>
				<xsl:catch errors="*">
				   <error id="{$lineTokens[1]}">
				      <additionOperands><xsl:value-of select="$a || ', ' || $b"/></additionOperands>
					  <errCode><xsl:value-of select="$err:code"/></errCode>
					  <errDesc><xsl:value-of select="$err:description"/></errDesc>
				   </error>
				</xsl:catch>	
			 </xsl:try>
		 </xsl:variable>
		 <xsl:copy-of select="if (not($xslTryResult1/error)) then $xslTryResult1/sum else $xslTryResult1/error"/>
	   </xsl:for-each>
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
