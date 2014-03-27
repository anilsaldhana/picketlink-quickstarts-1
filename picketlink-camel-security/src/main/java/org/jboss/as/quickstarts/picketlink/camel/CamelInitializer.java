/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.as.quickstarts.picketlink.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.CdiCamelContext;
import org.picketlink.integration.fuse.camel.PicketLinkCamelProcessor;
import org.picketlink.integration.fuse.camel.authorization.PicketLinkCamelAuthorizationPolicy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Set up the Routes
 * @author Anil Saldhana
 * @since March 27, 2014
 */
@Singleton
@Startup
public class CamelInitializer {
    @Inject
    private CdiCamelContext camelCtx;

    @Inject
    private PicketLinkCamelProcessor picketLinkCamelProcessor;

    @Inject
    private PicketLinkCamelAuthorizationPolicy picketLinkCamelAuthorizationPolicy;

    @PostConstruct
    public void createRoutes() throws Exception {
        /*
        <route>
      <!-- incoming requests from the servlet is routed -->
      <from uri="servlet:///hello"/>
      <choice>
        <when>
          <!-- is there a header with the key name? -->
          <header>name</header>
          <!-- yes so return back a message to the user -->
          <transform>
            <simple>Hello ${header.name} how are you?</simple>
          </transform>
        </when>
        <otherwise>
          <!-- if no name parameter then output a syntax to the user -->
          <transform>
            <constant>Add a name parameter to uri, eg ?name=foo</constant>
          </transform>
        </otherwise>
      </choice>
    </route>
         */

        RouteBuilder routeBuilder = new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("servlet:///hello")
                        .process(picketLinkCamelProcessor).policy(picketLinkCamelAuthorizationPolicy)
                        .choice()
                        .when(header("childname")).transform(simple("<html><body>Hello ${header.childname} how are you?</body></html>"))
                        .otherwise().transform(constant("<html><body>Add a name parameter to uri, eg ?childname=foo</body></html>"));
            }
        };
        camelCtx.addRoutes(routeBuilder);
        camelCtx.start();
    }

    @PreDestroy
    public void stop() throws Exception {
        camelCtx.stop();
    }
}