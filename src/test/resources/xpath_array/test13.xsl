<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="array"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3.0 test case for the, XPath 3.1 arrays testing 
         following aspects:
         1) 'Non empty array' constructed via curly array constructor syntax.
         2) 'Empty array' constructed via square array constructor syntax.
         3) 'Empty array' constructed via curly array constructor syntax.
    -->                              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <result>
         <xsl:variable name="arr1" select="array {1, 2, 5, 7}"/>
         <xsl:variable name="arr2" select="[]"/>
         <xsl:variable name="arr3" select="array {}"/>
         <arr1 size="{array:size($arr1)}">
           <xsl:copy-of select="$arr1"/>
         </arr1>
         <arr2 size="{array:size($arr2)}">
           <xsl:copy-of select="$arr2"/>
         </arr2>
         <arr3 size="{array:size($arr3)}">
           <xsl:copy-of select="$arr3"/>
         </arr3>
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
