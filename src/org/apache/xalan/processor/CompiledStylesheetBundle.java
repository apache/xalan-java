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
package org.apache.xalan.processor;

import java.net.URL;
import java.util.Stack;
import java.util.Vector;
import java.io.File;
import java.util.StringTokenizer;
import org.apache.xalan.templates.Constants;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemLiteralResult;
import org.apache.xalan.templates.ElemAttributeSet;
import org.apache.xalan.templates.ElemAttribute;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.XMLNSDecl;
import org.apache.trax.ProcessorException;
import org.apache.trax.TemplatesBuilder;
import org.apache.trax.Templates;
import org.apache.trax.TransformException;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathFactory;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.Function;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.utils.PrefixResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// Java Compiler support. *****
// TODO: Merge the Microsoft VJ++ workarounds in this file into that one.
import org.apache.xalan.utils.synthetic.JavaUtils;

/**
 * <meta name="usage" content="advanced"/>
 * File save and reload for Compiled Stylesheets.
 * @see CompilingStylesheetHandler
 */
public class CompiledStylesheetBundle
{
	public CompiledStylesheetBundle()
	{
	}
	
	// Create an executable bundle -- a zipfile
	// containing the serialized root, the synthesized classes needed
	// to support it, and a main entry point which retrieves these.
	//
	// ***** ISSUE: Need to make sure we're looking in the same directory
	// that the classes were generated into. Currently that's ".". Should
	// be parameterized both during compilation and here.
	// ***** ISSUE: What filename to write to? Hardcoded for initial test,
	// should be taken from stylesheet's Public/System IDs. Stand-alone
	// loader entry point ditto?
	// ***** ISSUE: ZIP or JAR? Zipfile support is in Java 1.1.x, direct
	// jarfile doesn't appear until 1.2. Main difference is that latter
	// automatically produces manefest, but a manefest is optional anyway.
  static void createBundle(Stylesheet root,Vector compiledTemplates)
  {
	String outdir="."+File.separator;
	java.util.Hashtable dirs=new java.util.Hashtable();		

	try {
		java.io.FileOutputStream f=
			new java.io.FileOutputStream(outdir+"CompiledStylesheet.zip");
		java.util.zip.ZipOutputStream zf=new java.util.zip.ZipOutputStream(f);
		zf.setMethod(zf.DEFLATED);
		
		// Copy in the classfiles
		byte buffer[]=new byte[4096];
		java.util.zip.ZipEntry ze;
		for(int i=compiledTemplates.size()-1;i>=0;--i)
		{
			// WARNING: I'm assuming that a package name has been specified,
			// which should be safe in this case. If that changes, this
			// lazy parse needs to be improved.
			Class c=compiledTemplates.elementAt(i).getClass();
			String fullname=c.getName();
			int start=fullname.lastIndexOf(".");
			String packagename=fullname.substring(0,start);
			String shortname=fullname.substring(start+1);
			// Need to convert as a relative path for the jarfile;
			// it's "anchored" later when we open for read.
			String source=packageNameToDirectory(packagename,outdir,File.separator)
				+shortname+".class";
			String sink=packageNameToDirectory(packagename,"","/")
				+shortname+".class";

// I'm not sure whether this is needed or not. The JDK I'm using seems to say
// it isn't, but I want to keep it around for now in case another JDK
// disagrees.
if(false)			
{	
			for(int slash=sink.indexOf('/');
				slash>=0;
				slash=sink.indexOf('/',slash+1))
			{
				String dirname=sink.substring(0,slash+1);
				if(dirs.get(dirname) == null)
				{
					ze=new java.util.zip.ZipEntry(dirname);
					zf.putNextEntry(ze);
					zf.closeEntry();
					dirs.put(dirname,dirname);
				}
			}
}				
			
 			ze=new java.util.zip.ZipEntry(sink);
			zf.putNextEntry(ze);
			java.io.FileInputStream fis=new java.io.FileInputStream(source);
			int count=0;
			for(int got=1;got>=0;)
			{
				got=fis.read(buffer);
				if(got>0)
				{
					zf.write(buffer,0,got); 
					count+=got;
				}
			}
			fis.close();
			ze.setSize(count); // Ought to get set automagically, BUT....
			zf.closeEntry();
		}
		
		// Write out the serialized stylesheet
		ze=new java.util.zip.ZipEntry("Stylesheet.ser");
		zf.putNextEntry(ze);
		java.io.ObjectOutputStream of=new java.io.ObjectOutputStream(zf);
		of.writeObject(root);
		of.flush(); // ***** Should this be close?
		zf.closeEntry();
		
		// TODO: *****MAKE SELF-LOADABLE!*****
		// Set up a Classloader pointed at the zipfile and deserialize from?
		
		// This may be overkill, but I'm getting complaints about 
		// "not a ZIP file (END header not found"...
		zf.finish();
		zf.flush();
		zf.close();
		f.flush();
		f.close();
	  }
	  catch(java.io.IOException e)
	  {
		  System.err.println("Exception while packaging compiled stylesheet");
		  e.printStackTrace(System.err);
	  }
  }
  
	// Reload an executable bundle -- a zipfile or jarfile
	// containing the serialized root, the synthesized classes needed
	// to support it, and a main entry point which retrieves these.
	// 
	// Note use of a custom classloader in order to pick these up interactively.
  public Stylesheet loadBundle(String filename)
	  throws java.io.IOException,java.lang.ClassNotFoundException
  {
	  Stylesheet ss=null;

		java.io.InputStream is;
		java.io.ObjectInputStream os;
		if(true)
		{
			java.lang.ClassLoader cl=new ZipfileClassLoader(filename);
			is=cl.getResourceAsStream("Stylesheet.ser");
			os=new ClassLoaderObjectInputStream(cl,is);
		}
		else
		{
			is=this.getClass().getResourceAsStream("/Stylesheet.ser");
			os=new java.io.ObjectInputStream(is);
		}
				
		ss=(Stylesheet)os.readObject();
		os.close();
		is.close();

	 return ss;
  }
  
  
  static String packageNameToDirectory(String packagename,String baseLocation,
									   String separator)
  {
    int fnstart=baseLocation.lastIndexOf(separator);
    StringBuffer subdir=new StringBuffer(
        (fnstart>=0)
        ? baseLocation.substring(0,fnstart+1)
        : "");
    StringTokenizer parts=
        new StringTokenizer(packagename,".");
    while(parts.hasMoreTokens())
        subdir.append(parts.nextToken()).append(separator);
	return subdir.toString();
  }

  // Custom classloader is needed if we want to load from a
  // zipfile which wasn't on the classpath at startup time.
  class ZipfileClassLoader extends ClassLoader 
  {
	String filename;
	java.util.zip.ZipFile zip=null;
	
	ZipfileClassLoader(String filename) throws java.io.IOException
	{
		this.filename=filename;
		zip=new java.util.zip.ZipFile(filename); 
	}

	// TODO: ***** RECONSIDER EFFECTIVE CLASSPATH
	// What I really want is to try system classes, than zipfile, then
	// user classpath. JDK 1.2 seems to have hooks that will support that,
	// but I'm not sure about JDK 1.1.
	public Class findClass(String name) 
	{
		Class c=null;
		try
		{
			c=this.getClass().forName(name);
		}
		catch (ClassNotFoundException e)
		{
			byte[] b = loadClassData(name);
			if(b!=null)
				c=defineClass(name,b,0,b.length);
		}
		return c;
	}
		
	private byte[] loadClassData(String name) 
	{
		int start=name.lastIndexOf(".");
		String packagename=name.substring(0,start);
		String shortname=name.substring(start+1);
		// Need to convert as a relative path for the jarfile;
		// it's "anchored" later when we open for read.
		String fn=packageNameToDirectory(packagename,"","/")
				+shortname+".class";
		
		byte[] data=null;
		try 
		{
			java.util.zip.ZipEntry entry=zip.getEntry(fn);
			
			// If it isn't in the zipfile, we can't retrieve any data
			if(entry!=null)
			{
				// This assumes entry length fits in an int. Should be safe.
				int bufsize = (int)entry.getSize();
				data=new byte[bufsize];
				java.io.InputStream is=zip.getInputStream(entry);
				int len=0, off=0;
				while (off<bufsize){
					len = is.read(data,off,bufsize-off);
					off += len;
				}
				is.close();
			}
		}
		catch(java.io.IOException e)
		{
		  System.err.println("Exception while reloading compiled stylesheet");
		  e.printStackTrace(System.err);
		}
		return data;
    }
	
	public synchronized Class loadClass(String name, boolean resolve) 
	throws ClassNotFoundException
	{
		Class c=findClass(name);
		if(c!=null && resolve==true)
			resolveClass(c);
		return c;
	}
		
	public java.io.InputStream getResourceAsStream(String name)
	{
		java.io.InputStream is=null;
		try 
		{
			java.util.zip.ZipEntry entry=zip.getEntry(name);
			is=zip.getInputStream(entry);
		}
		catch(java.io.IOException e)
		{
		  System.err.println("Problem loading compiled stylesheet");
		  e.printStackTrace();
		  // TODO: ***** Should this fall back to default classloader?
		  // is=this.getClass().getResourceAsStream(name);
		}
		return is;
	}
  }

  // Kluge: Check a specific classloader when deserializing. Alternative
  // is to dump a stub object into the zipfile and have it do the deserialize
  // ... which doesn't strike me as being any prettier.
  class ClassLoaderObjectInputStream extends java.io.ObjectInputStream
  {
	java.lang.ClassLoader cl;
	public ClassLoaderObjectInputStream(java.lang.ClassLoader cl,java.io.InputStream is)
		throws java.io.IOException
	{
		super(is);
		this.cl=cl;
	}

	// Note that this assumes cl knows how to cascade to other classloaders
	// as appropriate, since it has to resolve _all_ classes that may be
	// referenced in the object stream -- including system classes.
	protected Class resolveClass(java.io.ObjectStreamClass v)
		throws java.io.IOException, ClassNotFoundException
	{
		return cl.loadClass(v.getName());
	}
  }
  
}
