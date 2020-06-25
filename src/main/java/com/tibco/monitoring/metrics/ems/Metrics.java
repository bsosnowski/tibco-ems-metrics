/**
 * Copyright (c) 2020 Bartosz Sosnowski
 * 
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.tibco.monitoring.metrics.ems;

import java.util.Timer;

public class Metrics {

  /**
   * Main static methods of the application.
   * @param args input arguments
   */
  public static void main(String[] args) {
    new Config();
    Collector collector = new Collector();
    Timer timer = new Timer();
    timer.schedule(collector, 0, Config.getInstance().getMetricsIntervalMillis());
  }

}