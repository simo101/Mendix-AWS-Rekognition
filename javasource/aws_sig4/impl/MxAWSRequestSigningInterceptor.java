package aws_sig4.impl;

import com.mendix.http.HttpHeader;
import com.mendix.http.IHttpContext;
import com.mendix.http.IHttpRequest;
import com.mendix.http.IHttpRequestInterceptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collector;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

/**
 * Contains logic specific to intercepting Mendix HTTP requests and signing them
 *
 * @author reinout
 */
public class MxAWSRequestSigningInterceptor extends AWSRequestSigningInterceptor implements IHttpRequestInterceptor {

    public MxAWSRequestSigningInterceptor(String signingName, Region region) {
        super(signingName, region);
    }

    public MxAWSRequestSigningInterceptor(String signingName, Region region, AwsCredentialsProvider credentialsProvider) {
        super(signingName, region, credentialsProvider);
    }

    @Override
    public void process(IHttpRequest ihr, IHttpContext ihc) {
        try {
             
        	String uri = ihr.getUri();
            String host = ihc.getHttpTargetHostUri()
                .orElseThrow(AWSRequestSigningInterceptorException::new);
            SdkHttpMethod httpMethod = SdkHttpMethod.fromValue(ihr.getMethod());
            Optional<InputStream> content = ihr.getContent();
            Header[] headers = Arrays.stream(ihr.getAllHeaders())
                .collect(apacheHeaderCollect());
            byte[] contentBytes = content.isPresent() ? inputStreamToBytes(content.get()) : new byte[0];

            SdkHttpFullRequest signedRequest = createSignedRequestForAWS(uri, host, httpMethod, contentBytes, headers);

            // Translate back to Mendix request
            ByteArrayInputStream ihrContent = new ByteArrayInputStream(contentBytes);

            ihr.setHeaders(mapToMxHeaderArray(signedRequest.headers()));
            ihr.setContent(ihrContent, contentBytes.length);
        } catch (IOException ex) {
            throw new AWSRequestSigningInterceptorException(ex);
        }
    }

    protected static HttpHeader[] mapToMxHeaderArray(final Map<String, List<String>> mapHeaders) {

        return mapHeaders.entrySet().stream()
            .flatMap(entry -> entry.getValue().stream()
                .map(hdrValue -> new HttpHeader(entry.getKey(), hdrValue))
            ).toArray(HttpHeader[]::new);
    }

    // Implicitly converts Mendix HttpHeaders to Apache BasicHeaders
    // https://dzone.com/articles/how-to-transform-elements-in-a-stream-using-a-coll
    private static Collector<HttpHeader, ?, Header[]> apacheHeaderCollect() {
        return Collector.of(
            () -> new ArrayList<Header>(),
            (list, mxHeader) -> list.add(new BasicHeader(mxHeader.getName(), mxHeader.getValue())),
            (first, second) -> {
                first.addAll(second);
                return first;
            },
            list -> list.toArray(new Header[list.size()]));
    }
}
