<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"                
                version="3.0">

   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1.xml -->
   
   <!-- An XSLT test case to test, xsl:attribute instruction's 
        attribute 'validation'. -->				

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:import-schema>
      <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
		   <xs:element name="info" type="Type1"/>
		   
		   <xs:complexType name="Type1">
			  <xs:sequence> 
				 <xs:element name="a" maxOccurs="unbounded">
					<xs:complexType>
					   <xs:simpleContent>
						  <xs:extension base="xs:string">
							 <xs:attribute name="val" type="xs:integer"/>
						  </xs:extension>
					   </xs:simpleContent>
					</xs:complexType>
				 </xs:element>
			  </xs:sequence>
		   </xs:complexType>
		   
		   <xs:attribute name="t1" type="tType"/>
		   <xs:attribute name="t2" type="tType"/>
		   
		   <xs:simpleType name="tType">
		      <xs:restriction base="xs:integer">
			     <xs:minInclusive value="10"/>
			  </xs:restriction>
		   </xs:simpleType>
	   </xs:schema>
   </xsl:import-schema>

   <xsl:template match="/info">
	  <info>
         <xsl:for-each select="a">
		   <p>
              <xsl:attribute name="t1" validation="strict">10</xsl:attribute>
			  <xsl:attribute name="t2" validation="strict">20</xsl:attribute>
		      <val><xsl:value-of select="@val + 10"/></val>
			  <data><xsl:value-of select="."/></data>
		   </p>
		 </xsl:for-each>
      </info>	  
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
