package com.hen6003.mi.mixin;

import com.hen6003.mi.IBossBarHud;
import com.hen6003.mi.MICommands;
import com.hen6003.mi.MIMod;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.block.SlimeBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

	@Inject(at = @At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.defaultAlphaFunc()V"), method = "render", locals = LocalCapture.CAPTURE_FAILSOFT)
	public void render(float float_1, long long_1, boolean boolean_1, CallbackInfo info, int i, int j, Window window, MatrixStack matrixStack){

		MinecraftClient client = MinecraftClient.getInstance();
		PlayerEntity playerEntity = client.player;

		int bossBars = ((IBossBarHud) client.inGameHud.getBossBarHud()).getBossBarsLength();

		if (!client.options.debugEnabled & MICommands.config.showHud & bossBars < 1 & MinecraftClient.isHudEnabled()) {
			RenderSystem.pushMatrix();
			String miString = "";


			if (MIMod.timer == 30){ //only runs once every 30 frames
				MIMod.oldMilliTime = MIMod.newMilliTime; //gets time changed
				MIMod.newMilliTime = Util.getMeasuringTimeMs();

				MIMod.oldBlockPos = MIMod.newBlockPos;
				MIMod.newBlockPos = new Vector3f((float)(playerEntity.getX()), (float)(playerEntity.getY()), (float)(playerEntity.getZ())); //gets player cords

				Vector3f diffBlockPos = new Vector3f();
				diffBlockPos.set(MIMod.newBlockPos.getX(), MIMod.newBlockPos.getY(), MIMod.newBlockPos.getZ()); //sets diffBLocksPos to newBlockPos
				diffBlockPos.subtract(MIMod.oldBlockPos); //subtracts oldBlockPos
				MIMod.playerSpeed = diffBlockPos; //player speed equals vector changed
				float timeDelta = (MIMod.newMilliTime - MIMod.oldMilliTime) / 1000f; //gets delta

				if (timeDelta != 0){ //fixes dividing by 0 at start of game
					MIMod.playerSpeed.set((int)(MIMod.playerSpeed.getX() / timeDelta), (int)(MIMod.playerSpeed.getY() / timeDelta), (int)(MIMod.playerSpeed.getZ() / timeDelta));
				}
				MIMod.timer = 0;
			} else {
				MIMod.timer += 1;
			}

			Vector3f tempVector = new Vector3f(MIMod.playerSpeed.getX(), MIMod.playerSpeed.getY(), MIMod.playerSpeed.getZ());

			int playerBPS = (int)(Math.sqrt(tempVector.dot(tempVector))); //gets BPS


			Block standingOnBlock = playerEntity.world.getBlockState(new BlockPos(MIMod.newBlockPos.getX(), MIMod.newBlockPos.getY() - 1f, MIMod.newBlockPos.getZ())).getBlock();
			float slipperiness = standingOnBlock.getSlipperiness();
			if (slipperiness > 0.6f){ //normal block 'slipperiness' equals 0.6
				miString += "[Sliding]";
			}

			if (playerEntity.isBlocking()){ //if player's using there shield 
				miString += "[Blocking]";
			}
			if (playerEntity.isTouchingWater()){ 
				miString += "[Swimming]";
			} else if (playerEntity.isSprinting()){
				miString += "[Sprinting]";
			}
			if (playerEntity.isSneaking()){
				miString += "[Sneaking]";
			}

			if (playerEntity.getVehicle() != null){ //if their riding something
				String name = playerEntity.getVehicle().getDisplayName().asString(); //gets riden entity name 
				if (name == ""){ //if entity has no name
					 name = playerEntity.getVehicle().getClass().getSimpleName(); //gets entitys class name
					name = name.substring(0, name.length() - 6); //removes 'Entity' from the end
				}

				miString += "[Riding: " + name + "]";

				if (!playerEntity.getVehicle().isOnGround() & !playerEntity.getVehicle().isSubmergedInWater()){
					miString += "[Jumping]";
				}
			} else if (playerEntity.abilities.flying){
				miString += "[Flying]";
			} else if (playerEntity.isFallFlying()){ //is using an elytra
				miString += "[Flying]";
			} else if (playerEntity.isClimbing()){
				miString += "[Climbing]";
			} else if (!playerEntity.isOnGround() & !playerEntity.isSwimming() & standingOnBlock.getClass() != SlimeBlock.class){
				miString += "[Jumping]";
			}

			if (playerEntity.getHungerManager().getFoodLevel() <= 6){ //low hunger so player can't run
				miString += "[LowHunger]";
			}

			Integer gameMode = client.interactionManager.getCurrentGameMode().getId(); //gets id of gamemode

			if (gameMode == 1){
				miString += "[Creative]";
			} else if (gameMode == 2){
				miString += "[Adventure]";
			} else if (gameMode == 3){
				miString += "[Spectator]";
			}

			if (playerEntity.isSleeping()){
				miString += "[Sleeping]";
			}

			if (playerEntity.isOnFire()){
				miString += "[OnFire]";
			}

			String psString = "";


			if (playerBPS != 0 & !MICommands.config.hideBps){
				psString = "[" + MICommands.config.otherBps + + playerBPS + "]";
			}

			if (MIMod.cps != 0 & !MICommands.config.hideCps){
				psString += "[" + MICommands.config.otherCps + MIMod.cps + "]";
			}
			

			float textPosX = 5;
			float bpsTextPosX = 5;

			if (MICommands.config.align == 1){ //set where text shows up
				textPosX = (client.getWindow().getScaledWidth() - client.textRenderer.getWidth(miString)) / 2f;
				bpsTextPosX = (client.getWindow().getScaledWidth() - client.textRenderer.getWidth(psString)) / 2f;
			}
			if (MICommands.config.align == 2){
				textPosX = client.getWindow().getScaledWidth() - client.textRenderer.getWidth(miString) - textPosX;
				bpsTextPosX = client.getWindow().getScaledWidth() - client.textRenderer.getWidth(psString) - bpsTextPosX;
			}

			if (!MICommands.config.onlyPs){ //print text to screen
				client.textRenderer.drawWithShadow(matrixStack, miString, textPosX, 5, MICommands.config.hudColor);
				client.textRenderer.drawWithShadow(matrixStack, psString, bpsTextPosX, 15, MICommands.config.hudColor);
			} else {
				client.textRenderer.drawWithShadow(matrixStack, psString, bpsTextPosX, 5, MICommands.config.hudColor);
			}

			RenderSystem.popMatrix();
		}
	}
}