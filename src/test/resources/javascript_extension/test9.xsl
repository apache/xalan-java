<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	            xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:js0="http://js0"
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
			   // a list of numeric values.			
			   function getMaxValue(xmlDoc) {
                  var result;
				  
				  const nodeList = xmlDoc.getElementsByTagName("n");
				  const nodeListSize = nodeList.getLength();
				  for (var idx = 0; idx < nodeListSize; idx++) {
                     var number = Number((nodeList.item(idx)).textContent);
                     if ((idx === 0) || (number > result)) {
                        result = number;
                     }
                  }				  
				  
				  return result;
               }
			   
			   // Function definition, to get the minimum value from 
			   // a list of numeric values.
			   function getMinValue(xmlDoc) {
                  var result;
				  
				  const nodeList = xmlDoc.getElementsByTagName("n");
				  const nodeListSize = nodeList.getLength();
				  for (var idx = 0; idx < nodeListSize; idx++) {
                     var number = Number((nodeList.item(idx)).textContent);
                     if ((idx === 0) || (number < result)) {
                        result = number;
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
		     <xsl:value-of select="js0:getMaxValue(/document/nums)"/>
		  </max>
		  <min>
		     <xsl:value-of select="js0:getMinValue(/document/nums)"/>
		  </min>
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