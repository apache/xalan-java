<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="#all"
				version="3.0">
				
  <!-- This XSL stylesheet example, is provided by Martin Honnen. -->				
				
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test14.xml -->				

   <xsl:output method="xml" indent="yes"/>

   <xsl:param name="chunk-size" select="3" as="xs:integer"/>

   <xsl:template match="/">
     <result>
	     <xsl:for-each-group select="items/item" group-adjacent="(position() - 1) idiv $chunk-size">
	       <chunk number="{position()}">
	         <xsl:sequence select="current-group()"/>
	       </chunk>
	     </xsl:for-each-group>
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