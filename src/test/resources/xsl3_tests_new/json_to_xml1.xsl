<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:fn="http://www.w3.org/2005/xpath-functions"
				exclude-result-prefixes="xs fn"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->                
			 
    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="jsonStr1" as="xs:string">
       {
	      "a" : 1,
		  "b" : 2,
		  "c" : 3
	   }
    </xsl:variable>

    <xsl:variable name="xmlNodeSet1" select="json-to-xml(normalize-space($jsonStr1))" as="element(fn:map)"/>
    
	<!-- An XSL stylesheet initial template -->
    <xsl:template name="Template1" visibility="public">
	   <result>
	      <one>
	         <xsl:call-template name="Template2">
		        <xsl:with-param name="nodeSet1" select="$xmlNodeSet1" as="element(fn:map)"/>
		     </xsl:call-template>
		  </one>
		  <two>
		     <xsl:variable name="nodeSetVar1" as="element(fn:map)">
				 <xsl:call-template name="Template2">
					<xsl:with-param name="nodeSet1" select="$xmlNodeSet1" as="element(fn:map)"/>
				 </xsl:call-template>
			 </xsl:variable>
			 <xsl:copy-of select="$nodeSetVar1"/>
		  </two>
	   </result>
	</xsl:template>
	
	<xsl:template name="Template2">
	   <xsl:param name="nodeSet1" as="element(fn:map)"/>
	   <xsl:copy-of select="$nodeSet1"/>
	</xsl:template>
    
</xsl:stylesheet>