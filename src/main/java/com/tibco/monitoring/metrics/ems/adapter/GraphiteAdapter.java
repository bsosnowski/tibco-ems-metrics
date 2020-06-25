/**
 * Copyright (c) 2020 Bartosz Sosnowski
 * 
 * This software is released under the MIT License.
 * https://opensource.org/licenses/MIT
 */

package com.tibco.monitoring.metrics.ems.adapter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphiteAdapter implements AdapterInterface {
  private static final Logger LOGGER = Logger.getLogger(GraphiteAdapter.class.getName());

  private String host;
  private int port;
  private int retryCount;
  private int retryCountIntervalSeconds;
  Socket socket;
  private Writer writer;

  /**
   * Default constructor for GraphiteAdapter class.
   * @param host hostname of the Graphite server
   * @param port tcp port of Graphite server
   */
  public GraphiteAdapter(String host, int port, int retryCount, int retryIntervalSeconds) {
    this.host = host;
    this.port = port;
    this.retryCount = retryCount;
    this.retryCountIntervalSeconds = retryIntervalSeconds;
    connect();
  }

  public void init() throws IOException {
    socket = new Socket(host, port);
    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
  }

  /**
   * Method for connection initialization.
   */
  @Override
  public void connect() {
    for (int i = 1; i <= retryCount; i++) {
      try {
        init();
        LOGGER.log(Level.INFO, () -> 
            "Connected to Graphite on host: " + host + " and port: " + port);
        break;
      } catch (IOException e) {
        LOGGER.log(Level.SEVERE, 
            "Cannot connect to Graphite, waiting: {0} sec to retry. Attempt [{1}/{2}]. Error: {3}",
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
   * Method for connection closing.
   */
  @Override
  public void disconnect() {
    try {
      writer.close();
      socket.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, 
                 "Error has occured when trying to close connection socket. Error: ", e);
    }
  }

  /**
   * Method for flushing writer stream.
   */
  public void flush() {
    try {
      writer.flush();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Flushing has failed, trying to reconnect. Error: ", e);
      connect();
    }
  }

  /**
   * Methods sends message to the Graphite.
   * @param metric as String
   */
  public void send(String metric) {
    LOGGER.log(Level.INFO, () -> "Graphite metric TCP line: " + metric);
    try {
      writer.write(metric);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, "Cannot write to socket.", e);
    }
  }
        
}
