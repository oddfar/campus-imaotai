package com.oddfar.campus.business.domain;

import com.oddfar.campus.business.entity.IUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserCoinInfo extends IUser {
    private Float xmyNum;
    private Integer energy;
}
