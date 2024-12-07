<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_c.xml -->
   
   <!-- An XSLT stylesheet, to test the XPath 3.1 "if" conditional 
        expression, when used within xsl:for-each-group to transform
        the current-grouping-key() value. -->                 

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="gThan" select="function($x, $y) { $x gt $y }"/>

   <xsl:template match="/elem">
      <result>
        <xsl:for-each-group select="item" group-by="$gThan(a, b)">
           <xsl:variable name="grtOrLess" select="if (string(current-grouping-key()) eq 'true') 
                                                                  then 'greater' 
                                                                  else 'lessOrEqual'"/>
           <xsl:element name="{$grtOrLess}">
              <xsl:attribute name="count" select="count(current-group())"/>
              <xsl:copy-of select="current-group()"/>
           </xsl:element>
        </xsl:for-each-group>
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