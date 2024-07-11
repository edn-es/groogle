/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.core;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.auth.Credentials;
import es.edn.groogle.Groogle;

public interface InternalService extends Groogle.GroogleService {
    void configure(JsonFactory jsonFactory,
                   HttpTransport httpTransport,
                   Credentials credentials,
                   String applicationName);
}
