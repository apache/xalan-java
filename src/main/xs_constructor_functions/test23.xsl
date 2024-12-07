<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test case, to test XPath data types 
       xs:normalizedString, xs:token.  
  -->                
                
  <xsl:output method="xml" indent="yes"/>                
  
  <xsl:template match="/">
     <xsl:variable name="normStr1" select="xs:normalizedString('abcpqr')"/>
     <xsl:variable name="token1" select="xs:token('abcpqr')"/>
     <result>
        <one>
           <xsl:value-of select="$normStr1 instance of xs:normalizedString"/>
        </one>
        <two>
           <xsl:value-of select="$token1 instance of xs:token"/>
        </two>
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