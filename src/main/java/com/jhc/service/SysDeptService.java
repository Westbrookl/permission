package com.jhc.service;

import com.google.common.base.Preconditions;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysDeptMapper;
import com.jhc.exception.ParamException;
import com.jhc.model.SysDept;
import com.jhc.param.DeptParam;
import com.jhc.util.BeanValidator;
import com.jhc.util.LevelUntil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author jhc on 2019/1/16
 */
@Service
public class SysDeptService {
    @Resource
    private SysDeptMapper sysDeptMapper;


    /**
     * 对部门更新的具体的步骤的理解：
     * 首先对传入的参数进行判断否则抛出异常
     * 第二判断部门下面是否有重复的内容，如果有重复的内容的话便抛出异常
     * 第三数据库里面的字段的值然后更新所有的子条目
     * 第四在更新子条目的时候传入两个值 一个是更新前的值 一个是更新后的值
     * 第五 是保存两个值的level前缀，这里主要的麻烦在于更新前缀的值
     * 第六 如果两个前缀不相同那么就把当前前缀下面所有符合条件的内容筛选出来用新的前缀进行代替
     * 第七 进行批量的更新
     * @param param
     */
    public void update(DeptParam param){
        BeanValidator.check(param);
        /**
         * 判断是否具有相同名称的部门的判断方法
         * 如果父Id相同并且部门名称相同的话并且id不相同的话 这就是相同的们
         */
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw  new ParamException("同一个层级下面具有相同名称的部门");
        }
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before,"待更新的部门不存在");
        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).seq(param.getSeq()).remark(param.getRemark()).parentId(param.getParentId())
                        .build();
        after.setLevel(LevelUntil.calculateLevel(getLevel(param.getParentId()), param.getParentId()));
        after.setOperateIp(RequestHolder.getCurrentUser().getUsername());
        after.setOperateTime(new Date());
        after.setOperator("System Update");
        updateWithChild(before,after);
    }

    @Transactional
    public void updateWithChild(SysDept before,SysDept after){
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if(!after.getLevel().equals(before.getLevel())){
            List<SysDept> deptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(deptList)){
                for(SysDept dept : deptList){
                    String level = dept.getLevel();
                    if(level.indexOf(oldLevelPrefix) == 0){
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(deptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);
    }



    /**
     * 对于save的操作的逻辑
     *
     * @param deptParam
     */
    public void save(DeptParam deptParam){
        //1 首先对传入的参数进行判断是否满足情况
        BeanValidator.check(deptParam);
        //2 检查这个层级下面是否存在相同的部门
        if(checkExist(deptParam.getParentId(),deptParam.getName(),deptParam.getId())){
            throw  new ParamException("同一层级下面存在相同名称的部门");
        }
        //创建出数据库里面与部门表对应的内容
        /**
         * 使用lombok的Builder的优点在于可以使得更方便的创建出一个我们需要的对象
         */
        SysDept dept = SysDept.builder().name(deptParam.getName()).parentId(deptParam.getParentId()).seq(deptParam.getSeq()).remark(deptParam.getRemark()).build();
        /**
         * 对于不能够从前端传来的数据需要我们自己进行处理
         * 即自己进行设置
         * 创建一个LevelUnit这样的一个类
         * 计算Level
         */
        dept.setLevel(LevelUntil.calculateLevel(getLevel(deptParam.getParentId()),deptParam.getParentId()));
        dept.setOperator(RequestHolder.getCurrentUser().getUsername());
        dept.setOperateTime(new Date());
        dept.setOperateIp("127.0.0.1");

        sysDeptMapper.insertSelective(dept);


    }
    public boolean checkExist(Integer parentId,String name,Integer deptId){
        return sysDeptMapper.countByNameAndParentId(parentId,name,deptId)>0;
    }

    /**
     * 这个类的作用是根据id得到对应id的level值
     * @param deptId
     * @return
     */
    public String getLevel(Integer deptId){
        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(deptId);
        if(sysDept == null){
            return  null;
        }
        return sysDept.getLevel();
    }
}
