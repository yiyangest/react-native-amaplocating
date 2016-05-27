# react-native-amaplocating
AMap (gaode map) locating sdk for react-native

## install

`npm install react-native-amaplocating --save`

### iOS

1. Open your project in XCode, right click on `Libraries` and click `Add
   Files to "Your Project Name"` Look under `node_modules/react-native-amaplocating` and add `RCTAMapLocating.xcodeproj`.
2. Add `libRCTAMapLocating.a` to `Build Phases -> Link Binary With Libraries.
3. Click on `RCTAMapLocating.xcodeproj` in `Libraries` and go the `Build
   Settings` tab. Double click the text to the right of `Header Search
   Paths` and verify that it has `$(SRCROOT)/../react-native/React` - if they
   aren't, then add them. This is so XCode is able to find the headers that
   the `RCTAMap` source files are referring to by pointing to the
   header files installed within the `react-native` `node_modules`
   directory.
4. Add `node_modules/react-native-amaplocating/RCTAMapLocating/RCTAMap/AMap/*.framework` to your project.
5. Set your project's framework Search Paths to include
`$(PROJECT_DIR)/../node_modules/react-native-amaplocating/ios/RCTAMapLocating/RCTAMap/AMap`.
6. Whenever you want to use it within React code now you can: `var MapView =
   require('react-native-amaplocating');`


### android

1. in `android/settings.gradle`

  ```
  include ':app', ':react-native-amaplocating'
  project(':react-native-amaplocating').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-amaplocating/android/react-native-amaplocating')
  ```

2. in `android/app/build.gradle` add:

  ```
  dependencies {
    ...
    compile project(':react-native-amaplocating')
  }
  ```
3. in `MainActivity.java` add
**Newer versions of React Native**
      ```
    ...
    import com.yiyang.reactnativebaidumap.ReactMapPackage; // <--- This!
    ...
    public class MainActivity extends ReactActivity {

     @Override
     protected String getMainComponentName() {
         return "sample";
     }

     @Override
     protected boolean getUseDeveloperSupport() {
         return BuildConfig.DEBUG;
     }

     @Override
     protected List<ReactPackage> getPackages() {
       return Arrays.<ReactPackage>asList(
         new MainReactPackage()
         new ReactMapPackage() // <---- and This!
       );
     }
   }
   ```

    **Older versions of React Native**
   ```
   ...
   import com.yiyang.rnamaplocating.ReactMapPackage; // <--- This!
   ...
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       mReactRootView = new ReactRootView(this);

       mReactInstanceManager = ReactInstanceManager.builder()
               .setApplication(getApplication())
               .setBundleAssetName("index.android.bundle")
               .setJSMainModuleName("index.android")
               .addPackage(new MainReactPackage())
               .addPackage(new ReactMapPackage()) // <---- and This!
               .setUseDeveloperSupport(BuildConfig.DEBUG)
               .setInitialLifecycleState(LifecycleState.RESUMED)
               .build();

       mReactRootView.startReactApplication(mReactInstanceManager, "MyApp", null);

       setContentView(mReactRootView);
   }
   ```
4. specify your Gaode Maps API Key in your `AndroidManifest.xml`:

  ```xml
  <application
    android:allowBackup="true"
    android:label="@string/app_name"
    android:icon="@mipmap/ic_launcher"
    android:theme="@style/AppTheme">
      <!-- You will only need to add this meta-data tag, but make sure it's a child of application -->
      <meta-data
        android:name="com.amap.api.v2.apikey"
        android:value="{{Your gaode maps API Key Here}}"/>
  </application>
  ```    

## usage

```
...
import YYAMapLocation from 'react-native-amaplocating';

...
componentDidMount() {
    YYAMapLocation.getCurrentPosition((position) => {
        console.log("location get current position: ", position);
        this.setState({
            text: JSON.stringify(position)
        });
    }, (error) => {
        console.log("location get current position error: ", error);
        this.setState({
            text: "error: " + error
        });
    });
    this.watchID = YYAMapLocation.watchPosition((position) => {
        console.log("watch position: ", position);
        this.setState({
            text: "watch position: " + JSON.stringify(position)
        });
        YYAMapLocation.clearWatch(this.watchID);
    });
}

render() {
    <View style={styles.container}>
        <Text style={styles.welcome}>
            {this.state.text}
        </Text>
    </View>
}
