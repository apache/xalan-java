<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- use with test1_d.xml -->
  
  <!-- An XSLT stylesheet test case, to test XPath 3.1 fn:filter  
       function.
  -->               
                
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/info">    
    <result>
      <one>
         <xsl:copy-of select="filter(val, function($elem) { xs:double($elem/text()) lt 6 })"/>
      </one>
      <two>
         <xsl:copy-of select="filter(val, function($elem) { xs:double($elem/text()) gt 6 })"/>
      </two>
      <three>
         <xsl:copy-of select="filter(val, function($elem) { xs:double($elem/text()) eq 5.1 })"/>
      </three>
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