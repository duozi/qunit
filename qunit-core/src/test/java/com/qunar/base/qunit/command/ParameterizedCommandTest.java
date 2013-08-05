package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.event.StepNotifier;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.HttpResponse;
import com.qunar.base.qunit.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 2:40 PM
 */
public class ParameterizedCommandTest {
    @Test
    public void should_process_param_with_str_prefix() throws Throwable {
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        KeyValueStore name = new KeyValueStore("name", "str:6");
        params.add(name);
        ParameterizedCommand command = new ParameterizedCommand(params) {


            @Override
            protected Response doExecuteInternal(Response response, List<KeyValueStore> processedParams, Context context) {
                assertThat(processedParams.get(0).getValue().toString().length(), is(6));
                return null;
            }

            @Override
            public StepCommand doClone() {
                return this;
            }

            @Override
            public Map<String, Object> toReport() {
                return null;
            }
        };

        command.execute(null, null, new StepNotifier());
    }

    @Test
    public void should_replace_all_regex_str() throws Throwable {
        String str = "{\n" +
                "\t\t\t\t\t  \"logoUrl\":\"http://flight.qunar.com/twell/flight/redirect.jsp?url=http://ichotelsgroup.com/redirect?path=home&brandCode=6c&regionCode=280&localeCode=zh&_PMID=99585032?cm_mmc=mdpr-_-Qunar-_-HotelSearch-_-6C_Contextual?id=ihghotel&city=beijing_city&fromDate=${fromDate}&type=HE\",\n" +
                "\t\t\t\t\t  \"hotel\":[\n" +
                "\t\t\t\t\t    {\n" +
                "\t\t\t\t\t      \"hotelPrice\":\"¥1234\",\n" +
                "\t\t\t\t\t      \"hotelName\":\"北京中环假日酒店\",\n" +
                "\t\t\t\t\t      \"bookingUrl\":\"http://flight.qunar.com/twell/flight/redirect.jsp?url=http://hotel.qunar.com/booksystem/booking.jsp?full=false&tid=995293&required1=${fromDate}&required2=${toDate}&cpcRoomType=%E5%81%87%E6%97%A5%E9%AB%98%E7%BA%A7%E6%88%BF-%E4%B8%8D%E5%90%AB%E6%97%A9%E6%8F%90%E5%89%8D2%E5%A4%A9%E8%BF%9E%E4%BD%8F2%E6%99%9A%E8%B5%B7&iPt=om?id=ihghotel&city=beijing_city&fromDate=${fromDate}&price=1234&type=HB&hotelid=beijing_city_346\"\n" +
                "\t\t\t\t\t    }\n" +
                "\t\t\t\t\t  ],\n" +
                "\t\t\t\t\t  \"hotelUrl\":\"http://flight.qunar.com/twell/flight/redirect.jsp?url=http://ichotelsgroup.com/redirect?path=home&brandCode=6c&regionCode=280&localeCode=zh&_PMID=99585032?cm_mmc=mdpr-_-Qunar-_-HotelSearch-_-6C_Contextual?id=ihghotel&city=beijing_city&fromDate=${fromDate}&type=HA\"\n" +
                "\t\t\t\t\t}";

        ParameterizedCommand command = new ParameterizedCommand(Arrays.asList(new KeyValueStore("data", str))) {

            @Override
            protected Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams, Context context) throws Throwable {
                return null;
            }

            @Override
            public StepCommand doClone() {
                return this;
            }

            @Override
            public Map<String, Object> toReport() {
                return null;
            }

        };

        Response response = new HttpResponse(200, "");
        Context context = new Context(null);
        context.addContext("fromDate", "2012-08-08");
        context.addContext("toDate", "2012-08-09");
        command.doExecute(response, context);
        String paramValue = command.params.get(0).getValue().toString();
        Assert.assertThat(paramValue, not(containsString("${fromDate}")));
        Assert.assertThat(paramValue, not(containsString("${toDate}")));
    }

}
