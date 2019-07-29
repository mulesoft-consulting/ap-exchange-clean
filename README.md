## APExchangeClean
This is a java program that removes the specified project and version from Anypoint Exchange. This is used during the development process of Mule projects that are deployed and tested in RTF.

## Purpose
Remove a Mule project that has previously been published to RTF. This java program is added to the Mule projects deployment scripting as part of the Maven clean phase.

## Mule version
Runtime Fabric

## Integrations/frameworks used
Runtime Fabric deployments

### Prerequisites
No prerequistes.

## Features
Uses the Anypoint Platform API's to remove the Mule project from Exchange as described here:

https://support.mulesoft.com/s/article/How-to-remove-an-asset-from-Exchange-using-Exchange-Experience-API

## Command Line Example

```
java -jar APExchangeClean.jar clean %u% %p% 1674d8b0-3a4f-4bfb-8c70-89641e023735 artifactId artifactVersion
```

## Maven pom.xml Example

Used within a maven build section as a exec-maven-plugin execution. The build tag is usually specified under the exchange profile.

```
			<build>
				<plugins>
					<plugin>
						<groupId>org.codehaus.mojo</groupId>
						<artifactId>exec-maven-plugin</artifactId>
						<version>1.6.0</version>
						<executions>
							<execution>
								<id>Clean-Exchange-RTF</id>
								<phase>clean</phase>
								<goals>
									<goal>java</goal>
								</goals>
								<configuration>
									<includePluginDependencies>true</includePluginDependencies>
									<includeProjectDependencies>false</includeProjectDependencies>
									<mainClass>com.mulesoft.java.APExchangeClean</mainClass>
									<arguments>
										<argument>clean</argument>
										<argument>${u}</argument>
										<argument>${p}</argument>
										<argument>${my-organization-anypoint-orgid}</argument>
										<argument>${project.artifactId}</argument>
										<argument>${version}</argument>
									</arguments>
								</configuration>
							</execution>
						</executions>
						<dependencies>
							<dependency>
								<groupId>${my-organization-anypoint-orgid}</groupId>
								<artifactId>ap-exchange-clean</artifactId>
								<version>1.0.0</version>
								<type>jar</type>
							</dependency>
						</dependencies>
					</plugin>
				</plugins>
			</build>

``` 

## Java Command Line Example

The APExchangeClean is a java program, so it can be run from a console session. In order to run as in a Java command line, the APExchangeClean.jar file will need to be download to the workstation running the command. After downloading it, (for example, to the target folder), it can be run with this command:

```
java -jar target/APExchangeClean.jar clean myAnypointUser MyAnypointPassword myExchangeOrg artifactId artifactVersion
```

**myAnypointUser** is the Anypoint user that will be used to perform all the registration steps. Note that this tool will create any client applications that are listed in the my‑clients.json file. In doing so, the user specified here becomes the owner of record for the application and its client credentials...no other users will be able to see these applications except the master org owner. Using a consistent user name here is important in order to have consistent visibility of the credentials for all automated API registrations.

**MyAnypointPassword** is the password for the user specified above.

**myExchangeOrgId** is the Anypoint master organization id.

**artifactId** is the artifactId to delete from Exchange (as entered in the pom.xml file).

**artifactVersion** is the version of the artifact to delete from Exchange (as entered in the pom.xml file).

The APExchangeClean.jar must either be in the current running directory or on the Java classpath as a resource file. 

## API Reference
Uses Anypoint Platform Exchange Exerience API described here:

https://anypoint.mulesoft.com/exchange/portals/anypoint-platform/f1e97bc6-315a-4490-82a7-23abe036327a.anypoint-platform/exchange-experience-api/

