package com.jediexe.pouchviewer;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lotr.common.item.LOTRItemPouch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

public class Pouchviewer {
	
	public static Pouchviewer instance = new Pouchviewer();

	public static KeyBinding keyBindingShowAll = new KeyBinding("Show all items in tooltip", 42, "LOTR");
	
	public List<ItemStack> inventory;
	
	static ResourceLocation guitexture = new ResourceLocation("lotr:gui/pouch.png");
	
	static String commonItems = Main.commonItems.toLowerCase();
	static String uncommonItems = Main.uncommonItems.toLowerCase();
	static String rareItems = Main.rareItems.toLowerCase();
	static String epicItems = Main.epicItems.toLowerCase();
	static String legendaryItems = Main.legendaryItems.toLowerCase();
	static String tagItemsColor = Main.tagItemsColor;
	static String slotColor = Main.slotColor;
	static String defaultColor = Main.defaultColor;
	static String commonColor = Main.commonColor;
	static String uncommonColor = Main.uncommonColor;
	static String rareColor = Main.rareColor;
	static String epicColor = Main.epicColor;
	static String legendaryColor = Main.legendaryColor;
	String[] colors = new String[]
			{"dark_red", "red", "gold", 
			"yellow", "dark_green", "green", 
			"aqua", "dark_aqua", "dark_blue", 
			"blue", "light_purple", "dark_purple", 
			"white", "gray", "dark_gray", "black"};
	String[] colorValues = new String[]
				{"4", "c", "6", 
				"e", "2", "a", 
				"b", "3", "1", 
				"9", "d", "5", 
				"f", "7", "8", "0"};
	String space = " ";
	String color = (char) 167 + "f";
	String slotColorValue = (char) 167 + "3";
	String tagItemsColorValue = (char) 167 + "9";
	
	//Checks for an event where an item tooltip is generated by the player (by hovering over items)
	@SubscribeEvent
    public void onTooltipGen(ItemTooltipEvent event){
		//Removes "ï¿½belonged to: name" description in tooltip from all items
		//Configurable in pouchviewer.cfg
		if (!Main.showOwned) {
			if(event.toolTip.toString().contains("Belonged to:")){
				int size = event.toolTip.size();
				int z = size-1;
				while (event.toolTip.toString().contains("Belonged to: ")){
					event.toolTip.remove(z);
					z-=1;
				}
				event.toolTip.remove(event.toolTip.size()-1);
			}
		}
		
		//Removes "dyed" description in tooltip from all items
		//Configurable in pouchviewer.cfg
		if (!Main.showDyed) {
			if(event.toolTip.contains("Dyed")){
				event.toolTip.remove("Dyed");
			}
		}
		
		if (Main.legacyTooltip) {

			//Check if the item player is hovering over has lotrpouchdata (is a pouch and has data)
	    	if(event.itemStack.hasTagCompound() && event.itemStack.getTagCompound().hasKey("LOTRPouchData")){
	    		
	    		//Get the nbt data of the pouch
	    		NBTTagCompound nbt = event.itemStack.getTagCompound().getCompoundTag("LOTRPouchData");
				NBTTagList items = nbt.getTagList("Items", 10);
				
				//If the pouch has an item
				if(items.tagCount()>0){
					
					//Sets the color of the slot numbers
					if (Main.showSlotNumber) {
						for (int l=0; l<colors.length;l++) {
							if (slotColor.equals(colors[l])) {
								slotColorValue = (char) 167 + colorValues[l];
								break;
							}
							else {
								slotColorValue = (char) 167 + "3";
							}
						}
					}
					
					//Adds "Items" above the listed items
					if (Main.addTagItems) {
						//Sets the color based on the config value
						for (int m=0; m<colors.length;m++) {
							if (tagItemsColor.equals(colors[m])) {
								tagItemsColorValue = (char) 167 + colorValues[m];
								break;
							}
							else {
								tagItemsColorValue = (char) 167 + "9";
							}
						}
						event.toolTip.add(new String(tagItemsColorValue + "Items"));	
					}
					
					//If player is pressing shift, list all items in the pouch
		    		if(Keyboard.isKeyDown(keyBindingShowAll.getKeyCode()) && items.tagCount()>Main.defaultItemsShown){
						for (int i = 0; i < items.tagCount(); ++i) {
							NBTTagCompound itemData = items.getCompoundTagAt(i);
							byte slot = itemData.getByte("Slot");
							if (slot < 0 || slot >= 27) {
								continue;
							}
							
							
							//Chooses colors based on config
							if (Main.enableRarity) {
								for (int k=0; k<colors.length;k++) {
									if (defaultColor.equals(colors[k])) {
										color = (char) 167 + colorValues[k];
										break;
									}
									else {
										color = (char) 167 + "f";
									}
								}
								if(commonItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (commonColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
								else if(uncommonItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (uncommonColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
								else if(rareItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (rareColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
								else if(epicItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (epicColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
								else if(legendaryItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (legendaryColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
							}
							else if (!Main.enableRarity) {
								color = (char) 167 + "f";
							}
							
							//Checks if name will be first or quantity will be first
							if (!Main.nameFirst) {
								//Checks if to display slot numbers
								if(Main.showSlotNumber) {
									if(slot<9) {
										String slotnumber = slotColorValue + "0" + (slot + 1) + (char) 167 + "f" + ": x";
										event.toolTip.add(new String(slotnumber +
											ItemStack.loadItemStackFromNBT(itemData).stackSize + space + color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
									}
									else {
										String slotnumber = slotColorValue + (slot + 1) + (char) 167 + "f" + ": x";
										event.toolTip.add(new String(slotnumber +
											ItemStack.loadItemStackFromNBT(itemData).stackSize + space + color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
									}
								}
								
								//Otherwise don't show slot numbers
								else{
									event.toolTip.add(new String((char) 167 + "f" + "x" +
										ItemStack.loadItemStackFromNBT(itemData).stackSize + space + color + 
										ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
								}
							}
							else if (Main.nameFirst) {
								if(Main.showSlotNumber) {
									if(slot<9) {
										String slotnumber = slotColorValue + "0" + (slot + 1) + (char) 167 + "f" + ": ";
										event.toolTip.add(new String(slotnumber + color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName() + (char) 167 + "f" + " x" + 
											ItemStack.loadItemStackFromNBT(itemData).stackSize));
									}
									else {
										String slotnumber = slotColorValue + (slot + 1) + (char) 167 + "f" + ": ";
										event.toolTip.add(new String(slotnumber + color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName() + (char) 167 + "f" + " x" + 
											ItemStack.loadItemStackFromNBT(itemData).stackSize));
									}
								}

								else{
									event.toolTip.add(new String(color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName() + (char) 167 + "f" + " x" + 
											ItemStack.loadItemStackFromNBT(itemData).stackSize));
								}
							}
						}
					}
		    		
		    		//If player is not pressing shift (default view), list only the first 5 items (edit in config)
		    		else {
		    			int j = 0;
						for (int i = 0; i < items.tagCount(); ++i) {
							NBTTagCompound itemData = items.getCompoundTagAt(i);
							byte slot = itemData.getByte("Slot");
							if (slot < 0 || slot >= 27) {
								continue;
							}
							j+=1;
							
							//Chooses colors based on config
							if (Main.enableRarity) {
								for (int k=0; k<colors.length;k++) {
									if (defaultColor.equals(colors[k])) {
										color = (char) 167 + colorValues[k];
										break;
									}
									else {
										color = (char) 167 + "f";
									}
								}
								if(commonItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (commonColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
								else if(uncommonItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (uncommonColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
								else if(rareItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (rareColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
								else if(epicItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (epicColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
								else if(legendaryItems.contains(ItemStack.loadItemStackFromNBT(itemData).getDisplayName().toString().toLowerCase())) {
									for (int k=0; k<colors.length;k++) {
										if (legendaryColor.equals(colors[k])) {
											color = (char) 167 + colorValues[k];
											break;
										}
										else {
											color = (char) 167 + "f";
										}
									}
								}
							}
							else if (!Main.enableRarity) {
								color = (char) 167 + "f";
							}
							
							//Checks if name will be first or quantity will be first
							if (!Main.nameFirst) {
								if(Main.showSlotNumber && j<=Main.defaultItemsShown) {
									if(slot<9) {
										String slotnumber = slotColorValue + "0" + (slot + 1) + (char) 167 + "f" + ": x";
										event.toolTip.add(new String(slotnumber + 
											ItemStack.loadItemStackFromNBT(itemData).stackSize + space + color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
									}
									else {
										String slotnumber = slotColorValue + (slot + 1) + (char) 167 + "f" + ": x";
										event.toolTip.add(new String(slotnumber + 
											ItemStack.loadItemStackFromNBT(itemData).stackSize + space + color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
									}
								}
								
								else if(j<=Main.defaultItemsShown){
									event.toolTip.add(new String((char) 167 + "f" + "x" + 
										ItemStack.loadItemStackFromNBT(itemData).stackSize + space + color + 
										ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
								}
							}
							else if (Main.nameFirst){
								if(Main.showSlotNumber && j<=Main.defaultItemsShown) {
									if(slot<9) {
										String slotnumber = slotColorValue + "0" + (slot + 1) + (char) 167 + "f" + ": ";
										event.toolTip.add(new String(slotnumber + color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName() + (char) 167 + "f" + " x" +
											ItemStack.loadItemStackFromNBT(itemData).stackSize));
									}
									else {
										String slotnumber = slotColorValue + (slot + 1) + (char) 167 + "f" + ": x";
										event.toolTip.add(new String(slotnumber + color + 
											ItemStack.loadItemStackFromNBT(itemData).getDisplayName() + (char) 167 + "f" + " x" +
											ItemStack.loadItemStackFromNBT(itemData).stackSize));
									}
								}
								
								else if(j<=Main.defaultItemsShown){
									event.toolTip.add(new String(color + 
										ItemStack.loadItemStackFromNBT(itemData).getDisplayName() + (char) 167 + "f" + " x" + 
										ItemStack.loadItemStackFromNBT(itemData).stackSize));
								}
							}
							else {
								continue;
							}
						}
						
						//Add "and #x more..." to the end of the default view
						if((items.tagCount()-Main.defaultItemsShown)!=0 && j>Main.defaultItemsShown){
							event.toolTip.add(new String((char) 167 + "o" + "and " + (items.tagCount()-Main.defaultItemsShown) + " more..."));
						}
		    		}
				}
	    	}
	    	//Makes it display things for any item with the NBT tag 'items'
	    	else if(event.itemStack.hasTagCompound() && !event.itemStack.getTagCompound().hasKey("LOTRPouchData") && event.itemStack.getTagCompound().hasKey("Items")){
	    		
	    		NBTTagCompound NBTTagCompound = event.itemStack.getTagCompound();
	    		NBTTagList tagList = NBTTagCompound.getTagList("Items", 10);
	    		
	    		if(tagList.tagCount()>0){
	    			
	    			event.toolTip.add(new String((char) 167 + "9" + "Items"));
	    			
		    		if(Keyboard.isKeyDown(keyBindingShowAll.getKeyCode()) && tagList.tagCount()>Main.defaultItemsShown){
			    		for (int i = 0; i < tagList.tagCount(); ++i) {
							NBTTagCompound itemData = tagList.getCompoundTagAt(i);
							byte slot = itemData.getByte("Slot");
							if (slot < 0 || slot >= 27) {
								continue;
							}
							if(Main.showSlotNumber) {
								if(i<9) {
									String slotnumber = (char) 167 + "3" + "0" + (i+1) + (char) 167 + "f" + ": x";
									event.toolTip.add(new String(slotnumber + 
										ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
										ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
								}
								else {
									String slotnumber = (char) 167 + "3" + (i+1) + (char) 167 + "f" + ": x";
									event.toolTip.add(new String(slotnumber + 
										ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
										ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
								}
							}
							else{
								event.toolTip.add(new String((char) 167 + "f" + "x" + 
									ItemStack.loadItemStackFromNBT(itemData).stackSize + " " + 
									ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
							}
			    		}
		    		}
		    		else{
		    			int j=0;
			    		for (int i = 0; i < tagList.tagCount(); ++i) {
							NBTTagCompound itemData = tagList.getCompoundTagAt(i);
							byte slot = itemData.getByte("Slot");
							if (slot < 0 || slot >= 27) {
								continue;
							}
							j+=1;
							if(Main.showSlotNumber && j<=Main.defaultItemsShown) {
								if(i<9) {
									String slotnumber = (char) 167 + "3" + "0" + (i+1) + (char) 167 + "f" + ": x";
									event.toolTip.add(new String(slotnumber + 
										ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
										ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
								}
								else {
									String slotnumber = (char) 167 + "3" + (i+1) + (char) 167 + "f" + ": x";
									event.toolTip.add(new String(slotnumber + 
										ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
										ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
								}
							}
							else if(j<=Main.defaultItemsShown){
								event.toolTip.add(new String((char) 167 + "f" + "x" + 
									ItemStack.loadItemStackFromNBT(itemData).stackSize + " " + 
									ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
							}
							else {
								continue;
							}
			    		}
			    		if((tagList.tagCount()-Main.defaultItemsShown)!=0 && j>Main.defaultItemsShown){
							event.toolTip.add(new String((char) 167 + "o" + "and " + (tagList.tagCount()-Main.defaultItemsShown) + " more..."));
						}
		    		}
		    	}
	    	}
		}
		
		/*else {
			if(event.itemStack.hasTagCompound() && event.itemStack.getTagCompound().hasKey("LOTRPouchData")){
	    		NBTTagCompound nbt = event.itemStack.getTagCompound().getCompoundTag("LOTRPouchData");
				NBTTagList items = nbt.getTagList("Items", 10);
				if(items.tagCount()>0){
					List<ItemStack> inventory = new ArrayList<ItemStack>();
					for (int i = 0; i < items.tagCount(); ++i) {
						NBTTagCompound itemData = items.getCompoundTagAt(i);
						byte slot = itemData.getByte("Slot");
						if (slot < 0 || slot >= 27) {
							continue;
						}
						inventory.add(ItemStack.loadItemStackFromNBT(itemData));
					}
					renderItems(inventory, event.itemStack, 20, 20);
				}
			}
		}*/
	}
	
	/*public void renderItems(List <ItemStack> list, ItemStack item, int x, int y) {
		if (item.getItem() instanceof LOTRItemPouch) {
			LOTRItemPouch pouch = (LOTRItemPouch)item.getItem();
			int rows = pouch.getCapacity(item)/9;
			int columns = 9;
			int k = 0;
			
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	        RenderHelper.enableGUIStandardItemLighting();
	        
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < columns; c++) {
					int a = x + c * 18 + 1;
					int b = y + 1 * 18 + 1;
					drawItem(list, a, b, k++, Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine);
				}
			}
			
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	        RenderHelper.disableStandardItemLighting();
		}
	}
	
	public void drawItem(List <ItemStack> list, int x, int y, int index, FontRenderer textRenderer, RenderItem itemRenderer, TextureManager textureManager) {
		ItemStack item = (ItemStack) list.get(index);
		if (item==null) return;
		else {
			GL11.glEnable(GL11.GL_DEPTH);
			itemRenderer.zLevel = 120.0f;
			RenderHelper.enableGUIStandardItemLighting();
			itemRenderer.renderItemAndEffectIntoGUI(textRenderer, textureManager, item, x, y);
			final String count = (item.stackSize == 1) ? "" : String.valueOf(item.stackSize);
	        final String more = (item.stackSize % item.getMaxStackSize() == 0) ? "" : ("+" + item.stackSize % item.getMaxStackSize());
	        final String count2 = (item.stackSize == 1) ? "" : (item.stackSize / item.getMaxStackSize() + "S" + more);
	        itemRenderer.renderItemOverlayIntoGUI(textRenderer, textureManager, item, x, y, count);
	        itemRenderer.zLevel = 0.0f;
		}
	}*/

	public static void update() {
		commonItems = Main.commonItems.toLowerCase();
		uncommonItems = Main.uncommonItems.toLowerCase();
		rareItems = Main.rareItems.toLowerCase();
		epicItems = Main.epicItems.toLowerCase();
		legendaryItems = Main.legendaryItems.toLowerCase();
		tagItemsColor = Main.tagItemsColor;
		slotColor = Main.slotColor;
		defaultColor = Main.defaultColor;
		commonColor = Main.commonColor;
		uncommonColor = Main.uncommonColor;
		rareColor = Main.rareColor;
		epicColor = Main.epicColor;
		legendaryColor = Main.legendaryColor;
	}
}