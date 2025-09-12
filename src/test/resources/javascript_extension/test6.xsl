<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
	            xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:js0="http://js0"
				xmlns:fn0="http://fn0"
	            exclude-result-prefixes="#all"
				version="3.0">    
    
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test2.xml -->
    
    <!-- An XSL stylesheet test case, to test Xalan-J's interface 
         with JavaScript extension functions. -->				
				
    <xsl:output method="xml" indent="yes"/>				

    <xalan:component prefix="js0">
        <xalan:script lang="javascript">
            <![CDATA[
               // Function definition, to get the maximum value from 
			   // an array of numeric values.			
			   function getMaxValue(nums) {
                  var result;

                  if (nums.length === 0) {
                     return "Empty input";
                  }
				  
				  result = Number(nums[0]);
 				  
				  for (var i = 1; i < nums.length; i++) {
				     if (Number(nums[i]) > result) {
				        result = Number(nums[i]);
				     }
				  }				  
				  
				  return result;
               }
			   
			   // Function definition, to get the minimum value from 
			   // an array of numeric values.
			   function getMinValue(nums) {
                  var result;

                  if (nums.length === 0) {
                     return "Empty input";
                  }
				  
				  result = Number(nums[0]);
 				  
				  for (var i = 1; i < nums.length; i++) {
				     if (Number(nums[i]) < result) {
				        result = Number(nums[i]);
				     }
				  }				  
				  
				  return result;
               }
            ]]>
        </xalan:script>
    </xalan:component>

    <xsl:template match="/">
       <result>   
		  <max>
		     <xsl:value-of select="js0:getMaxValue(fn0:getDecimalSeq(/document/nums/n))"/>
		  </max>
		  <min>
		     <xsl:value-of select="js0:getMinValue(fn0:getDecimalSeq(/document/nums/n))"/>
		  </min>
       </result>
    </xsl:template>
	
	<!-- An XSL stylesheet function, to get a sequence of xs:decimal 
	     values from the supplied XML nodeset.-->
	<xsl:function name="fn0:getDecimalSeq" as="xs:decimal*">
	   <xsl:param name="nodeSet1" as="element(n)*"/>
	   <xsl:sequence select="for $i in 1 to count($nodeSet1) return xs:decimal($nodeSet1[$i])"/>
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