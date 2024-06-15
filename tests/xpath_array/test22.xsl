<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:array="http://www.w3.org/2005/xpath-functions/array"
                exclude-result-prefixes="array"
                version="3.0">
                
    <!-- Author: mukulg@apache.org -->
    
    <!-- An XSL 3.0 test case, to test XPath function array:sort.
    
         The XPath function examples provided within this test case,
         are borrowed from XPath 3.1 F&O spec.
    -->                                           

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/">
	   <result>
		  <one>
             <xsl:value-of select="array:sort([1, 4, 6, 5, 3])"/>
	      </one>
		  <two>
             <xsl:value-of select="array:sort([1, -2, 5, 10, -10, 10, 8], (), function($x) {abs($x)})"/>
	      </two>
		  <three>
		     <!-- Sort an array of strings, using Swedish collation. Collected the following, 
		          list of words via web search. -->
		     <xsl:variable name="words" select="['Ja', 'Nej', 'God', 'Natt', 'Snalla']"/>
             <xsl:value-of select="array:sort($words, 'http://www.w3.org/2013/collation/UCA?lang=se')"/>
	      </three>
	   </result>
    </xsl:template>
    
</xsl:stylesheet>
