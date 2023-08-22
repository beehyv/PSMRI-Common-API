package com.iemr.common.repository.nhm_dashboard;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iemr.common.data.nhm_dashboard.AgentRealTimeData;
@Repository
public interface NHMAgentRealTimeDataRepo extends CrudRepository<AgentRealTimeData, Long>{

}
