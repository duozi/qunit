package com.qunar.base.qunit.util;

import com.qunar.base.qunit.model.*;
import com.qunar.base.qunit.objectfactory.BeanUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BeanUtilsTest {
    @Test
    public void should_create_Integer_type() {
        Integer result = BeanUtils.create(Integer.class, "5");

        assertThat(result, is(5));
    }

    @Test
    public void should_create_user_define_type() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("city", "beijing");

        Address address = BeanUtils.create(Address.class, properties);

        assertThat(address.getCity(), is("beijing"));
    }

    @Test
    public void should_create_user_define_type_with_pri_type() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("id", "5");
        properties.put("name", "qunar");

        Group group = BeanUtils.create(Group.class, properties);

        assertThat(group.getId(), is(5));
        assertThat(group.getName(), is("qunar"));
    }

    @Test
    public void should_create_nested_user_defined_type() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("userName", "yuyijq");
        Map<String, String> addressProperties = new HashMap<String, String>();
        addressProperties.put("city", "beijing");
        properties.put("address", addressProperties);

        User user = BeanUtils.create(User.class, properties);

        assertThat(user.getUserName(), is("yuyijq"));
        assertThat(user.getAddress().getCity(), is("beijing"));
    }

    @Test
    public void should_create_partial_of_user_defined_type() {
        HashMap<String, Integer> properties = new HashMap<String, Integer>();
        properties.put("id", 5);

        Group group = BeanUtils.create(Group.class, properties);

        assertThat(group.getId(), is(5));
        assertNull(group.getName());
    }

    @Test
    public void should_create_user_defined_type_with_array_type() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("seqs", "a,b,c");

        Hotel hotel = BeanUtils.create(Hotel.class, properties);

        assertThat(hotel.getSeqs().length, is(3));
    }

    @Test
    public void should_create_null_string() {
        String s = BeanUtils.create(String.class, null);

        assertNull(s);
    }

    @Test
    public void should_create_object_with_map_properties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("seqs", "1,2,3,4");
        Map<String, String> attr = new HashMap<String, String>();
        attr.put("name", "qitian");
        attr.put("seq", "2");
        properties.put("attr", attr);

        Hotel hotel = BeanUtils.create(Hotel.class, properties);

        assertThat(hotel.getAttr().get("name"), is("qitian"));
    }

    @Test
    public void should_create_object_by_xml() throws DocumentException {
        Document doc = loadXml(" <call service=\"Rpc_FeedService_saveOrUpdateFeedContent\">\n" +
                "                            <FeedContent>\n" +
                "                                     <param\n" +
                "                                               uid=\"1001\"\n" +
                "                                               toUid=\"1002\"\n" +
                "                                               feedOid=\"oid001\"\n" +
                "                                               content=\"testContent\"\n" +
                "                                               feedType=\"1\"\n" +
                "                                               originType=\"1\"\n" +
                "                                               feedTime=\"2012-01-01 01:01:01\"\n" +
                "                                               feedIp=\"1234567890\"\n" +
                "                                     />\n" +
                "                            </FeedContent>\n" +
                "                   </call>");

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());

        FeedContent content = BeanUtils.create(FeedContent.class, config.getParams().get(0).getValue());
        assertThat(content.getContent(), is("testContent"));
        assertNotNull(content.getFeedTime());
    }

    @Test
    public void should_create_object_with_map() throws DocumentException {
        Document doc = loadXml(" <call service=\"Rpc_FeedService_saveOrUpdateFeedContent\">\n" +
                "                            <FeedContent>\n" +
                "                                     <param\n" +
                "                                               uid=\"1001\"\n" +
                "                                               toUid=\"1002\"\n" +
                "                                               feedOid=\"oid001\"\n" +
                "                                               content=\"testContent\"\n" +
                "                                               feedType=\"1\"\n" +
                "                                               originType=\"1\"\n" +
                "                                               feedTime=\"2012-01-01 01:01:01\"\n" +
                "                                               feedIp=\"1234567890\"\n" +
                "                                     />\n" +
                "                            </FeedContent>\n" +
                "                   </call>");

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());
        Object[] parameters = BeanUtils.getParameters(config.getParams(), new Class[]{HashMap.class});

        assertThat(parameters.length, is(1));
        assertThat((String) ((Map) parameters[0]).get("uid"), is("1001"));
    }

    @Test
    public void should_create_object_by_pass_into_json() throws DocumentException {
        Document doc = loadXml("<call service=\"test\">\n" +
                "<param name=\"user\">\n" +
                "\t{\"userName\":\"yuyijq\"}\n" +
                "</param>\n" +
                "</call>");

        TestElementConfig config = ConfigUtils.init(TestElementConfig.class, doc.getRootElement());
        Object[] parameters = BeanUtils.getParameters(config.getParams(), new Class[]{User.class});

        assertThat(parameters.length, is(1));
        assertThat(((User) parameters[0]).getUserName(), is("yuyijq"));
    }

    private Document loadXml(String xml) throws DocumentException {
        SAXReader reader = new SAXReader();
        StringReader stream = new StringReader(xml);
        return reader.read(stream);
    }

    @Test
    public void should_create_object_with_parameters_ctor() {
        Map properties = new HashMap();
        properties.put("pCookie", "p");
        properties.put("vCookie", "v");
        properties.put("qCookie", "q");
        UserCookies userCookies = BeanUtils.create(UserCookies.class, properties);

        assertThat(userCookies.getpCookie(), is("p"));
        assertThat(userCookies.getvCookie(), is("v"));
        assertThat(userCookies.getqCookie(), is("q"));
    }

}
