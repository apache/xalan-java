<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  
			    xmlns:xs="http://www.w3.org/2001/XMLSchema"
			    exclude-result-prefixes="xs"
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->			    

  <xsl:output method="xml" indent="yes"/>
  
  <!-- An XSL stylesheet identity template -->
  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- An XSL stylesheet template, that processes an XML element 
       'data'.       
       An XDM text node within an element 'data', corresponds 
       to a csv string whose items are lexical integer values. One 
       additional csv integer item is appended to the text node.
  -->
  <xsl:template match="data">
     <data>
	   <xsl:variable name="seq1" select="for $str in tokenize(., ',') return xs:integer($str)" as="xs:integer*"/>
	   <xsl:value-of select=". || ',' || (max($seq1) + 1)"/>
	 </data>
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
