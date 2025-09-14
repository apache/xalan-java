<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
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
				  
				  result = JSON.parse('["a", "b", {"one" : 1.1, "two" : 2.1, "three" : 3.1}]');
				  
				  return result;
			   }
            ]]>
        </xalan:script>
    </xalan:component>

    <xsl:template match="/">
       <result>
		  <xsl:variable name="arr1" select="js0:getJsonInfo()" as="array(*)"/>
		  <array>
		     <item><xsl:value-of select="array:get($arr1,1)"/></item>
			 <item><xsl:value-of select="array:get($arr1,2)"/></item>
			 <xsl:variable name="map1" select="array:get($arr1,3)" as="map(xs:string,xs:double)"/>
			 <item>
			    <map>
				   <entry>
				      <key>one</key>
					  <value><xsl:value-of select="map:get($map1,'one')"/></value>
				   </entry>
				   <entry>
				      <key>two</key>
					  <value><xsl:value-of select="map:get($map1,'two')"/></value>
				   </entry>
				   <entry>
				      <key>three</key>
					  <value><xsl:value-of select="map:get($map1,'three')"/></value>
				   </entry>
				</map>
			 </item>
		  </array>
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