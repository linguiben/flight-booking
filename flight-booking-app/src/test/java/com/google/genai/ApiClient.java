package com.google.genai;

/**
 * @desc: TODO
 * @author: Jupiter.Lin
 * @date: 2025/7/17
 */
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.genai.errors.GenAiIOException;
import com.google.genai.types.HttpOptions;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jspecify.annotations.Nullable;

abstract class ApiClient {
    CloseableHttpClient httpClient;
    final Optional<String> apiKey;
    final Optional<String> project;
    final Optional<String> location;
    final Optional<GoogleCredentials> credentials;
    HttpOptions httpOptions;
    final boolean vertexAI;

    protected ApiClient(Optional<String> apiKey, Optional<HttpOptions> customHttpOptions) {
        Preconditions.checkNotNull(apiKey, "API Key cannot be null");
        Preconditions.checkNotNull(customHttpOptions, "customHttpOptions cannot be null");

        try {
            this.apiKey = Optional.of((String)apiKey.orElse(System.getenv("GOOGLE_API_KEY")));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("API key must either be provided or set in the environment variable GOOGLE_API_KEY.", e);
        }

        this.project = Optional.empty();
        this.location = Optional.empty();
        this.credentials = Optional.empty();
        this.vertexAI = false;
        this.httpOptions = defaultHttpOptions(false, this.location);
        if (customHttpOptions.isPresent()) {
            this.httpOptions = this.mergeHttpOptions((HttpOptions)customHttpOptions.get());
        }

        this.httpClient = this.createHttpClient(this.httpOptions.timeout());
    }

    ApiClient(Optional<String> project, Optional<String> location, Optional<GoogleCredentials> credentials, Optional<HttpOptions> customHttpOptions) {
        Preconditions.checkNotNull(project, "project cannot be null");
        Preconditions.checkNotNull(location, "location cannot be null");
        Preconditions.checkNotNull(credentials, "credentials cannot be null");
        Preconditions.checkNotNull(customHttpOptions, "customHttpOptions cannot be null");

        try {
            this.project = Optional.of((String)project.orElse(System.getenv("GOOGLE_CLOUD_PROJECT")));
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Project must either be provided or set in the environment variable GOOGLE_CLOUD_PROJECT.", e);
        }

        if (((String)this.project.get()).isEmpty()) {
            throw new IllegalArgumentException("Project must not be empty.");
        } else {
            try {
                this.location = Optional.of((String)location.orElse(System.getenv("GOOGLE_CLOUD_LOCATION")));
            } catch (NullPointerException e) {
                throw new IllegalArgumentException("Location must either be provided or set in the environment variable GOOGLE_CLOUD_LOCATION.", e);
            }

            if (((String)this.location.get()).isEmpty()) {
                throw new IllegalArgumentException("Location must not be empty.");
            } else {
                this.credentials = Optional.of((GoogleCredentials)credentials.orElseGet(() -> this.defaultCredentials()));
                this.httpOptions = defaultHttpOptions(true, this.location);
                if (customHttpOptions.isPresent()) {
                    this.httpOptions = this.mergeHttpOptions((HttpOptions)customHttpOptions.get());
                }

                this.apiKey = Optional.empty();
                this.vertexAI = true;
                this.httpClient = this.createHttpClient(this.httpOptions.timeout());
            }
        }
    }

    private CloseableHttpClient createHttpClient(Optional<Integer> timeout) {
        System.out.println("Setting proxy for HttpClient to localhost:8086");
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setProxy(new HttpHost("localhost", 8086));
        if (!timeout.isPresent()) {
            return httpClientBuilder.build();
        } else {
            RequestConfig config = RequestConfig.custom().setConnectTimeout((Integer)timeout.get()).build();
            return httpClientBuilder.setDefaultRequestConfig(config).build();
        }
    }

    public abstract ApiResponse request(String var1, String var2, String var3, Optional<HttpOptions> var4);

    public abstract ApiResponse request(String var1, String var2, byte[] var3, Optional<HttpOptions> var4);

    static String libraryVersion() {
        String libraryLabel = "google-genai-sdk/0.1.0";
        String languageLabel = "gl-java/" + System.getProperty("java.version");
        return libraryLabel + " " + languageLabel;
    }

    public boolean vertexAI() {
        return this.vertexAI;
    }

    public @Nullable String project() {
        return (String)this.project.orElse((String) null);
    }

    public @Nullable String location() {
        return (String)this.location.orElse((String) null);
    }

    public @Nullable String apiKey() {
        return (String)this.apiKey.orElse((String) null);
    }

    CloseableHttpClient httpClient() {
        return this.httpClient;
    }

    private Optional<Map<String, String>> getTimeoutHeader(HttpOptions httpOptionsToApply) {
        if (httpOptionsToApply.timeout().isPresent()) {
            int timeoutInSeconds = (int)Math.ceil((double)(Integer)httpOptionsToApply.timeout().get() / (double)1000.0F);
            return Optional.of(ImmutableMap.of("X-Server-Timeout", Integer.toString(timeoutInSeconds)));
        } else {
            return Optional.empty();
        }
    }

    HttpOptions mergeHttpOptions(HttpOptions httpOptionsToApply) {
        if (httpOptionsToApply == null) {
            return this.httpOptions;
        } else {
            HttpOptions.Builder mergedHttpOptionsBuilder = this.httpOptions.toBuilder();
            if (httpOptionsToApply.baseUrl().isPresent()) {
                mergedHttpOptionsBuilder.baseUrl((String)httpOptionsToApply.baseUrl().get());
            }

            if (httpOptionsToApply.apiVersion().isPresent()) {
                mergedHttpOptionsBuilder.apiVersion((String)httpOptionsToApply.apiVersion().get());
            }

            if (httpOptionsToApply.timeout().isPresent()) {
                mergedHttpOptionsBuilder.timeout((Integer)httpOptionsToApply.timeout().get());
            }

            if (httpOptionsToApply.headers().isPresent()) {
                Stream<Map.Entry<String, String>> headersStream = Stream.concat(Stream.concat(((Map)this.httpOptions.headers().orElse(ImmutableMap.of())).entrySet().stream(), ((Map)this.getTimeoutHeader(httpOptionsToApply).orElse(ImmutableMap.of())).entrySet().stream()), ((Map)httpOptionsToApply.headers().orElse(ImmutableMap.of())).entrySet().stream());
                Map<String, String> mergedHeaders = (Map)headersStream.collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue, (val1, val2) -> val2));
                mergedHttpOptionsBuilder.headers(mergedHeaders);
            }

            return mergedHttpOptionsBuilder.build();
        }
    }

    static HttpOptions defaultHttpOptions(boolean vertexAI, Optional<String> location) {
        ImmutableMap.Builder<String, String> defaultHeaders = ImmutableMap.builder();
        defaultHeaders.put("Content-Type", "application/json");
        defaultHeaders.put("user-agent", libraryVersion());
        defaultHeaders.put("x-goog-api-client", libraryVersion());
        HttpOptions.Builder defaultHttpOptionsBuilder = HttpOptions.builder().headers(defaultHeaders.build());
        if (vertexAI && location.isPresent()) {
            defaultHttpOptionsBuilder.baseUrl(((String)location.get()).equalsIgnoreCase("global") ? "https://aiplatform.googleapis.com" : String.format("https://%s-aiplatform.googleapis.com", location.get())).apiVersion("v1beta1");
        } else {
            if (vertexAI && !location.isPresent()) {
                throw new IllegalArgumentException("Location must be provided for Vertex AI APIs.");
            }

            defaultHttpOptionsBuilder.baseUrl("https://generativelanguage.googleapis.com").apiVersion("v1beta");
        }

        return defaultHttpOptionsBuilder.build();
    }

    GoogleCredentials defaultCredentials() {
        try {
            return GoogleCredentials.getApplicationDefault().createScoped(new String[]{"https://www.googleapis.com/auth/cloud-platform"});
        } catch (IOException e) {
            throw new GenAiIOException("Failed to get application default credentials, please explicitly provide credentials.", e);
        }
    }
}
