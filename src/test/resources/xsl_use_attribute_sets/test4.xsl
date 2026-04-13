<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:fo="http://www.w3.org/1999/XSL/Format">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test2.xml -->
   
  <!-- An XSLT 3.0 stylesheet test case to, test XSL attribute "use-attribute-sets". 
       This stylesheet example uses an XSL-FO document vocabulary as result of XSL 
       transformation. This XSL stylesheet example, also demonstrates overriding 
       attributes in an attribute set.
  -->				
				
  <xsl:output method="xml" indent="yes"/>				    
  
  <xsl:template match="/">
      <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
	      <fo:layout-master-set>
			<fo:simple-page-master master-name="first"
								   margin-right="1.5cm"
								   margin-left="1.5cm"
								   margin-bottom="2cm"
								   margin-top="1cm"
								   page-width="21cm"
								   page-height="29.7cm">
			   <fo:region-body margin-top="1cm"/>
			   <fo:region-before extent="1cm"/>
			   <fo:region-after extent="1.5cm"/>
			</fo:simple-page-master>
		  </fo:layout-master-set>
		  <fo:page-sequence master-reference="first">
		     <fo:flow flow-name="xsl-region-body">
			    <fo:block space-before.optimum="3pt" space-after.optimum="15pt">
                   <xsl:apply-templates select="chapter/heading"/>
                </fo:block>
			 </fo:flow>
		  </fo:page-sequence>
	  </fo:root>
  </xsl:template>
  
  <xsl:template match="chapter/heading">
    <fo:block font-stretch="condensed" xsl:use-attribute-sets="base-style">
	   <xsl:attribute name="font-size">14pt</xsl:attribute>
       <xsl:attribute name="font-weight">bold</xsl:attribute>
       <xsl:apply-templates/>
    </fo:block>
  </xsl:template>

  <xsl:attribute-set name="base-style">
     <xsl:attribute name="font-family">Univers</xsl:attribute>
     <xsl:attribute name="font-size">10pt</xsl:attribute>
     <xsl:attribute name="font-style">normal</xsl:attribute>
     <xsl:attribute name="font-weight">normal</xsl:attribute>
  </xsl:attribute-set>
  
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