<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="array"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3.0 test case for the, XPath 3.1 function array:join.
         XPath expression examples within this test case, are borrowed
         from XPath 3.1 F&O spec.
    -->                              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
       <result>
         <xsl:variable name="arr1" select="array:join(())"/>
         <arr1 isArr="{$arr1 instance of array(*)}" size="{array:size($arr1)}"/>
         
         <xsl:variable name="arr2" select="[1, 2, 3]"/>
         <xsl:variable name="arr3" select="array:join($arr2)"/>
         <arr2 isArr="{$arr3 instance of array(*)}" size="{array:size($arr3)}"/>
         
         <xsl:variable name="arr4" select="['a', 'b']"/>
         <xsl:variable name="arr5" select="['c', 'd']"/>
         <xsl:variable name="seq1" select="($arr4, $arr5)"/>
         <xsl:variable name="arr6" select="array:join($seq1)"/>
         <arr3 isArr="{$arr6 instance of array(*)}" size="{array:size($arr6)}"/>
         
         <xsl:variable name="seq2" select="($arr4, $arr5, $arr1)"/>
         <xsl:variable name="arr7" select="array:join($seq2)"/>
         <arr4 isArr="{$arr7 instance of array(*)}" size="{array:size($arr7)}"/>
         
         <xsl:variable name="arr8" select="['e', 'f']"/>
         <xsl:variable name="seq3" select="($arr4, $arr5, $arr8)"/>
         <xsl:variable name="arr9" select="array:join($seq3)"/>
         <arr5 isArr="{$arr9 instance of array(*)}" size="{array:size($arr9)}"/>                                     
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
