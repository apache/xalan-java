<xsl:stylesheet 
      xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>
      
  <xsl:param name="a-param">default param value
  </xsl:param>
  
  <xsl:template match="/">
    <out>
      <xsl:value-of select="$a-param"/>
      <xsl:apply-templates/>
    </out>
  </xsl:template>
      
  <xsl:template 
      match="@*|*|text()|processing-instruction()">
    <xsl:copy>
      <xsl:apply-templates 
         select="@*|*|text()|processing-instruction()"/>
    </xsl:copy>
  </xsl:template>
</xsl:stylesheet>