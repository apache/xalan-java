<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fn0="http://ns0"
				exclude-result-prefixes="fn0"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
    
   <!-- An XSL stylesheet test case, to test XSLT 3.0's xsl:import-schema 
        instruction. 
   -->                 			

   <xsl:output method="xml" indent="yes"/>
   
   <!-- An xsl:import-schema instruction, that has inline XML Schema 
        definitions for complex types. -->
   <xsl:import-schema>
		<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">		   
		   <xs:complexType name="Address">
			  <xs:sequence>
				 <xs:element name="fName" type="xs:string"/>
				 <xs:element name="mName" type="xs:string" minOccurs="0"/>
				 <xs:element name="lName" type="xs:string"/>
				 <xs:element name="addressLine1" type="xs:string"/>
				 <xs:element name="addressLine2" type="xs:string" minOccurs="0"/>
				 <xs:element name="city" type="xs:string" minOccurs="0"/>
				 <xs:element name="country" type="xs:string"/>
				 <xs:element name="state" type="xs:string" minOccurs="0"/>
				 <xs:element name="zip" type="xs:string" minOccurs="0"/>
			  </xs:sequence>
		   </xs:complexType>
		   
		   <xs:complexType name="Person">
			  <xs:sequence>
				 <xs:element name="fName" type="xs:string"/>
				 <xs:element name="mName" type="xs:string" minOccurs="0"/>
				 <xs:element name="lName" type="xs:string"/>
			  </xs:sequence>
		   </xs:complexType>
		</xs:schema>
   </xsl:import-schema>

   <xsl:template match="/info">
	  <result>
	     <xsl:for-each select="address">
		   <xsl:variable name="addressElem" select="."/>
		   <address id="{'id_' || position()}">
		      <xsl:variable name="person1" as="element(*, Person)">
		         <xsl:copy-of select="fn0:getAddressInfo($addressElem cast as Address)"/>
			  </xsl:variable>			  
			  <xsl:copy-of select="$person1"/>
		   </address>
		 </xsl:for-each>
	  </result>	  
   </xsl:template>
   
   <xsl:function name="fn0:getAddressInfo" as="element(*, Person)">
	  <xsl:param name="addressElem" as="element(*, Address)"/>
	  <person>
	     <xsl:copy-of select="$addressElem/fName | $addressElem/mName | $addressElem/lName"/>
	  </person>
   </xsl:function>
   
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
