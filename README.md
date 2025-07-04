# A Minimal Maven-based Project Skeleton for using Bigraph Framework

This project provides a quick introduction on how to setup, configure and use the Bigraph Framework.

This main class `org.example.MainBigraphApplication` creates a signature and two bigraphs first, and performs the following operations afterwards:
- The two bigraphs are composed
- A reactive system is created, a matching conducted and the agent rewritten
- A bigraph is converted to the BigraphER specification language format


## Getting Started

This project is best worked with using IntelliJ IDEA:
- Open the project in IntelliJ IDEA
- Run the application

## Build Configuration 

This project works with Maven or Gradle for dependency management and building. 
Below are the instructions to configure and build the project using Maven or Gradle.

### Maven

#### Create a Fat-JAR / Uber-JAR

All the dependencies are included in the generated JAR.

```console
# Create the executable JAR
$ mvn clean package -PfatJar
# Execute the application
$ java -jar ./target/fatJar-empty-project-skeleton-bigraphframework-VERSION.jar
```

#### Classpath-Approach (1): Relative Libs-Folder

The necessary dependencies are installed in your local Maven repository, and also copied in a local folder next to the 
generated JAR and referred to at runtime.
That is, the classpath in the `MANIFEST.MF` is set to `libs/` (relative to the generated JAR).

```console
# Create the executable JAR
$ mvn clean install -PlocalLib
# Execute the application
$ java -jar ./target/localLib-empty-project-skeleton-bigraphframework-VERSION.jar
```

#### Classpath-Approach (2): Local Maven Repository

The necessary dependencies are installed in your local Maven repository, which is where the generated application refers to.
That is, the classpath in the `MANIFEST.MF` is set to `~/.m2/repository/`.

```console
# Create the executable JAR
$ mvn clean install -PlocalM2
# Execute the application
$ java -jar ./target/localM2-empty-project-skeleton-bigraphframework-VERSION.jar
```

### Gradle 

Verify that you have Gradle >= 8.4 installed:

```console
$ gradle -v
```

Run the following command to build the project:

```console
$ gradle build
```
