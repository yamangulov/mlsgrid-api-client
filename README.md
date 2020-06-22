# mlsgrid-api-client
Java client for MLS GRID API

Library connection in Gradle:

build.gradle file:

```
ext {
...
mlsGridApiClientVersion = '1.0-SNAPSHOT'
    awsAccessKeyId = System.env.AWS_ACCESS_KEY_ID ?: findProperty('AWS_ACCESS_KEY_ID')
    awsSecretAccessKey = System.env.AWS_SECRET_ACCESS_KEY ?: findProperty('AWS_SECRET_ACCESS_KEY')
}
repositories {
    ...
    maven {
        url publishUrl
        credentials(AwsCredentials) {
            accessKey = awsAccessKeyId
            secretKey = awsSecretAccessKey
        }
    }
}
dependencies {
    compile group: 'com.innedhub', name: 'mlsgrid-api-client', version: "${mlsGridApiClientVersion}"
}
```
gradle.properties file:
(replace with your values)

```
AWS_ACCESS_KEY_ID=key
AWS_SECRET_ACCESS_KEY=secretKey
```



