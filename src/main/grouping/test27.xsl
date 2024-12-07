<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- use with test1_i.xml -->
  
  <!-- An XSLT stylesheet to test, xsl:for-each-group instruction. Within this
       stylesheet example, from an XML input document, "part" elements are
       grouped as per "part" element's expiryDate xs:date value.   
  -->                
                
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/parts">     
     <RESULT>
        <xsl:for-each-group select="part" group-by="xs:date(expiryDate)">
           <PARTS expiryDate="{current-grouping-key()}">           
              <xsl:apply-templates select="current-group()"/>
           </PARTS>
        </xsl:for-each-group>
     </RESULT>
  </xsl:template>
  
  <xsl:template match="part">
    <part>
       <xsl:copy-of select="id | name"/>
    </part>
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