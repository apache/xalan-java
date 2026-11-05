<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"                           
                exclude-result-prefixes="xs"
			    version="3.0">
			    
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test13.xml -->
    
   <!-- An XSL stylesheet test case, to test XPath 'instance of' operator -->			    
				
   <xsl:output method="xml" indent="yes"/>				

   <xsl:variable name="values" select="(xs:integer(1), xs:decimal(1.52), xs:double(1.52), xs:untypedAtomic('2.5E1'), 
                                        xs:untypedAtomic('2'), 3,4, info/integer, map{'a':1,'b':[5, 6, 7],'c':3})" as="item()*"/>

   <xsl:template match="/">
	  <result>
	     <xsl:for-each select="$values">
		    <xsl:variable name="value1" select="." as="item()"/>
		    <xsl:choose>
			   <xsl:when test="$value1 instance of xs:integer">
			      <value type="integer"><xsl:value-of select="$value1"/></value>
			   </xsl:when>
			   <xsl:when test="$value1 instance of xs:decimal">
			      <value type="decimal"><xsl:value-of select="$value1"/></value>
			   </xsl:when>
			   <xsl:when test="$value1 instance of xs:double">
			      <value type="double"><xsl:value-of select="$value1"/></value>
			   </xsl:when>
			   <xsl:when test="$value1 instance of xs:untypedAtomic">
			      <value type="untypedAtomic"><xsl:value-of select="$value1"/></value>
			   </xsl:when>
			   <xsl:when test="$value1 instance of element()">
			      <value type="elemNode"><xsl:copy-of select="$value1"/></value>
			   </xsl:when>
			   <xsl:when test="$value1 instance of map(*)">
			      <value type="map"><xsl:value-of select="serialize($value1, map{'method':'adaptive'})"/></value>
				  <xsl:variable name="array1" select="$value1('b')" as="array(*)"/>
				  <value type="array"><xsl:value-of select="serialize($array1, map{'method':'adaptive'})"/></value>
			   </xsl:when>			   
			</xsl:choose>
		 </xsl:for-each>
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
