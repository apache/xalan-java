<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:g="http://www.w3.org/2001/03/XPath/grammar">
  
  
  <xsl:template match="*">
  	<xsl:copy>
  		<xsl:apply-templates select="@*"/>
  		<xsl:apply-templates/>
  	</xsl:copy>
  </xsl:template>

  <!--xsl:template match="@node-test">
  	<xsl:if test="not( ../@name='Slash')">
  		<xsl:copy/>
  	</xsl:if>
  </xsl:template-->
  
   
  <xsl:template match="g:production[@name='AbbreviatedForwardStep' or @name='AbbreviatedReverseStep' ]">
  	<xsl:copy>
  		<xsl:attribute name="node-type">void</xsl:attribute>
  		<xsl:apply-templates select="@*"/>
  		<xsl:apply-templates/>
  	</xsl:copy>
  </xsl:template>
  
  
  <xsl:template match="@*">
 	  	<xsl:copy/>

  </xsl:template>

  
  
</xsl:stylesheet>