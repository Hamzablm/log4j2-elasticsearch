package org.appenders.log4j2.elasticsearch.bulkprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.appenders.log4j2.elasticsearch.ClientProvider;
import org.appenders.log4j2.elasticsearch.IndexTemplate;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateRequest;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static org.appenders.log4j2.elasticsearch.bulkprocessor.BulkProcessorObjectFactoryTest.createTestObjectFactoryBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.dom.*"})
@PrepareForTest(TransportClient.class)
@RunWith(PowerMockRunner.class)
public class BulkProcessorObjectFactoryPowerMockTest {

    @Test
    public void passesIndexTemplateToClient() throws IOException {

        //given
        BulkProcessorObjectFactory factory = spy(createTestObjectFactoryBuilder().build());

        IndicesAdminClient indicesAdminClient = mockedIndicesAdminClient(factory);

        IndexTemplate indexTemplate = spy(IndexTemplate.newBuilder()
                .withPath("classpath:indexTemplate.json")
                .withName("testName")
                .build());

        String expectedPayload = indexTemplate.getSource().replaceAll("\\s+","");

        // when
        factory.execute(indexTemplate);

        // then
        ArgumentCaptor<PutIndexTemplateRequest> requestArgumentCaptor = ArgumentCaptor.forClass(PutIndexTemplateRequest.class);
        verify(indicesAdminClient).putTemplate(requestArgumentCaptor.capture(), any(ActionListener.class));

        String actualPayload = extractPayload(requestArgumentCaptor.getValue());

        Assert.assertTrue(actualPayload.contains(new ObjectMapper().readTree(expectedPayload).get("mappings").toString()));

    }

    private IndicesAdminClient mockedIndicesAdminClient(BulkProcessorObjectFactory factory) {
        ClientProvider clientProvider = mock(ClientProvider.class);
        when(factory.getClientProvider()).thenReturn(clientProvider);

        TransportClient transportClient = PowerMockito.mock(TransportClient.class);
        when(clientProvider.createClient()).thenReturn(transportClient);

        AdminClient adminClient = mock(AdminClient.class);
        when(transportClient.admin()).thenReturn(adminClient);

        IndicesAdminClient indicesAdminClient = mock(IndicesAdminClient.class);
        when(adminClient.indices()).thenReturn(indicesAdminClient);

        return indicesAdminClient;
    }

    private String extractPayload(PutIndexTemplateRequest putIndexTemplateRequest) throws IOException {
        BytesStreamOutput out = new BytesStreamOutput();
        putIndexTemplateRequest.writeTo(out);
        return new String(out.bytes().toBytesRef().bytes);
    }

}
