<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with xml4.xml -->                 

    <xsl:output method="xml" indent="yes"/>

    <!-- A variable referring to an, XPath inline function expression -->
    <xsl:variable name="constructGrpResultWithDelims" select="function($prefixDelimElemName as xs:string,
                                                                       $suffixDelimElemName as xs:string,
                                                                       $valElems as element(val)*) as element()*
                                                                   { (fn0:constructDelimElem($prefixDelimElemName),
                                                                      fn0:groupValElemsByPriority($valElems),
                                                                      fn0:constructDelimElem($suffixDelimElemName))
                                                                   }"/>

    <xsl:template match="/info">
      <result>
        <xsl:copy-of select="$constructGrpResultWithDelims('groupsStart', 'groupsEnd', val)"/>
      </result>
    </xsl:template>

    <!-- A stylesheet function, that constructs an XML element given an element's name. -->
    <xsl:function name="fn0:constructDelimElem" as="element()">
      <xsl:param name="delimElemName" as="xs:string"/>
      <xsl:element name="{$delimElemName}"/>
    </xsl:function>

    <!-- A stylesheet function, that groups (via xsl:for-each-group instruction) a sequence of
         XML 'val' elements by the specified grouping criteria (via xsl:for-each-group
         instruction's "group-by" attribute).
    -->
    <xsl:function name="fn0:groupValElemsByPriority" as="element(valElems)*">
      <xsl:param name="valElems" as="element(val)*"/>
      <xsl:for-each-group select="$valElems" group-by="xs:integer(@priority)">
        <valElems priority="{current-grouping-key()}">
          <xsl:for-each select="current-group()">
            <val a="{@a}" b="{@b}"/>
          </xsl:for-each>
        </valElems>
      </xsl:for-each-group>
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