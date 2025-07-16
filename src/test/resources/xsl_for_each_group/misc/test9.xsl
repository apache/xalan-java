<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xpath-default-namespace="http://ns0"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test2.xml -->
   
   <!-- An XSL stylesheet test case, to test xsl:for-each-group instruction 
        to group XML input information within a namespace, and using XSLT 
        attribute xpath-default-namespace. -->                

   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/body">
      <chapter>
         <xsl:for-each-group select="*" group-ending-with="h2">
           <xsl:variable name="currGrp" select="current-group()"/>
           <xsl:variable name="currGrpLastItem" select="$currGrp[count($currGrp)]"/>
           <section title="{$currGrpLastItem}">
      	     <xsl:for-each select="$currGrp[self::p]">
      	        <para><xsl:value-of select="."/></para>
      	     </xsl:for-each> 
           </section>
         </xsl:for-each-group>
      </chapter>
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