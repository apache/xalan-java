<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- An XSLT stylesheet, to test XPath 3.1 simple map 
        operator '!'.
        
        The XPath expression having simple map operator '!',
        as used within this stylesheet example, is borrowed
        from XPath 3.1 spec, with slight modifications.
        
        The XPath expression example illustrated within this stylesheet
        using the simple map operator '!', returns an XPath path string 
        containing the names of the XML document ancestor elements of 
        the given XML element, separated by "/" characters.        
   -->                

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/temp">
      <result>         
        <xsl:variable name="var1" select="c/three/mesg"/>
        <xsl:value-of select="string-join($var1/ancestor::*!name(), '/')"/>                  
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