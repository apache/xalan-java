<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
    
    <!-- An XSLT stylesheet test case, to test the sequence type
         declaration attribute "as" on an xsl:variable instruction.
         
         Within this stylesheet example, the named template 'Template1''s
         output is wrapped within an xsl:variable. The named template's
         output needs to conform to the sequence type specified as
         value of xsl:variable's "as" attribute.   
    -->                
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:variable name="varElemName" select="'a1'"/>
    <xsl:variable name="iterFrom" select="xs:integer(1)"/>
    <xsl:variable name="iterTo" select="xs:integer(5)"/>
    
    <xsl:template match="/">       
       <result>
          <xsl:variable name="var1" as="element(a1)*">
	         <xsl:call-template name="Template1">
	            <xsl:with-param name="elemName" select="$varElemName"/>
	            <xsl:with-param name="from" select="$iterFrom"/>
	            <xsl:with-param name="to" select="$iterTo"/>
	         </xsl:call-template>
	      </xsl:variable>
	      <xsl:copy-of select="$var1"/>
       </result> 
    </xsl:template>
    
    <xsl:template name="Template1">
       <xsl:param name="elemName"/>
       <xsl:param name="from"/>
       <xsl:param name="to"/>
       
       <xsl:for-each select="$from to $to">
          <xsl:element name="{$elemName}"/>
       </xsl:for-each>
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