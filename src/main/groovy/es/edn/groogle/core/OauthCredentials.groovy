/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.core

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.util.store.AbstractDataStoreFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.google.auth.Credentials
import com.google.auth.oauth2.UserCredentials
import es.edn.groogle.Groogle
import groovy.transform.CompileStatic

@CompileStatic
class OauthCredentials extends GroogleCredentials implements Groogle.WithOAuthCredentials{


    protected boolean storeCredentials=false

    @Override
    Groogle.WithCredentials storeCredentials(boolean b) {
        this.storeCredentials = b
        this
    }

    protected File getDataStoreDir(){
        return new File(System.getProperty("user.home"), ".credentials/$applicationName")
    }

    protected AbstractDataStoreFactory getDataStoreFactory() {
        return new FileDataStoreFactory(getDataStoreDir());
    }

    protected Credentials loginImpl() {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(clientSecret))
        String clientId = clientSecrets.getDetails().getClientId()
        String clientSecret = clientSecrets.getDetails().getClientSecret()

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        httpTransport,
                        jsonFactory,
                        clientSecrets,
                        scopes)
                        .setDataStoreFactory(storeCredentials == false ? new MemoryDataStoreFactory() : dataStoreFactory)
                        .setAccessType("offline")
                        .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(credential.getRefreshToken())
                .build();

        credentials
    }

}
