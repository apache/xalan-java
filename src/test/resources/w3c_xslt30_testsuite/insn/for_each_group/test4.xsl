<?spec xslt#grouping?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"               
               exclude-result-prefixes="xs"
			   version="3.0">
			   
  <!-- use with test4.xml -->
  
  <!-- An XSL test case, to verify fix for W3C XSLT 3.0 xsl:for-each-group 
       test case for-each-group-043.
       (XSLT 3.0 grouping using group-by with composite keys)   
   -->			   
  
  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <out>
	     <xsl:for-each-group select="/*/city" 
	                         group-by="@country, xs:decimal(@pop)"
	                         composite="yes">
	        <group country="{current-grouping-key()[1]}" pop="{current-grouping-key()[2]}">
	           <xsl:copy-of select="current-group()"/>
	        </group>
	     </xsl:for-each-group>
      </out>
   </xsl:template>
	
</xsl:transform>
