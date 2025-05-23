<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- An XSL stylesheet test case, to test XPath 3.1 dynamic 
       function calls. In this XSL stylesheet example, a function call 
       returns a function item (an XSL stylesheet named function reference) 
       which is subsequently called. -->                 
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <result>
	   <xsl:variable name="func3" select="function($p) {if ($p eq true()) then fn0:add#2 else fn0:mult#2}"/>
	   <one>
	      <xsl:value-of select="$func3(true())(2,3)"/>
	   </one>
	   <two>
	      <xsl:value-of select="$func3(false())(5,6)"/>
	   </two>
	</result>
  </xsl:template>
  
  <!-- An XSL stylesheet function to add two integer values -->
  <xsl:function name="fn0:add" as="xs:integer">
     <xsl:param name="x" as="xs:integer"/>
	 <xsl:param name="y" as="xs:integer"/>
	 <xsl:sequence select="$x + $y"/>
  </xsl:function>
  
  <!-- An XSL stylesheet function to multiply two integer values -->
  <xsl:function name="fn0:mult" as="xs:integer">
     <xsl:param name="x" as="xs:integer"/>
	 <xsl:param name="y" as="xs:integer"/>
	 <xsl:sequence select="$x * $y"/>
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
