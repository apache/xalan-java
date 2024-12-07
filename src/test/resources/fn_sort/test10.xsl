<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_e.xml -->
   
   <!-- An XSLT stylesheet test case, to test XPath 3.1 fn:sort function,
        by reading input data from an XML external source document.
   
        This stylesheet example, first sorts an XML input element list using
        fn:sort function, and then reverses that produced sequence using a 
        dynamic function call to a function item, which provides us the
        final resulting information in descending order.
   -->                             

   <xsl:output method="xml" indent="yes"/>
   
   <!-- A variable, defining a function item, that reverses the order of an xdm 
        input sequence. -->
   <xsl:variable name="fnReverse" select="function($seq) { for $idx in (-1 * count($seq)) to -1 return $seq[abs($idx)]}"/>
   
   <xsl:template match="/document">
      <document>        
        <xsl:variable name="sortedPersonList" select="sort(person, (), function($person) { string($person/name) })"/>
        <xsl:iterate select="$fnReverse($sortedPersonList)">
           <xsl:apply-templates select="." mode="m1"/>
        </xsl:iterate>
      </document>
   </xsl:template>
   
   <!-- XSL transformation of XML person element, transforming an XML element 
        information to attribute. Using a 'mode' attribute allows us to
        unambiguously select this XSL template during transformation. -->
   <xsl:template match="person" mode="m1">
      <person id="{id}">
         <xsl:copy-of select="name"/>
      </person>
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