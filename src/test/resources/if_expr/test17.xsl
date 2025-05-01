<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
  
    <!-- An XSL 3 stylesheet test case, to test combination of XSL 
         instructions xsl:function, xsl:choose and XPath 'if' 
         expression. -->				

	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
	   <result>
          <test1>	   
			  <one><xsl:value-of select="fn0:emitStr1('arg0')"/></one>
			  <two><xsl:value-of select="fn0:emitStr1('arg1')"/></two>
			  <three><xsl:value-of select="fn0:emitStr1('arg-unknown')"/></three>
		  </test1>
		  <test2>
			  <one><xsl:value-of select="fn0:emitStr2('arg0')"/></one>
			  <two><xsl:value-of select="fn0:emitStr2('arg1')"/></two>
			  <three><xsl:value-of select="fn0:emitStr2('arg-unknown')"/></three>
		  </test2>
	   </result>
	</xsl:template>
	
	<!-- An XSL stylesheet function, that emits an xs:string value depending upon 
	     the value of argument provided to the function. The function decides which 
		 value to emit, using xsl:choose instruction. -->
	<xsl:function name="fn0:emitStr1" as="xs:string">
	   <xsl:param name="param1" as="xs:string"/>
	   <xsl:choose>
	      <xsl:when test="$param1 eq 'arg0'">
		    <xsl:sequence select="'emitStr1: arg0 was provided'"/>
		  </xsl:when>
		  <xsl:when test="$param1 eq 'arg1'">
		    <xsl:sequence select="'emitStr1: arg1 was provided'"/>
		  </xsl:when>
		  <xsl:otherwise>
			<xsl:sequence select="'emitStr1: arg-unknown value was provided'"/>
	      </xsl:otherwise>
	   </xsl:choose>
	</xsl:function>
	
	<!-- An XSL stylesheet function, that emits an xs:string value depending upon 
	     the value of argument provided to the function. The function decided which 
		 value to emit, using an XPath 'if' expression. -->
	<xsl:function name="fn0:emitStr2" as="xs:string">
	   <xsl:param name="param1" as="xs:string"/>
	   <xsl:sequence select="if ($param1 eq 'arg0') then ('emitStr2: arg0 was provided') 
	                                                else (if ($param1 eq 'arg1') then ('emitStr2: arg1 was provided') 
													                             else ('emitStr2: arg-unknown value was provided'))"/>
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
