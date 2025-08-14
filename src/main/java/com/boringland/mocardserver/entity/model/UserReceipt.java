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
@Table(name = "user_receipt")
public class UserReceipt implements Serializable {

	/**
	 * receipt id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/**
	 * 关联user_order表id
	 */
	private Integer orderId;

	/**
	 * 用户设备id
	 */
	private String deviceId;

	/**
	 * 商品id
	 */
	private String productId;

	/**
	 * 购买状态
	 */
	private String purchaseStatus;

	/**
	 * 客户端发送的receipt
	 */
	private String receipt;

	/**
	 * 客户端发送的transaction_id
	 */
	private String transactionId;

	/**
	 * 苹果返回receiptInfo
	 */
	private String receiptInfo;

	/**
	 * 苹果返回latestReceiptInfo
	 */
	private String latestReceiptInfo;

	/**
	 * 苹果返回originalTransactionId
	 */
	private String originalTransactionId;

	/**
	 * 原始购买时间
	 */
	private LocalDateTime originalPurchaseDate;

	/**
	 * 购买时间
	 */
	private LocalDateTime purchaseDate;

	/**
	 * 过期时间
	 */
	private LocalDateTime expiresDate;

	/**
	 * 服务端校验标记
	 */
	private String checkStatus;

	/**
	 * 环境：沙箱还是正式
	 */
	private String environment;

	/**
	 * 服务端校验状态
	 */
	private String receiptStatus;

	/**
	 * 创建时间
	 */
	private LocalDateTime createTime = LocalDateTime.now();

	/**
	 * 更新时间
	 */
	private LocalDateTime updateTime = LocalDateTime.now();


}
