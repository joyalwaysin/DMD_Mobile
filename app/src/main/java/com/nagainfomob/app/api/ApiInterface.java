package com.nagainfomob.app.api;

import com.nagainfomob.app.model.AccountTypeList;
import com.nagainfomob.app.model.ChangePassData;
import com.nagainfomob.app.model.ForgotPassword.ForgotPassData;
import com.nagainfomob.app.model.ForgotPassword.ForgotPassResult;
import com.nagainfomob.app.model.ForgotPasswordOTP.ForgotPassOTPData;
import com.nagainfomob.app.model.ForgotPasswordOTP.ForgotPassOTPResult;
import com.nagainfomob.app.model.Payment.PaymentData;
import com.nagainfomob.app.model.Payment.PaymentResult;
import com.nagainfomob.app.model.Plan.PlanResponse;
import com.nagainfomob.app.model.RegisterDevice.RegisterDeviceData;
import com.nagainfomob.app.model.RegisterDevice.RegisterDeviceResult;
import com.nagainfomob.app.model.ResponseModel;
import com.nagainfomob.app.model.TileCategoryModel;
import com.nagainfomob.app.model.CountryList;
import com.nagainfomob.app.model.CreateProject.CreateProjectData;
import com.nagainfomob.app.model.CreateProject.CreateProjectResult;
import com.nagainfomob.app.model.LoadProject.LoadProjectResult;
import com.nagainfomob.app.model.LoadTile.LoadTileResult;
import com.nagainfomob.app.model.Login.LoginData;
import com.nagainfomob.app.model.Login.LoginResult;
import com.nagainfomob.app.model.OTP.OtpData;
import com.nagainfomob.app.model.OTP.OtpResult;
import com.nagainfomob.app.model.ProfileUpdate.UserData;
import com.nagainfomob.app.model.ProfileUpdate.UserResult;
import com.nagainfomob.app.model.Register.RegisterData;
import com.nagainfomob.app.model.Register.RegisterResult;
import com.nagainfomob.app.model.ResendOTP.ResendOTPData;
import com.nagainfomob.app.model.ResendOTP.ResendOTPResult;
import com.nagainfomob.app.model.TileTypeModel;
import com.nagainfomob.app.model.UnitList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("get_account_types")
    Call<List<AccountTypeList>> getAccountTypes();

    @GET("get_countries")
    Call<List<CountryList>> getCountryList();

    @GET("get_plans")
    Call<List<PlanResponse>> getPlan();

    @POST("users/signup")
    Call<RegisterResult> registerUser(@Body RegisterData body);

    @POST("users/verify_otp")
    Call<OtpResult> verifyOPT(@Body OtpData body);

    @POST("users/resend_otp")
    Call<ResendOTPResult> resendOPT(@Body ResendOTPData body);

    @POST("users/login")
    Call<LoginResult> loginUser(@Body LoginData body);

    @POST("users/forgot_password")
    Call<ForgotPassOTPResult> forgotPass(@Body ForgotPassOTPData body);

    @POST("users/reset_password")
    Call<ForgotPassResult> resetPass(@Body ForgotPassData body);

    @POST("users/change_password")
    Call<ResponseModel> changePass(@Body ChangePassData body, @Header("Authorization") String authHeader);

    @POST("devices")
    Call<RegisterDeviceResult> devicesRegister(@Body RegisterDeviceData body, @Header("Authorization") String authHeader);

    @PUT("users/update_user")
    Call<UserResult> updateUser(@Body UserData body, @Header("Authorization") String authHeader);

    @GET("get_units")
    Call<List<UnitList>> getUnits();

    @POST("project/create")
    Call<CreateProjectResult> createProject(@Body CreateProjectData body, @Header("Authorization") String authHeader);

    @GET("project/load_project")
    Call<LoadProjectResult> getProjects(@Query("type") String type, @Header("Authorization") String authHeader);

    @PUT("project/deactivate/{id}")
    Call<ResponseModel> deactivateProject(@Path("id") String id, @Header("Authorization") String authHeader);

    @GET("tiles/download")
    Call<LoadTileResult> getTiles(@Query("updated_at") String updated_at, @Header("Authorization") String authHeader);

    @GET("tiles/category/all")
    Call<TileCategoryModel> getTileCategory(@Header("Authorization") String authHeader);

    @GET("tiles/tile_types/all")
    Call<TileTypeModel> getTileType(@Header("Authorization") String authHeader);

    @POST("payment")
    Call<PaymentResult> requestPay(@Body PaymentData body);

}
