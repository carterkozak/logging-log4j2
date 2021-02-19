/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.appender.rolling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 * LOG4J2-1804.
 */
public class RollingAppenderSizeNoCompressTest {

  private static final String CONFIG = "log4j-rolling-size.xml";

    private static final String DIR = "target/rolling1";

    public static LoggerContextRule loggerContextRule = LoggerContextRule.createShutdownTimeoutLoggerContextRule(CONFIG);

    @Rule
    public RuleChain chain = loggerContextRule.withCleanFoldersRule(DIR);

    private Logger logger;

    @Before
    public void setUp() throws Exception {
        this.logger = loggerContextRule.getLogger(RollingAppenderSizeNoCompressTest.class.getName());
    }

    @Test
    public void testAppender() throws Exception {
      final List<String> messages = new ArrayList<>();
        for (int i=0; i < 1000; ++i) {
          final String message = "This is test message number " + i;
          messages.add(message);
            logger.debug(message);
            if (i % 100 == 0) {
              Thread.sleep(500);
            }
        }
        if (!loggerContextRule.getLoggerContext().stop(30, TimeUnit.SECONDS)) {
            System.err.println("Could not stop cleanly " + loggerContextRule + " for " + this);
        }
        final File dir = new File(DIR);
        assertThat(dir.exists()).describedAs("Directory not created").isTrue();
        final File[] files = dir.listFiles();
        assertThat(files).isNotNull();
        for (final File file : files) {
          final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (FileInputStream fis = new FileInputStream(file)) {
              try {
                    IOUtils.copy(fis, baos);
                } catch (final Exception ex) {
                    ex.printStackTrace();
                    fail("Unable to read " + file.getAbsolutePath());
                }
          }
            final String text = new String(baos.toByteArray(), Charset.defaultCharset());
            final String[] lines = text.split("[\\r\\n]+");
            for (final String line : lines) {
              messages.remove(line);
            }
        }
        assertThat(messages.isEmpty()).describedAs("Log messages lost : " + messages.size()).isTrue();
        assertThat(files).describedAs("Files not rolled : " + files.length).hasSizeGreaterThan(2);
    }
}
