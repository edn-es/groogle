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

interface Groogle {

    interface WithCredentials {

        WithCredentials applicationName(String name);

        @Deprecated
        WithCredentials withScopes(String... scope);

        @Deprecated
        WithCredentials withScopes(List<String> scope);

        WithCredentials scopes(String... scope);

        WithCredentials scopes(List<String> scope);

        WithCredentials usingCredentials(String fileName);

        WithCredentials usingCredentials(File file);

        WithCredentials usingCredentials(InputStream inputStream);

        boolean isLogged();
    }

    interface WithOAuthCredentials extends WithCredentials{

        WithCredentials storeCredentials(boolean b);
    }

    interface WithServiceCredentials extends WithCredentials{

        WithCredentials accountUser(String user);

        WithCredentials accountId(String id);
    }

    interface WithAccessTokenCredentials extends WithCredentials{

        WithCredentials accessToken(AccessToken accessToken);

        WithCredentials accessToken(String token);
    }

    Groogle withOAuthCredentials(@DelegatesTo(WithOAuthCredentials) final Closure closure);

    Groogle withOAuthCredentials(final Consumer<WithOAuthCredentials> consumer);

    Groogle withServiceCredentials(@DelegatesTo(WithServiceCredentials) final Closure closure);

    Groogle withServiceCredentials(final Consumer<WithServiceCredentials> consumer);

    Groogle withAccessToken(@DelegatesTo(WithAccessTokenCredentials) final Closure closure);

    Groogle withAccessToken(final Consumer<WithAccessTokenCredentials> consumer);


    WithCredentials getCredentials();

    interface GroogleService {
    }

    <T extends GroogleService> T service(Class<T> type);

    Groogle login();
}
