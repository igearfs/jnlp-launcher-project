--

If you like using the programs feel free to support:

<h1 style="font-size: 36px;">Support this development.</h1>

* [Support me on Ko-fi](https://ko-fi.com/igearfs)

# 🌈 **JNLP Launcher – Beta v1.0** 🌈

* Works with Mirth Connect 4.5.2 

⚠️ **WARNING: BACKUP YOUR `jnlp_entries.txt` FILE!** ⚠️  
Before running this version, **make a backup** of your JNLP entries! The universe is unpredictable, and while we’ve done our best to keep things smooth, bad juju can still sneak in. Protect your data before proceeding! 😱

⚠️ ** MOVE YOUR FILE INTO THE DATA DIRECTORY FROM THE OLD PATH ON WINDOWS to: C:\Users\<username>\AppData\Roaming\SyncSyndicate\data\jnlp_entries.txt INTO THE DATA FOLDER IN THE PROJECT **
⚠️ ️ ** If you use the .exe installer let it make the directory for you after the first run and replace or drop in your jnlp_entries.txt into the correct folder. (may be different for installer just look in roaming folders for SyncSyndicate)

---

📜 **GNU GENERAL PUBLIC LICENSE Version 3**  
📌 This project is licensed under the **GNU GENERAL PUBLIC LICENSE Version 3**.

---

⚠️ WARNING: As always, use at your own risk! ⚠️
By downloading, installing, or running this program, you acknowledge that you are doing so at your own risk. 
The developer(S)/company is not responsible for any issues, data loss, or damage that may occur as a result of using this software. 
Please proceed with caution and make sure to back up your files!

## 📂 **Setup & Installation**

### **1️⃣ Build the Project**
This project uses **Maven** for dependency management and packaging.  
To build:
```bash
mvn clean package
```  
After a successful build, the runnable JAR will be located in the `target/` directory.

### **2️⃣ Run the Launcher**

#### **Option 1: Use System Java (Requires JVM 17+)**
```bash
java -jar .\target\SyncSyndicate-launcher.jar
```  

#### **Option 2: Use the Bundled JRE (Windows Only)**
1. **Extract the provided `jre.rar`**
2. Run the launcher using the included Java runtime:
   ```bash
   .\jre\bin\java.exe -jar .\target\SyncSyndicate-launcher.jar
   ```  
#### **Option 3: use the exe generated (Windows Only 64 bit systems)*
1. Install the generated exe file. SyncSyndicate-1.0.1.exe <-- version may be different as bugs are squashed...

---

### **3️⃣ Generate the Icon List**
Before using the app, generate the icon list:
- Run **`icon.bat`**
- This creates `icons_list.txt`, ensuring icons load properly.

---

## 🌟 **Features**

✅ **Runs on JVM 20+ or Bundled JRE** – Choose your runtime.  
✅ **Add & Edit Entries** – Store JNLP launch info with name, URL, and notes.  
✅ **🔎 Search Functionality** – Instantly find entries by name or URL.    
✅ **🖼️ Grid-Based Icon Picker** – Choose from **4,000+ icons** with pagination.  
✅ **🎨 Icon Highlighting** – Selected icons get highlighted, making selection clear.  
✅ **📜 Persistent Storage** – Entries and icons persist between sessions.
✅ **📜 Caches jarfile buy hostname** – In cache folder you will see things like localhost.
✅ **📜 Works with Mirth Connect 4.5.2 **
✅ **📜 Works with current Bridgelink fork of Mirth Connect 4.5.2 **

---

## 🚀 **How to Use**

### **📌 Adding a New Entry**
🟢 Click `+ Add`  
🟢 Fill in Name, URL, and Notes  
🟢 Click `Select Icon` → Choose an icon → Click `Done`  
🟢 Click `Save`

### **🔎 Searching for an Entry**
🔹 Use the **search bar** at the top of the list  
🔹 Type a **name or URL** to filter results  
🔹 Click an entry to view/edit details

### **🔧 Editing an Entry**
🟡 Click an entry in the list  
🟡 Update fields as needed  
🟡 Click `Select Icon` to change the icon  
🟡 Click `Save`

### **❌ Deleting an Entry**
🔴 Select an entry  
🔴 Click `Delete`

---

## ⚙ **Technical Details**

- **Data Storage**:
    - Entries are saved to `jnlp_entries.txt` in the **user's home directory**.
    - Example locations:
        - **Windows**: `.\data\jnlp_entries.txt`
        - **Mac/Linux**: `./data/jnlp_entries.txt`

- **Icon Management**:
    - Icons are loaded from `resources/icons/` based on `icons_list.txt`.
    - The grid-based selection system efficiently paginates to prevent performance issues.

---

## ❓ **Troubleshooting**

### 🔹 **JNLP URL Not Working?**
- Double-check that the **URL is correct and accessible**.
- Open the URL in a browser to see if it prompts a JNLP download.

### 🔹 **Icons Not Showing?**
- Ensure `icons_list.txt` is present in `resources/`.
- Run `icon.bat` to regenerate the list.

### 🔹 **App Fails to Launch?**
- Make sure you are using **JVM 21+** or the **included JRE** (Windows only).
- Check the console for errors and missing dependencies.

---

💡 **Enjoy the new features and let us know if you run into any issues!** 🚀