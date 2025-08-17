package com.internship.deal_service.service.rabbit;

import com.internship.deal_service.model.dto.ContractorRequestRabbit;
import com.internship.deal_service.model.dto.DealContractorDto;

public interface DealContractorRabbitService {

    DealContractorDto saveDealContractorWithUserId(ContractorRequestRabbit request);

}
