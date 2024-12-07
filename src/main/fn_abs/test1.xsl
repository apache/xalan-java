<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- test for the XPath 3.1 fn:abs() function -->

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/elem">
      <elem>
         <item><xsl:value-of select="abs(5)"/></item>
         <item><xsl:value-of select="abs(-5)"/></item>
         <item><xsl:value-of select="abs(-2.7)"/></item>
         <result1>
           <xsl:for-each select="a">
              <item><xsl:value-of select="abs(.)"/></item>
           </xsl:for-each>
         </result1>
         <result2>
	        <xsl:for-each select="a">
	           <item><xsl:value-of select="abs()"/></item>
	        </xsl:for-each>
         </result2>
         <result3>
            <xsl:for-each select="/elem/a[abs() &gt; 7]">
	           <item><xsl:value-of select="."/></item>
	        </xsl:for-each>
         </result3>
         <extra_elem1><xsl:value-of select="a[2] &lt; 0"/></extra_elem1>
         <extra_elem2><xsl:value-of select="a[2] &gt; 0"/></extra_elem2>
         <extra_elem3><xsl:value-of select="abs(a[2]) &gt; 7"/></extra_elem3>
         <extra_elem4><xsl:value-of select="abs(a[2]) &lt; 7"/></extra_elem4>
      </elem>
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