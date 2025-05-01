<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSLT 3.0 test case, where we pass a function item as
       an argument with a dynamic function call.
       We also test an XPath, sequence type expression which is
       a FunctionTest. 
  -->               
                
  <xsl:output method="xml" indent="yes"/>

  <!-- An variable referring to, an XPath inline function expression
       whose function item shall calculate square of a numeric value.
  -->
  <xsl:variable name="sqr1" select="function($a as xs:integer) as xs:integer { $a * $a }"/>
  
  <!-- An variable referring to, an XPath inline function expression,
       whose function body evaluates a specific XPath expression using
       argument information of the function call. 
  -->
  <xsl:variable name="calc1" select="function($sqr1 as function(*), $val1 as xs:integer) 
                                                               as xs:integer { $sqr1($val1) * 2 }"/>    

  <xsl:template match="/">     
    <result>
      <xsl:value-of select="$calc1($sqr1, 5)"/>
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