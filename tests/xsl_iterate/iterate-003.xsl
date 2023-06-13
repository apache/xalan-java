<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
  <!-- use with iterate001.xml -->
   
  <!-- An XSLT stylesheet test case, that uses xsl:iterate 
       along with xsl:param and xsl:next-iteration. This
       XSLT stylesheet uses xsl:break instruction to exit
       from xsl:iterate loop early.  
       
       This XSLT stylesheet has been borrowed from W3C XSLT 
       3.0 test suite and makes few changes to XSLT stylesheet 
       syntax. -->
        
  <xsl:output method="xml" indent="yes"/>                

  <xsl:template match="/">
    <out>
      <xsl:iterate select="//ITEM">
        <xsl:param name="basketCost" select="0"/>
        <xsl:choose>
          <xsl:when test="$basketCost &gt; 12.00">
            <xsl:break/>
          </xsl:when>
          <xsl:otherwise>
            <item cost="{format-number($basketCost, '00.00')}">
              <xsl:copy-of select="TITLE"/>
            </item>            
          </xsl:otherwise>
        </xsl:choose>
        <xsl:next-iteration>
	   <xsl:with-param name="basketCost" select="$basketCost + PRICE"/>
        </xsl:next-iteration>
      </xsl:iterate>
    </out>
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
