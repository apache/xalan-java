<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:err="http://www.w3.org/2005/xqt-errors"
			    exclude-result-prefixes="err"         				
			    version="3.0">

  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test2.xml -->
  
  <!-- An XSL stylesheet test case, to test xsl:try and xsl:catch 
       instructions. 
       
       An algorithm for this xsl:try and xsl:catch use case, is borrowed
       from XSLT 3.0 spec with minor modifications.       
  -->				
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:import-schema schema-location="info_1.xsd"/>
  
  <xsl:template match="/info">
	 <result>
		<xsl:variable name="var1">
		   <aList id="10">       
			  <xsl:copy-of select="a"/>
			  <xsl:copy-of select="a"/>
			  <xsl:copy-of select="a"/>
		   </aList>
	    </xsl:variable>
		<xsl:try>
	       <xsl:copy-of select="$var1/aList" validation="strict"/>
		   <xsl:catch>
             <warning>
				<errCode><xsl:value-of select="$err:code"/></errCode>
                <reason><xsl:value-of select="$err:description"/></reason>
				<errLocation><xsl:value-of select="'Line number : ' || $err:line-number || ', Column number : ' || $err:column-number"/></errLocation>
				<documentValidated>
				   <xsl:copy-of select="$var1/aList"/>
				</documentValidated>
             </warning>			 
           </xsl:catch>		   
		</xsl:try>
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
