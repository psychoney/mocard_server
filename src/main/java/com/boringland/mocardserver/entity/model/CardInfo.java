package com.boringland.mocardserver.entity.model;


import lombok.Data;
import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 卡片信息
 *
 * @author James
 * @date 2023-05-10
 */
@Data
@Entity
@Table(name = "card_info")
public class CardInfo implements Serializable {

	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 卡片名称
	 */
	private String title;

	/**
	 * 卡片描述
	 */
	private String description;

	/**
	 * 卡片图片
	 */
	private String imageUrl;

	/**
	 * 卡片类型
	 */
	private String type;

	/**
	 * 卡片颜色
	 */
	private String color;

	/**
	 * fewshot开关
	 */
	private Integer fewshot;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;


}
