// Synthetic Class descriptors ("reverse reflection")
// Copyright ©2000 International Business Machines Corportation
// All rights reserved.
package synthetic;

public class SynthesisException
extends Exception
{
    int code;
    
    // Manefest constants
    public static final int SYNTAX=0;
    public static final int UNSUPPORTED=1;
    public static final int REIFIED=2;
    public static final int UNREIFIED=3;
    public static final int WRONG_OWNER=4;
    public static final String[] errToString = 
    { 
        "(Syntax error; specific message should be passed in)",
        "Feature not yet supported",
        "Can't change features of 'real' class",
        "Can't yet instantiate/invoke without 'real' class",
        "Can't add Member to an object other than its declarer",
    };
    
    
    public SynthesisException(int code)
    {
        super(errToString[code]);
        this.code=code;
    }
    
    public SynthesisException(int code, String msg)
    {
        super(msg);
        this.code=code;
    }

    int getCode() {return code;}
    
}
