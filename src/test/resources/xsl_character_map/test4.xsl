<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                				
			    version="3.0">	
  
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case to test, xsl:character-map 
       instruction. -->	
       
  <xsl:output method="xml" use-character-maps="cm1 cm2" indent="yes"/>
  
  <xsl:character-map name="cm1">
     <xsl:output-character character="x" string="hello1"/>
	 <xsl:output-character character="y" string="hello2"/>
  </xsl:character-map>
  
  <xsl:character-map name="cm2">
     <xsl:output-character character="p" string="PPP"/>
	 <xsl:output-character character="q" string="QQQ"/>
	 <xsl:output-character character="r" string="RRR"/>
  </xsl:character-map>
  
  <xsl:template match="/">
	 <result>
	    <one>
		   <xsl:text>thanks x foR Reading y. p and q and r.</xsl:text>
		</one>
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
