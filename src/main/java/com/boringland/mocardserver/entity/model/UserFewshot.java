package com.boringland.mocardserver.entity.model;


import lombok.Data;
import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * @author James
 * @date 2023-05-22
 */
@Data
@Entity
@Table(name = "user_fewshot")
public class UserFewshot implements Serializable {

	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 关联user_account deviceid
	 */
	private String deviceId;

	/**
	 * 关联card_info id
	 */
	private Integer cardId;

	/**
	 * 对话uid
	 */
	private String uid;

	/**
	 * 角色
	 */
	private String role;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 删除标记
	 */
	private Integer deleteFlag = 0;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime = LocalDateTime.now();


}
