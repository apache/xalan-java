<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:math="http://www.w3.org/2005/xpath-functions/math"
                exclude-result-prefixes="math" 				
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->                
				
   <!-- An XSL 3 stylesheet test case, to test use of XPath user-defined 
        functions and binary operator '*' together in different ways for the 
		same use case. 
		
		This XSL stylesheet test case is motivated by the fact that, Xalan-J's 
		implementation of XPath 3.1 expression $sqrt(9) * $sqr(5) has a bug and 
		presenting various alternate legitimate ways of achieving this computation 
		correctly.
   -->	
                
   <xsl:output method="xml" indent="yes"/>
   
   <xsl:template match="/">
      <result>
         <xsl:variable name="sqrt" select="function($a) { math:sqrt($a) }" as="function(*)"/>
		 <xsl:variable name="sqr" select="function($a) { $a * $a }" as="function(*)"/>
		 <one><xsl:value-of select="apply($sqrt, [9]) * apply($sqr, [5])"/></one>
		 
		 <xsl:variable name="p" select="$sqrt(9)"/>
		 <xsl:variable name="q" select="$sqr(5)"/>
		 
		 <two><xsl:value-of select="$p * $q"/></two>
		 
		 <xsl:variable name="apply1" select="function($a, $b) { let $L := math:sqrt($a), 
		                                                            $M := $b * $b 
																    return $L * $M }"/>
		 <three><xsl:value-of select="$apply1(9, 5)"/></three>
		 
		 <xsl:variable name="apply2" select="function($a, $b) { let $L := $sqrt($a), 
		                                                            $M := $sqr($b) return $L * $M }"/>
         <four><xsl:value-of select="$apply2(9, 5)"/></four>
		 
		 <!-- Illustration of a buggy XPath expression below. Expected answer
		      is 75, but actual answer is 625. -->
         <five_bug><xsl:value-of select="$sqrt(9) * $sqr(5)"/></five_bug>		 
	  </result>
   </xsl:template>

</xsl:stylesheet>