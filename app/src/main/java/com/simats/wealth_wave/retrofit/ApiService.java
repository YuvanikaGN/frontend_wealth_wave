package com.simats.wealth_wave.retrofit;

import com.google.gson.JsonObject;
import com.simats.wealth_wave.models.AddPlanRequest;
import com.simats.wealth_wave.models.AddSavingRequest;
import com.simats.wealth_wave.models.AddSavingRequestWithPlan;
import com.simats.wealth_wave.models.BankVerifyRequest;
import com.simats.wealth_wave.models.ChatbotRequest;
import com.simats.wealth_wave.models.DeleteExpRequest;
import com.simats.wealth_wave.models.DeletePlanRequest;
import com.simats.wealth_wave.models.DeleteSavingRequest;
import com.simats.wealth_wave.models.DeleteSavingRequestWithPlan;
import com.simats.wealth_wave.models.DeleteTransactionRequest;
import com.simats.wealth_wave.models.ExpRequest;
import com.simats.wealth_wave.models.GetSavingsRequest;
import com.simats.wealth_wave.models.HomeRequest;
import com.simats.wealth_wave.models.LoginRequest;
import com.simats.wealth_wave.models.OTPRequest;
import com.simats.wealth_wave.models.Plan;
import com.simats.wealth_wave.models.ResetPasswordRequest;
import com.simats.wealth_wave.models.SignupRequest;
import com.simats.wealth_wave.models.Transaction;
import com.simats.wealth_wave.models.TransactionRequest;
import com.simats.wealth_wave.models.UpdateIncomeRequest;
import com.simats.wealth_wave.models.UpdatePlanRequest;
import com.simats.wealth_wave.models.UpdateSavingsPlanRequest;
import com.simats.wealth_wave.models.User;
import com.simats.wealth_wave.models.UserDetailsRequest;
import com.simats.wealth_wave.models.UserIdRequest;
import com.simats.wealth_wave.responses.AddPlanResponse;
import com.simats.wealth_wave.responses.AddSavingResponse;
import com.simats.wealth_wave.responses.AddSavingResponseWithPlan;
import com.simats.wealth_wave.responses.BaseResponse;
import com.simats.wealth_wave.responses.ChatbotResponse;
import com.simats.wealth_wave.responses.DeleteExpResponse;
import com.simats.wealth_wave.responses.DeleteSavingResponse;
import com.simats.wealth_wave.responses.DeleteSavingResponseWithPlan;
import com.simats.wealth_wave.responses.DeleteTransactionResponse;
import com.simats.wealth_wave.responses.ExpResponse;
import com.simats.wealth_wave.responses.GenericResponse;
import com.simats.wealth_wave.responses.GetAllPlansResponse;
import com.simats.wealth_wave.responses.GetSavingsPlanResponse;
import com.simats.wealth_wave.responses.GetSavingsResponse;
import com.simats.wealth_wave.responses.GetUserDetailsResponse;
import com.simats.wealth_wave.responses.HomeResponse;
import com.simats.wealth_wave.responses.IncomeResponse;
import com.simats.wealth_wave.responses.LoginResponse;
import com.simats.wealth_wave.responses.OTPResponse;
import com.simats.wealth_wave.responses.PlanResponse;
import com.simats.wealth_wave.responses.ResetPasswordResponse;
import com.simats.wealth_wave.responses.SavingsResponse;
import com.simats.wealth_wave.responses.SignupResponse;
import com.simats.wealth_wave.responses.BankVerifyResponse;
import com.simats.wealth_wave.responses.TransactionResponse;
import com.simats.wealth_wave.responses.UpdatePlanResponse;
import com.simats.wealth_wave.responses.UpdateSavingsPlanResponse;
import com.simats.wealth_wave.responses.UserDetailsResponse;
import com.simats.wealth_wave.responses.UserPlanResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("sign_up.php")
    Call<SignupResponse> registerUser(@Body SignupRequest signupRequest);

    @Headers("Content-Type: application/json")
    @POST("log_in.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @Headers("Content-Type: application/json")
    @POST("verify_bank_details.php")
    Call<BankVerifyResponse> verifyBankDetails(@Body BankVerifyRequest request);

    @POST("send_otp.php")
    Call<OTPResponse> sendOTP(@Body OTPRequest request);

    @Headers("Content-Type: application/json")
    @POST("reset_password.php")
    Call<ResetPasswordResponse> resetPassword(@Body ResetPasswordRequest request);

    @Headers("Content-Type: application/json")
    @POST("user_details.php")
    Call<UserDetailsResponse> submitUserDetails(@Body UserDetailsRequest request);


    @Headers("Content-Type: application/json")
    @POST("get_home_data.php")  // Your PHP file endpoint
    Call<HomeResponse> getHomeData(@Body HomeRequest request);

    @GET("get_user_details.php")
    Call<UserPlanResponse> getUserPlan(@Query("user_id") int userId);

    @POST("get_user_details.php")
    Call<GetUserDetailsResponse> getUserDetails(@Body UserIdRequest request);


    @POST("update_user_details.php")
    Call<UpdatePlanResponse> updateSavingsPlan(@Body UpdatePlanRequest request);

    @POST("update_income.php")
    Call<GenericResponse> updateIncome(@Body UpdateIncomeRequest request);


    @POST("auth/login")
    Call<User> login(@Body Map<String, String> body);



    @POST("update_profile.php")
    Call<JsonObject> updateProfile(@Body JsonObject body);


    @GET("get_user_details.php")   // your PHP file
    Call<UserDetailsResponse> getUserDetails(@Query("email") String email);

    // Change the GET request to POST for user plan
    @Headers("Content-Type: application/json")
    @POST("get_user_plan.php")  // your PHP endpoint
    Call<UserPlanResponse> getUserPlan(@Body Map<String, Integer> body);


    @POST("add_transaction.php")
    Call<TransactionResponse> addTransaction(@Body TransactionRequest transaction);

    @POST("transaction_delete.php")
    Call<DeleteTransactionResponse> deleteTransaction(@Body DeleteTransactionRequest request);

    @POST("add_exp.php")
    Call<ExpResponse> addExp(@Body ExpRequest exp);

    @POST("exp_delete.php")
    Call<DeleteExpResponse> deleteExp(@Body DeleteExpRequest request);


    @GET("get_transaction.php")
    Call<List<Transaction>> getTransactions(@Query("user_id") int userId);


    @GET("get_expense.php")
    Call<List<Transaction>> getExpense(@Query("user_id") int userId);

    // POST request to add saving
    @POST("save_savings.php")
    Call<AddSavingResponse> addSaving(@Body AddSavingRequest request);

    // GET request to fetch savings
    @GET("get_savings.php")
    Call<GetSavingsResponse> getSavings(
            @Query("user_id") int userId,
            @Query("plan_id") int planId
    );

    // in your Api interface
    @POST("add_user_details.php")
    Call<AddPlanResponse> addPlan(@Body AddPlanRequest request);

    @POST("fetch_user_plans.php")
    Call<List<Plan>> getUserPlans(@Body Map<String, Integer> body);


    @GET("get_income.php") // endpoint on your server
    Call<IncomeResponse> getIncome(@Query("user_id") int userId);

    @FormUrlEncoded
    @POST("get_savings_plan.php")
    Call<GetSavingsPlanResponse> getSavingsPlan(@Field("user_id") int userId);

    @POST("update_savings_plan.php")
    Call<UpdateSavingsPlanResponse> updateSavingsPlan(@Body UpdateSavingsPlanRequest request);

    @POST("add_savings_plan.php")
    Call<AddPlanResponse> addSavingsPlan(@Body AddPlanRequest request);

    // Fetch all plans for a user
    @GET("get_savings_plans.php")
    Call<GetSavingsPlanResponse> getSavingsPlans(@Query("user_id") int userId);

    @GET("get_all_plans.php")
    Call<GetAllPlansResponse> getAllPlans(@Query("user_id") int userId);

    @POST("delete_savings_plan.php")
    Call<BaseResponse> deletePlan(@Body DeletePlanRequest request);

    @FormUrlEncoded
    @POST("get_plan_with_approx.php")
    Call<GetAllPlansResponse> getPlansWithApprox(
            @Field("user_id") int userId
    );

    @POST("save_savings.php")
    Call<AddSavingResponseWithPlan> addSaving(@Body AddSavingRequestWithPlan request);

    @POST("delete_saving.php")
    Call<DeleteSavingResponseWithPlan> deleteSaving(@Body DeleteSavingRequestWithPlan request);

    @GET("get_all_plan.php")
    Call<PlanResponse> getPlans(@Query("user_id") int userId);

    @POST("chatbot.php")
    Call<ChatbotResponse> sendMessage(@Body ChatbotRequest request);

    @Multipart
    @POST("upload_profile_image.php")
    Call<JsonObject> uploadProfileImage(
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part profile_image
    );

    @FormUrlEncoded
    @POST("remove_profile_image.php")
    Call<JsonObject> removeProfileImage(@Field("user_id") int userId);


}
