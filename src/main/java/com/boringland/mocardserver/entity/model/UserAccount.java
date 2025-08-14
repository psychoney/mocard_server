package com.boringland.mocardserver.entity.model;


import lombok.Data;
import java.io.Serializable;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *
 * @author James
 * @date 2023-05-16
 */
@Data
@Entity
@Table(name = "user_account")
public class UserAccount implements Serializable {

	/**
	 * 用户id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 设备id
	 */
	@Column(unique=true)
	private String deviceId;

	/**
	 * 用户邮箱
	 */
	private String email;

	/**
	 * 会员标志
	 */
	private String memberType = "normal";

	/**
	 * 用户状态
	 */
	private String status = "active";

	/**
	 * 最后登录时间
	 */
	private LocalDateTime lastLogin;

	/**
	 * 会员购买时间
	 */
	private LocalDateTime purchaseTime;

	/**
	 * 会员过期时间
	 */
	private LocalDateTime expiryTime;

	private LocalDateTime createTime = LocalDateTime.now();

	private LocalDateTime updateTime = LocalDateTime.now();


}
