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
/**
 * 
 */
package com.iemr.common.service.beneficiary;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.iemr.common.data.beneficiary.Beneficiary;
import com.iemr.common.data.mctshistory.MctsOutboundCallDetail;
import com.iemr.common.dto.identity.BeneficiariesDTO;
import com.iemr.common.dto.identity.CommonIdentityDTO;
import com.iemr.common.dto.identity.Identity;
import com.iemr.common.dto.identity.IdentityEditDTO;
import com.iemr.common.mapper.CommonIdentityMapper;
import com.iemr.common.mapper.IdentityBenEditMapper;
import com.iemr.common.mapper.utils.InputMapper;
import com.iemr.common.model.beneficiary.BeneficiaryDemographicsModel;
import com.iemr.common.model.beneficiary.BeneficiaryGenModel;
import com.iemr.common.model.beneficiary.BeneficiaryModel;
import com.iemr.common.repository.mctshistory.OutboundHistoryRepository;
import com.iemr.common.utils.exception.IEMRException;
import com.iemr.common.utils.mapper.OutputMapper;
import com.iemr.common.utils.response.OutputResponse;
import com.iemr.common.utils.validator.Validator;

/**
 * @author WA875423
 *
 */
@Service
public class RegisterBenificiaryServiceImpl implements RegisterBenificiaryService {

	@Autowired
	CommonIdentityMapper identityMapper;
	@Autowired
	IdentityBeneficiaryService identityBeneficiaryService;

	@Autowired
	IdentityBenEditMapper identityBenEditMapper;

	@Autowired
	Validator validator;

	@Autowired
	OutboundHistoryRepository outboundHistoryRepository;

	/**
	 * IemrUserRegisterRepository declare
	 */
	// private BeneficiaryRepository benificiaryRepository;

	/**
	 * Inject IemrUserRegisterRepository
	 */

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	@Override
	public Beneficiary save(Beneficiary benificiaryDetails) {

		logger.info("benificiaryDetails: " + benificiaryDetails);

		return new Beneficiary(); // savedBenificiaryDetails;
	}

	@Async
	private void updateBeneficiaryID(String beneficiaryID, Long beneficiaryRegID) {
		try {
			// benificiaryRepository.addBeneficiaryID(beneficiaryID, beneficiaryRegID);
		} catch (Exception e) {
			logger.error("updateBeneficiaryID failed with error " + e.getMessage(), e);
		}
	}

	@Async
	@Override
	public Integer updateBenificiary(BeneficiaryModel benificiaryDetails, String auth) throws IEMRException {
		Integer updatedRows = 0;

		IdentityEditDTO identityEditDTO = identityBenEditMapper.BenToIdentityEditMapper(benificiaryDetails);
		if (benificiaryDetails.getBeneficiaryIdentities() != null
				&& benificiaryDetails.getBeneficiaryIdentities().size() > 0) {
			identityEditDTO.setIdentities(Identity.createIdentity(benificiaryDetails.getBeneficiaryIdentities(),
					benificiaryDetails.getCreatedBy()));
		}
		identityEditDTO.setDob(benificiaryDetails.getDOB());
		// String jsoninput=new Gson().toJson(identityEditDTO);
		updatedRows = identityBeneficiaryService.editIdentityEditDTO(identityEditDTO, auth,
				benificiaryDetails.getIs1097());

		return updatedRows;
	}

	private int updateDemographics(BeneficiaryDemographicsModel i_BenDemographics) {
		int updatedRows = 0;
		System.out.println(i_BenDemographics);

		return updatedRows;
	}

	@Override
	public String save(BeneficiaryModel beneficiaryModel, HttpServletRequest servletRequest) throws Exception {

//		logger.info("benificiaryDetails: " + beneficiaryModel);

		CommonIdentityDTO identityDTO = identityMapper.beneficiaryModelCommonIdentityDTO(beneficiaryModel);
		identityDTO.setEmergencyRegistration(beneficiaryModel.isEmergencyRegistration());
		identityDTO
				.setBenFamilyDTOs(identityMapper.benPhoneMapListToBenFamilyDTOList(beneficiaryModel.getBenPhoneMaps()));
		String request = InputMapper.getInstance().gson().toJson(identityDTO).toString();

		if (beneficiaryModel.getIs1097() == null)
			beneficiaryModel.setIs1097(false);

		String identityResponse = identityBeneficiaryService.getIdentityResponse(request,
				servletRequest.getHeader("authorization"), beneficiaryModel.getIs1097());
		JSONObject responseObj = new JSONObject(identityResponse);
		BeneficiaryModel beneficiary = new BeneficiaryModel();
		if (responseObj.has("response")) {
			OutputResponse response = InputMapper.getInstance()
					.fromJson(responseObj.getJSONObject("response").toString(), OutputResponse.class);
			if (response.isSuccess()) {
				BeneficiariesDTO createdBen = InputMapper.getInstance().fromJson(response.getData(),
						BeneficiariesDTO.class);
				beneficiaryModel.setBeneficiaryRegID(Long.parseLong(createdBen.getBenRegId().toString()));
				beneficiaryModel.setBeneficiaryID(createdBen.getBenId().toString());
				beneficiary = beneficiaryModel;
			} else {
				return response.toString();
			}
		}
		return OutputMapper.gsonWithoutExposeRestriction().toJson(beneficiary);
	}

	@Override
	public Integer updateCommunityorEducation(BeneficiaryModel benificiaryDetails, String auth) throws IEMRException {
		IdentityEditDTO identityEditDTO = new IdentityEditDTO();
		identityEditDTO.setBeneficiaryRegId(new BigInteger(benificiaryDetails.getBeneficiaryRegID() + ""));
		if (benificiaryDetails.getI_bendemographics() != null) {
			identityEditDTO.setCommunityId(benificiaryDetails.getI_bendemographics().getCommunityID());
			identityEditDTO.setEducationId(benificiaryDetails.getI_bendemographics().getEducationID());
		}

		Integer updatedRows = 0;
		updatedRows = identityBeneficiaryService.editIdentityEditDTOCommunityorEducation(identityEditDTO, auth,
				benificiaryDetails.getIs1097());
		// updateBeneficiaryPreferredLanguage(identityEditDTO, auth);
		return updatedRows;
	}

	private void updateMCTSRecord(BeneficiaryModel beneficiaryModel) {
		List<MctsOutboundCallDetail> mctsOutboundCallDetails = new ArrayList<MctsOutboundCallDetail>();

		try {

			mctsOutboundCallDetails = outboundHistoryRepository.checkBenExist(beneficiaryModel.getBeneficiaryRegID());

			MctsOutboundCallDetail callDetail = mctsOutboundCallDetails.get(0);

			if (callDetail != null && callDetail.getIsMother() != null) {

				String firstName = beneficiaryModel.getFirstName();
				String lastName = beneficiaryModel.getLastName();
				String name = "";
				if (lastName != null && !lastName.equalsIgnoreCase("")) {
					name = firstName + " " + lastName;
				} else {
					name = firstName;
				}

				Integer age = beneficiaryModel.getAge();

				String districtName = outboundHistoryRepository
						.getDistrictName(beneficiaryModel.getI_bendemographics().getDistrictID());
				String talukaName = outboundHistoryRepository
						.getBlockName(beneficiaryModel.getI_bendemographics().getBlockID());
				String villageName = outboundHistoryRepository
						.getVillageName(beneficiaryModel.getI_bendemographics().getDistrictBranchID());

				String gender = null;
				if (beneficiaryModel.getGenderID() == 1) {
					gender = "Male";
				} else if (beneficiaryModel.getGenderID() == 2) {
					gender = "Female";
				} else {
					gender = "Transgender";
				}

				Timestamp lastModDate = Timestamp.valueOf(LocalDateTime.now());
				if (callDetail.getIsMother() == true) {
					int updateRow = outboundHistoryRepository.updateMotherData(name, age, districtName, talukaName,
							villageName, lastModDate, beneficiaryModel.getBeneficiaryRegID());
				} else {
					int updateRow = outboundHistoryRepository.updateChildData(name, gender, districtName, talukaName,
							villageName, lastModDate, beneficiaryModel.getBeneficiaryRegID());

				}
			}
		} catch (Exception e) {

		}

	}

	@Override
	public String generateBeneficiaryIDs(String request, HttpServletRequest servletRequest) throws Exception {

		logger.info("request: " + request.toString());
		List<BeneficiaryGenModel> listBen = identityBeneficiaryService.generateBeneficiaryIDs(request,
				servletRequest.getHeader("authorization"));

		return listBen.toString();
	}
}
