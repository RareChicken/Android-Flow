package kr.hs.dgsw.flow.view.login.presenter;

import android.content.Context;
import android.support.annotation.NonNull;

import kr.hs.dgsw.flow.R;
import kr.hs.dgsw.flow.application.FlowApplication;
import kr.hs.dgsw.flow.data.model.EditData;
import kr.hs.dgsw.flow.view.login.model.LoginData;
import kr.hs.dgsw.flow.util.retrofit.model.signin.LoginRequestBody;
import kr.hs.dgsw.flow.util.retrofit.model.signin.LoginResponseBody;
import retrofit2.Response;

public class LoginPresenterImpl implements ILoginContract.Presenter {

    private ILoginContract.View mView;

    private LoginData mLoginData;

    public LoginPresenterImpl(@NonNull ILoginContract.View view, Context context) {
        this.mView = view;
        this.mLoginData = new LoginData(context);
    }

    @Override
    public void setView(@NonNull ILoginContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDestroy() {
        this.mView = null;
    }

    @Override
    public void validateEmail(String email) {
        email = email.trim();

        EditData emailData = mLoginData.getEmail();
        emailData.setData(email);

        if (email.isEmpty()) {
            emailData.setValid(false);
            mView.setEmailError(LoginData.STR_EMAIL_EMPTY_ERROR);
            return;
        }

        if (!mLoginData.isEmailValid(email)) {
            emailData.setValid(false);
            mView.setEmailError(LoginData.STR_EMAIL_ERROR);
            return;
        }

        emailData.setValid(true);
        mView.setEmailErrorEnabled(false);
    }

    @Override
    public void validatePassword(String password) {
        password = password.trim();

        EditData passwordData = mLoginData.getPassword();
        passwordData.setData(password);

        if (password.isEmpty()) {
            passwordData.setValid(false);
            mView.setPasswordError(LoginData.STR_PASSWORD_EMPTY_ERROR);
            return;
        }

        if (!mLoginData.isPasswordValid(password)) {
            passwordData.setValid(false);
            mView.setPasswordError(LoginData.STR_PASSWORD_ERROR);
            return;
        }

        passwordData.setValid(true);
        mView.setPasswordErrorEnabled(false);
    }

    @Override
    public void validateLoginFields() {
        if (mLoginData.isDataValid()) {
            LoginRequestBody requestBody = mLoginData.makeLoginRequestBody();
            this.attemptLogin(requestBody);
        }
    }

    @Override
    public void attemptLogin(LoginRequestBody requestBody) {
        if (!FlowApplication.getConnectivityStatus()) {
            mView.showMessageToast(FlowApplication.getContext()
                    .getString(R.string.error_not_connected_to_internet));
            return;
        }

        mView.showProgress(true);

        mLoginData.callSignIn(requestBody, new LoginData.LoginCallback() {
            @Override
            public void onResponse(Response<LoginResponseBody> response) {
                mView.showProgress(false);
                if (response.isSuccessful()) {
                    LoginResponseBody responseBody = response.body();
                    mView.showMessageToast(responseBody.getMessage());
                    if (responseBody.getStatus() == 200) { // 성공
                        String token = responseBody.getData().getToken();

                        // 로컬DB에 User 업뎃 or 삽입
                        mLoginData.insertOrUpdateUser(
                                requestBody.getEmail(),
                                requestBody.getPassword(),
                                token
                        );

                        // 로그인 기록 저장
                        if (!mLoginData.insertLogin(requestBody.getEmail())) {
                            mView.showMessageToast("이메일이 DB에 존재하지 않습니다.");
                            return;
                        }

                        // 메인 엑티비티로 이동
                        mView.navigateToMain();
                    }
                } else {
                    mView.showMessageToast(response.message());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                mView.showProgress(false);
                mView.showMessageToast(t.getMessage());
            }
        });
    }
}
