<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_g.xml -->
   
   <!-- An XSLT stylesheet to test, XPath 3.1 fn:sort function to sort sequence
        of XML elements (read from an, XML input document), both in ascending
        and descending orders. Each XML node input item, represents an XML person
        element (with sub-element fName as sort primary key, and sub-element lName
        as sort secondary key). -->                            

   <xsl:output method="xml" indent="yes"/>      
   
   <xsl:template match="/document">
     <result>
        <sorted1 sortOrder="ascending"><xsl:copy-of select="sort(person, (), function($person) { string($person/fName) || 
                                                                                                        ':' || string($person/lName)  })"/></sorted1>
       
        <sorted2 sortOrder="descending"><xsl:copy-of select="reverse(sort(person, (), function($person) { string($person/fName) || 
                                                                                                        ':' || string($person/lName)  }))"/></sorted2>
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