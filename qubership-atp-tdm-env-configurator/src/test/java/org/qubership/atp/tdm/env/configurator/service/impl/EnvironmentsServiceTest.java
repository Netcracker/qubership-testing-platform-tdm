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

package org.qubership.atp.tdm.env.configurator.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.qubership.atp.tdm.env.configurator.EnvironmentHelper;
import org.qubership.atp.tdm.env.configurator.configuration.ModelMapperConfig;
import org.qubership.atp.tdm.env.configurator.model.Connection;
import org.qubership.atp.tdm.env.configurator.model.LazyEnvironment;
import org.qubership.atp.tdm.env.configurator.model.LazyProject;
import org.qubership.atp.tdm.env.configurator.model.LazySystem;
import org.qubership.atp.tdm.env.configurator.model.Project;
import org.qubership.atp.tdm.env.configurator.model.System;
import org.qubership.atp.tdm.env.configurator.service.DtoConvertService;
import org.qubership.atp.tdm.env.configurator.service.EnvironmentsService;
import org.qubership.atp.tdm.env.configurator.service.client.EnvironmentFeignClient;
import org.qubership.atp.tdm.env.configurator.service.client.ProjectEnvironmentFeignClient;
import org.qubership.atp.tdm.env.configurator.service.client.SystemEnvironmentFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ModelMapperConfig.class, EnvironmentsServiceTest.TestConfig.class})
public class EnvironmentsServiceTest {

    @Configuration
    static class TestConfig {
        @Bean
        public DtoConvertService dtoConvertService(ModelMapper modelMapper) {
            return new DtoConvertService(modelMapper);
        }
    }

    @MockitoBean
    protected EnvironmentFeignClient environmentFeignClient;
    @MockitoBean
    protected ProjectEnvironmentFeignClient projectEnvFeignClient;
    @MockitoBean
    protected SystemEnvironmentFeignClient systemEnvironmentFeignClient;
    @Autowired
    protected ModelMapper modelMapper;
    @MockitoSpyBean
    protected DtoConvertService dtoConvertService;

    private EnvironmentsService environmentsService;

    @BeforeEach
    public void setUp() {
        when(projectEnvFeignClient.getAllProjects(any(), eq(false)))
                .thenReturn(new ResponseEntity(Collections.singletonList(EnvironmentHelper.projectFullVer2ViewDto),
                        HttpStatus.OK));
        when(projectEnvFeignClient.getAllShort(eq(false)))
                .thenReturn(new ResponseEntity(Collections.singletonList(EnvironmentHelper.projectNameViewDto),
                        HttpStatus.OK));
        when(projectEnvFeignClient.getProject(any(), eq(true)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.projectFullVer1ViewDto, HttpStatus.OK));
        when(projectEnvFeignClient.getShortProject(any(), eq(false)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.projectLazyVer1ViewDto, HttpStatus.OK));
        when(projectEnvFeignClient.getEnvironments(any(), eq(false)))
                .thenReturn(new ResponseEntity(Collections.singletonList(EnvironmentHelper.environmentResDto),
                        HttpStatus.OK));
        when(projectEnvFeignClient.getShortProjectByName(any(), eq(false)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.projectFullVer1ViewDtoLazyProjectByName,
                        HttpStatus.OK));
        when(projectEnvFeignClient.getProjectSystems(any(), any(), any()))
                .thenReturn(new ResponseEntity(EnvironmentHelper.systemsFullVer2ViewDtoP, HttpStatus.OK));
        when(projectEnvFeignClient.getAllShortSystemsOnProject(any()))
                .thenReturn(new ResponseEntity(EnvironmentHelper.systemEnvironmentsViewDto, HttpStatus.OK));
        when(projectEnvFeignClient.getEnvironmentsShort(any()))
                .thenReturn(new ResponseEntity(Collections.singletonList(EnvironmentHelper.lazyEnvironmentShort),
                        HttpStatus.OK));
        when(environmentFeignClient.getSystemsShort(any()))
                .thenReturn(new ResponseEntity(EnvironmentHelper.systemFullVer2ViewDto, HttpStatus.OK));
        when(environmentFeignClient.getEnvironment(any(), eq(true)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.environmentFullVer1ViewDto, HttpStatus.OK));
        when(environmentFeignClient.getEnvironment(any(), eq(false)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.environmentLazyVer1ViewDto, HttpStatus.OK));
        when(environmentFeignClient.findBySearchRequest(any(), any()))
                .thenReturn(new ResponseEntity(Collections.singletonList(EnvironmentHelper.environmentDto),
                        HttpStatus.OK));
        when(environmentFeignClient.getEnvironmentNameById(any()))
                .thenReturn(new ResponseEntity("test", HttpStatus.OK));
        when(systemEnvironmentFeignClient.getSystemByName(any(), any(), eq(true)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.systemFullVer1ViewDto, HttpStatus.OK));
        when(systemEnvironmentFeignClient.getSystemByName(any(), any(), eq(false)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.systemFullVer1ViewDto, HttpStatus.OK));
        when(systemEnvironmentFeignClient.getShortSystem(any(), eq(false)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.systemFullVer1ViewDto, HttpStatus.OK));
        when(systemEnvironmentFeignClient.getSystem(any(), eq(true)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.systemFullVer1ViewDto, HttpStatus.OK));
        when(systemEnvironmentFeignClient.getSystemConnections(any(), eq(false)))
                .thenReturn(new ResponseEntity(EnvironmentHelper.systemConnections, HttpStatus.OK));

        environmentsService = new EnvironmentsServiceImpl(environmentFeignClient,
                projectEnvFeignClient,
                systemEnvironmentFeignClient,
                dtoConvertService,
                null);
    }

    @Test
    public void getLazyProjectsTest() {
        List<LazyProject> lazyProjects = environmentsService.getLazyProjects();
        Assertions.assertEquals(Collections.singletonList(EnvironmentHelper.lazyProject), lazyProjects);
    }

    //@Test
    public void getFullProjectTest() {
        Project actualProject = environmentsService.getFullProject(EnvironmentHelper.project.getId());
        Assertions.assertEquals(EnvironmentHelper.project, actualProject);
    }

    @Test
    public void getLazyEnvironmentsTest() {
        List<LazyEnvironment> actualEnvironments = environmentsService.getLazyEnvironments(EnvironmentHelper.project.getId());
        Assertions.assertEquals(Collections.singletonList(EnvironmentHelper.lazyEnvironment), actualEnvironments);
    }

    @Test
    public void getLazyEnvironmentsShortByProjectId() {
        List<LazyEnvironment> environmentsShort = environmentsService.getLazyEnvironmentsShort(EnvironmentHelper.project.getId());
        Assertions.assertEquals(Collections.singletonList(EnvironmentHelper.lazyEnvironmentShort), environmentsShort);
    }

    @Test
    public void getLazySystemsTest() {
        List<LazySystem> actualLazySystems = environmentsService.getLazySystems(EnvironmentHelper.environment.getId(), "Default");
        Assertions.assertEquals(EnvironmentHelper.lazySystems, actualLazySystems);
        Assertions.assertEquals("Default", actualLazySystems.getFirst().getName());
    }

    @Test
    public void getLazyProjectByNameTest() {
        LazyProject actualLazyProject = environmentsService.getLazyProjectByName("Lazy Project Name");
        Assertions.assertEquals(EnvironmentHelper.lazyProject, actualLazyProject);
    }

    @Test
    public void getLazyEnvironmentByNameTest() {
        LazyEnvironment actualLazyEnvironment = environmentsService.getLazyEnvironmentByName(EnvironmentHelper.project.getId(),
                " Lazy Environment Name ");
        Assertions.assertEquals(EnvironmentHelper.lazyEnvironment, actualLazyEnvironment);
    }

    //@Test
    public void getFullSystemByNameTest() {
        System actualSystem = environmentsService.getFullSystemByName(EnvironmentHelper.project.getId(),
                EnvironmentHelper.environment.getId(), " System Name ");
        Assertions.assertEquals(EnvironmentHelper.system, actualSystem);
    }

    //@Test
    public void getLazySystemByNameTest() {
        LazySystem lazySystemByName = environmentsService.getLazySystemByName(EnvironmentHelper.project.getId(),
                EnvironmentHelper.environment.getId(), " System Name ");
        Assertions.assertEquals(EnvironmentHelper.lazySystem, lazySystemByName);
    }

    //@Test
    public void getLazySystemByProjectIdWithConnections() {
        List<LazySystem> systems = environmentsService.getLazySystemsByProjectIdWithConnections(EnvironmentHelper.project.getId());
        Assertions.assertEquals(EnvironmentHelper.lazySystems, systems);
    }

    //@Test
    public void getLazySystemByProjectIdWithEnvIds() {
        List<LazySystem> systems = environmentsService.getLazySystemsByProjectWithEnvIds(EnvironmentHelper.project.getId());
        Assertions.assertEquals(EnvironmentHelper.lazySystems, systems);
    }

    @Test
    public void getLazySystems() {
        List<LazySystem> systems = environmentsService.getLazySystems(EnvironmentHelper.environment.getId());
        Assertions.assertEquals(EnvironmentHelper.lazySystems, systems);
    }

    //@Test
    public void getLazySystemById() {
        LazySystem lazySystemById = environmentsService.getLazySystemById(EnvironmentHelper.system.getId());
        Assertions.assertEquals(EnvironmentHelper.lazySystem, lazySystemById);
    }

    //@Test
    public void getFullSystemById() {
        System fullSystemById = environmentsService.getFullSystemById(EnvironmentHelper.system.getId());
        Assertions.assertEquals(EnvironmentHelper.system, fullSystemById);
    }

    @Test
    public void getLazyEnvironment() {
        LazyEnvironment lazyEnvironmentById = environmentsService.getLazyEnvironment(EnvironmentHelper.environment.getId());
        Assertions.assertEquals(EnvironmentHelper.lazyEnvironment, lazyEnvironmentById);
    }

    @Test
    public void getLazyProjectById() {
        LazyProject lazyProjectById = environmentsService.getLazyProjectById(EnvironmentHelper.project.getId());
        Assertions.assertEquals(EnvironmentHelper.lazyProject, lazyProjectById);
    }

    @Test
    public void getEnvironmentByEnvironmentId() {
        LazyEnvironment environment = environmentsService.getLazyEnvironment(EnvironmentHelper.lazyEnvironment.getId());
        Assertions.assertEquals(EnvironmentHelper.lazyEnvironment, environment);
    }

    @Test
    public void getConnectionsBySystemId() {
        List<Connection> connectionsSystemById = environmentsService.getConnectionsSystemById(EnvironmentHelper.lazySystem.getId());
        Assertions.assertEquals(EnvironmentHelper.systemConnections, connectionsSystemById);
    }

    @Test
    public void getEnvNameByEnvironmentId() {
        String  envName = environmentsService.getEnvNameById(EnvironmentHelper.lazyEnvironment.getId());
        Assertions.assertEquals("test", envName);
    }
}
