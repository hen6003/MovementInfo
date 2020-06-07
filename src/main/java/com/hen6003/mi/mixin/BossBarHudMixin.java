package com.hen6003.mi.mixin;

import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.hen6003.mi.IBossBarHud;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;

@Mixin(BossBarHud.class)
class BossBarHudMixin implements IBossBarHud {
    @Shadow
    @Final
    private Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();
    
    @Override
    public int getBossBarsLength() {
        return bossBars.size();
    }
}