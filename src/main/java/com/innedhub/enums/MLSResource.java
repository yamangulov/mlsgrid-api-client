package com.innedhub.enums;

import com.innedhub.exceptions.NotValidParamsForSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

@AllArgsConstructor
@Getter
public enum MLSResource {
    PROPERTY_RESI(
            "PropertyResi",
            getParamsList("Property")
    ),
    PROPERTY_RLSE(
            "PropertyRlse",
            getParamsList("Property")
    ),
    PROPERTY_RINC(
            "PropertyRinc",
            getParamsList("Property")
    ),
    PROPERTY_LAND(
            "PropertyLand",
            getParamsList("Property")
    ),
    PROPERTY_FARM(
            "PropertyFarm",
            getParamsList("Property")
    ),
    PROPERTY_MOBI(
            "PropertyMobi",
            getParamsList("Property")
    ),
    PROPERTY_COMS(
            "PropertyComs",
            getParamsList("Property")
    ),
    PROPERTY_COML(
            "PropertyComl",
            getParamsList("Property")
    ),
    PROPERTY_BUSO(
            "PropertyBuso",
            getParamsList("Property")
    ),
    MEDIA(
            "Media",
            getParamsList("Media")
    ),
    MEMBER(
            "Member",
            getParamsList("Member")
    ),
    OFFICE(
            "Office",
            getParamsList("Office")
    ),
    OPEN_HOUSE(
            "OpenHouse",
            getParamsList("OpenHouse")
    );

    private final String resource;
    private final List<String> resourceParams;

    private static List<String> getParamsList(String resourceName) {
        List<String> paramsList = new LinkedList<>();
        switch (resourceName) {
            case "Property":
                paramsList = Arrays.asList(
                        "ModificationTimestamp",
                        "OriginatingSystemName",
                        "StandardStatus",
                        "ListingId",
                        "MlgCanView"
                );
                break;
            case "Media":
                paramsList = Arrays.asList(
                        "ModificationTimestamp",
                        "OriginatingSystemName",
                        "MediaKey",
                        "ResourceRecordID",
                        "MlgCanView"
                );
                break;
            case "Member":
                paramsList = Arrays.asList(
                        "ModificationTimestamp",
                        "OriginatingSystemName",
                        "MemberMlsId",
                        "MlgCanView"
                );
                break;
            case "Office":
                paramsList = Arrays.asList(
                        "ModificationTimestamp",
                        "OriginatingSystemName",
                        "OfficeMlsId",
                        "MlgCanView"
                );
                break;
            case "OpenHouse":
                paramsList = Arrays.asList(
                        "ModificationTimestamp",
                        "OriginatingSystemName",
                        "OpenHouseKey",
                        "ListingId",
                        "MlgCanView",
                        "OpenHouseDate"
                );
                break;
            default:
                try {
                    throw new NotValidParamsForSearch();
                } catch (NotValidParamsForSearch e) {
                    e.printStackTrace();
                }
        }
        return paramsList;
    }

}
