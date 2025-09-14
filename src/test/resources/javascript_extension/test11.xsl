<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	            xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:js0="http://js0"
	            exclude-result-prefixes="#all"
				version="3.0">
 				
    <!-- Author: mukulg@apache.org -->				

    <!-- use with test5.xml -->
    
    <!-- An XSL stylesheet test case, to test Xalan-J's interface 
         with JavaScript extension functions. -->				
				
    <xsl:output method="xml" indent="yes"/>				

    <xalan:component prefix="js0">
        <xalan:script lang="javascript">
            <![CDATA[				
			   function processXmlDocument(xmlDoc) {
                  var result = "";

                  const nodeList = xmlDoc.getElementsByTagName("word");
                  const wordCount = nodeList.getLength();
                  for (var idx = 0; idx < wordCount; idx++) {
				     var elem1 = nodeList.item(idx);                     
					 var id = elem1.getAttribute("id");
					 var word = elem1.textContent;
					 if (idx < (wordCount - 1)) {
					    result = result + "[" + id + " : " + word + "], ";
					 }
					 else {
					    result = result + "[" + id + " : " + word + "]";
					 }
                  }				  
				  
                  return result;                  
               }
            ]]>
        </xalan:script>
    </xalan:component>

    <xsl:template match="/">
       <result>
		  <xsl:variable name="result1" select="js0:processXmlDocument(/document)"/>
		  <xsl:value-of select="$result1"/>
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