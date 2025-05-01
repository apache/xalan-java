<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                version="3.0">

   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1.xml -->
    
   <!-- An XSLT stylesheet test case, to test use of 'type' attribute
        on stylesheet element xsl:element. This stylesheet test uses
        an user-defined schema type specified via xsl:import-schema 
        instruction, specified on xsl:element's attribute 'type'.
   --> 				

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
		   
		   <xs:complexType name="pType">
			  <xs:sequence>
				 <xs:element name="val">
					<xs:simpleType>
					   <xs:restriction base="xs:integer">
						  <xs:minInclusive value="10"/>
					   </xs:restriction>
					</xs:simpleType>
				 </xs:element>
				 <xs:element name="data" type="xs:string"/>
			  </xs:sequence>
			  <xs:attribute name="t1" type="xs:string"/>
			  <xs:attribute name="t2" type="xs:string"/>
		   </xs:complexType>
	   </xs:schema>
   </xsl:import-schema>

   <xsl:template match="/info">
	  <info>
         <xsl:for-each select="a">
		   <xsl:element name="p" type="pType">
              <xsl:attribute name="t1">hello</xsl:attribute>
			  <xsl:attribute name="t2">there</xsl:attribute>
		      <val><xsl:value-of select="@val + 10"/></val>
			  <data><xsl:value-of select="."/></data>
		   </xsl:element>
		 </xsl:for-each>
      </info>	  
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
