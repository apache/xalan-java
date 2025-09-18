<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
  <!-- use with iterate001.xml -->
   
  <!-- An XSL 3.0 stylesheet test case, to test xsl:iterate 
       instruction. 
       
       This XSL stylesheet has been borrowed from W3C XSLT 3.0 
       test suite and makes few changes to the XSL stylesheet 
       algorithm.
  -->
        
  <xsl:output method="xml" indent="yes"/>                

  <xsl:template match="/">
    <out>
      <xsl:iterate select="//ITEM">
        <xsl:param name="basketCost" select="0"/>
        <xsl:if test="$basketCost &lt;= 12.00">
           <item cost="{format-number($basketCost, '00.00')}">
              <xsl:copy-of select="TITLE"/>
           </item>            
           <xsl:next-iteration>
	          <xsl:with-param name="basketCost" select="$basketCost + PRICE"/>
           </xsl:next-iteration>
        </xsl:if>
      </xsl:iterate>
    </out>
  </xsl:template>

</xsl:stylesheet>
