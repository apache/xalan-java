<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns0="http://example.com"
                exclude-result-prefixes="ns0"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test7.xml -->
    
    <!-- An XSLT test case to test, XPath 3.1 functions fn:local-name, 
         fn:normalize-space.
    -->              

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/ns0:root">
       <result>
         <one>
            <xsl:value-of select="local-name()"/>
         </one>
         <two>
            <xsl:value-of select="local-name(ns0:abc)"/>
         </two>
         <three>
            <xsl:value-of select="normalize-space(/ns0:root)"/>
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
