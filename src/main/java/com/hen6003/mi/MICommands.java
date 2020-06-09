package com.hen6003.mi;

import static com.hen6003.mi.MIMod.MOD_ID;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import io.github.cottonmc.clientcommands.ArgumentBuilders;
import io.github.cottonmc.clientcommands.ClientCommandPlugin;
import io.github.cottonmc.clientcommands.CottonClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class MICommands implements ClientCommandPlugin {

    public static MIConfig config;
    File runDirectory = MinecraftClient.getInstance().runDirectory;
    MinecraftClient client = MinecraftClient.getInstance();
    PlayerEntity playerEntity = (PlayerEntity) client.player;

    @Override
    public void registerCommands(CommandDispatcher<CottonClientCommandSource> commandDispatcher) {
        String configPath = runDirectory + "/config" + MOD_ID + ".json";

        Gson gson = new Gson();

        File configFile = new File(configPath);

        if(!configFile.exists()) {
            config = new MIConfig();
            String result = gson.toJson(config);
            try {
                FileOutputStream out = new FileOutputStream(configFile, false);

                out.write(result.getBytes());
                out.flush();
                out.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else {

            try {
                config = gson.fromJson( new FileReader(configFile), MIConfig.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                config = (config == null? new MIConfig() : config);
            }
        }


        commandDispatcher.register(ArgumentBuilders.literal("mitoggle")
            .executes(context -> {
                config.showHud = !config.showHud;
                config.saveConfig();
                return 1;
            }));


        commandDispatcher.register(ArgumentBuilders.literal("mionlyps")
            .executes(context -> {
                config.onlyPs = !config.onlyPs;
                config.saveConfig();
                return 1;
            }));

        commandDispatcher.register(ArgumentBuilders.literal("miotherps")
            .executes(context -> {
                config.otherPs = !config.otherPs;
                config.saveConfig();
                return 1;
            }));


        commandDispatcher.register(ArgumentBuilders.literal("micolour")
            .then(ArgumentBuilders.argument("r", IntegerArgumentType.integer())
                    .then(ArgumentBuilders.argument("g", IntegerArgumentType.integer())
                            .then(ArgumentBuilders.argument("b", IntegerArgumentType.integer())
                                    .executes(context -> {
                                        int r = IntegerArgumentType.getInteger(context,"r");
                                        int g = IntegerArgumentType.getInteger(context,"g");
                                        int b = IntegerArgumentType.getInteger(context,"b");

                                        config.hudColor = b + (g << 8) + (r << 16);
                                        config.saveConfig();
                                        return 1;
                                    })))));

        commandDispatcher.register(ArgumentBuilders.literal("mireset")
            .executes(context -> {
                config = new MIConfig();
                config.saveConfig();
                return 1;
            }));

        commandDispatcher.register(ArgumentBuilders.literal("mihelp")
            .executes(context -> {
                String helpMsg = "======== Movement Info Help ========\nmihelp -> Shows help\nmitoggle -> Toggles mod On/Off\nmips -> Toggles showing BPS/CPS only\nmialign <left|center|right> -> Changes position of text\nmicolour <r> <g> <b> -> Change text colour\nmireset -> Resets config";

                final Text text = new LiteralText(helpMsg).formatted();
                client.inGameHud.getChatHud().addMessage(text);
                return 1;
            }));

        commandDispatcher.register(ArgumentBuilders.literal("mialign")
            .then(ArgumentBuilders.literal("left")
                    .executes(context -> {
                        config.align = 0;
                        config.saveConfig();
                        return 1;
                    }))
            .then(ArgumentBuilders.literal("center")
                    .executes(context -> {
                        config.align = 1;
                        config.saveConfig();
                        return 1;
                    }))
            .then(ArgumentBuilders.literal("right")
                    .executes(context -> {
                        config.align = 2;
                        config.saveConfig();
                        return 1;
                    })));
    }
}
