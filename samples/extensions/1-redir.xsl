<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0"
    xmlns:lxslt="http://xml.apache.org/xslt"
    xmlns:redirect="org.apache.xalan.lib.Redirect"
    extension-element-prefixes="redirect">

  <lxslt:component prefix="redirect" elements="write open close" functions="">
    <lxslt:script lang="javaclass" src="org.apache.xalan.lib.Redirect"/>
  </lxslt:component>  
    
  <xsl:template match="/">
    <standard-out>
      Standard output:
      <xsl:apply-templates/>
    </standard-out>
  </xsl:template>

  <!-- not redirected -->
  <xsl:template match="doc/main">
    <main>
    -- look in <xsl:value-of select="/doc/foo/@file"/> for the redirected output --
      <xsl:apply-templates/>
    </main>
  </xsl:template>
  
  <!-- redirected -->
  <xsl:template match="doc/foo">
    <!-- get redirect file name from XML input -->
    <redirect:write select="@file">
      <foo-out>
        <xsl:apply-templates/>
      </foo-out>
    </redirect:write>
  </xsl:template>
  
<!-- redirected (from the xsl:apply-templates above. I.e., bar is in /doc/foo -->  
  <xsl:template match="bar">
    <foobar-out>
      <xsl:apply-templates/>
    </foobar-out>
  </xsl:template>
  
</xsl:stylesheet>
