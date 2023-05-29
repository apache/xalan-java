<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- tokenizing a string, and then forming groups from resulting tokens.
        with this example, all numeric even valued tokens form one group, 
        and all numeric odd valued tokens form a different group. the  
        groups formed are, subsequently sorted (the sort key within this 
        example to re-order the groups, is the grouping key). -->

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="str" select="'1, 2, 3, 4, 5, 6, 7, 8, 9, 10'"/>

   <xsl:template match="/">
      <result>
        <xsl:variable name="tokens">
           <xsl:for-each select="tokenize($str, ',\s*')">
              <value><xsl:value-of select="."/></value>
           </xsl:for-each>
        </xsl:variable>
        <xsl:for-each-group select="$tokens/value" group-by="(number(.) mod 2) = 0">
          <xsl:sort select="current-grouping-key()" data-type="number" order="descending"/>
          <xsl:variable name="isEven">
             <xsl:choose>
                <xsl:when test="current-grouping-key() = true()">
                   yes
                </xsl:when>
                <xsl:otherwise>
                   no
                </xsl:otherwise>
             </xsl:choose>
          </xsl:variable>
          <numbers even="{normalize-space($isEven)}">
             <xsl:copy-of select="current-group()"/>
          </numbers>
        </xsl:for-each-group>
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