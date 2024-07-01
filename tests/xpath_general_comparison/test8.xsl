<?xml version='1.0' encoding='utf-8'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				version="3.0">
				
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL test case to test, XPath 3.1 general comparison 
       operator =. -->											
				
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match='/'>
     <result>
        <!-- user-defined function that does numerical square of the 
             function argument. -->
	    <xsl:variable name="func1" select="function($x) { $x*$x }"/>
		<one>
		   <xsl:value-of select="if (4 = ($func1(2),$func1(4))) then 'abc' else 'pqr'"/>
		</one>
		<two>
		   <xsl:value-of select="if (10 = ($func1(2),$func1(4))) then 'abc' else 'pqr'"/>
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
