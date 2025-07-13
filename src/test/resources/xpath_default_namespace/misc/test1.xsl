<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
				xmlns:fn0="http://fn0" 
			    xpath-default-namespace="http://ns0"               
                exclude-result-prefixes="#all"			   
			    version="3.0">
			    
   <!-- Author: mukulg@apache.org -->			    
			   
   <!-- use with test1.xml-->
   
   <!-- An XSL stylesheet test case, to test XSL attribute 
        'xpath-default-namespace'. -->				   

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/">
      <result>
	     <one>
            <xsl:for-each select="info/item/subitem | info/str/substr">
		       <xsl:if test="self::subitem">
		         <elem name="{name(.)}" strLength="{fn0:getElemNodeStringLength1(.)}"/>
		      </xsl:if>
		    </xsl:for-each>
		 </one>
		 <two>
		    <xsl:for-each select="info/item/subitem | info/str/substr">
		       <xsl:if test="self::substr">
		         <elem name="{name(.)}" strLength="{fn0:getElemNodeStringLength2(.)}"/>
		       </xsl:if>
		    </xsl:for-each>
		 </two>
      </result>
   </xsl:template>

   <xsl:function name="fn0:getElemNodeStringLength1" as="xs:integer">
      <xsl:param name="p1" as="element()"/>
	  <xsl:sequence select="string-length($p1/self::subitem)"/>
   </xsl:function>
   
   <xsl:function name="fn0:getElemNodeStringLength2" as="xs:integer">
      <xsl:param name="p1" as="element()"/>
	  <xsl:sequence select="string-length($p1/self::substr)"/>
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
