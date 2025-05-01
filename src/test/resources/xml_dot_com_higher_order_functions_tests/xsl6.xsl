<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn1="http://fn1"
                exclude-result-prefixes="xs fn1"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                

    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="degreeCelsiusTemps" select="(5, 10.2, 15, 20.7, 25)" as="xs:double*"/>

    <xsl:template match="/">
      <result>
        <xsl:for-each select="$degreeCelsiusTemps">
          <xsl:variable name="degreesTemp" select="."/>
          <temperature degrees="{$degreesTemp}">
            <xsl:value-of select="fn1:degreeCelsiusToFahrenheit($degreesTemp)"/>
          </temperature>
        </xsl:for-each>
      </result>
    </xsl:template>
    
    <!-- An XSL stylesheet function, that converts a degrees celsius temperature
         value to fahrenheit temperature value, and rounds the result to 
         two decimal places.
    -->
    <xsl:function name="fn1:degreeCelsiusToFahrenheit" as="xs:double">
       <xsl:param name="c1" as="xs:double"/>
       <xsl:sequence select="round(($c1 * (9 div 5)) + 32, 2)"/>
    </xsl:function>
    
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