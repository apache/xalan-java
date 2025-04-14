<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_f.xml -->
   
   <!-- Test case description : This XSLT stylesheet, searches an XML input 
        document's contents, for the keywords mentioned within an auxiliary 
        text file srch_file.txt and produces the search results as output
        of XSLT transformation. -->                 

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="scfFileContents" select="unparsed-text('srch_file.txt')"/>
   
   <xsl:variable name="filterCheck" select="function($str, $srch) { if (contains($str, $srch)) 
                                                                           then true() 
                                                                           else false() }"/>
                                                                           
   <xsl:template match="/">
      <result>      
	      <xsl:variable name="srchInp" select="/list/word"/>
	      <xsl:variable name="srchKeyWords" select="tokenize($scfFileContents, ',')"/>
	      <xsl:for-each select="$srchKeyWords">
	        <xsl:variable name="keyWord" select="."/>
	        <srchResult> 
	          <xsl:copy-of select="$srchInp[$filterCheck(., $keyWord)]"/>
	        </srchResult>
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