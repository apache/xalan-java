<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- An XSL 3 stylesheet test case, to test XPath 3.1 function fn:sort 
        using an explicit argument for sort key which is a function definition, 
        to sort sequence of input items by xs:gYearMonth values. Both ascending 
        and descending sort orders are tested by this XSL stylesheet.
   -->                
                
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="nodeSet1">
	  <item>
	    <id>1</id>
	    <yrMonth>2005-10</yrMonth>
	  </item>
	  <item>
	    <id>2</id>
	    <yrMonth>2005-03</yrMonth>
	  </item>
	  <item>
	    <id>3</id>
	    <yrMonth>2005-07</yrMonth>
	  </item>
   </xsl:variable>
   
   <xsl:template match="/">
      <result>
        <items sortedOrder="ascending_by_yrMonth">
           <xsl:copy-of select="sort($nodeSet1/item, (), function($item) {xs:gYearMonth($item/yrMonth)})"/>
        </items>
		<items sortedOrder="descending_by_yrMonth">
           <xsl:copy-of select="reverse(sort($nodeSet1/item, (), function($item) {xs:gYearMonth($item/yrMonth)}))"/>
        </items>
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