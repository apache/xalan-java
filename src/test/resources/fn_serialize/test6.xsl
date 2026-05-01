<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
				xmlns:ns0="http://ns0"
                exclude-result-prefixes="#all"
                version="3.0">              
    
    <!-- Author: mukulg@apache.org -->
    
    <!-- use with test1.xml -->
    
    <!-- An XSL 3 stylesheet test case, to test XPath 3.1 function fn:serialize 
         method 'adaptive'. -->               				 
				
    <xsl:output method="xml" indent="yes"/>				

	<xsl:template match="/">
	   <result>
		  <one>
		     <xsl:variable name="seq1" select="(true(), false())" as="xs:boolean*"/>
             <xsl:value-of select="serialize($seq1, map{'method':'adaptive'})"/>
		  </one>
		  <two>
		     <xsl:variable name="seq1" select="('abc', '123', 'm&quot;no')" as="xs:string*"/>
			 <xsl:value-of select="serialize($seq1, map{'method':'adaptive'})"/>
		  </two>
		  <three>
		     <xsl:variable name="seq1" select="(12, 13, 14)" as="xs:integer*"/>
			 <xsl:value-of select="serialize($seq1, map{'method':'adaptive'})"/>
		  </three>
		  <four>
		     <xsl:variable name="seq1" select="(12.2, 13.5, 14.76)" as="xs:decimal*"/>
			 <xsl:value-of select="serialize($seq1, map{'method':'adaptive'})"/>
		  </four>
		  <five>
		     <xsl:variable name="seq1" select="(xs:double(12.2), xs:double(13.5), xs:double(14.76))" as="xs:double*"/>
			 <xsl:value-of select="serialize($seq1, map{'method':'adaptive'})"/>
		  </five>
		  <six>
		     <xsl:variable name="seq1" select="(xs:QName('name1'), xs:QName('name2'), xs:QName('name3'))" as="xs:QName*"/>
			 <xsl:value-of select="serialize($seq1, map{'method':'adaptive'})"/>
		  </six>
		  <seven>
		     <xsl:variable name="seq1" select="(xs:QName('ns0:name1'), xs:QName('ns0:name2'), xs:QName('ns0:name3'))" as="xs:QName*"/>
			 <xsl:value-of select="serialize($seq1, map{'method':'adaptive'})"/>
		  </seven>
		  <eight>
		     <xsl:variable name="date1" select="xs:date('2015-07-17')" as="xs:date"/>
			 <xsl:variable name="dateTime1" select="xs:dateTime('2005-02-10T10:00:00Z')" as="xs:dateTime"/>
			 <xsl:value-of select="serialize(($date1, $dateTime1), map{'method':'adaptive'})"/>
		  </eight>		  
	   </result>
	</xsl:template>
	
	<!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the "License");
      * you may not use this file except in compliance with the License.
      * You may obtain a copy of the License at
      *
      *     http://www.apache.org/licenses/LICENSE-2.0
      *
      * Unless required by applicable law or agreed to in writing, software
      * distributed under the License is distributed on an "AS IS" BASIS,
      * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
      * See the License for the specific language governing permissions and
      * limitations under the License.
    -->

</xsl:stylesheet>
