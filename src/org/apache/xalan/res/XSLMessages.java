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
package org.apache.xalan.res;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ListResourceBundle;
import java.util.MissingResourceException;

import org.apache.xpath.res.XPATHErrorResources;

/**
 * <meta name="usage" content="internal"/>
 * Sets things up for issuing error messages.  This class is misnamed, and
 * should be called XalanMessages, or some such.
 */
public class XSLMessages
{

  /** NEEDSDOC Field fLocale          */
  private Locale fLocale = Locale.getDefault();

  /** NEEDSDOC Field XSLTBundle          */
  private static XSLResourceBundle XSLTBundle = null;

  /** NEEDSDOC Field XPATHBundle          */
  private static XSLResourceBundle XPATHBundle = null;

  /** NEEDSDOC Field XSLT_ERROR_RESOURCES          */
  private static final String XSLT_ERROR_RESOURCES =
    "org.apache.xalan.res.XSLTErrorResources";

  /** NEEDSDOC Field XPATH_ERROR_RESOURCES          */
  private static final String XPATH_ERROR_RESOURCES =
    "org.apache.xpath.res.XPATHErrorResources";

  /** NEEDSDOC Field BAD_CODE          */
  private static String BAD_CODE = "BAD_CODE";

  /** NEEDSDOC Field FORMAT_FAILED          */
  private static String FORMAT_FAILED = "FORMAT_FAILED";

  /**
   *
   *
   * NEEDSDOC @param locale
   */
  public void setLocale(Locale locale)
  {
    fLocale = locale;
  }

  /**
   *
   *
   * NEEDSDOC ($objectName$) @return
   */
  public Locale getLocale()
  {
    return fLocale;
  }

  /**
   * Creates a message from the specified key and replacement
   * arguments, localized to the given locale.
   *
   * @param errorCode The key for the message text.
   * @param args      The arguments to be used as replacement text
   *                  in the message created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public static final String createXPATHWarning(int errorCode, Object args[])  //throws Exception 
  {

    if (XPATHBundle == null)
      XPATHBundle =
        (XSLResourceBundle) loadResourceBundle(XPATH_ERROR_RESOURCES);

    XSLResourceBundle fResourceBundle = XPATHBundle;

    if (fResourceBundle != null)
    {
      String msgKey = fResourceBundle.getWarningKey(errorCode);

      return createXPATHMsg(fResourceBundle, msgKey, args);
    }
    else
      return "Could not load any resource bundles.";
  }

  /**
   * Creates a message from the specified key and replacement
   * arguments, localized to the given locale.
   *
   * @param errorCode The key for the message text.
   * @param args      The arguments to be used as replacement text
   *                  in the message created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public static final String createXPATHMessage(int errorCode, Object args[])  //throws Exception 
  {

    if (XPATHBundle == null)
      XPATHBundle =
        (XSLResourceBundle) loadResourceBundle(XPATH_ERROR_RESOURCES);

    XSLResourceBundle fResourceBundle = XPATHBundle;

    if (fResourceBundle != null)
    {
      String msgKey = fResourceBundle.getMessageKey(errorCode);

      return createXPATHMsg(fResourceBundle, msgKey, args);
    }
    else
      return "Could not load any resource bundles.";
  }

  /**
   * Creates a message from the specified key and replacement
   * arguments, localized to the given locale.
   *
   * @param errorCode The key for the message text.
   *
   * NEEDSDOC @param fResourceBundle
   * NEEDSDOC @param msgKey
   * @param args      The arguments to be used as replacement text
   *                  in the message created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public static final String createXPATHMsg(XSLResourceBundle fResourceBundle,
                                            String msgKey, Object args[])  //throws Exception 
  {

    String fmsg = null;
    boolean throwex = false;
    String msg = null;

    if (msgKey != null)
      msg = fResourceBundle.getString(msgKey);

    if (msg == null)
    {
      msg = fResourceBundle.getString(XPATHErrorResources.BAD_CODE);
      throwex = true;
    }

    if (args != null)
    {
      try
      {

        // Do this to keep format from crying.
        // This is better than making a bunch of conditional
        // code all over the place.
        int n = args.length;

        for (int i = 0; i < n; i++)
        {
          if (null == args[i])
            args[i] = "";
        }

        fmsg = java.text.MessageFormat.format(msg, args);
      }
      catch (Exception e)
      {
        fmsg = fResourceBundle.getString(XPATHErrorResources.FORMAT_FAILED);
        fmsg += " " + msg;
      }
    }
    else
      fmsg = msg;

    if (throwex)
    {
      throw new RuntimeException(fmsg);
    }

    return fmsg;
  }

  /**
   * Creates a message from the specified key and replacement
   * arguments, localized to the given locale.
   *
   * @param errorCode The key for the message text.
   * @param args      The arguments to be used as replacement text
   *                  in the message created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public static final String createWarning(int errorCode, Object args[])  //throws Exception 
  {

    if (XSLTBundle == null)
      XSLTBundle =
        (XSLResourceBundle) loadResourceBundle(XSLT_ERROR_RESOURCES);

    XSLResourceBundle fResourceBundle = XSLTBundle;

    if (fResourceBundle != null)
    {
      String msgKey = fResourceBundle.getWarningKey(errorCode);

      return createMsg(fResourceBundle, msgKey, args);
    }
    else
      return "Could not load any resource bundles.";
  }

  /**
   * Creates a message from the specified key and replacement
   * arguments, localized to the given locale.
   *
   * @param errorCode The key for the message text.
   * @param args      The arguments to be used as replacement text
   *                  in the message created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public static final String createMessage(int errorCode, Object args[])  //throws Exception 
  {

    if (XSLTBundle == null)
      XSLTBundle =
        (XSLResourceBundle) loadResourceBundle(XSLT_ERROR_RESOURCES);

    XSLResourceBundle fResourceBundle = XSLTBundle;

    if (fResourceBundle != null)
    {
      String msgKey = fResourceBundle.getMessageKey(errorCode);

      return createMsg(fResourceBundle, msgKey, args);
    }
    else
      return "Could not load any resource bundles.";
  }

  /**
   * Creates a message from the specified key and replacement
   * arguments, localized to the given locale.
   *
   * @param errorCode The key for the message text.
   *
   * NEEDSDOC @param fResourceBundle
   * NEEDSDOC @param msgKey
   * @param args      The arguments to be used as replacement text
   *                  in the message created.
   *
   * NEEDSDOC ($objectName$) @return
   */
  public static final String createMsg(XSLResourceBundle fResourceBundle,
                                       String msgKey, Object args[])  //throws Exception 
  {

    String fmsg = null;
    boolean throwex = false;
    String msg = null;

    if (msgKey != null)
      msg = fResourceBundle.getString(msgKey);

    if (msg == null)
    {
      msg = fResourceBundle.getString(BAD_CODE);
      throwex = true;
    }

    if (args != null)
    {
      try
      {

        // Do this to keep format from crying.
        // This is better than making a bunch of conditional
        // code all over the place.
        int n = args.length;

        for (int i = 0; i < n; i++)
        {
          if (null == args[i])
            args[i] = "";
        }

        fmsg = java.text.MessageFormat.format(msg, args);
      }
      catch (Exception e)
      {
        fmsg = fResourceBundle.getString(FORMAT_FAILED);
        fmsg += " " + msg;
      }
    }
    else
      fmsg = msg;

    if (throwex)
    {
      throw new RuntimeException(fmsg);
    }

    return fmsg;
  }

  /**
   * Creates a message from the specified key and replacement
   * arguments, localized to the given locale.
   *
   * @param bundleName The name of the resource bundle to be
   *                  used.
   * @param errorCode The key for the message text.
   * @param args      The arguments to be used as replacement text
   *                  in the message created.
   *
   * NEEDSDOC ($objectName$) @return
   *
   * @throws Exception
   */
  public String createMessage(String bundleName, int errorCode, Object args[])
          throws Exception
  {

    boolean throwex = false;
    int majorCode;
    int minorCode;
    String fmsg = null;
    XSLResourceBundle aResourceBundle = null;

    aResourceBundle = (XSLResourceBundle) loadResourceBundle(bundleName);

    String msgKey = aResourceBundle.getMessageKey(errorCode);
    String msg = null;

    if (msgKey != null)
      msg = aResourceBundle.getString(msgKey);

    if (msg == null)
    {
      msg = aResourceBundle.getString(BAD_CODE);
      throwex = true;
    }

    if (args != null)
    {
      try
      {

        // Do this to keep format from crying.
        // This is better than making a bunch of conditional
        // code all over the place.
        int n = args.length;

        for (int i = 0; i < n; i++)
        {
          if (null == args[i])
            args[i] = "";
        }

        fmsg = java.text.MessageFormat.format(msg, args);
      }
      catch (Exception e)
      {
        fmsg = aResourceBundle.getString(FORMAT_FAILED);
        fmsg += " " + msg;
      }
    }
    else
      fmsg = msg;

    if (throwex)
    {
      throw new RuntimeException(fmsg);
    }

    return fmsg;
  }

  /**
   * Return a named ResourceBundle for a particular locale.  This method mimics the behavior
   * of ResourceBundle.getBundle().
   *
   * @param res the name of the resource to load.
   * @param locale the locale to prefer when searching for the bundle
   *
   * NEEDSDOC @param className
   * @return the ResourceBundle
   * @throws MissingResourceException
   */
  public static final ListResourceBundle loadResourceBundle(String className)
          throws MissingResourceException
  {

    Locale locale = Locale.getDefault();

    // String suffix = getResourceSuffix(locale);  
    try
    {

      //System.out.println("resource " +className+suffix);
      // first try with the given locale
      return (ListResourceBundle) ResourceBundle.getBundle(className, locale);
    }
    catch (MissingResourceException e)
    {
      try  // try to fall back to en_US if we can't load
      {

        // Since we can't find the localized property file,
        // fall back to en_US.
        return (ListResourceBundle) ResourceBundle.getBundle(
          XSLT_ERROR_RESOURCES, new Locale("en", "US"));
      }
      catch (MissingResourceException e2)
      {

        // Now we are really in trouble.
        // very bad, definitely very bad...not going to get very far
        throw new MissingResourceException(
          "Could not load any resource bundles." + className, className, "");
      }
    }
  }

  /**
   * Return the resource file suffic for the indicated locale
   * For most locales, this will be based the language code.  However
   * for Chinese, we do distinguish between Taiwan and PRC
   *
   * @param locale the locale
   * @return an String suffix which canbe appended to a resource name
   */
  private static final String getResourceSuffix(Locale locale)
  {

    String suffix = "_" + locale.getLanguage();
    String country = locale.getCountry();

    if (country.equals("TW"))
      suffix += "_" + country;

    return suffix;
  }
}
