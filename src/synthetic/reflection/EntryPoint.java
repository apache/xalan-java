// Synthetic Class descriptors ("reverse reflection")
// Copyright ©2000 International Business Machines Corportation
// All rights reserved.
package synthetic.reflection;

import java.lang.reflect.InvocationTargetException;
import synthetic.SynthesisException;

/***** OPEN ISSUES:
    Reflection doesn't tell us about deprecation; if we want
    that info, MFC advises mousing our way into the class (ugh).
    Should we at least model that for synthetics?
*/

/**
  API/behaviors shared between Constructors and Methods.
  They're mostly similar, except for what they proxy and
  a few specific calls (name, invoke/getInstance).
  */
abstract public class EntryPoint
implements Member 
{
  protected Object realep;
  
  private synthetic.Class declaringclass = null;
  protected synthetic.Class returntype = null;
  private String[] parameternames=new String[0];
  private synthetic.Class[] parametertypes=new synthetic.Class[0];
  private synthetic.Class[] exceptiontypes=new synthetic.Class[0];;
  private int modifiers;
  protected String name=null; // for Methods
  
  // For synthesis:
  private StringBuffer body=null;
  private String language=null;
  
  // For reifying:
  Class[] realE,realP;

  /**
   * Insert the method's description here.
   * <p>
   * Creation date: (12-27-99 2:31:39 PM)
   * @param realConstructor java.lang.reflect.Constructor
   */
  public EntryPoint(synthetic.Class declaringclass)
  {
    this.declaringclass=declaringclass;
  }
  
  /** Nonpublic constructor. Wrap this to appropriate "real" type */
  protected EntryPoint(Object ep,synthetic.Class declaringclass) 
       throws IllegalArgumentException
  {
    
    realep=ep;
    this.declaringclass=declaringclass;
    if(ep instanceof java.lang.reflect.Method)
      {
        java.lang.reflect.Method m=(java.lang.reflect.Method)ep;
        if(declaringclass==null)
	  {
            declaringclass=synthetic.Class.forClass(m.getDeclaringClass());
	  }
	name=m.getName();
	modifiers=m.getModifiers();
	returntype=synthetic.Class.forClass(m.getReturnType());
	realP=m.getParameterTypes();
	realE=m.getExceptionTypes();
      }
    else if(ep instanceof java.lang.reflect.Constructor)
      {
        java.lang.reflect.Constructor c=(java.lang.reflect.Constructor)ep;
        if(declaringclass==null)
	  {
            declaringclass=synthetic.Class.forClass(c.getDeclaringClass());
	  }
	name=declaringclass.getShortName();
	modifiers=c.getModifiers();
	returntype=declaringclass;
	realP=c.getParameterTypes();
	realE=c.getExceptionTypes();
      }
    else
      throw new IllegalArgumentException();
  }
  
  /** Nonpublic constructor. Wrap this to appropriate "real" type */
  protected EntryPoint(Object ep) 
       throws IllegalArgumentException
  {
    this(ep,null);
  }
  
  /**
    Compares this against the specified
    object. Returns true if the objects are the same.
    Two EntryPoints are the same if they were
    declared by the same class, have the same name
    (or are both ctors) and have the same
    formal parameter types. 
    */
  public boolean equals(Object obj)
  {
    EntryPoint otherep=null;
    if (obj instanceof EntryPoint)
      otherep=(EntryPoint)obj;
    else if (obj instanceof java.lang.reflect.Constructor ||
	     obj instanceof java.lang.reflect.Method)
      otherep = (EntryPoint)obj;
    
    return (otherep!=null 
	    && ((this instanceof Constructor && otherep instanceof Constructor)
		||
		(this instanceof Method && otherep instanceof Method &&
		 this.getName().equals(otherep.getName()) ))
	    && otherep.getDeclaringClass().equals(declaringclass) 
	    && otherep.getParameterTypes().equals(parametertypes));
  }
  
  /**
    Returns the Class object representing the class that
    declares the constructor represented by this
    Constructor object. 
    */
  public synthetic.Class getDeclaringClass()
  {
    return declaringclass;
  }
  
  /**
    Returns the Class object representing the class that
    will be returned by this EntryPoint. Needed by the Method
    API, but made meaningful for Constructors as well.
    */
  public synthetic.Class getReturnType()
  {
    return returntype;
  }
  
  /**
    Returns an array of Class objects that represent the
    types of the checked exceptions thrown by the
    underlying constructor represented by this
    Constructor object. Returns an array of length 0 if
    the constructor throws no checked exceptions. 
    */
  public synthetic.Class[] getExceptionTypes()
  {
    if(realep!=null && exceptiontypes==null)
      {
	exceptiontypes=new synthetic.Class[realE.length];
	for(int i=0;i<realE.length;++i)
	  exceptiontypes[i]=synthetic.Class.forClass(realE[i]);
	realE=null;
      }

    return exceptiontypes;
  }
  
  public void addExceptionType(synthetic.Class exception)
  throws SynthesisException
  {
    if(realep!=null)
        throw new SynthesisException(SynthesisException.REIFIED);
        
    synthetic.Class[] e=new synthetic.Class[exceptiontypes.length+1];
    System.arraycopy(exceptiontypes,0,e,0,exceptiontypes.length);
    e[exceptiontypes.length]=exception;
	exceptiontypes=e;
  }
  
  /**
    Returns the Java language modifiers for the
    constructor represented by this Constructor object,
    as an integer. The Modifier class should be used to
    decode the modifiers. 
    
    @see 
    Modifier 
    */
  public int getModifiers()
  {
    return modifiers;
  }
  /**
   * Member method. C'tor's name is always that of the defining class.
   * Methods have a "real" name.
   * Creation date: (12-25-99 1:32:06 PM)
   * @return java.lang.String
   */
  public java.lang.String getName() {
    if(this instanceof Constructor)
        return declaringclass.getShortName();
    return name;
  }
  
  /**
   * Member method. C'tor's name is always that of the defining class.
   * Methods have a "real" name.
   * Creation date: (12-25-99 1:32:06 PM)
   * @return java.lang.String
   */
  public void setName(String name) 
       throws SynthesisException
  {
    if(realep!=null)
      throw new SynthesisException(SynthesisException.REIFIED);
    
    this.name=name;
  }
  /**
    Returns an array of Class objects that represent the
    formal parameter types, in declaration order, of the
    constructor represented by this Constructor object.
    Returns an array of length 0 if the underlying
    constructor takes no parameters. 
    */
  public synthetic.Class[] getParameterTypes()
  {
    if(realep!=null && parametertypes==null)
      {
	    parametertypes=new synthetic.Class[realP.length];
	    for(int i=0;i<realP.length;++i)
	    parametertypes[i]=synthetic.Class.forClass(realP[i]);
	    realP=null;
      }
  
    return parametertypes;
  }

  public String[] getParameterNames()
  {
    return parameternames;
  }

  
  public void addParameter(synthetic.Class type,String name)
  throws SynthesisException
  {
    if(realep!=null)
        throw new SynthesisException(SynthesisException.REIFIED);

    synthetic.Class[] types=new synthetic.Class[parametertypes.length+1];
    System.arraycopy(parametertypes,0,types,0,parametertypes.length);
    types[parametertypes.length]=type;
    parametertypes=types;
    
    String[] names=new String[parameternames.length+1];
    System.arraycopy(parameternames,0,names,0,parameternames.length);
    names[parameternames.length]=name;
    parameternames=names;
  }
  /**
    Returns a hashcode for this Constructor. The
    hashcode is the same as the hashcode for the
    underlying constructor's declaring class name,
    xor'ed (for Methods) with the method name.
    (Implemented in the subclasses rather than here.)
    */
  abstract public int hashCode();
  
  /**
    Assert the Class object representing the class that
    declares the constructor represented by this
    Constructor object.
    */
  public void setDeclaringClass(synthetic.Class declaringClass)
       throws SynthesisException
  {
    if(realep!=null)
      throw new SynthesisException(SynthesisException.REIFIED);
    this.declaringclass=declaringClass;
  }
  /**
  ***** Should only be accepted before a "real" entrypoint is bound.
  * Creation date: (12-25-99 1:28:28 PM)
  * @return int
  * @param modifiers int
  */
  public void setModifiers(int modifiers) 
       throws SynthesisException
  {
    if(realep!=null)
      throw new SynthesisException(SynthesisException.REIFIED);
    
    this.modifiers=modifiers;
  }
  /**
    Return a string describing this Constructor. The
    string is formatted as the constructor access
    modifiers, if any, followed by the fully-qualified
    name of the declaring class, followed by a
    parenthesized, comma-separated list of the
    constructor's formal parameter types. For example:
    <code>
    public java.util.Hashtable(int,float)
    </code>
    <p>
    The only possible modifiers for constructors are
    the access modifiers public, protected or
    private. Only one of these may appear, or none
    if the constructor has default (package) access. 
    <p>
    Methods will also display their checked exceptions.
    */
  public String toString()
  {
    StringBuffer sb=
      new StringBuffer(java.lang.reflect.Modifier.toString(getModifiers()));
    
    if(this instanceof synthetic.reflection.Method)	    
      sb.append(' ').append(getReturnType())
	.append(getDeclaringClass().getName())
	.append('.').append(getName());
    else
      sb.append(getDeclaringClass().getName());
    sb.append('(');
    synthetic.Class[] p=getParameterTypes();
    if(p!=null && p.length>0)
      {
	sb.append(p[0].getName());
	for(int i=1;i<p.length;++i)
	  sb.append(',').append(p[i].getName());
      }
    sb.append(')');
    if(this instanceof synthetic.reflection.Method)	    
      {
	p=getExceptionTypes();
	if(p!=null && p.length>0)
	  {
	    sb.append(" throws ").append(p[0].getName());
	    for(int i=1;i<p.length;++i)
	      sb.append(',').append(p[i].getName());
	  }
      }
    return sb.toString();
  }
  
  /** Extension: For synthesis, we need a place to hang a
    method body.
    */
  public void setBody(String language,StringBuffer body)
       throws SynthesisException
  {
    if(realep!=null)
      throw new SynthesisException(SynthesisException.REIFIED);
    this.language=language;
    this.body=body;
  }
  /** Extension: For synthesis, we need a place to hang a
    method body. Note that this returns a mutable object,
    for editing etc. Slightly sloppy first cut.
    */
  public StringBuffer getBody()
  {
    if(body==null)
        body=new StringBuffer();
    return body;
  }
  /** Extension: For synthesis, we need a place to hang a
    method body.
    */
  public String getLanguage()
  {
    return language;
  }
  
  /** Generate Java code
   */
  public String toSource(String basetab)
  {
    StringBuffer sb=new StringBuffer();
    sb.append(basetab)
      .append(java.lang.reflect.Modifier.toString(getModifiers()));
    if(this instanceof synthetic.reflection.Method)
    {
        if (returntype!=null)
          sb.append(" ").append(getReturnType().getJavaName());
        else
          sb.append(" void");
    }
    sb.append(" ").append(getName())
      .append("(");
    
    synthetic.Class[] types=getParameterTypes();
    if(types!=null & types.length>0)
      {
        sb.append(types[0].getJavaName());
        if(parameternames!=null)
	  sb.append(' ').append(parameternames[0]);
        for(int i=1;i<types.length;++i)
	  {
            sb.append(',').append(types[i].getJavaName());
            if(parameternames!=null)
	      sb.append(' ').append(parameternames[i]);
	  }
      }
    sb.append(')');
    
    types=getExceptionTypes();
    if(types!=null & types.length>0)
      {
        sb.append(" throws ").append(types[0].getJavaName());
        for(int i=1;i<types.length;++i)
	  {
            sb.append(',').append(types[i].getJavaName());
	  }
      }
    
    if(body==null)
      sb.append("; // No method body available\n");
    else
      {
        sb.append("\n"+basetab+"{\n");
        if(language==null || "java".equals(language))
	  {
            sb.append(basetab+"// ***** Should prettyprint this code...\n");
            sb.append(basetab+body+"\n");
	  }
        else
	  {
            sb.append(basetab+"// ***** Generate BSF invocation!?\n");
	  }
        sb.append(basetab+"}\n");
        
      }
    return sb.toString();
  }
  
}
