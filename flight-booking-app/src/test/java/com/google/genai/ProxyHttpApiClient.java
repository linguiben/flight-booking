package com.google.genai;

/**
 * @desc: TODO
 * @author: Jupiter.Lin
 * @date: 2025/7/17
 */

import com.google.auth.oauth2.GoogleCredentials;
import com.google.genai.types.HttpOptions;
import java.util.Optional;

public class ProxyHttpApiClient extends HttpApiClient {
    public ProxyHttpApiClient(Optional<String> apiKey, Optional<HttpOptions> httpOptions) {
        super(apiKey, httpOptions);
    }

    public ProxyHttpApiClient(Optional<String> project, Optional<String> location, Optional<GoogleCredentials> credentials, Optional<HttpOptions> httpOptions) {
        super(project, location, credentials, httpOptions);
    }
}
