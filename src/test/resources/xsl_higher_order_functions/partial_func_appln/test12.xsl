<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                			
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
  
    <!-- An XSL stylesheet test case, to test XPath 3.1 partial 
         function application with an XPath function fn:substring-after. -->                  
	
	<xsl:output method="xml" indent="yes"/>
        
    <xsl:template match="/">
		<result>	
		   <one>
		      <x><xsl:value-of select="substring-after('tattoo','tat')"/></x>
		      <y><xsl:value-of select="substring-after('tattoo','tattoo')"/></y>
		   </one>
		   <two>
		      <x><xsl:value-of select="let $f := substring-after('tattoo',?) return $f('tat')"/></x>
			  <y><xsl:value-of select="let $f := substring-after('tattoo',?) return $f('tattoo')"/></y>
		   </two>
		   <three>
			  <x><xsl:value-of select="let $f := substring-after(?,'tat') return $f('tattoo')"/></x>
			  <y><xsl:value-of select="let $f := substring-after(?,'tattoo') return $f('tattoo')"/></y>
		   </three>
		   <four>
			  <a><xsl:value-of select="let $f := substring-after('tattoo','t',?) return $f(default-collation())"/></a>
			  <b><xsl:value-of select="let $f := substring-after('tattoo','ta',?) return $f(default-collation())"/></b>
			  <c><xsl:value-of select="let $f := substring-after('tattoo','tat',?) return $f(default-collation())"/></c>
			  <d><xsl:value-of select="let $f := substring-after('tattoo','tatt',?) return $f(default-collation())"/></d>
			  <e><xsl:value-of select="let $f := substring-after('tattoo','tatto',?) return $f(default-collation())"/></e>
			  <f><xsl:value-of select="let $f := substring-after('tattoo','tattoo',?) return $f(default-collation())"/></f>
			  <g><xsl:value-of select="let $f := substring-after('tattoo','abc',?) return $f(default-collation())"/></g>
		   </four>
		   <five>
			  <x><xsl:value-of select="let $f := substring-after('Strassetattoo','Straße',?) return $f('http://www.w3.org/2013/collation/UCA?lang=de;strength=primary')"/></x>
			  <y><xsl:value-of select="let $f := substring-after('Straßetattoo','Strasse',?) return $f('http://www.w3.org/2013/collation/UCA?lang=de;strength=primary')"/></y>
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
