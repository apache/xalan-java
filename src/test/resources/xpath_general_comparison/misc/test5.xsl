<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn0="http://fn0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="#all"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1.xml -->
  
    <!-- An XSL stylesheet test case, to test XPath 3.1 general 
         comparison operator =, using an XML input document, xsl:variable 
         instructions and XSL stylesheet functions within the stylesheet.-->             				
				
    <xsl:output method="xml" indent="yes"/>				

	<xsl:template match="/doc">
	   <result>
	      <xsl:variable name="v1" select="one" as="xs:integer"/>
		  <xsl:variable name="v2" select="four" as="xs:integer"/>
		  <xsl:variable name="var1">
		     <xsl:if test="(for $x in $v1 to $v2 return $x) = fn0:getSeq1()">
			    1
			 </xsl:if>
		  </xsl:variable>
		  <a>
			 <xsl:value-of select="normalize-space($var1)"/>
		  </a>
		  <xsl:variable name="var1">
		     <xsl:if test="(for $x in $v1 to $v2 return $x) = fn0:getSeq2()">
			   2
			 </xsl:if>
	      </xsl:variable>
		  <b>
			 <xsl:value-of select="normalize-space($var1)"/>
		  </b>
	   </result>
	</xsl:template>
	
	<xsl:function name="fn0:getSeq1" as="xs:integer*">
       <xsl:sequence select="(1,7)"/>
    </xsl:function>
	
	<xsl:function name="fn0:getSeq2" as="xs:integer*">
       <xsl:sequence select="(5,6)"/>
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
