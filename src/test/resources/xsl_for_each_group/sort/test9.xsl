<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn0="http://fn0"
                exclude-result-prefixes="xs fn0"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->                
                
  <!-- use with test1_d.xml -->
  
  <!-- An XSLT stylesheet, to test xsl:for-each-group instruction. -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/info">     
     <result>
       <xsl:for-each-group select="person" group-by="xs:integer(score) lt xs:integer(3)">
         <xsl:sort select="current-grouping-key()" order="descending"/>
         <group score="{fn0:trfGroupingKey(current-grouping-key())}">           
            <xsl:copy-of select="current-group()"/>
         </group>
       </xsl:for-each-group>
     </result>
  </xsl:template>
  
  <!-- A stylesheet function, to transform the grouping key value into a 
       string value, to display on output. --> 
  <xsl:function name="fn0:trfGroupingKey" as="xs:string">
    <xsl:param name="groupingKey"/>
    
    <xsl:sequence select="if (string($groupingKey) eq 'true') then 
                                                    xs:string('lt_three') else 
                                                    xs:string('gt_or_eq_three')"/>
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