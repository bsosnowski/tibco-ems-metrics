/**
 * Copyright (c) 2020 Bartosz Sosnowski
 * 
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.tibco.monitoring.metrics.ems.adapter;

import com.tibco.monitoring.metrics.ems.Config;
import com.tibco.monitoring.metrics.ems.model.TibcoEmsMetrics;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;
import java.time.Duration;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TibcoEmsAdapter implements AdapterInterface {
  private static final Logger LOGGER = Logger.getLogger(TibcoEmsAdapter.class.getName());

  String separator = ".";

  private String url;
  private String user;
  private String password;
  private int retryCount;
  private int retryCountIntervalSeconds;

  private TibjmsAdmin tibjmsAdmin;

  /**
   * Constructor for TibcoEmsAdapter class.
   * @param url TIBCO EMS server URL string
   * @param user TIBCO EMS admin user name
   * @param password TIBCO EMS admin user password
   */
  public TibcoEmsAdapter(String url, String user, String password, 
      int retryCount, int retryCountIntervalSeconds) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.retryCount = retryCount;
    this.retryCountIntervalSeconds = retryCountIntervalSeconds;
    connect();
  }

  public void init() throws TibjmsAdminException {
    tibjmsAdmin = new TibjmsAdmin(url, user, password);
  }
   
  /**
   * Method for the connection initialization.
   */
  @Override
  public void connect() {
    for (int i = 1; i <= retryCount; i++) {
      try {
        init();
        LOGGER.log(Level.INFO, () -> "EMS successfully connected: " + url);
        break;
      } catch (TibjmsAdminException e) {
        LOGGER.log(Level.SEVERE, 
            "Cannot connect to EMS, waiting: {0} seconds to retry. Attempt [{1}/{2}]. Error: {3}",
            new Object[]{retryCountIntervalSeconds,
              i,
              retryCount,
              e
            }
        );
        try {
          Thread.sleep((long) retryCountIntervalSeconds * 1000);
        } catch (InterruptedException e1) {
          LOGGER.log(Level.SEVERE, "Thread sleep failed. Error: ", e);
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  /**
   * Methods for disconnecting from the server.
   */
  @Override
  public void disconnect() {
    try {
      tibjmsAdmin.close();
    } catch (TibjmsAdminException e) {
      LOGGER.log(Level.SEVERE, "Cannot close EMS connection. Error: ", e);
    }
  }

  /**
   * Main methods for gathering metrics.
   * @return TibcoEmsMetrics object
   */
  public TibcoEmsMetrics getMetrics() {

    TibcoEmsMetrics tea = new TibcoEmsMetrics();
    try {
      Instant start = Instant.now();
      tea.setServerInfo(tibjmsAdmin.getInfo());
      Instant end = Instant.now();
      tea.setServerLatency(String.valueOf((Duration.between(start, end)).toMillis()));
      tea.setStateInfo(tibjmsAdmin.getStateInfo());
      if (Boolean.TRUE.equals(Config.getInstance().getMetricsEnabledDestinations())) {
        tea.setQueues(tibjmsAdmin.getQueues());
        tea.setTopics(tibjmsAdmin.getTopics());
      }
    } catch (TibjmsAdminException e) {
      LOGGER.log(Level.SEVERE, "Cannot query EMS server. Error: ", e);
      connect();
    }
    return tea;

  } 

}