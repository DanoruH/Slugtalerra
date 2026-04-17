package com.slugtalerra.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.bson.BsonDocument;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ChargingMetadataInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<ChargingMetadataInteraction> CODEC;

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = interactionContext.getEntity();
        Store<EntityStore> store = interactionContext.getEntity().getStore();
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Player player = store.getComponent(ref, Player.getComponentType());

        ItemStack itemStack = interactionContext.getHeldItem();

        BsonDocument metadata = itemStack.getMetadata();
        if(metadata == null) return;

        String slugAmmoID = metadata.get("Slug").asString().getValue();
        boolean loaded = metadata.get("Loaded").asBoolean().getValue();

        if(slugAmmoID.equals("None") && !loaded) {
            markFailed(interactionContext);
        }
    }

    private void markFailed(@NonNullDecl InteractionContext context) {
        if(context.getState() != null) {
            context.getState().state = InteractionState.Failed;
        }

        if(context.getClientState() != null) {
            context.getClientState().state = InteractionState.Failed;
        }

        if(context.getServerState() != null) {
            context.getServerState().state = InteractionState.Failed;
        }
    }

    static {
        CODEC = BuilderCodec.builder(ChargingMetadataInteraction.class, ChargingMetadataInteraction::new, SimpleInstantInteraction.CODEC).build();
    }
}
