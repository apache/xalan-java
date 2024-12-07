<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_a.xml -->
   
   <!-- An XSLT stylesheet to test, XPath node comparison
        operators "is", "<<", ">>". -->                

   <xsl:output method="xml" indent="yes"/>
      
   <xsl:template match="/temp">
      <result>
        <val1><xsl:value-of select="a is a"/></val1>
        <val2><xsl:value-of select="b/@p is b/@p"/></val2>
        <val3><xsl:value-of select="a is b"/></val3>        
        <val4><xsl:value-of select="b/@p is b/@q"/></val4>
        <snip/>
        <val5><xsl:value-of select="a &lt;&lt; b"/></val5>
        <val6><xsl:value-of select="a &lt;&lt; c"/></val6>
        <val7><xsl:value-of select="b/@p &lt;&lt; a"/></val7>
        <val8><xsl:value-of select="b &lt;&lt; a"/></val8>
	    <val9><xsl:value-of select="c &lt;&lt; a"/></val9>
        <val10><xsl:value-of select="b/@p &lt;&lt; a"/></val10>
        <snip/>
        <val11><xsl:value-of select="b &gt;&gt; a"/></val11>
        <val12><xsl:value-of select="c &gt;&gt; a"/></val12>
        <val13><xsl:value-of select="b/@p &gt;&gt; a"/></val13>
        <val14><xsl:value-of select="a &gt;&gt; b"/></val14>
	    <val15><xsl:value-of select="a &gt;&gt; c"/></val15>
        <val16><xsl:value-of select="b/@p &gt;&gt; a"/></val16>
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