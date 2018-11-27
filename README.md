# CryptoStreamAPI
Query Streaming Crypto Exchange Market Data to Elastic-Search-Engine

# Tech-Stack:

- JDK-8 for core programming

- Spring-Boot for Service/Repository management and interaction with Elastic-Engine

- Spring-elastic-data manages all dependencies with Elastic-Search Component

- Mockito and Junit for Unit-Testing

- Embedded-Elastic-Search for Integration Testing

# API:

  Swagger support is added for Application and on startup should be able to locate @

  http://localhost:8081/swagger-ui.html

  ** Replace port with the number configured in src/main/resources/application.yaml

  API endpoints:

    ** Replace port with the number configured in src/main/resources/application.yaml

  # Rolling-Average :

    - Path variables in the URL are prefixed with :

      http://localhost:62992/crypto/movingAverage/:fromDate/:toDate/:dayRollover

  # Get All for a Date Range:

     http://localhost:53426/crypto/historic/dateRange/:fromDate/:toDate


  # Get All for given number of Days:

     http://localhost:64652/crypto/historic/days/:days/

# How-To-Run:

  1. check JAVA_HOME, set it to JDK-8
  2. check MAVEN_HOME or M2_HOME , set it to maven-3

  3. Make sure that elastic-search instance is up & running on port 9300
      - For further details on how to install and run, refer the git project:
          ```
           Git Project page: https://github.com/kanthgithub/CryptoAnalytics
          ```
   4. navigate to the project directory (if you are running from command console)
   5. run command
      ```
       mvn clean install
       ```
   6. After successful build , start application :
      ```
      mvn spring-boot:run
      ```
   7. application will startup on random port, in case if you want to set to specific port:
      ```
      - open application.yaml file
      - update property:
        - server:
             port: 0
      ```



# Improvements Required:


1. Cucumber Tests for Scenarios identified in Usecases (Blocked by Embedded Elastic Engine in-compatibility with Spring)
2. Support for Elastic-Cluster (Multiple elastic nodes across Data-Centers)
3. Current system is limited by single node processing
   Current Systems does parallel processing but it is limited to number of cores/processors in the Machine
4. Distributed processing using AKKA - Actor based Programming:


# Alternative Approaches:

# AKKA:

   1. Build Actor-System where Supervisor / Root Guardian to spawn Child-Actors to process files in directory
   2. Sub-ordinate Actors Parse Lines and Create a sub-ordinate/Child Actor to process and load data to Elastic-Search-Engine
   3. By Shifting to Actor based approach, it will become a distributed System and Horizontally Scalable

# Redisson:
1. Redis Database is a key-Value based Storage
2. Redisson is a library/framework to achieve storage and processing in Redis in Distributed way
3. Redisson Library is built on JDK Concurrent utilities (java.util.concurrent)
4. Distributed Locks, Distributed

# Chronicle-IO:
1. Off-Heap Storage mechanism
2. Offers Mechanical Sympathy where process can be pinned to specific core of machine
3. Distributed Heap Offers faster read/write mechanisms

# DevOps Improvements:
 - Add Docker configuration
 - enable/configure Piplelines for Continuous Build, Delivery & Deployment
 - Use AWS - RDS (Relational Database As a Service) for scalable feature
 - Push Docker image to AWS and run from AWS

