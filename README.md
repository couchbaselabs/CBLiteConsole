
# CBLiteConsole

CBLiteConsole is a debugging tool for apps based on [Couchbase Lite](https://github.com/couchbase/couchbase-lite-android).

![](http://cl.ly/image/1U2J1p431i1Q/Screen%20Shot%202013-07-23%20at%207.02.05%20PM.png)

# Purpose

Drop CBLiteConsole into a Couchbase Lite app and:

* Show stats about the db
* Show the raw contents of the db
* Add sample docs
* Delete all docs

# Upcoming features

* View logcat logs
* View status of replications

# How to use CBLiteConsole

## Assuming project layout

This will assume the same project layout as [Grocery-Sync Android](https://github.com/couchbaselabs/GrocerySync-Android)

```
|-- GrocerySync-Android
|   |-- GrocerySync-Android-GrocerySync-Android.iml
|   |-- build
|   |-- build.gradle
|   |-- libs
|   `-- src
|-- GrocerySync-Android.iml
|-- README.md
|-- build.gradle
|-- gradle
|   `-- wrapper
|-- gradlew
|-- gradlew.bat
|-- local.properties
|-- local.properties.example
`-- settings.gradle
```

## Clone the submodule

```
$ git submodule add https://github.com/couchbaselabs/CBLiteConsole.git CBLiteConsole
```

your directory structure should now look like:

```
|-- CBLiteConsole
|   |-- README.md
|   |-- build.gradle
|   |-- libs
|   `-- src
|-- GrocerySync-Android
|   |-- GrocerySync-Android-GrocerySync-Android.iml
|   |-- build
|   |-- build.gradle
|   |-- libs
|   `-- src
|-- GrocerySync-Android.iml
|-- README.md
|-- etc..
```

## Edit settings.gradle 

Update settings.gradle to tell it about the new module.  

Before:

```groovy
include ':GrocerySync-Android'
```

After:

```groovy
include ':GrocerySync-Android', ':CBLiteConsole'
```

## Edit $project/build.gradle

In the case of Grocery-Sync, this is the file GrocerySync-Android/GrocerySync-Android/build.gradle:
 
Before:

```groovy
dependencies {
    compile 'com.android.support:support-v4:13.0.+'
    compile 'com.couchbase.cblite:CBLite:0.7.2'
    ...
}

```

After:

```groovy
dependencies {
    compile 'com.android.support:support-v4:13.0.+'
    compile 'com.couchbase.cblite:CBLite:0.7.2'
    ...
    compile project(':CBLiteConsole')
}
```

## Add entries to manifest.xml

Under the `application` element, add the following activities:

```xml
<activity
  android:name="com.couchbase.cblite.cbliteconsole.CBLiteConsoleActivity"
  android:label="@string/console_activity_display_message"
  android:parentActivityName="com.couchbase.couchbaseliteproject.MainActivity" >
  <meta-data
    android:name="android.support.PARENT_ACTIVITY"
    android:value="com.couchbase.couchbaseliteproject.MainActivity" />
</activity>

<activity
  android:name="com.couchbase.cblite.cbliteconsole.AllDocsActivity"
  android:label="@string/title_activity_all_docs" >
</activity>

```


## Modify dependencies if using maven artifacets

If your project uses Couchbase Lite maven artifacts, you will need to do a small hack to CBLiteConsole/build.gradle.

Before:

```
dependencies {
    compile 'com.android.support:support-v4:13.0.+'
    compile project(':CBLite')
    compile project(':CBLiteEktorp')
}
```

After:

```
dependencies {
    compile 'com.android.support:support-v4:13.0.+'
    compile 'com.couchbase.cblite:CBLite:0.7.2'
    compile 'com.couchbase.cblite:CBLiteEktorp:0.7.4'
}
```

## Launch the CBLiteConsole activity

This could be in a button handler, in response to shaking the phone, or wherever you want to put it.

```java
Intent intent = new Intent(MainActivity.this, CBLiteConsoleActivity.class);
Bundle b = new Bundle();
b.putString(CBLiteConsoleActivity.INTENT_PARAMETER_DATABASE_NAME, DATABASE_NAME);
intent.putExtras(b);
startActivity(intent);
```

NOTE: you will need to define the DATABASE_NAME variable to contain the database name your app uses.

