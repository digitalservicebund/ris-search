package de.bund.digitalservice.ris.search.service;

import java.util.logging.Logger;
import testnamespace.VERSIONResponse;

public class ConnectorSoapImpl implements testnamespace.ConnectorSoap {

  private static final Logger LOG = Logger.getLogger(ConnectorSoapImpl.class.getName());

  @Override
  public String aboutConnector(String type) {
    return "";
  }

  @Override
  public String testQuery(String type) {
    return type;
  }

  @Override
  public String request(String query) {
    return "";
  }

  @Override
  public VERSIONResponse version(Object parameters) {
    return null;
  }
}
