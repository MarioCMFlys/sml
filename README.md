# Simple Minecraft Launcher
SML is a simple Minecraft launcher which was designed with the creation of modpacks in mind.

## Features
 * Easy to use
 * Works on Windows, Mac OSX, and Linux
 * Created-provided manifest system
 * Authors can push updates to their users instantly

## System Requirements
 * At least 200MB of system memory
 * Java 1.8 or later

## Installation
To install SML, download and run the launcher. All game and config files are stored in a directory called `.sml` in the user's home directory.

## Manifest Format
The manifest format provides a straightforward way to distribute modpacks and has many advantages:
 * Eliminates the need for distributing large zip files
 * Tiny text file compared to a monolithic 100+ megabyte zip file
 * Human readable
 * Can download files directly from their official source
### Example Manifest
```json
{
  "manifest_version": 1,
  "id": "me.authorname.profilename",
  "display_name": "Name of Profile",
  "author": "Author Name Here",
  "version": "1.0.0",

  "mc_version": "mc_version_here",
  "tweaks": ["forge"],

  "files": [
    {
      "path": "mods/Mod.jar",
      "download": "https://path/to/Mod.jar",
    },
    {
      "path": "config/modconfig.cfg",
      "download": "https://path/to/modconfig.cfg",
    }
  ],

  "upgrade": [],

  "mainClass": "net.minecraft.launchwrapper.Launch",
  "args": [
    "--username", 
    "$USERNAME", 
    "--version", 
    "$VERSION", 
    "--gameDir", 
    "$GAMEDIR", 
    "-assetsDir", 
    "$ASSETSDIR", 
    "--assetIndex", 
    "$ASSETINDEX", 
    "--uuid", "$USER_UUID", 
    "--accessToken", 
    "$USER_TOKEN", 
    "--userType", 
    "$USER_TYPE", 
    "--userProperties", 
    "$USER_PROP", 
    "--tweakClass", 
    "cpw.mods.fml.common.launcher.FMLTweaker"
  ],
  
  "assets": {
    "id": "mc_version_here",
    "url": "https://path/to/versionMeta.json"
  },

  "downloads": {
    "version": "mc_version_here",
    "client": "https://path/to/client.jar",
    "server": "https://path/to/server.jar",
    "windows_server": "https://path/to/windows_server.exe"
  },
  "libraries": [
    {"native": false, "universal": "https://path.to.a/library.jar"},
    {
      "native": true,
      "native-win32": "https://path.to.win32/binaries.jar",
      "native-win64": "https://path.to.win64/binaries.jar",
      "native-osx": "https://path.to.osx/binaries.jar",
      "native-linux": "https://path.to.linux/binaries.jar",
      "rules": {"os": ["windows", "osx", "linux"]}
    }
  ]
}
```
