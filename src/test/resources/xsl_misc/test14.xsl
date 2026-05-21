<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn0="http://fn0"
				exclude-result-prefixes="#all"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test XPath function call with form .(arg) -->				
				
    <xsl:output method="xml" indent="yes"/>				
				
    <xsl:variable name="funcSeq1" select="(abs#1, fn0:func1#1)" as="function(*)*"/>				
				
    <xsl:template match="/">
       <result>
	      <one>
	         <xsl:for-each select="$funcSeq1">
			    <item><xsl:value-of select=".(-2)"/></item>
		     </xsl:for-each>
		  </one>
		  <two>
		     <xsl:for-each select="abs#1">
			    <item><xsl:value-of select=".(-2)"/></item>
		     </xsl:for-each>
		  </two>
		  <three>
		     <xsl:variable name="funcSeq2" select="abs#1" as="function(*)"/>				
		     <xsl:for-each select="$funcSeq2">
			    <item><xsl:value-of select=".(-2)"/></item>
		     </xsl:for-each>
		  </three>
	   </result>
    </xsl:template>

    <xsl:function name="fn0:func1">
      <xsl:param name="p1"/>
	  <xsl:sequence select="$p1 + 5"/>
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