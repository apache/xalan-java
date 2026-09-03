<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
                version="3.0">
                
     <!-- Author: mukulg@apache.org -->
   
     <!-- An XSL stylesheet test case, to test XPath function arguments  -->                

     <xsl:output method="xml" indent="yes"/>
	 
	 <xsl:variable name="x1" select="5" as="xs:integer"/>
	 <xsl:variable name="x2" select="10" as="xs:integer"/>

     <xsl:template match="/">
         <result>
		    <one>
              <xsl:value-of select="not($x1 = $x2)"/>
			</one>
			<two>
              <xsl:value-of select="not($x1 = $x1)"/>
			</two>
			<three>
			   <xsl:value-of select="not(5 = $x2)"/>
			</three>
			<four>
			   <xsl:value-of select="not($x1 = 10)"/>
			</four>
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