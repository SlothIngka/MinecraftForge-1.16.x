/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.misc;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

@Mod("containertypetest")
public class ContainerTypeTest
{
    @ObjectHolder("containertypetest:container")
    public static final ContainerType<TestContainer> TYPE = null;
    public class TestContainer extends Container
    {
        private final String text;
        
        protected TestContainer(int windowId, PlayerInventory playerInv, PacketBuffer extraData)
        {
            this(windowId, new Inventory(9), extraData.readUtf(128));
        }
        
        public TestContainer(int windowId, Inventory inv, String text)
        {
            super(TYPE, windowId);
            this.text = text;
            for (int i = 0; i < 9; i++)
            {
                this.addSlot(new Slot(inv, i, (i % 3) * 18, (i / 3) * 18));
            }
        }

        @Override
        public boolean stillValid(PlayerEntity playerIn)
        {
            return true;
        }
    }
    
    public class TestGui extends ContainerScreen<TestContainer>
    {
        public TestGui(TestContainer container, PlayerInventory inv, ITextComponent name)
        {
            super(container, inv, name);
        }

        @Override
        protected void renderBg(MatrixStack mStack, float partialTicks, int mouseX, int mouseY)
        {
            drawString(mStack, this.font, getMenu().text, mouseX, mouseY, -1);
        }
    }

    public ContainerTypeTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, this::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::onRightClick);
    }

    private void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().register(IForgeContainerType.create(TestContainer::new).setRegistryName("container"));
    }
    
    private void setup(FMLClientSetupEvent event)
    {
        ScreenManager.register(TYPE, TestGui::new);
    }
    
    private void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if (!event.getWorld().isClientSide && event.getHand() == Hand.MAIN_HAND)
        {
            if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.SPONGE)
            {
                String text = "Hello World!";
                NetworkHooks.openGui((ServerPlayerEntity) event.getPlayer(), new INamedContainerProvider()
                {
                    @Override
                    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
                    {
                        Inventory inv = new Inventory(9);
                        for (int i = 0; i < inv.getContainerSize(); i++)
                        {
                            inv.setItem(i, new ItemStack(Items.DIAMOND));
                        }
                        return new TestContainer(p_createMenu_1_, inv, text);
                    }
                    
                    @Override
                    public ITextComponent getDisplayName()
                    {
                        return new StringTextComponent("Test");
                    }
                }, extraData -> {
                    extraData.writeUtf(text);
                });
            }
        }
    }
}
