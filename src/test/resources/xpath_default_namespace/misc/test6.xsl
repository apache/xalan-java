<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn0="http://fn0"
                exclude-result-prefixes="#all"			   
                xpath-default-namespace="http://ns0"
			    xmlns="http://ns0"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->			    
			   
   <!-- use with test3.xml-->
   
   <!-- An XSL stylesheet test case, to test XSLT attribute 
        xpath-default-namespace. -->                 
				
   <xsl:output method="xml" indent="yes"/>

   <!--  An XSL variable, referring to an XPath inline function expression. -->
   <xsl:variable name="lastAttrCharFunc" select="function($attr as attribute()) as xs:string { substring($attr, string-length($attr)) }" 
                                                                                                                as="function(attribute()) as xs:string"/>   

   <xsl:template match="/">
      <result>
         <xsl:for-each-group select="info1/item" group-by="fn0:getGroupByValue(@attr)">
		    <group key="{current-grouping-key()}">
			   <xsl:copy-of select="current-group()"/>
			</group>
		 </xsl:for-each-group>
      </result>
   </xsl:template>
   
   <!-- An XSL stylesheet function, that finds the grouping key 
        value for an xdm item in XML input population that needs 
        to be formed into groups. -->
   <xsl:function name="fn0:getGroupByValue" as="xs:boolean">
      <xsl:param name="attr" as="attribute()"/>
	  <xsl:sequence select="(number($lastAttrCharFunc($attr)) mod 2) eq 0"/>
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
