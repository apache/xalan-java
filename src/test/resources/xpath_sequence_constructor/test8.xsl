<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet, to test XPath 3.1 sequences and
        use of predicate/index operator (i.e, [..]) on them). 
        -->                

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/">
      <result>         
         <xsl:variable name="seq" select="(1.1, 2.1, 3.1, 4.1, 5.1)"/>         
         <xsl:variable name="val1" select="$seq[2]"/>
         
         <val><xsl:value-of select="($seq[2], $seq[3])"/></val>
         <val><xsl:value-of select="$val1"/></val>                  
         <val><xsl:value-of select="xs:double(103) + $val1"/></val>
         <val><xsl:value-of select="$val1 * 11.5"/></val>
         <val><xsl:value-of select="$val1 + xs:double(103)"/></val>
         <val><xsl:value-of select="xs:double(103) - $val1"/></val>
         <val><xsl:value-of select="xs:double(103) * $val1"/></val>
         <val><xsl:value-of select="xs:double(103) div $val1"/></val>         
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