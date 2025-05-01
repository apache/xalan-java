<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- use with test1_d.xml -->
   
  <!-- An XSLT stylesheet test case, that uses xsl:iterate and xsl:next-iteration. 
       The xsl:next-iteration instruction is specified in a tail position (as per 
       definition of, when an XSLT instruction is in a sequence constructor's tail 
       position) of sequence constructor that is a child of xsl:iterate.
       
       Using xsl:iterate, this stylesheet reads a sequence of 'a' elements, and breaks
       from xsl:iterate when value of 'sequence' attribute changes for the first time.
       This stylesheet also, maintains and outputs cumulative total of attribute 
       'value'. -->                 
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/elem">
     <result>
        <xsl:iterate select="a">
	       <xsl:param name="total" select="0.00"/>
	       <xsl:param name="prevSequence" select="0"/>	   
	       <xsl:variable name="newTotal" select="$total + @value"/>
	       <xsl:variable name="thisSequence" select="@sequence"/>	   
	       <xsl:choose>
	          <xsl:when test="(position() = 1) or ($thisSequence = $prevSequence)">
	             <total sequence="{$thisSequence}"
	                    value="{format-number($newTotal, '0.00')}"/>
	             <xsl:next-iteration>
	                <xsl:with-param name="total" select="$newTotal"/>
	                <xsl:with-param name="prevSequence" select="$thisSequence"/>
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
