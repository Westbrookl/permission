package com.jhc.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.jhc.common.RequestHolder;
import com.jhc.dao.SysAclMapper;
import com.jhc.dao.SysAclModuleMapper;
import com.jhc.dao.SysDeptMapper;
import com.jhc.dto.AclLevelDto;
import com.jhc.dto.DeptLevelDto;
import com.jhc.dto.AclModuleLevelDto;
import com.jhc.model.SysAcl;
import com.jhc.model.SysAclModule;
import com.jhc.model.SysDept;
import com.jhc.util.LevelUntil;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jhc on 2019/1/16
 */
@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysAclModuleMapper sysAclModuleMapper;

    @Resource
    private SysCoreService sysCoreService;

    @Resource
    private SysAclMapper sysAclMapper;

    /**
     * 针对于部门树的设计的总体的思路是
     * 首先我们要取出当前具有的所有的部门，并且把所有的部门的类型转换成为我们的树的结构
     * 第二步传建一个Map<String,DeptLevelDto>类型的数据结构，用来保存所有的内容并且创建一个rootList用来保存所有的根节点的内容，
     * 在第二步的过程中我们还需要对其进行递归的排序。设计了三个参数 rootList level 和总体的map
     * 第三步就是对递归的处理:
     * 对每个rootList的值进行遍历，遍历以后得到当前值的下一层level然后去总体的map中去寻找在这层所有的内容，如果这一层中有内容
     * 那么便对这一层的内容进行排序，并且添加到rootList当中去，然后去递归查下一层的内容，。
     *
     * @return
     */
    public List<DeptLevelDto> deptTree() {
        List<SysDept> deptList = sysDeptMapper.getAllDept();

        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for (SysDept dept : deptList) {
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }

    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Lists.newArrayList();
        }
        //新的数据结构
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = new ArrayList<>();
        for (DeptLevelDto dto : dtoList) {
            levelDeptMap.put(dto.getLevel(), dto);
            if (LevelUntil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        //按Seq排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        //递归生成树
        transformDeptTree(rootList, LevelUntil.ROOT, levelDeptMap);
        return rootList;
    }

    public void transformDeptTree(List<DeptLevelDto> dtoList, String level, Multimap<String, DeptLevelDto> map) {
        for (int i = 0; i < dtoList.size(); i++) {
            //遍历每个元素
            DeptLevelDto dto = dtoList.get(i);
            //得到下一层的Level值
            String nextLevel = LevelUntil.calculateLevel(level, dto.getId());
            //得到下一层级的所有的值
            List<DeptLevelDto> tempList = (List<DeptLevelDto>) map.get(nextLevel);
            if (!CollectionUtils.isEmpty(tempList)) {
                //排序
                Collections.sort(tempList, deptSeqComparator);
                //附加到当前List的后面
                dto.setDeptList(tempList);
                //处理下一层
                transformDeptTree(tempList, nextLevel, map);
            }
        }
    }

    public Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };


    public List<AclModuleLevelDto> aclTree() {
        List<SysAclModule> moduleList = sysAclModuleMapper.getAllAclModule();
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();

        for (SysAclModule module : moduleList) {
            dtoList.add(AclModuleLevelDto.adapt(module));
        }

        return aclListToTree(dtoList);
    }

    public List<AclModuleLevelDto> aclListToTree(List<AclModuleLevelDto> moduleList) {
        if (CollectionUtils.isEmpty(moduleList)) {
            return Lists.newArrayList();
        }
        Multimap<String, AclModuleLevelDto> map = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();
        for (AclModuleLevelDto dto : moduleList) {
            map.put(dto.getLevel(), dto);
            if (LevelUntil.ROOT.equals(dto.getLevel())) {
                rootList.add(dto);
            }
        }
        Collections.sort(rootList, aclComparator);
        recursiveOrder(rootList, LevelUntil.ROOT, map);
        return rootList;
    }

    public void recursiveOrder(List<AclModuleLevelDto> dtoList, String level, Multimap<String, AclModuleLevelDto> map) {
        for (int i = 0; i < dtoList.size(); i++) {
            AclModuleLevelDto dto = dtoList.get(i);
            String nextLevel = LevelUntil.calculateLevel(level, dto.getId());
            List<AclModuleLevelDto> nextList = (List<AclModuleLevelDto>) map.get(nextLevel);
            if (!CollectionUtils.isEmpty(nextList)) {
                Collections.sort(nextList, aclComparator);
                dto.setAclModuleList(nextList);
                recursiveOrder(nextList, nextLevel, map);
            }

        }
    }

    public Comparator<AclModuleLevelDto> aclComparator = new Comparator<AclModuleLevelDto>() {
        @Override
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    /**
     * role acl
     *
     * @param roleId
     * @return 这个方法的作用获取当前用户的所有的权限 以及当前用户下面的所有角色的对应的权限
     * 如果用户具有该权限便可以先
     * 如果角色具有该权限便可以设置为被选定
     * <p>
     * 其实最终的目的是把当前权限模块下面所有的模块和权限点都绑定到相应的权限模块下面去
     */
    public List<AclModuleLevelDto> roleTree(Integer roleId) {
        List<SysAcl> userAclList = sysCoreService.getCurrentUserAclList();
        List<SysAcl> roleAclList = sysCoreService.getRoleAclList(roleId);
        List<AclLevelDto> allAclDtoList = Lists.newArrayList();
        Set<Integer> userAclId = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclId = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> allAclList = sysAclMapper.getAll();
        for (SysAcl acl : allAclList) {
            AclLevelDto dto = AclLevelDto.adapt(acl);
            if (userAclId.contains(acl.getId())) {
                dto.setHasAcl(true);
            }
            if (roleAclId.contains(acl.getId())) {
                dto.setChecked(true);
            }
            allAclDtoList.add(dto);
        }
        return aclDtoListToTree(allAclDtoList);
    }

    public List<AclModuleLevelDto> aclDtoListToTree(List<AclLevelDto> allAclDtoList) {
        if (CollectionUtils.isEmpty(allAclDtoList)) {
            return Lists.newArrayList();
        }
        List<AclModuleLevelDto> aclModuleDtoList = aclTree();
        Multimap<Integer, AclLevelDto> map = ArrayListMultimap.create();

        for (AclLevelDto dto : allAclDtoList) {
            if (dto.getStatus() == 1) {
                map.put(dto.getAclModuleId(), dto);
            }
        }
        bindAclWithModule(aclModuleDtoList, map);
        return aclModuleDtoList;
    }

    public void bindAclWithModule(List<AclModuleLevelDto> aclModuleLevelDtoList, Multimap<Integer, AclLevelDto> map) {
        if (CollectionUtils.isEmpty(aclModuleLevelDtoList)) {
            return;
        }
        for (AclModuleLevelDto moduleDto : aclModuleLevelDtoList) {
            List<AclLevelDto> aclDto = (List<AclLevelDto>) map.get(moduleDto.getId());
            if (CollectionUtils.isNotEmpty(aclDto)) {
                Collections.sort(aclDto, new Comparator<AclLevelDto>() {
                    @Override
                    public int compare(AclLevelDto o1, AclLevelDto o2) {
                        return o1.getSeq() - o2.getSeq();
                    }
                });
                moduleDto.setAclList(aclDto);
            }
            bindAclWithModule(moduleDto.getAclModuleList(), map);
        }
    }
    public List<AclModuleLevelDto> userAclTree(int userId){
        List<SysAcl> userAclList = sysCoreService.getAclListByUserId(userId);
        List<AclLevelDto> aclDtoList = Lists.newArrayList();
        for (SysAcl acl : userAclList) {
            AclLevelDto dto = AclLevelDto.adapt(acl);
            dto.setHasAcl(true);
            dto.setChecked(true);
            aclDtoList.add(dto);
        }
        return aclDtoListToTree(aclDtoList);
    }

}
