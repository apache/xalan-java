<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"   
			    version="3.0">
			    
   <!-- This XSL stylesheet example, is contributed by Martin Honnen. -->			    
			    
   <!-- Authors: mukulg@apache.org -->                              
				
   <!-- use with test11.xml -->
   
   <!-- An XSL 3 stylesheet test case, to test that an 
        xsl:template instruction with multiple mode names 
        as value of "mode" attribute works fine. -->			    

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:mode name="m1" on-no-match="shallow-skip"/>
   <xsl:mode name="m2" on-no-match="shallow-skip"/>

   <xsl:mode on-no-match="shallow-copy"/>

   <xsl:template match="/">
     <xsl:copy>
       <xsl:apply-templates/>       
     </xsl:copy>
   </xsl:template>

   <xsl:template match="/*">
     <xsl:copy>
       <mode-test name="m1">
         <xsl:apply-templates mode="m1"/>
       </mode-test>
       <mode-test name="m2">
         <xsl:apply-templates mode="m2"/>
       </mode-test>
     </xsl:copy>
   </xsl:template>

   <!-- An XSL template that is selected for more 
        than one mode. -->
   <xsl:template match="item" mode="m1 m2">
     <xsl:copy>
        <xsl:attribute name="info" select="."/>
	 </xsl:copy>
   </xsl:template>
   
   <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the "License");
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