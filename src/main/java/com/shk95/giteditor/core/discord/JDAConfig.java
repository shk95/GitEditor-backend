package com.shk95.giteditor.core.discord;

import com.shk95.giteditor.config.ApplicationProperties;
import com.shk95.giteditor.core.discord.listener.SimpleListener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class JDAConfig {

	private final ApplicationProperties properties;
	private final SimpleListener simpleListener;

	@Bean
	public JDABuilder jdaBuilder() {
		String token = properties.getDiscord().getBot().getToken();
		return JDABuilder.createDefault(token)
			.setActivity(Activity.customStatus("대기중..."))
			.addEventListeners(simpleListener)
			.setStatus(OnlineStatus.ONLINE)
			.enableIntents(GatewayIntent.MESSAGE_CONTENT)
//			.setChunkingFilter(ChunkingFilter.ALL)
			.setMemberCachePolicy(MemberCachePolicy.ONLINE);
	}
}
