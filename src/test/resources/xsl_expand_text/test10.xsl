<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
				xmlns:fn0="http://fn0"
                exclude-result-prefixes="#all"				
                expand-text="yes"                                						
                version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test2.xml -->
  
  <!-- An XSL stylesheet test case, to test XSLT 3.0 
       'expand-text' attribute. --> 				
  
  <xsl:output method="xml" indent="yes"/>    

  <xsl:template match="/">
     <result>	    
        <a>
		  <xsl:text>{math:pow(doc/a,doc/b)}</xsl:text>
		</a>
		<b>		  
		  <xsl:text>{fn0:apply(doc/a,doc/b,$add)}</xsl:text>
		</b>
		<c>		  
		  <xsl:text>{fn0:apply(doc/a,doc/b,$mult)}</xsl:text>
		</c>
		<d>
		  <!-- An 'expand-text' attribute here, overrides 'expand-text' 
		       specified on xsl:stylesheet element. -->
		  <xsl:text expand-text="no">{fn0:apply(doc/a,doc/b,$mult)}</xsl:text>
		</d>
     </result>
  </xsl:template>
  
  <xsl:variable name="add" select="function($x as xs:integer, $y as xs:integer) as xs:integer {$x + $y}" as="function(*)"/>
  
  <xsl:variable name="mult" select="function($x as xs:integer, $y as xs:integer) as xs:integer {$x * $y}" as="function(*)"/>
  
  <xsl:function name="fn0:apply" as="xs:integer">
     <xsl:param name="x" as="xs:integer"/>
	 <xsl:param name="y" as="xs:integer"/>
	 <xsl:param name="f" as="function(*)"/>
	 <xsl:sequence select="$f($x,$y)"/>
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
