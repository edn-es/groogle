package es.edn.groogle.core

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.auth.Credentials
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.UserCredentials
import es.edn.groogle.Groogle
import groovy.transform.CompileStatic

@CompileStatic
class AccessTokenCredentials extends GroogleCredentials implements Groogle.WithAccessTokenCredentials{

    private AccessToken accessToken

    private String access_token

    @Override
    Groogle.WithCredentials accessToken(AccessToken accessToken) {
        this.accessToken = accessToken
        return this
    }

    @Override
    Groogle.WithCredentials accessToken(String token) {
        this.access_token = token
        return this
    }


    protected Credentials loginImpl() {
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(clientSecret))
        String clientId = clientSecrets.getDetails().getClientId()
        String clientSecret = clientSecrets.getDetails().getClientSecret()

        credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setAccessToken(accessToken ?: new AccessToken(access_token, null))
                .build();

        credentials
    }

}
