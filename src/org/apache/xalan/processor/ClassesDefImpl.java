/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written 
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.processor;

import org.apache.xalan.templates.ElemApplyImport;
import org.apache.xalan.templates.ElemApplyTemplates;
import org.apache.xalan.templates.ElemAttribute;
import org.apache.xalan.templates.ElemCallTemplate;
import org.apache.xalan.templates.ElemChoose;
import org.apache.xalan.templates.ElemCopy;
import org.apache.xalan.templates.ElemCopyOf;
import org.apache.xalan.templates.ElemElement;
import org.apache.xalan.templates.ElemForEach;
import org.apache.xalan.templates.ElemIf;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemNumber;
import org.apache.xalan.templates.ElemOtherwise;
import org.apache.xalan.templates.ElemPI;
import org.apache.xalan.templates.ElemParam;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemText;
import org.apache.xalan.templates.ElemTextLiteral;
import org.apache.xalan.templates.ElemValueOf;
import org.apache.xalan.templates.ElemVariable;
import org.apache.xalan.templates.ElemWhen;
import org.apache.xalan.templates.ElemWithParam;

/**
 * Classes definition of Xalan template elements.
 */
public class ClassesDefImpl extends ClassesDef {

	Class[] m_classes = {
		null,
		null,
        ElemWithParam.class, //2
		null,
		null,
		null,
		null,
		null,
		null,
		ElemCopy.class, // 9
		null, // 10
		null,
		null,
		null,
		null,
		null,
		null,
		ElemCallTemplate.class, // 17
		null,
		ElemTemplate.class,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		ElemForEach.class,
		null,
		ElemValueOf.class,  // 30
		null,
		null,
		null,
		null,
		ElemNumber.class, // 35
		ElemIf.class, // 36
		ElemChoose.class,
		ElemWhen.class,
		ElemOtherwise.class,
		null, // 40
		ElemParam.class, // 41
		ElemText.class, // 42
		null,
		null,
		null,
		ElemElement.class, // 46
		null,
		ElemAttribute.class, // 48
		null,
		ElemApplyTemplates.class, // 50
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		ElemPI.class, // 58
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null, // 70
		null,
		ElemApplyImport.class, // 72
		ElemVariable.class,
		ElemCopyOf.class, // 74
		null,
		null,
		ElemLiteralResult.class, // 77
		ElemTextLiteral.class,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
		null,
        null,
        null // 90
		
	};
	
    /**
     * @see org.apache.xalan.processor.ClassesDef#getClass
     */	
	public Class getClass( int template ) {
		return m_classes[template];
	}
}

