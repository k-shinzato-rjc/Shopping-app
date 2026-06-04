package com.example.shopping.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.example.shopping.dto.CartDto;
import com.example.shopping.dto.MenuDto;
import com.example.shopping.form.CartForm;
import com.example.shopping.service.CartService;
import com.example.shopping.service.MenuService;
import com.example.shopping.service.SessionService;

/**
 * コントローラー
 * @author koki_shinzato
 */
@Controller
@SessionAttributes("orders")
public class ShoppingController {
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private SessionService sessionService;
	
	/**
	 * 全Menu情報をViewへ渡し、メニュー一覧画面へ遷移
	 * @param model
	 * @return
	 */
	@GetMapping("/menu/list")
	public String menuList(Model model) {
		
		List<MenuDto> dtoList = menuService.findAll();
		model.addAttribute("menus", menuService.convertFromDtoToForm(dtoList));
		
		return "menu";
	}
	
	/**
	 * メニュー画面からカート一覧画面へ遷移
	 * @param model
	 * @return カート画面
	 */
	@GetMapping("/cart/list")
	public String cartList(Model model) {
		
		// DBからカート情報を取得
		List<CartDto> dtoList = cartService.findAll();
		
		// Formに変換してViewへ渡す（自動でセッションに登録される）
		model.addAttribute("orders", cartService.convertFromDtoToForm(dtoList));
		
		return "cart";
	}
	
	/**
	 * メニュー画面からクリアボタンを押下
	 */
	@GetMapping("/cart/clear")
	public String cartClear() {
		cartService.deleteAll();
		
		return "redirect:/menu/list";
	}
	
	/**
	 * IDに該当する商品をカートへ1つ追加
	 * （カートテーブルからIDに該当した商品データを取り出し、個数を1加算して再登録する）
	 * @param id
	 */
	@PostMapping("/cart/add")
	public String cartAdd(@RequestParam(name="id") Integer id) {
		
		cartService.add(id);
		
		return "redirect:/menu/list";
	}
	
	/**
	 * カート一覧画面から削除ボタン押下 → カート一覧へリダイレクト
	 * @param commodityId
	 * @return
	 */
	@PostMapping("/cart/delete")
	public String cartDelete(@RequestParam(name="commodityId") Integer commodityId) {
		cartService.deleteById(commodityId);
		
		return "redirect:/cart/list";
	}
	
	@PostMapping("/cart/update")
	public String cartUpdate(@RequestParam("commodityId") Integer commodityId, @RequestParam("quantity") Integer quantity,
			ModelMap modelMap ,Model model, @SessionAttribute(name="orders", required=false) List<CartForm> sessionList) {
		
		List<CartDto> changeOrders = sessionService.sessionQuantities(cartService.convertFromFormToDto(sessionList), commodityId, quantity);
		
		model.addAttribute("orders", cartService.convertFromDtoToForm(changeOrders));
		
		return "cart";
	}
	
	/**
	 * カート内確定ボタンを押下 → カート内商品をDBに反映させる
	 * @author koki_shinzato
	 * 
	 * @param modelmap
	 * @return カート一覧画面
	 */
	@GetMapping("/cart/data-regist")
	public String updateCart(HttpSession httpSession) {
		
		cartService.deleteAll();
		cartService.updateCart(cartService.convertFromFormToDto((List<CartForm>) httpSession.getAttribute("orders")));
		
		return "redirect:/cart/list";
	}
	
	/**
	 * カートテーブル内の全データを削除
	 * @return カート一覧画面
	 */
	@GetMapping("/cart/purchase")
	public String cartPurChase() {
		
		cartService.deleteAll();
		
		return "redirect:/cart/list";
	}
}
