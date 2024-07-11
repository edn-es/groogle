/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle

import com.google.auth.oauth2.AccessToken

import java.util.function.Consumer

public interface Groogle {

    interface WithCredentials {

        public WithCredentials applicationName(String name);

        @Deprecated
        public WithCredentials withScopes(String... scope);

        @Deprecated
        public WithCredentials withScopes(List<String> scope);

        public WithCredentials scopes(String... scope);

        public WithCredentials scopes(List<String> scope);

        public WithCredentials usingCredentials(String fileName);

        public WithCredentials usingCredentials(File file);

        public WithCredentials usingCredentials(InputStream inputStream);

        public boolean isLogged();
    }

    interface WithOAuthCredentials extends WithCredentials{

        public WithCredentials storeCredentials(boolean b);
    }

    interface WithServiceCredentials extends WithCredentials{

        public WithCredentials accountUser(String user);

        public WithCredentials accountId(String id);
    }

    interface WithAccessTokenCredentials extends WithCredentials{

        public WithCredentials accessToken(AccessToken accessToken);

        public WithCredentials accessToken(String token);
    }

    public Groogle withOAuthCredentials(@DelegatesTo(WithOAuthCredentials) final Closure closure);

    public Groogle withOAuthCredentials(final Consumer<WithOAuthCredentials> consumer);

    public Groogle withServiceCredentials(@DelegatesTo(WithServiceCredentials) final Closure closure);

    public Groogle withServiceCredentials(final Consumer<WithServiceCredentials> consumer);

    public Groogle withAccessToken(@DelegatesTo(WithAccessTokenCredentials) final Closure closure);

    public Groogle withAccessToken(final Consumer<WithAccessTokenCredentials> consumer);


    public WithCredentials getCredentials();

    interface GroogleService {
    }

    public Groogle register(GroogleService service, Class<? extends GroogleService> type);

    public <T extends GroogleService> T service(Class<T> type);

    public Groogle login();
}
