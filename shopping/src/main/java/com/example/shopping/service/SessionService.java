package com.example.shopping.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.shopping.dto.CartDto;

/**
 * セッション処理用
 * @author koki_shinzato
 */
@Service
public class SessionService {
	
	// 指定されたセッションデータ（カート情報）の数量を変更
	public List<CartDto> sessionQuantities(List<CartDto> sessionOrders, Integer commodityId, Integer quantity) {
		
		List<CartDto> changeOrders = sessionOrders;
		
		changeOrders.stream().filter(order -> order.getCommodityId() == commodityId).forEach(order -> order.setQuantity(quantity));
		
		return changeOrders;
	}
}
