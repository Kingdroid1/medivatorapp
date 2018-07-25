package ng.org.faithtabernacle.android.MedivatorBeta;


import ng.org.faithtabernacle.android.MedivatorBeta.model.Remote.IGoogleAPIService;
import ng.org.faithtabernacle.android.MedivatorBeta.model.Remote.RetrofitClient;
import ng.org.faithtabernacle.android.MedivatorBeta.model.Results;

public class Common {

    public static Results currentResult;
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com";

    public static IGoogleAPIService getGoogleAPIService()
    {
        return RetrofitClient.getClient ( GOOGLE_API_URL ).create ( IGoogleAPIService.class );
    }
}
