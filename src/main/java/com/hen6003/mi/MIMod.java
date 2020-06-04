package com.hen6003.mi;

import java.util.Timer;
import org.aperlambda.lambdacommon.utils.LambdaUtils;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.util.math.Vector3f;

public class MIMod implements ClientModInitializer {

    public static final String MOD_ID = "hen6003_mi";
    public static Vector3f playerSpeed = new Vector3f(0, 0, 0);
    public static Vector3f newBlockPos = new Vector3f(0, 0, 0);
    public static Vector3f oldBlockPos = new Vector3f(0, 0, 0);
    public static long newMilliTime = 0;
    public static long oldMilliTime = 0;
    public static int timer = 0;
    public static int cps = 0;

    @Override
    public void onInitializeClient() {
        new Timer().scheduleAtFixedRate(LambdaUtils.newTimerTaskFromLambda(() -> MIMod.cps = 0), 0, 1000);
    }
}