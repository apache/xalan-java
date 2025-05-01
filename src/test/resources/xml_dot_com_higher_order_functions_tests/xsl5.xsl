<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn1="http://fn1"
                exclude-result-prefixes="xs fn1"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with xml3.xml -->                 

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/info">
       <result>
         <res1>
           <xsl:value-of select="fn1:binaryOp(function($a as xs:integer, $b as xs:integer) as xs:integer { $a + $b }, val1, val2)"/>
         </res1>
         <res2>
           <xsl:value-of select="fn1:binaryOp(function($a as xs:integer, $b as xs:integer) as xs:integer { $a - $b }, val1, val2)"/>
         </res2>
         <res3>
           <xsl:value-of select="fn1:binaryOp(function($a as xs:integer, $b as xs:integer) as xs:integer { $a * $b }, val1, val2)"/>
         </res3>
       </result>
    </xsl:template>

    <!-- A stylesheet function (that works as a generic logical binary operator implemented by this stylesheet function), that has
         three parameters. The first parameter is a function item (that accepts two integer arguments, and returns an integer result).
         The other two parameters of this stylesheet function are integer paramaters, that're provided as arguments to the function
         item parameter.
    -->
    <xsl:function name="fn1:binaryOp" as="xs:integer">
      <xsl:param name="f1" as="function(xs:integer, xs:integer) as xs:integer"/>
      <xsl:param name="a" as="xs:integer"/>
      <xsl:param name="b" as="xs:integer"/>
      <xsl:sequence select="$f1($a, $b)"/>
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