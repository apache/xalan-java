// Synthetic Class descriptors ("reverse reflection")
// Copyright ©2000 International Business Machines Corportation
// All rights reserved.
package synthetic.reflection;
import synthetic.SynthesisException;

/**
Member is an interface that reflects identifying
information about a single member (a field or a method)
or a constructor.
<p>
Note that this is <strong>not</strong> currently derived from
java.lang.reflect.Member, due to questions about how to handle
declarignClass.

@see Field
@see Method
@see Constructor
@see synthetic.Class
@see java.lang.reflect.Member
*/
public interface Member
{
/**
Returns the Class object representing the class or
interface that declares the member or constructor
represented by this Member.
*/
public abstract synthetic.Class getDeclaringClass();
/**
Returns the Java language modifiers for the
member or constructor represented by this
Member, as an integer. The Modifier class should
be used to decode the modifiers in the integer. 

@see Modifier
**/
public abstract int getModifiers();
/**
Returns the Class object representing the class or
interface that declares the member or constructor
represented by this Member. 
*/
public abstract void setDeclaringClass(synthetic.Class declaringClass)
throws SynthesisException;
/**
Returns the Java language modifiers for the
member or constructor represented by this
Member, as an integer. The Modifier class should
be used to decode the modifiers in the integer. 

@see Modifier
**/
public abstract void setModifiers(int modifiers) 
throws SynthesisException;
}
