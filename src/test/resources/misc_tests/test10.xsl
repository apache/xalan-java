<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test assigning an XPath named 
         function reference to a stylesheet parameter or variable and 
         checking the match of parameter or variable's value with the 
         specified XPath function test sequence type. -->                
 
    <xsl:output method="xml" indent="yes"/>
 
    <xsl:param name="absf1" select="abs#1" as="function(*)"/>
 
    <xsl:template name="main">
       <result>      
          <a><xsl:value-of select="$absf1(-2)"/></a>
		  <xsl:variable name="absf2" select="abs#1" as="function(*)"/>
		  <b><xsl:value-of select="$absf2(-3)"/></b>
		  <c><xsl:value-of select="$absf1(-2) * $absf2(-3)"/></c>
		  <d><xsl:value-of select="1 + $absf1(-2) * $absf2(-3)"/></d>
		  <e><xsl:value-of select="1 + ($absf1(-2) * $absf2(-3))"/></e>
		  <f><xsl:value-of select="(1 + $absf1(-2)) * $absf2(-3)"/></f>
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
