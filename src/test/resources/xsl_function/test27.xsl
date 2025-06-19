<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:ns0="http://ns0"                
               version="3.0">
               
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with doc1.xml -->
   
   <!-- An XSL stylesheet test case, to test constructing an
        XML attribute node from within an XSL function. -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:function name="ns0:constructAttrNode" as="attribute(ns0:a1)">
	  <xsl:attribute name="ns0:a1">123</xsl:attribute>
   </xsl:function>

   <xsl:template match="/doc">
      <result>
	     <xsl:variable name="attrNode1" select="ns0:constructAttrNode()"/>
		 <one>
		    <xsl:value-of select="$attrNode1 instance of attribute(ns0:a1)"/>
		 </one>
		 <two>
		    <xsl:value-of select="$attrNode1 instance of attribute(a1)"/>
		 </two>
         <three>
		   <xsl:copy-of select="ns0:constructAttrNode()"/>
		   hello
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
   
</xsl:transform>
