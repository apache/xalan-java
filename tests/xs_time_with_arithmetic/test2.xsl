<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test use of XML Schema 
       data type xs:time within XPath expressions.
  -->                
                
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">     
     <result>
        <one>
           <xsl:value-of select="xs:time('10:00:00') + xs:dayTimeDuration('PT5H')"/>
        </one>      
        <two>
           <xsl:value-of select="xs:time('10:00:00') + xs:dayTimeDuration('PT1H30M')"/>
        </two>
        <three>
           <xsl:value-of select="xs:time('10:00:00') + xs:dayTimeDuration('P1DT3H')"/>
        </three>
        <snip/>
        <four>
           <xsl:value-of select="xs:time('10:00:00') - xs:dayTimeDuration('PT5H')"/>
        </four>      
        <five>
           <xsl:value-of select="xs:time('10:00:00') - xs:dayTimeDuration('PT1H30M')"/>
        </five>
        <six>
           <xsl:value-of select="xs:time('10:00:00') - xs:dayTimeDuration('P1DT3H')"/>
        </six>
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