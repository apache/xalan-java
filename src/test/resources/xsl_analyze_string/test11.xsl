<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_g.xml -->
   
   <!-- using the xsl:analyze-string instruction, to process xml attribute 
        values from a sequence of xml elements. post processing the result 
        of xsl:analyze-string instruction by doing xsl grouping on this 
        resulting data. -->

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <elem>
         <xsl:variable name="tempResult">
            <xsl:apply-templates select="elem/item"/>
         </xsl:variable>
         <xsl:for-each-group select="$tempResult/item" group-by="count(token)">
            <group grpSize="{current-grouping-key()}">
               <xsl:for-each select="current-group()">
                 <grpMember>
                    <xsl:copy-of select="*"/>
                 </grpMember>
               </xsl:for-each>
            </group>
         </xsl:for-each-group>
      </elem>
   </xsl:template>
   
   <xsl:template match="item">
      <item>
         <xsl:analyze-string select="@attr1" regex="(\s)+">
	    <xsl:non-matching-substring>
	      <token><xsl:value-of select="."/></token>
	    </xsl:non-matching-substring>
         </xsl:analyze-string>
      </item>
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