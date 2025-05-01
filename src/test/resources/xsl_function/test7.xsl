<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:fn1="http://example.com/namespace"
                version="3.0"
                exclude-result-prefixes="xs fn1">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1_b.xml -->
    
    <!-- An XSLT stylesheet test case, to test a stylesheet
         function defined with an XSL element xsl:function.
    -->                 
                
	<xsl:output method="xml" indent="yes"/>
	
	<xsl:template match="/info">
	   <result>
	      <one>
	         <xsl:value-of select="fn1:shallow-equals(elem1, elem2)"/>
	      </one>
	      <two>
	         <xsl:value-of select="fn1:shallow-equals(elem1, elem3)"/>
	      </two>
	   </result>
	</xsl:template>
	
	<!-- A function, that checks whether two XML nodes passed as arguments
	     to it are shallow equal. This function, checks for the equality of
	     two node sets that are XML element children of the respective
	     function arguments. -->
	<xsl:function name="fn1:shallow-equals" as="xs:boolean">
	   <xsl:param name="node1" as="element()"/>
	   <xsl:param name="node2" as="element()"/>
	   
	   <xsl:variable name="isNodeListsSizeEqual" select="count($node1/*) eq count($node2/*)"/>
	   <xsl:choose>
	      <xsl:when test="$isNodeListsSizeEqual">
	         <xsl:variable name="temp1" as="element(val)*">
	            <xsl:for-each select="$node1/*">
	              <xsl:variable name="pos" select="position()"/>
	              <val><xsl:value-of select="name(.) eq name($node2/*[$pos])"/></val>
	            </xsl:for-each>
	         </xsl:variable>
	         <xsl:value-of select="every $val in $temp1 satisfies (xs:boolean(string($val)) eq xs:boolean('true'))"/>
	      </xsl:when>
	      <xsl:otherwise>
	         <xsl:value-of select="xs:boolean('false')"/>
	      </xsl:otherwise>
	   </xsl:choose>   
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