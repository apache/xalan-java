<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
  
    <!-- An XSL stylesheet test case, to test XPath 3.1 general 
         comparison operator =, with XPath 'for' expression as LHS 
         operand. -->                				
				
    <xsl:output method="xml" indent="yes"/>				

	<xsl:template match="/">
	   <result>
		  <one>
		     <xsl:variable name="var1">
		        <xsl:if test="(for $x in 1 to 5 return $x) = (1,7)">
			       1
			    </xsl:if>
			 </xsl:variable>
			 <a>
			    <xsl:value-of select="normalize-space($var1)"/>
		     </a>
			 <xsl:variable name="var1">
		        <xsl:if test="(for $x in 1 to 5 return $x) = [1,7]">
			       2
			    </xsl:if>
			 </xsl:variable>
			 <b>
			    <xsl:value-of select="normalize-space($var1)"/>
		     </b>
		  </one>
		  <two>
		     <xsl:variable name="var1">
		        <xsl:if test="(for $x in 2 to 10 return $x) = (1,15)">
			       1
			    </xsl:if>
			 </xsl:variable>
			 <a>
			    <xsl:value-of select="normalize-space($var1)"/>
		     </a>
			 <xsl:variable name="var1">
		        <xsl:if test="(for $x in 2 to 10 return $x) = [1,15]">
			       2
			    </xsl:if>
			 </xsl:variable>
			 <b>
			    <xsl:value-of select="normalize-space($var1)"/>
		     </b>
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
