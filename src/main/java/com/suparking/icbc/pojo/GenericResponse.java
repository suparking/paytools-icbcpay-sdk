package com.suparking.icbc.pojo;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Alsa
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse implements Serializable {
    private int status;
    private String message;
    private String cmd;
    private Object result;

    @Override
    public String toString(){
        if(Objects.isNull(this.result)){
            this.setResult(new Object());
        }
        if(this.cmd.isEmpty())
        {
            this.cmd = "";
        }
        return JSON.toJSONString(this);
    }
}
