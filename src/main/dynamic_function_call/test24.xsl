<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="xs array"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT test case to test, passing literal array or sequence as 
       argument with XPath dynamic function call. -->                 

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
      <result>
	     <xsl:variable name="func1" select="function ($val as xs:integer, $arr as array(*)) as xs:integer { $val + array:size($arr) }"/>
	     <one>            
		    <xsl:value-of select="$func1(10,[1,2,3,4,5])"/>
		 </one>
		 <xsl:variable name="func2" select="function ($val as xs:integer, $seq as item()*) as xs:integer 
		                                                          { $val + count($seq) }"/>
	     <two>            
		    <xsl:value-of select="$func2(10,(1,2,3,4,5))"/>
		 </two>
      </result>
  </xsl:template>
  
  <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the  "License");
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