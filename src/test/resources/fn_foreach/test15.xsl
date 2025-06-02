<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:local="http://local"
                exclude-result-prefixes="#all"				
                version="3.0">
				
   <!-- Author: mukulg@apache.org -->
   				
   <!-- use with test14.xml -->
   
   <!-- An XSL stylesheet test case, to test function fn:for-each when 
        its function item argument is an XSL stylesheet function 
        reference. -->
   
   <xsl:output method="xml" indent="yes"/>				

   <xsl:template match="doc">
      <result>
	     <xsl:variable name="namedFuncRef" select="local:sqr#1"/>
		 <xsl:value-of select="for-each(n, $namedFuncRef)"/>
      </result>
   </xsl:template>
   
   <xsl:function name="local:sqr" as="xs:double">
     <xsl:param name="elemNode1" as="element(n)"/>
	 <xsl:sequence select="number($elemNode1) * number($elemNode1)"/>
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
