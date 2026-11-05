<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:f="http://example.com/functions"
                exclude-result-prefixes="#all"
				expand-text="yes" version="3.0">
				
  <!-- An XSL stylesheet contributed by Martin Honnen -->
  
  <!-- use with test18.xml -->				

  <xsl:output method="xml" indent="yes"/>

  <xsl:function name="f:fib" as="xs:integer" cache="yes">
    <xsl:param name="num" as="xs:integer"/>
    <xsl:sequence
      select="if ($num = 0)
                then 0
                else if ($num = 1)
                then 1
                else f:fib($num - 2) + f:fib($num - 1)"/>
  </xsl:function>
  
  <xsl:template match="/" name="xsl:initial-template">
    <fib n="{/*}">{f:fib(/*)}</fib>    
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