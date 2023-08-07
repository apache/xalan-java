<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet, to test XPath 3.1 simple map 
        operator '!'.
        
        The XPath expression having simple map operator '!',
        as used within this stylesheet example, is borrowed
        from XPath 3.1 spec.       
   -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="n1" select="5"/>
      
   <xsl:template match="/">
      <xsl:variable name="n2" select="7"/>
      <result>
         <!-- returns a string containing 7 asterisks. -->
         <val1><xsl:value-of select="string-join((1 to 7)!'*')"/></val1>
         
         <!-- returns a string containing $n1 asterisks. -->
         <val2><xsl:value-of select="string-join((1 to $n1)!'*')"/></val2>
         
         <!-- returns a string containing $n2 asterisks. -->
         <val3><xsl:value-of select="string-join((1 to $n2)!'*')"/></val3>
         
         <!-- store the result of, simple map operation '!' into a variable,
              and use the variable's value subsequently. -->
         <xsl:variable name="asteriskList" select="string-join((1 to $n2)!'*')"/>
         <val4><xsl:value-of select="$asteriskList"/></val4>         
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