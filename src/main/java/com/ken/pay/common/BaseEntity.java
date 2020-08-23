package com.ken.pay.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ken
 * @date 2020/02/08
 */
@Data
@Accessors(chain = true)
public class BaseEntity extends BaseFillEntity{

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

}
