<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">

   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- Test for the XPath 3.1 fn:for-each() function.
        Within this stylesheet, we use a function item variable
        reference as an argument to the fn:for-each() function 
        call. We use the, same function item variable reference,
        on more than one fn:for-each() function call.   
   -->
   
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="func1" select="function($x) { $x + 3 }"/>

   <xsl:template match="/elem">
      <result>
        <result1>        
           <xsl:for-each select="for-each(a, $func1)">
              <num><xsl:value-of select="."/></num>
           </xsl:for-each>
        </result1>
        <result2>        
	       <xsl:for-each select="for-each(a, $func1)">
	          <num><xsl:value-of select="."/></num>
	       </xsl:for-each>
        </result2>
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