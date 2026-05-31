/*
 * # Copyright 2024-2026 NetCracker Technology Corporation
 * #
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * # you may not use this file except in compliance with the License.
 * # You may obtain a copy of the License at
 * #
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * #
 * # Unless required by applicable law or agreed to in writing, software
 * # distributed under the License is distributed on an "AS IS" BASIS,
 * # WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * # See the License for the specific language governing permissions and
 * # limitations under the License.
 */

package org.qubership.atp.tdm.backend;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.qubership.atp.auth.springbootstarter.config.FeignConfiguration;
import org.qubership.atp.tdm.service.client.HighchartsFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;


@EnableFeignClients(clients = {HighchartsFeignClient.class})
@ExtendWith(PactConsumerTestExt.class)
@SpringJUnitConfig(classes = {HighchartsFeignClientPactUnitTest.TestApp.class})
@Import({JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, FeignConfiguration.class,
        FeignAutoConfiguration.class})
@TestPropertySource(
        properties = {"feign.atp.highcharts.name=atp-environments",
                "feign.atp.highcharts.route=",
                "feign.atp.highcharts.url=http://localhost:8888"})
@PactTestFor(providerName = "atp-charts", port = "8888", pactVersion = PactSpecVersion.V3)
public class HighchartsFeignClientPactUnitTest {

    @Configuration
    public static class TestApp {

    }
    @Autowired
    HighchartsFeignClient highchartsFeignClient;

    @Test
    @PactTestFor(pactMethod = "createPact")
    public void allPass() {
        String body = """
                {
                    "options": {
                        "title": {
                            "text": "My title",
                        },
                        "xAxis": {
                           " categories": ["Jan", "Feb", "Mar", "Apr", "Mar", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
                        },
                        "series": [
                            {
                                "type": "line",
                                "data": [1, 3, 2, 4],
                            },
                            {
                                "type": "line",
                                "data": [5, 3, 4, 2],
                            },
                        ],
                        "credits": {
                            "enabled": false,
                        },
                    },
                }\
                """;

        ResponseEntity<Resource> response = highchartsFeignClient.create(body);
        Assertions.assertEquals(201, response.getStatusCode().value());
    }

    @Pact(consumer = "atp-tdm")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        String body = """
                {
                    "options": {
                        "title": {
                            "text": "My title",
                        },
                        "xAxis": {
                           " categories": ["Jan", "Feb", "Mar", "Apr", "Mar", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
                        },
                        "series": [
                            {
                                "type": "line",
                                "data": [1, 3, 2, 4],
                            },
                            {
                                "type": "line",
                                "data": [5, 3, 4, 2],
                            },
                        ],
                        "credits": {
                            "enabled": false,
                        },
                    },
                }\
                """;

        PactDslResponse response = builder
                .given("all ok")
                    .uponReceiving("POST /api/v1/create OK")
                    .path("/api/v1/create")
                    .method("POST")
                    .headers(headers)
                    .body(body)
                .willRespondWith()
                    .status(201);

        return response.toPact();
    }
}
