// Synthetic Class descriptors ("reverse reflection")
// Copyright ©2000 International Business Machines Corportation
// All rights reserved.

/** Test driver for com.ibm.synthetic
    <p>
    DEVELOPMENT NOTES:
    Defer construction of reified data. Probably don't need
    it all, and it builds up a significant-sized tree.

    toSource should probably be factored out into a separate
    java generator class, sharing an API with a BSC generator
    class.
*/

package com.ibm.synthetic;
import com.ibm.synthetic.Class;
import com.ibm.synthetic.reflection.*;

public class TestDriver
{
    public static int sampleField=32;
    private boolean inTest=false;
    
    public static void main(String[] args)
    {
        // Proxy a class
        try
        {
            System.out.println("Proxying java.awt.Frame...");
            Class myC=Class.forName("java.awt.Frame");
            myC.toSource(System.out,0);
            System.out.println("\nProxying synthetic.TestDriver...");
            myC=Class.forName("com.ibm.synthetic.TestDriver");
            myC.toSource(System.out,0);
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Couldn't proxy: ");
            e.printStackTrace();
        }

        
        // Start getting serious
        try
        {
            System.out.println("\nBuild a new beast...");
            Class myC=Class.declareClass("com.ibm.synthetic.BuildMe");
            Class inner=myC.declareInnerClass("island");
            inner.addExtends(Class.forName("java.lang.String"));
            Method m=inner.declareMethod("getValue");
            m.setReturnType(Class.forName("java.lang.String"));
            m.getBody().append("return toString();");
            myC.toSource(System.out,0);
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(SynthesisException e)
        {
            e.printStackTrace();
        }
        catch(IllegalStateException e)
        {
            System.out.println("Unwritten function: "+e);
            e.printStackTrace();
        }
    }

    public static void dumpClass(Class C)
    {
        System.out.println("toString(): "+C);
        System.out.println("\tisPrimitive(): "+C.isPrimitive());
        System.out.println("\tisInterface(): "+C.isInterface());
        System.out.println("\tisInstance(\"foo\"): "+C.isInstance("foo"));
        System.out.println("\tisArray(): "+C.isArray());
        System.out.println("\tgetRealClass(): "+C.getRealClass());
    }

    /* Test for something we plan to do in BSC */
    public void quickcheck()
    {
        Inner a=new Inner();
        a.setTest(!a.getTest());
    }
    private class Inner
    {
        public boolean getTest()
        {
            return inTest;
        }
        public void setTest(boolean test)
        {
            inTest=test;
        }
    }
    
}
