package com.hen6003.mi.mixin;

import com.hen6003.mi.MICommands;
import com.hen6003.mi.MIMod;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(at = @At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.defaultAlphaFunc()V"), method = "render")
	public void render(float float_1, long long_1, boolean boolean_1, CallbackInfo info) {

		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity playerEntity = (PlayerEntity) client.player;

		if(!client.options.debugEnabled & MICommands.config.showHud) {
			RenderSystem.pushMatrix();
			String miString = "";

			if (playerEntity.isBlocking()) {
				miString += "[Blocking]";
			}
			if (playerEntity.isTouchingWater()) {
				miString += "[Swimming]";
			} else if (playerEntity.isSprinting()) {
				miString += "[Sprinting]";
			}
			if (playerEntity.isSneaking()) {
				miString += "[Sneaking]";
			}

			if (playerEntity.getVehicle() != null){
			miString += "[" + playerEntity.getVehicle().getDisplayName().asFormattedString() + "]";
				
				if (!playerEntity.getVehicle().onGround & !playerEntity.getVehicle().isSubmergedInWater()){
				miString += "[Jumping]";
				}
			} else if (playerEntity.abilities.flying){
				miString += "[Flying]";
			} else if (playerEntity.isFallFlying()){
				miString += "[Flying]";
			} else if (playerEntity.isClimbing()){
				miString += "[Climbing]";
			} else if (!playerEntity.onGround & !playerEntity.isSwimming()) {
				miString += "[Jumping]";
			}

			if (MIMod.timer == 30){
				MIMod.oldMilliTime = MIMod.newMilliTime;
				MIMod.newMilliTime = Util.getMeasuringTimeMs();

				MIMod.oldBlockPos = MIMod.newBlockPos;
				MIMod.newBlockPos = new Vector3f((float)(playerEntity.getX()), (float)(playerEntity.getY()), (float)(playerEntity.getZ()));

				Vector3f diffBlockPos = new Vector3f();
				diffBlockPos.set(MIMod.newBlockPos.getX(), MIMod.newBlockPos.getY(), MIMod.newBlockPos.getZ());
				diffBlockPos.subtract(MIMod.oldBlockPos);
				MIMod.playerSpeed = diffBlockPos;
				float timeDelta = (MIMod.newMilliTime - MIMod.oldMilliTime) / 1000f;
				System.out.println(timeDelta);
				System.out.println("pos: " + MIMod.newBlockPos);

				if (timeDelta != 0){
					MIMod.playerSpeed.set((int)(MIMod.playerSpeed.getX() / timeDelta), (int)(MIMod.playerSpeed.getY() / timeDelta), (int)(MIMod.playerSpeed.getZ() / timeDelta));
				}
				MIMod.timer = 0;
			} else {
				MIMod.timer += 1;
			}

			Vector3f tempVector = new Vector3f(MIMod.playerSpeed.getX(), MIMod.playerSpeed.getY(), MIMod.playerSpeed.getZ());

			int playerBPS = (int)(Math.sqrt(tempVector.dot(tempVector)));

			String bpsString = "";

			if (playerBPS != 0){
				bpsString = "[BPS:" + playerBPS + "]";
			}

			float textPosX = 5;
			float bpsTextPosX = 5;

			if (MICommands.config.align == 1) {
				textPosX = (client.getWindow().getScaledWidth() - client.textRenderer.getStringWidth(miString)) / 2f;
				bpsTextPosX = (client.getWindow().getScaledWidth() - client.textRenderer.getStringWidth(bpsString)) / 2f;
			}
			if (MICommands.config.align == 2) {
				textPosX = client.getWindow().getScaledWidth() - client.textRenderer.getStringWidth(miString) - textPosX;
				bpsTextPosX = client.getWindow().getScaledWidth() - client.textRenderer.getStringWidth(bpsString) - bpsTextPosX;
			}

			client.textRenderer.drawWithShadow(miString, textPosX, 5, MICommands.config.hudColor);
			client.textRenderer.drawWithShadow(bpsString, bpsTextPosX, 15, MICommands.config.hudColor);
			RenderSystem.popMatrix();
		}
	}
}