/*VERIFICA SI HAY UN ITEM QUE INICIE CON AMMO_ Y LUEGO LO REMUEVE DEL INVENTARIO (TEMPORAL)*/

package com.slugtalerra.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
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
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ItemsConditionInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<ItemsConditionInteraction> CODEC;

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NonNullDecl CooldownHandler cooldownHandler) {
        Store<EntityStore> store = interactionContext.getEntity().getStore();
        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = store.getComponent(ref, Player.getComponentType());

        //SECCIÓN 1
        ItemStack itemHandled = interactionContext.getHeldItem();
        if(itemHandled == null && itemHandled.isEmpty()) return;

        //SECCIÓN 2
        Inventory inventory = player.getInventory();
        ItemContainer itemContainer = inventory.getCombinedHotbarFirst();
        short capacity = itemContainer.getCapacity();

        boolean isItemExists = isItemsExist(capacity, itemContainer);

        if(!isItemExists) {
            markFailed(interactionContext);
        }
    }

    private boolean isItemsExist(short capacity, ItemContainer itemContainer) {
        for(short slot = 0; slot < capacity; slot++) {
            ItemStack item = itemContainer.getItemStack(slot);
            if(item != null && !item.isEmpty() && item.getItemId().startsWith("Ammo_")) {
                return true;
            }
        }
        return false;
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
        CODEC = BuilderCodec.builder(ItemsConditionInteraction.class, ItemsConditionInteraction::new, SimpleInstantInteraction.CODEC).build();
    }
}
