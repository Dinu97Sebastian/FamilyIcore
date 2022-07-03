package com.familheey.app.Interfaces;

import com.familheey.app.Models.Response.UserRegistrationResponse;

public interface RegistrationInteractor {

    void requestSocialMediaLogin(UserRegistrationResponse userRegistration);

    void loadUserRegistrationLevelOne(UserRegistrationResponse userRegistration);

    void loadUserRegistrationLevelTwo(UserRegistrationResponse userRegistration);

    void loadUserRegistrationLevelThree(UserRegistrationResponse userRegistration);

    void registerUser(UserRegistrationResponse userRegistration);

    void captureUserProfilePicture();

    void revertOneLevel();
}
