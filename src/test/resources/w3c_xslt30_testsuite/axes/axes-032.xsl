<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">
	
<?spec xpath#axes?>
  <!-- PURPOSE: Test for 'child::' Axis Identifier with wildcard. -->
<xsl:template match="/">
   <out>
	  <xsl:for-each select="//center">
        <xsl:apply-templates select="child::*"/>
      </xsl:for-each>
   </out>
</xsl:template>
  
<xsl:template match="*">
   <xsl:value-of select="name(.)"/><xsl:text> </xsl:text>
</xsl:template>
 
</xsl:stylesheet>
