<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="3.0">
                
   <!-- Author: mukulg@apache.org -->
   
   <!-- Test for the XPath 3.1 fn:fold-left() function.
   
        The XPath fn:fold-left() function usage examples, as
        illustrated within this stylesheet are borrowed from
        XPath 3.1 spec, and https://www.altova.com/. -->                

   <xsl:output method="xml" indent="yes"/>
   
   <xsl:variable name="fnAdd" select="function($arg1, $arg2) { $arg1 + $arg2 }"/>
   
   <!-- the XPath evaluation of select expression of this variable,
        produces an empty sequence. -->
   <xsl:variable name="emptySeq" select="1 to 0"/>
      
   <xsl:template match="/">      
      <result>
        <!-- with this fold-left function call example, the inline function adds '$arg1' to 
	         '$arg2' with a base value of 0. i.e, the following evaluation is performed : 
             (((((0 + 1) + 2) + 3) + 4) + 5) -->
        <val1><xsl:value-of select="fold-left(1 to 5, 0, function($a, $b) { $a + $b })"/></val1>
        
        <!-- with this fold-left function call example, the inline function multiplies '$arg1' 
             by '$arg2' with a base value of 1. i.e, the following evaluation is performed : 
             (((((1 * 1) * 2) * 3) * 4) * 5) -->
        <val2><xsl:value-of select="fold-left(1 to 5, 1, function($arg1, $arg2) { $arg1 * $arg2 })"/></val2>
        
        <!-- with this fold-left function call example, the inline function multiplies '$arg1' 
	         by '$arg2' with a base value of 0. i.e, the following evaluation is performed : 
             (((((0 * 1) * 2) * 3) * 4) * 5) -->
        <val3><xsl:value-of select="fold-left(1 to 5, 0, function($arg1, $arg2) { $arg1 * $arg2 })"/></val3>
        
        <!-- with this fold-left function call example, the inline function subtracts '$arg2' from '$arg1' 
             with a base value of 0. i.e, the following evaluation is performed : 
             (((((0 - 1) - 2) - 3) - 4) - 5) -->
        <val4><xsl:value-of select="fold-left(1 to 5, 0, function($arg1, $arg2) { $arg1 - $arg2 })"/></val4>
        
        <!-- with this fold-left function call example, the result is product of the numeric values 
             present within an input sequence.-->
        <xsl:variable name="seq1" select="(2, 3, 5, 7)"/>        
        <val5><xsl:value-of select="fold-left($seq1, 1, function($a, $b) { $a * $b })"/></val5>
        
        <xsl:variable name="seq2" select="(true(), false(), false())"/>
        
        <!-- with this fold-left function call example, the boolean result 'true' is returned if any 
             xdm item within an input sequence has an effective boolean value of 'true'. -->
        <val6><xsl:value-of select="fold-left($seq2, false(), function($a, $b) { $a or $b })"/></val6>
        
        <!-- with this fold-left function call example, the boolean result 'true' is returned if every 
             xdm item within an input sequence has an effective boolean value of 'true'. -->
        <val7><xsl:value-of select="fold-left($seq2, false(), function($a, $b) { $a and $b })"/></val7>
        
        <!-- The fold-left function call below, reverses the order of xdm items within an input 
             sequence. -->
        <val8><xsl:value-of select="fold-left(1 to 5, $emptySeq, function($a, $b) { ($b, $a) })"/></val8>
        
        <!-- with this fold-left function call example, an inline function concatenates '$arg1' with 
             '$arg2', with a base value of 'z'. i.e, the following evaluation is performed :
             concat(concat(concat('z','a'), 'b'), 'c') -->
        <xsl:variable name="charListSeq" select="('a', 'b', 'c')"/>
        <val9><xsl:value-of select="fold-left($charListSeq, 'z' , function($arg1, $arg2) 
                                                                        { concat($arg1, $arg2) })"/></val9>
                                                                        
        <!-- the following fn:fold-left function call example, refers an inline function, 
             via a variable reference. -->
         <val10><xsl:value-of select="fold-left(1 to 7, 0, $fnAdd)"/></val10>                                                                        
      </result>
   </xsl:template>
   
   <!--
      * Licensed to the Apache Software Foundation (ASF) under one
      * or more contributor license agreements. See the NOTICE file
      * distributed with this work for additional information
      * regarding copyright ownership. The ASF licenses this file
      * to you under the Apache License, Version 2.0 (the  "License");
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