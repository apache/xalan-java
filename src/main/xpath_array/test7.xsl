<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
				exclude-result-prefixes="xs array"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT 3.0 test case to test, XPath 3.1 function array:subarray.
    -->                				

   <xsl:output method="xml" indent="yes"/>      

   <xsl:template match="/">        
      <result>   	     	     	     
	     <xsl:variable name="arr1" select="['a', 'b', 'c', 'd']" as="array(xs:string)"/>
         <xsl:variable name="resultArr1" select="array:subarray($arr1, 2)" as="array(xs:string)"/>
         <xsl:variable name="resultArr2" select="array:subarray($arr1, 2, 0)" as="array(xs:string)"/>
         <xsl:variable name="resultArr3" select="array:subarray($arr1, 2, 1)" as="array(xs:string)"/>
         <xsl:variable name="resultArr4" select="array:subarray($arr1, 2, 2)" as="array(xs:string)"/>
         <one arrSize="{array:size($resultArr1)}">
            <xsl:value-of select="$resultArr1"/>
         </one>
         <two arrSize="{array:size($resultArr2)}">
            <xsl:value-of select="$resultArr2"/>
         </two>
         <three arrSize="{array:size($resultArr3)}">
            <xsl:value-of select="$resultArr3"/>
         </three>
         <four arrSize="{array:size($resultArr4)}">
            <xsl:value-of select="$resultArr4"/>
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
