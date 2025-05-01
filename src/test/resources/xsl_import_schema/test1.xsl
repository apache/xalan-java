<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:opt1="http://example.com/ns/yes-no"
                exclude-result-prefixes="opt1"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
    
  <!-- An XSLT stylesheet test case, to test XSLT 3.0's xsl:import-schema 
       instruction. 
  -->                 
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:import-schema>
      <xs:schema targetNamespace="http://example.com/ns/yes-no"
                 xmlns:xs="http://www.w3.org/2001/XMLSchema">
         <xs:simpleType name="yes-no">
            <xs:restriction base="xs:string">
               <xs:enumeration value="yes"/>
               <xs:enumeration value="no"/>
            </xs:restriction>
         </xs:simpleType>
      </xs:schema>
  </xsl:import-schema>
  
  <xsl:template match="/">
     <result>
       <xsl:variable name="val1" select="opt1:yes-no('yes')" as="opt1:yes-no"/>
       <xsl:variable name="val2" select="opt1:yes-no('no')" as="opt1:yes-no"/>
       <one>
         <xsl:value-of select="$val1"/>
       </one>
       <two>
         <xsl:value-of select="$val2"/>
       </two>
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