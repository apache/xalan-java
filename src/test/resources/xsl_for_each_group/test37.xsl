<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"
               exclude-result-prefixes="xs"
               version="3.0">
               
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test37.xml -->               
  
  <!-- An XSL 3 stylesheet test case, to do grouping using variable-length 
       composite keys.
	   
       This XSL stylesheet test case, solves same use case as W3C XSLT 3.0 test 
       case for-each-group-069, but using a different XPath expression for 
       xsl:for-each-group's attribute 'group-by'. 	   
  -->
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <result>
	    <xsl:for-each-group select="//*" 	                        
							group-by="for $x in ancestor-or-self::* return name($x)"
	                        composite="yes">
	       <element path="{string-join(current-grouping-key(), '/')}" count="{count(current-group())}"/>
	    </xsl:for-each-group>
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
