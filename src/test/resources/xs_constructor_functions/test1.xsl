<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- This XSLT stylesheet, tests various XPath 3.1 constructor functions
        and use of logical and div operations on such values. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
      <xsl:variable name="decimal1" select="xs:decimal(12.23)"/>
      <xsl:variable name="decimal2" select="xs:decimal(12.2)"/>
      <result>
         <one><xsl:value-of select="xs:decimal(12.23)"/></one>
         <two><xsl:value-of select="xs:decimal('12.23')"/></two>
         <three><xsl:value-of select="xs:decimal(12.23) = xs:decimal(12.23)"/></three>
         <four><xsl:value-of select="xs:decimal(12.23) = xs:decimal('12.23')"/></four>
         <five><xsl:value-of select="xs:decimal(12.23) = xs:decimal(12.2)"/></five>
         <six><xsl:value-of select="$decimal1"/></six>
	     <seven><xsl:value-of select="$decimal2"/></seven>
	     <eight><xsl:value-of select="$decimal1 = $decimal1"/></eight>
         <nine><xsl:value-of select="$decimal1 = $decimal2"/></nine>
         <divOp>
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
         </divOp>
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