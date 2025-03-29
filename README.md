--

If you like using the programs feel free to support:

<h1 style="font-size: 36px;">Support this development.</h1>

* [Support me on Ko-fi](https://ko-fi.com/igearfs)

# 🌈 **JNLP Launcher – Beta v1.0** 🌈

⚠️ **WARNING: BACKUP YOUR `jnlp_entries.txt` FILE!** ⚠️  
Before running this version, **make a backup** of your JNLP entries! The universe is unpredictable, and while we’ve done our best to keep things smooth, bad juju can still sneak in. Protect your data before proceeding! 😱

---

📜 **Server Side Public License**  
📌 This project is licensed under the **Server Side Public License (SSPL) v1.0**.

---

## 📂 **Setup & Installation**

### **1️⃣ Build the Project**
This project uses **Maven** for dependency management and packaging.  
To build:
```bash
mvn clean package
```  
After a successful build, the runnable JAR will be located in the `target/` directory.

### **2️⃣ Run the Launcher**

#### **Option 1: Use System Java (Requires JVM 21+)**
```bash
java -jar .\target\jnlp-launcher-1.0-SNAPSHOT.jar
```  

#### **Option 2: Use the Bundled JRE (Windows Only)**
1. **Extract the provided `jre.rar`**
2. Place the extracted `jre` folder in the project directory
3. Run the launcher using the included Java runtime:
   ```bash
   .\jre\bin\java.exe -jar .\target\jnlp-launcher-1.0-SNAPSHOT.jar
   ```  

---

### **3️⃣ Generate the Icon List**
Before using the app, generate the icon list:
- Run **`icon.bat`**
- This creates `icons_list.txt`, ensuring icons load properly.

---

## 🌟 **Features**

✅ **Runs on JVM 17+ or Bundled JRE** – Choose your runtime.  
✅ **Add & Edit Entries** – Store JNLP launch info with name, URL, and notes.  
✅ **🔎 Search Functionality** – Instantly find entries by name or URL.  
✅ **🚀 One-Click Launch** – Click an entry to launch the JNLP app.  
✅ **🖼️ Grid-Based Icon Picker** – Choose from **4,000+ icons** with pagination.  
✅ **🎨 Icon Highlighting** – Selected icons get highlighted, making selection clear.  
✅ **📜 Persistent Storage** – Entries and icons persist between sessions.

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
        - **Windows**: `C:\Users\YourUsername\jnlp_entries.txt`
        - **Mac/Linux**: `/Users/YourUsername/jnlp_entries.txt`

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