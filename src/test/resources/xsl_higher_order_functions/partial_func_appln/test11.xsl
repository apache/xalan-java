<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                			
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
  
    <!-- An XSL stylesheet test case, to test XPath 3.1 partial 
         function application with an XPath function fn:substring-before. -->                 
	
	<xsl:output method="xml" indent="yes"/>
        
    <xsl:template match="/">
		<result>	
		   <one>
		      <x><xsl:value-of select="substring-before('tattoo','attoo')"/></x>
		      <y><xsl:value-of select="substring-before('tattoo','tatto')"/></y>
		   </one>
		   <two>
		      <x><xsl:value-of select="let $f := substring-before('tattoo',?) return $f('attoo')"/></x>
			  <y><xsl:value-of select="let $f := substring-before('tattoo',?) return $f('tatto')"/></y>
		   </two>
		   <three>
			  <x><xsl:value-of select="let $f := substring-before(?,'attoo') return $f('tattoo')"/></x>
			  <y><xsl:value-of select="let $f := substring-before(?,'tatto') return $f('tattoo')"/></y>
		   </three>
		   <four>
		      <x><xsl:value-of select="let $f := substring-before('tattoo','attoo',?) return $f(default-collation())"/></x>
		      <y><xsl:value-of select="let $f := substring-before('tattoo','tatto',?) return $f(default-collation())"/></y>
		   </four>
		   <five>
		      <x><xsl:value-of select="let $f := substring-before('test stringHelloStrasse','HelloStraße',?) return $f('http://www.w3.org/2013/collation/UCA?lang=de;strength=primary')"/></x>
		      <y><xsl:value-of select="let $f := substring-before('test stringHelloStraße','HelloStrasse',?) return $f('http://www.w3.org/2013/collation/UCA?lang=de;strength=primary')"/></y>
		   </five>
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
