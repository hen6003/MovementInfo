package com.hen6003.mi;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MIConfig {

    public boolean showHud = true;

    public int hudColor = 0xeeeeee;

    public int align = 1;

    public void saveConfig() {
        String configPath = FabricLoader.getInstance().getConfigDirectory() + "/" + MIMod.MOD_ID + ".json";
        File configFile = new File(configPath);
        String result = new Gson().toJson(this);
        try {
            FileOutputStream out = new FileOutputStream(configFile, false);

            out.write(result.getBytes());
            out.flush();
            out.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}