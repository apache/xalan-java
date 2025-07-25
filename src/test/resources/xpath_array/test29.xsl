<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="#all"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3 stylesheet test case, to test XSL function 
         returning xdm arrays and verifying function's return 
         value with xsl:function's 'as' attribute. -->                				
				
    <xsl:output method="xml" indent="yes"/>				

	<xsl:template match="/">
	   <result>
		  <one>
		    <xsl:value-of select="fn0:func1()"/>
		  </one>
		  <two>
		    <xsl:value-of select="fn0:func2()"/>
		  </two>
		  <three>
		    <xsl:value-of select="fn0:func3()"/>
		  </three>
	   </result>
	</xsl:template>

    <xsl:function name="fn0:func1" as="array(xs:string)">
       <xsl:sequence select="['a','b','c']"/>
    </xsl:function>

    <xsl:function name="fn0:func2" as="array(xs:integer)">
       <xsl:sequence select="[1,2,3]"/>
    </xsl:function>

    <xsl:function name="fn0:func3" as="array(*)">
       <xsl:sequence select="['a',1,1.5]"/>
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
