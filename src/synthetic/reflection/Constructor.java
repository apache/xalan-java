// Synthetic Class descriptors ("reverse reflection")
// Copyright ©2000 International Business Machines Corportation
// All rights reserved.
package synthetic.reflection;

import java.lang.reflect.InvocationTargetException;
import synthetic.SynthesisException;

/**
  Constructor provides information about, and access to, a
  single constructor for a class. 
  
  Constructor permits widening conversions to occur when
  matching the actual parameters to newInstance() with
  the underlying constructor's formal parameters, but
  throws an IllegalArgumentException if a narrowing
  conversion would occur. 

  @see Member  
  @see Class  
  @see getConstructors  
  @see getConstructor  
  @see getDeclaredConstructors 
  */
public class Constructor 
extends EntryPoint
implements Member 
{
  /**
    Actual Java class object. When present, all interactions
    are redirected to it. Allows our Class to function as a
    wrapper for the Java version (in lieu of subclassing or
    a shared Interface), and allows BSC or similar 
    compilation to replace a generated description with an
    directly runnable class.
    */
  private synthetic.Class declaringclass = null;
  /**
    Actual Java class object. When present, all interactions
    are redirected to it. Allows our Class to function as a
    wrapper for the Java version (in lieu of subclassing or
    a shared Interface), and allows BSC or similar 
    compilation to replace a generated description with an
    directly runnable class.
    */
  private java.lang.reflect.Constructor realconstructor = null;
  
  private synthetic.Class[] parametertypes;
  private String[] parameternames;
  private synthetic.Class[] exceptiontypes;
  private int modifiers;
  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   */
  public Constructor(synthetic.Class declaringclass)
  {
    super(declaringclass);
  }
  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   */
  public Constructor(java.lang.reflect.Constructor ctor,synthetic.Class declaringclass)
  {
    super(ctor,declaringclass);
  }
  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   */
  public Constructor(java.lang.reflect.Constructor realconstructor)
  {
    super(realconstructor);
  }
  /**
    Returns a hashcode for this Constructor. The
    hashcode is the same as the hashcode for the
    underlying constructor's declaring class name. 
    */
  public int hashCode()
  {
    return getDeclaringClass().getName().hashCode();
  }
  
  /**
    Uses the constructor represented by this
    Constructor object to create and initialize a new
    instance of the constructor's declaring class, with
    the specified initialization parameters. Individual
    parameters are automatically unwrapped to match
    primitive formal parameters, and both primitive
    and reference parameters are subject to widening
    conversions as necessary. Returns the newly
    created and initialized object. 
    <p>
    Creation proceeds with the following steps, in
    order: 
    <p>
    If the class that declares the underlying constructor
    represents an abstract class, the creation throws an
    InstantiationException. 
    <p>
    If this Constructor object enforces Java language
    access control and the underlying constructor is
    inaccessible, the creation throws an
    IllegalAccessException. 
    <p>
    If the number of actual parameters supplied via
    initargs is different from the number of formal
    parameters required by the underlying constructor,
    the creation throws an IllegalArgumentException. 
    <p>
    A new instance of the constructor's declaring class
    is created, and its fields are initialized to their
    default initial values. 
    <p>
    For each actual parameter in the supplied initargs
    array: 
    <p>
    If the corresponding formal parameter has a
    primitive type, an unwrapping conversion is
    attempted to convert the object value to a value of
    the primitive type. If this attempt fails, the
    creation throws an IllegalArgumentException. 
    <p>
    
    If, after possible unwrapping, the parameter value
    cannot be converted to the corresponding formal
    parameter type by an identity or widening
    conversion, the creation throws an
    IllegalArgumentException. 
    <p>
    Control transfers to the underlying constructor to
    initialize the new instance. If the constructor
    completes abruptly by throwing an exception, the
    exception is placed in an
    InvocationTargetException and thrown in turn to
    the caller of newInstance. 
    <p>
    If the constructor completes normally, returns the
    newly created and initialized instance. 
    
    @throws  IllegalAccessException 
    if the underlying constructor is inaccessible. 
    @throws  IllegalArgumentException 
    if the number of actual and formal
    parameters differ, or if an unwrapping
    conversion fails. 
    @throws  InstantiationException 
    if the class that declares the underlying
    constructor represents an abstract class. 
    @throws  InvocationTargetException 
    if the underlying constructor throws an
    exception. 
    */
  public Object newInstance(Object initargs[]) 
       throws InstantiationException, IllegalAccessException,
	 IllegalArgumentException,
	 java.lang.reflect.InvocationTargetException
  {
    if(realep!=null)
      return ((java.lang.reflect.Constructor)realep).newInstance(initargs);
    else    
      throw new InstantiationException("Un-reified synthetic.Class doesn't yet support invocation");
  }
  
}
