<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
				xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="#all"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test XPath function 
         fn:function-name. -->                
 
    <xsl:output method="xml" indent="yes"/>
 
    <xsl:template match="/">
       <result>
	      <one>
		     <a><xsl:value-of select="fn:function-name(fn:substring#2)"/></a>		  
		     <b><xsl:value-of select="function-name(substring#2)"/></b>
		     <c><xsl:value-of select="function-name(math:pi#0)"/></c>
		  </one>
		  <two>
		     <xsl:variable name="var1" select="fn:substring#2" as="function(*)"/>
			 <xsl:variable name="var2" select="substring#2" as="function(*)"/>
			 <xsl:variable name="var3" select="math:pi#0" as="function(*)"/>
		     <a><xsl:value-of select="fn:function-name($var1)"/></a>		  
		     <b><xsl:value-of select="function-name($var2)"/></b>
		     <c><xsl:value-of select="function-name($var3)"/></c>
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
