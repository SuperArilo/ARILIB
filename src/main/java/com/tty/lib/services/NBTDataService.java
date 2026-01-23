package com.tty.lib.services;


import de.tr7zw.nbtapi.iface.NBTFileHandle;

import javax.annotation.Nullable;

public interface NBTDataService {
    @Nullable NBTFileHandle getData(String playerUUID);
}
