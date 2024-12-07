<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_f.xml -->
   
   <!-- An XSLT stylesheet, to test xsl:iterate instruction, when xsl:iterate's 
        select attribute evaluates to a sequence of atomic values. -->                

   <xsl:output method="xml" indent="yes"/>
   
   <!-- A variable, referring to an XPath sequence constructor having various 
        function item XPath expressions. -->
   <xsl:variable name="fnItemsSeq" select="(function($a) { $a + 2 }, function($a) { $a - 3 }, 
                                               function($a) { $a * 4 }, function($a) { $a div 4 })"/>
                                               
   <xsl:variable name="num" select="7"/>                                               
      
   <xsl:template match="/">
      <result>
         <xsl:iterate select="$fnItemsSeq">
            <xsl:variable name="fnItem" select="."/>
            <!-- Make a dynamic function call to respective function
                 item, and pass an argument as well to function call. -->
            <val><xsl:value-of select="$fnItem($num)"/></val>
         </xsl:iterate>
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