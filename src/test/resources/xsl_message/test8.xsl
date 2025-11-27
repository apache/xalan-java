<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                 
                version="3.0">
                
  <!-- Author: mukulg@apache.org -->
   
  <!-- An XSL 3 stylesheet test case, to test xsl:message 
       instruction, and using 'expand-text' attribute 
       on xsl:message. -->                
				
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:variable name="products">
     <product>
	    <name>P1</name>
		<price>2</price>
	 </product>
	 <product>
	    <name>P2</name>
		<price>3</price>
	 </product>
	 <product>
	    <name>P3</name>
		<price>4</price>
	 </product>
	 <product>
	    <name>P4</name>
		<price>5</price>
	 </product>
  </xsl:variable>

  <xsl:template match="/">
     <xsl:message>Starting XSL transformation ...</xsl:message>
     <result>	    
	    <xsl:apply-templates select="$products/product"/>
	 </result>
  </xsl:template>
  
  <xsl:template match="product">
     <xsl:variable name="productName" select="name"/>
     <xsl:variable name="productPrice" select="price"/>
     <xsl:message expand-text="yes">
        <xsl:text>Processing product: {$productName} with price: {$productPrice}</xsl:text>
     </xsl:message>
	 <product name="{$productName}" price="{$productPrice}"/>
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
