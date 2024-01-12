<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, that tests an arithmetic operator 
       '+' with various kinds of function calls as operands. -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <!-- Specifying an inline function expression, that returns an argument value unchanged. -->
  <xsl:variable name="identityFunc1" select="function($a as xs:integer) as xs:integer { $a }"/>
  
  <xsl:template match="/">
    <result>      
      <one>
        <xsl:value-of select="$identityFunc1(5) + fn0:identityFunc2(7)"/>
      </one>
      <two>
        <xsl:value-of select="fn0:identityFunc2(2) + $identityFunc1(5)"/>
      </two>
      <three>
        <xsl:value-of select="fn0:identityFunc2(2) + fn0:identityFunc2(10)"/>
      </three>
    </result> 
  </xsl:template>
  
  <!-- A stylesheet function, that returns an argument value unchanged. -->
  <xsl:function name="fn0:identityFunc2" as="xs:integer">
    <xsl:param name="a" as="xs:integer"/>
    
    <xsl:sequence select="$a"/>
  </xsl:function>
  
  <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the  "License");
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