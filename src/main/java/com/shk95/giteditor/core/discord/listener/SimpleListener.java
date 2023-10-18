package com.shk95.giteditor.core.discord.listener;

import com.shk95.giteditor.core.discord.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SimpleListener extends ListenerAdapter {

	private final UserContext userContext;

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().isBot()) return;
		User user = event.getAuthor();
		Message message = event.getMessage();

		log.debug("""
			discord message : {}
			user`s name : {}
			user`s id_long : {}
			""", message.getContentDisplay(), user.getName(), user.getIdLong());
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		switch (event.getName()) {
			case "help":
				event.reply("""
					공통 기능 :
					등록된 사용자의 기능 :
					""").queue();
				break;
			case "valid":
				event.deferReply().queue();
				String msg = userContext.isUserExists(String.valueOf(event.getUser().getIdLong()))
					? "등록된 사용자입니다."
					: "서비스 페이지에서 해당 디스코드 아이디를 등록해주세요. [" + event.getUser().getIdLong() + "]";
				event.getHook().sendMessage(msg).queue();
				break;

		}
	}
}
