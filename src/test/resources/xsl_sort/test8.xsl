<?xml version="1.0" ?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
                  
  <!-- An XSL stylesheet test case, to test xsl:for-each's xsl:sort 
       instruction. -->                 
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="var1" as="element()*">
     <word>there</word>
	 <word>There</word>
	 <word>hello</word>
	 <word>Hello</word>
	 <word>Thanks</word>
  </xsl:variable>

  <xsl:template match="/">
     <result>	    
		<one>
		   <xsl:for-each select="$var1">
		      <xsl:sort select="." case-order="lower-first"/>
			  <item>
				 <xsl:value-of select="."/>
			  </item>
		   </xsl:for-each>
		</one>
		<two>
		   <xsl:for-each select="('there','There','hello','Hello','Thanks')">
			  <xsl:sort select="." case-order="lower-first"/>
			  <item>
				 <xsl:value-of select="."/>
			  </item>
		   </xsl:for-each>
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
