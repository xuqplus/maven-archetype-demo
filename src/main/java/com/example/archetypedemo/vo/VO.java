package com.example.archetypedemo.vo;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public abstract class VO implements Serializable {

    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
