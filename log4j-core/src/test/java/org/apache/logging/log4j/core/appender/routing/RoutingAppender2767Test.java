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
package org.apache.logging.log4j.core.appender.routing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.EventLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.message.StructuredDataMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

/**
 *
 */
public class RoutingAppender2767Test {
    private static final String CONFIG = "log4j-routing-2767.xml";
    private static final String ACTIVITY_LOG_FILE = "target/routing1/routingtest-Service.log";

    private final LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);

    @Rule
    public RuleChain rules = loggerContextRule.withCleanFilesRule(ACTIVITY_LOG_FILE);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
        this.loggerContextRule.getLoggerContext().stop();
    }

    @Test
    public void routingTest() throws Exception {
        StructuredDataMessage msg = new StructuredDataMessage("Test", "This is a test", "Service");
        EventLogger.logEvent(msg);
        File file = new File(ACTIVITY_LOG_FILE);
        assertThat(file.exists()).describedAs("Activity file was not created").isTrue();
        List<String> lines = Files.lines(file.toPath()).collect(Collectors.toList());
        assertThat(lines).describedAs("Incorrect number of lines").hasSize(1);
        assertThat(lines.get(0).contains("This is a test")).describedAs("Incorrect content").isTrue();
    }
}
