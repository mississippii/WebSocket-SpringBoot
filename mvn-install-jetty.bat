for %%F in (lib\jetty-12\*.jar) do mvn install:install-file -Dfile=%%F -DgroupId=org.eclipse.jetty.websocket -DartifactId=%%~nF -Dversion=12.0.15 -Dpackaging=jar
