<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                
   
   <!-- use with test1_b.xml -->

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/elem">
      <elem>
        <xsl:variable name="tokens">
	   <xsl:for-each select="tokenize(str2, '\s+')">
	     <item><xsl:value-of select="."/></item>
	   </xsl:for-each>              
        </xsl:variable>
        <!-- emitting specific items from resulting sequence (please 
             see the previous xsl:variable evaluation), using following 
             xslt template instructions. -->
        <result1>
           <xsl:copy-of select="$tokens/item[position() &lt; 5]"/>
        </result1>
        <result2>
	   <xsl:copy-of select="$tokens/item[2] | $tokens/item[3]"/>
        </result2>
        <result3>
           <xsl:copy-of select="$tokens/item[(position() != 1) and 
                                                 (position() != last())]"/>
        </result3>
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