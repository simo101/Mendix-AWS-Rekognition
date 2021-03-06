// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package aws_sig4.actions;

import static com.mendix.core.Core.http;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import aws_sig4.impl.MxAWSRequestSigningInterceptor;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.regions.Region;

public class InitializeAWSRequestInterceptor extends CustomJavaAction<java.lang.Void>
{
	private java.lang.String hostPattern;
	private java.lang.String signingName;
	private java.lang.String awsRegion;

	public InitializeAWSRequestInterceptor(IContext context, java.lang.String hostPattern, java.lang.String signingName, java.lang.String awsRegion)
	{
		super(context);
		this.hostPattern = hostPattern;
		this.signingName = signingName;
		this.awsRegion = awsRegion;
	}

	@java.lang.Override
	public java.lang.Void executeAction() throws Exception
	{
		// BEGIN USER CODE
        String strippedHostPattern = StringUtils.substring(hostPattern, "https://".length());

        MxAWSRequestSigningInterceptor interceptor = new MxAWSRequestSigningInterceptor(signingName, Region.of(awsRegion));
        http().registerHttpRequestInterceptor(strippedHostPattern, interceptor);
        return null;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "InitializeAWSRequestInterceptor";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}
