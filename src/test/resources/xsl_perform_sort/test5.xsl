<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
  
  <!-- An XSL 3 stylesheet test case to test, xsl:perform-sort 
       instruction using composite sort key. -->                 
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="nodeSet1" as="element()*">
     <item>
        <id>4</id>
	    <color>red</color>
     </item>
     <item>
        <id>3</id>
	    <color>yellow</color>
     </item>
     <item>
        <id>2</id>
	    <color>blue</color>
     </item>
     <item>
        <id>1</id>
	    <color>red</color>
     </item>
     <item>
        <id>5</id>
	    <color>red</color>
     </item>	 
  </xsl:variable>
  
  <xsl:template match="/">
     <result>
        <xsl:perform-sort select="$nodeSet1"> 		
		   <xsl:sort select="color"/>	   
		   <xsl:sort select="id" data-type="number"/>
        </xsl:perform-sort>
     </result>
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
