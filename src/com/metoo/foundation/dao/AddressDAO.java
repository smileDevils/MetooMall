package com.metoo.foundation.dao;
import org.springframework.stereotype.Repository;

import com.metoo.core.base.GenericDAO;
import com.metoo.foundation.domain.Address;
@Repository("addressDAO")
public class AddressDAO extends GenericDAO<Address> {

}