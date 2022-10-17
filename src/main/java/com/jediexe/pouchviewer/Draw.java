package com.jediexe.pouchviewer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;

public class Draw {
	
	public static Draw instance = new Draw();

	//Checks for an event where an item tooltip is generated by the player (by hovering over items)
	@SubscribeEvent
    public void onTooltipGen(ItemTooltipEvent event){
		
		//Removes "�belonged to: name" description in tooltip from all items
		//Remove this bit if you want to keep the "�belonged to: name" text in tooltips
		if(event.toolTip.toString().contains("Belonged to:")){
			int size = event.toolTip.size();
			event.toolTip.remove(size-2);
			event.toolTip.remove(size-3);
			event.toolTip.remove(size-3);
		}
		
		//Removes "dyed" description in tooltip from all items
		//Remove this bit if you want to see "dyed" text in tooltips
		if(event.toolTip.contains("Dyed")){
			event.toolTip.remove("Dyed");
		}
		
		//Check if the item player is hovering over has lotrpouchdata (is a pouch and has data)
    	if(event.itemStack.hasTagCompound() && event.itemStack.getTagCompound().hasKey("LOTRPouchData")){
    		
    		//Get the nbt data of the pouch
    		NBTTagCompound nbt = event.itemStack.getTagCompound().getCompoundTag("LOTRPouchData");
			NBTTagList items = nbt.getTagList("Items", 10);
			
			//If the pouch has an item
			if(items.tagCount()>0){
				
				//Adds "Items" in blue above the listed items
				event.toolTip.add(new String((char) 167 + "9" + "Items"));	
				
				//If player is pressing shift, list all items in the pouch
	    		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && items.tagCount()>5){
					for (int i = 0; i < items.tagCount(); ++i) {
						NBTTagCompound itemData = items.getCompoundTagAt(i);
						byte slot = itemData.getByte("Slot");
						if (slot < 0 || slot >= 27) {
							continue;
						}
						
						//If player has advanced tooltips on while pressing shift, 
						//display slot numbers as well
						if(event.showAdvancedItemTooltips) {
							if(slot<9) {
								String slotnumber = (char) 167 + "3" + "0" + (slot + 1) + (char) 167 + "f" + ": x";
								event.toolTip.add(new String(slotnumber + 
									ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
									ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
							}
							else {
								String slotnumber = (char) 167 + "3" + (slot + 1) + (char) 167 + "f" + ": x";
								event.toolTip.add(new String(slotnumber + 
									ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
									ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
							}
						}
						
						//If player doesn't have advanced tooltips on while pressing shift, 
						//just display item name and quantity
						else{
							event.toolTip.add(new String((char) 167 + "f" + "x" +
								ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
								ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
						}
					}
				}
	    		
	    		//If player is not pressing shift (default view), list only the first 5 items
	    		else {
	    			int j = 0;
					for (int i = 0; i < items.tagCount(); ++i) {
						NBTTagCompound itemData = items.getCompoundTagAt(i);
						byte slot = itemData.getByte("Slot");
						if (slot < 0 || slot >= 27) {
							continue;
						}
						j+=1;
						
						//If player has advanced tooltips on, display slot numbers as well
						if(event.showAdvancedItemTooltips && j<=5) {
							if(slot<9) {
								String slotnumber = (char) 167 + "3" + "0" + (slot + 1) + (char) 167 + "f" + ": x";
								event.toolTip.add(new String(slotnumber + 
									ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
									ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
							}
							else {
								String slotnumber = (char) 167 + "3" + (slot + 1) + (char) 167 + "f" + ": x";
								event.toolTip.add(new String(slotnumber + 
									ItemStack.loadItemStackFromNBT(itemData).stackSize + " " +
									ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
							}
						}
						
						//If player doesn't have advanced tooltips on, just display item name and quantity
						else if(j<=5){
							event.toolTip.add(new String((char) 167 + "f" + "x" + 
								ItemStack.loadItemStackFromNBT(itemData).stackSize + " " + 
								ItemStack.loadItemStackFromNBT(itemData).getDisplayName()));
						}
						else {
							continue;
						}
					}
					
					//Add "and #x more..." to the end of the default view
					if(items.tagCount()-5!=0 && j>5){
						event.toolTip.add(new String((char) 167 + "o" + "and " + (items.tagCount()-5) + " more..."));
					}
	    		}
			}
    	}
	}
}

//The file is called "draw" because I initially wanted to draw the items, but I got too lazy
//If anyone wants to use this in a modpack or to customize feel free as long as you give me credit
//If the LOTR mod team wants me to take this mod down, please message me and I will

// Nam�ri�!
//- jediexe (jedi#6034 on discord if you have any questions/feedback)