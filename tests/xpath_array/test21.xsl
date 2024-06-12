<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="xs array"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3.0 test case, to test XPath function calls that accept
         literal array expressions as arguments.
    -->                                            

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
	   <result>
	     <one>
            <xsl:value-of select="array:filter(['A', 'B', 1, 2], function($x) {$x instance of xs:integer})"/>
	     </one>
		 <two>
            <xsl:value-of select="array:filter(array {'A', 'B', 1, 2}, function($x) {$x instance of xs:integer})"/>
	     </two>
	   </result>
    </xsl:template>
    
    <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the  "License");
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
