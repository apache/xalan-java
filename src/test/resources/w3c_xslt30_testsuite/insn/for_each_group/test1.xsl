<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                version="3.0">
                
   <!-- An XSL test case, to verify fix for W3C XSLT 3.0 xsl:for-each-group 
        test case for-each-group-039.
        (To test position() and last() with the result of xsl:for-each-group group-starting-with)
   -->                 

   <xsl:output method="xml" indent="yes"/>   
   
   <xsl:template match="/">
     <xsl:variable name="languages">
       <p>English</p>
       <lang>English</lang>
       <lang>Chinese</lang>
       <p>French</p>
       <lang>French</lang>
       <p>Chinese</p>
       <lang>French</lang>
       <lang>Chinese</lang>
     </xsl:variable>	 
     <r>
       <xsl:for-each-group select="$languages/*" group-starting-with="p">
         <p><xsl:value-of select="position()"/></p>
         <v><xsl:value-of select="."/></v>
         <l><xsl:value-of select="last()"/></l>
       </xsl:for-each-group>
     </r>
   </xsl:template>
</xsl:stylesheet>
