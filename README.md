
Here’s a polished version of your README:  

---

# JNLP Launcher  

This project is a **Java-based launcher** that downloads and runs JAR applications defined in a JNLP file.  
It includes a **bundled JRE**, so users do not need to have Java installed system-wide.  

## Setup Instructions  

1. Extract the provided `jre.rar` — this will give you a portable JRE.  
2. Start the **Mirth Connect server** (or your JNLP server).  
3. Use Maven to build the project:  
   - Either through your IDE's Maven build tools  
   - Or by running:  
   ```bash  
   mvn clean package  
   ```  

This will create a jar in the `target` directory with dependencies included.  

## Running the Launcher  

Once built, run the launcher using the bundled JRE:  
```bash  
.\jre\bin\java.exe -jar target/jnlp-launcher-1.0-SNAPSHOT-jar-with-dependencies.jar https://localhost:8443/webstart  
```  

