<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">

   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_c.xml -->
   
   <!-- Test for the XPath 3.1 fn:for-each() function. Within this 
        stylesheet example, we're passing XML element node with a sub 
        structure, as an argument to an XPath function item. The
        function item's XPath expression also refers to a stylesheet
        variable defined with XSLT xsl:variable instruction. -->
   
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="id" select="'xslt'"/>

   <xsl:template match="/elem">
      <result>
        <trfItems>
	       <xsl:for-each select="for-each(item, function($item) { concat($id, ' : ', $item/a, '_', $item/b) })">
	          <item><xsl:value-of select="."/></item>
	       </xsl:for-each>
        </trfItems>
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