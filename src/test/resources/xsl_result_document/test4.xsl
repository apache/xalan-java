<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                exclude-result-prefixes="fn"				
			    version="3.0">
			    
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL 3 stylesheet test case, to test xsl:result-document 
       instruction having method="json". -->			    					

  <xsl:output method="text"/>

  <xsl:template match="/">
     <xsl:result-document href="result2.json" method="json">
		<xsl:variable name="xmlNodeSet1">
          <fn:map>
		     <fn:array key="data">
			    <fn:map>
				   <fn:number key="a">1</fn:number>
				   <fn:number key="b">2</fn:number>
				   <fn:number key="c">3</fn:number>
				</fn:map>
				<fn:map>
				   <fn:number key="m">4</fn:number>
				   <fn:number key="n">5</fn:number>
				   <fn:number key="o">6</fn:number>
				</fn:map>
				<fn:map>
				   <fn:number key="p">7</fn:number>
				   <fn:number key="q">8</fn:number>
				   <fn:number key="r">9</fn:number>
				</fn:map>
			 </fn:array>
		  </fn:map>
        </xsl:variable>
        <xsl:value-of select="xml-to-json($xmlNodeSet1, map {'indent' : true()})"/> 		
     </xsl:result-document>
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
