<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                exclude-result-prefixes="xs map"				
			    version="3.0">									

  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test2.xml -->
  
  <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:transform. This XSL 
       stylesheet test case, uses similar XSL stylesheet algorithm details provided by 
       Martin Honnen on Apache Xalan jira forum. -->
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/root">
      <result>
		  <xsl:variable name="result" select="transform(map {'source-node' : parse-xml(xml), 'stylesheet-node' : parse-xml(xslt)})" as="map(*)"/>          														
		  <xsl:for-each select="map:keys($result)">
		     <xsl:choose>
			    <xsl:when test="contains(., '/')">
		           <xsl:variable name="urlParts" select="tokenize(xs:string(.), '/')" as="xs:string*"/>
		           <xsl:variable name="urlSuffixStr" select="$urlParts[count($urlParts)]" as="xs:string"/>
		           <doc uri="{$urlSuffixStr}">
                      <xsl:copy-of select="$result(.)"/>
			       </doc>
			    </xsl:when>
			    <xsl:otherwise>
			       <doc uri="{.}">
                     <xsl:copy-of select="$result(.)"/>
			       </doc>
			    </xsl:otherwise>
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
  