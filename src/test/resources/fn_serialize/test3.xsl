<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test XPath function fn:serialize. -->				
    
    <xsl:output method="json"/>
    
    <xsl:template match="/">
        <xsl:variable name="map1" select="map {'a' : 1, 'b' : 2, 'c' : [5, 6, 7]}" as="map(*)"/>		
		<xsl:variable name="params" as="element(output:serialization-parameters)" xmlns:output="http://www.w3.org/2010/xslt-xquery-serialization">
		   <output:serialization-parameters>
		      <output:method value="json"/>
		   </output:serialization-parameters>
		</xsl:variable>
        <xsl:value-of select="serialize($map1, $params)"/>
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