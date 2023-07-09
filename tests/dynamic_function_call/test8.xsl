<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_d.xml -->
   
   <!-- Test case description : Specifying complex valued arguments to 
        XPath dynamic function call (for e.g, within this XSLT stylesheet, 
        dynamic function call arguments are themselves function calls. -->                 

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="trfNameStr" select="function($pos, $str) { concat('Id : ', $pos, ', Name : ', $str) }"/>

   <xsl:template match="/list">
      <list>
         <xsl:for-each select="person">
           <person>
              <xsl:copy-of select="$trfNameStr(position(), concat(fName, ' ', lName))"/>
           </person>
         </xsl:for-each>
      </list>
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