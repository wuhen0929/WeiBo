package com.wenming.weiswift.ui.react;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;

import java.util.Arrays;
import java.util.List;

/**
 * Created by brad on 16/6/19.
 */
public class ReactNativeActivity extends ReactActivity {

    @Override
    protected String getMainComponentName() {
        return "AwesomeProject";
    }

    @Override
    protected boolean getUseDeveloperSupport() {
        return false;
    }

    @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(new MainReactPackage());
    }
}
