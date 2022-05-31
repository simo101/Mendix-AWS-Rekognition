package aws_sig4.impl;

import static aws_sig4.proxies.constants.Constants.getAWS_AccessKeyId;
import static aws_sig4.proxies.constants.Constants.getAWS_SecretAccessKey;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpFullRequest.Builder;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.regions.Region;

/**
 * Abstract class with the shared logic to sign requests for use with AWS
 *
 * See also https://github.com/awslabs/aws-request-signing-apache-interceptor/issues/5
 *
 * @author reinout
 */
public abstract class AWSRequestSigningInterceptor {

    private final String signingName;
    private final Region region;
    private final AwsCredentialsProvider credentialsProvider;


    public AWSRequestSigningInterceptor(String signingName, Region region) {
        this(signingName, region, StaticCredentialsProvider.create(
            AwsBasicCredentials.create(getAWS_AccessKeyId(), getAWS_SecretAccessKey()))
        );
    }

    public AWSRequestSigningInterceptor(String signingName, Region region, AwsCredentialsProvider credentialsProvider) {
        this.signingName = signingName;
        this.region = region;
        this.credentialsProvider = credentialsProvider;

    }

    private void appendHeaders(Header[] headers, SdkHttpFullRequest.Builder requestBuilder) {
        Arrays.stream(headers)
            .filter(Predicate.not(shouldSkipHeader()))
            .forEach(hdr -> requestBuilder.appendHeader(hdr.getName(), hdr.getValue()));
    }

    private void appendQueryParameters(URIBuilder uriBuilder, SdkHttpFullRequest.Builder requestBuilder) {
        uriBuilder.getQueryParams().stream()
            .collect(Collectors.toMap(
                NameValuePair::getName,
                NameValuePair::getValue,
                (first, second) -> first)) // merge function in case of conflicts
            .forEach(requestBuilder::appendRawQueryParameter);
    }

    private static Predicate<? super Header> shouldSkipHeader() {
        return header -> "host".equalsIgnoreCase(header.getName())
            || ("content-length".equalsIgnoreCase(header.getName()) && "0".equals(header.getValue())); // Strip Content-Length: 0
    }

    protected SdkHttpFullRequest createSignedRequestForAWS(String uri, String host, SdkHttpMethod httpMethod, byte[] content, Header[] headers) throws IOException {
        try {
            Aws4Signer signer = Aws4Signer.create();
            URIBuilder uriBuilder = new URIBuilder(uri);

            Aws4SignerParams signerParams = Aws4SignerParams.builder()
                .signingName(signingName)
                .awsCredentials(credentialsProvider.resolveCredentials())
                .signingRegion(region)
                .build();

            Builder requestBuilder = SdkHttpFullRequest.builder()
                .uri(URI.create(host))
                .method(httpMethod)
                .encodedPath(uriBuilder.build().getRawPath())
                .contentStreamProvider(() -> new ByteArrayInputStream(content));

            appendQueryParameters(uriBuilder, requestBuilder);
            appendHeaders(headers, requestBuilder);

            // Sign the request
            final SdkHttpFullRequest signedRequest = signer.sign(requestBuilder.build(), signerParams);

            return signedRequest;
        } catch (URISyntaxException ex) {
            // HttpHost already contains checks for host validity, so in practice don't expect this to happen
            throw new AWSRequestSigningInterceptorException(ex);
        }
    }

    protected byte[] inputStreamToBytes(InputStream is) throws IOException {
        return IOUtils.toString(is, StandardCharsets.UTF_8).getBytes();
    }

}
