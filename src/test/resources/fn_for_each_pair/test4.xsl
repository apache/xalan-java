<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"               
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT test case to test, an XPath 3.1 function 
        call fn:for-each-pair, where the 1st two arguments of
        this function are XPath literal 'for' expressions and
        3rd argument of this function call is a literal inline 
        function expression. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/">
     <result>
	    <xsl:value-of select="for-each-pair(for $x in (1,2,3) return $x, for $x in (4,5,6) return $x, function($a,$b) {$a+$b})"/>
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
