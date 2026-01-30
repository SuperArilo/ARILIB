package com.tty.lib.services.impl;

import com.tty.api.Log;
import com.tty.lib.services.NBTDataService;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.NBTFileHandle;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class NBTDataServiceImpl implements NBTDataService {

    @Override
    public NBTFileHandle getData(String playerUUID) {
        File playerDataFile = new File(new File(Bukkit.getServer().getWorldContainer(), "world") + "/playerdata/" + playerUUID + ".dat");
        try {
           return NBT.getFileHandle(playerDataFile);
        } catch (IOException e) {
            Log.error(e, "load file data {} error. ", playerUUID);
            return null;
        }
    }
}
