package com.tty.lib.services;


import de.tr7zw.nbtapi.iface.NBTFileHandle;

public interface NBTDataService {
    NBTFileHandle getData(String playerUUID);
}
