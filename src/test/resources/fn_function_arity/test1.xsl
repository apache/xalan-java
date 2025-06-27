<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="#all"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath function 
         fn:function-arity. -->                
 
    <xsl:output method="xml" indent="yes"/>
 
    <xsl:template match="/">
       <result>                
		  <one>
		     <a>
		       <xsl:value-of select="function-arity(abs#1)"/>
			 </a>
			 <b>
			   <xsl:variable name="funcVar1" select="abs#1" as="function(*)"/>
			   <xsl:value-of select="function-arity($funcVar1)"/>
			 </b>
		  </one>		  
		  <two>
		     <a>
		       <xsl:value-of select="function-arity(fn0:func1#2)"/>
			 </a>
			 <b>
			   <xsl:variable name="xslFunc1" select="fn0:func1#2" as="function(*)"/>
			   <xsl:value-of select="function-arity($xslFunc1)"/>
			 </b>
		  </two>
		  <three>
		     <a>
		       <xsl:value-of select="function-arity(function($a as xs:integer) as xs:integer { $a })"/>
			 </a>
			 <b>
			   <xsl:variable name="func1" select="function($a as xs:integer, $b as xs:integer) as xs:integer { $a + $b }" as="function(*)"/>
			   <xsl:value-of select="function-arity($func1)"/>
			 </b>
		  </three>
       </result>
    </xsl:template>
	
	<xsl:function name="fn0:func1" as="xs:integer">
	   <xsl:param name="x" as="xs:integer"/>
	   <xsl:param name="y" as="xs:integer"/>
	   <xsl:sequence select="$x + $y"/>
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
