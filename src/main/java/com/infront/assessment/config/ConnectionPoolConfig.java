package com.infront.assessment.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectionPoolConfig {

  private static final int DEFAULT_MAX_TOTAL = 20;
  private static final int DEFAULT_MAX_PER_ROUTE = 20;
  private static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 2000;
  private static final int DEFAULT_CONNECTION_TIMEOUT = 2000;
  private static final int DEFAULT_SOCKET_TIMEOUT = 8000;

  private Integer maxTotal = DEFAULT_MAX_TOTAL;
  private Integer maxPerRoute = DEFAULT_MAX_PER_ROUTE;
  private Integer connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;
  private Integer connectTimeout = DEFAULT_CONNECTION_TIMEOUT;
  private Integer socketTimeout = DEFAULT_SOCKET_TIMEOUT;
}
