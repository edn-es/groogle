package es.edn.groogle.core


import com.google.auth.Credentials
import com.google.auth.oauth2.ImpersonatedCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import es.edn.groogle.Groogle
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode

@CompileStatic
class ServiceCredentials extends GroogleCredentials implements Groogle.WithServiceCredentials {

    String serviceAccountUser
    @Override
    Groogle.WithCredentials accountUser(String user) {
        this.serviceAccountUser = user
        this
    }

    String serviceAccountId
    @Override
    Groogle.WithCredentials accountId(String id) {
        this.serviceAccountId = id
        this
    }


    @CompileStatic(TypeCheckingMode.SKIP)
    protected Credentials loginImpl() {

        credentials = clientSecret ?
                ServiceAccountCredentials.fromStream(clientSecret) : ServiceAccountCredentials.applicationDefault

        if( serviceAccountUser || serviceAccountId ){
            credentials = ImpersonatedCredentials.newBuilder()
                    .setScopes(scopes)
                    .setSourceCredentials(credentials)
                    .setTargetPrincipal("jorge.aguilera@puravida-software.com")
                    //.setDelegates(["jorge.aguilera@puravida-groogle-267017.iam.gserviceaccount.com"])
                    .setLifetime(300)
                    .build()
            credentials.refreshAccessToken()
        }else {
            credentials = credentials.createScoped(scopes) as ServiceAccountCredentials
        }
        credentials
    }
}
