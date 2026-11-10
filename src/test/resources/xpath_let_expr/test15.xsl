<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                exclude-result-prefixes="fn"				
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
    
  <!-- An XSL stylesheet test case, to test XPath 3.1 expression 'let'
       where XPath let expression few of the variable names are 
       XML qualified names with non null namespace URIs. -->                 
   
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
    <result>
      <xsl:value-of select="let $fn:name1 := 5, $fn:name2 := 2, $y := 4 return ($fn:name1 + $fn:name2) - $y"/>
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