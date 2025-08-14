package com.boringland.mocardserver.entity.model;


import lombok.Data;
import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * @author James
 * @date 2023-06-14
 */
@Data
@Entity
@Table(name = "user_order")
public class UserOrder implements Serializable {

	/**
	 * order id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 设备id
	 */
	private String deviceId;

	/**
	 * 用户表id
	 */
	private Integer userId;

	/**
	 * 会员标志(week:周会员month:月会员year:年会员)
	 */
	private String orderType;

	/**
	 * 订单状态(pending:交易中error:交易错误canceled:取消purchased:交易成功restored:恢复)
	 */
	private String status;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime = LocalDateTime.now();

	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime = LocalDateTime.now();


}
