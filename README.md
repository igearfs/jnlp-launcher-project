
---

# JNLP Launcher

This project is a **Java-based launcher** that runs your JAR application. It includes a **bundled JRE** so users don't need to install Java separately. 
Extract the jre.rar (which is a compressed file)
Start Mirth Connect server.
use maven to Package the jar in your IDE or run maven

Run with:

.\jre\bin\java.exe -jar target/jnlp-launcher-1.0-SNAPSHOT-jar-with-dependencies.jar https://localhost:8443/webstart
