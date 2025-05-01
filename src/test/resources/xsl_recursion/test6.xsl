<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_a.xml -->
  
  <!-- An XSLT stylesheet test case, to test mutual recursion
       between two XPath 3.1 function item expressions.
       
       This stylesheet also performs a loop, and does XSLT
       grouping of an XML intermediate fragment data set.
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
       <xsl:call-template name="analyzeNumsAndMakeGroups">
         <xsl:with-param name="from" select="xs:integer(from)"/>
         <xsl:with-param name="to" select="xs:integer(to)"/>
       </xsl:call-template>
     </result>
  </xsl:template>

  <xsl:template name="analyzeNumsAndMakeGroups" as="element(nums)*">
     <xsl:param name="from" as="xs:integer"/>
     <xsl:param name="to" as="xs:integer"/>
     <xsl:variable name="numsResult">
        <xsl:for-each select="$from to $to">
           <xsl:variable name="num" select="."/>
           <num val="{$num}" isEven="{$isEven($num)}"/>
        </xsl:for-each>
     </xsl:variable>
     <xsl:for-each-group select="$numsResult/num" group-by="@isEven">
        <nums isEven="{current-grouping-key()}">
          <xsl:apply-templates select="current-group()"/>
        </nums>
     </xsl:for-each-group>
  </xsl:template>
  
  <xsl:template match="num">
    <num val="{@val}"/>
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