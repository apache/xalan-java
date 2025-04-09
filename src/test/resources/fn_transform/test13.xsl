<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"			    
			    xmlns:xs="http://www.w3.org/2001/XMLSchema"
			    xmlns:fn0="http://fn0"
			    exclude-result-prefixes="xs fn0"
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->				
				
  <!-- use with test3.xml -->

  <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:transform
       doing a chaining of XSL stylesheet transformations. 
       
       An XSL stylesheet algorithm implemented within this test case, has been
       suggested by Martin Honnen within jira issue XALANJ-2809.
  -->  

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
     <xsl:variable name="seq1" select="('secondary.xsl', 'secondary.xsl')" as="xs:string*"/>
     <xsl:copy-of select="fold-left($seq1, ., fn0:transform-node#2)"/>
  </xsl:template>
  
  <!-- An XSL stylesheet function, that transforms an XDM node
       using the supplied stylesheet specified as uri. -->
  <xsl:function name="fn0:transform-node" as="node()">
     <xsl:param name="node" as="node()"/>
     <xsl:param name="xslt" as="xs:string"/>
     <xsl:variable name="transform-arg-map" select="map {'source-node' : $node, 'stylesheet-node' : doc($xslt)}" as="map(*)"/>
     <xsl:sequence select="let $result := transform($transform-arg-map) return $result?output"/>
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
