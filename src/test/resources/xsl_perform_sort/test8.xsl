<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"			    
			    xmlns:array="http://www.w3.org/2005/xpath-functions/array"
			    exclude-result-prefixes="array" 
				version="3.0">
				
   <!-- Author: mukulg@apache.org -->
  
   <!-- An XSL 3 stylesheet test case to test, xsl:perform-sort 
        instruction, to sort a sequence of xdm array items. -->
        
   <xsl:output method="xml" indent="yes"/>         				

   <xsl:variable name="array-sequence1" select="( [1,2,3], [1], [5,6,7,8,9], [1,2], [2,3,4,5] )" as="array(*)*"/>
       
   <xsl:template match="/">
      <result>
	     <xsl:variable name="sorted-array-sequence1" as="array(*)*">
            <xsl:perform-sort select="$array-sequence1">
		      <xsl:sort select="array:size(.)" data-type="number"/>			
		    </xsl:perform-sort>
		 </xsl:variable>
		 <xsl:variable name="sorted-array-sequence2" as="array(*)*">
            <xsl:perform-sort select="$array-sequence1">
		      <xsl:sort select="array:size(.)" data-type="number" order="descending"/>			
		    </xsl:perform-sort>
		 </xsl:variable>
		 <one>
		    <xsl:for-each select="$sorted-array-sequence1">
               <array>
                  <xsl:copy-of select="."/>
               </array>
            </xsl:for-each>
         </one>
         <two>
		    <xsl:for-each select="$sorted-array-sequence2">
               <array>
                  <xsl:copy-of select="."/>
               </array>
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
