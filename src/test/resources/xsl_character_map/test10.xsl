<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">

    <!-- Author: mukulg@apache.org -->
  
    <!-- An XSL stylesheet test case to test, xsl:character-map 
         instruction.
       
         Within this stylesheet example, xsl:output element doesn't
         have 'use-character-maps' attribute which implies that,
         this stylesheet should not use xsl:character-map mappings.
         Therefore, any xsl:character-map elements within this stylesheet 
         will be ignored for processing.                     
    --> 
    
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:character-map name="cm1">
	   <xsl:output-character character="1" string="abc"/>   
	   <xsl:output-character character="2" string="def"/>
	   <xsl:output-character character="3" string='ghi'/>
	</xsl:character-map>
	
	<xsl:character-map name="cm2">
	   <xsl:output-character character="4" string="mno"/>   
	   <xsl:output-character character="5" string="pqr"/>
	</xsl:character-map>

	<xsl:template match="/">
	   <result>
		  <one>123abc</one>
	      <two att="123abc"/>
	      <three><xsl:value-of select="'123abc'"/></three>
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