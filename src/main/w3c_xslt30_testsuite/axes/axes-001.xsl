<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">
    <!-- PURPOSE: Test for 'ancestor::' Axis Identifier. -->
    
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
        <out>
          <xsl:for-each select="//center">
            <xsl:apply-templates select="ancestor::*"/>
          </xsl:for-each>
  	  </out>
  </xsl:template>
    
  <xsl:template match="*">
     <xsl:value-of select="name(.)"/><xsl:text> </xsl:text>
  </xsl:template>
 
</xsl:stylesheet>
