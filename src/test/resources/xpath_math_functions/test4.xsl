<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="math"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- This XSL stylesheet test, has various XPath 3.1 expressions involving
        functions within the XML namespace http://www.w3.org/2005/xpath-functions/math.
        
        This XSL stylesheet uses as input, information from an external XML document.
   -->                 

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/list">
      <result>
         <xsl:for-each select="a">
            <exp inp="{if (@val) then @val else .}">
               <xsl:choose>
                  <xsl:when test="@val">
                     <xsl:value-of select="round(math:exp(@val), 2)"/>   
                  </xsl:when>
                  <xsl:otherwise>
                     <xsl:value-of select="round(math:exp(.), 2)"/>
                  </xsl:otherwise>
               </xsl:choose>
            </exp>
            <sin inp="{if (@val) then @val else .}">
	           <xsl:choose>
	              <xsl:when test="@val">
	                 <xsl:value-of select="round(math:sin(@val), 2)"/>   
	              </xsl:when>
	              <xsl:otherwise>
	                 <xsl:value-of select="round(math:sin(.), 2)"/>
	              </xsl:otherwise>
	           </xsl:choose>
            </sin>
            <atan inp="{if (@val) then @val else .}">
	           <xsl:choose>
	    	      <xsl:when test="@val">
	    	         <xsl:value-of select="round(math:atan(@val), 2)"/>   
	    	      </xsl:when>
	    	      <xsl:otherwise>
	    	         <xsl:value-of select="round(math:atan(.), 2)"/>
	    	      </xsl:otherwise>
	           </xsl:choose>
            </atan>
         </xsl:for-each>
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