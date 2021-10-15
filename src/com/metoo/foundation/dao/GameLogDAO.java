package com.metoo.foundation.dao;


import org.springframework.stereotype.Repository;

import com.metoo.core.base.GenericDAO;
import com.metoo.foundation.domain.GameLog;

@Repository("gameLogDAO")
public class GameLogDAO extends GenericDAO<GameLog>{

}
