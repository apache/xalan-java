<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSLT 3.0 test case, to test XPath 3.1 operator "=>" 
         preceded by literal sequence. -->                                           

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
      <result>
	     <one>
           <xsl:value-of select="(1 to 5) => sum()"/>
		 </one>
		 <two>
		   <xsl:value-of select="(1 to 5) => for-each(function($x) { $x * $x })"/>
		 </two>
		 <three>
		   <xsl:variable name="startVal" select="1"/>
           <sum><xsl:value-of select="($startVal to 5) => sum()"/></sum>
		   <avg><xsl:value-of select="($startVal to 5) => avg()"/></avg>
		 </three>
		 <four>
		   <xsl:variable name="startVal" select="1"/>
		   <xsl:variable name="endVal" select="5"/>
           <sum><xsl:value-of select="($startVal to $endVal) => sum()"/></sum>
		   <avg><xsl:value-of select="($startVal to $endVal) => avg()"/></avg>
		 </four>
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
