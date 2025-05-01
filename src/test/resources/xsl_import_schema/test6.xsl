<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ns0="http://example.com/ns/ns0"
                exclude-result-prefixes="ns0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_a.xml -->
    
  <!-- An XSLT stylesheet test case, to test XSLT 3.0's xsl:import-schema 
       instruction providing access to more than one XML Schema simple type 
       definitions. Within this stylesheet, we illustrate doing XPath expression 
       evaluations on validated xdm information. 
  -->                 
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:import-schema>
      <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
                 targetNamespace="http://example.com/ns/ns0">         
         <xs:simpleType name="boundedInteger1">
            <xs:restriction base="xs:integer">
               <xs:minInclusive value="5"/>
               <xs:maxInclusive value="10"/>
            </xs:restriction>
         </xs:simpleType>
         
         <xs:simpleType name="boundedInteger2">
	        <xs:restriction base="xs:integer">
	          <xs:minInclusive value="12"/>
	          <xs:maxInclusive value="52"/>
	        </xs:restriction>
         </xs:simpleType>         
      </xs:schema>
  </xsl:import-schema>
  
  <xsl:template match="/data1">
     <result>
       <xsl:variable name="val1" select="ns0:boundedInteger1(string(val1))" as="ns0:boundedInteger1"/>
       <xsl:variable name="val2" select="ns0:boundedInteger1(string(val2))" as="ns0:boundedInteger1"/>
       <xsl:variable name="val3" select="ns0:boundedInteger2(string(val3))" as="ns0:boundedInteger2"/>
       <one>
         <xsl:value-of select="$val1 + $val2"/>
       </one>
       <two>
         <xsl:value-of select="$val1 - $val2"/>
       </two> 
       <three>
         <xsl:value-of select="$val1 * $val2"/>
       </three>
       <four>
         <xsl:value-of select="$val1 div $val2"/>
       </four>
       <five>
         <xsl:value-of select="$val3 - $val1"/>
       </five>
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