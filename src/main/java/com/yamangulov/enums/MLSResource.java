package com.yamangulov.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MLSResource {
    PROPERTY_RESI(
            "PropertyResi"
    ),
    PROPERTY_RLSE(
            "PropertyRlse"
    ),
    PROPERTY_RINC(
            "PropertyRinc"
    ),
    PROPERTY_LAND(
            "PropertyLand"
    ),
    PROPERTY_FARM(
            "PropertyFarm"
    ),
    PROPERTY_MOBI(
            "PropertyMobi"
    ),
    PROPERTY_COMS(
            "PropertyComs"
    ),
    PROPERTY_COML(
            "PropertyComl"
    ),
    PROPERTY_BUSO(
            "PropertyBuso"
    ),
    MEDIA(
            "Media"
    ),
    MEMBER(
            "Member"
    ),
    OFFICE(
            "Office"
    ),
    OPEN_HOUSE(
            "OpenHouse"
    );

    private final String resource;

}
