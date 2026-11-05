<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_e.xml -->
   
   <!-- An XSLT stylesheet, to test the XPath 3.1 "if" conditional 
        expression. The XPath "if" conditional expression example 
        illustrated within this stylesheet test, is borrowed from 
        XPath 3.1 spec. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/parts">
      <result>
         <xsl:call-template name="analyzePart">
            <xsl:with-param name="part" select="part[1]"/>
         </xsl:call-template>
         <xsl:call-template name="analyzePart">
	        <xsl:with-param name="part" select="part[2]"/>
         </xsl:call-template>
      </result>
   </xsl:template>
   
   <xsl:template name="analyzePart">
      <xsl:param name="part"/>
      
      <part>
         <xsl:copy-of select="$part/name"/>
         <xsl:copy-of select="if ($part/@discounted)
                                    then $part/wholesale
                                    else $part/retail"/>
      </part>
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