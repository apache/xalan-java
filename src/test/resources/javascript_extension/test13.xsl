<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
	            xmlns:xalan="http://xml.apache.org/xalan"				
                xmlns:js0="http://js0"
	            exclude-result-prefixes="#all"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->				
    
    <!-- An XSL stylesheet test case, to test Xalan-J's interface 
         with JavaScript extension functions. -->							
				
    <xsl:output method="xml" indent="yes"/>				

    <xalan:component prefix="js0">
        <xalan:script lang="javascript">
            <![CDATA[							   
			   function getJsonInfo() {
                  var result;                  			  
				  
				  result = JSON.parse('{"person" : {"fName" : "Mukul", "lName" : "Gandhi"}, "hobbies" : ["cooking", "gardening", "reading"], "city" : "New Delhi"}');
				  
                  return result;                  
               }
            ]]>
        </xalan:script>
    </xalan:component>

    <xsl:template match="/">
       <result>
		  <xsl:variable name="map1" select="js0:getJsonInfo()" as="map(*)"/>
		  <map>
		     <xsl:for-each select="sort(map:keys($map1))">
			    <entry>
				   <key><xsl:value-of select="."/></key>
				   <value>
				      <xsl:variable name="val1" select="map:get($map1,.)"/>
					  <xsl:choose>
					     <xsl:when test="$val1 instance of map(*)">
						    <map>
						       <xsl:for-each select="sort(map:keys($val1))">
							      <entry>
							         <key><xsl:value-of select="."/></key>
								     <value><xsl:value-of select="map:get($val1,.)"/></value>
								  </entry>
							   </xsl:for-each>
							</map>
						 </xsl:when>
						 <xsl:when test="$val1 instance of array(*)">
						    <array>
							   <xsl:for-each select="1 to array:size($val1)">
							      <item><xsl:value-of select="array:get($val1,.)"/></item>
							   </xsl:for-each>
							</array>
						 </xsl:when>
						 <xsl:otherwise>
						    <xsl:value-of select="$val1"/>
						 </xsl:otherwise>
					  </xsl:choose>
				   </value>
				</entry>
			 </xsl:for-each>
		  </map>
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