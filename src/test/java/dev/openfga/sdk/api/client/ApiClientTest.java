package dev.openfga.sdk.api.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.net.http.HttpClient;
import org.junit.jupiter.api.Test;

class ApiClientTest {

    @Test
    public void returnSameHttpClient() {
        ApiClient apiClient = new ApiClient();
        assertEquals(apiClient.getHttpClient(), apiClient.getHttpClient());
    }

    @Test
    public void newHttpClientWhenBuilderModified() {
        ApiClient apiClient = new ApiClient();

        HttpClient client1 = apiClient.getHttpClient();
        apiClient.setHttpClientBuilder(HttpClient.newBuilder());

        assertNotEquals(client1, apiClient.getHttpClient());
    }

    @Test
    public void httpClientShouldUseHttp1ByDefault() {
        ApiClient apiClient = new ApiClient();
        assertEquals(apiClient.getHttpClient().version(), HttpClient.Version.HTTP_1_1);
    }

    @Test
    public void customHttpClientWithHttp2() {
        HttpClient.Builder builder = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2);
        ApiClient apiClient = new ApiClient(builder);
        ;
        assertEquals(apiClient.getHttpClient().version(), HttpClient.Version.HTTP_2);
    }
}
