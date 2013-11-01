package com.qunar.base.qunit.transport.command;

import com.qunar.base.qunit.exception.CommandNotFoundException;
import com.qunar.base.qunit.extension.ExtensionLoader;
import com.qunar.base.qunit.reporter.Reporter;
import com.qunar.base.qunit.transport.config.HttpServiceConfig;
import com.qunar.base.qunit.transport.config.LocalServiceConfig;
import com.qunar.base.qunit.transport.config.SHttpServiceConfig;
import com.qunar.base.qunit.transport.config.ServiceConfig;
import com.qunar.base.qunit.util.ConfigUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

public class ServiceFactory {
    private static Logger logger = LoggerFactory.getLogger(ServiceFactory.class);

    private static final Map<String, Class<? extends ServiceConfig>> CONFIG = new HashMap<String, Class<? extends ServiceConfig>>();

    private static Map<String, ExecuteCommand> COMMAND_CACHE = new HashMap<String, ExecuteCommand>();

    private static Set<String> PARSED_FILES = new HashSet<String>();

    static {
        CONFIG.put(HttpServiceConfig.name, HttpServiceConfig.class);
        CONFIG.put(SHttpServiceConfig.name, SHttpServiceConfig.class);
        CONFIG.put(LocalServiceConfig.name, LocalServiceConfig.class);
        loadAllConfig();
    }

    static void loadAllConfig() {
        try {
            Map<String, Class<? extends ServiceConfig>> map = ExtensionLoader.loadExtension(ServiceConfig.class);
            CONFIG.putAll(map);
        } catch (Exception e) {
            logger.error("can not load extensions for {}", ServiceConfig.class.getName());
        }
    }

    public void init(String[] fileNames, Reporter reporter) {
        for (String fileName : fileNames) {
            init(fileName, reporter);
        }
        init("serviceInner.xml", reporter);
    }

    private void init(String fileName, Reporter reporter) {
        try {
            URL url = this.getClass().getClassLoader().getResource(fileName);
            if (url == null) {
                throw new RuntimeException(String.format("服务配置文件不存在,file=<%s>", fileName));
            }
            String path = url.getPath();
            if (PARSED_FILES.contains(path)) {
                return;
            }
            PARSED_FILES.add(path);
            Document document = load(path);
            initCommands(document, reporter);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("服务配置文件不存在,file=<%s>", fileName), e);
        } catch (DocumentException e) {
            throw new RuntimeException(String.format("服务配置文件格式错误，是非法的xml文档,file=<%s>", fileName), e);
        }
    }

    private void initCommands(Document document, Reporter reporter) {
        Iterator serviceElements = document.getRootElement().elementIterator();
        while (serviceElements.hasNext()) {
            Element serviceElement = (Element) serviceElements.next();
            ExecuteCommand command = getCommand(serviceElement);
            if (COMMAND_CACHE.containsKey(command.getId())) {
                throw new RuntimeException(String.format("id为%s的接口存在重复", command.getId()));
            }
            COMMAND_CACHE.put(command.getId(), command);
            reporter.addService(command.desc());
        }
    }

    private ExecuteCommand getCommand(Element element) {
        Class<? extends ServiceConfig> clazz = CONFIG.get(element.getName());
        if (clazz == null) {
            throw new CommandNotFoundException(String.format("未找到给定的服务配置节的解析器:%s", element.getName()));
        }
        try {
            ServiceConfig serviceConfig = ConfigUtils.init(clazz, element);
            return serviceConfig.createCommand();
        } catch (Exception e) {
            throw new RuntimeException("创建服务命令出现异常", e);
        }
    }

    public final ExecuteCommand getCommand(String id) {
        if (!COMMAND_CACHE.containsKey(id)) {
            throw new CommandNotFoundException(String.format("在service配置文件里没有配置id为[%s]的服务", id));
        }
        return COMMAND_CACHE.get(id);
    }

    private Document load(String fileName) throws FileNotFoundException, DocumentException {
        SAXReader reader = new SAXReader();
        return reader.read(new FileInputStream(fileName));
    }

    static ServiceFactory factory = new ServiceFactory();

    public static ServiceFactory getInstance() {
        return factory;
    }

}
