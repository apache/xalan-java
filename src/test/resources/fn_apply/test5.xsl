<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:local="http://local"
                exclude-result-prefixes="#all"				
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:apply -->                   
				
   <xsl:output method="xml" indent="yes"/>				

   <xsl:template match="/">
      <result>	  
         <one>
		    <xsl:value-of select="apply(local:sum#1, [2])"/>
		 </one>
		 <two>
		    <xsl:value-of select="apply(local:sum#2, [2,3])"/>
		 </two>
		 <three>
		    <xsl:value-of select="apply(local:sum#3, [1,2,3])"/>
		 </three>
      </result>
   </xsl:template>
   
   <xsl:function name="local:sum" as="xs:integer">
     <xsl:param name="n" as="xs:integer"/>
     <xsl:sequence select="$n + 1"/>
   </xsl:function>
   
   <xsl:function name="local:sum" as="xs:integer">
     <xsl:param name="n1" as="xs:integer"/>
	 <xsl:param name="n2" as="xs:integer"/>
	 <xsl:sequence select="$n1 + $n2"/>
   </xsl:function>
   
   <xsl:function name="local:sum" as="xs:integer">
     <xsl:param name="n1" as="xs:integer"/>
	 <xsl:param name="n2" as="xs:integer"/>
	 <xsl:param name="n3" as="xs:integer"/>
	 <xsl:sequence select="$n1 + $n2 + $n3"/>
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
