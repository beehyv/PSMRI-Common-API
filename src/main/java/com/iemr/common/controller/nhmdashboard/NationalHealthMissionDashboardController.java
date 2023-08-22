/*
* AMRIT – Accessible Medical Records via Integrated Technology 
* Integrated EHR (Electronic Health Records) Solution 
*
* Copyright (C) "Piramal Swasthya Management and Research Institute" 
*
* This file is part of AMRIT.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see https://www.gnu.org/licenses/.
*/
package com.iemr.common.controller.nhmdashboard;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.iemr.common.data.nhm_dashboard.AbandonCallSummary;
import com.iemr.common.data.nhm_dashboard.AgentSummaryReport;
import com.iemr.common.data.nhm_dashboard.DetailedCallReport;
import com.iemr.common.service.nhm_dashboard.NHM_DashboardService;
import com.iemr.common.utils.mapper.InputMapper;
import com.iemr.common.utils.response.OutputResponse;

import io.swagger.annotations.ApiOperation;

@RequestMapping(value = "/nhm_dashboard")
@RestController
public class NationalHealthMissionDashboardController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Autowired
	private NHM_DashboardService nHM_DashboardService;

	@CrossOrigin()
	@ApiOperation(value = "Push abandoned calls from call centre")
	@RequestMapping(value = "/push/abandon_calls", method = RequestMethod.POST, headers = "Authorization")
	public String pushAbandonCallsFromC_Zentrix(@RequestBody AbandonCallSummary abandonCallSummary) {
		OutputResponse output = new OutputResponse();

		try {
			String s = nHM_DashboardService.pushAbandonCalls(abandonCallSummary);
			output.setResponse(s);
		} catch (Exception e) {
			logger.error("error in NHM Push Abandon call API : " + e.getLocalizedMessage());
			output.setError(5000, e.getLocalizedMessage());
		}
		return output.toString();
	}

	@ApiOperation(value = "Get abandoned call information")
	@CrossOrigin()
	@RequestMapping(value = "/get/abandon_calls", method = RequestMethod.GET, headers = "Authorization")
	public String getAbandonCalls() {
		OutputResponse output = new OutputResponse();

		try {
			String s = nHM_DashboardService.getAbandonCalls();
			output.setResponse(s);
		} catch (Exception e) {
			logger.error("error in get Abandon call API : " + e.getLocalizedMessage());
			output.setError(5000, e.getLocalizedMessage());
		}
		return output.toString();
	}

	@ApiOperation(value = "Get agent wise staff & idle time")
	@CrossOrigin()
	@RequestMapping(value = "/get/agentsummaryreport", method = RequestMethod.GET, headers = "Authorization")
	public String getAgentSummaryReport() {
		OutputResponse output = new OutputResponse();

		try {
			String s = nHM_DashboardService.getAgentSummaryReport();
			output.setResponse(s);
		} catch (Exception e) {
			logger.error("error in get agent summary report API : " + e.getLocalizedMessage());
			output.setError(5000, e.getLocalizedMessage());
		}
		return output.toString();
	}

	@ApiOperation(value = "Get detailed call report")
	@CrossOrigin()
	@RequestMapping(value = "/get/detailedCallReport", method = RequestMethod.GET, headers = "Authorization")
	public String getDetailedCallReport() {
		OutputResponse output = new OutputResponse();

		try {
			String s = nHM_DashboardService.getDetailedCallReport();
			output.setResponse(s);
		} catch (Exception e) {
			logger.error("error in get detailed call report API : " + e.getLocalizedMessage());
			output.setError(5000, e.getLocalizedMessage());
		}
		return output.toString();
	}
}
