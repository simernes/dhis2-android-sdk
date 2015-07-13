/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.controllers.tasks;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.controllers.ResponseHolder;
import org.hisp.dhis.android.sdk.network.http.ApiRequest;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Header;
import org.hisp.dhis.android.sdk.network.http.Request;
import org.hisp.dhis.android.sdk.network.http.RestMethod;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.utils.APIException;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.sdk.utils.Preconditions.isNull;

/**
 * @author Simen Skogly Russnes on 23.02.15.
 */
public class RegisterEnrollmentTask implements INetworkTask {

    private final static String CLASS_TAG = "RegisterEnrollmentTask";

    private final ApiRequest.Builder<Object> requestBuilder;

    /**
     *
     * @param networkManager
     * @param callback
     * @param enrollment
     */
    public RegisterEnrollmentTask(NetworkManager networkManager,
                                  ApiRequestCallback<Object> callback, Enrollment enrollment) {

        requestBuilder = new ApiRequest.Builder<>();
        try {
        isNull(callback, "ApiRequestCallback must not be null");
        isNull(networkManager.getServerUrl(), "Server URL must not be null");
        isNull(networkManager.getHttpManager(), "HttpManager must not be null");
        isNull(networkManager.getBase64Manager(), "Base64Manager must not be null");
        isNull(enrollment, "Enrollment must not be null");
            List<Header> headers = new ArrayList<>();
            headers.add(new Header("Authorization", networkManager.getCredentials()));
            headers.add(new Header("Content-Type", "application/json"));

            byte[] body = null;
            try {
                body = Dhis2.getInstance().getObjectMapper().writeValueAsBytes(enrollment);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            Log.e(CLASS_TAG, new String(body));

            String url = networkManager.getServerUrl() + "/api/enrollments";
            RestMethod restMethod = RestMethod.POST;

            //updating if the enrollment has a valid UID
            if(enrollment.getEnrollment() != null) {
                url += "/" + enrollment.getEnrollment();
                restMethod = RestMethod.PUT;
            }
            Request request = new Request(restMethod, url, headers, body);

            requestBuilder.setRequest(request);
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(callback);
        } catch(IllegalArgumentException e) {
            requestBuilder.setRequest(new Request(RestMethod.POST, CLASS_TAG, new ArrayList<Header>(), null));
            requestBuilder.setNetworkManager(networkManager.getHttpManager());
            requestBuilder.setRequestCallback(callback);
            ResponseHolder holder = new ResponseHolder();
            holder.setApiException(APIException.unexpectedError(e.getMessage(), e));
        }
    }

    @Override
    public void execute() {
        new Thread() {
            public void run() {
                requestBuilder.build().request();
            }
        }.start();
    }
}