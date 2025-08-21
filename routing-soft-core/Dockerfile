# Use Tomcat with JDK 17
FROM tomcat:10.1-jdk17

# Remove default ROOT app
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR to Tomcat
COPY target/routing.war /usr/local/tomcat/webapps/ROOT.war

# Ensure Tomcat listens on all interfaces
RUN sed -i 's/address="localhost"/address="0.0.0.0"/' /usr/local/tomcat/conf/server.xml

# Expose Tomcat port
EXPOSE 8080

# Add entrypoint script that will load secrets into env vars
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

ENTRYPOINT ["docker-entrypoint.sh"]
CMD ["catalina.sh", "run"]

