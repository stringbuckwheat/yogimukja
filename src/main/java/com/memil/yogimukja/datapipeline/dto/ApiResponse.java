package com.memil.yogimukja.datapipeline.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class ApiResponse {

    @JsonProperty("LOCALDATA_072404")
    private LocalData localdata;

    @Getter
    @ToString
    public static class LocalData {
        @JsonProperty("list_total_count")
        private int listTotalCount;

        @JsonProperty("RESULT")
        private Result result;

        @JsonProperty("row")
        @Getter
        private List<Row> row;
    }

    @Getter
    @ToString
    public static class Result {
        @JsonProperty("CODE")
        private String code;

        @JsonProperty("MESSAGE")
        private String message;
    }
    @Getter
    @ToString
    public static class Row {
        @JsonProperty("OPNSFTEAMCODE")
        private String opnsfTeamCode;  // 개방자치단체코드

        @JsonProperty("MGTNO")
        private String mgtNo;  // 관리번호

        @JsonProperty("APVPERMYMD")
        private String apvPermYmd;  // 인허가일자

        @JsonProperty("APVCANCELYMD")
        private String apvCancelYmd;  // 인허가취소일자

        @JsonProperty("TRDSTATEGBN")
        private String trdStateGbn;  // 영업상태코드

        @JsonProperty("TRDSTATENM")
        private String trdStateNm;  // 영업상태명

        @JsonProperty("DTLSTATEGBN")
        private String dtlStateGbn;  // 상세영업상태코드

        @JsonProperty("DTLSTATENM")
        private String dtlStateNm;  // 상세영업상태명

        @JsonProperty("DCBYMD")
        private String dcbYmd;  // 폐업일자

        @JsonProperty("CLGSTDT")
        private String clgStdt;  // 휴업시작일자

        @JsonProperty("CLGENDDT")
        private String clgEndDt;  // 휴업종료일자

        @JsonProperty("ROPNYMD")
        private String ropnYmd;  // 재개업일자

        @JsonProperty("SITETEL")
        private String siteTel;  // 전화번호

        @JsonProperty("SITEAREA")
        private String siteArea;  // 소재지면적

        @JsonProperty("SITEPOSTNO")
        private String sitePostNo;  // 소재지우편번호

        @JsonProperty("SITEWHLADDR")
        private String siteWhlAddr;  // 지번주소

        @JsonProperty("RDNWHLADDR")
        private String rdnWhlAddr;  // 도로명주소

        @JsonProperty("RDNPOSTNO")
        private String rdnPostNo;  // 도로명우편번호

        @JsonProperty("BPLCNM")
        private String bplcNm;  // 사업장명

        @JsonProperty("LASTMODTS")
        private String lastModTs;  // 최종수정일자

        @JsonProperty("UPDATEGBN")
        private String updateGbn;  // 데이터갱신구분

        @JsonProperty("UPDATEDT")
        private String updateDt;  // 데이터갱신일자

        @JsonProperty("UPTAENM")
        private String uptAenM;  // 업태구분명

        @JsonProperty("X")
        private String x;  // 좌표정보(X)

        @JsonProperty("Y")
        private String y;  // 좌표정보(Y)

        @JsonProperty("SNTUPTAENM")
        private String sntUpTaenM;  // 위생업태명

        @JsonProperty("MANEIPCNT")
        private String maneipcCnt;  // 남성종사자수

        @JsonProperty("WMEIPCNT")
        private String wmeipcCnt;  // 여성종사자수

        @JsonProperty("TRDPJUBNSENM")
        private String trdPjubnSenM;  // 영업장주변구분명

        @JsonProperty("LVSENM")
        private String lvsEnM;  // 등급구분명

        @JsonProperty("WTRSPLYFACILSENM")
        private String wtrSplyFacilSenM;  // 급수시설구분명

        @JsonProperty("TOTEPNUM")
        private String totEpNum;  // 총인원

        @JsonProperty("HOFFEPCNT")
        private String hoffepCnt;  // 본사종업원수

        @JsonProperty("FCTYOWKEPCNT")
        private String fctyOwkePcCnt;  // 공장사무직종업원수

        @JsonProperty("FCTYSILJOBEPCNT")
        private String fctySilJobePcCnt;  // 공장판매직종업원수

        @JsonProperty("FCTYPDTJOBEPCNT")
        private String fctyPdtJobePcCnt;  // 공장생산직종업원수

        @JsonProperty("BDNGOWNSENM")
        private String bdngOwnSenM;  // 건물소유구분명

        @JsonProperty("ISREAM")
        private String isReam;  // 보증액

        @JsonProperty("MONAM")
        private String monAm;  // 월세액

        @JsonProperty("MULTUSNUPSOYN")
        private String multUsnUpsoYn;  // 다중이용업소여부

        @JsonProperty("FACILTOTSCP")
        private String faciltOtsCp;  // 시설총규모

        @JsonProperty("JTUPSOASGNNO")
        private String jtUpSoAsgnNo;  // 전통업소지정번호

        @JsonProperty("JTUPSOMAINEDF")
        private String jtUpSoMainEdF;  // 전통업소주된음식

        @JsonProperty("HOMEPAGE")
        private String homePage;  // 홈페이지
    }
}

