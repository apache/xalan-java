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

import com.ibm.cs.util.ReflectionUtils;

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

    // determine the argument types as they are given
    Class[] argTypes = null;
    if (methodArgs != null) 
    {
      argTypes = new Class[methodArgs.length];
      for (int i = 0; i < argTypes.length; i++) 
      {
        argTypes[i] = (methodArgs[i]!=null) ? methodArgs[i].getClass() : null;
      }
    }

    // try to find the method and run it, taking into account the special
    // type conversions we want. If an arg is a Double, we first try with
    // double and then with Double. Same for Boolean/boolean. This is done
    // wholesale tho - that is, if there are two Double args both are
    // tried double first and then Double.
    boolean done = false;
    boolean toggled = false;
    try 
    {
      while (!done) 
      {
        if (methodArgs == null) 
        {
          done = true; // nothing to retry - do as-is or give up
        }
        else 
        {
          if (!toggled) 
          {
            for (int i = 0; i < argTypes.length; i++) 
            {
              Class cl = argTypes[i];
              if (cl != null) 
              {
                if (cl == Double.class) 
                {
                  cl = double.class;
                }
                if (cl == Float.class) 
                {
                  cl = float.class;
                }
                else if (cl == Boolean.class) 
                {
                  cl = boolean.class;
                }
                else if (cl == Byte.class) 
                {
                  cl = byte.class;
                }
                else if (cl == Character.class) 
                {
                  cl = char.class;
                }
                else if (cl == Short.class) 
                {
                  cl = short.class;
                }
                else if (cl == Integer.class) 
                {
                  cl = int.class;
                }
                else if (cl == Long.class) 
                {
                  cl = long.class;
                }
                argTypes[i] = cl;
              }
            }
            toggled = true;
          }
          else 
          {
            for (int i = 0; i < argTypes.length; i++) 
            {
              Class cl = argTypes[i];
              if (cl != null) 
              {
                if (cl == double.class) 
                {
                  cl = Double.class;
                }
                if (cl == float.class) 
                {
                  cl = Float.class;
                }
                else if (cl == boolean.class) 
                {
                  cl = Boolean.class;
                }
                else if (cl == byte.class) 
                {
                  cl = Byte.class;
                }
                else if (cl == char.class) 
                {
                  cl = Character.class;
                }
                else if (cl == short.class) 
                {
                  cl = Short.class;
                }
                else if (cl == int.class) 
                {
                  cl = Integer.class;
                }
                else if (cl == long.class) 
                {
                  cl = Long.class;
                }
                argTypes[i] = cl;
              }
            }
            done = true;
          }
        }
        
        // now find method with the right signature, call it and return result.
        try 
        {
          if (isNew) 
          {
            // if its a "new" call then need to find and invoke a constructor
            // otherwise find and invoke the appropriate method. The method
            // searching logic is the same of course.
            Constructor c =
                           ReflectionUtils.getConstructor ((Class) object, argTypes);
            Object obj = c.newInstance (methodArgs);
            return obj;
          }
          else 
          {
            Method m = ReflectionUtils.getMethod (object, method, argTypes);
            return m.invoke (object, methodArgs);
          }
        }
        catch (NoSuchMethodException e) 
        {
          // ignore if not done looking
          if (done) 
          {
            throw e;
          }
        }
      }
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
    return null;
  }
}

