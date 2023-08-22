package com.iemr.common.config.quartz;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.iemr.common.service.nhm_dashboard.NHM_AgentRealTimeDataService;
@Component
public class ScheduleJobForNHMAgentRealTimeData {
	@Value("${nhm.agent.real.time.data.cron.flag}")
	private boolean nhmFlag;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	@Autowired
	NHM_AgentRealTimeDataService agentRealTimeDataService;
	@Scheduled(cron = "${nhm.agent.real.time.data.cron.scheduler}")
	public void getAllNHMAgentRealTimeData() throws IOException {
		if(nhmFlag) {
			log.info("Calling Scheduler for NHM_Agent_Real_Time_Data");
			agentRealTimeDataService.getData();
		}
	}
}
