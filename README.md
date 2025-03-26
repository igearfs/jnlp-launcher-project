---
📜 Server Side Public License

📌 This project is licensed under the Server Side Public License (SSPL) v1.0.

------------------------------------------------------------------------------------

# JNLP Launcher

This project is a **Java-based launcher** that downloads and runs JAR applications defined in a JNLP file.  
It includes a **bundled JRE**, so users do not need to have Java installed system-wide.

## Setup Instructions

NOTE: The included JRE is for **Windows**. For other OSes, please download the appropriate JRE from [Adoptium](https://adoptium.net/temurin/releases/).  
Once downloaded, extract the JRE and rename the folder to `jre`. Place it in the main project directory.

### Steps:
1. **Extract the provided `jre.rar`** — this will give you a portable JRE (for Windows currently).
2. **Start the JNLP server** (e.g., Mirth Connect or your own server).
3. **Use Maven to build the project**:
   - Either through your IDE's Maven build tools
   - Or by running the following command in your terminal:
   ```bash
   mvn clean package
   ```
   This will create a JAR file in the `target` directory with all dependencies included.

## Running the Launcher

Once the project is built, you can run the launcher using the bundled JRE:

### Command:
```bash
.\jre\bin\java.exe -jar .\target\jnlp-launcher-1.0-SNAPSHOT.jar
```

### How to Use:
- The **JNLP Launcher** allows you to add, save, and launch entries defined by a name and a JNLP URL.
- The launcher saves the entries to a file located in your **user's home directory** (`jnlp_entries.txt`).
- You can update the saved entries or add new ones, and the launcher will automatically update the file.

  **Note**: The `jnlp_entries.txt` file is stored in your home directory to ensure data persistence, even if the cache is cleared.

## Features:
- **Add New Entries**: Manually add a new JNLP entry with a name and URL.
- **Launch Entries**: Select and launch the defined JNLP entries, which will automatically trust the server certificate for secure connections.
- **Save and Update Entries**: Modify the name or URL of existing entries and save them back to the `jnlp_entries.txt` file.

### File Location:
- The `jnlp_entries.txt` file is stored in the **user's home directory**. This ensures that even if the application cache is cleared, your entries are saved.

Example location:
- **Windows**: `C:\Users\YourUsername\jnlp_entries.txt`
- **Mac/Linux**: `/Users/YourUsername/jnlp_entries.txt`

### How the Data is Managed:
- All JNLP entries are loaded from and saved to the `jnlp_entries.txt` file.
- Each entry consists of a **name** and a **URL** (the URL points to the JNLP file that defines the application to launch).

## Troubleshooting:

- If the application fails to launch or you encounter an issue with a JNLP URL, ensure that the URL is correct and accessible.
- You can check the `jnlp_entries.txt` file in your home directory to confirm the entries are saved properly.

---
