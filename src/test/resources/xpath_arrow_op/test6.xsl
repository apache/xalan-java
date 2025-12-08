<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"   
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
    
   <!-- An XSL stylesheet test case, to test XPath 3.1 operator "=>" -->                 
				
   <xsl:output method="xml" indent="yes"/>

   <xsl:variable name="nodeSet1" as="element()*">
      <a>1</a>
	  <b>2</b>
	  <c>3</c>
	  <d>4</d>
	  <e>5</e>
	  <f>6</f>
	  <g>7</g>
   </xsl:variable>   

   <xsl:template match="/">
      <result>
	     <one>
		    <xsl:value-of select="('a', 'b', 'c', 'd', 'e', 'f', 'g') => subsequence(3)"/>
         </one>
         <two>
		    <xsl:value-of select="('a', 'b', 'c', 'd', 'e', 'f', 'g') => subsequence(3,3)"/>
         </two>
         <three>
            <xsl:copy-of select="$nodeSet1 => subsequence(3)"/>
         </three>
         <four>
            <xsl:copy-of select="$nodeSet1 => subsequence(3,3)"/>
         </four>
         <five>
            <xsl:copy-of select="(for $elem1 in $nodeSet1 return $elem1) => subsequence(3,3)"/>
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