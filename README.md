
---

### ❤️ Support This Project

If you find this project useful or want to support continued development!

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/igearfs)

Your support fuels prototypes, uptime tools, and indie infrastructure builds.

---

# 🌈 **JNLP Launcher – v1.7.1**

Compatible with:
- ✅ Mirth Connect 4.5.2
- ✅ Bridgelink 4.5.2
- ✅ OIE 4.5.2

> ⚠️ **Important:** Back up your `jnlp_entries.txt` file before running this version. While stable, it's always good practice to safeguard your data against potential issues.

### 🔄 File Migration Notice (Windows Users)
Move your `jnlp_entries.txt` from the previous location to:
```
C:\Users\<username>\AppData\Roaming\SyncSyndicate\data\jnlp_entries.txt
```
If using the `.exe` installer, allow the app to create the directory on first run, then copy the file into the appropriate `data` folder.

---

## 📜 License

This project is licensed under the [GNU General Public License v3](https://www.gnu.org/licenses/gpl-3.0.html).

---

## 📦 Setup & Installation

### 1️⃣ Build the Project
This project uses Maven for dependency management.

```bash
mvn clean package
```

After building, the runnable JAR will appear in the `target/` directory.

---

### 2️⃣ Run the Launcher

#### Option 1: System Java (JVM 17+)
```bash
java -jar .\target\SyncSyndicate-launcher.jar
```

#### Option 2: Bundled JRE (Windows Only)
1. Extract `jre.rar`
2. Run using the embedded Java runtime:
   ```bash
   .\jre\bin\java.exe -jar .\target\SyncSyndicate-launcher.jar
   ```

#### Option 3: Windows EXE Installer
Install `SyncSyndicate-1.0.1.exe` (version may vary as updates are released).

---

### 3️⃣ Generate the Icon List

Before first use:
```bash
icon.bat
```
This generates `icons_list.txt` to ensure all icons load properly.

---

## 🌟 Features

- ✅ JVM 20+ support (required for jpackage)
- ✅ Add/Edit JNLP entries (name, URL, notes, credentials)
- ✅ Grid-based icon picker (4,000+ icons with pagination)
- ✅ Icon highlight for selection feedback
- ✅ Persistent storage of entries and icons
- ✅ Caching of JAR files by host (e.g. `localhost`)
- ✅ Auto-launch with stored username/password (uses system password manager)
- ✅ Corruption recovery with "Clear Cache" per instance
- ✅ Unsaved changes alert
- ✅ Visual loading spinner
- ✅ Error logging in `<USER_HOME>/logs`
- ✅ Verified compatibility with Mirth/Bridgelink/OIE forks

---

## 🚀 How to Use

### ➕ Add New Entry
- Click `+ Add`
- Fill out Name, URL, Notes (optional: username/password)
- Select an icon
- Save the entry

### 🔎 Search
- Use the search bar to filter by name or URL

### ✏️ Edit
- Select an entry
- Update details
- Save changes

### ❌ Delete
- Select an entry
- Click `Delete`

---

## 🧰 Technical Details

### Storage Locations

**JNLP Cache:**
- Windows: `%APPDATA%\SyncSyndicate\jnlp_cache`
- Mac/Linux: `~/.config/SyncSyndicate/jnlp_cache`

**Data File:**
- Windows: `%APPDATA%\SyncSyndicate\data\jnlp_entries.txt`
- Mac/Linux: `~/.config/SyncSyndicate/data/`

**Logs:**
- Windows: `%APPDATA%\SyncSyndicate\logs\app-<date>.txt`
- Mac/Linux: `~/.config/SyncSyndicate/logs/app-<date>.txt`

**Icons:**
- Pulled from `resources/icons/`
- Uses `icons_list.txt` for performance-aware loading and pagination

---

## ❓ Troubleshooting

### JNLP Not Launching?
- Ensure URL is reachable in a browser and links to a `.jnlp` file

### Icons Missing?
- Run `icon.bat` to regenerate `icons_list.txt`

### App Won’t Start?
- Make sure you're using JVM 20+ or the bundled JRE
- Check logs in the `logs/` directory for errors

---

## ⚠ Disclaimer

Use at your own risk.  
The developer(s) and associated organizations are **not responsible** for data loss, system failures, or any unintended behavior. Always **backup** your data before upgrading or using new versions.

---
