<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1.xml -->
  
  <!-- An XSL stylesheet test case, to test xsl:iterate instruction.
       This XSL stylesheet is an attempt to solve Xalan-J's implementation
       for W3C XSLT 3.0 test case iterate-018.
  -->					

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
     <result>
        <xsl:iterate select="document/*">
           <xsl:param name="countA" select="0"/>
           <xsl:param name="countOther" select="0"/>
           <xsl:on-completion>
              <count a="{$countA}" other="{$countOther}"/>
           </xsl:on-completion>
           <xsl:choose>
              <xsl:when test="self::a">
                <a/>
				<xsl:variable name="countAPrev" select="$countA"/>
                <xsl:next-iteration>
                  <xsl:with-param name="countA" select="$countAPrev + 1"/>
                </xsl:next-iteration>
              </xsl:when>
             <xsl:otherwise>
			   <xsl:variable name="countAPrev" select="$countA"/>
               <xsl:element name="{name()}">
                 <xsl:value-of select="$countAPrev + 10000"/>
               </xsl:element>
               <xsl:next-iteration>
                  <xsl:with-param name="countOther" select="$countOther + 1"/>
               </xsl:next-iteration>
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
