package com.baseflow.permissionhandler;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;

import android.app.Activity;

import io.flutter.plugin.common.PluginRegistry;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

import android.util.Log;



public class FakeActivity extends Activity implements PermissionManager.ActivityRegistry, PermissionManager.PermissionRegistry {

    private MethodChannel methodChannel;
    private MethodCallHandlerImpl methodCallHandler;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int idx = getIntent().getIntExtra("HANDLER_INDEX", -1);
        MethodCallHandlerImpl.handle(idx, this);
    }
    public void addListener(PluginRegistry.ActivityResultListener handler) {

    }

    public void addListener(PluginRegistry.RequestPermissionsResultListener handler) {
        // todo - does this need to be wired up somewhere?
    }

}