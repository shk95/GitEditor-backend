package com.shk95.giteditor.core.discord;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DiscordContext {

	private final JDABuilder jdaBuilder;
	private JDA jda;

	@PostConstruct
	public void startBot() {
		try {
			jda = jdaBuilder.build();
			jda.awaitReady();
			jda.updateCommands()
				.addCommands(
					Commands.slash("help", "사용방법 안내."),
					Commands.slash("valid", "등록된 사용자인지 확인.")
				).queue();
		} catch (Exception e) {
			log.info("Failed to initialize Discord service . {}", e.getMessage());
			if (log.isDebugEnabled()) e.printStackTrace();
			// Handle exceptions
		}
	}

	@PreDestroy
	public void stopBot() {
		if (jda != null) {
			jda.shutdown();
		}
	}
}
