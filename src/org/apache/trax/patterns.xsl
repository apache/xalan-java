<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:output method="html"/>

<xsl:template match="patterns">
  <HTML>
    <TITLE>Design Patterns: <xsl:value-of select="@module"/></TITLE>
    <BODY>
    <H1>Design Patterns: <xsl:value-of select="@module"/></H1>
    <img src="trax.gif"/>
    <xsl:for-each select="pattern">
      <HR/>
        <xsl:for-each select="pattern-name">
          <H2><xsl:value-of select="."/></H2>
        </xsl:for-each>
        <DL>
          <xsl:apply-templates select="*"/>
        </DL>
    </xsl:for-each>
    </BODY>
  </HTML>
</xsl:template>
  
<xsl:template match="pattern-name"/>

<xsl:template match="intent">
  <DT>Intent</DT>
  <DD><xsl:value-of select="."/></DD>
</xsl:template>

<xsl:template match="responsibilities">
  <DT>Responsibilities</DT>
  <DD><xsl:value-of select="."/></DD>
</xsl:template>

<xsl:template match="thread-safety">
  <DT>Thread-safety</DT>
  <DD><xsl:value-of select="."/></DD>
</xsl:template>

<xsl:template match="*">
  <DT><xsl:value-of select="name(.)"/></DT>
  <DD><xsl:value-of select="."/></DD>
</xsl:template>

</xsl:stylesheet>
