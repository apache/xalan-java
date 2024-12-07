<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://ns0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
  
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT stylesheet test, to test an xsl:sequence 
       instruction.
  -->
                  
  <xsl:output method="xml" indent="yes"/>                

  <xsl:template match="/">
     <output>
        <xsl:value-of select="count(fn0:func1())"/>
     </output>
  </xsl:template>
  
  <xsl:function name="fn0:func1" as="xs:integer*">
     <xsl:for-each select="1 to 10">
        <xsl:variable name="val" select="."/>
        <xsl:sequence select="$val"/>
     </xsl:for-each>
  </xsl:function>
  
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