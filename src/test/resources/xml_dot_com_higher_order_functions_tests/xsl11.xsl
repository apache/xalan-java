<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                

    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="binaryOp" select="function($f1 as function(*),
                                                   $a as xs:double,
                                                   $b as xs:double) as xs:double { $f1($a, $b) }"/>

    <xsl:variable name="add" select="function($a as xs:double, $b as xs:double) as xs:double { $a + $b }"/>

    <xsl:variable name="subtract" select="function($a as xs:double, $b as xs:double) as xs:double { $a - $b }"/>

    <xsl:variable name="multiply" select="function($a as xs:double, $b as xs:double) as xs:double { $a * $b }"/>

    <xsl:variable name="divide" select="function($a as xs:double, $b as xs:double) as xs:double { $a div $b }"/>

    <xsl:template match="/">
      <result>
        <one>
          <xsl:value-of select="$binaryOp($add, 7, 2)"/>
        </one>
        <two>
          <xsl:value-of select="$binaryOp($subtract, 7, 2)"/>
        </two>
        <three>
          <xsl:value-of select="$binaryOp($multiply, 7, 2)"/>
        </three>
        <four>
          <xsl:value-of select="$binaryOp($divide, 7, 2)"/>
        </four>
      </result>
   </xsl:template>
   
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