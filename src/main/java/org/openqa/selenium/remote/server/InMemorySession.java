// PATCH: find in this file 'robodriver' to see changes

// Licensed to the Software Freedom Conservancy (SFC) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The SFC licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.openqa.selenium.remote.server;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.TemporaryFilesystem;
import org.openqa.selenium.remote.Dialect;
import org.openqa.selenium.remote.SessionId;
import org.openqa.selenium.remote.http.HttpRequest;
import org.openqa.selenium.remote.http.HttpResponse;

import com.google.common.base.Preconditions;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.test.automation.robodriver.RoboDriver;

/**
 * PATCH: use W3C dialect for robodriver. 
 * This is needed because Selenium server does not allow 
 * to switch DriverProvider implementations to the dialect it needs.
 * Find 'RoboDriver.BROWSER_NAME' to see the changed line of code.
 */
class InMemorySession implements ActiveSession {

  private static final Logger LOG = Logger.getLogger(InMemorySession.class.getName());

  private final WebDriver driver;
  private final Map<String, Object> capabilities;
  private final SessionId id;
  private final Dialect downstream;
  private final TemporaryFilesystem filesystem;
  private final JsonHttpCommandHandler handler;

  private InMemorySession(WebDriver driver, Capabilities capabilities, Dialect downstream) {
    this.driver = Preconditions.checkNotNull(driver);

    Capabilities caps;
    if (driver instanceof HasCapabilities) {
      caps = ((HasCapabilities) driver).getCapabilities();
    } else {
      caps = capabilities;
    }

    this.capabilities = caps.asMap().entrySet().stream()
        .filter(e -> e.getValue() != null)
        .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    this.id = new SessionId(UUID.randomUUID().toString());
    this.downstream = Preconditions.checkNotNull(downstream);

    File tempRoot = new File(StandardSystemProperty.JAVA_IO_TMPDIR.value(), id.toString());
    Preconditions.checkState(tempRoot.mkdirs());
    this.filesystem = TemporaryFilesystem.getTmpFsBasedOn(tempRoot);

    this.handler = new JsonHttpCommandHandler(
        new PretendDriverSessions(),
        LOG);
  }

  @Override
  public void execute(HttpRequest req, HttpResponse resp) throws IOException {
    handler.handleRequest(req, resp);
  }

  @Override
  public SessionId getId() {
    return id;
  }

  @Override
  public Dialect getUpstreamDialect() {
    return Dialect.OSS;
  }

  @Override
  public Dialect getDownstreamDialect() {
    return downstream;
  }

  @Override
  public Map<String, Object> getCapabilities() {
    return capabilities;
  }

  @Override
  public TemporaryFilesystem getFileSystem() {
    return filesystem;
  }

  @Override
  public WebDriver getWrappedDriver() {
    return driver;
  }

  @Override
  public void stop() {
    driver.quit();
  }

  public static class Factory implements SessionFactory {

    private final DriverProvider provider;

    public Factory(DriverProvider provider) {
      this.provider = provider;
    }


    @Override
    public boolean isSupporting(Capabilities capabilities) {
      return provider.canCreateDriverInstanceFor(capabilities);
    }

    @Override
    public Optional<ActiveSession> apply(Set<Dialect> downstreamDialects, Capabilities caps) {
      // Assume the blob fits in the available memory.
      try {
        if (!provider.canCreateDriverInstanceFor(caps)) {
          return Optional.empty();
        }

        WebDriver driver = provider.newInstance(caps);

        // Prefer the OSS dialect.
        Dialect downstream = downstreamDialects.contains(Dialect.OSS) || downstreamDialects.isEmpty() ?
                             Dialect.OSS :
                             downstreamDialects.iterator().next();
        if (RoboDriver.BROWSER_NAME.equals(caps.getCapability("browserName"))) {
        	downstream = Dialect.W3C;
        }
        return Optional.of(new InMemorySession(driver, caps, downstream));
      } catch (IllegalStateException e) {
        return Optional.empty();
      }
    }

    @Override
    public String toString() {
      return getClass() + " (provider: " + provider + ")";
    }

  }

  private class PretendDriverSessions implements DriverSessions {

    private final Session session;

    private PretendDriverSessions() {
      this.session = new ActualSession();
    }

    @Override
    public Session get(SessionId sessionId) {
      return getId().equals(sessionId) ? session : null;
    }

    @Override
    public Set<SessionId> getSessions() {
      return ImmutableSet.of(getId());
    }
  }

  private class ActualSession implements Session {

    private final KnownElements knownElements;
    private volatile String screenshot;

    private ActualSession() {
      knownElements = new KnownElements();
    }

    @Override
    public void close() {
      driver.quit();
    }

    @Override
    public WebDriver getDriver() {
      return driver;
    }

    @Override
    public KnownElements getKnownElements() {
      return knownElements;
    }

    @Override
    public Map<String, Object> getCapabilities() {
      return capabilities;
    }

    @Override
    public void attachScreenshot(String base64EncodedImage) {
      screenshot = base64EncodedImage;
    }

    @Override
    public String getAndClearScreenshot() {
      String toReturn = screenshot;
      screenshot = null;
      return toReturn;
    }

    @Override
    public SessionId getSessionId() {
      return getId();
    }

    @Override
    public TemporaryFilesystem getTemporaryFileSystem() {
      return getFileSystem();
    }
  }
}