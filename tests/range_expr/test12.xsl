<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">

   <!-- Author: mukulg@apache.org -->
   
   <!-- use with test1_b.xml -->
   
   <!-- An XSLT stylesheet demonstrating that, arguments of XPath 
        range "to" operator should have data type xs:integer. -->
        
   <xsl:output method="xml" indent="yes"/>

   <xsl:template match="/elem">
      <result>
        <xsl:for-each select="x to y">
           <val><xsl:value-of select="."/></val>
        </xsl:for-each>
      </result>
   </xsl:template>

</xsl:stylesheet>