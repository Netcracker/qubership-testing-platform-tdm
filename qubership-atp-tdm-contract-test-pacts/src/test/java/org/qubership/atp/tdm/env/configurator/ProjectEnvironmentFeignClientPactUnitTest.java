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

package org.qubership.atp.tdm.env.configurator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.qubership.atp.auth.springbootstarter.config.FeignConfiguration;
import org.qubership.atp.tdm.env.configurator.api.dto.project.EnvironmentResDto;
import org.qubership.atp.tdm.env.configurator.api.dto.project.ProjectFullVer1ViewDto;
import org.qubership.atp.tdm.env.configurator.api.dto.project.ProjectFullVer2ViewDto;
import org.qubership.atp.tdm.env.configurator.api.dto.project.SystemEnvironmentsViewDto;
import org.qubership.atp.tdm.env.configurator.api.dto.project.SystemFullVer1ViewDto;
import org.qubership.atp.tdm.env.configurator.api.dto.project.SystemFullVer2ViewDto;
import org.qubership.atp.tdm.env.configurator.service.client.ProjectEnvironmentFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;

@EnableFeignClients(clients = {ProjectEnvironmentFeignClient.class})
@ExtendWith(PactConsumerTestExt.class)
@SpringJUnitConfig(classes = {ProjectEnvironmentFeignClientPactUnitTest.TestApp.class})
@Import({JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, FeignConfiguration.class,
        FeignAutoConfiguration.class})
@TestPropertySource(
        properties = {"feign.atp.environments.name=atp-environments",
                "feign.atp.environments.route=",
                "feign.atp.environments.url=http://localhost:8888"})
@PactTestFor(providerName = "atp-environments", port = "8888", pactVersion = PactSpecVersion.V3)
public class ProjectEnvironmentFeignClientPactUnitTest {

    @Configuration
    public static class TestApp {

    }

    @Autowired
    ProjectEnvironmentFeignClient projectEnvFeignClient;

    @Test
    @PactTestFor(pactMethod = "createPact")
    public void allPass() {
        UUID projectId = UUID.fromString("7c9dafe9-2cd1-4ffc-ae54-45867f2b9701");

        ResponseEntity<List<ProjectFullVer2ViewDto>> result1 = projectEnvFeignClient.getAllProjects(null, false);
        Assertions.assertEquals(200, result1.getStatusCode().value());
        Assertions.assertTrue(Objects.requireNonNull(result1.getHeaders().get("Content-Type"))
                .contains("application/json"));

        ResponseEntity<ProjectFullVer1ViewDto> result2 = projectEnvFeignClient.getProject(projectId, true);
        Assertions.assertEquals(200, result2.getStatusCode().value());
        Assertions.assertTrue(Objects.requireNonNull(result2.getHeaders().get("Content-Type"))
                .contains("application/json"));

        ResponseEntity<List<EnvironmentResDto>> result3 = projectEnvFeignClient.getEnvironments(projectId, false);
        Assertions.assertEquals(200, result3.getStatusCode().value());
        Assertions.assertTrue(Objects.requireNonNull(result3.getHeaders().get("Content-Type"))
                .contains("application/json"));

        ResponseEntity<List<SystemEnvironmentsViewDto>> result4 = projectEnvFeignClient.getAllShortSystemsOnProject(projectId);
        Assertions.assertEquals(200, result4.getStatusCode().value());
        Assertions.assertTrue(Objects.requireNonNull(result4.getHeaders().get("Content-Type"))
                .contains("application/json"));

        ResponseEntity<List<SystemFullVer2ViewDto>> result5 = projectEnvFeignClient.getProjectSystems(projectId, null, false);
        Assertions.assertEquals(200, result5.getStatusCode().value());
        Assertions.assertTrue(Objects.requireNonNull(result5.getHeaders().get("Content-Type"))
                .contains("application/json"));
    }

    @Pact(consumer = "atp-tdm")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        DslPart object = new PactDslJsonBody()
                .integerType("created")
                .uuid("createdBy")
                .stringType("description")
                .uuid("id")
                .integerType("modified")
                .uuid("modifiedBy")
                .stringType("name")
                .stringType("shortName")
                .array("environments").object().closeArray();

        Assertions.assertNotNull(object);
        DslPart projectFullVer2Res = new PactDslJsonArray().template(object);

        DslPart projectFullVer1Res = new PactDslJsonBody()
                .integerType("created")
                .uuid("createdBy")
                .stringType("description")
                .uuid("id")
                .integerType("modified")
                .uuid("modifiedBy")
                .stringType("name")
                .stringType("shortName")
                .array("environments").object().closeArray();

        DslPart object1 = new PactDslJsonBody()
                .integerType("created")
                .uuid("createdBy")
                .stringType("description")
                .uuid("id")
                .integerType("modified")
                .uuid("modifiedBy")
                .stringType("name")
                .stringType("graylogName")
                .uuid("projectId")
                .array("systems").object().closeArray();

        Assertions.assertNotNull(object1);
        DslPart environmentRes = new PactDslJsonArray().template(object1);

        DslPart object2 = new PactDslJsonBody()
                .stringType("name")
                .uuid("id")
                .array("environmentIds").object().closeArray();

        Assertions.assertNotNull(object2);
        DslPart systemEnvironmentsViewDtoList = new PactDslJsonArray().template(object2);

        DslPart object3 = new PactDslJsonBody()
                .integerType("created")
                .uuid("createdBy")
                .integerType("dateOfCheckVersion")
                .integerType("dateOfLastCheck")
                .stringType("description")
                .uuid("externalId")
                .stringType("externalName")
                .uuid("id")
                .uuid("linkToSystemId")
                .booleanType("mergeByName")
                .integerType("modified")
                .uuid("modifiedBy")
                .stringType("name")
                .uuid("parentSystemId")
                .stringType("status", SystemFullVer1ViewDto.StatusEnum.FAIL.toString())
                .object("serverITF")
                .object("parametersGettingVersion").closeObject()
                .array("environments").object().closeArray()
                .array("connections").object().closeArray();

        Assertions.assertNotNull(object3);
        DslPart systemFullVer2ViewDto = new PactDslJsonArray().template(object3);

        PactDslResponse response = builder
                .given("all ok")
                .uponReceiving("GET /api/projects OK")
                .path("/api/projects")
                .matchQuery("full", "true|false", "false")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(projectFullVer2Res)

                .given("all ok")
                .uponReceiving("GET /api/projects/{projectId} OK")
                .path("/api/projects/7c9dafe9-2cd1-4ffc-ae54-45867f2b9701")
                .matchQuery("full", "true|false", "false")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(projectFullVer1Res)

                .given("all ok")
                .uponReceiving("GET /api/projects/{projectId}/environments OK")
                .path("/api/projects/7c9dafe9-2cd1-4ffc-ae54-45867f2b9701/environments")
                .matchQuery("full", "true|false", "false")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(environmentRes)

                .given("all ok")
                .uponReceiving("GET /api/projects/{projectId}/environments/systems/short OK")
                .path("/api/projects/7c9dafe9-2cd1-4ffc-ae54-45867f2b9701/environments/systems/short")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(systemEnvironmentsViewDtoList)

                .given("all ok")
                .uponReceiving("GET /api/projects/{projectId}/environments/systems?category=category&full=false OK")
                .path("/api/projects/7c9dafe9-2cd1-4ffc-ae54-45867f2b9701/environments/systems")
                .query("category=category")
                .query("full=false")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(systemFullVer2ViewDto);

        return response.toPact();
    }
}
