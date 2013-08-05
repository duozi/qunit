package com.qunar.base.qunit.util;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.AssertConfig;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 7/17/12
 * Time: 4:53 PM
 */
public class ParameterUtilsTest {

    private Context caseContext;

    @Before
    public void setUp() throws Exception {
        caseContext = new Context(new Context(null));
    }

    @Test
    public void should_replace_string_variable() {
        List<KeyValueStore> params = Arrays.asList(new KeyValueStore("userid", "$result.userid"));
        Response preResult = new Response("{\"userid\":\"1\"}", null);
        List<KeyValueStore> processedParams = ParameterUtils.prepareParameters(params, preResult, caseContext);

        assertThat(processedParams.size(), is(1));
        assertThat((String) processedParams.get(0).getValue(), is("1"));
    }

    @Test
    public void should_replace_map_variable() {
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        Map user = new HashMap();
        user.put("userid", "$result.userid");
        KeyValueStore param = new KeyValueStore("user", user);
        params.add(param);
        Response preResult = new Response("{\"userid\":\"1\"}", null);
        List<KeyValueStore> processedParams = ParameterUtils.prepareParameters(params, preResult, caseContext);

        assertThat(processedParams.size(), is(1));
        assertThat((String) ((Map) processedParams.get(0).getValue()).get("userid"), is("1"));
    }

    @Test
    public void should_replace_list_paramter() throws DocumentException {
        String xml = "<call service=\"launchBatchRemitRequest\">\n" +
                "\t\t\t<groupNo>DATE(0,HHmmssSS)</groupNo>\n" +
                "\t\t\t<list>\n" +
                "\t\t\t\t<remitRequestParam>\n" +
                "\t\t\t\t\t<remitRequestNo>${remitRequestNo1}</remitRequestNo>\n" +
                "\t\t\t\t\t<accountType>TO_PRIVATE</accountType>\n" +
                "\t\t\t\t\t<bankCard>\n" +
                "\t\t\t\t\t\t<openAccountName>霖霖</openAccountName>\n" +
                "\t\t\t\t\t\t<cardNo>4324234347874231</cardNo>\n" +
                "\t\t\t\t\t\t<bankNo>ICBC</bankNo>\n" +
                "\t\t\t\t\t\t<bankName>招行银行</bankName>\n" +
                "\t\t\t\t\t\t<province>北京</province>\n" +
                "\t\t\t\t\t\t<city>北京</city>\n" +
                "\t\t\t\t\t</bankCard>\n" +
                "\t\t\t\t\t<amount>${amount1}</amount>\n" +
                "\t\t\t\t\t<isUrgent>${isUrgent1}</isUrgent>\n" +
                "\t\t\t\t\t<currencyType>CNY</currencyType>\n" +
                "\t\t\t\t\t<notifyAddress>http://paydev.qunar.com/callback/success.jsp</notifyAddress>\n" +
                "\t\t\t\t</remitRequestParam>\n" +
                "\t\t\t\t<remitRequestParam>\n" +
                "\t\t\t\t\t<remitRequestNo>${remitRequestNo2}</remitRequestNo>\n" +
                "\t\t\t\t\t<accountType>TO_PRIVATE</accountType>\n" +
                "\t\t\t\t\t<bankCard>\n" +
                "\t\t\t\t\t\t<openAccountName>霖霖</openAccountName>\n" +
                "\t\t\t\t\t\t<cardNo>4324234347874231</cardNo>\n" +
                "\t\t\t\t\t\t<bankNo>ICBC</bankNo>\n" +
                "\t\t\t\t\t\t<bankName>招行银行</bankName>\n" +
                "\t\t\t\t\t\t<province>北京</province>\n" +
                "\t\t\t\t\t\t<city>北京</city>\n" +
                "\t\t\t\t\t</bankCard>\n" +
                "\t\t\t\t\t<amount>${amount2}</amount>\n" +
                "\t\t\t\t\t<isUrgent>${isUrgent2}</isUrgent>\n" +
                "\t\t\t\t\t<currencyType>CNY</currencyType>\n" +
                "\t\t\t\t\t<notifyAddress>http://paydev.qunar.com/callback/success.jsp</notifyAddress>\n" +
                "\t\t\t\t</remitRequestParam>\n" +
                "\t\t\t</list>\n" +
                "\t\t</call>";

        Document document = loadXml(xml);
        AssertConfig config = ConfigUtils.init(AssertConfig.class, document.getRootElement());

        Context context = new Context(null);
        context.addContext("remitRequestNo1", "1111");
        context.addContext("amount1", "0.02");
        context.addContext("remitRequestNo2", "2222");
        List<KeyValueStore> processedParameters = ParameterUtils.prepareParameters(config.getParams(), null, context);
        Object value = processedParameters.get(1).getValue();
        List<KeyValueStore> p1 = (List<KeyValueStore>) value;
        Map map = (Map) p1.get(0).getValue();
        assertThat(map.get("remitRequestNo").toString(), is("1111"));
    }

    private Document loadXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        StringReader stream = new StringReader(xml);
        return reader.read(stream);
    }
}
