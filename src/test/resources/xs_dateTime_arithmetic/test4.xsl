<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_a.xml -->
  
  <!-- This XSLT stylesheet, tests xs:dateTime data type's
       arithmetic, and illustrates stylesheet codebase
       modularity via xsl:function instruction.  
  -->                
                
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/info">     
    <result>
      <one>
         <xsl:value-of select="fn0:subtractMonthsFromDateTime(xs:dateTime(dateTime1), month1)"/>
      </one>
      <two>
         <!-- Add 15 months of duration value, to result of a function call. -->
         <xsl:value-of select="fn0:subtractMonthsFromDateTime(xs:dateTime(dateTime1), month1) + 
                                                                                           xs:yearMonthDuration('P15M')"/>
      </two>
    </result>
  </xsl:template>
  
  <!-- Subtract integer number of months from an xs:dateTime value. -->
  <xsl:function name="fn0:subtractMonthsFromDateTime" as="xs:dateTime">
     <xsl:param name="xsDateTime" as="xs:dateTime"/>
     <xsl:param name="noOfMonths" as="xs:integer"/>
    
     <xsl:variable name="yearMonthDurationStr" select="'P' || string($noOfMonths) || 'M'"/>
     <xsl:sequence select="$xsDateTime - xs:yearMonthDuration($yearMonthDurationStr)"/>    
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