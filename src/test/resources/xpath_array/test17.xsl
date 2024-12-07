<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="xs array"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3.0 test case for the, XPath 3.1 function array:for-each-pair.
         XPath expression examples within this test case, have been borrowed from 
         XPath 3.1 F&O spec.
    -->                             

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <xsl:variable name="arr1" select="['A', 'B', 'C']" as="array(xs:string)"/>
       <xsl:variable name="arr2" select="[1, 2, 3]" as="array(xs:integer)"/>
       <xsl:variable name="func1" select="function($x, $y) { [$x, $y] }"/>
       <xsl:variable name="resultArr1" select="array:for-each-pair($arr1, $arr2, $func1)" as="array(*)"/>       
       <result>
          <one resultArrSize="{array:size($resultArr1)}">
            <xsl:for-each select="$resultArr1">
              <item isArray="{. instance of array(*)}"><xsl:value-of select="."/></item>
            </xsl:for-each>
          </one>
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
