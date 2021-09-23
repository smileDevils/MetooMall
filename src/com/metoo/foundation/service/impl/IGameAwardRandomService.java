package com.metoo.foundation.service.impl;

import com.metoo.foundation.domain.User;

public interface IGameAwardRandomService {
	
	// 方式一：为不同阶段树设置不同概率奖励
	public Object randomAward(Long id, User user);
	
	// 方式二：每阶段随机奖品概率相同
	public Object randomAward(User user);
	
	// 方式三 指定奖励
	public Object randomAward(User user, int index);
}
