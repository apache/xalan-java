<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="xs fn0"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test13.xml -->
   
   <!-- An XSLT test case to test, XPath 3.1 tokenize function. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
     <groupsOfWords> 	 
	    <xsl:for-each-group select="fn0:getNonEmptyTokens(tokenize(info,'[\s+|,|\.]+'))" group-by="string-length(.)">
	      <xsl:sort select="current-grouping-key()" data-type="number"/>
	        <wordsGroup strLength="{current-grouping-key()}" groupSize="{count(current-group())}">
			  <words>			    
			    <xsl:value-of select="string-join(for $nonMatchElem in current-group() return xs:string($nonMatchElem),',')"/>
			  </words>
		    </wordsGroup>
	    </xsl:for-each-group>
	 </groupsOfWords>
   </xsl:template>
   
   <!-- Get tokens list as sequence of "token" elements, for token 
        strings having length > 0. -->
   <xsl:function name="fn0:getNonEmptyTokens" as="element()*">
      <xsl:param name="tokens" as="xs:string*"/>
	  <xsl:for-each select="$tokens[string-length(.) &gt; 0]">
		 <token><xsl:value-of select="."/></token>
	  </xsl:for-each>
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
