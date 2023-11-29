package org.apache.xalan;

/**
 * Provides tests in other packages with access to (package) protected methods
 * in class {@link Version}, passing through parameters and results
 */
public class VersionAccessor {
  /**
   * Provides tests in other packages with access to (package) protected method
   * {@link Version#parseVersionNumber(String)}, passing through parameters and
   * results
   */
  public static void parseVersionNumber(String version) {
    Version.parseVersionNumber(version);
  }
}
