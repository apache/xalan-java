<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_a.xml -->
  
  <!-- An XSLT stylesheet test case, to test mutual recursion
       between two XSLT stylesheet functions.
       
       This stylesheet also performs a loop, and does XSLT
       grouping of an XML intermediate fragment data set.
  -->                
                
  <xsl:output method="xml" indent="yes"/>
  
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
           <num val="{$num}" isEven="{fn0:isEven($num)}"/>
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
  
  <xsl:function name="fn0:isEven" as="xs:boolean">
    <xsl:param name="num" as="xs:integer"/>
    <xsl:sequence select="if ($num eq 0) then true() 
                                            else fn0:isOdd($num - 1)"/>
  </xsl:function>
    
  <xsl:function name="fn0:isOdd" as="xs:boolean">
    <xsl:param name="num" as="xs:integer"/>
    <xsl:sequence select="if ($num eq 0) then false() 
                                            else fn0:isEven($num - 1)"/>  
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