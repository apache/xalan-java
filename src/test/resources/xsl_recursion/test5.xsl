<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_a.xml -->
  
  <!-- An XSLT stylesheet test case, to test mutual recursion
       between two XPath 3.1 function item expressions.
       
       The mutual recursion algorithm of the two XPath function item
       expressions, as mentioned within this stylesheet, has been shared by 
       Dimitre Novatchev (ref, https://dnovatchev.wordpress.com/2023/10/04/mutual-recursion-with-anonymous-inline-functions-in-xpath-3/).
  -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="isEven" select="function($n as xs:integer) as xs:boolean { 
                                                          if ($n eq 0) then true() 
                                                                          else $isOdd($n - 1) }"/>
  
  <xsl:variable name="isOdd" select="function($n as xs:integer) as xs:boolean { 
                                                          if ($n eq 0) then false() 
                                                                          else $isEven($n - 1) }"/>
  
  <xsl:template match="/info">     
     <result>
       <one>
         <xsl:value-of select="$isEven(xs:integer(val))"/>
       </one>
       <two>
         <xsl:value-of select="$isOdd(xs:integer(val))"/>       
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