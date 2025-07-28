<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				xmlns:fn0="http://fn0"
				exclude-result-prefixes="#all"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL stylesheet test case, to test an XPath 3.1 fn:parse-json 
         function call. -->                  				
				
    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="m1" select="parse-json(unparsed-text('item_doc1.json'))" as="map(xs:string, map(*))"/>

	<xsl:template match="/">
	   <result>
		   <xsl:for-each select="sort(map:keys($m1))">
			  <xsl:variable name="itemMap1" select="fn0:getValidatedItemMap(map:get($m1,.))" as="map(*)"/>			  
			  <object key="{.}">
				 <id>
				    <xsl:value-of select="map:get($itemMap1, 'id')"/>
				 </id>
				 <desc>
					<xsl:value-of select="map:get($itemMap1, 'desc')"/>
				 </desc>
				 <xsl:variable name="info" select="map:get($itemMap1, 'info')"/>
				 <info size="{map:get($info, 'size')}" weight="{map:get($info, 'weight')}" color="{map:get($info, 'color')}"/>
			  </object>
		   </xsl:for-each>
	   </result>
	</xsl:template>
	
	<xsl:function name="fn0:getValidatedItemMap" as="map(*)">
	   <xsl:param name="itemMap1" as="map(*)"/>
	   <xsl:variable name="id" select="map:get($itemMap1,'id')" as="xs:double"/>
	   <xsl:variable name="desc" select="map:get($itemMap1,'desc')" as="xs:string"/>
	   <xsl:variable name="info" select="map:get($itemMap1,'info')" as="map(xs:string, xs:anyAtomicType)"/>
	   <xsl:variable name="size" select="map:get($info,'size')" as="xs:double"/>
	   <xsl:variable name="weight" select="map:get($info,'weight')" as="xs:double"/>
	   <xsl:variable name="color" select="map:get($info,'color')" as="xs:string"/>
	   <xsl:sequence select="let $m1 := map {}, $m1 := map:put($m1,'id',$id), 
	                                            $m1 := map:put($m1,'desc',$desc),
								                $i1 := map {}, $i1 := map:put($i1,'size',$size),
											    $i1 := map:put($i1,'weight',$weight),
										        $i1 := map:put($i1,'color',$color),
											    $m1 := map:put($m1,'info',$i1) return $m1"/>
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
