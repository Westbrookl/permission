package com.jhc.beans;

import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author jhc on 2019/1/21
 */
@Getter
@Setter
@ToString
@Builder
public class PageResult<T> {
    private List<T> data = Lists.newArrayList();

    private int total;
}
