<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->              
   
   <!-- An XSLT stylesheet test case, to test the evaluation of 
        an XPath 3.1 "let" expression, along with XPath inline
        function expression and dynamic function call. -->                  
    
    <xsl:output method="xml" indent="yes"/>
    
    <!-- A user-defined inline function expression, to check whether a function argument value
         is an instance of type xs:integer. -->
    <xsl:variable name="fIsValXsInteger" select="function($val) { $val instance of xs:integer }"/>
    
    <!-- A user-defined inline function expression, to check whether a function argument value
         is an instance of type xs:string. -->
    <xsl:variable name="fIsValXsString" select="function($val) { $val instance of xs:string }"/>
    
    <!-- A user-defined inline function expression, to do boolean 'and' operation on two 
         boolean argument values. -->
    <xsl:variable name="fnAnd" select="function($val1, $val2) { let $result1 := $val1, 
                                                                    $result2 := $val2 
                                                                          return ($result1 and $result2) }"/>
    
    <xsl:template match="/">       
       <result>
          <one>
             <xsl:value-of select="let $result1 := $fIsValXsInteger('5a'), 
                                       $result2 := $fIsValXsString('hi') 
                                                                return ($result1 and $result2)"/>
          </one>
          <two>
             <xsl:value-of select="$fnAnd($fIsValXsInteger('5a'), $fIsValXsString('hi'))"/>
          </two>
          <three>
             <xsl:variable name="var1" select="7"/>
             <xsl:variable name="var2" select="'hi'"/>
	         <xsl:value-of select="$fnAnd($fIsValXsInteger($var1), $fIsValXsString($var2))"/>
          </three>
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