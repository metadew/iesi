**Iesi-Rest without microservices**

Instructions for using Iesi-Rest without microservices.

**1. 1 Launch Iesi-Rest as an HTTP application**

Go to &quot;application.yml&quot; into directory &quot;src/main/ressources&quot;. Comment lines 5 to 14.

Got to the file ResourceServerConfiguration.java&quot; into directory &quot;src/main/java/iesi/server/ rest /configuration&quot;. Comment lines &quot;47&quot; and in file &quot;WebSecurityConfiguration.java&quot; into the same directory, comment lines 73 to 75.

Then follows instruction on section 1.5 to configure Oauth2.

**1.2 Launch Iesi-Rest as an HTTPS application: Generate a self-signed SSL certificate for enabling https**

You can use either of the following certificate formats:

1- PKCS12: Public Key Cryptographic Standards is a password protected format that can contain multiple certificates and keys; it’s an industry-wide used format.
2-JKS: Java KeyStore is similar to PKCS12; it’s a proprietary format and is limited to the Java environment.

You can use either of keytool, shipped with Java Runtime Environment, or OpenSSL, which can be downloaded from [here](https://www.openssl.org/), to generate the certificates from the command line.

It is recommended to use the PKCS12 format which is an industry standard format.

**1.3 Generating a Keystore**

In this example, we will use keytool.

To generate a PKCS12 keystore format, write in the command line:

    keytool -genkeypair -alias iesi -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore iesi.p12 -validity 3650

Copy the file named &quot;iesi.p12&quot; generated from the previous step into the &quot;src/main/resource&quot; directory.

In the file &quot;_application.yml_&quot; in the same directory, you can configure the SSL related properties:

                    _# The format used for the keystore. It could be set to P12 in case it is a P12 file_

                    server.ssl.key-store-type=PKCS12

                    _# The path to the keystore containing the certificate_

                    server.ssl.key-store=classpath:iesi.p12

                    _# The password used to generate the certificate_

                    server.ssl.key-store-password=password

                    _# The alias mapped to the certificate_

                    server.ssl.key-alias=iesi

For more information, check this link: [HTTPS using Self-Signed Certificate in Spring Boot](https://www.baeldung.com/spring-boot-https-self-signed-certificate)

**1.4 Spring Security with Oauth2**

 To secure your application with Oauth2, you must, first, generate an asymmetric key (Public and Private keys) to do the signing process.

With Oauth2, the resource owner provides the client with a username and password. The client then sends a token request to the authorization server by providing the credential information. The authorization server authorizes the client and returns with an access token. On every subsequent request, the server validates the client token.

                      **Explanation of Oauth2 Configuration**

                      **AuthorizationServerConfiguration.java:**

                      Validate client and user credentials, generate JWT.

                      Bean JwtAccessTokenConverter sign the JWT using SSL, Bean TokenStore reads the data from the tokens, and DefaultTokenServices persists the tokens.

                      SSL are configured in applications.yml via SecurityPropertiesClient binding it to the properties.

                      **ResourceServerConfiguration.java**

                      By convention, both Auth and Resource Servers run on different applications for handling Oauth2; here both run on one app, for the reason that the configuration is simpler. The Resource Server use the public key form the SSL to decode JWT token via SecurityPropertiesClient (check the iesi-rest-gateway where the class is bind to application.yml).

                      The public key is used by the JwtAccessTokenConverter, the JwtTokenStore uses it for reading the tokens and the Default TokenServices persist the tokens.

                      **WebSecurityConfiguration.java**

                      Provide web based security.

                      The Datasource provided by Spring Boot is auto-configured and is just add to the class. Needs to be injected to the UserDetailsService allowing authentication and retrieving users. The AuthenticationManager, since it is required by some autoconfigured Spring Bean, is override.

                      **Schema.sql:**

                      Default Oauth2 SQL for Spring Security.

                      **Data.sql:**

                      Specify client-id, secret… for users registered in H2 database.

                      _Documentation:_ [_https://projects.spring.io/spring-security-oauth/docs/oauth2.html_](https://projects.spring.io/spring-security-oauth/docs/oauth2.html)

**1.5 Generate a PKCS12 file**

Generate a .p12 file using the command line tool keytool:

                              keytool -genkeypair -alias iesi

                                                  -keyalg RSA

                                                  -keypass password

                                                  -keystore iesi.jks

                                                  -storepass password

Then, to migrate to PKCS12:

              keytool -importkeystore -srckeystore iesi.jks -destkeystore iesi.jks -deststoretype pkcs12

This command will generate a file called iesi.p12 containing public and private keys.

To export your Public key from generated PKCS, use the following command:

              keytool -list -rfc --keystore iesi.p12 | openssl x509 -inform pem -pubkey

If you use the microservice Iesi-Rest-Gateway, copy paste your public key to this service (cf. section 2.6).

Copy the file named &quot;iesi.p12&quot; generated from the previous step into the &quot;src/main/resource&quot; directory.

In the file &quot;_application.yml_&quot; in the same directory, you can configure the PKCS related properties:

                                          security:
                                                   jwt:
                                                      key-store: classpath:iesi.p12
                                                      key-store-password: password
                                                      key-store-type: PKCS12
                                                      key-pair-alias: iesi
                                                      key-pair-password: password


For more information about Oauth2 configuration, check this link: [Using JWT with Spring Security OAuth](https://www.baeldung.com/spring-security-oauth-jwt)

**1.6 Launch Iesi-Rest**

The application has https port 8080 as default (you can change it on file application.yml).

**1.7 Generating an Access Token**

Before generating an Access Token, you must add the Oauth2 specification, a flexible authorization framework describing a number of grants (&quot;methods&quot;) for a client application to acquire an access token (which represents a user&#39;s permission for the client to access their data) which can be used to authenticate a request to an API endpoint.

Into the &quot;src/main/resource/&quot; directory, add to the &quot;data.sql&quot; file your grants for acquiring an access token. Under the Sql table VALUES:

                                      _ # The application&#39;s_ _client ID_ _(how the API identifies the application)_

                                      &#39;client-id&#39;,

                                      _#Your client-secret must be encrypt using BCrypt with 10 rounds(click on this link            (https://www.browserling.com/tools/bcrypt) to generate your encrypt password. 
                                      In this example the client-secret is &quot;secret&quot;):_

                                      &#39;$2a$10$vCXMWCn7fDZWOcLnIEhmK.74dvK1Eh8ae2WrWlhr2ETPLoxQctN4.&#39;,

                                      _#The scope, specifying the level of access that the application is requesting_

                                      &#39;openid&#39;,

                                      _#The Authorization grant_

                                      &#39;authorization\_code,check\_token,refresh\_token,password&#39;,

                                      _#The redirect uri_

                                      https://localhost:8080,

                                      _#The Authorities_

                                      &#39;ROLE\_CLIENT&#39;,

                                      _# The validity of the access token in seconds_

                                      2500,

                                      _#The validity of the refresh token in seconds._

                                      2500

You can generate your access token via curl or Postman. For Postman, click [here.](https://learning.getpostman.com/docs/postman/sending_api_requests/authorization/)

Via curl, to get an Access Token send a _POST_ to the &quot;api_/oauth/token_&quot; endpoint, without forgetting to add your grants define in the previous step:

curl -k -u client-id:secret -X POST https://localhost:8080/api/oauth/token\?grant\_type=password\&amp;username=admin\&amp;password=password

 The server will respond with a token, e.g:
 
"access_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjAyNjU0NDksInVzZXJfbmFtZSI6InJvb3QiLCJhdXRob3JpdGllcyI6WyJBVVRIT1JJWkVEX1VTRVIiXSwianRpIjoiYmYyNTA0ZWMtNDlhMi00NTY4LTgwY2YtNDJhZGRiMjgwMDEzIiwiY2xpZW50X2lkIjoiY2xpZW50LWlkIiwic2NvcGUiOlsib3BlbmlkIl19.h5Pqzc_WKJFQXmfvF609O-MOAU5hRf0H4A1rKqQzxUuVGIYl4u8ibPFBgTu7vbKzf316ZF7Ky6g0oAohrH3FhJWD1LVlevb-5bb-zLz8v2tgrgUZQ9KmmxnMzAtKbZnRJi2sK8JkaYZCdaQsr7Y2npazJWhnBkLwgedM0Pj22rJwfqJ2GoTIgzcMzyi_LYVTlf2JcBLIgstBKJJwSKMpXVjL9FYEBeBgVhmdViaB8yRH_-ewzBg-t6cI4bDFnIeauG-eTJFkqo-JJ6FsPNvX2iSDOOVDFqnzZVHJghUia7xwDBZvxgxoMQ3BqtT2oo7L2S4W4jAy5fkE796SsPHj1A","token_type":"bearer","refresh_token":"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJyb290Iiwic2NvcGUiOlsib3BlbmlkIl0sImF0aSI6ImJmMjUwNGVjLTQ5YTItNDU2OC04MGNmLTQyYWRkYjI4MDAxMyIsImV4cCI6MTU2MDUxMjk0OSwiYXV0aG9yaXRpZXMiOlsiQVVUSE9SSVpFRF9VU0VSIl0sImp0aSI6Ijg1ZDU0OTNjLWM2NmItNGYzZi1iNjliLTgyZDVkOTg4NDU0NiIsImNsaWVudF9pZCI6ImNsaWVudC1pZCJ9.kNwW4mf5epN8AMW9pGmsqnlKf8foGkGspq0hjek2V1biQmWrblKGTylMRJfW0Nre-4poIsjWzG1Ph5aaHYPfwKaVPG8Pm5f3kFBsphClmZZ_m5duAIvObh3izbmT9Veym9H9Jz6bVPQShSg4PPxOycU7briYe_EATsJVF-pqjxft3hfWprdd_5pdnqQy9arNUi0j5OFVYvrgcEkvAlD8K1BLbYwNr58CD-pPqkuLS-6gDhmdqbnDxlUq64HeuLICy_pJXpI8i6pIv-KVUZIfnFULQC8W1rJWqb85xaN6y2lrOYedLpqImD-P8OSLP1o1eaQ9_BRVRE40VG65s5laPQ","expires_in":2499,"scope":"openid","jti":"bf2504ec-49a2-4568-80cf-42addb280013"

Then, to access specific endpoints:

curl --insecure -H "Authorization: Token eyJhbGciOiJSU-zI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE1NjAyNjU0NDksInVzZXJfbmFtZSI6InJvb3QiLCJhdXRob3JpdGllcyI6WyJBVVRIT1JJWkVEX1VTRVIiXSwianRpIjoiYmYyNTA0ZWMtNDlhMi00NTY4LTgwY2YtNDJhZGRiMjgwMDEzIiwiY2xpZW50X2lkIjoiY2xpZW50LWlkIiwic2NvcGUiOlsib3BlbmlkIl19.h5Pqzc_WKJFQXmfvF609O-MO-AU5hRf0H4A1rKqQzxUuVGIYl4u8ibPFBgTu7vbKzf316ZF7Ky6g0oAohrH3FhJWD1LVlevb-5bb-zLz8v2tgrgUZQ9KmmxnMzAtKbZnRJi2sK8JkaYZCdaQsr7Y2npazJWhnBkLwgedM0Pj22rJwfqJ2GoTIgzcMzyi_LYVTlf2JcBLIgstBKJJwSKMpXVjL9FYEBeBgVhmdViaB8yRH_-ewzBg-t6cI4bDFnIeauG-eTJFkqo-JJ6FsPNvX2iSDOOVDFqnzZVHJghUia7xwDBZvxgxoMQ3BqtT2oo7L2S4W4jAy5fkE796SsPHj1A" https://localhost:8080/api/environments

The command &quot;—insecure&quot; is added to prevent SSL certificate error. Check [this article](https://www.poftut.com/how-to-use-curl-with-https-protocol-and-urls/) to provide a certificate authority explicitly.

For Postman, you just need to add your generated token to the Bearer Token collapsed menu under the Authorization tab.

**2 Iesi-Rest with microservices**

The configuration of Iesi-Rest with microservices is like the one without microservices except that the configuration is done via Spring-Cloud.

The configuration files are on file system (src/main/ressources/config), but they can also be stored on a [git repository.](https://cloud.spring.io/spring-cloud-config/multi/multi__spring_cloud_config_server.html)

Iesi-Rest run on https.

**2.1 Spring Cloud**

Spring-Cloud allows to gather and encrypt information stored in &quot;applications.properties&quot; and &quot;application.yml&quot;.

For Spring Cloud to be able to encrypt and decrypt properties you will need to add the Java Cryptography Extension to your JVM (it is not included by default). You can [download the &quot;Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files&quot;](https://www.oracle.com/technetwork/java/javase/downloads/jce-all-download-5170447.html) from Oracle and follow the installation instructions. Make sure you download JCE for your Java platform.

The installation process is easy. [Basically you will need to copy the two downloaded jar files](https://dzone.com/articles/install-java-cryptography-extension-jce-unlimited) to a _/lib/security_ folder in your JDK.

Spring Cloud Server authentication is done using basic spring security, via a unique user stored in H2 database. Other stronger authentication (e.g. oauth2,  Vault…) are possible but the utilization of message broker or container are mandatory (e.g Docker, Rabbit MQ …).

To change the user&#39;s credentials stored in the database, go to &quot;src/main/ressources&quot; and add your information to the file &quot;data.sql&quot; to the table user (N.B; the password must be encrypt using Bcrypt with 10 rounds (click [here](https://www.browserling.com/tools/bcrypt) to generate your encrypt password):

            INSERT INTO users (username,password,enabled)  VALUES (&#39;admin&#39;,&#39;$2a$10$dJYXnn2ztBU72ooXSyl9.u/xF29ADNp3mbQYy/6cvkiGLskNG1UQi&#39;, TRUE);

**2.2 Generate Keystore for Asymmetric Encryption**

For asymmetric cryptography, configure a keystore to use:

keytool -genkeypair -alias iesi -keyalg RSA -keypass password -keystore iesiCloud.jks -storepass iesipassword

Once the keystore file is generated update the _bootstrap.yml_ (in directory &quot;src/main/resources&quot;) file of your Spring Cloud Config:

                                                encrypt:

                                                  key-store:

                                                      location: classpath:iesiCloud.jks

                                                      password: iesipassword

                                                      alias: iesi

                                                      secret: password

For more information, check this link: [Quick Intro to Spring Cloud Configuration] (https://www.baeldung.com/spring-cloud-configuration)

**2.3 Encrypt your data with Java Cryptography Extension**

To encrypt configuration property value, start you Spring Cloud Config server and then send an HTTP POST request to a /encrypt URL endpoint (you can also use Postman), e.g.:

                    curl -X POST http://localhost:8888/encrypt -d myPassword -u admin:password

where localhost:8888 is the port number of which the Spring Cloud Config server is running (in your case this port number might be different), &quot;myPassword&quot; is the string we want to encrypt, and &quot;admin:password&quot; are the username and password defined in the file &quot;data.sql&quot;.

The response in this case is an encrypted value:

    7509AQCP2xs/8TUf2mrQoZJ37AiKBf/UJHHaXoIgHqMOia0gNs9iAbm5BSuUWYL+0Tc2FiUln0UpT/gqLnEMNgo7gNXMyD42MM6NOMq4VfzJWXFMV93ngzDLlSWEvdHijFajUTFqts3Q0T1jxDHSehLipZ5AAXmeLc3hXZYvYMEBWQVxi0O2UYhYs0PZkyfC3kKq4PqIKHHsHGiOWNl3A6Wl6fY13VWsSVVIF4t03k/Ylats/ks0//KQdCmzEVg2vWITkpxSlv4Nl7YNT6XXdRFWrcqtFjXpUzrc+W0frtN4WvjLmFFP2BMZv982zF+R/X8gHMfRqDz9hJ1MInI+ZL2hBHqktV+VgK8DOOiEW1heuccFsiKF9LWkrRbEMQh0B93IUiY

To use the encrypted value of a configuration property, add a **{cipher} ** prefix.

**2.4 Configuration of Iesi-Rest with Spring Cloud**

Follow the instructions on section 1.2 for generating your SSL certificate.

Into the Spring-Cloud directory &quot;src/main/ressources/config&quot;, you can configure your .p12 related properties to the file &quot;iesi-rest.properties&quot;:

Don&#39;t forget to copy your PKCS12 keystore format file into the &quot;src/main/resource&quot; directory.

                                    iesi-keyalias=iesi

                                    iesi-keystore=iesi.p12

                                    _#The type of your keystore, PKCS12 or P12_

                                    iesi-keystoreType = PKCS12

                                    _#Each field with the keyword {cipher} must be encrypt as explained above._

                                    iesi-keypassword={cipher}AQBQMtxOkJLNPXKFgzTnd7aiegY…

                                    iesi-keystorepassword={cipher}AQBQMtxOkJLNPXKFgzTnd7aiegY…

The field &quot;iesi-HttpsPort&quot; allows you to choose your Https port number (e.g iesi-HttpsPort =8080).

The field &quot;iesi-HttpsadminPort&quot; allows Iesi-Rest-Admin to connect to this application; write the full length of your Https url, e.g: &quot;iesi-HttpsadminPort = [https://localhost:8080](https://localhost:8080) &quot;(without slash at the end).

Then, follow the instruction on section 1.3 for generating your .p12 file.

Into the Spring-Cloud directory &quot;src/main/ressources/config&quot;, you can configure your .p12  related properties to the file &quot;iesi-rest.properties&quot;:

Don&#39;t forget to copy your .p12 file  into the &quot;src/main/resource&quot; directory.

                                  iesi-security-keystore=iesi.p12

                                  _#Each field with the keyword {cipher} must be encrypt as explained above._

                                  iesi-security-keystore\_password={cipher}AQAjgcN7a/TXE1zy0DY/ZL9Lbwmc0+…

                                  iesi-security-keypair-alias= iesi
                                  
                                  iesi-security-keystoreType= PKCS12

                                  iesi-security-keypair-password={cipher}AQAjgcN7a/TXE1zy0DY/ZL…

**2.5 Configuration of Iesi-Rest-Admin with Spring Cloud**

Iesi-Rest-Admin is the [Spring Boot Admin UI, made by CodeCentric Team](https://github.com/codecentric/spring-boot-admin), to manage and monitor all the Spring Boot application Actuator endpoints at one place.

Actuator is a Spring Boot module, which adds REST/JMX endpoints to your application, so you can easily monitor and manage it in production. The endpoints offer health-check, metrics monitoring, access to logs, thread dumps, heap dumps, environmental info and more.

Authentication of the admin is done by basic spring security defined in the file&quot; iesi-admin.properties&quot;. This microservices is only linked to the Iesi-Rest microservice by hitting the actuator of this one to get metrics and other information.

Into the Spring-Cloud directory &quot;src/main/ressources/config&quot;, you can configure the application properties via the file &quot;iesi-admin.properties&quot;:

                                _#The port number of the iesi-rest-admin, only https._

                                iesi-adminPort = 8081

                                _#The username for connecting to the admin microservice_

                                iesi-adminUser= admin

                                _#The full https URL(without slash at the end)_

                                iesi-adminFullUrl = https://localhost:8081

                                _#Your password. Each field with the keyword {cipher} must be encrypt as explained above._

                                iesi-adminPassword={cipher}AQBNbs+rX+…

Follow the instructions on section 1.2 for generating your SSL certificate.

Don&#39;t forget to copy your PKCS12 keystore format file into the &quot;src/main/resource&quot; directory.

                                iesi-adminkeyalias= iesi

                                iesi-adminkeypassword={cipher}AQBLxnV1eYGwQ2WsPe/…

                                iesi-adminkeystore= iesi.p12

                                iesi-adminkeystorepassword= {cipher}AQBLxnV1eYGwQ2WsPe/…

                                iesi-adminkeystoretype= PKCS12

Enabling communication over https between Iesi-Rest application and the admin application using the self-signed certificate will probably cause a &quot;javax.net.ssl.SSLHandshakeException&quot; error. To solve this error, follow this[tutorial](http://www.littlebigextra.com/how-to-fix-javax-net-ssl-sslhandshakeexception-java-security-cert-certificateexception-no-subject-alternative-names-present/).



**2.6 Configuration of Iesi-Rest-Gateway with Spring Cloud**

In most microservice implementations, internal microservice endpoints are kept as private services. A set of public services are exposed to the clients using an API gateway.

The microservice Iesi-Rest-Gateway uses [Zuul](https://github.com/Netflix/zuul) as Gateway, a JVM based router and server side load balancer by Netflix. It provides an entry point to an ecosystem of microservices.

Iesi-Rest-Gateway runs on https and is secured by Oauth2. To decode the JWT token from the Authorization Server, i.e Iesi-Rest, it is necessary to use the public key from the self-signed certificated generated on section 2.4. Copy your public key to a &quot;public.txt&quot; file; no other name or extension are allowed.

Enabling communication over https between Iesi-Rest application and the Gateway using the self-signed certificate will probably cause a PKIX path building failed. To solve this error, you will need to import the self-signed certificate into the [cacerts file](http://www.littlebigextra.com/how-to-fix-pkix-path-building-failed-sun-security-provider-certpath-suncertpathbuilderexception/).
(You can also use [KeyStore Explorer](https://keystore-explorer.org/), an open source GUI replacement for the Java command-line utilities keytool.)

For testing purposes, you can configure Zuul to disable that validation, adding &quot;zuul.sslHostnameValidationEnabled=false&quot; to the file &quot;applications.yml&quot;.

Your SSL certificate must be the same that the one used for Iesi-Rest on section 2.4. Into the Spring-Cloud directory &quot;src/main/ressources/config&quot;, you can configure your SSL generated key  to the file &quot;iesi-gateway.properties&quot;:

                                        iesi-GatewayKeyalias= iesi
                                        iesi-GatewayKeystore= iesi.p12
                                        iesi-GatewayKeystoreType= PKCS12
                                        iesi-GatewayKeypassword= {cipher}AQBQMtxOkJLNPXKFgzTnd7aiegY…
                                        iesi-GatewayKeystorepassword= {cipher}AQBQMtxOkJLNPXKFgzTnd7aiegY…

The field &quot;iesi-GatewayPort&quot; allows you to choose your Https port number (e.g. iesi-HttpsPort =8082).

The field &quot;iesi-GatewayFullUrl&quot; allows Iesi-Rest to connect to this application; write the full length of Iesi-Rest Https url defined in section 2.4 , e.g.: &quot;iesi-GatewayFullUrl = [https://localhost:8080](https://localhost:8080) &quot;(without slash at the end).

With Iesi-Gateway, instead of hitting the main microservice Iesi-Rest, you will hit the Https port of the Gateway. The instructions are the same than on section 1.6, you just need to replace the Https port of Iesi-Rest by the one of the Gateway, e.g.:

curl -k -u client-id:secret -X POST https://localhost:8082/api/oauth/token\?grant_type=password\&username=admin\&password=password

where localhost:8082 is the port number of which the Iesi-Gateway is running (in your case this port number might be different).

Then, use your generated token to open endpoints on port 8082.

Now, there isn&#39;t any implementations of Zuul Filters (rate limiting, black listed users …), check the [documentation](https://cloud.spring.io/spring-cloud-netflix/multi/multi__router_and_filter_zuul.html) to add your own.

**Query String**

Querying through JSON elements:

[https://localhost:8080/api/environments?name=string](https://localhost:8080/api/environments?name=string)

[https://localhost:8080/api/environments?description=string](https://localhost:8080/api/environments?description=string)

Querying through JSON Array with objects as elements, add a capital letter to the object:

[https://localhost:8080/api/environments?parametersName=string](https://localhost:8080/api/environments?parametersName=string)

[https://localhost:8080/api/environments?parametersValue=string](https://localhost:8080/api/environments?parametersValue=string)

For skipping or limiting your query, add skip and limit:

[https://localhost:8080/api/environments?skip=5](https://localhost:8080/api/environments?skip=5)

will skip the first five results.

[https://localhost:8080/api/environments?limit=20](https://localhost:8080/api/environments?limit=20)

will show the 20 first results.

[https://localhost:8080/api/environments?skip=10&amp;li](https://localhost:8080/api/environments?skip=10&amp;limit=20)[m](https://localhost:8080/api/environments?skip=10&amp;limit=20)[it=20](https://localhost:8080/api/environments?skip=10&amp;limit=20)

will show the result between 10 and 20.

You can combine multiple query, e.g.:

[https://localhost:8080/api/environments?name=iesi-test&amp;skip=10&amp;limit=25](https://localhost:8080/api/environments?name=iesi-test&amp;skip=10&amp;limit=25)

