package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.app.Application;
import android.content.Context;

import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface DefaultAppModule {
    Application providesApplication();

    Logger providesLogger();

    ApiExceptionHandler providesApiExceptionHandler(Context context, Logger logger);

    AppPreferences providesApplicationPreferences(Context context);

    SessionPreferences providesSessionPreferences(Context context);

    SyncDateWrapper providesSyncDateWrapper(Context context, AppPreferences preferences, Logger logger);
}