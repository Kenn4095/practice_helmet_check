package com.helmetcheck;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface HelmetCheckConfig extends Config
{
	@ConfigItem(
		keyName = "helmChecker",
		name = "Helmet Checker",
		description = "Displays a message and overlay box signifying whether the player is wearing a helmet or not."
	)
	default String greeting()
	{
		return "Hello";
	}
}
