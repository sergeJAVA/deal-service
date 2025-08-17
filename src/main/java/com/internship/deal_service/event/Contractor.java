package com.internship.deal_service.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Entity
@Table(name = "contractors")
public class Contractor {

    // Основные поля
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_full")
    private String nameFull;

    @Column(name = "inn")
    private String inn;

    @Column(name = "ogrn")
    private String ogrn;

    @Column(name = "country_id")
    private String countryId;

    @Column(name = "industry_id")
    private Integer industryId;

    @Column(name = "org_form_id")
    private Integer orgFormId;


    // Служебные поля
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @Column(name = "create_user_id")
    private String createUserId;

    @Column(name = "modify_user_id")
    private String modifyUserId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "industry_name")
    private String industryName;

    @Column(name = "org_form_name")
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
