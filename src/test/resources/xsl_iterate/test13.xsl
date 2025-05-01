<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- use with test1_c.xml -->
   
  <!-- This XSLT stylesheet, uses xsl:iterate to compute cumulative 
       totals.
       
       This XSLT stylesheet is borrowed from XSLT 3.0 spec, with 
       slight modifications. -->                 
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
     <result>
        <xsl:iterate select="transactions/transaction">
           <xsl:param name="balance" select="0.00"/>
           <xsl:param name="prevDate"/>
           <xsl:variable name="newBalance" select="$balance + xs:decimal(@value)"/>
           <xsl:variable name="thisDate" select="xs:date(@date)"/>
           <xsl:choose>
              <xsl:when test="(position() eq 1) or ($thisDate eq $prevDate)">
                 <balance date="{$thisDate}" value="{format-number(number(xs:string($newBalance)), '0.00')}"/>
                 <xsl:next-iteration>
                    <xsl:with-param name="balance" select="$newBalance"/>
                    <xsl:with-param name="prevDate" select="$thisDate"/>
                 </xsl:next-iteration>
              </xsl:when>
              <xsl:otherwise>
                 <xsl:break/>
              </xsl:otherwise>
           </xsl:choose>
        </xsl:iterate>
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
