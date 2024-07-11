/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.core


import es.edn.groogle.Groogle
import groovy.transform.CompileStatic
import groovy.transform.Synchronized

import java.util.function.Consumer
import java.util.function.Function

@CompileStatic
class GroovyGoogle implements Groogle{

    static void noOp() {}

    private static final Function<GroogleCredentials, ? extends GroogleCredentialsBuilder> factoryCredentials = {
        GroogleCredentials credentials->
            new GroogleCredentialsBuilder(credentials:credentials)
    } as Function<GroogleCredentials, ? extends GroogleCredentialsBuilder>;

    GroogleCredentials credentialsImpl

    @Override
    Groogle withOAuthCredentials(@DelegatesTo(value=WithOAuthCredentials, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        credentialsImpl = new OauthCredentials()
        Closure clone = closure.rehydrate(credentialsImpl,credentialsImpl,credentialsImpl)
        clone()
        factoryCredentials.apply(credentialsImpl).credentials
        this
    }

    @Override
    Groogle withOAuthCredentials(Consumer<WithOAuthCredentials> consumer) {
        credentialsImpl = new OauthCredentials()
        consumer.accept(credentialsImpl as WithOAuthCredentials )
        factoryCredentials.apply(credentialsImpl).credentials
        this
    }



    @Override
    Groogle withServiceCredentials(@DelegatesTo(value=WithServiceCredentials, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        credentialsImpl = new ServiceCredentials()
        Closure clone = closure.rehydrate(credentialsImpl,credentialsImpl,credentialsImpl)
        clone()
        factoryCredentials.apply(credentialsImpl).credentials
        this
    }

    @Override
    Groogle withServiceCredentials(Consumer<WithServiceCredentials> consumer) {
        credentialsImpl = new ServiceCredentials()
        consumer.accept(credentialsImpl as ServiceCredentials )
        factoryCredentials.apply(credentialsImpl).credentials
        this
    }

    @Override
    Groogle withAccessToken(@DelegatesTo(value=WithAccessTokenCredentials, strategy = Closure.DELEGATE_FIRST) Closure closure) {
        credentialsImpl = new AccessTokenCredentials()
        Closure clone = closure.rehydrate(credentialsImpl,credentialsImpl,credentialsImpl)
        clone()
        factoryCredentials.apply(credentialsImpl).credentials
        this
    }

    @Override
    Groogle withAccessToken(Consumer<WithAccessTokenCredentials> consumer) {
        credentialsImpl = new AccessTokenCredentials()
        consumer.accept(credentialsImpl as AccessTokenCredentials )
        factoryCredentials.apply(credentialsImpl).credentials
        this
    }

    @Override
    WithCredentials getCredentials() {
        credentialsImpl
    }

    Map<String,InternalService> services = [:]
    Groogle service(GroogleService service, Class<? extends GroogleService> type){
        register(service, type)
    }

    @Override
    Groogle register(GroogleService service, Class<? extends GroogleService> type) {
        assert service instanceof InternalService
        InternalService internalService = service as InternalService;
        assert services.containsKey(type.getName())==false;
        services[type.getName()]=internalService
        this
    }

    @Override
    def <T extends GroogleService> T service(Class<T> type){
        T service  =(T)services.get(type.name)
        login()
        InternalService internalService = service as InternalService;
        internalService.configure(credentialsImpl.jsonFactory, credentialsImpl.httpTransport, credentialsImpl.credentials, credentialsImpl.applicationName )
        service
    }

    private boolean logged=false

    @Synchronized
    public Groogle login() {
        if(!logged) {
            credentialsImpl.login();
            logged=true;
        }
        this
    }

}
