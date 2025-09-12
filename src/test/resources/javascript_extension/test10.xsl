<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	            xmlns:xalan="http://xml.apache.org/xalan"
				xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:js0="http://js0"
	            exclude-result-prefixes="#all"
				version="3.0">

    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test4.xml -->
    
    <!-- An XSL stylesheet test case, to test Xalan-J's interface 
         with JavaScript extension functions. -->				
				
    <xsl:output method="xml" indent="yes"/>				

    <xalan:component prefix="js0">
        <xalan:script lang="javascript">
            <![CDATA[
               // Function definition, to transform a JSON string 
			   // value in a specific way.			
			   function processJsonStr(jsonStr1) {
                  var result;
				  
				  const jsObject = JSON.parse(jsonStr1);				  
                  var fName = jsObject.fName;				  
                  var lName = jsObject.lName;
				  var profession = jsObject.profession;

                  result = "First name = " + capitalizeFirstLetterWord(fName) + ", Last name = " + 
				                             capitalizeFirstLetterWord(lName) + ", Profession = " + 
											 capitalizeFirstLetterWord(profession); 				  
				  
				  return result;
               }
			   
			   // Function definition, to capitalize first letter of 
			   // the supplied word.
			   function capitalizeFirstLetterWord(word) {
			       var result;				   
				   result = (word.charAt(0)).toUpperCase() + word.substring(1); 				   
				   return result;
			   }
            ]]>
        </xalan:script>
    </xalan:component>

    <xsl:template match="/">
       <result>   
		  <xsl:for-each select="document/person">
		     <xsl:variable name="xmlNode1" as="element(fn:map)">
			    <fn:map>
				   <fn:number key="id"><xsl:value-of select="@id"/></fn:number>
				   <fn:string key="fName"><xsl:value-of select="fName"/></fn:string>
				   <fn:string key="lName"><xsl:value-of select="lName"/></fn:string>
				   <fn:string key="profession"><xsl:value-of select="profession"/></fn:string>
				</fn:map>
			 </xsl:variable>
			 <person>
			    <xsl:value-of select="js0:processJsonStr(xml-to-json($xmlNode1))"/>
			 </person>
		  </xsl:for-each>
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