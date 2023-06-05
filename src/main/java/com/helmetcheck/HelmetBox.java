package com.helmetcheck;

import java.awt.image.BufferedImage;
import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.util.QuantityFormatter;

class HelmetBox extends Counter
{
	@Getter
	private final int itemID;
	private final String name;

	HelmetBox(Plugin plugin, int itemID, String name, BufferedImage image)
	{
		super(image, plugin, 1);
		this.itemID = itemID;
		this.name = name;
	}

	@Override
	public String getText()
	{
		return QuantityFormatter.quantityToRSDecimalStack(getCount());
	}

	@Override
	public String getTooltip()
	{
		return name;
	}
}