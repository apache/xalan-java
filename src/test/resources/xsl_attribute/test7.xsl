<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1_b.xml -->
    
    <!-- An XSLT stylesheet test case, to test xsl:attribute 
         instruction having 'select' attribute, along with 
         use of few other XSLT instructions. -->                             

    <xsl:output method="xml" indent="yes"/>
    
    <!-- An integer valued variable, that is used by an XSL
         template declaration [xsl:template match="c"] 
         specified within this stylesheet.
    -->
    <xsl:variable name="c1NodeAddCount" select="3" as="xs:integer"/>

    <!-- An XSL identity template -->
    <xsl:template match="node() | @*">
       <xsl:copy>
         <xsl:apply-templates select="node() | @*"/>
       </xsl:copy>
    </xsl:template>
    
    <!-- This XSL template transforms, an XML input document
         element named "a" by adding an attribute "trfVal"
         to an element "a", and also transforming XML
         element "a"'s child contents.
    -->
    <xsl:template match="a">
      <a>
        <xsl:copy-of select="@*"/>
        <xsl:attribute name="trfVal" select="fn0:trfAttrSeq(@*)"/>
        <xsl:apply-templates/>
      </a>
    </xsl:template>
    
    <!-- This XSL template does a specific transformation,
         of an XML input document element named "c".
    -->
    <xsl:template match="c">
      <xsl:variable name="depth" select="fn0:getElemDepth(.)"/>
      <c depth="{$depth}" mesg="{.}">
        <xsl:for-each select="1 to $c1NodeAddCount">
          <c1 depth="{$depth + 1}" seq="{.}"/>
        </xsl:for-each>
      </c>
    </xsl:template>
    
    <!-- This stylesheet function, accepts an XML element node
         as argument, and returns an integer valued depth of
         that element wrt an XML input document's top most 
         element.
    -->
    <xsl:function name="fn0:getElemDepth" as="xs:integer">
      <xsl:param name="elemNode" as="element()"/>
      <xsl:sequence select="count($elemNode/ancestor-or-self::*)"/>
    </xsl:function>
    
    <!-- This stylesheet function, accepts a sequence of attribute
         nodes as argument, and returns the sum of square of their 
         integer values.
    -->
    <xsl:function name="fn0:trfAttrSeq" as="xs:integer">
      <xsl:param name="attrSeq" as="attribute()*"/>
      <xsl:sequence select="sum(for $attr in $attrSeq return 
                                                (xs:integer($attr) * xs:integer($attr)))"/>
    </xsl:function>
    
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
