<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
    
   <!-- An XSL stylesheet test case, to test XPath function 
        fn:format-integer. -->                 
				
   <xsl:output method="xml" indent="yes"/>				

   <xsl:template match="/">
      <result>
	     <one>
            <xsl:value-of select="format-integer(123, '0000')"/>
		 </one>
		 <two>
            <a><xsl:value-of select="format-integer(7, 'w')"/>, <xsl:value-of select="format-integer(55, 'w')"/></a>
			<b><xsl:value-of select="format-integer(7, 'W')"/>, <xsl:value-of select="format-integer(55, 'W')"/></b>
			<c><xsl:value-of select="format-integer(7, 'Ww')"/>, <xsl:value-of select="format-integer(55, 'Ww')"/></c>
		 </two>
		 <three>
            <a><xsl:value-of select="format-integer(105, 'w')"/>, <xsl:value-of select="format-integer(857, 'w')"/></a>
			<b><xsl:value-of select="format-integer(105, 'W')"/>, <xsl:value-of select="format-integer(857, 'W')"/></b>
			<c><xsl:value-of select="format-integer(105, 'Ww')"/>, <xsl:value-of select="format-integer(857, 'Ww')"/></c>
		 </three>
		 <four>
		    <a><xsl:value-of select="format-integer(2150, 'w')"/>, <xsl:value-of select="format-integer(8700, 'w')"/></a>
			<b><xsl:value-of select="format-integer(2150, 'W')"/>, <xsl:value-of select="format-integer(8700, 'W')"/></b>
			<c><xsl:value-of select="format-integer(2150, 'Ww')"/>, <xsl:value-of select="format-integer(8700, 'Ww')"/></c>
		 </four>
		 <five>
		    <a><xsl:value-of select="format-integer(10502, 'w')"/>, <xsl:value-of select="format-integer(10500, 'w')"/></a>
			<b><xsl:value-of select="format-integer(10502, 'W')"/>, <xsl:value-of select="format-integer(10500, 'W')"/></b>
			<c><xsl:value-of select="format-integer(10502, 'Ww')"/>, <xsl:value-of select="format-integer(10500, 'Ww')"/></c>			
		 </five>
		 <six>
		    <z><xsl:value-of select="format-integer(7, 'a')"/>, <xsl:value-of select="format-integer(7, 'A')"/></z>
			<list1>
			   <xsl:for-each select="1 to 15">
			      <value inp="{.}" result="{format-integer(., 'A')}"/>
			   </xsl:for-each>
			</list1>
			<list2>
			   <xsl:for-each select="50 to 55">
			      <value inp="{.}" result="{format-integer(., 'A')}"/>
			   </xsl:for-each>
			</list2>
		 </six>
		 <seven>
		    <xsl:value-of select="format-integer(57, 'i')"/>, <xsl:value-of select="format-integer(57, 'I')"/>
		 </seven>
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
