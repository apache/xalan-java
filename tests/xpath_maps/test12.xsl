<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:map="http://www.w3.org/2005/xpath-functions/map"
				exclude-result-prefixes="xs map"
                version="3.0">				

   <!-- Author: mukulg@apache.org -->                 				

   <xsl:output method="xml" indent="yes"/>
   
   <!-- An XPath 3.1 test case, to test SequenceType MapTest specified as 
        value of xsl:variable's "as" attribute. -->

   <xsl:template match="/">	  
      <result>
	     <xsl:variable name="map1" select="map {'zero' : xs:boolean(0), 'one' : xs:boolean(1)}" as="map(xs:string, xs:boolean)"/>		 
	     <map noOfEntries="{count(map:keys($map1))}">
            <xsl:for-each select="map:keys($map1)">
			   <xsl:variable name="key" select="."/>
			   <entry key="{$key}" value="{map:get($map1, $key)}"/>
			</xsl:for-each>
		 </map>		 
		 <xsl:variable name="map2" select="map {1 : 'Mukul', 2 : 'Joseph', 3 : 'Gary', 4 : 'John', 5 : 'Henry'}" as="map(xs:integer, xs:string)"/>
		 <map noOfEntries="{count(map:keys($map2))}">
            <xsl:for-each select="map:keys($map2)">
			   <xsl:variable name="key" select="."/>
			   <entry key="{$key}" value="{map:get($map2, $key)}"/>
			</xsl:for-each>
		 </map>
	  </result>
   </xsl:template>

</xsl:stylesheet>
