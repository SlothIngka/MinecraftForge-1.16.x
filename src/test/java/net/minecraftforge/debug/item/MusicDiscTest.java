/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.item;

import net.minecraft.item.Item;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(MusicDiscTest.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MusicDiscTest.MOD_ID)
public class MusicDiscTest
{
    static final String MOD_ID = "music_disc_test";

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);

    private static final RegistryObject<SoundEvent> TEST_SOUND_EVENT = SOUND_EVENTS.register("test_sound_event",
            () -> new SoundEvent(new ResourceLocation(MOD_ID, "test_sound_event")));

    private static final RegistryObject<Item> TEST_MUSIC_DISC = ITEMS.register("test_music_disc",
            () -> new MusicDiscItem(1, TEST_SOUND_EVENT, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public MusicDiscTest()
    {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        SOUND_EVENTS.register(modBus);
    }
}
