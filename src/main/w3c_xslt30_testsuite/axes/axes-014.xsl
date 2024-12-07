<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="3.0">

<?spec xpath#axes?>
  <!-- PURPOSE: Test for 'descendant-or-self::' Axis Identifier with self specified. -->
<xsl:template match="/">
   <out>
	  <xsl:for-each select="//center">
        <xsl:apply-templates select="descendant-or-self::center"/>
      </xsl:for-each>
   </out>
</xsl:template>
  
<xsl:template match="*">
   <xsl:value-of select="name(.)"/>
</xsl:template>
 
</xsl:stylesheet>
