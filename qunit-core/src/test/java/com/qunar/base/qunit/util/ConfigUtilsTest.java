package com.qunar.base.qunit.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qunar.autotest.mock.model.HttpExpectation;
import com.qunar.base.qunit.config.MockStepConfig;
import com.qunar.base.qunit.model.AssertConfig;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.TestConfig;
import com.qunar.base.qunit.model.TestElementConfig;
import com.qunar.base.qunit.objectfactory.BeanUtils;
import com.qunar.base.qunit.response.Response;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 6/11/12
 * Time: 2:26 PM
 */
public class ConfigUtilsTest {
    @Test
    public void should_init_text() throws DocumentException {
        Document doc = loadXml("<test userName=\"yuyijq\">testtest</test>");

        TestConfig config = ConfigUtils.init(TestConfig.class, doc.getRootElement());

        assertThat(config.getText(), is("testtest"));
        assertThat(config.getUserName(), is("yuyijq"));
    }

    @Test
    public void should_read_data_with_no_name() throws DocumentException {
        Document doc = loadXml("<call><param>test</param></call>");

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());

        assertThat(config.getParams().size(), is(1));
        assertThat(config.getParams().get(0).getName(), is("param"));
        assertThat((String) config.getParams().get(0).getValue(), is("test"));
    }

    @Test
    public void should_init_element_property() throws DocumentException {
        String xml = "<call>" +
                "<param user=\"admin\" pwd=\"12345\" />" +
                "</call>";
        Document doc = loadXml(xml);

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());

        assertThat(config.getParams().size(), is(2));
        assertThat(config.getParams().get(0).getName(), is("user"));
        assertThat((String) config.getParams().get(0).getValue(), is("admin"));
        assertThat(config.getParams().get(1).getName(), is("pwd"));
        assertThat((String) config.getParams().get(1).getValue(), is("12345"));
    }

    @Test
    public void should_init_complex_element_property() throws DocumentException {
        String xml = "<call>" +
                "<hotel>" +
                "<seq>12345</seq>" +
                "</hotel>" +
                "<user>" +
                "<name>admin</name>" +
                "<pwd>12345</pwd>" +
                "</user>" +
                "</call>";
        Document doc = loadXml(xml);

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());

        assertThat(config.getParams().size(), is(2));
        assertThat(config.getParams().get(0).getName(), is("hotel"));
        Map user = (Map) config.getParams().get(1).getValue();
        assertThat((String) user.get("pwd"), is("12345"));
    }

    @Test
    public void should_init_complex_property_with_empty_value() throws DocumentException {
        String xml = "<call>" +
                "<hotel>" +
                "<seq></seq>" +
                "</hotel>" +
                "</call>";
        Document doc = loadXml(xml);

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());

        assertThat(config.getParams().size(), is(1));
        assertThat(config.getParams().get(0).getName(), is("hotel"));
        Map user = (Map) config.getParams().get(0).getValue();
        assertThat((String) user.get("seq"), is(""));
    }

    @Test
    public void should_init_attribute_property_and_complex_element() throws DocumentException {
        String xml = "<call>" +
                "<param user=\"admin\" />" +
                "<hotel>" +
                "<seq>12345</seq>" +
                "</hotel>" +
                "</call>";
        Document doc = loadXml(xml);

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());

        assertThat(config.getParams().size(), is(2));
    }

    @Test
    public void should_init_nested_object_property() throws DocumentException {
        String xml = "<call>" +
                "<hotel>" +
                "<seq>12345</seq>" +
                "<address>" +
                "<city>beijing</city>" +
                "</address>" +
                "</hotel>" +
                "</call>";
        Document doc = loadXml(xml);

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());

        assertThat(config.getParams().size(), is(1));
        assertThat((String) ((Map) ((Map) config.getParams().get(0).getValue()).get("address")).get("city"), is("beijing"));
    }

    @Test
    public void should_init_name_value_pair_parameters() throws DocumentException {
        String xml = "<call service=\"streamapi\">" +
                "<param name=\"username\">admin</param>" +
                "<param name=\"pwd\">12345</param>" +
                "</call>";
        Document doc = loadXml(xml);

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());

        assertThat(config.getParams().size(), is(2));
    }

    @Test
    public void should_init_assert_config_with_nested_value() throws DocumentException {
        String xml = "<assert>\n" +
                "        <status>200</status>\n" +
                "        <body>\n" +
                "            <![CDATA[\n" +
                "                            {\"orderNo\":\"123456\",\"status\":true}\n" +
                "                    ]]>\n" +
                "        </body>\n" +
                "    </assert>";
        Document doc = loadXml(xml);

        AssertConfig config = ConfigUtils.init(AssertConfig.class, doc.getRootElement());
        assertThat(config.getParams().size(), is(2));
        assertThat(config.getParams().get(0).getName(), is("status"));
    }

    @Test
    public void should_init_assert_config() throws DocumentException {
        String xml = "<assert>\n" +
                "        <status value=\"200\"/>\n" +
                "        <body>OK</body>\n" +
                "        <exception class=\"java.lang.IllegalArgumentException\"/>" +
                "    </assert>";
        Document doc = loadXml(xml);

        AssertConfig config = ConfigUtils.init(AssertConfig.class, doc.getRootElement());
        assertThat(config.getParams().size(), is(3));
        assertThat(config.getParams().get(0).getName(), is("status"));
        assertThat(config.getParams().get(2).getName(), is("class"));
    }

    @Test
    public void should_init_mock_config() throws DocumentException {
        String xml = "<mock source=\"192.168.29.117\" target=\"HOTEL_OAS\" service=\"/login\">\n" +
                "            <return>\n" +
                "                <status>200</status>\n" +
                "                <headers>\n" +
                "                    <param Content-Type=\"text/html\" />\n" +
                "                </headers>\n" +
                "                <body>test</body>\n" +
                "                <callback>\n" +
                "                    <url>http://www.qunar.com</url>\n" +
                "                    <headers>\n" +
                "                        <param Cookie=\"this is cookies\"/>\n" +
                "                    </headers>\n" +
                "                    <method>post</method>\n" +
                "                    <body>\n" +
                "                        <![CDATA[\n" +
                "                        a=b&b=c\n" +
                "                        ]]>\n" +
                "                    </body>\n" +
                "                </callback>\n" +
                "            </return>\n" +
                "        </mock>";

        Document doc = loadXml(xml);

        MockStepConfig mock = ConfigUtils.init(MockStepConfig.class, doc.getRootElement());

        Object[] parameters = BeanUtils.getParameters(mock.getParams(), new Class[]{HttpExpectation.class});

        assertNotNull(parameters);
        assertThat(parameters.length, is(1));
        HttpExpectation expectation = (HttpExpectation) parameters[0];
        assertThat(expectation.getCallback().getUrl(), is("http://www.qunar.com"));
    }

    @Test
    public void should_init_rpc_expectation() throws DocumentException {
        String xml = "<mock source=\"192.168.29.117\" target=\"HOTEL_OAS\" service=\"/login\">\n" +
                "            <return>test</return>\n" +
                "        </mock>";

        Document doc = loadXml(xml);

        MockStepConfig mock = ConfigUtils.init(MockStepConfig.class, doc.getRootElement());
    }

    @Test
    public void should_init_list_parameters() throws DocumentException, NoSuchMethodException {
        String xml = "<call service=\"test\">" +
                "<list><Address><param city=\"beijing\" /></Address>" +
                "<Address><param city=\"wuhan\" /></Address></list>" +
                "</call>";

        Document doc = loadXml(xml);

        AssertConfig config = ConfigUtils.init(AssertConfig.class, doc.getRootElement());

        Method method = TestMethods.class.getDeclaredMethod("test1", new Class[]{List.class});
        Object[] parameters = BeanUtils.getParameters(config.getParams(), method.getGenericParameterTypes());
        assertThat(parameters.length, is(1));
        List<Address> param = (List<Address>) parameters[0];
        assertThat(param.size(), is(2));
        assertThat(param.get(0), Matchers.<Object>instanceOf(Address.class));
    }

    @Test
    public void should_init_list_parameters_from_json() throws DocumentException, NoSuchMethodException {
        String xml = "<call service=\"test\">" +
                "<Addresses>[{\"@type\":\"com.qunar.base.qunit.util.Address\",\"city\":\"beijing\"},{\"@type\":\"com.qunar.base.qunit.util.Address\",\"city\":\"wuhan\"}]</Addresses>" +
                "</call>";

        Document doc = loadXml(xml);
        AssertConfig config = ConfigUtils.init(AssertConfig.class, doc.getRootElement());
        Method method = TestMethods.class.getDeclaredMethod("test1", new Class[]{List.class});
        Object[] parameters = BeanUtils.getParameters(config.getParams(), method.getGenericParameterTypes());
        assertThat(parameters.length, is(1));
        List<Address> addresses = (List<Address>) parameters[0];
        assertThat(addresses.size(), is(2));
        assertThat(addresses.get(0), Matchers.<Object>instanceOf(Address.class));
        String s = JSON.toJSONString(addresses, SerializerFeature.WriteClassName);
        System.out.println(s);
    }

    @Test
    public void should_init_set_parameter() throws DocumentException, NoSuchMethodException {
        String xml = "<call service=\"test\">" +
                "<param name=\"ids\">[1,2,3]</param>" +
                "</call>";
        Document doc = loadXml(xml);

        AssertConfig config = ConfigUtils.init(AssertConfig.class, doc.getRootElement());
        Method method = TestMethods.class.getDeclaredMethod("test2", new Class[]{Set.class});

        Object[] parameters = BeanUtils.getParameters(config.getParams(), method.getGenericParameterTypes());
        assertThat(parameters.length, is(1));
        assertThat(parameters[0], instanceOf(Set.class));
    }

    @Test
    public void should_extract_value_from_preResult() throws DocumentException {
        String xml = "<mock source=\"192.168.29.117\" target=\"HOTEL_OAS\" service=\"/login\">\n" +
                "            <return><![CDATA[$result]]></return>\n" +
                "        </mock>";

        Document document = loadXml(xml);
        MockStepConfig config = ConfigUtils.init(MockStepConfig.class, document.getRootElement());

        Response preResult = new Response("test", null);
        List<KeyValueStore> list = ParameterUtils.prepareParameters(config.getParams(), preResult, null);

        System.out.println(list);
    }

    private Document loadXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        StringReader stream = new StringReader(xml);
        return reader.read(stream);
    }
}
