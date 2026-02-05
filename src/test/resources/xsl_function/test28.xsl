<?xml version="1.0" encoding="UTF-8"?>
<xsl:transform xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
               xmlns:xs="http://www.w3.org/2001/XMLSchema"
               xmlns:str="http://example.com/namespace"               
               exclude-result-prefixes="#all"
			   version="3.0">
			   
   <!-- Author: mukulg@apache.org -->			   
			   
   <!-- An XSL stylesheet example, to test stylesheet function. 
        Ref : XSLT 3.0 specification, section 10.3.9 Examples of Stylesheet Functions. -->			   
			   
   <xsl:output method="xml" indent="yes"/>			   

   <!-- An XSL stylesheet recursive function named str:reverse 
        that reverses the words in a supplied sentence. -->
   <xsl:function name="str:reverse" as="xs:string">
      <xsl:param name="sentence" as="xs:string"/>
      <xsl:sequence select="if (contains($sentence, ' '))
                                  then concat(str:reverse(substring-after($sentence, ' ')),
                                          ' ',
                                    substring-before($sentence, ' '))
                                  else $sentence"/>
   </xsl:function>

   <xsl:template match="/">
      <output>
        <xsl:value-of select="str:reverse('DOG BITES MAN')"/>
      </output>
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

</xsl:transform>
