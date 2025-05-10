<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">				

   <xsl:output method="xml" indent="yes"/>   
   
   <xsl:template name="main">
      <result>
	    <xsl:for-each select="(1, 2, 3, 4, 5)">
		  <value>
		    <xsl:value-of select="."/>
		  </value>
		</xsl:for-each>
	  </result>
   </xsl:template>
   
</xsl:stylesheet>
