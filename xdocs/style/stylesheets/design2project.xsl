<?xml version="1.0"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <!-- match the root book element -->
  <xsl:template match="book">
    <project>
      <parameter name="copyright" value="{@copyright}"/>
      <xsl:apply-templates/>
    </project>
  </xsl:template>

 <xsl:template match="document">

    <process source="{@source}" producer="parser">
      <processor name="xslt">
        <parameter name="stylesheet" value="sbk:/style/stylesheets/any2project.xsl"/>
      </processor>
    </process>

    <create source="{@source}" target="{@id}.html" producer="parser" printer="html">
      <processor name="xslt">
        <parameter name="id" value="{@id}"/>
        <parameter name="stylesheet" value="sbk:/style/stylesheets/designdoc2html.xsl"/>
      </processor>
    </create>
  </xsl:template>
  
</xsl:stylesheet>