package com.wadaro.promotor.api;

public class Config {
//    public static final String MAIN_PATH         = "http://103.41.205.42/wadaro-erp/android/api/v1/promotor/";
//    public static final String IMAGE_PATH        = "http://103.41.205.42/wadaro-api/public/images/promotor/";
//    public static final String IMAGE_PATH_BOOKER = "http://103.41.205.42/wadaro-api/public/images/booker/";

    public static final String MAIN_PATH = "https://erp-api.wadaro.id/wadaro-erp/android/api/v1/promotor/";
    public static final String IMAGE_PROFILE = "https://erp-api.wadaro.id/wadaro-erp/production/uploads/photo/employee/";
    public static final String IMAGE_PATH_BOOKER = "https://erp-api.wadaro.id/wadaro-api/public/images/booker/";

    public static final String API_LOGIN =  "login";
    public static final String API_FORGOT_PASSWORD =  "forgot-password";

    public static final String API_TOKEN_CHECK_PROFILE =  "profile/#userId";
    public static final String CHANGE_PASSWORD =  "profile/#userId/change-password";
    public static final String CHANGE_PROFILE_PHOTO =  "profile/#userId/change-photo";

    public static final String DEMO_CHANGE_COORDINATOR =  "move-coordinator";

    public static final String API_TOKEN_DEMO_CONFIRMED =  "process-demo/booking-confirmed";
    public static final String DEMO_CANCELLED =  "process-demo/booking-canceled";
    public static final String DEMO_ITEM_CONFIRMATION =  "process-demo/confirmation";
    public static final String DEMO_PROCESS =  "process-demo";

    public static final String API_TOKEN_CHECK_COORDINATOR_NIK =  "coordinator/{nik}/check-nik";

    public static final String GET_SUDDEN_DEMO =  "booking/create";
    public static final String API_TOKEN_SUDDEN_DEMO =  "booking";

    public static final String GET_DEMO_DATA =  "assignment-demo";
    public static final String GET_DEMO_DATA_DETAIL =  "assignment-demo/detail-booking";
    public static final String UPLOAD_DENAH =  "assignment-demo/upload-denah";

    public static final String API_TOKEN_GET_DEMO_DATA_DETAIL_JP =  "consumer-lead/find-consumer-lead";
    public static final String CREATE_DEMO_DATA_JP =  "consumer-lead";
    public static final String DELETE_DEMO_DATA_JP =  "consumer-lead/";

    public static final String GET_DATA_FOR_CREATE_SALES_ORDER =  "sales-order";
    public static final String CREATE_SALES_ORDER =  "sales-order/create";
    public static final String CREATE_SALES_ORDER_WO =  "sales-order/create/without-so";
    public static final String SALES_ORDER_DETAIL =  "sales-order/detail";
    public static final String SALES_ORDER_REPORT =  "sales-order/report";
    public static final String SALES_ORDER_ADD_PRODUCT =  "sales-order/detail/add-product";
    public static final String SALES_ORDER_DELETE_PRODUCT =  "sales-order/detail/delete-product/";

    public static final String GET_INFO_PRODUCT =  "product-sales";
    public static final String GET_DETAIL_INFO_PRODUCT =  "product-sales/#productId";

    public static final String GET_HASIL_DEMO =  "booking/report";
    public static final String UPLOAD_OFFLINE =  "offline/upload";
}
