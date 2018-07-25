package ng.org.faithtabernacle.android.MedivatorBeta.model.Remote;

import ng.org.faithtabernacle.android.MedivatorBeta.model.MyPlaces;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {

    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);

    @GET
    Call<MyPlaces> getDetailPlace(@Url String url);
}
