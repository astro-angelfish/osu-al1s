/*
 * Copyright 2024 Astro angelfish
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package moe.orangemc.osu.al1s.auth;

import com.google.gson.Gson;
import moe.orangemc.osu.al1s.auth.credential.CredentialBase;
import moe.orangemc.osu.al1s.auth.credential.RefreshingCredentialImpl;
import moe.orangemc.osu.al1s.auth.token.ServerTokenResponse;
import moe.orangemc.osu.al1s.auth.token.TokenImpl;
import moe.orangemc.osu.al1s.bot.OsuBotImpl;
import moe.orangemc.osu.al1s.inject.api.Inject;
import moe.orangemc.osu.al1s.util.HttpUtil;
import moe.orangemc.osu.al1s.util.URLUtil;

import java.net.URL;
import java.util.Set;
import java.util.function.Consumer;

public class AuthenticationAPI {
    @Inject
    private Gson gson;

    @Inject
    private OsuBotImpl requester;
    private final URL targetURL;
    private final URL userRequestURL;

    public AuthenticationAPI() {
        URL rootUrl = requester.getBaseUrl();
        targetURL = URLUtil.concat(rootUrl, "oauth/token");
        userRequestURL = URLUtil.concat(rootUrl, "oauth/authorize");
    }

    public TokenImpl authorize(CredentialBase credential) {
        Set<Runnable> preHook = credential.getPreHook();

        for (Runnable runnable : preHook) {
            runnable.run();
        }

        ServerTokenResponse str = gson.fromJson(HttpUtil.post(targetURL, credential.toUrlEncodedForm()), ServerTokenResponse.class);
        return new TokenImpl(credential, str);
    }

    public void refreshToken(RefreshingCredentialImpl refreshingCredential, Consumer<ServerTokenResponse> updater) {
        ServerTokenResponse str = gson.fromJson(HttpUtil.post(targetURL, refreshingCredential.toUrlEncodedForm()), ServerTokenResponse.class);
        updater.accept(str);
    }

    public URL getUserRequestURL() {
        return userRequestURL;
    }

    public OsuBotImpl getRequester() {
        return requester;
    }
}
