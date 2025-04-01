<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs map fn0"				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL stylesheet test case, to test XPath 3.1 function fn:transform -->				    									

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
      <details>
         <!-- Specifying an XSL secondary stylesheet, as a literal text as a value of this variable. -->
	     <xsl:variable name="xslStylesheetTxtVar1" select="'
			&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot;				
							version=&quot;3.0&quot;&gt;
							
				&lt;xsl:output method=&quot;xml&quot; indent=&quot;yes&quot;/&gt;				
				
				&lt;xsl:template match=&quot;/info&quot;&gt;
				   &lt;details&gt;
					  &lt;xsl:for-each select=&quot;*&quot;&gt;
						 &lt;xsl:element name=&quot;{name()}&quot;&gt;
							&lt;xsl:value-of select=&quot;position()&quot;/&gt;
						 &lt;/xsl:element&gt;
					  &lt;/xsl:for-each&gt;
				   &lt;/details&gt;
				&lt;/xsl:template&gt;
				
			&lt;/xsl:stylesheet&gt;
		 '" as="xs:string"/>
		 <xsl:variable name="fnTransformArg" select="fn0:getFnTransformArg($xslStylesheetTxtVar1, doc('test1.xml'))" as="map(*)"/>	 
	     <xsl:variable name="fnTransformResult" select="transform($fnTransformArg)" as="map(*)"/>
		 <xsl:copy-of select="let $result := $fnTransformResult('output') return $result/details/*"/>
	  </details>
  </xsl:template>
  
  <!-- An XSL function definition, to get function fn:transform's 
       argument as an XDM map. This XSL function definition uses 
       stylesheet-text option while using function fn:transform. 
       
       This stylesheet function returns an XDM map, as if it was constructed from
       the following lexical XPath map syntax :
       map {
          "stylesheet-text" : '<xsl:stylesheet> ......... </xsl:stylesheet>',
          "source-node"     : doc('test.xml')
       }     
  -->
  <xsl:function name="fn0:getFnTransformArg" as="map(*)">
     <xsl:param name="xslStylesheetTxt" as="xs:string"/>
	 <xsl:param name="xmlInpNode" as="node()"/>
	 <xsl:variable name="fnTransformArg" select="map {}"/> 
	 <xsl:variable name="fnTransformArg" select="map:put($fnTransformArg, 'stylesheet-text', $xslStylesheetTxt)"/>
	 <xsl:variable name="fnTransformArg" select="map:put($fnTransformArg, 'source-node', $xmlInpNode)"/>
	 <xsl:sequence select="$fnTransformArg"/>
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
  