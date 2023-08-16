<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_e.xml -->
   
   <!-- An XSLT stylesheet test, to test the XPath 3.1 sequence construction
        using comma operator. The XPath sequence constructor example,
        illustrated within this stylesheet is a heterogeneous sequence
        comprising of atomic values and XML elements. We traverse this
        sequence, using xsl:iterate instruction. -->                              

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/document">
      <document>
        <xsl:variable name="seq1" select="(10, 15, person[2], person[3])"/>

        <xsl:iterate select="$seq1">
           <info>
              <xsl:copy-of select="."/>
           </info>
        </xsl:iterate>
      </document>
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