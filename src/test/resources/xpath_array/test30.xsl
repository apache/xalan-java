<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="#all"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test XSL function 
         returning xdm arrays and verifying function's return 
         value with xsl:function's 'as' attribute.
          
         This XSL stylesheet also reads information, from an 
         external JSON document using fn:unparsed-text and 
         fn:parse-json functions. 
    -->                 				
				
    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="m1" select="parse-json(unparsed-text('test2.json'))" as="map(*)"/>	

	<xsl:template match="/">
	   <result>
		  <one>
		    <xsl:value-of select="fn0:func1($m1,'a1')"/>
		  </one>
		  <two>
		    <xsl:value-of select="fn0:func2($m1,'a2')"/>
		  </two>
		  <three>
		    <xsl:value-of select="fn0:func3($m1,'a3')"/>
		  </three>
		  <four>
		     <xsl:for-each select="map:keys($m1)">
			    <array>
				   <xsl:value-of select="fn0:func3($m1,.)"/>
				</array>
			 </xsl:for-each>
		  </four>
	   </result>
	</xsl:template>

    <xsl:function name="fn0:func1" as="array(xs:string)">
	   <xsl:param name="m1" as="map(*)"/>
	   <xsl:param name="k1" as="xs:string"/>
       <xsl:sequence select="map:get($m1,$k1)"/>
    </xsl:function>

    <xsl:function name="fn0:func2" as="array(xs:double)">
       <xsl:param name="m1" as="map(*)"/>
	   <xsl:param name="k1" as="xs:string"/>
       <xsl:sequence select="map:get($m1,$k1)"/>
    </xsl:function>

    <xsl:function name="fn0:func3" as="array(*)">
       <xsl:param name="m1" as="map(*)"/>
	   <xsl:param name="k1" as="xs:string"/>
       <xsl:sequence select="map:get($m1,$k1)"/>
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
