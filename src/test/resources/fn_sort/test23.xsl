<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                
				
   <!-- use with test23.xml -->
   
   <!-- An XSL 3 stylesheet test case, to test XPath 3.1 function fn:sort.
        For this test case, the sort key function definition modifies the
        standard meaning of each item that is part of an input sequence that
        is sorted (which for this test case, means that xs:gYearMonth and 
        xs:gYear values are made comparable).
   -->				
                
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/items">
      <items>
         <xsl:copy-of select="sort(item, (), function($item) { xs:integer(translate(xs:string($item/year), '-', '')) })"/>      
      </items>
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