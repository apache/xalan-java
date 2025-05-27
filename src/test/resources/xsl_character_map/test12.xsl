<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">               
  
  <!-- use with test12.xml -->
  
  <!-- An XSL stylesheet test case to test, W3C XSLT 3.0 test character-map-001 -->                    

  <xsl:character-map name="map01">
    <xsl:output-character character="c" string="[C]"/>
    <xsl:output-character character="x" string="[X]"/>
  </xsl:character-map>
  
  <xsl:output method="xml" use-character-maps="map01"/>

  <xsl:template match="/">
    <xsl:copy-of select="."/>
  </xsl:template>

</xsl:stylesheet>