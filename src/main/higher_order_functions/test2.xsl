<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs math fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_a.xml -->
  
  <!-- An XSLT 3.0 test case, where we pass a function item as
       an argument to a stylesheet function xsl:function. 
  -->               
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/info">     
     <result>
       <one>
         <xsl:value-of select="fn0:eval(function($a, $b) { $a + $b }, a, b)"/>
       </one>
       <two>
         <xsl:value-of select="fn0:eval(function($a, $b) { $a * $b }, c, d)"/>     
       </two>
       <three>
         <xsl:value-of select="fn0:eval(function($a, $b) { math:pow($a, $b) }, c, d)"/>     
       </three>
     </result>
  </xsl:template>
  
  <!-- A stylesheet function, that takes a function item as an argument,
       and other arguments as well that are further used as arguments
       within a dynamic function call. The dynamic function call, is a
       call to a function that was passed as a function item argument
       to this xsl:function function. 
  -->
  <xsl:function name="fn0:eval" as="xs:double">
    <xsl:param name="f1"/>    
    <xsl:param name="a" as="xs:double"/>
    <xsl:param name="b" as="xs:double"/>
    
    <xsl:sequence select="$f1($a, $b)"/>
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