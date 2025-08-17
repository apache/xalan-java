<?xml version="1.0" ?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
                  
  <!-- An XSL stylesheet test case, to test xsl:for-each's xsl:sort 
       instruction with two levels of sort keys. -->                
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="var1" as="element()*">
     <name>
	    <fName>Gary</fName>
		<lName>Gregory</lName>
	 </name>
	 <name>
	    <fName>Joseph</fName>
		<lName>Kesselman</lName>
	 </name>
	 <name>
	    <fName>Mukul</fName>
		<lName>Gandhj</lName>
	 </name>
	 <name>
	    <fName>Mukul</fName>
		<lName>Gandhi</lName>
	 </name>
	 <name>
	    <fName>Mukul</fName>
		<lName>Gandhk</lName>
	 </name>
  </xsl:variable>

  <xsl:template match="/">
     <result>
	     <names>
		    <xsl:for-each select="$var1">
			   <xsl:sort select="fName"/>
			   <xsl:sort select="lName"/>
			   <xsl:copy-of select="."/>
			</xsl:for-each>
	     </names>
         <names>
		    <xsl:for-each select="($var1[1], $var1[2], $var1[3], $var1[4], $var1[5])">
			   <xsl:sort select="fName"/>
			   <xsl:sort select="lName"/>
			   <xsl:copy-of select="."/>
			</xsl:for-each>
	     </names>
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
