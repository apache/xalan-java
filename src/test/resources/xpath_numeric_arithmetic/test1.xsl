<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT test case, to test an XPath operator 'div' applied 
       to atomic types. -->                 

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
      <result>
         <xsl:variable name="resultVal" select="xs:integer(12345678901) div xs:integer(5)" as="xs:decimal"/>
		 <one>
		   <xsl:value-of select="$resultVal"/>
		 </one>
         <xsl:variable name="resultVal" select="xs:integer(22) div xs:integer(7)" as="xs:decimal"/>
		 <two>
		   <xsl:value-of select="$resultVal"/>
		 </two>
		 <xsl:variable name="resultVal" select="xs:byte(22) div xs:byte(7)" as="xs:decimal"/>
		 <three>
		   <xsl:value-of select="$resultVal"/>
		 </three>
		 <xsl:variable name="resultVal" select="xs:unsignedByte(22) div xs:unsignedByte(7)" as="xs:decimal"/>
		 <four>
		   <xsl:value-of select="$resultVal"/>
		 </four>
		 <xsl:variable name="resultVal" select="-3 div 2"/>
		 <five isDecimal="{$resultVal instance of xs:decimal}">
		   <xsl:value-of select="$resultVal"/>
		 </five>
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