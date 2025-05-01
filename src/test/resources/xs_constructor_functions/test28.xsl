<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test constructor function calls
       for XML Schema types xs:nonNegativeInteger & xs:positiveInteger. -->               

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
      <result>
         <xsl:variable name="result1" select="fn0:getValue1(2,-2)" as="xs:nonNegativeInteger"/>
		 <one><xsl:value-of select="$result1"/></one>
		 <!-- The following commented XSL code when run, shall produce a run-time error since 
		      result of function call fn0:getValue1 shall be negative. -->
		 <!-- 
		 <xsl:variable name="result1" select="fn0:getValue1(2,-3)" as="xs:nonNegativeInteger"/>
		 <xsl:value-of select="$result1"/>
		 -->
		 <!-- The following commented XSL code when run, shall produce a run-time error since 
		      result of function call fn0:getValue2 shall be a non-positive integer. -->
		 <!--
		 <xsl:variable name="result1" select="fn0:getValue2(2,-2)" as="xs:positiveInteger"/>
		 <xsl:value-of select="$result1"/>
		 -->
		 <xsl:variable name="result1" select="fn0:getValue2(2,-1)" as="xs:positiveInteger"/>
		 <two><xsl:value-of select="$result1"/></two>
      </result>
  </xsl:template>
  
  <!-- An XSL stylesheet function that adds two integers,
       and requires the result to be xs:nonNegativeInteger. -->
  <xsl:function name="fn0:getValue1" as="xs:nonNegativeInteger">
     <xsl:param name="a" as="xs:integer"/>
	 <xsl:param name="b" as="xs:integer"/>
	 
	 <xsl:sequence select="$a + $b"/>
  </xsl:function>
  
  <!-- An XSL stylesheet function that adds two integers,
       and requires the result to be xs:positiveInteger. -->
  <xsl:function name="fn0:getValue2" as="xs:positiveInteger">
     <xsl:param name="a" as="xs:integer"/>
	 <xsl:param name="b" as="xs:integer"/>
	 
	 <xsl:sequence select="$a + $b"/>
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