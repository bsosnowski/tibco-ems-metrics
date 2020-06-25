/**
 * Copyright (c) 2020 Bartosz Sosnowski
 * 
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.tibco.monitoring.metrics.ems;

import java.util.regex.Pattern;

public class Utils {

  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Method for sanitization of KPI value.
   */
  public static String sanitize(String input) {
    final Pattern whitespace = Pattern.compile("[\\s]+");
    final Pattern dot = Pattern.compile("\\.");
    final String dash = "-";
    final String hash = "##";
    String removedWhitespace = whitespace.matcher(input.trim()).replaceAll(dash);
    return dot.matcher(removedWhitespace.trim()).replaceAll(hash);
  }
    
}