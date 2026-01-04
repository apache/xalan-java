/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.xalan.tests.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class definition, that replaces text content within designated files 
 * starting at a particular file system root folder.
 * 
 * This helps with, bulk text string replacements within files, manually.
 * 
 * @author Mukul Gandhi <mukulg@apache.org>
 * 
 * @xsl.usage advanced
 */
public class TextReplaceUtil {

	// List variable, to have names of file extensions that needs
	// to be considered by this application.
	private static List<String> fileExts = new ArrayList<String>();
	
	/**
	 * Main method of this class.
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Incorrect program invocation");
			System.out.println("Usage : java TextReplaceUtil <folder_root> srcStr replStr");
		}
		else {
			String fileSystemFolderRoot = args[0];
			String srcStr = args[1];
			String replStr = args[2];
			
			TextReplaceUtil textReplaceUtil = new TextReplaceUtil();
			textReplaceUtil.applnInit();
			try {
			   textReplaceUtil.filesTextReplaceProcess(fileSystemFolderRoot, srcStr, replStr);
			}
			catch(IOException ex) {
			   ex.printStackTrace();	
			}
		}
	}
	
	/**
	 * Method definition, to initialize this application, by specifying 
	 * file extensions that needs to be considered, for text replacement.
	 */
	private void applnInit() {
	   fileExts.add(".html");
	   fileExts.add(".properties");
	   fileExts.add(".java");	   
	}
	
	/**
	 * Method definition, to do files text replacement by traversing file system
	 * recursively starting at a particular file system folder location.
	 */
	private void filesTextReplaceProcess(String fileSystemFolderRoot, String srcStr, 
			                             String replStr) throws IOException {
		File folderObj = new File(fileSystemFolderRoot);
		
		File[] filesList1 = folderObj.listFiles();
		if (filesList1 != null) {
			for (int idx1 = 0; idx1 < filesList1.length; idx1++) {
			   File fileObj = filesList1[idx1];		   
			   if (!fileObj.isDirectory() && isFileValidCandidateForTextReplacement(fileObj)) {
				   // This file object is a file and not directory. Doing
				   // text replacement of this file.
				   String fileStr = getStrContentOfFile(fileObj);
				   fileStr = fileStr.replace(srcStr, replStr);
				   fileObj.delete();
				   
				   FileOutputStream fos = new FileOutputStream(fileObj);
				   fos.write(fileStr.getBytes());			   
				   fos.flush();
				   fos.close();
			   }
			   else {
				  // Recursive class to this function
				  filesTextReplaceProcess(fileObj.getAbsolutePath(), srcStr, replStr);
			   }
			}
		}
	}
	
	/**
	 * Method definition, to get content of file as string.
	 */
	private String getStrContentOfFile(File file) throws IOException {
	   String fileStr = null;
	   
	   FileInputStream fileInpStream = new FileInputStream(file);
	   
	   int fileChar;
	   StringBuffer strBuff = new StringBuffer();
	   while((fileChar = fileInpStream.read()) != -1) {
		  char chr = (char)fileChar;
		  strBuff.append(chr);
	   }
	   
	   fileInpStream.close();
	   
	   fileStr = strBuff.toString(); 
	   
	   return fileStr;
	}
	
	/**
	 * Method definition, to determine whether, file is an appropriate 
	 * candidate file for text replacement.
	 */
	private boolean isFileValidCandidateForTextReplacement(File fileObj) {
	   boolean result = false;
	   
	   String fileAbsPathStr = fileObj.getAbsolutePath();
	   Iterator<String> iter = fileExts.iterator();
	   while(iter.hasNext()) {
		  String allowedFileExt = iter.next();
		  if (fileAbsPathStr.endsWith(allowedFileExt)) {
			 result = true;
			 break;
		  }
	   }
	   
	   return result;
	}

}
