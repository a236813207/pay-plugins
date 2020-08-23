package com.ken.pay.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Ken
 * @date 2020/02/08
 */
@Data
@Accessors(chain = true)
public class BaseFillEntity {

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "modify_by", fill = FieldFill.INSERT_UPDATE)
    private String modifyBy;

    @TableField(value = "modify_Time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime modifyTime;


}
