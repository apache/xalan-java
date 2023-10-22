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
          <xsl:value-of select="fn0:addMonthsToDateTime(xs:dateTime(dateTime1), month1)"/>
       </one>
       <two>
          <xsl:value-of select="fn0:addYearsToDateTime(xs:dateTime(dateTime1), year2)"/>
       </two>
       <three>
          <xsl:value-of select="fn0:addYearsAndMonthsToDateTime(xs:dateTime(dateTime1), year1, month1)"/>
       </three>
    </result>
  </xsl:template>
  
  <!-- Add integer number of months to an xs:dateTime value. -->
  <xsl:function name="fn0:addMonthsToDateTime" as="xs:dateTime">
     <xsl:param name="xsDateTime" as="xs:dateTime"/>
     <xsl:param name="noOfMonths" as="xs:integer"/>
    
     <xsl:variable name="yearMonthDurationStr" select="'P' || string($noOfMonths) || 'M'"/>
     <xsl:sequence select="$xsDateTime + xs:yearMonthDuration($yearMonthDurationStr)"/>    
  </xsl:function>
  
  <!-- Add integer number of years to an xs:dateTime value. -->
  <xsl:function name="fn0:addYearsToDateTime" as="xs:dateTime">
     <xsl:param name="xsDateTime" as="xs:dateTime"/>
     <xsl:param name="noOfYears" as="xs:integer"/>
      
     <xsl:variable name="yearMonthDurationStr" select="'P' || string($noOfYears) || 'Y'"/>
     <xsl:sequence select="$xsDateTime + xs:yearMonthDuration($yearMonthDurationStr)"/>    
  </xsl:function>
  
  <!-- Add xs:yearMonthDuration value to an xs:dateTime value. This function
       first constructs an xs:yearMonthDuration value from the years and months
       argument values passed as integers to this function, which is 
       subsequently added to an xs:dateTime value. 
  -->
  <xsl:function name="fn0:addYearsAndMonthsToDateTime" as="xs:dateTime">
     <xsl:param name="xsDateTime" as="xs:dateTime"/>
     <xsl:param name="noOfYears" as="xs:integer"/>
     <xsl:param name="noOfMonths" as="xs:integer"/>
        
     <xsl:variable name="yearMonthDurationStr" select="'P' || string($noOfYears) 
                                                                 || 'Y' || string($noOfMonths) 
                                                                              || 'M'"/>
     <xsl:sequence select="$xsDateTime + xs:yearMonthDuration($yearMonthDurationStr)"/>    
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