<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
   
    <!-- An XSLT stylesheet test case, to test XPath 3.1 "instance of" 
         expression involving data type xs:int and its ancestor type 
         hierarchy.
         XML Schema datatypes spec defines xs:int's type hierarchy as
         follows : xs:anyAtomicType -> xs:decimal -> xs:integer ->
         xs:long -> xs:int.         
    -->                               

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
      <xsl:variable name="int1" select="xs:int(5)"/>
      <result>
        <one>
           <xsl:value-of select="$int1 instance of xs:int"/>
        </one>
        <two>
	       <xsl:value-of select="$int1 instance of xs:long"/>
        </two>
        <three>
           <xsl:value-of select="$int1 instance of xs:integer"/>
        </three>
        <four>
           <xsl:value-of select="$int1 instance of xs:decimal"/>
        </four>
        <five>
	       <xsl:value-of select="$int1 instance of xs:anyAtomicType"/>
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
