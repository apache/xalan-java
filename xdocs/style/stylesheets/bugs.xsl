<?xml version="1.0" encoding="ISO-8859-1" ?>

<!DOCTYPE xsl:stylesheet>

<!-- XSL Style sheet, DTD omitted -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="xml"/>
  
  <xsl:param name="package-root" select="'../../../src/'"/>  <!-- root of destination for package.html files -->

  <xsl:template match="Sprs">
  <xsl:comment>This XML fragment contains a list of open bugs to be included in an &lt;s3&gt; section of readme.xml</xsl:comment>
  <p>Open bugs:</p>
    <ul>
    <xsl:for-each select="Spr[string(State)='Open'] [string(Subsystem)!='Other']">
      <li><xsl:apply-templates select="Name|DateCreated|TestDesc"/><br/><br/></li>
    </xsl:for-each>
    </ul>
  </xsl:template>

  <xsl:template match="Name">
    <ref>Name: </ref><xsl:value-of select="."/><br/>
  </xsl:template>
  <xsl:template match="DateCreated">      
    <ref>Date Created: </ref><xsl:value-of select="."/><br/>
  </xsl:template>    
  <xsl:template match="TestDesc">    
    <ref>Description: </ref><xsl:value-of select="."/><br/>
  </xsl:template>    
  
</xsl:stylesheet>