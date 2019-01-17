package com.jhc.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.jhc.dao.SysDeptMapper;
import com.jhc.dto.DeptLevelDto;
import com.jhc.model.SysDept;
import com.jhc.util.LevelUntil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author jhc on 2019/1/16
 */
@Service
public class SysTreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;

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
}
