package com.oddfar.campus.business.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapPoint {

  /**
   * 纬度
   */
  private Double latitude;

  /**
   * 经度
   */
  private Double longitude;

}