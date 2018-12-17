package de.codecentric.mule.assertobjectequals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.config.MuleConfiguration;
import org.mule.api.el.ExpressionLanguageExtension;
import org.mule.api.expression.ExpressionManager;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.registry.MuleRegistry;
import org.mule.api.transformer.DataType;
import org.mule.el.mvel.MVELExpressionLanguage;
import org.mule.expression.DefaultExpressionManager;

public class AbstractConnectorTest {
    protected AssertObjectEqualsConnector aoec;

    @Before
    public void before() throws InitialisationException {
        aoec = new AssertObjectEqualsConnector();
        aoec.setMuleContext(createMuleContext());
    }

    @After
    public void after() {
        aoec = null;
    }

    protected MuleEvent createEvent(Object payload) {
        SimpleMock<MuleEvent> eventMock = new SimpleMock<>(MuleEvent.class);
        MuleMessage message = new TestMuleMessage();
        message.setPayload(payload, DataType.OBJECT_DATA_TYPE);
        eventMock.storeResult(message, "getMessage");
        return eventMock.getMockObject();
    }

    protected MuleContext createMuleContext() throws InitialisationException {
        SimpleMock<MuleContext> contextMock = new SimpleMock<>(MuleContext.class);
        contextMock.storeResult(createRegistry(), "getRegistry");
        contextMock.storeResult(createConfiguration(), "getConfiguration");
        contextMock.storeResult(createExpressionManager(contextMock.getMockObject()), "getExpressionManager");
        return contextMock.getMockObject();
    }

    protected MuleRegistry createRegistry() {
        SimpleMock<MuleRegistry> registryMock = new SimpleMock<>(MuleRegistry.class);
        registryMock.storeResult(new ArrayList<Object>(), "lookupObjectsForLifecycle", ExpressionLanguageExtension.class);
        return registryMock.getMockObject();
    }

    protected ExpressionManager createExpressionManager(MuleContext muleContext) throws InitialisationException {
        DefaultExpressionManager manager = new DefaultExpressionManager();
        MVELExpressionLanguage expressionLanguage = new MVELExpressionLanguage(muleContext);
        expressionLanguage.initialise();
        manager.setExpressionLanguage(expressionLanguage);
        return manager;
    }

    protected MuleConfiguration createConfiguration() {
        SimpleMock<MuleConfiguration> configurationMock = new SimpleMock<>(MuleConfiguration.class);
        configurationMock.storeResult("UTF-8", "getDefaultEncoding");
        return configurationMock.getMockObject();
    }

    protected InputStream string2Stream(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        return new ByteArrayInputStream(bytes);
    }

    protected String stream2String(InputStream is) throws IOException {
        byte[] bytes = IOUtils.toByteArray(is);
        is.close();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
