<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_c.xml -->
   
   <!-- An XSLT stylesheet test case, to test XPath 3.1 xs:date(..)
        constructor function together with value comparison operators 
        'ge' and 'le'.
        
        This stylesheet, reads input data to be transformed, from an XML 
        external source document.
   -->                             

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="dateFrom" select="xs:date('2022-01-01')"/>
   
   <xsl:variable name="dateTo" select="xs:date('2023-12-31')"/>
   
   <xsl:template match="/parts">
     <parts>
        <xsl:copy-of select="part[(xs:date(manufactureDate) ge $dateFrom) and (xs:date(manufactureDate) le $dateTo)]"/>
     </parts>
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