package com.thor.core.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.thor.common.enums.ResultStatus;
import com.thor.common.exception.RTException;
import com.thor.core.dao.mapper.UserMapper;
import com.thor.core.entity.Resource;
import com.thor.core.entity.Tag;
import com.thor.core.entity.User;
import com.thor.core.param.user.UserGetParam;
import com.thor.core.param.user.UserListParam;
import com.thor.core.param.user.UserRemoveParam;
import com.thor.core.param.user.UserSaveParam;
import com.thor.core.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ldang
 */
@Service
public class UserService extends BaseServiceAdapter {

	private @Autowired UserMapper mapper;

	public Map<String, Object> list(UserListParam listParam) {
        Map<String, Object> paramMap = injectTenantIdToMap(listParam);
		PageHelper.startPage(listParam.getCurrentPage(), listParam.getPageSize());
		Page<User> list = (Page<User>) mapper.list(paramMap);
		return PageUtil.toResultMap(list);
	}

    /**
     * 供登录接口使用
     * 支持用户名/手机号/邮箱号来登录
     * @param user
     * @return
     */
	public List<User> login(User user){
		return mapper.login(user);
	}

	public User get(UserGetParam getParam){
        Map<String, Object> paramMap = injectTenantIdToMap(getParam);
	    return mapper.get(paramMap);
    }

	/**
	 * 新增
	 * @param saveParam
	 * @return
	 */
	public User add(UserSaveParam saveParam) {
		//主键应在数据库中   不存在
		User user = get(new UserGetParam(saveParam.getUserName()));
		if(user != null){
			throw new DuplicateKeyException(user.getUserName());
		}
		user = saveParam.toUser();
		Date now = new Date();
		user.setCreateTime(now);
		user.setUpdateTime(now);
		user.setTenantId(injectTenantId());
		mapper.add(user);
		return user;
	}

	/**
	 * 更新
	 * @param saveParam
	 * @return
	 */
	public User update(UserSaveParam saveParam) {
		//主键应应在数据库中  存在
		User user = get(new UserGetParam(saveParam.getUserName()));
		if(user == null){
			throw new RTException(ResultStatus.INTERFACE_USER_UPDATE_NOTEXIST_ERROR);  //5250
		}
        User updatedUser = saveParam.toUser();
        updatedUser.setCreateTime(user.getCreateTime());
        updatedUser.setUpdateTime(new Date());
        updatedUser.setTenantId(injectTenantId());
        mapper.update(updatedUser);
        return updatedUser;
	}

	/**
	 * 删除
	 * @param removeParam
	 * @return
	 */
	public void remove(UserRemoveParam removeParam) {
        Map<String, Object> paramMap = injectTenantIdToMap(removeParam);
		mapper.delete(paramMap);
	}

    /**
     * 获取用户拥有的资源集合
     * @param getParam
     * @return
     */
	public List<Resource> getResources(UserGetParam getParam) {
	    return mapper.getResources(injectTenantIdToMap(getParam));
    }

    /**
     * 获取用户隶属的标签集合
     * @param getParam
     * @return
     */
    public List<Tag> getTags(UserGetParam getParam) {
	    return  mapper.getTags(injectTenantIdToMap(getParam));
    }
}
