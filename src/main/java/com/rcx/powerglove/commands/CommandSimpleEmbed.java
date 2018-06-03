package com.rcx.powerglove.commands;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;

public class CommandSimpleEmbed extends Command {

	MessageEmbed returns;

	public CommandSimpleEmbed(MessageEmbed returns) {
		this.returns = returns;
	}

	@Override
	public void execute(String[] arguments, MessageReceivedEvent event) {
		try {
			event.getChannel().sendMessage(returns).queue();
		} catch (InsufficientPermissionException e) {
			event.getChannel().sendMessage("This looks terrible because the bot doesn't have embed permissions.\n" + returns.getDescription()).queue();
		}
	}
}
