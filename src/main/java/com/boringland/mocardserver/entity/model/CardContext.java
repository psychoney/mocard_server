package com.boringland.mocardserver.entity.model;


import lombok.Data;
import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * @author James
 * @date 2023-05-10
 */
@Data
@Entity
@Table(name = "card_context")
public class CardContext implements Serializable {

	/**
	 * 自增主键
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 角色
	 */
	private String role;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 排序
	 */
	private Integer orderIndex;

	/**
	 * 关联card_context id
	 */
	private Integer pid;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime;


}
