package com.internship.deal_service.util;

import com.internship.deal_service.model.Pagination;
import com.internship.deal_service.model.dto.DealDto;
import com.internship.deal_service.model.dto.DealSearchRequest;
import com.internship.deal_service.model.security.TokenAuthentication;
import com.internship.deal_service.model.security.TokenData;
import com.internship.deal_service.service.DealService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public final class UserRoleUtil {

    private UserRoleUtil() {

    }

    public static boolean isSuperuser(List<String> roles) {
        return roles.contains("ROLE_SUPERUSER") || roles.contains("ROLE_DEAL_SUPERUSER");
    }

    public static boolean isOverdraftUser(List<String> roles) {
        return roles.contains("ROLE_OVERDRAFT_USER");
    }

    public static boolean isCreditUser(List<String> roles) {
        return roles.contains("ROLE_CREDIT_USER");
    }

    public static boolean isOverdraftAndCreditUser(List<String> roles) {
        return isOverdraftUser(roles) && isCreditUser(roles);
    }

    public static boolean isOnlyOverdraftUser(List<String> roles) {
        return isOverdraftUser(roles) && !isCreditUser(roles);
    }

    public static boolean isOnlyCreditUser(List<String> roles) {
        return isCreditUser(roles) && !isOverdraftUser(roles);
    }

    public static Page<DealDto> searchDealConditionals(TokenAuthentication tokenAuthentication,
                                                       DealService dealService,
                                                       DealSearchRequest request,
                                                       int page,
                                                       int size) {
        Page<DealDto> resultPage = null;
        TokenData tokenData = tokenAuthentication.getTokenData();

        List<String> roles = tokenData.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (UserRoleUtil.isSuperuser(roles)) {
            resultPage = dealService.searchDeals(request, new Pagination(page, size));
        } else if (UserRoleUtil.isOverdraftAndCreditUser(roles)) {
            DealSearchRequest filterRequest = DealSearchRequest.builder()
                    .typeIds(List.of("OVERDRAFT", "CREDIT"))
                    .build();
            resultPage = dealService.searchDeals(filterRequest, new Pagination(page, size));
        } else if (UserRoleUtil.isOnlyOverdraftUser(roles)) {
            DealSearchRequest filterRequest = DealSearchRequest.builder()
                    .typeIds(List.of("OVERDRAFT"))
                    .build();
            resultPage = dealService.searchDeals(filterRequest, new Pagination(page, size));
        } else if (UserRoleUtil.isOnlyCreditUser(roles)) {
            DealSearchRequest filterRequest = DealSearchRequest.builder()
                    .typeIds(List.of("CREDIT"))
                    .build();
            resultPage = dealService.searchDeals(filterRequest, new Pagination(page, size));
        }

        return resultPage;
    }

    public static byte[] exportDealConditionals(TokenAuthentication tokenAuthentication,
                                                DealSearchRequest searchRequest,
                                                DealService dealService,
                                                int page,
                                                int size) {
        byte[] excelBytes = null;

        TokenData tokenData = tokenAuthentication.getTokenData();

        List<String> roles = tokenData.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (UserRoleUtil.isSuperuser(roles)) {
            excelBytes = dealService.exportDealsToExcel(searchRequest, new Pagination(page, size));
        } else if (UserRoleUtil.isOverdraftAndCreditUser(roles)) {
            DealSearchRequest filterRequest = DealSearchRequest.builder()
                    .typeIds(List.of("OVERDRAFT", "CREDIT"))
                    .build();
            excelBytes = dealService.exportDealsToExcel(filterRequest, new Pagination(page, size));
        } else if (UserRoleUtil.isOnlyOverdraftUser(roles)) {
            DealSearchRequest filterRequest = DealSearchRequest.builder()
                    .typeIds(List.of("OVERDRAFT"))
                    .build();
            excelBytes = dealService.exportDealsToExcel(filterRequest, new Pagination(page, size));
        } else if (UserRoleUtil.isOnlyCreditUser(roles)) {
            DealSearchRequest filterRequest = DealSearchRequest.builder()
                    .typeIds(List.of("CREDIT"))
                    .build();
            excelBytes = dealService.exportDealsToExcel(filterRequest, new Pagination(page, size));
        }

        return excelBytes;
    }

}
