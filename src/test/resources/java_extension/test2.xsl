<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
	            xmlns:xalan="xalan://org.apache.xalan.tests.util.XalanJavaExtension1"
	            exclude-result-prefixes="#all"
				version="3.0">
				
    <!-- Author: mukulg@apache.org -->				
    
    <!-- An XSL stylesheet test case, to test Xalan-J's interface 
         with Java extension function. -->								
				
    <xsl:output method="xml" indent="yes"/>				

    <xsl:template match="/">
       <result>
          <one>
		    <xsl:value-of select="xalan:squareSeqItems((1, 2, 3, 4, 5, 6, 7, 8, 9, 10))"/>
		  </one>
		  <two>
		    <xsl:value-of select="xalan:squareArrayItems([5, 6, 7, 8, 9, 10, 11, 12])"/>
		  </two>
		  <three>
		    <xsl:variable name="map1" select="xalan:processMap1(map {'a' : 1, 'b' : 2, 'c' : 3})" as="map(xs:string, xs:double)"/>
		    <map>
		       <xsl:for-each select="map:keys($map1)">
		          <entry key="{.}" value="{map:get($map1, .)}"/>
		       </xsl:for-each>
		    </map>
		  </three>
		  <four>
		     <xsl:value-of select="xalan:getAverage(&quot;{'a' : 1, 'b' : 2.5, 'c' : 3}&quot;, 3)"/>
		  </four>
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
