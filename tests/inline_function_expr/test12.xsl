<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- use with test1_e.xml -->
  
  <!-- An XSLT stylesheet test case, that tests an XPath 3.1 inline 
       function expression calling a stylesheet 'xsl:function' function.
       This stylesheet test case also illustrates that, an XPath inline
       function expression can produce XML complex content via xsl:function
       calls.
  -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <!-- This XPath inline function expression reference, concatenates results 
       of multiple stylesheet function calls. 
  -->
  <xsl:variable name="func1" select="function($nodeSet as element()*) as element()* { (fn0:GetElemA(), fn0:makeGroups($nodeSet), 
                                                                                                                    fn0:GetElemB()) }"/>
  
  <xsl:template match="/info">     
     <result>
        <xsl:copy-of select="$func1(elem)"/>   
     </result>
  </xsl:template>
  
  <xsl:function name="fn0:makeGroups" as="element()*">
    <xsl:param name="nodeSet" as="element()*"/>
    
    <xsl:for-each-group select="$nodeSet" group-by="xs:integer(@a)">
      <elems a="{current-grouping-key()}">
         <xsl:apply-templates select="current-group()"/>
      </elems>
    </xsl:for-each-group>
  </xsl:function>
  
  <xsl:function name="fn0:GetElemA" as="element()">
    <A/>
  </xsl:function>
  
  <xsl:function name="fn0:GetElemB" as="element()">
    <B/>
  </xsl:function>
  
  <xsl:template match="elem">
    <elem>
       <xsl:copy-of select="node()"/>
    </elem>
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