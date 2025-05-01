<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="xs array"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT 3.0 test case to test, XPath 3.1 function 
         array:tail.
    -->                              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <result>
         <xsl:variable name="arr1" select="[5, 6, 7, 8]" as="array(xs:integer)"/>         
         <xsl:variable name="arr2" select="[5]" as="array(xs:integer)"/>
                           
         <xsl:variable name="afterArrTail1" select="array:tail($arr1)"/>
         <xsl:variable name="afterArrTail2" select="array:tail($arr2)"/>
          
         <one resultArrSize="{array:size($afterArrTail1)}">
           <xsl:value-of select="$afterArrTail1"/>
         </one>
         <two resultArrSize="{array:size($afterArrTail2)}">
           <xsl:value-of select="$afterArrTail2"/>
         </two>
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
