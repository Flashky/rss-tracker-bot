package com.flashk.bots.rsstracker.repositories.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TelegramEntity {

	private Long userId;
	private Long chatId;
	
}
