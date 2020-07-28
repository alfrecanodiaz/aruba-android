package py.com.aruba.clientes.data.helpers;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import py.com.aruba.clientes.BuildConfig;
import py.com.aruba.clientes.data.helpers.request.NetworkConnectionInterceptor;
import py.com.aruba.clientes.utils.Constants;
import py.com.aruba.clientes.utils.SharedPreferencesUtils;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestAdapter {

    private static Retrofit retrofit = null;
    private static Retrofit retrofitNoHeaders = null;

    /**
     * Obtenemos la instancia de Retrofit
     *
     * @param context
     * @return
     */
    public static Retrofit getClient(Context context) {
        String BASE_URL = Constants.ENV.PRODUCTION;
//        String BASE_URL = SharedPreferencesUtils.getBoolean(context, "IS_DEBUG") ? Constants.ENV.PRODUCTION : Constants.ENV.DEVELOP;

        // Retrofit
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);

        // Interceptor para loguear las peticiones
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(logging);
        }

        // Agregamos el token de autenticación a la cabecera
        final String token = "Bearer " + SharedPreferencesUtils.getString(context, Constants.TOKEN);
        builder.addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .build();
            return chain.proceed(newRequest);
        });

        // para manejar el error de internet
        builder.addInterceptor(new NetworkConnectionInterceptor(context));

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    /**
     * Obtenemos la instancia de Retrofit
     *
     * @param context
     * @return
     */
    public static Retrofit getPasswordClient(Context context) {
        String BASE_URL = "https://aruba.com.py/";
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);

        // Interceptor para loguear las peticiones
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(logging);
        }


        builder.addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .build();
            return chain.proceed(newRequest);
        });

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // para manejar el error de internet
        builder.addInterceptor(new NetworkConnectionInterceptor(context));

        Retrofit retro = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retro;
    }


    /**
     * Obtenemos la instancia de Retrofit sin headers extra
     *
     * @return
     */
    public static Retrofit getNoAuthClient(Context context) {
        String BASE_URL = Constants.ENV.PRODUCTION;

        // Retrofit
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);

        // Interceptor para loguear las peticiones
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            builder.addInterceptor(logging);
        }

        builder.addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .build();
            return chain.proceed(newRequest);
        });


        // para manejar el error de internet
        builder.addInterceptor(new NetworkConnectionInterceptor(context));

        retrofitNoHeaders = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
//        }
        return retrofitNoHeaders;
    }

    /**
     * Método para setear null las instancias de retrofit
     */
    public static void setNull() {
        retrofit = null;
        retrofitNoHeaders = null;
    }

    /**
     * Unsafe httpClient
     *
     * @return
     */
    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Open SSLSocket directly to gmail.com
//            SocketFactory sf = SSLSocketFactory.getDefault();
//            SSLSocket socket = (SSLSocket) sf.createSocket("admin.monchis.com.py", 80);
//            HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
//            SSLSession s = socket.getSession();

            ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2)
                    .cipherSuites(
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                            CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                            CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                            CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                            CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA,
                            CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA
                    )
                    .build();

            OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .connectionSpecs((Collections.singletonList(spec)));

//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
//            builder.hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//            // Seteamos en modo debug las consultas
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(logging);

            // Creamos el interceptor
//            client.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Chain chain) throws IOException {
//                    Request newRequest = null;
//                    if(token.isEmpty()){
//                        newRequest  = chain.request().newBuilder().build();
//                    }else{
//                        newRequest  = chain.request().newBuilder().addHeader("Authorization", token).build();
//                    }
//                    return chain.proceed(newRequest);
//                }
//            });

            return client.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
