<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_c.xml -->                
   
   <!-- An XSLT stylesheet test, to test the evaluation of an 
        XPath 3.1 "for" expression. -->               

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="procInfo" select="function($info) { for $a in (substring($info/a, 1, 5) || ' ' || 'there'), 
                                                               $b in substring($info/b, 1, 3) 
                                                                            return $a || ', ' || $b || ' is fine' }"/>
   
   <xsl:template match="/temp">
      <result>
        <xsl:for-each select="info">
          <newInfo>
            <xsl:value-of select="$procInfo(.)"/>
          </newInfo>
        </xsl:for-each>
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