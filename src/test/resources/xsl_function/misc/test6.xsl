<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0/"
                exclude-result-prefixes="#all"				
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test xsl:function instruction. -->                
				
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
     <result>
	    <xsl:value-of select="fn0:func1(1,2)"/>
	 </result>
  </xsl:template>
  
  <!-- An XSL stylesheet function accepting two arguments, and has 
       a variable within its sequence constructor. The function 
       does type checking on arguments, variable and the return value. -->
  <xsl:function name="fn0:func1" as="xs:integer">
     <xsl:param name="a" as="xs:integer"/>
	 <xsl:param name="b" as="xs:integer"/>
	 <xsl:variable name="c" select="10" as="xs:integer"/>
	 <xsl:sequence select="($a + $b) * $c"/>
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
