/*
 * @(#)$Id$
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * @author Jacek Ambroziak
 * @author Santiago Pericas-Geertsen
 * @author Erwin Bolwidt <ejb@klomp.org>
 *
 */

package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

import java.util.Vector;

final class CallTemplate extends Instruction {
    private QName _name;
    
    // The array of effective parameters in this CallTemplate.
    // An object in this array can be either a WithParam or
    // a Param if no WithParam exists for a particular parameter.
    private Object[] _parameters = null;
    
    // True if we need to create temporary variables to hold
    // the parameter values.
    private boolean _createTempVar = false;
    
    // The corresponding template which this CallTemplate calls.
    private Template _calleeTemplate = null;
    
    // The array to hold the old load instructions for the parameters.
    private org.apache.bcel.generic.Instruction[] _oldLoadInstructions;

    public void display(int indent) {
	indent(indent);
	System.out.print("CallTemplate");
	Util.println(" name " + _name);
	displayContents(indent + IndentIncrement);
    }
		
    public boolean hasWithParams() {
	return elementCount() > 0;
    }

    public void parseContents(Parser parser) {
	_name = parser.getQNameIgnoreDefaultNs(getAttribute("name"));
	parseChildren(parser);
    }
		
    /**
     * Verify that a template with this name exists.
     */
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
	final Template template = stable.lookupTemplate(_name);
	if (template != null) {
	    typeCheckContents(stable);
	}
	else {
	    ErrorMsg err = new ErrorMsg(ErrorMsg.TEMPLATE_UNDEF_ERR,_name,this);
	    throw new TypeCheckError(err);
	}
	return Type.Void;
    }

    /**
     * Translate call-template.
     * A parameter frame is pushed only if some template in the stylesheet
     * uses parameters.
     * TODO: optimize by checking if the callee has parameters.
     */
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
	final Stylesheet stylesheet = classGen.getStylesheet();
	final ConstantPoolGen cpg = classGen.getConstantPool();
	final InstructionList il = methodGen.getInstructionList();


	if (stylesheet.hasLocalParams() || hasContents()) {
	    _calleeTemplate = getCalleeTemplate();
	    
	    // Build the parameter list if the called template is
	    // a simple named template.
	    if (_calleeTemplate != null) {
	    	buildParameterList();
	    }
	    // This is only needed when the called template is not
	    // a simple named template.
	    else {
	        // Push parameter frame
	        final int push = cpg.addMethodref(TRANSLET_CLASS, 
					          PUSH_PARAM_FRAME,
					          PUSH_PARAM_FRAME_SIG);
	        il.append(classGen.loadTranslet());
	        il.append(new INVOKEVIRTUAL(push));
	        // Translate with-params
	        translateContents(classGen, methodGen);
	    }
	}

	final String className = stylesheet.getClassName();
	// Generate a valid Java method name
	String methodName = Util.escape(_name.toString());

	il.append(classGen.loadTranslet());
	il.append(methodGen.loadDOM());
	il.append(methodGen.loadIterator());
	il.append(methodGen.loadHandler());
	il.append(methodGen.loadCurrentNode());
	String methodSig = "(" + DOM_INTF_SIG + NODE_ITERATOR_SIG
	                   + TRANSLET_OUTPUT_SIG + NODE_SIG;
	
	if (_calleeTemplate != null) {
	    Vector calleeParams = _calleeTemplate.getParameters();
	    int numParams = _parameters.length;
	    org.apache.bcel.generic.Type objectType = null;	                
	    
	    if (_createTempVar) {
	    	_oldLoadInstructions = new org.apache.bcel.generic.Instruction[numParams];
	    	objectType = Util.getJCRefType(OBJECT_SIG);
	    }
	    
	    // Translate all effective WithParams and Params in the list
	    for (int i = 0; i < numParams; i++) {
	    	methodSig = methodSig + OBJECT_SIG;
	        SyntaxTreeNode node = (SyntaxTreeNode)_parameters[i];
	        node.translate(classGen, methodGen);
	        
	        // Store the parameter value in a local variable if default
	        // parameters are used. In this case the default value of a
	        // parameter may reference another parameter declared earlier. 
	        if (_createTempVar) {
	            il.append(DUP);
	            
	            // The name of the variable used to hold the value of a
	            // parameter
	            String name = "call$template$" + Util.escape(_name.toString())
	                          + "$" + getParameterName(node);
	            
	            // Search for the local variable first, only add it
	            // if it does not exist.
	            LocalVariableGen local = methodGen.getLocalVariable(name);
	            
	            if (local == null) {
	            	local = methodGen.addLocalVariable2(name,
				            objectType,
				            il.getEnd());
	            }
	            
	            // Store the parameter value into a variable.
	            il.append(new ASTORE(local.getIndex()));
	            
	            // Update the load instructions of the Param objects so that
	            // they point to the local variables. Store the old load
	            // instructions in the _oldLoadInstructions array so that
	            // we can restore them later. 
	            if (node instanceof Param) {
	            	Param param = (Param)node;            	
	            	org.apache.bcel.generic.Instruction oldInstruction = 
	            	    param.setLoadInstruction(new ALOAD(local.getIndex()));	                
	                _oldLoadInstructions[param.getIndex()] = oldInstruction;
	            }
	            else if (node instanceof WithParam) {
	            	Param param = (Param)calleeParams.elementAt(i);
	            	org.apache.bcel.generic.Instruction oldInstruction = 
	            	    param.setLoadInstruction(new ALOAD(local.getIndex()));
	            	_oldLoadInstructions[param.getIndex()] = oldInstruction;
	            }
	        }
	    }
	    
	    // Restore the old load instructions for the Params.
	    if (_createTempVar) {
	    	for (int i = 0; i < numParams; i++) {
	    	    Param param = (Param)calleeParams.elementAt(i);
	    	    param.setLoadInstruction(_oldLoadInstructions[i]);     
	    	}
	    }
	}
	
	methodSig = methodSig + ")V";
	il.append(new INVOKEVIRTUAL(cpg.addMethodref(className,
						     methodName,
						     methodSig)));
	
	// Do not need to call Translet.popParamFrame() if we are
	// calling a simple named template.
	if (_calleeTemplate == null && (stylesheet.hasLocalParams() || hasContents())) {
	    // Pop parameter frame
	    final int pop = cpg.addMethodref(TRANSLET_CLASS,
					     POP_PARAM_FRAME,
					     POP_PARAM_FRAME_SIG);
	    il.append(classGen.loadTranslet());
	    il.append(new INVOKEVIRTUAL(pop));
	}
    }
    
    /**
     * Return the name of a Param or WithParam.
     */
    private String getParameterName(SyntaxTreeNode node) {
        if (node instanceof Param) {
            return Util.escape(((Param)node).getName().toString());
        }
        else if (node instanceof WithParam) {
            WithParam withParam = (WithParam)node;
            return Util.escape(withParam.getName().toString());
        }
        else
            return null;
    }
    
    /**
     * Return the simple named template which this CallTemplate calls.
     * Return false if there is no matched template or the matched
     * template is not a simple named template.
     */
    public Template getCalleeTemplate() {
    	Stylesheet stylesheet = getStylesheet();
    	Vector templates = stylesheet.getAllValidTemplates();
    	int size = templates.size();
    	for (int i = 0; i < size; i++) {
    	    Template t = (Template)templates.elementAt(i);
    	    if (t.getName() == _name && t.isSimpleNamedTemplate()) {
    	    	return t;
    	    }
    	}
    	return null;
    }
    
    /**
     * Build the list of effective parameters in this CallTemplate.
     * The parameters of the called template are put into the array first.
     * Then we visit the WithParam children of this CallTemplate and replace
     * the Param with a corresponding WithParam having the same name.
     */
    private void buildParameterList() {   	
    	// Put the parameters from the called template into the array first.
    	// This is to ensure the order of the parameters.
    	Vector defaultParams = _calleeTemplate.getParameters();
    	int numParams = defaultParams.size();
    	_parameters = new Object[numParams];
    	for (int i = 0; i < numParams; i++) {
    	    _parameters[i] = defaultParams.elementAt(i);
    	}
    		    	
    	// Replace a Param with a WithParam if they have the same name.
    	int count = elementCount();
    	for (int i = 0; i < count; i++) {
    	    Object node = elementAt(i);
    	    if (node instanceof WithParam) {
    	    	WithParam withParam = (WithParam)node;
    	    	QName name = withParam.getName();
    	    	for (int k = 0; k < numParams; k++) {
    	    	    Object object = _parameters[k];
    	    	    if (object instanceof Param 
    	    	        && ((Param)object).getName() == name) {
    	    	        withParam.setDoParameterOptimization(true);
    	    	        _parameters[k] = withParam;
    	    	        break;
    	    	    }
    	    	    else if (object instanceof WithParam 
    	    	        && ((WithParam)object).getName() == name) {
    	    	        withParam.setDoParameterOptimization(true);
    	    	        _parameters[k] = withParam;    	    	        
    	    	        break;
    	    	    }
    	    	}    	    	
    	    }
    	}
    	
    	// Set the _createTempVar flag to true if the select expression
    	// in a parameter may reference another parameter.
    	for (int i = 0; i < numParams; i++) {
    	    if (_parameters[i] instanceof Param) {
    	    	Param param = (Param)_parameters[i];
    	    	Expression expr = param.getExpression();
    	    	if ((expr != null || param.elementCount() != 0)
    	    	    && !(expr instanceof CastExpr
    	    	        && (((CastExpr)expr).getExpr() instanceof LiteralExpr
    	    	            || ((CastExpr)expr).getExpr() instanceof BooleanExpr)))
    	    	{
    	    	    _createTempVar = true;
    	    	    break;	
    	    	}
    	    }
    	}
    }
    
}
    
