package com.slugtalerra.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.projectile.ProjectileModule;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import com.hypixel.hytale.server.core.util.TargetUtil;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;

public class BlasterInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<BlasterInteraction> CODEC =
            BuilderCodec.builder(BlasterInteraction.class, BlasterInteraction::new, SimpleInstantInteraction.CODEC).build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        Store<EntityStore> store = interactionContext.getEntity().getStore();
        Ref<EntityStore> ref = interactionContext.getEntity();
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Player player = store.getComponent(ref, Player.getComponentType());

        String slugAmmo = "";
        boolean isLoaded;
        ItemStack newBlaster = null;
        InteractionSyncData clientState = interactionContext.getClientState();

        boolean hasClientState = clientState != null && clientState.attackerPos != null && clientState.attackerRot != null;
        Vector3d position;
        Vector3d direction;
        UUID uuid;

        ItemStack itemHandled = interactionContext.getHeldItem();
        BsonDocument metadata = itemHandled.getMetadata();
        if(metadata == null && metadata.isEmpty()) return;
        if(metadata.containsKey("Slug")) {
            slugAmmo =  metadata.get("Slug").asString().getValue();
        }

        metadata.append("Slug", new BsonString("None"));
        metadata.append("Loaded", new BsonBoolean(false));
        newBlaster = itemHandled.withMetadata(metadata);
        player.getInventory().getHotbar().setItemStackForSlot(player.getInventory().getActiveHotbarSlot(),  newBlaster);

        String idProjectile = slugAmmo + "_Projectile";

        if(hasClientState) {
            position = PositionUtil.toVector3d(clientState.attackerPos);
            Vector3f lookVec = PositionUtil.toRotation(clientState.attackerRot);
            direction = new Vector3d(lookVec.getYaw(), lookVec.getPitch());
            uuid = clientState.generatedUUID;
            if(!LaunchProjectile(ref, commandBuffer, idProjectile, position, direction, uuid)) {
                player.sendMessage(Message.raw("Error..."));
            }
        } else {
            Transform lookVec = TargetUtil.getLook(ref, commandBuffer);
            position = lookVec.getPosition();
            direction = lookVec.getDirection();
            uuid = null;
            if(!LaunchProjectile(ref, commandBuffer, idProjectile, position, direction, uuid)) {
                player.sendMessage(Message.raw("Error..."));
            }
        }

    }

    public boolean LaunchProjectile(Ref<EntityStore> ref, CommandBuffer<EntityStore> commandBuffer, String projectileConfig, Vector3d position, Vector3d direction, UUID uuid) {
        ProjectileConfig projectile = ProjectileConfig.getAssetMap().getAsset(projectileConfig);
        if(projectile == null) return false;

        ProjectileModule.get().spawnProjectile(uuid, ref, commandBuffer, projectile, position, direction);
        return true;
    }
}
