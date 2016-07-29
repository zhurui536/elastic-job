/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
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
 * </p>
 */

package com.dangdang.ddframe.job.lite.internal.settings;

import com.dangdang.ddframe.job.lite.api.JobSettingsAPI;
import com.dangdang.ddframe.job.lite.domain.JobSettings;
import com.dangdang.ddframe.reg.base.CoordinatorRegistryCenter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JobSettingsAPIImplTest {
    
    private JobSettingsAPI jobSettingsAPI;
    
    @Mock
    private CoordinatorRegistryCenter registryCenter;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        jobSettingsAPI = new JobSettingsAPIImpl(registryCenter);
    }
    
    @Test
    public void assertGetJobSettingsWithMonitorPort() {
        String dataflowJobJson =  "{\"jobName\":\"test_job\",\"jobClass\":\"com.dangdang.ddframe.job.lite.fixture.TestDataflowJob\",\"jobType\":\"DATAFLOW\",\"cron\":\"0/1 * * * * ?\","
                + "\"shardingTotalCount\":3,\"shardingItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":false,\"misfire\":true,\"description\":\"\","
                + "\"jobProperties\":{},\"monitorExecution\":true,\"maxTimeDiffSeconds\":-1,\"monitorPort\":-1,\"jobShardingStrategyClass\":\"\",\"disabled\":false,\"overwrite\":false,"
                + "\"dataflowType\":\"SEQUENCE\",\"streamingProcess\":true,\"concurrentDataProcessThreadCount\":10}";
        when(registryCenter.get("/test_job/config")).thenReturn(dataflowJobJson);
        JobSettings actual = jobSettingsAPI.getJobSettings("test_job");
        assertJobSettings(actual);
        assertThat(actual.getMonitorPort(), is(-1));
        verify(registryCenter).get("/test_job/config");
    }
    
    @Test
    public void assertGetJobSettingsWithoutMonitorPort() {
        String dataflowJobJson =  "{\"jobName\":\"test_job\",\"jobClass\":\"com.dangdang.ddframe.job.lite.fixture.TestDataflowJob\",\"jobType\":\"DATAFLOW\",\"cron\":\"0/1 * * * * ?\","
                + "\"shardingTotalCount\":3,\"shardingItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":false,\"misfire\":true,\"description\":\"\","
                + "\"jobProperties\":{},\"monitorExecution\":true,\"maxTimeDiffSeconds\":-1,\"monitorPort\":8888,\"jobShardingStrategyClass\":\"\",\"disabled\":false,\"overwrite\":false,"
                + "\"dataflowType\":\"SEQUENCE\",\"streamingProcess\":true,\"concurrentDataProcessThreadCount\":10}";
        when(registryCenter.get("/test_job/config")).thenReturn(dataflowJobJson);
        assertJobSettings(jobSettingsAPI.getJobSettings("test_job"));
        verify(registryCenter).get("/test_job/config");
    }
    
    private void assertJobSettings(final JobSettings jobSettings) {
        assertThat(jobSettings.getJobName(), is("test_job"));
        assertThat(jobSettings.getJobType(), is("DATAFLOW"));
        assertThat(jobSettings.getJobClass(), is("com.dangdang.ddframe.job.lite.fixture.TestDataflowJob"));
        assertThat(jobSettings.getShardingTotalCount(), is(3));
        assertThat(jobSettings.getCron(), is("0/1 * * * * ?"));
        assertThat(jobSettings.getShardingItemParameters(), is(""));
        assertThat(jobSettings.getJobParameter(), is(""));
        assertThat(jobSettings.isMonitorExecution(), is(true));
        assertThat(jobSettings.getMaxTimeDiffSeconds(), is(-1));
        assertFalse(jobSettings.isFailover());
        assertTrue(jobSettings.isMisfire());
        assertTrue(jobSettings.isStreamingProcess());
        assertThat(jobSettings.getJobShardingStrategyClass(), is(""));
        assertThat(jobSettings.getDescription(), is(""));
    }
    
    @Test
    public void assertUpdateJobSettings() {
        String dataflowJobJson =  "{\"jobName\":\"test_job\",\"jobClass\":\"com.dangdang.ddframe.job.lite.fixture.TestDataflowJob\",\"jobType\":\"DATAFLOW\",\"cron\":\"0/1 * * * * ?\","
                + "\"shardingTotalCount\":3,\"shardingItemParameters\":\"\",\"jobParameter\":\"\",\"failover\":false,\"misfire\":true,\"description\":\"\","
                + "\"jobProperties\":{},\"monitorExecution\":true,\"maxTimeDiffSeconds\":-1,\"monitorPort\":8888,\"jobShardingStrategyClass\":\"\",\"disabled\":false,\"overwrite\":false,"
                + "\"dataflowType\":\"SEQUENCE\",\"streamingProcess\":true,\"concurrentDataProcessThreadCount\":10}";
        when(registryCenter.get("/test_job/config")).thenReturn(dataflowJobJson);
        JobSettings jobSettings = new JobSettings();
        jobSettings.setJobName("test_job");
        jobSettings.setJobClass("com.dangdang.ddframe.job.lite.fixture.TestDataflowJob");
        jobSettings.setShardingTotalCount(10);
        jobSettings.setConcurrentDataProcessThreadCount(10);
        jobSettings.setMaxTimeDiffSeconds(-1);
        jobSettings.setMonitorExecution(true);
        jobSettings.setCron("0/1 * * * * ?");
        jobSettings.setStreamingProcess(true);
        jobSettings.setFailover(false);
        jobSettings.setMisfire(true);
        jobSettingsAPI.updateJobSettings(jobSettings);
        verify(registryCenter).update("/test_job/config", "{\"jobName\":\"test_job\",\"jobClass\":\"com.dangdang.ddframe.job.lite.fixture.TestDataflowJob\",\"cron\":\"0/1 * * * * ?\","
                + "\"shardingTotalCount\":10,\"monitorExecution\":true,\"concurrentDataProcessThreadCount\":10,\"streamingProcess\":true,\"maxTimeDiffSeconds\":-1,\"monitorPort\":-1,"
                + "\"failover\":false,\"misfire\":true}");
    }
}