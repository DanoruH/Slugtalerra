package com.slugtalerra.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class InventoryModifyConditionalInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<InventoryModifyConditionalInteraction> CODEC;

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = interactionContext.getEntity();
        Store<EntityStore> store = interactionContext.getEntity().getStore();
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        Player player = store.getComponent(ref, Player.getComponentType());

        short slug_Ammo_Slot;
        String slug_Ammo_ID;
        ItemStack newBlaster;

        ItemStack itemHandled = interactionContext.getHeldItem();
        Inventory inventory = player.getInventory();
        ItemContainer itemContainer = inventory.getCombinedHotbarFirst();
        short capacity = itemContainer.getCapacity();
        slug_Ammo_Slot = getSlotItemID(capacity, itemContainer);
        slug_Ammo_ID = itemContainer.getItemStack(slug_Ammo_Slot).getItemId();

        BsonDocument metadata = itemHandled.getMetadata();
        if(metadata == null) {
            metadata = new BsonDocument();
        }

        if(slug_Ammo_Slot != -1) {
            itemContainer.removeItemStackFromSlot(slug_Ammo_Slot, 1);
            metadata.append("Slug", new BsonString(slug_Ammo_ID));
            metadata.append("Loaded", new BsonBoolean(true));
            newBlaster = itemHandled.withMetadata(metadata);
            player.getInventory().getHotbar().setItemStackForSlot(player.getInventory().getActiveHotbarSlot(),  newBlaster);
        } else {
            markFailed(interactionContext);
        }

    }

    private short getSlotItemID(short capacity, ItemContainer itemContainer) {
        for(short slot = 0; slot < capacity; slot++) {
            ItemStack item = itemContainer.getItemStack(slot);
            if(item != null && !item.isEmpty() && item.getItemId().startsWith("Ammo_")) {
                return slot;
            }
        }
        return -1;
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
        CODEC = BuilderCodec.builder(InventoryModifyConditionalInteraction.class, InventoryModifyConditionalInteraction::new, SimpleInstantInteraction.CODEC).build();
    }
}
