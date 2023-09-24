<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_d.xml -->
  
  <!-- An XSLT stylesheet test case, to test XPath '+' and
       '-' operations on xs:date values.
  -->                
                
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/info">
     <result>        
        <xsl:variable name="var1" as="xs:date">
     	   <xsl:sequence select="str"/>
        </xsl:variable>
        <xsl:variable name="var2" as="xs:date">
           <xsl:sequence select="xs:date('2003-10-03')"/>
        </xsl:variable>
        <xsl:variable name="var3" as="xs:dayTimeDuration">
           <xsl:sequence select="xs:dayTimeDuration('P5D')"/>
        </xsl:variable>
        <one>
           <xsl:value-of select="$var1"/>
        </one>
        <two>
           <xsl:value-of select="$var2"/>
        </two>
        <three>
           <xsl:value-of select="$var3"/>
        </three>
        <snip/>
        <plus value="{$var1 + $var3}"/>
        <plus value="{$var1 + xs:dayTimeDuration('P5D')}"/>
        <plus value="{$var1 + xs:dayTimeDuration('PT72H')}"/>
        <plus value="{$var1 + xs:yearMonthDuration('P5Y7M')}"/>
        <snip/>
        <diff value="{$var1 - $var2}"/>        
        <diff value="{$var1 - xs:dayTimeDuration('P1D')}"/>
        <diff value="{$var1 - xs:dayTimeDuration('PT72H')}"/>
        <diff value="{$var1 - xs:yearMonthDuration('P2Y3M')}"/>        
     </result>
  </xsl:template>
  
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