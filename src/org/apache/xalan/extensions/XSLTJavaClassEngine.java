/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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

package org.apache.xalan.extensions;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

import com.ibm.bsf.*;
import com.ibm.bsf.util.BSFEngineImpl;

import com.ibm.bsf.util.ReflectionUtils;

/**
 * <meta name="usage" content="internal"/>
 * This is a custom scripting engine for the XSLT processor's needs of calling
 * into Java objects. 
 *
 * @author   Sanjiva Weerawarana (sanjiva@watson.ibm.com)
 */

public class XSLTJavaClassEngine extends BSFEngineImpl 
{
  /**
   * This is used by an application to evaluate an object containing
   * some expression - clearly not possible for compiled code ..
   */
  public Object eval (String source, int lineNo, int columnNo, 
                      Object oscript) throws BSFException 
  {
    throw new BSFException (BSFException.REASON_UNSUPPORTED_FEATURE,
      "Java bytecode engine can't evaluate expressions");
  }

  /**
   * call the named method on the object that was loaded by eval. 
   * The method selection stuff is very XSLT-specific, hence the
   * custom engine.
   *
   * @param object ignored - should always be null
   */
  public Object call (Object object, String method, Object[] args) 
    throws BSFException
  {
    // if the function name has a "." in it, then its a static
    // call with the args being arguments to the call. If it 
    // does not, then its a method call on args[0] with the rest
    // of the args as the arguments to the call
    int dotPos = method.lastIndexOf (".");
    Object[] methodArgs = null;
    boolean isNew = false;
    if (dotPos == -1) 
    {
      object = args[0];
      if (args.length > 1) 
      {
        methodArgs = new Object[args.length-1];
        System.arraycopy (args, 1, methodArgs, 0, args.length - 1);
      }
    }
    else 
    {
      String className = method.substring (0, dotPos);
      method = method.substring (dotPos+1);
      methodArgs = args;
      isNew = method.equals ("new");
      try 
      {
        object = Class.forName (className);
      }
      catch (ClassNotFoundException e) 
      {
        throw new BSFException(1, "unable to load class '" + 
          className + "'", e);
      }
    }

    Object[][] convertedArgs = new Object[1][];
    try 
    {
      if (isNew) 
      {
        // if its a "new" call then need to find and invoke a constructor
        // otherwise find and invoke the appropriate method. The method
        // searching logic is the same of course.
        Constructor c = MethodResolver.getConstructor((Class) object, 
                                                      methodArgs, convertedArgs,
                                                      null);
        Object obj = c.newInstance (methodArgs);
        return obj;
      }
      else 
      {
        Method m = MethodResolver.getMethod(object.getClass(), method,
                                                      methodArgs, 
                                                      convertedArgs, null);
        return m.invoke (object, convertedArgs[0]);
      }
    }
    catch (NoSuchMethodException nsme) 
    {
      // ignore if not done looking
      throw new BSFException (BSFException.REASON_OTHER_ERROR,
        "NoSuchMethodException: " + nsme);
    }
    catch (Exception e) 
    {
      Throwable t = (e instanceof InvocationTargetException) ?
                    ((InvocationTargetException)e).getTargetException () :
                    null;
      throw new BSFException (BSFException.REASON_OTHER_ERROR,
        "method call/new failed: " + e +
        ((t==null)?"":(" target exception: "+t)), t);
    }
    // should not get here
    // return null;
  }
}

