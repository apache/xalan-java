<?xml version="1.0"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:lxslt="http://xml.apache.org/xslt"
                xmlns:counter="MyCounter"
                extension-element-prefixes="counter"
                version="1.0">


  <lxslt:component prefix="counter"
                   elements="init incr" functions="read">
    <lxslt:script lang="javaclass" src="MyCounter"/>
  </lxslt:component>

  <xsl:template match="/">
    <HTML>
      <H1>Java Example</H1>
      <counter:init name="index" value="1"/>
      <p>Here are the names in alphabetical order by last name:</p>
      <xsl:for-each select="doc/name">
        <xsl:sort select="@last"/>
        <xsl:sort select="@first"/>
        <p>
        <xsl:text>[</xsl:text>
        <xsl:value-of select="counter:read('index')"/>
        <xsl:text>]. </xsl:text>
        <xsl:value-of select="@last"/>
        <xsl:text>, </xsl:text>
        <xsl:value-of select="@first"/>
        </p>
        <counter:incr name="index"/>
      </xsl:for-each>
    </HTML>
  </xsl:template>
 
</xsl:stylesheet>
