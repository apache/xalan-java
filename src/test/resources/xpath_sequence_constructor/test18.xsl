<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="math"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSLT stylesheet test case, where XPath 3.1 built-in function calls 
        have literal sequence arguments.        
   -->                                           

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
	   <result>
	     <xsl:variable name="p" select="math:sin(1)"/>
	     <xsl:variable name="q" select="math:sin(2)"/>
	     <xsl:variable name="r" select="math:sin(3)"/>
	     <one>	       	       
           <xsl:value-of select="for-each(($p,$q,$r), function($x) { 5 * $x })"/>
	     </one>
	     <two>	       
           <xsl:value-of select="for-each(($p,$q,$r,4,5), function($x) { 5 * $x })"/>
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
