<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_f.xml -->
   
   <!-- An XSLT stylesheet test, to test the XPath 3.1 sequence construction
        using comma operator.
        
        This stylesheet, also performs XML data grouping on the sequence
        formed, using xsl:for-each-group instruction. 
   -->                             

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/document">
      <document>       
         <data1>
            <xsl:for-each-group select="(person[2], person[3], person[4], person[5])" group-by="profession">
               <group grpKey="{current-grouping-key()}">
                  <xsl:copy-of select="current-group()"/>
               </group>
            </xsl:for-each-group>
         </data1>
         <data2>
            <!-- perform the XML data grouping again, on an xdm sequence having same collection
                 of node references as before. -->
	        <xsl:for-each-group select="(person[2], person[3], person[4], person[5])" group-by="profession">
	           <group grpKey="{current-grouping-key()}">
	              <xsl:copy-of select="current-group()"/>
	           </group>
            </xsl:for-each-group>
         </data2>
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