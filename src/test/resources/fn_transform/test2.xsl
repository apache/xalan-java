<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="map fn0"				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:transform -->				    									

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
     <details>
		<xsl:variable name="fnTransformArg" select="fn0:getFnTransformArg(doc('render.xsl'), doc('test1.xml'))" as="map(*)"/>	 
	    <xsl:variable name="fnTransformResult" select="transform($fnTransformArg)" as="map(*)"/>
		<xsl:copy-of select="let $result := $fnTransformResult('output') return $result/details/*"/>
	 </details>
  </xsl:template>
  
  <!-- An XSL function definition, to get function fn:transform's 
       argument as an XDM map. This XSL function definition uses 
       stylesheet-node option while using function fn:transform. -->
  <xsl:function name="fn0:getFnTransformArg" as="map(*)">
     <xsl:param name="stylesheetNode" as="node()"/>
	 <xsl:param name="xmlInpNode" as="node()"/>
	 <xsl:variable name="fnTransformArg" select="map {}"/> 
	 <xsl:variable name="fnTransformArg" select="map:put($fnTransformArg, 'stylesheet-node', $stylesheetNode)"/>
	 <xsl:variable name="fnTransformArg" select="map:put($fnTransformArg, 'source-node', $xmlInpNode)"/>
	 <xsl:sequence select="$fnTransformArg"/>
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
  