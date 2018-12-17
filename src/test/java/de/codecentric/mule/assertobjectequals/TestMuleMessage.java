package de.codecentric.mule.assertobjectequals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.activation.DataHandler;

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.DataType;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;

@SuppressWarnings("deprecation")
public class TestMuleMessage implements MuleMessage {
    private static final long serialVersionUID = 5839164919556844196L;
    private DataType<?> payloadDataType;
    private Object payload;

    @Deprecated
    @Override
    public void addProperties(Map<String, Object> properties) {
        throw new UnsupportedOperationException("addProperties");
    }

    @Override
    public void addProperties(Map<String, Object> properties, PropertyScope scope) {
        throw new UnsupportedOperationException("addProperties");
    }

    @Deprecated
    @Override
    public void clearProperties() {
        throw new UnsupportedOperationException("clearProperties");
    }

    @Override
    public void clearProperties(PropertyScope scope) {
        throw new UnsupportedOperationException("clearProperties");
    }

    @Deprecated
    @Override
    public Object getProperty(String aKey) {
        throw new UnsupportedOperationException("getProperty");
    }

    @Deprecated
    @Override
    public void setProperty(String key, Object value) {
        throw new UnsupportedOperationException("setProperty");
    }

    @Override
    public void setInvocationProperty(String key, Object value) {
        throw new UnsupportedOperationException("setInvocationProperty");
    }

    @Override
    public void setInvocationProperty(String key, Object value, DataType<?> dataType) {
        throw new UnsupportedOperationException("setInvocationProperty");
    }

    @Override
    public void setOutboundProperty(String key, Object value) {
        throw new UnsupportedOperationException("setOutboundProperty");
    }

    @Override
    public void setOutboundProperty(String key, Object value, DataType<?> dataType) {
        throw new UnsupportedOperationException("setOutboundProperty");
    }

    @Override
    public void setProperty(String key, Object value, PropertyScope scope) {
        throw new UnsupportedOperationException("setProperty");
    }

    @Override
    public void setProperty(String key, Object value, PropertyScope scope, DataType<?> dataType) {
        throw new UnsupportedOperationException("setProperty");
    }

    @Deprecated
    @Override
    public Object removeProperty(String key) {
        throw new UnsupportedOperationException("removeProperty");
    }

    @Override
    public Object removeProperty(String key, PropertyScope scope) {
        throw new UnsupportedOperationException("removeProperty");
    }

    @Deprecated
    @Override
    public Set<String> getPropertyNames() {
        throw new UnsupportedOperationException("getPropertyNames");
    }

    @Override
    public Set<String> getPropertyNames(PropertyScope scope) {
        throw new UnsupportedOperationException("getPropertyNames");
    }

    @Override
    public Set<String> getInvocationPropertyNames() {
        throw new UnsupportedOperationException("getInvocationPropertyNames");
    }

    @Override
    public Set<String> getInboundPropertyNames() {
        throw new UnsupportedOperationException("getInboundPropertyNames");
    }

    @Override
    public Set<String> getOutboundPropertyNames() {
        throw new UnsupportedOperationException("getOutboundPropertyNames");
    }

    @Deprecated
    @Override
    public Set<String> getSessionPropertyNames() {
        throw new UnsupportedOperationException("getSessionPropertyNames");
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public String getUniqueId() {
        throw new UnsupportedOperationException("getUniqueId");
    }

    @Override
    public String getMessageRootId() {
        throw new UnsupportedOperationException("getMessageRootId");
    }

    @Override
    public void setMessageRootId(String rootId) {
        throw new UnsupportedOperationException("setMessageRootId");
    }

    @Override
    public void propagateRootId(MuleMessage aParent) {
        throw new UnsupportedOperationException("propagateRootId");
    }

    @Deprecated
    @Override
    public Object getProperty(String name, Object defaultValue) {
        throw new UnsupportedOperationException("getProperty");
    }

    @Override
    public <T> T getProperty(String name, PropertyScope scope) {
        throw new UnsupportedOperationException("getProperty");
    }

    @Override
    public <T> T getInboundProperty(String name, T defaultValue) {
        throw new UnsupportedOperationException("getInboundProperty");
    }

    @Override
    public <T> T getInboundProperty(String name) {
        throw new UnsupportedOperationException("getInboundProperty");
    }

    @Override
    public <T> T getInvocationProperty(String name, T defaultValue) {
        throw new UnsupportedOperationException("getInvocationProperty");
    }

    @Override
    public <T> T getInvocationProperty(String name) {
        throw new UnsupportedOperationException("getInvocationProperty");
    }

    @Override
    public <T> T getOutboundProperty(String name, T defaultValue) {
        throw new UnsupportedOperationException("getOutboundProperty");
    }

    @Override
    public <T> T getOutboundProperty(String name) {
        throw new UnsupportedOperationException("getOutboundProperty");
    }

    @Override
    public <T> T findPropertyInAnyScope(String name, T defaultValue) {
        throw new UnsupportedOperationException("findPropertyInAnyScope");
    }

    @Override
    public <T> T getProperty(String name, PropertyScope scope, T defaultValue) {
        throw new UnsupportedOperationException("getProperty");
    }

    @Override
    public DataType<?> getPropertyDataType(String name, PropertyScope scope) {
        throw new UnsupportedOperationException("getPropertyDataType");
    }

    @Deprecated
    @Override
    public int getIntProperty(String name, int defaultValue) {
        throw new UnsupportedOperationException("getIntProperty");
    }

    @Deprecated
    @Override
    public long getLongProperty(String name, long defaultValue) {
        throw new UnsupportedOperationException("getIntProperty");
    }

    @Deprecated
    @Override
    public double getDoubleProperty(String name, double defaultValue) {
        throw new UnsupportedOperationException("getDoubleProperty");
    }

    @Deprecated
    @Override
    public String getStringProperty(String name, String defaultValue) {
        throw new UnsupportedOperationException("getStringProperty");
    }

    @Deprecated
    @Override
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        throw new UnsupportedOperationException("getBooleanProperty");
    }

    @Deprecated
    @Override
    public void setBooleanProperty(String name, boolean value) {
        throw new UnsupportedOperationException("setBooleanProperty");
    }

    @Deprecated
    @Override
    public void setIntProperty(String name, int value) {
        throw new UnsupportedOperationException("setIntProperty");
    }

    @Deprecated
    @Override
    public void setLongProperty(String name, long value) {
        throw new UnsupportedOperationException("setLongProperty");
    }

    @Deprecated
    @Override
    public void setDoubleProperty(String name, double value) {
        throw new UnsupportedOperationException("setDoubleProperty");
    }

    @Deprecated
    @Override
    public void setStringProperty(String name, String value) {
        throw new UnsupportedOperationException("setStringProperty");
    }

    @Override
    public void setCorrelationId(String id) {
        throw new UnsupportedOperationException("setCorrelationId");
    }

    @Override
    public String getCorrelationId() {
        throw new UnsupportedOperationException("getCorrelationId");
    }

    @Override
    public int getCorrelationSequence() {
        throw new UnsupportedOperationException("getCorrelationSequence");
    }

    @Override
    public void setCorrelationSequence(int sequence) {
        throw new UnsupportedOperationException("setCorrelationSequence");
    }

    @Override
    public int getCorrelationGroupSize() {
        throw new UnsupportedOperationException("getCorrelationGroupSize");
    }

    @Override
    public void setCorrelationGroupSize(int size) {
        throw new UnsupportedOperationException("setCorrelationGroupSize");
    }

    @Override
    public void setReplyTo(Object replyTo) {
        throw new UnsupportedOperationException("setReplyTo");
    }

    @Override
    public Object getReplyTo() {
        throw new UnsupportedOperationException("getReplyTo");
    }

    @Override
    public ExceptionPayload getExceptionPayload() {
        throw new UnsupportedOperationException("getExceptionPayload");
    }

    @Override
    public void setExceptionPayload(ExceptionPayload payload) {
        throw new UnsupportedOperationException("setExceptionPayload");
    }

    @Deprecated
    @Override
    public void addAttachment(String name, DataHandler dataHandler) throws Exception {
        throw new UnsupportedOperationException("addAttachment");
    }

    @Override
    public void addOutboundAttachment(String name, DataHandler dataHandler) throws Exception {
        throw new UnsupportedOperationException("addOutboundAttachment");
    }

    @Override
    public void addOutboundAttachment(String name, Object object, String contentType) throws Exception {
        throw new UnsupportedOperationException("addOutboundAttachment");
    }

    @Deprecated
    @Override
    public void removeAttachment(String name) throws Exception {
        throw new UnsupportedOperationException("removeAttachment");
    }

    @Override
    public void removeOutboundAttachment(String name) throws Exception {
        throw new UnsupportedOperationException("removeOutboundAttachment");
    }

    @Deprecated
    @Override
    public DataHandler getAttachment(String name) {
        throw new UnsupportedOperationException("getAttachment");
    }

    @Override
    public DataHandler getInboundAttachment(String name) {
        throw new UnsupportedOperationException("getInboundAttachment");
    }

    @Override
    public DataHandler getOutboundAttachment(String name) {
        throw new UnsupportedOperationException("getOutboundAttachment");
    }

    @Deprecated
    @Override
    public Set<String> getAttachmentNames() {
        throw new UnsupportedOperationException("getAttachmentNames");
    }

    @Override
    public Set<String> getInboundAttachmentNames() {
        throw new UnsupportedOperationException("getInboundAttachmentNames");
    }

    @Override
    public Set<String> getOutboundAttachmentNames() {
        throw new UnsupportedOperationException("getOutboundAttachmentNames");
    }

    @Override
    public String getEncoding() {
        throw new UnsupportedOperationException("getEncoding");
    }

    @Override
    public void setEncoding(String encoding) {
        throw new UnsupportedOperationException("setEncoding");
    }

    @Override
    public void release() {
        throw new UnsupportedOperationException("release");
    }

    @Override
    public void applyTransformers(MuleEvent event, List<? extends Transformer> transformers) throws MuleException {
        throw new UnsupportedOperationException("applyTransformers");
    }

    @Override
    public void applyTransformers(MuleEvent event, Transformer... transformers) throws MuleException {
        throw new UnsupportedOperationException("applyTransformers");
    }

    @Override
    public void applyTransformers(MuleEvent event, List<? extends Transformer> transformers, Class<?> outputType) throws MuleException {
        throw new UnsupportedOperationException("applyTransformers");
    }

    @Override
    public void setPayload(Object payload) {
        this.payload = payload;
    }

    @Override
    public void setPayload(Object payload, DataType<?> dataType) {
        this.payload = payload;
        payloadDataType = dataType;
    }

    @Override
    public <T> T getPayload(Class<T> outputType) throws TransformerException {
        throw new UnsupportedOperationException("getPayload");
    }

    @Override
    public <T> T getPayload(DataType<T> outputType) throws TransformerException {
        throw new UnsupportedOperationException("getPayload");
    }

    @Override
    public String getPayloadAsString(String encoding) throws Exception {
        return payload == null ? "null" : payload.toString();
    }

    @Override
    public String getPayloadAsString() throws Exception {
        return payload == null ? "null" : payload.toString();
    }

    @Override
    public byte[] getPayloadAsBytes() throws Exception {
        throw new UnsupportedOperationException("getPayloadAsBytes");
    }

    @Override
    public Object getOriginalPayload() {
        throw new UnsupportedOperationException("getOriginalPayload");
    }

    @Override
    public String getPayloadForLogging() {
        return payload == null ? "null" : payload.toString();
    }

    @Override
    public String getPayloadForLogging(String encoding) {
        return payload == null ? "null" : payload.toString();
    }

    @Deprecated
    @Override
    public MuleContext getMuleContext() {
        throw new UnsupportedOperationException("getMuleContext");
    }

    @Override
    public DataType<?> getDataType() {
        return payloadDataType;
    }

    @Deprecated
    @Override
    public <T> T getSessionProperty(String name, T defaultValue) {
        throw new UnsupportedOperationException("getSessionProperty");
    }

    @Deprecated
    @Override
    public <T> T getSessionProperty(String name) {
        throw new UnsupportedOperationException("getSessionProperty");
    }

    @Deprecated
    @Override
    public void setSessionProperty(String key, Object value) {
        throw new UnsupportedOperationException("setSessionProperty");
    }

    @Override
    public MuleMessage createInboundMessage() throws Exception {
        throw new UnsupportedOperationException("createInboundMessage");
    }

    @Override
    public void clearAttachments() {
        throw new UnsupportedOperationException("clearAttachments");
    }
}
