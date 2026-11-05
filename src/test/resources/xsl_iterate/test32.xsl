<?xml version="1.0" encoding="UTF-8"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:f="http://example.com/functions"
                exclude-result-prefixes="#all"
                expand-text="yes" version="3.0">
                
  <!-- An XSL stylesheet contributed by Martin Honnen -->
  
  <!-- use with test32.xml -->	                
				
  <xsl:output method="xml" indent="yes"/>  
  
  <xsl:function name="f:iterative-fib" as="xs:integer">
    <xsl:param name="n" as="xs:integer"/>
    <xsl:iterate select="1 to $n">
      <xsl:param name="n" select="0"/>
      <xsl:param name="next" select="1"/>
      <xsl:on-completion select="$n"/>
      <xsl:next-iteration>
        <xsl:with-param name="n"
                        select="$next"/>
        <xsl:with-param name="next"
                        select="$n + $next"/>
      </xsl:next-iteration>
    </xsl:iterate>
  </xsl:function> 

  <xsl:template match="/" name="xsl:initial-template">
    <fib n="{/*}">{f:iterative-fib(/*)}</fib>
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