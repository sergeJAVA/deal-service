package com.internship.deal_service.event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Contractor {

    // Основные поля
    private String id;
    private String parentId;
    private String name;
    private String nameFull;
    private String inn;
    private String ogrn;
    private String countryId;
    private Integer industryId;
    private Integer orgFormId;

    // Служебные поля
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    private String createUserId;
    private String modifyUserId;
    private Boolean isActive;

    // поля для поиска
    private String countryName;
    private String industryName;
    private String orgFormName;

    public Contractor() {

    }

    public Contractor(String id,
                      String parentId, String name, String nameFull,
                      String inn, String ogrn, String countryId, Integer industryId,
                      Integer orgFormId, LocalDateTime createDate,
                      LocalDateTime modifyDate, String createUserId,
                      String modifyUserId, Boolean isActive) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.nameFull = nameFull;
        this.inn = inn;
        this.ogrn = ogrn;
        this.countryId = countryId;
        this.industryId = industryId;
        this.orgFormId = orgFormId;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.createUserId = createUserId;
        this.modifyUserId = modifyUserId;
        this.isActive = isActive;
    }

    public Contractor(String id, String parentId, String name,
                      String nameFull, String inn, String ogrn,
                      String countryId, Integer industryId, Integer orgFormId,
                      LocalDateTime createDate, LocalDateTime modifyDate, String createUserId,
                      String modifyUserId, Boolean isActive, String countryName,
                      String industryName, String orgFormName) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.nameFull = nameFull;
        this.inn = inn;
        this.ogrn = ogrn;
        this.countryId = countryId;
        this.industryId = industryId;
        this.orgFormId = orgFormId;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.createUserId = createUserId;
        this.modifyUserId = modifyUserId;
        this.isActive = isActive;
        this.countryName = countryName;
        this.industryName = industryName;
        this.orgFormName = orgFormName;
    }

}
