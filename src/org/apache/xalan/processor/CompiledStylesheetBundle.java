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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathFactory;
import org.apache.xpath.compiler.XPathParser;
import org.apache.xpath.compiler.FunctionTable;
import org.apache.xpath.functions.Function;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xml.utils.PrefixResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.InputSource;

import javax.xml.transform.TransformerException;
import javax.xml.transform.ErrorListener;

/**
 * <meta name="usage" content="advanced"/>
 * Serialize and reload routines for Compiled Stylesheets. Basically, a
 * "bundle" is a zipfile containing the generated and compiled java 
 * classes, plus a .ser (ObjectStream) representation of those classes
 * as actually instantiated and connected to represent a top-level
 * Stylesheet. This class provides routines that generate a bundle in
 * a disk file, and that will reread a bundle from such a file.
 * 
 * @see CompilingStylesheetHandler
 */
public class CompiledStylesheetBundle
{
	/** Public constructor. The loadBundle() operation requires an actual
	 * compiledStylesheetBundle object, for reasons discussed there.
	 */
	public CompiledStylesheetBundle()
	{
	}
	
	/** Create an executable bundle -- a zipfile which contains
	* the serialized Root Stylesheet and the synthesized classes needed
	* to support it. Nominally this should be a jarfile, but zipfiles work
	* and jarfile support wasn't added to the JDK until 1.2.
	* <P>
	* The output file's name is currently derived from the Stylesheet's System
	* ID, by stripping out the "local" name and adding the suffix .xsb (Xalan
	* Stylesheet Bundle). If that approach doesn't work, we fall back on 
	* the name UnidentifiedStylesheet.xsb.
	* <P>
	* TODO: Open issues in CompiledStylesheetBundle.createBundle()
	* <ul>
	* <li>We need to copy classfiles from the directory that the code was
	* generated into. Currently that's hardwired as "." -- but when we
	* parameterize the code-gen operations we'll need to update this too.
	* <li>What directory should we output to? Same?
	* <li>Should we really be writing to a file, or should we be generating
	* a stream which the caller directs appropriately?
	* <li>Should we really be invoked as part of the compilation? Or are we
	* in fact a "serialization" mechanism for compiled stylesheets?
	* </ul>
	* 
	* @param Stylesheet root The compiled Root Stylesheet, after being
	* processed by CompilingStylesheetHandler's endDocument() method.
	* @param Vector compiledTemplates A list of the Class objects that were
	* generated to support this stylesheet. (Avoids rewalking the tree to
	* gather than information.)
	*/ 
	static void createBundle(Stylesheet root,Vector compiledTemplates)
	{
		//TODO: outdir should be parameterized  
		String outdir="."+File.separator;

		try 
		{
			// Output filename is based on the System ID of the stylesheet.
			// It's possible there isn't a useful ID (eg if the file
			// came from a stream); in that case I fall back on a standard
			// name as "better than nothing"... but folks really should
			// establish a real ID earlier in the process.
			String zipname=null;
			String systemID=root.getSystemId();
			if(systemID!=null)
			{
				// Try to parse the terminal name out of the System ID, which
				// may be a URI Reference (though I'd be much happier if it
				// could be counted on to be an Absolute URI instead!)
				// TODO: Should we strip off extension, or retain?
				// This may be a platform issue; repeated .'s aren't always
				// legal...
				int namestart=systemID.lastIndexOf('/')+1;
				int nameend=systemID.lastIndexOf('#',namestart);
				if(nameend<0)
					nameend=systemID.length();
				if(namestart<nameend)
					zipname=systemID.substring(namestart,nameend);
			}
			if(zipname==null)
				zipname="UnidentifiedStylesheet";
			
			java.io.FileOutputStream f=
				new java.io.FileOutputStream(outdir+zipname+".xsb");
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
				// Where to write the class within the jar/zipfile's
				// directory structure
				String sink=
					packageNameToDirectory(packagename,"",'/')
					+shortname+".class";
 				ze=new java.util.zip.ZipEntry(sink);
				zf.putNextEntry(ze);
				
				// It would be nice if we could read the bytecodes
				// from the existing Class, rather than going back to the
				// .class file, to save disk activity. But Java 
				// doesn't guarantee that the bytecodes are still in memory
				// -- a JIT may have discarded them after compilation. 
				// Asking the classloader to getResourceAsStream is our
				// "best bet" for any caching that may have occurred, and 
				// is the standard solution recommended by Sun.
				// PROBLEM: Java 1.1 security forbids this and throws an
				// exception. For compatability, I'm forced to use a fallback;
				// luckily, we _do_ know where we did our compilation.
				java.io.InputStream fis;
				if(false) // Works only >= Java 1.2
					fis=c.getClass().getResourceAsStream(sink);	
				else
				{				
					// Where to copy the classfile from. 
					String source=
						packageNameToDirectory(packagename,outdir,File.separatorChar)
						+shortname+".class";
					fis=new java.io.FileInputStream(source);
				}

				// Need to count how many bytes are transferred; the ZipEntry
				// does _NOT_ set itself automatically.
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
				ze.setSize(count); // Should be set automagically, but isn't.
				zf.closeEntry();
			}
		
			// Write out the serialized stylesheet tree which uses the classes
			// we've just saved.
			// TODO: Should this name be related to the stylesheet name/filename?
			ze=new java.util.zip.ZipEntry("Stylesheet.ser");
			zf.putNextEntry(ze);
			java.io.ObjectOutputStream of=new java.io.ObjectOutputStream(zf);
			of.writeObject(root);
			of.flush();
			zf.closeEntry();
		
			// TODO: Should bundle be self-loadable (generated entry point)?
		
			// TODO: The following is undoubtedly massive overkill, but I had
			// problems with "not a ZIP file (END header not found)"...
			zf.finish();
			zf.flush();
			zf.close();
			f.flush();
			f.close();
		}
		catch(java.io.IOException e)
		{
			// TODO: Improve bundling diagnostics
			System.err.println("Exception while bundling compiled stylesheet");
			e.printStackTrace(System.err);
		}
	}
  
	/** Reload an executable bundle -- a zipfile or jarfile
	* containing the serialized root and the synthesized classes needed
	* to support it, and a main entry point which retrieves these.
	* A custom classloader is used so we can pick these up interactively,
	* along with a customized version of ObjectInputStream that can be told
	* which classloader to consult.
	* <p>
	* TODO: Open issues in CompiledStylesheetBundle.loadBundle()
	* <ul>
	* <li>At the moment this is an instance method, because the support
	* objects mentioned above are declared as inner classes. They're arguably
	* general enough to be worth factoring out...
	* <li>Should this be restructured to be able to read from other kinds of
	* streams? Would require significant changes to the classloader, to preload
	* it from the stream rather than having it access the zipfile on demand.
	* </ul>
	* 
	* @param String filename Filesystem name of the bundle file to be loaded.
	* @return Stylesheet as loaded.
	* @throws java.io.IOException if there are any problems reading the
	* bundle file
	* @throws java.lang.ClassNotFoundException if a class used in 
	* serializing the Stylesheet can't be resolved. This could be a glitch
	* in the bundle, but is more likely to be incompatable versions if the
	* Xalan code was changed between when the bundle was created and when it
	* is being reloaded.
	*/
	public javax.xml.transform.Templates loadBundle(String filename)
		throws java.io.IOException,java.lang.ClassNotFoundException
	{
		java.io.InputStream is=null;
		java.io.ObjectInputStream os=null;
		javax.xml.transform.Templates ss=null;
		
		try
		{
			// Create the custom classloader which will consult the bundle
			// TODO: Ponder whether to cache ZipfileClassLoader
			java.lang.ClassLoader cl=new ZipfileClassLoader(filename,false);
			// Read the .ser file from the bundle, via that classloader		
			is=cl.getResourceAsStream("Stylesheet.ser");
			// Read objects from the .ser, loading from bundle if necessary
			os=new ClassLoaderObjectInputStream(cl,is);
			ss=(javax.xml.transform.Templates)os.readObject();
		}
		finally
		{
			// Whether read succeeded or not, release the file resources
			if(os!=null) os.close();
			if(is!=null) is.close();
		}
		return ss;
	}
  
	/** Utility function: Take a dot-delimited packagename and a base
	 * directory, and return a relative directory suitable for locating
	 * .java and .class files. 
	 * TODO: This should be moved somewhere more reusable,
	 * probably into the compiler-invocation support stuff or synthetic.Class.
	 * 
	 * @param String packagename dot-delimited packagename, as you'd write it
	 * in the Java package statement.
	 * @param String baseLocation Base directory, appended to the front of the
	 * returned value. We discard anything after the last instance of the 
	 * separator, to allow passing in a filename ... but that means that if
	 * you want to pass in a directory, it's your responsibility to make
	 * sure it ends in the separator character.
	 * TODO: Clean up directory parsing a bit...
	 * @param char separator Separator into which the '.'s are converted.
	 * This is a parameter because I'm using this function to work with
	 * zipfiles (which insist on '/') as well as the normal filesystem
	 * (which varies from platform to platform).
	 * @return directory name, ending in separator
	 */  
	static String packageNameToDirectory(String packagename,String baseLocation,
									   char separator)
	{
		int fnstart=baseLocation.lastIndexOf(separator);
	    StringBuffer subdir=new StringBuffer(
		    (fnstart>=0)
			? baseLocation.substring(0,fnstart+1)
			: "");
		subdir.append(packagename.replace('.',separator)).append(separator);
		return subdir.toString();
	}
	
	//=================================================================

	/** This is a quick-and-dirty classloader which effectively appends
	 * the specified zipfile to the tail end of the existing classpath
	 * (by consulting the ClassLoader which loaded _it_ first, and only
	 * then attempting to load from the file.)
	 * This allows us to read from files -- in particular, from 
	 * CompiledStylesheetBundles -- which weren't available at the time the
	 * applications was launched.
	 * <p>
	 * TODO: BEHAVIOR IS INCOMPLETE; consider finishing it properly...
	 * TODO: This probably should be factored out as an independent object
	 * rather than an inner class. It's resuable.
	 * TODO: In Java 1.2.x, this can be replaced to a greater or lesser extent
	 * by URIClassLoader.
	 */
	class ZipfileClassLoader extends ClassLoader 
	{
		java.util.zip.ZipFile zip=null;
		java.util.Hashtable cache;
	
		/** Constructor.
		 * TODO: Should we be able to load from stream as well as string?
		 * That would require a preload solution or a random-access stream...
		 * OK for our simple case where we know we're going to use everything
		 * eventually, not so hot for others.
		 * <p>
		 * There's an open question here re caching. Fact is, the custom
		 * classes in a compiled stylesheet are currently used once per 
		 * load of that stylesheet, so the cache wouldn't buy us much. 
		 * And if we cache, we also have to think about when to _release_ 
		 * the cached objects. So I'm making this optional...
		 * 
		 * @param String filename Name of the zipfile to be read
		 * @param boolean cached True iff you want classes cached for reuse
		 */	
		public ZipfileClassLoader(String filename, boolean cached) throws java.io.IOException
		{
			zip=new java.util.zip.ZipFile(filename); 
			if(cached==true)
				cache=new java.util.Hashtable();
		}

		/** Find a class within the Zipfile. Note that this does not _resolve_
		* the class, and hence should probably not be used by the general public.
		* TODO: Reconsider the resulting effective classpath.
		* What I really want is to try system classes, than zipfile, then
		* user classpath. JDK 1.2 seems to have hooks that will support that,
		* but I'm not sure about JDK 1.1.
		*
		* @param String name fully-qualified classname (including package, if any)
		*/
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
		
		/** Internal subroutine: Given the name of a class (or of a resource),
		 * access the zipfile and retrieve the contents thereof as a byte array.
		 * TODO: Implement the "leading / means already in filename syntax" trick?
		 * Or is that irrelevant?
		 * 
		 * @param String name Name to be retrieved, in Java package syntax.
		 * @return byte[] with full contents of "file".
		 */
		private byte[] loadClassData(String name) 
		{		
			int start=name.lastIndexOf(".");
			String packagename=name.substring(0,start);
			String shortname=name.substring(start+1);
			// Need to convert as a relative path for the jarfile;
			// it's "anchored" later when we open for read.
			String fn=packageNameToDirectory(packagename,"",'/')
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
					while (off<bufsize)
					{
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
	
		/** This is one of the main entry points that makes it a "real"
		 * classloader.
		 * @see java.io.ClassLoader.loadClass
		 */
		public synchronized Class loadClass(String name, boolean resolve) 
		throws ClassNotFoundException
		{
			Class c=(cache==null)? null : (Class)cache.get(name);
			if(c==null)
			{
				c=findClass(name);
				if(c!=null && resolve==true)
					resolveClass(c);
				if(cache!=null)
					cache.put(name,c);
			}
			return c;
		}
			
		/** This is one of the main entry points that makes it a "real"
		* classloader.
		* @see java.io.ClassLoader.getResourceAsStream
		*/
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

	//===============================================================
	
	/** Modified ObjectInputStream which consults a specific ClassLoader
	 * rather than the default. This was required to allow reloading our
	 * CompiledStylesheetBundle files, which carry both a serialized
	 * object tree and the additional classfiles needed to support it.
	 * <p>
	 * An alternative solution would have been to put a stub object in
	 * the bundle file, explicitly load that (setting its default 
	 * classloader in the process), then have it load the rest of the data,
	 * but that requires another generated class and I'm not convinced it's
	 * really cleaner.
	 * <p>
	 * TODO: Make this a more complete implementation (other factories, eg)?
	 * TODO: Move this to somewhere more reusable.
	 */
	class ClassLoaderObjectInputStream extends java.io.ObjectInputStream
	{
		java.lang.ClassLoader cl;
		
		/** Public constructor.
		 * @param java.lang.ClassLoader cl The custom classloader to be used
		 * when deserializing these objects. Note that it is the classloader's
		 * responsibility to deliver _all_ classes for this stream; that will
		 * probably requre that it understand how to fall back upon a parent
		 * or default classloader.
		 * @param java.io.InputStream is The stream from which to read the
		 * serialized representation of the objects.
		 */
		public ClassLoaderObjectInputStream(java.lang.ClassLoader cl,java.io.InputStream is)
			throws java.io.IOException
		{
			super(is);
			this.cl=cl;
		}

		/** Overriding this routine is what allows us to consult the specified
		 * classloader rather than the default. Note that this assumes the
		 * classloader knows how to cascade to others as appropriate (and with
		 * the proper priority of search), since it has to resolve _all_ 
		 * classes that may be referenced in the object stream -- including
		 * system classes.
		 * <p>
		 * Should we have a "backup" fallback here? I'd say no;
		 * the user may want to distinguish whether the class came from
		 * the intended classloader, and it's easy enough to handle any
		 * fallbacks at that level.
		 */
		protected Class resolveClass(java.io.ObjectStreamClass v)
			throws java.io.IOException, ClassNotFoundException
		{
			return cl.loadClass(v.getName());
		}
	}

}
