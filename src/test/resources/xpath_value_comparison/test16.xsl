<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:ns0="http://xalan.apache.org/xslt3"
				exclude-result-prefixes="xs ns0"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- An XSL stylesheet test case, to test XPath 3.1 value comparison 
         operator 'ne'. This stylesheet test case, tests when one or both 
         operands of XPath operator 'ne' are XML Schema user defined simple 
         typed values. -->                 			

	<xsl:output method="xml" indent="yes"/>
	
	<xsl:import-schema namespace="http://xalan.apache.org/xslt3" schema-location="info_1.xsd"/>

	<xsl:template match="/">
	  <root>
	     <xsl:variable name="val1" select="5" as="xs:decimal"/>
		 <three>
		   <xsl:value-of select="$val1 ne ns0:shoesize(5)"/>
		 </three>
		 <four>
		   <xsl:value-of select="$val1 ne ns0:shoesize(7)"/>
		 </four>
		 <five>
		   <xsl:value-of select="ns0:hatsize(5) ne ns0:shoesize(5)"/>
		 </five>
		 <six>
		   <xsl:value-of select="ns0:hatsize(5) ne ns0:shoesize(7)"/>
		 </six>
		 <seven>
		   <xsl:value-of select="ns0:shoesize(5) ne ns0:shoesize(7)"/>
		 </seven>
		 <eight>
		   <xsl:value-of select="ns0:shoesize(7) ne ns0:shoesize(7)"/>
		 </eight>
	  </root>
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
