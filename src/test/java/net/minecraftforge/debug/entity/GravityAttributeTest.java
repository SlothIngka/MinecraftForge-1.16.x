/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraftforge.common.ForgeMod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Multimap;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Rarity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraft.item.Item.Properties;

@Mod("gravity_attribute_test")
public class GravityAttributeTest
{
    public static final boolean ENABLE = false;
    private static Logger logger = LogManager.getLogger();
    private int ticks;
    private static final UUID REDUCED_GRAVITY_ID = UUID.fromString("DEB06000-7979-4242-8888-00000DEB0600");
    private static final AttributeModifier REDUCED_GRAVITY = (new AttributeModifier(REDUCED_GRAVITY_ID, "Reduced gravity", (double)-0.80, Operation.MULTIPLY_TOTAL));


    public GravityAttributeTest()
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
        }
    }

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event)
    {
        if (!event.world.isClientSide)
        {
            if (ticks++ > 60)
            {
                ticks = 0;
                World w = event.world;
                List<LivingEntity> list;
                if(w.isClientSide)
                {
                    ClientWorld cw = (ClientWorld)w;
                    list = new ArrayList<>(100);
                    for(Entity e : cw.entitiesForRendering())
                    {
                        if(e.isAlive() && e instanceof LivingEntity)
                            list.add((LivingEntity)e);
                    }
                }
                else
                {
                    ServerWorld sw = (ServerWorld)w;
                    Stream<LivingEntity> s = sw.getEntities().filter(Entity::isAlive).filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity)e);
                    list = s.collect(Collectors.toList());
                }

                for(LivingEntity liv : list)
                {
                    ModifiableAttributeInstance grav = liv.getAttribute(ForgeMod.ENTITY_GRAVITY.get());
                    boolean inPlains = liv.level.getBiome(liv.blockPosition()).getBiomeCategory() == Category.PLAINS;
                    if (inPlains && !grav.hasModifier(REDUCED_GRAVITY))
                    {
                        logger.info("Granted low gravity to Entity: {}", liv);
                        grav.addTransientModifier(REDUCED_GRAVITY);
                    }
                    else if (!inPlains && grav.hasModifier(REDUCED_GRAVITY))
                    {
                        logger.info("Removed low gravity from Entity: {}", liv);
                        grav.removeModifier(REDUCED_GRAVITY);
                    }
                }
            }

        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemGravityStick(new Item.Properties().tab(ItemGroup.TAB_TOOLS).rarity(Rarity.RARE)).setRegistryName("gravity_attribute_test:gravity_stick"));
    }

    public static class ItemGravityStick extends Item
    {
        private static final UUID GRAVITY_MODIFIER = UUID.fromString("DEB06001-7979-4242-8888-10000DEB0601");

        public ItemGravityStick(Properties properties)
        {
            super(properties);
        }

        @Override
        public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType slot)
        {
            @SuppressWarnings("deprecation")
            Multimap<Attribute, AttributeModifier> multimap = super.getDefaultAttributeModifiers(slot);
            if (slot == EquipmentSlotType.MAINHAND)
                multimap.put(ForgeMod.ENTITY_GRAVITY.get(), new AttributeModifier(GRAVITY_MODIFIER, "More Gravity", 1.0D, Operation.ADDITION));

            return multimap;
        }
    }
}
