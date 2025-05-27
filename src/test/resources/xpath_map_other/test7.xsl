<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL stylesheet test case to test, XPath dynamic function call 
        syntax with chained arguments to query XPath map information. 
        This stylesheet reads input information from a JSON document. -->             

    <xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/">
	   <result>
		  <xsl:variable name="jsonmap1" select="parse-json(unparsed-text('test2.json'))" as="map(*)"/>
		  <one>
			 <array1><xsl:value-of select="$jsonmap1('info1')('numList')"/></array1>
			 <value1><xsl:value-of select="$jsonmap1('info1')('message')"/></value1>			
		  </one>
		  <two>
			 <array2><xsl:value-of select="$jsonmap1('info2')('numList')"/></array2>
			 <value2><xsl:value-of select="$jsonmap1('info2')('message')"/></value2>
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
