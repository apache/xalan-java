<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
				xmlns:fn0="http://fn0"               
                exclude-result-prefixes="#all"			   
			    version="3.0">
			    
   <!-- Author: mukulg@apache.org -->			    
			   
   <!-- use with test2.xml-->
   
   <!-- An XSL stylesheet test case, to test XSL attribute 
        'xpath-default-namespace'. With this XSL stylesheet example, 
        we test consuming XML document input information where XML 
        input elements are in two different namespaces. -->				   

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/info">
      <result>
	     <one xsl:xpath-default-namespace="http://ns0">
		    <xsl:variable name="var1" select="one" as="element()"/>
            <xsl:for-each select="$var1/item/subitem">
		       <elem name="{name(.)}" strLength="{fn0:getElemNodeStringLength(.)}"/>
		    </xsl:for-each>
		 </one>
		 <two xsl:xpath-default-namespace="http://ns1">
		    <xsl:variable name="var2" select="two" as="element()"/>
		    <xsl:for-each select="$var2/str/substr">
		       <elem name="{name(.)}" strLength="{fn0:getElemNodeStringLength(.)}"/>
		    </xsl:for-each>
		 </two>
      </result>
   </xsl:template>

   <xsl:function name="fn0:getElemNodeStringLength" as="xs:integer">
      <xsl:param name="p1" as="element()"/>
	  <xsl:sequence select="string-length($p1)"/>
   </xsl:function>
   
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
