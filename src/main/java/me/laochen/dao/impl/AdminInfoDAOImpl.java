package me.laochen.dao.impl;

import me.laochen.dao.AdminInfoDAO;
import me.laochen.dao.core.AbstractDao;
import me.laochen.po.AdminInfo;

import org.springframework.stereotype.Repository;

@Repository("adminInfoDAO")
public class AdminInfoDAOImpl extends AbstractDao<AdminInfo> implements AdminInfoDAO {

}
