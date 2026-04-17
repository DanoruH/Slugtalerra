package com.slugtalerra;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.slugtalerra.interactions.BlasterInteraction;
import com.slugtalerra.interactions.ChargingMetadataInteraction;
import com.slugtalerra.interactions.InventoryModifyConditionalInteraction;
import com.slugtalerra.interactions.ItemsConditionInteraction;

import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class Game extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Game(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        this.getCodecRegistry(Interaction.CODEC).register("Blaster", BlasterInteraction.class, BlasterInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("ItemsCondition", ItemsConditionInteraction.class, ItemsConditionInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("InventoryModifyCondition", InventoryModifyConditionalInteraction.class, InventoryModifyConditionalInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("ChargedMetaData", ChargingMetadataInteraction.class, ChargingMetadataInteraction.CODEC);
    }
}