<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect">

  <xsl:template match="/">
    <standard-out>
      Standard output:
      <xsl:apply-templates/>
    </standard-out>
  </xsl:template>
  
  <xsl:template match="main">
    <main>
      <xsl:apply-templates/>
    </main>
  </xsl:template>
  
  <xsl:template match="/doc/foo">
    <redirect:write select="@file">
      <foo-out>
        <xsl:apply-templates/>
      </foo-out>
    </redirect:write>
  </xsl:template>
  
  <xsl:template match="bar">
    <foobar-out>
      <xsl:apply-templates/>
    </foobar-out>
  </xsl:template>
  
</xsl:stylesheet>
