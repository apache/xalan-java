<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test XPath 3.1 
       function fn:timezone-from-time. 
  -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="tz1" select="timezone-from-time(xs:time('13:20:00-05:00'))"/>
  <xsl:variable name="tz2" select="timezone-from-time(xs:time('13:20:00'))"/>
  
  <xsl:template match="/">
    <result>      
      <one isTimezoneVal="{$tz1 instance of xs:dayTimeDuration}">        
        <xsl:value-of select="$tz1"/>
      </one>
      <two isTimezoneVal="{$tz2 instance of xs:dayTimeDuration}" isEmtySeq="{$tz2 instance of empty-sequence()}">        
        <xsl:value-of select="$tz2"/>
      </two>
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