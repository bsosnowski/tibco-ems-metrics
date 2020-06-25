/**
 * Copyright (c) 2020 Bartosz Sosnowski
 * 
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.tibco.monitoring.metrics.ems;

import com.tibco.monitoring.metrics.ems.adapter.GraphiteAdapter;
import com.tibco.monitoring.metrics.ems.adapter.TibcoEmsAdapter;
import com.tibco.monitoring.metrics.ems.mapper.GraphiteMapper;
import com.tibco.monitoring.metrics.ems.model.TibcoEmsMetrics;

import java.time.Instant;
import java.util.List;
import java.util.TimerTask;

public class Collector extends TimerTask {

  TibcoEmsAdapter emsAdapter;
  GraphiteAdapter graphiteAdapter;

  /**
   * Default constructor for the Collector.
   */
  public Collector() {
    this.emsAdapter = new TibcoEmsAdapter(Config.getInstance().getEmsConnectionAdminUrl(), 
      Config.getInstance().getEmsConnectionAdminUser(), 
      Config.getInstance().getEmsConnectionAdminPassword(),
      Config.getInstance().getEmsConnectionRetryCount(),
      Config.getInstance().getEmsConnectionReteryIntervalSeconds());
    this.graphiteAdapter = new GraphiteAdapter(Config.getInstance().getGraphiteConnectionHost(),
      Config.getInstance().getGraphiteConnectionPort(),
      Config.getInstance().getGraphiteConnectionRetryCount(),
      Config.getInstance().getGraphiteConnectionRetryIntervalSeconds());
  }

  @Override
  public void run() {
    TibcoEmsMetrics metrics = emsAdapter.getMetrics();
    List<String> metricList = GraphiteMapper.map(metrics, Instant.now().getEpochSecond());
    metricList.forEach(metric -> graphiteAdapter.send(metric));
    graphiteAdapter.flush();
  }
   
}