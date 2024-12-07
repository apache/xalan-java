<xsl:stylesheet version="3.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <!-- A primitive test for the XSLT xsl:iterate instruction. Exercises 
       position() and last() functions. -->

  <xsl:output method="xml" indent="yes"/>
  
  <xsl:template match="/">
     <out>
        <xsl:iterate select="//ITEM/TITLE">
           <item position="{position()}" last="{last()}">
               <xsl:copy-of select="."/>
           </item>
        </xsl:iterate>
     </out>
  </xsl:template>

</xsl:stylesheet>
