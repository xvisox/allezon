package pl.mimuw.allezon;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class Constants {

    public static final String HTTP_OK = "200";
    public static final String HTTP_NO_CONTENT = "204";
    public static final String HTTP_INTERNAL_SERVER_ERROR = "500";

    public static final String COOKIE_PARAM = "cookie";
    public static final String ACTION_PARAM = "action";
    public static final String AGGREGATES_PARAM = "aggregates";
    public static final String LIMIT_PARAM = "limit";
    public static final String TIME_RANGE_PARAM = "time_range";
    public static final String ORIGIN_PARAM = "origin";
    public static final String BRAND_ID_PARAM = "brand_id";
    public static final String CATEGORY_ID_PARAM = "category_id";

    public static final String APP_PROPS_PREFIX = "app";
    public static final String PROFILES_COLLECTION = "profiles";

    public static final int MAX_PROFILE_SIZE = 200;
    public static final String MAX_PROFILE_SIZE_STR = "" + MAX_PROFILE_SIZE;

    public static final String USER_TAG_TOPIC = "user-tag-topic";
}
