<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- This XSLT stylesheet, tests xs:dateTime data type's
       arithmetic, and illustrates stylesheet codebase
       modularity via xsl:function instruction.  
  -->                 
                
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">     
    <result>
      <one>
         <xsl:value-of select="fn0:addDaysToDateTime(xs:dateTime('2007-10-05T10:00:00'), 10)"/>
      </one>
      <two>
         <xsl:value-of select="fn0:addHoursToDateTime(xs:dateTime('2007-10-05T10:00:00'), 5)"/>
      </two>
      <three>
         <xsl:value-of select="fn0:addHoursAndMinsToDateTime(xs:dateTime('2007-10-05T10:00:00'), 5, 15)"/>
      </three>
    </result>
  </xsl:template>
  
  <!-- Add integer number of days to an xs:dateTime value. -->
  <xsl:function name="fn0:addDaysToDateTime" as="xs:dateTime">
     <xsl:param name="xsDateTime" as="xs:dateTime"/>
     <xsl:param name="noOfDays" as="xs:integer"/>
    
     <xsl:variable name="dayTimeDurationStr" select="'P' || string($noOfDays) || 'D'"/>
     <xsl:sequence select="$xsDateTime + xs:dayTimeDuration($dayTimeDurationStr)"/>    
  </xsl:function>
  
  <!-- Add integer number of hours to an xs:dateTime value. -->
  <xsl:function name="fn0:addHoursToDateTime" as="xs:dateTime">
     <xsl:param name="xsDateTime" as="xs:dateTime"/>
     <xsl:param name="noOfHours" as="xs:integer"/>
      
     <xsl:variable name="dayTimeDurationStr" select="'P' || 'T' || string($noOfHours) || 'H'"/>
     <xsl:sequence select="$xsDateTime + xs:dayTimeDuration($dayTimeDurationStr)"/>    
  </xsl:function>
  
  <!-- Add xs:dayTimeDuration value to an xs:dateTime value. This function
       first constructs an xs:dayTimeDuration value from the hours and minutes
       argument values passed as integers to this function, which is 
       subsequently added to an xs:dateTime value. 
  -->
  <xsl:function name="fn0:addHoursAndMinsToDateTime" as="xs:dateTime">
     <xsl:param name="xsDateTime" as="xs:dateTime"/>
     <xsl:param name="noOfHours" as="xs:integer"/>
     <xsl:param name="noOfMins" as="xs:integer"/>
        
     <xsl:variable name="dayTimeDurationStr" select="'P' || 'T' || string($noOfHours) 
                                                                       || 'H' || string($noOfMins) 
                                                                                     || 'M'"/>
     <xsl:sequence select="$xsDateTime + xs:dayTimeDuration($dayTimeDurationStr)"/>    
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