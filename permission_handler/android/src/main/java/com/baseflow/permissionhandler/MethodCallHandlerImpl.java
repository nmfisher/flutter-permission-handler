package com.baseflow.permissionhandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;
import com.baseflow.permissionhandler.PermissionManager.ActivityRegistry;
import com.baseflow.permissionhandler.PermissionManager.PermissionRegistry;

import com.baseflow.permissionhandler.FakeActivity;

import android.util.Log;

import java.util.List;
import java.util.ArrayList;

final class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler {
    private final Context applicationContext;
    private final AppSettingsManager appSettingsManager;
    private final PermissionManager permissionManager;
    private final ServiceManager serviceManager;

    MethodCallHandlerImpl(
            Context applicationContext,
            AppSettingsManager appSettingsManager,
            PermissionManager permissionManager,
            ServiceManager serviceManager) {
        this.applicationContext = applicationContext;
        this.appSettingsManager = appSettingsManager;
        this.permissionManager = permissionManager;
        this.serviceManager = serviceManager;
    }

    @Nullable
    private Activity activity;

    @Nullable
    private ActivityRegistry activityRegistry;

    @Nullable
    private PermissionRegistry permissionRegistry;

    public void setActivity(@Nullable Activity activity) {
      this.activity = activity;
    }

    public void setActivityRegistry(
        @Nullable ActivityRegistry activityRegistry) {
      this.activityRegistry = activityRegistry;
    }

    public void setPermissionRegistry(
        @Nullable PermissionRegistry permissionRegistry) {
      this.permissionRegistry = permissionRegistry;
    }

    static List<MethodCallHandlerImpl> handlers = new ArrayList<MethodCallHandlerImpl>();
    static List<MethodCall> calls = new ArrayList<MethodCall>();
    static List<Result> results = new ArrayList<Result>();
    public static void handle(int index, FakeActivity activity) {
      MethodCallHandlerImpl handler = handlers.get(index);
      handler.setActivity(activity);
      handler.setActivityRegistry(activity);
      handler.setPermissionRegistry(activity);
      handler.onMethodCall(calls.get(index), results.get(index));
      handlers.remove(index);
      calls.remove(index);
      results.remove(index);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result)
    {
      if(activity == null) {
        Intent intent = new Intent(applicationContext, FakeActivity.class);
        handlers.add(this);
        calls.add(call);
        results.add(result);
        intent.putExtra("HANDLER_INDEX", handlers.size() - 1);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        applicationContext.startActivity(intent);
      } else {
        switch (call.method) {
            case "checkServiceStatus": {
                @PermissionConstants.PermissionGroup final int permission = Integer.parseInt(call.arguments.toString());
                serviceManager.checkServiceStatus(
                        permission,
                        applicationContext,
                        result::success,
                        (String errorCode, String errorDescription) -> result.error(
                                errorCode,
                                errorDescription,
                                null));

                break;
            }
            case "checkPermissionStatus": {
                @PermissionConstants.PermissionGroup final int permission = Integer.parseInt(call.arguments.toString());
                permissionManager.checkPermissionStatus(
                        permission,
                        applicationContext,
						activity,
                        result::success,
                        (String errorCode, String errorDescription) -> result.error(
                                errorCode,
                                errorDescription,
                                null));

                break;
            }
            case "requestPermissions":
                final List<Integer> permissions = call.arguments();
                permissionManager.requestPermissions(
                        permissions,
                        activity,
                        activityRegistry,
                        permissionRegistry,
                        result::success,
                        (String errorCode, String errorDescription) -> result.error(
                                errorCode,
                                errorDescription,
                                null));

                break;
            case "shouldShowRequestPermissionRationale": {
                @PermissionConstants.PermissionGroup final int permission = Integer.parseInt(call.arguments.toString());
                permissionManager.shouldShowRequestPermissionRationale(
                        permission,
                        activity,
                        result::success,
                        (String errorCode, String errorDescription) -> result.error(
                                errorCode,
                                errorDescription,
                                null));

                break;
            }
            case "openAppSettings":
                appSettingsManager.openAppSettings(
                        applicationContext,
                        result::success,
                        (String errorCode, String errorDescription) -> result.error(
                                errorCode,
                                errorDescription,
                                null));

                break;
            default:
                result.notImplemented();
                break;
        }
    }
  }
}

