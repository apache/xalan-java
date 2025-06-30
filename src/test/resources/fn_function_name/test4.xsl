<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema"               
				xmlns:fn="http://www.w3.org/2005/xpath-functions"				
                exclude-result-prefixes="#all"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath function 
         fn:function-name. -->                
 
    <xsl:output method="xml" indent="yes"/>
 
    <xsl:template match="/">
       <result>
	      <one>
		     <a><xsl:value-of select="fn:function-name(function($x as xs:integer) as xs:integer {$x + 2})"/></a>		  
		     <b><xsl:value-of select="function-name(function($x as xs:integer) as xs:integer {$x + 2})"/></b>
			 <c><xsl:value-of select="fn:function-name(function($x as xs:integer) as xs:integer {$x + 2}) instance of empty-sequence()"/></c>
			 <d><xsl:value-of select="function-name(function($x as xs:integer) as xs:integer {$x + 2}) instance of empty-sequence()"/></d>
		  </one>
		  <two>
		     <xsl:variable name="var1" select="function($x as xs:integer) as xs:integer {$x + 2}" as="function(*)"/>
		     <a><xsl:value-of select="fn:function-name($var1)"/></a>		  
		     <b><xsl:value-of select="function-name($var1)"/></b>
			 <c><xsl:value-of select="fn:function-name($var1) instance of empty-sequence()"/></c>
			 <d><xsl:value-of select="function-name($var1) instance of empty-sequence()"/></d>
		  </two>
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
