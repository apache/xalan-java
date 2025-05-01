<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"              
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test case, for the XPath 3.1 
        function fn:apply. -->                 

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
     <result>
	    <xsl:variable name="func1" select="function ($a) { $a + 2 }"/>
		<xsl:variable name="func2" select="function ($a,$b) { $a + $b }"/>				
		<one>
		   <xsl:value-of select="apply($func1,[2]) + apply($func2,[1,2])"/>
		</one>
		<two>
		   <!-- Within the below xsl:value-of instruction, the 2nd operand of an operator 
		        +, is a literal inline function expression. -->
		   <xsl:value-of select="apply($func1,[2]) + apply(function ($a,$b) { $a*$b + 2 },[1,2])"/>
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
