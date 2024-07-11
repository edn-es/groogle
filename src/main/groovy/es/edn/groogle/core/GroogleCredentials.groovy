/*
 * Groogle, a Groovy Google DSL
 *
 * @author Jorge Aguilera <jorge@edn.es>
 *
 * Copyright (c) 2024.
 *
 */

package es.edn.groogle.core


import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.auth.Credentials
import es.edn.groogle.Groogle
import groovy.transform.CompileStatic

@CompileStatic
abstract class GroogleCredentials implements Groogle.WithCredentials{

    protected String applicationName

    @Override
    Groogle.WithCredentials applicationName(String name) {
        this.applicationName = name
        this
    }

    protected List<String> scopes = []

    @Override
    Groogle.WithCredentials scopes(String... scope) {
        scopes.clear()
        scopes.addAll(scope)
        this
    }

    @Override
    Groogle.WithCredentials scopes(List<String> scope) {
        scopes.clear()
        scopes.addAll(scope)
        this
    }

    @Deprecated
    @Override
    Groogle.WithCredentials withScopes(String... scope) {
        this.scopes(scope)
    }

    @Deprecated
    @Override
    Groogle.WithCredentials withScopes(List<String> scope) {
        this.scopes(scope)
    }

    InputStream clientSecret
    @Override
    Groogle.WithCredentials usingCredentials(String fileName) {
        File f = new File(fileName);
        if (f.exists()) {
            usingCredentials(f);
        } else {
            InputStream resource = this.getClass().getResourceAsStream(fileName);
            if (resource != null) {
                usingCredentials(resource);
            } else {
                throw new RuntimeException("Credentials $fileName not found");
            }
        }
    }

    @Override
    Groogle.WithCredentials usingCredentials(File file) {
        try {
            usingCredentials(new FileInputStream(file));
        }catch (IOException io){
            throw new RuntimeException("Credentials $file.name not found");
        }
    }

    @Override
    Groogle.WithCredentials usingCredentials(InputStream inputStream) {
        this.clientSecret =inputStream
        this
    }

    JsonFactory jsonFactory;

    JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    HttpTransport httpTransport;

    HttpTransport getHttpTransport() {
        return httpTransport;
    }

    Credentials credentials

    @Override
    boolean isLogged(){
        credentials != null
    }


    protected abstract Credentials loginImpl()

    protected void login(){
        jsonFactory = GsonFactory.defaultInstance
        httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        credentials = loginImpl()
    }


}
