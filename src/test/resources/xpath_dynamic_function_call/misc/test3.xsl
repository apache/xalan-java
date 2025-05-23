<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- An XSL stylesheet test case, to test XPath 3.1 dynamic 
       function calls. In this XSL stylesheet example, a function call 
       returns a function item (an XPath 'schema type' constructor 
       function as an XPath named function reference) which is 
       subsequently called. -->                  
  
  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <result>
	   <xsl:variable name="func3" select="function($p) {if ($p eq true()) then xs:date#1 else xs:time#1}"/>
	   <one>
	      <xsl:value-of select="let $date1 := $func3(true())('2010-10-05') return year-from-date($date1)"/>
	   </one>
	   <two>
	      <xsl:value-of select="let $time1 := $func3(false())('13:10:00Z') return minutes-from-time($time1)"/>
	   </two>
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
