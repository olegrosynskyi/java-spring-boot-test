# Spring Boot microservice template

* Description: Working template for creating a microservice running using spring boot
* External resources(s) used (for example: MySQL, Redis, RabbitMq): MySQL, RabbitMQ, Redis
* Used by: microcosm microservices

```scala
PLEASE REPLACE THIS FILE CONTENT WITH THE FOLLOWING DETAILS FOR THE IMPLEMENTED SERVICE:
```

* Description:
* External resources(s) used (for example: MySQL, Redis, RabbitMq):
* Used by:
* Depends on:
* Design link:
* Architecture Diagram:
* Swagger docs/link:

## Testing locally
It is important to get tests running in as close to a production-like environments as early as you can, so we
enable building the artifact just as it would look in production right on your dev machine. We figured out a few possible
recipes for this, and here they are.

### What you need (these requirements might change with time and new processes):
1. an updated version of Docker installed on you machine (1.12.+ and up)
1. docker-compose (1.12 and up) installed
1. some way to launch everything integrated with your testing cycle (on Java we use Gradle plugins & tasks, on Python Invoke)
1. login to with the following command `docker login kenshoo-docker.jfrog.io`

(there are multiple ways to install and operate docker, from the official installs for your OS, through all kinds of
hacks up to installing it via an external server, same goes for docker-compose. We encourage you to not try the first
method offered but investigate a bit to see which is trully the best for you. feel free to contact the microservices
team to ask and discuss on our #microservices-discuss channel on slack)

### How to do it:
1. Modify your local docker-compose.yml (a simple one comes with this template it might not be fitting for you, or ok with some changes).
   that starts everything that is essential for your app to work (as a rule you should try to limit such dependencies) -
   in most cases the setup will include your app and a mysql docker like this sample but your case might be different,  
   You can read more about compose [here](https://docs.docker.com/compose/gettingstarted/)

### Gradle Dependency Lock

To achieve reproducible builds, it is necessary to lock versions of dependencies and transitive dependencies such that 
a build with the same inputs will always resolve the same module versions. This is called dependency locking.

This template contains `gradle.lockfile` in each module, it contains list of all dependencies which are used in application. 
If you need to do any changes to dependencies you also need to update lock file. 

There are few ways to do that:

- Run command `./gradlew assemble --write-locks`
- Run existing task `./gradlew updateLocks`

It will add all necessary changes to `gradle.lockfile`, then you can review them and commit together with other changes.

### Project files explained

- `Dockerfile`: Basic image used for your application (in both production and tests). Usually the file merely points to a pre-built image hosted on `kenshoo.jfrog.io`. You can build the image locally using `./gradlew assemble`
- `docker-compose.yml`: Instructions on all docker containers used for testing. Contain reference to image "app" (built using `Dockerfile`). Each image in a `docker-compose` file can have a dependency, e.g. “app” often depends on  DB, Redis and RabbitMQ using this clause:

   ```yml
     depends_on:
         - "mysql"
         - "redis"
         - "rabbitmq"
   ```
  This mean "app" wouldn’t pass basic healthcheck if one of the dependencies is not started properly

### aws-credentials-provider

This template supports the usage of AWS assume-role functionality. It means that your application could use temporary credentials for a time-limited session or use static user credentials.

The way to use it is to populate your application.yml file correctly. Here's an example for such configuration:

```
aws:
  accessKey: '${AWS_ACCESS_KEY_ID:}'
  secretKey: '${AWS_SECRET_ACCESS_KEY:}'
  useRoleBasedAuth: '${USE_ROLE_BASED_AUTH:false}'
  webTokenFile: '${AWS_WEB_IDENTITY_TOKEN_FILE:}'
  roleArnName: '${AWS_ROLE_ARN:}'
  accessKeyAssumeRole: '${AWS_ACCESS_KEY_ID_ASSUME_ROLE:}'
  secretKeyAssumeRole: '${AWS_SECRET_ACCESS_KEY_ASSUME_ROLE:}'
  roleAppName: '${spring.application.name}'
```
There are three conditions to get credentials:


`AwsCredentialsProviderConfiguration()` - Is the object that collect all the parameters.

`GenerateAwsCredentialsProvider()` - Return the AWSCredentialsProvider within the credentials.


We use this toggle `useRoleBasedAuth` to determine if use static user credentials or role-based, by default this toggle set to `false` to use static user
credentials.


1. Using static credentials - simply providing user credentials that don't change `accessKey`, `secretKey`.


2. Assume Role - to be able to use temporary credentials from AWS STS using assume role via IAM-ROLE will need to set the toggle `useRoleBasedAuth` to `true`,
   provide `accessKeyAssumeRole`, `secretKeyAssumeRole` and pass the role arn name `roleArnName`.


3. If running on EKS, will need to set the toggle `useRoleBasedAuth` to `true` in addition, the library use WebIdentityToken to assume the role with the
   `roleArnName` and `webTokenFile`, that attached in the pod via the K8s resource - ServiceAccount.


Example:
After you added the relevant environment variable based on your strategy
```
    @Autowired
    private GenerateAwsCredentialsProvider credentialsProvider;
    
    AWSCredentialsProvider awsCredentials = credentialsProvider.getCredentials();
    //After we set the credentials we can configure the client builder with the AWSCredentialsProvider and build the client.
    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(awsCredentials).withRegion("us-east-1").build();
```        


### Useful commands
- Start all services: `./gradlew composedown clean cleandocker assemble dockerbuild composeup`
- Run all repository tests (after starting services): `./gradlew check`
- Find out which docker containers exist (and their names): `docker ps -a`
- Inspect docker container log: `docker logs [container_name]`
- SSH to docker: `docker exec -it [container_name] bash`
- Find docker IP and more info: `docker inspect [container_name]`
- Connect to MySQL database on docker container: `mysql -h 0.0.0.0 -p 33060 -uroot -proot` (if you have more than one running mysql docker containers, replace `0.0.0.0` with IP from docker inspect command)

### Useful Information
1. Build up your complete test integration with your prefered tool (using gradle for gradle based projects makes sense
   as it ties in well with your regular process. This can be by just wrapping docker-compose commands within cmd gradle
   tasks, or using one of the dedicated [gradle plugin](https://github.com/avast/docker-compose-gradle-plugin)
1. run your tests using the artifact (this is fitting for blackbox testing only of course)

### Additional thoughts
1. don't forget to leave in a simple task that just raises the docker containers, so that you can do manual
   testing/verification when required (basically just a `docker-compose up` command)
1. Another interesting use case is this allows to inject environment variables (via the compose environment section),
   so you can test different configurations easily.
1. Also this makes it very easy (compared to other methods) to test integration to other systems
   (just add the docker...) and mocking services (by launching a mock service and connecting to it instead like [moto standalone server](https://github.com/spulec/moto#stand-alone-server-mode)
   which allows wire mocking of many AWS services )
