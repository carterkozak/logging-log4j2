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
package org.apache.log4j.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.apache.log4j.ListAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.bridge.AppenderAdapter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.Test;

/**
 * Test configuration from Properties.
 */
public class PropertiesConfigurationTest {

    @Test
    public void testProperties() throws Exception {
        configure("target/test-classes/log4j1-file.properties");
        Logger logger = LogManager.getLogger("test");
        logger.debug("This is a test of the root logger");
        File file = new File("target/temp.A1");
        assertThat(file.exists()).describedAs("File A1 was not created").isTrue();
        assertThat(file.length()).describedAs("File A1 is empty").isGreaterThan(0);
        file = new File("target/temp.A2");
        assertThat(file.exists()).describedAs("File A2 was not created").isTrue();
        assertThat(file.length()).describedAs("File A2 is empty").isGreaterThan(0);
    }

    @Test
    public void testListAppender() throws Exception {
        LoggerContext loggerContext = configure("target/test-classes/log4j1-list.properties");
        Logger logger = LogManager.getLogger("test");
        logger.debug("This is a test of the root logger");
        Configuration configuration = loggerContext.getConfiguration();
        Map<String, Appender> appenders = configuration.getAppenders();
        ListAppender eventAppender = null;
        ListAppender messageAppender = null;
        for (Map.Entry<String, Appender> entry : appenders.entrySet()) {
            if (entry.getKey().equals("list")) {
                messageAppender = (ListAppender) ((AppenderAdapter.Adapter) entry.getValue()).getAppender();
            } else if (entry.getKey().equals("events")) {
                eventAppender = (ListAppender) ((AppenderAdapter.Adapter) entry.getValue()).getAppender();
            }
        }
        assertThat(eventAppender).describedAs("No Event Appender").isNotNull();
        assertThat(messageAppender).describedAs("No Message Appender").isNotNull();
        List<LoggingEvent> events = eventAppender.getEvents();
        assertThat(events != null).describedAs("No events").isTrue();
assertThat(events).describedAs("No events").hasSizeGreaterThan(0);
        List<String> messages = messageAppender.getMessages();
        assertThat(messages != null).describedAs("No messages").isTrue();
assertThat(messages).describedAs("No messages").hasSizeGreaterThan(0);
    }

    private LoggerContext configure(String configLocation) throws Exception {
        File file = new File(configLocation);
        InputStream is = new FileInputStream(file);
        ConfigurationSource source = new ConfigurationSource(is, file);
        LoggerContext context = (LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
        Configuration configuration = new PropertiesConfigurationFactory().getConfiguration(context, source);
        assertThat(configuration).describedAs("No configuration created").isNotNull();
        Configurator.reconfigure(configuration);
        return context;
    }

}
