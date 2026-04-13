<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="3.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1.xml -->
   
  <!-- An XSLT 3.0 stylesheet test case to, test XSL attribute "use-attribute-sets". -->                
				
  <xsl:output method="html" indent="yes"/>				
  
  <xsl:attribute-set name="note-style">
    <xsl:attribute name="style">font-style: italic</xsl:attribute>
	<xsl:attribute name="id">paragraph1</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="attribute_set1" use-attribute-sets="note-style"/>
  
  <xsl:template match="/">
     <html>
	    <head>
		   <title>Result from test stylesheet</title>
		</head>
		<body>
		   <p xsl:use-attribute-sets="attribute_set1">Hello. This is test information.</p>
		</body>
	 </html>
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