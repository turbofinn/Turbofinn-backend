package org.turbofinn.util;

public class Constants {
    public static final boolean IS_PROD = false;
    public static final boolean IS_PROD_TESTING = false;
    public static final boolean USE_LOCAL_AWS_CREDENTIALS = false;
    public static final String LOCAL_AWS_CREDENTIALS_PROFILE = "turbofinn";

    public static final int SUCCESS_RESPONSE_CODE = 1001;
    public static final int INVALID_INPUTS_RESPONSE_CODE = 9999;
    public static final int GENERIC_RESPONSE_CODE = 5001;
    public static final String SUCCESS_RESPONSE_MESSAGE = "Success";
    public static final String INVALID_INPUTS_RESPONSE_MESSAGE = "Invalid inputs";
    public static final String GENERIC_ERROR_RESPONSE_MESSAGE = "Invalid arguments";

    public static final Integer RESTAURANT_ACCOUNT_NO_LENGTH = 10;

    public static final String RDS_URL = "jdbc:mysql://turbofinn-dev.cjmo2k0q4pt6.ap-south-1.rds.amazonaws.com:3306/Turbofinn";
    public static final String RDS_USERNAME = "root";
    public static final String RDS_PASSWORD = "Turbofinn111";




}