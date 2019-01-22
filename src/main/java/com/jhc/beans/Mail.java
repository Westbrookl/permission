package com.jhc.beans;

import lombok.*;

import java.util.Set;

/**
 * @author jhc on 2019/1/22
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mail {
    private String subject;
    private String message;
    private Set<String> receivers;
}
