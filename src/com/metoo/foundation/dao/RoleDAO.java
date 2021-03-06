package com.metoo.foundation.dao;

import org.springframework.stereotype.Repository;

import com.metoo.core.base.GenericDAO;
import com.metoo.foundation.domain.Role;

@Repository("roleDAO")
public class RoleDAO extends GenericDAO<Role> {
}
