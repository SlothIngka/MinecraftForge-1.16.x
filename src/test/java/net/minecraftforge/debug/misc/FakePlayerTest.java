/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.misc;

import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("fake_player_test")
public class FakePlayerTest
{
    public static final boolean ENABLE = true;

    public FakePlayerTest()
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("fakeplayer")
                //Tests fake player creation & interaction with other entities in world
                .then(Commands.literal("attack")
                        .then(Commands.argument("target", EntityArgument.entity())
                        .executes(context -> {
                            FakePlayerFactory.getMinecraft(context.getSource().getLevel()).attack(EntityArgument.getEntity(context, "target"));
                            return 1;
                        }))
                )
                //Makes sure fake player does not crash when its `connection` is accessed [#7604]
                .then(Commands.literal("opencontainer")
                        .executes(context -> {
                            ServerPlayerEntity fakePlayer = FakePlayerFactory.getMinecraft(context.getSource().getLevel());
                            Hand hand = Hand.MAIN_HAND;
                            ItemStack stack = Items.WRITABLE_BOOK.getDefaultInstance();
                            fakePlayer.setItemInHand(hand, stack);
                            fakePlayer.openItemGui(stack, hand);
                            return 1;
                        })
                ));
    }
}
