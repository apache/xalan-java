/* $Id$ */

package synthetic;

import java.io.IOException;

public class JavaUtils
{
  // Debug flag - generates debug stuff if true.
  private static boolean debug = false;

  // Temporarily copied from JavaEngine...
  private static boolean cantLoadCompiler=false; // One-time flag for following

  // ADDED BY JKESS; callers want control over the -g option.
  public static void setDebug(boolean newDebug)
  {
    debug=newDebug;
  }

  public static boolean JDKcompile(String fileName, String classPath)
  {
    if (debug)
    {
      System.err.println ("JavaEngine: Compiling " + fileName);
      System.err.println ("JavaEngine: Classpath is " + classPath);
    }
    
    String option = debug ? "-g" : "-O";

    if(!cantLoadCompiler)
      {
  String args[] = {
    option,
    "-classpath",
    classPath,
    fileName
  };
  try
    {
      return new sun.tools.javac.Main(System.err, "javac").compile(args);
    }
  catch (Throwable th)
    {
      System.err.println("WARNING: Unable to load Java 1.1 compiler.");
      System.err.println("\tSwitching to command-line invocation.");
      cantLoadCompiler=true;
    }
      }
    
    // Can't load javac; try exec'ing it.
    String args[] = {
      "javac",
      option,
      "-classpath",
      classPath,
      fileName
    };
    try
      {
  Process p=java.lang.Runtime.getRuntime().exec(args);
  p.waitFor();
  return(p.exitValue()!=0);
      }
    catch(IOException e)
      {
  System.err.println("ERROR: IO exception during exec(javac).");
      }
    catch(SecurityException e)
      {
  System.err.println("ERROR: Unable to create subprocess to exec(javac).");
      }
    catch(InterruptedException e)
      {
  System.err.println("ERROR: Wait for exec(javac) was interrupted.");
      }
    return false;
  }
}
