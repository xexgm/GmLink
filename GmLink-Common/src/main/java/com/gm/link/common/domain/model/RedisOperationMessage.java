package com.gm.link.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xexgm
 * description: 操作redis的kafka消息格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RedisOperationMessage {

    /**
     * 操作类型: SET、SETNX、DELETE 等等
     */
    String op;

    /**
     * 保存的 key
     */
    String key;

    /**
     * 保存的 value
     */
    String value;

    /**
     * 过期时间，单位秒
     */
    Integer expireSeconds;
}
