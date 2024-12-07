<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_a.xml -->
  
  <!-- This XSLT stylesheet tests, xs:dateTime data type's constructor
       function, and XPath's 'lt' and 'gt' operators on xs:dateTime values. 
  -->                 
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/info">     
     <result>
        <one>
           <xsl:value-of select="xs:dateTime(dateTime[1]) lt current-dateTime()"/>
        </one>
        <two>
	       <xsl:value-of select="xs:dateTime(dateTime[1]) gt current-dateTime()"/>
        </two>
        <three>
           <xsl:value-of select="xs:dateTime(dateTime[2]) lt current-dateTime()"/>
        </three>
        <four>
	       <xsl:value-of select="xs:dateTime(dateTime[2]) gt current-dateTime()"/>
        </four>
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