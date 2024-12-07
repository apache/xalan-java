<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_e.xml -->
  
  <!-- An XSLT stylesheet test case, to test XPath
       date arithmetic.
  -->                
                
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/info">
     <xsl:variable name="var1" select="str" as="xs:date"/>
     <result dt="{$var1}">               
       <xsl:for-each select="1 to 5">
         <xsl:variable name="num" select="(. - 1) * 2"/>
         <xsl:copy-of select="fn0:procDate($var1, xs:integer($num))"/>
       </xsl:for-each>
     </result>
  </xsl:template>
  
  <xsl:function name="fn0:procDate" as="element(val)">
     <xsl:param name="dt" as="xs:date"/>
     <xsl:param name="shiftVal" as="xs:integer"/>
     <val>
       <one>
         <xsl:value-of select="$dt + xs:dayTimeDuration('P' || $shiftVal || 'D')"/>
       </one>
       <two>
         <xsl:value-of select="$dt + xs:yearMonthDuration('P' || $shiftVal || 'Y')"/>
       </two>
     </val>
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