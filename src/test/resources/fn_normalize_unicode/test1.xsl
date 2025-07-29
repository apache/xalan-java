<?xml version="1.0" encoding="iso-8859-1"?> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"                				
                version="3.0">
  
  <xsl:output indent="yes"/>
  
  <xsl:template match="/">
    <result>
	   <one>
	      <xsl:value-of select="string-to-codepoints(normalize-unicode('sch�n'))"/>
	   </one>
	   <two>
	      <xsl:value-of select="string-to-codepoints(normalize-unicode('sch�n', 'NFC'))"/>
	   </two>
	   <three>
	      <xsl:value-of select="string-to-codepoints(normalize-unicode('sch�n', 'NFD'))"/>
	   </three>
	   <four>
	      <xsl:value-of select="string-to-codepoints(normalize-unicode('sch�n', 'NFKC'))"/>
	   </four>
	   <five>
	      <xsl:value-of select="string-to-codepoints(normalize-unicode('sch�n', 'NFKD'))"/>
	   </five>
    </result>
  </xsl:template>
    

</xsl:stylesheet>
