<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"				
                version="3.0">
				
    <!-- use with test1.xml -->				
				
    <xsl:output method="xml" indent="yes"/>				

	<xsl:include href="include1.xsl"/>

	<xsl:template match="/doc">
	   <result>
	      <xsl:apply-templates select="*"/>
	   </result>
	</xsl:template>
	
	<xsl:template match="b">
	   <b id="1">hello</b>
	</xsl:template>

</xsl:stylesheet>
